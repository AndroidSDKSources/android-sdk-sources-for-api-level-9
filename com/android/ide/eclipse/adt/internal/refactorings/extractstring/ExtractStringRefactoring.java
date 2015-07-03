/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.eclipse.adt.internal.refactorings.extractstring;

import com.android.ide.eclipse.adt.AndroidConstants;
import com.android.ide.eclipse.adt.internal.editors.AndroidXmlEditor;
import com.android.ide.eclipse.adt.internal.editors.descriptors.AttributeDescriptor;
import com.android.ide.eclipse.adt.internal.editors.descriptors.ReferenceAttributeDescriptor;
import com.android.ide.eclipse.adt.internal.editors.uimodel.UiAttributeNode;
import com.android.ide.eclipse.adt.internal.editors.uimodel.UiElementNode;
import com.android.ide.eclipse.adt.internal.project.AndroidManifestHelper;
import com.android.ide.eclipse.adt.internal.resources.ResourceType;
import com.android.ide.eclipse.adt.internal.resources.manager.ResourceFolderType;
import com.android.sdklib.SdkConstants;
import com.android.sdklib.xml.ManifestData;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextEditChangeGroup;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This refactoring extracts a string from a file and replaces it by an Android resource ID
 * such as R.string.foo.
 * <p/>
 * There are a number of scenarios, which are not all supported yet. The workflow works as
 * such:
 * <ul>
 * <li> User selects a string in a Java (TODO: or XML file) and invokes
 *      the {@link ExtractStringAction}.
 * <li> The action finds the {@link ICompilationUnit} being edited as well as the current
 *      {@link ITextSelection}. The action creates a new instance of this refactoring as
 *      well as an {@link ExtractStringWizard} and runs the operation.
 * <li> Step 1 of the refactoring is to check the preliminary conditions. Right now we check
 *      that the java source is not read-only and is in sync. We also try to find a string under
 *      the selection. If this fails, the refactoring is aborted.
 * <li> TODO: Find the string in an XML file based on selection.
 * <li> On success, the wizard is shown, which let the user input the new ID to use.
 * <li> The wizard sets the user input values into this refactoring instance, e.g. the new string
 *      ID, the XML file to update, etc. The wizard does use the utility method
 *      {@link XmlStringFileHelper#valueOfStringId(IProject, String, String)} to check whether
 *      the new ID is already defined in the target XML file.
 * <li> Once Preview or Finish is selected in the wizard, the
 *      {@link #checkFinalConditions(IProgressMonitor)} is called to double-check the user input
 *      and compute the actual changes.
 * <li> When all changes are computed, {@link #createChange(IProgressMonitor)} is invoked.
 * </ul>
 *
 * The list of changes are:
 * <ul>
 * <li> If the target XML does not exist, create it with the new string ID.
 * <li> If the target XML exists, find the <resources> node and add the new string ID right after.
 *      If the node is <resources/>, it needs to be opened.
 * <li> Create an AST rewriter to edit the source Java file and replace all occurences by the
 *      new computed R.string.foo. Also need to rewrite imports to import R as needed.
 *      If there's already a conflicting R included, we need to insert the FQCN instead.
 * <li> TODO: Have a pref in the wizard: [x] Change other XML Files
 * <li> TODO: Have a pref in the wizard: [x] Change other Java Files
 * </ul>
 */
public class ExtractStringRefactoring extends Refactoring {

    public enum Mode {
        /**
         * the Extract String refactoring is called on an <em>existing</em> source file.
         * Its purpose is then to get the selected string of the source and propose to
         * change it by an XML id. The XML id may be a new one or an existing one.
         */
        EDIT_SOURCE,
        /**
         * The Extract String refactoring is called without any source file.
         * Its purpose is then to create a new XML string ID or select/modify an existing one.
         */
        SELECT_ID,
        /**
         * The Extract String refactoring is called without any source file.
         * Its purpose is then to create a new XML string ID. The ID must not already exist.
         */
        SELECT_NEW_ID
    }

    /** The {@link Mode} of operation of the refactoring. */
    private final Mode mMode;
    /** Non-null when editing an Android Resource XML file: identifies the attribute name
     * of the value being edited. When null, the source is an Android Java file. */
    private String mXmlAttributeName;
    /** The file model being manipulated.
     * Value is null when not on {@link Mode#EDIT_SOURCE} mode. */
    private final IFile mFile;
    /** The editor. Non-null when invoked from {@link ExtractStringAction}. Null otherwise. */
    private final IEditorPart mEditor;
    /** The project that contains {@link #mFile} and that contains the target XML file to modify. */
    private final IProject mProject;
    /** The start of the selection in {@link #mFile}.
     * Value is -1 when not on {@link Mode#EDIT_SOURCE} mode. */
    private final int mSelectionStart;
    /** The end of the selection in {@link #mFile}.
     * Value is -1 when not on {@link Mode#EDIT_SOURCE} mode. */
    private final int mSelectionEnd;

    /** The compilation unit, only defined if {@link #mFile} points to a usable Java source file. */
    private ICompilationUnit mUnit;
    /** The actual string selected, after UTF characters have been escaped, good for display.
     * Value is null when not on {@link Mode#EDIT_SOURCE} mode. */
    private String mTokenString;

    /** The XML string ID selected by the user in the wizard. */
    private String mXmlStringId;
    /** The XML string value. Might be different than the initial selected string. */
    private String mXmlStringValue;
    /** The path of the XML file that will define {@link #mXmlStringId}, selected by the user
     *  in the wizard. */
    private String mTargetXmlFileWsPath;

    /** The list of changes computed by {@link #checkFinalConditions(IProgressMonitor)} and
     *  used by {@link #createChange(IProgressMonitor)}. */
    private ArrayList<Change> mChanges;

    private XmlStringFileHelper mXmlHelper = new XmlStringFileHelper();

    private static final String KEY_MODE = "mode";              //$NON-NLS-1$
    private static final String KEY_FILE = "file";              //$NON-NLS-1$
    private static final String KEY_PROJECT = "proj";           //$NON-NLS-1$
    private static final String KEY_SEL_START = "sel-start";    //$NON-NLS-1$
    private static final String KEY_SEL_END = "sel-end";        //$NON-NLS-1$
    private static final String KEY_TOK_ESC = "tok-esc";        //$NON-NLS-1$
    private static final String KEY_XML_ATTR_NAME = "xml-attr-name";      //$NON-NLS-1$

    public ExtractStringRefactoring(Map<String, String> arguments) throws NullPointerException {
        mMode = Mode.valueOf(arguments.get(KEY_MODE));

        IPath path = Path.fromPortableString(arguments.get(KEY_PROJECT));
        mProject = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(path);

        if (mMode == Mode.EDIT_SOURCE) {
            path = Path.fromPortableString(arguments.get(KEY_FILE));
            mFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);

            mSelectionStart = Integer.parseInt(arguments.get(KEY_SEL_START));
            mSelectionEnd   = Integer.parseInt(arguments.get(KEY_SEL_END));
            mTokenString    = arguments.get(KEY_TOK_ESC);
            mXmlAttributeName = arguments.get(KEY_XML_ATTR_NAME);
        } else {
            mFile = null;
            mSelectionStart = mSelectionEnd = -1;
            mTokenString = null;
            mXmlAttributeName = null;
        }

        mEditor = null;
    }

    private Map<String, String> createArgumentMap() {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put(KEY_MODE,      mMode.name());
        args.put(KEY_PROJECT,   mProject.getFullPath().toPortableString());
        if (mMode == Mode.EDIT_SOURCE) {
            args.put(KEY_FILE,      mFile.getFullPath().toPortableString());
            args.put(KEY_SEL_START, Integer.toString(mSelectionStart));
            args.put(KEY_SEL_END,   Integer.toString(mSelectionEnd));
            args.put(KEY_TOK_ESC,   mTokenString);
            args.put(KEY_XML_ATTR_NAME, mXmlAttributeName);
        }
        return args;
    }

    /**
     * Constructor to use when the Extract String refactoring is called on an
     * *existing* source file. Its purpose is then to get the selected string of
     * the source and propose to change it by an XML id. The XML id may be a new one
     * or an existing one.
     *
     * @param file The source file to process. Cannot be null. File must exist in workspace.
     * @param editor
     * @param selection The selection in the source file. Cannot be null or empty.
     */
    public ExtractStringRefactoring(IFile file, IEditorPart editor, ITextSelection selection) {
        mMode = Mode.EDIT_SOURCE;
        mFile = file;
        mEditor = editor;
        mProject = file.getProject();
        mSelectionStart = selection.getOffset();
        mSelectionEnd = mSelectionStart + Math.max(0, selection.getLength() - 1);
    }

    /**
     * Constructor to use when the Extract String refactoring is called without
     * any source file. Its purpose is then to create a new XML string ID.
     *
     * @param project The project where the target XML file to modify is located. Cannot be null.
     * @param enforceNew If true the XML ID must be a new one. If false, an existing ID can be
     *  used.
     */
    public ExtractStringRefactoring(IProject project, boolean enforceNew) {
        mMode = enforceNew ? Mode.SELECT_NEW_ID : Mode.SELECT_ID;
        mFile = null;
        mEditor = null;
        mProject = project;
        mSelectionStart = mSelectionEnd = -1;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.Refactoring#getName()
     */
    @Override
    public String getName() {
        if (mMode == Mode.SELECT_ID) {
            return "Create or USe Android String";
        } else if (mMode == Mode.SELECT_NEW_ID) {
            return "Create New Android String";
        }

        return "Extract Android String";
    }

    public Mode getMode() {
        return mMode;
    }

    /**
     * Gets the actual string selected, after UTF characters have been escaped,
     * good for display.
     */
    public String getTokenString() {
        return mTokenString;
    }

    public String getXmlStringId() {
        return mXmlStringId;
    }

    /**
     * Step 1 of 3 of the refactoring:
     * Checks that the current selection meets the initial condition before the ExtractString
     * wizard is shown. The check is supposed to be lightweight and quick. Note that at that
     * point the wizard has not been created yet.
     * <p/>
     * Here we scan the source buffer to find the token matching the selection.
     * The check is successful is a Java string literal is selected, the source is in sync
     * and is not read-only.
     * <p/>
     * This is also used to extract the string to be modified, so that we can display it in
     * the refactoring wizard.
     *
     * @see org.eclipse.ltk.core.refactoring.Refactoring#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
     *
     * @throws CoreException
     */
    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
            throws CoreException, OperationCanceledException {

        mUnit = null;
        mTokenString = null;

        RefactoringStatus status = new RefactoringStatus();

        try {
            monitor.beginTask("Checking preconditions...", 6);

            if (mMode != Mode.EDIT_SOURCE) {
                monitor.worked(6);
                return status;
            }

            if (!checkSourceFile(mFile, status, monitor)) {
                return status;
            }

            // Try to get a compilation unit from this file. If it fails, mUnit is null.
            try {
                mUnit = JavaCore.createCompilationUnitFrom(mFile);

                // Make sure the unit is not read-only, e.g. it's not a class file or inside a Jar
                if (mUnit.isReadOnly()) {
                    status.addFatalError("The file is read-only, please make it writeable first.");
                    return status;
                }

                // This is a Java file. Check if it contains the selection we want.
                if (!findSelectionInJavaUnit(mUnit, status, monitor)) {
                    return status;
                }

            } catch (Exception e) {
                // That was not a Java file. Ignore.
            }

            if (mUnit != null) {
                monitor.worked(1);
                return status;
            }

            // Check this a Layout XML file and get the selection and its context.
            if (mFile != null && AndroidConstants.EXT_XML.equals(mFile.getFileExtension())) {

                // Currently we only support Android resource XML files, so they must have a path
                // similar to
                //    project/res/<type>[-<configuration>]/*.xml
                // There is no support for sub folders, so the segment count must be 4.
                // We don't need to check the type folder name because a/ we only accept
                // an AndroidXmlEditor source and b/ aapt generates a compilation error for
                // unknown folders.
                IPath path = mFile.getFullPath();
                // check if we are inside the project/res/* folder.
                if (path.segmentCount() == 4) {
                    if (path.segment(1).equalsIgnoreCase(SdkConstants.FD_RESOURCES)) {
                        if (!findSelectionInXmlFile(mFile, status, monitor)) {
                            return status;
                        }
                    }
                }
            }

            if (!status.isOK()) {
                status.addFatalError(
                        "Selection must be inside a Java source or an Android Layout XML file.");
            }

        } finally {
            monitor.done();
        }

        return status;
    }

    /**
     * Try to find the selected Java element in the compilation unit.
     *
     * If selection matches a string literal, capture it, otherwise add a fatal error
     * to the status.
     *
     * On success, advance the monitor by 3.
     * Returns status.isOK().
     */
    private boolean findSelectionInJavaUnit(ICompilationUnit unit,
            RefactoringStatus status, IProgressMonitor monitor) {
        try {
            IBuffer buffer = unit.getBuffer();

            IScanner scanner = ToolFactory.createScanner(
                    false, //tokenizeComments
                    false, //tokenizeWhiteSpace
                    false, //assertMode
                    false  //recordLineSeparator
                    );
            scanner.setSource(buffer.getCharacters());
            monitor.worked(1);

            for(int token = scanner.getNextToken();
                    token != ITerminalSymbols.TokenNameEOF;
                    token = scanner.getNextToken()) {
                if (scanner.getCurrentTokenStartPosition() <= mSelectionStart &&
                        scanner.getCurrentTokenEndPosition() >= mSelectionEnd) {
                    // found the token, but only keep if the right type
                    if (token == ITerminalSymbols.TokenNameStringLiteral) {
                        mTokenString = new String(scanner.getCurrentTokenSource());
                    }
                    break;
                } else if (scanner.getCurrentTokenStartPosition() > mSelectionEnd) {
                    // scanner is past the selection, abort.
                    break;
                }
            }
        } catch (JavaModelException e1) {
            // Error in unit.getBuffer. Ignore.
        } catch (InvalidInputException e2) {
            // Error in scanner.getNextToken. Ignore.
        } finally {
            monitor.worked(1);
        }

        if (mTokenString != null) {
            // As a literal string, the token should have surrounding quotes. Remove them.
            int len = mTokenString.length();
            if (len > 0 &&
                    mTokenString.charAt(0) == '"' &&
                    mTokenString.charAt(len - 1) == '"') {
                mTokenString = mTokenString.substring(1, len - 1);
            }
            // We need a non-empty string literal
            if (mTokenString.length() == 0) {
                mTokenString = null;
            }
        }

        if (mTokenString == null) {
            status.addFatalError("Please select a Java string literal.");
        }

        monitor.worked(1);
        return status.isOK();
    }
    /**
     * Try to find the selected XML element. This implementation replies on the refactoring
     * originating from an Android Layout Editor. We rely on some internal properties of the
     * Structured XML editor to retrieve file content to avoid parsing it again. We also rely
     * on our specific Android XML model to get element & attribute descriptor properties.
     *
     * If selection matches a string literal, capture it, otherwise add a fatal error
     * to the status.
     *
     * On success, advance the monitor by 1.
     * Returns status.isOK().
     */
    private boolean findSelectionInXmlFile(IFile file,
            RefactoringStatus status,
            IProgressMonitor monitor) {

        try {
            if (!(mEditor instanceof AndroidXmlEditor)) {
                status.addFatalError("Only the Android XML Editor is currently supported.");
                return status.isOK();
            }

            AndroidXmlEditor editor = (AndroidXmlEditor) mEditor;
            IStructuredModel smodel = null;
            Node node = null;
            String currAttrName = null;

            try {
                // See the portability note in AndroidXmlEditor#getModelForRead() javadoc.
                smodel = editor.getModelForRead();
                if (smodel != null) {
                    // The structured model gives the us the actual XML Node element where the
                    // offset is. By using this Node, we can find the exact UiElementNode of our
                    // model and thus we'll be able to get the properties of the attribute -- to
                    // check if it accepts a string reference. This does not however tell us if
                    // the selection is actually in an attribute value, nor which attribute is
                    // being edited.
                    for(int offset = mSelectionStart; offset >= 0 && node == null; --offset) {
                        node = (Node) smodel.getIndexedRegion(offset);
                    }

                    if (node == null) {
                        status.addFatalError(
                                "The selection does not match any element in the XML document.");
                        return status.isOK();
                    }

                    if (node.getNodeType() != Node.ELEMENT_NODE) {
                        status.addFatalError("The selection is not inside an actual XML element.");
                        return status.isOK();
                    }

                    IStructuredDocument sdoc = smodel.getStructuredDocument();
                    if (sdoc != null) {
                        // Portability note: all the structured document implementation is
                        // under wst.sse.core.internal.provisional so we can expect it to change in
                        // a distant future if they start cleaning their codebase, however unlikely
                        // that is.

                        int selStart = mSelectionStart;
                        IStructuredDocumentRegion region =
                            sdoc.getRegionAtCharacterOffset(selStart);
                        if (region != null &&
                                DOMRegionContext.XML_TAG_NAME.equals(region.getType())) {
                            // Find if any sub-region representing an attribute contains the
                            // selection. If it does, returns the name of the attribute in
                            // currAttrName and returns the value in the field mTokenString.
                            currAttrName = findSelectionInRegion(region, selStart);

                            if (mTokenString == null) {
                                status.addFatalError(
                                    "The selection is not inside an actual XML attribute value.");
                            }
                        }
                    }

                    if (mTokenString != null && node != null && currAttrName != null) {

                        // Validate that the attribute accepts a string reference.
                        // This sets mTokenString to null by side-effect when it fails and
                        // adds a fatal error to the status as needed.
                        validateSelectedAttribute(editor, node, currAttrName, status);

                    } else {
                        // We shouldn't get here: we're missing one of the token string, the node
                        // or the attribute name. All of them have been checked earlier so don't
                        // set any specific error.
                        mTokenString = null;
                    }
                }
            } finally {
                if (smodel != null) {
                    smodel.releaseFromRead();
                }
            }

        } finally {
            monitor.worked(1);
        }

        return status.isOK();
    }

    /**
     * The region gives us the textual representation of the XML element
     * where the selection starts, split using sub-regions. We now just
     * need to iterate through the sub-regions to find which one
     * contains the actual selection. We're interested in an attribute
     * value however when we find one we want to memorize the attribute
     * name that was defined just before.
     *
     * @return When the cursor is on a valid attribute name or value, returns the string of
     * attribute name. As a side-effect, returns the value of the attribute in {@link #mTokenString}
     */
    private String findSelectionInRegion(IStructuredDocumentRegion region, int selStart) {

        String currAttrName = null;

        int startInRegion = selStart - region.getStartOffset();

        int nb = region.getNumberOfRegions();
        ITextRegionList list = region.getRegions();
        String currAttrValue = null;

        for (int i = 0; i < nb; i++) {
            ITextRegion subRegion = list.get(i);
            String type = subRegion.getType();

            if (DOMRegionContext.XML_TAG_ATTRIBUTE_NAME.equals(type)) {
                currAttrName = region.getText(subRegion);

                // I like to select the attribute definition and invoke
                // the extract string wizard. So if the selection is on
                // the attribute name part, find the value that is just
                // after and use it as if it were the selection.

                if (subRegion.getStart() <= startInRegion &&
                        startInRegion < subRegion.getTextEnd()) {
                    // A well-formed attribute is composed of a name,
                    // an equal sign and the value. There can't be any space
                    // in between, which makes the parsing a lot easier.
                    if (i <= nb - 3 &&
                            DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS.equals(
                                                   list.get(i + 1).getType())) {
                        subRegion = list.get(i + 2);
                        type = subRegion.getType();
                        if (DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE.equals(
                                type)) {
                            currAttrValue = region.getText(subRegion);
                        }
                    }
                }

            } else if (subRegion.getStart() <= startInRegion &&
                    startInRegion < subRegion.getTextEnd() &&
                    DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE.equals(type)) {
                currAttrValue = region.getText(subRegion);
            }

            if (currAttrValue != null) {
                // We found the value. Only accept it if not empty
                // and if we found an attribute name before.
                String text = currAttrValue;

                // The attribute value will contain the XML quotes. Remove them.
                int len = text.length();
                if (len >= 2 &&
                        text.charAt(0) == '"' &&
                        text.charAt(len - 1) == '"') {
                    text = text.substring(1, len - 1);
                } else if (len >= 2 &&
                        text.charAt(0) == '\'' &&
                        text.charAt(len - 1) == '\'') {
                    text = text.substring(1, len - 1);
                }
                if (text.length() > 0 && currAttrName != null) {
                    // Setting mTokenString to non-null marks the fact we
                    // accept this attribute.
                    mTokenString = text;
                }

                break;
            }
        }

        return currAttrName;
    }

    /**
     * Validates that the attribute accepts a string reference.
     * This sets mTokenString to null by side-effect when it fails and
     * adds a fatal error to the status as needed.
     */
    private void validateSelectedAttribute(AndroidXmlEditor editor, Node node,
            String attrName, RefactoringStatus status) {
        UiElementNode rootUiNode = editor.getUiRootNode();
        UiElementNode currentUiNode =
            rootUiNode == null ? null : rootUiNode.findXmlNode(node);
        ReferenceAttributeDescriptor attrDesc = null;

        if (currentUiNode != null) {
            // remove any namespace prefix from the attribute name
            String name = attrName;
            int pos = name.indexOf(':');
            if (pos > 0 && pos < name.length() - 1) {
                name = name.substring(pos + 1);
            }

            for (UiAttributeNode attrNode : currentUiNode.getUiAttributes()) {
                if (attrNode.getDescriptor().getXmlLocalName().equals(name)) {
                    AttributeDescriptor desc = attrNode.getDescriptor();
                    if (desc instanceof ReferenceAttributeDescriptor) {
                        attrDesc = (ReferenceAttributeDescriptor) desc;
                    }
                    break;
                }
            }
        }

        // The attribute descriptor is a resource reference. It must either accept
        // of any resource type or specifically accept string types.
        if (attrDesc != null &&
                (attrDesc.getResourceType() == null ||
                 attrDesc.getResourceType() == ResourceType.STRING)) {
            // We have one more check to do: is the current string value already
            // an Android XML string reference? If so, we can't edit it.
            if (mTokenString.startsWith("@")) {                             //$NON-NLS-1$
                int pos1 = 0;
                if (mTokenString.length() > 1 && mTokenString.charAt(1) == '+') {
                    pos1++;
                }
                int pos2 = mTokenString.indexOf('/');
                if (pos2 > pos1) {
                    String kind = mTokenString.substring(pos1 + 1, pos2);
                    if (ResourceType.STRING.getName().equals(kind)) {                            //$NON-NLS-1$
                        mTokenString = null;
                        status.addFatalError(String.format(
                                "The attribute %1$s already contains a %2$s reference.",
                                attrName,
                                kind));
                    }
                }
            }

            if (mTokenString != null) {
                // We're done with all our checks. mTokenString contains the
                // current attribute value. We don't memorize the region nor the
                // attribute, however we memorize the textual attribute name so
                // that we can offer replacement for all its occurrences.
                mXmlAttributeName = attrName;
            }

        } else {
            mTokenString = null;
            status.addFatalError(String.format(
                    "The attribute %1$s does not accept a string reference.",
                    attrName));
        }
    }

    /**
     * Tests from org.eclipse.jdt.internal.corext.refactoringChecks#validateEdit()
     * Might not be useful.
     *
     * On success, advance the monitor by 2.
     *
     * @return False if caller should abort, true if caller should continue.
     */
    private boolean checkSourceFile(IFile file,
            RefactoringStatus status,
            IProgressMonitor monitor) {
        // check whether the source file is in sync
        if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
            status.addFatalError("The file is not synchronized. Please save it first.");
            return false;
        }
        monitor.worked(1);

        // make sure we can write to it.
        ResourceAttributes resAttr = file.getResourceAttributes();
        if (resAttr == null || resAttr.isReadOnly()) {
            status.addFatalError("The file is read-only, please make it writeable first.");
            return false;
        }
        monitor.worked(1);

        return true;
    }

    /**
     * Step 2 of 3 of the refactoring:
     * Check the conditions once the user filled values in the refactoring wizard,
     * then prepare the changes to be applied.
     * <p/>
     * In this case, most of the sanity checks are done by the wizard so essentially this
     * should only be called if the wizard positively validated the user input.
     *
     * Here we do check that the target resource XML file either does not exists or
     * is not read-only.
     *
     * @see org.eclipse.ltk.core.refactoring.Refactoring#checkFinalConditions(IProgressMonitor)
     *
     * @throws CoreException
     */
    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
            throws CoreException, OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            monitor.beginTask("Checking post-conditions...", 3);

            if (mXmlStringId == null || mXmlStringId.length() <= 0) {
                // this is not supposed to happen
                status.addFatalError("Missing replacement string ID");
            } else if (mTargetXmlFileWsPath == null || mTargetXmlFileWsPath.length() <= 0) {
                // this is not supposed to happen
                status.addFatalError("Missing target xml file path");
            }
            monitor.worked(1);

            // Either that resource must not exist or it must be a writeable file.
            IResource targetXml = getTargetXmlResource(mTargetXmlFileWsPath);
            if (targetXml != null) {
                if (targetXml.getType() != IResource.FILE) {
                    status.addFatalError(
                            String.format("XML file '%1$s' is not a file.", mTargetXmlFileWsPath));
                } else {
                    ResourceAttributes attr = targetXml.getResourceAttributes();
                    if (attr != null && attr.isReadOnly()) {
                        status.addFatalError(
                                String.format("XML file '%1$s' is read-only.",
                                        mTargetXmlFileWsPath));
                    }
                }
            }
            monitor.worked(1);

            if (status.hasError()) {
                return status;
            }

            mChanges = new ArrayList<Change>();


            // Prepare the change for the XML file.

            if (mXmlHelper.valueOfStringId(mProject, mTargetXmlFileWsPath, mXmlStringId) == null) {
                // We actually change it only if the ID doesn't exist yet
                Change change = createXmlChange((IFile) targetXml, mXmlStringId, mXmlStringValue,
                        status, SubMonitor.convert(monitor, 1));
                if (change != null) {
                    mChanges.add(change);
                }
            }

            if (status.hasError()) {
                return status;
            }

            if (mMode == Mode.EDIT_SOURCE) {
                List<Change> changes = null;
                if (mXmlAttributeName != null) {
                    // Prepare the change to the Android resource XML file
                    changes = computeXmlSourceChanges(mFile,
                            mXmlStringId, mTokenString, mXmlAttributeName,
                            status, monitor);

                } else {
                    // Prepare the change to the Java compilation unit
                    changes = computeJavaChanges(mUnit, mXmlStringId, mTokenString,
                            status, SubMonitor.convert(monitor, 1));
                }
                if (changes != null) {
                    mChanges.addAll(changes);
                }
            }

            monitor.worked(1);
        } finally {
            monitor.done();
        }

        return status;
    }

    /**
     * Internal helper that actually prepares the {@link Change} that adds the given
     * ID to the given XML File.
     * <p/>
     * This does not actually modify the file.
     *
     * @param targetXml The file resource to modify.
     * @param xmlStringId The new ID to insert.
     * @param tokenString The old string, which will be the value in the XML string.
     * @return A new {@link TextEdit} that describes how to change the file.
     */
    private Change createXmlChange(IFile targetXml,
            String xmlStringId,
            String tokenString,
            RefactoringStatus status,
            SubMonitor subMonitor) {

        TextFileChange xmlChange = new TextFileChange(getName(), targetXml);
        xmlChange.setTextType("xml");   //$NON-NLS-1$

        TextEdit edit = null;
        TextEditGroup editGroup = null;

        if (!targetXml.exists()) {
            // The XML file does not exist. Simply create it.
            StringBuilder content = new StringBuilder();
            content.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"); //$NON-NLS-1$
            content.append("<resources>\n");                                //$NON-NLS-1$
            content.append("    <string name=\"").                          //$NON-NLS-1$
                        append(xmlStringId).
                        append("\">").                                      //$NON-NLS-1$
                        append(tokenString).
                        append("</string>\n");                              //$NON-NLS-1$
            content.append("</resources>\n");                                //$NON-NLS-1$

            edit = new InsertEdit(0, content.toString());
            editGroup = new TextEditGroup("Create <string> in new XML file", edit);
        } else {
            // The file exist. Attempt to parse it as a valid XML document.
            try {
                int[] indices = new int[2];

                // TODO case where we replace the value of an existing XML String ID

                if (findXmlOpeningTagPos(targetXml.getContents(), "resources", indices)) {  //$NON-NLS-1$
                    // Indices[1] indicates whether we found > or />. It can only be 1 or 2.
                    // Indices[0] is the position of the first character of either > or />.
                    //
                    // Note: we don't even try to adapt our formatting to the existing structure (we
                    // could by capturing whatever whitespace is after the closing bracket and
                    // applying it here before our tag, unless we were dealing with an empty
                    // resource tag.)

                    int offset = indices[0];
                    int len = indices[1];
                    StringBuilder content = new StringBuilder();
                    content.append(">\n");                                      //$NON-NLS-1$
                    content.append("    <string name=\"").                      //$NON-NLS-1$
                                append(xmlStringId).
                                append("\">").                                  //$NON-NLS-1$
                                append(tokenString).
                                append("</string>");                            //$NON-NLS-1$
                    if (len == 2) {
                        content.append("\n</resources>");                       //$NON-NLS-1$
                    }

                    edit = new ReplaceEdit(offset, len, content.toString());
                    editGroup = new TextEditGroup("Insert <string> in XML file", edit);
                }
            } catch (CoreException e) {
                // Failed to read file. Ignore. Will return null below.
            }
        }

        if (edit == null) {
            status.addFatalError(String.format("Failed to modify file %1$s",
                    mTargetXmlFileWsPath));
            return null;
        }

        xmlChange.setEdit(edit);
        // The TextEditChangeGroup let the user toggle this change on and off later.
        xmlChange.addTextEditChangeGroup(new TextEditChangeGroup(xmlChange, editGroup));

        subMonitor.worked(1);
        return xmlChange;
    }

    /**
     * Parse an XML input stream, looking for an opening tag.
     * <p/>
     * If found, returns the character offest in the buffer of the closing bracket of that
     * tag, e.g. the position of > in "<resources>". The first character is at offset 0.
     * <p/>
     * The implementation here relies on a simple character-based parser. No DOM nor SAX
     * parsing is used, due to the simplified nature of the task: we just want the first
     * opening tag, which in our case should be the document root. We deal however with
     * with the tag being commented out, so comments are skipped. We assume the XML doc
     * is sane, e.g. we don't expect the tag to appear in the middle of a string. But
     * again since in fact we want the root element, that's unlikely to happen.
     * <p/>
     * We need to deal with the case where the element is written as <resources/>, in
     * which case the caller will want to replace /> by ">...</...>". To do that we return
     * two values: the first offset of the closing tag (e.g. / or >) and the length, which
     * can only be 1 or 2. If it's 2, the caller has to deal with /> instead of just >.
     *
     * @param contents An existing buffer to parse.
     * @param tag The tag to look for.
     * @param indices The return values: [0] is the offset of the closing bracket and [1] is
     *          the length which can be only 1 for > and 2 for />
     * @return True if we found the tag, in which case <code>indices</code> can be used.
     */
    private boolean findXmlOpeningTagPos(InputStream contents, String tag, int[] indices) {

        BufferedReader br = new BufferedReader(new InputStreamReader(contents));
        StringBuilder sb = new StringBuilder(); // scratch area

        tag = "<" + tag;
        int tagLen = tag.length();
        int maxLen = tagLen < 3 ? 3 : tagLen;

        try {
            int offset = 0;
            int i = 0;
            char searching = '<'; // we want opening tags
            boolean capture = false;
            boolean inComment = false;
            boolean inTag = false;
            while ((i = br.read()) != -1) {
                char c = (char) i;
                if (c == searching) {
                    capture = true;
                }
                if (capture) {
                    sb.append(c);
                    int len = sb.length();
                    if (inComment && c == '>') {
                        // is the comment being closed?
                        if (len >= 3 && sb.substring(len-3).equals("-->")) {    //$NON-NLS-1$
                            // yes, comment is closing, stop capturing
                            capture = false;
                            inComment = false;
                            sb.setLength(0);
                        }
                    } else if (inTag && c == '>') {
                        // we're capturing in our tag, waiting for the closing >, we just got it
                        // so we're totally done here. Simply detect whether it's /> or >.
                        indices[0] = offset;
                        indices[1] = 1;
                        if (sb.charAt(len - 2) == '/') {
                            indices[0]--;
                            indices[1]++;
                        }
                        return true;

                    } else if (!inComment && !inTag) {
                        // not a comment and not our tag yet, so we're capturing because a
                        // tag is being opened but we don't know which one yet.

                        // look for either the opening or a comment or
                        // the opening of our tag.
                        if (len == 3 && sb.equals("<--")) {                     //$NON-NLS-1$
                            inComment = true;
                        } else if (len == tagLen && sb.toString().equals(tag)) {
                            inTag = true;
                        }

                        // if we're not interested in this tag yet, deal with when to stop
                        // capturing: the opening tag ends with either any kind of whitespace
                        // or with a > or maybe there's a PI that starts with <?
                        if (!inComment && !inTag) {
                            if (c == '>' || c == '?' || c == ' ' || c == '\n' || c == '\r') {
                                // stop capturing
                                capture = false;
                                sb.setLength(0);
                            }
                        }
                    }

                    if (capture && len > maxLen) {
                        // in any case we don't need to capture more than the size of our tag
                        // or the comment opening tag
                        sb.deleteCharAt(0);
                    }
                }
                offset++;
            }
        } catch (IOException e) {
            // Ignore.
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // oh come on...
            }
        }

        return false;
    }


    /**
     * Computes the changes to be made to the source Android XML file(s) and
     * returns a list of {@link Change}.
     */
    private List<Change> computeXmlSourceChanges(IFile sourceFile,
            String xmlStringId,
            String tokenString,
            String xmlAttrName,
            RefactoringStatus status,
            IProgressMonitor monitor) {

        if (!sourceFile.exists()) {
            status.addFatalError(String.format("XML file '%1$s' does not exist.",
                    sourceFile.getFullPath().toOSString()));
            return null;
        }

        // In the initial condition check we validated that this file is part of
        // an Android resource folder, with a folder path that looks like
        //   /project/res/<type>-<configuration>/<filename.xml>
        // Here we are going to offer XML source change for the same filename accross all
        // configurations of the same res type. E.g. if we're processing a res/layout/main.xml
        // file then we want to offer changes for res/layout-fr/main.xml. We compute such a
        // list here.
        HashSet<IFile> files = new HashSet<IFile>();
        files.add(sourceFile);

        if (AndroidConstants.EXT_XML.equals(sourceFile.getFileExtension())) {
            IPath path = sourceFile.getFullPath();
            if (path.segmentCount() == 4 && path.segment(1).equals(SdkConstants.FD_RESOURCES)) {
                IProject project = sourceFile.getProject();
                String filename = path.segment(3);
                String initialTypeName = path.segment(2);
                ResourceFolderType type = ResourceFolderType.getFolderType(initialTypeName);

                IContainer res = sourceFile.getParent().getParent();
                if (type != null && res != null && res.getType() == IResource.FOLDER) {
                    try {
                        for (IResource r : res.members()) {
                            if (r != null && r.getType() == IResource.FOLDER) {
                                String name = r.getName();
                                // Skip the initial folder name, it's already in the list.
                                if (!name.equals(initialTypeName)) {
                                    // Only accept the same folder type (e.g. layout-*)
                                    ResourceFolderType t =
                                        ResourceFolderType.getFolderType(name);
                                    if (type.equals(t)) {
                                        // recompute the path
                                        IPath p = res.getProjectRelativePath().append(name).
                                                                               append(filename);
                                        IResource f = project.findMember(p);
                                        if (f != null && f instanceof IFile) {
                                            files.add((IFile) f);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (CoreException e) {
                        // Ignore.
                    }
                }
            }
        }

        SubMonitor subMonitor = SubMonitor.convert(monitor, Math.min(1, files.size()));

        ArrayList<Change> changes = new ArrayList<Change>();

        try {
            // Portability note: getModelManager is part of wst.sse.core however the
            // interface returned is part of wst.sse.core.internal.provisional so we can
            // expect it to change in a distant future if they start cleaning their codebase,
            // however unlikely that is.
            IModelManager modelManager = StructuredModelManager.getModelManager();

            for (IFile file : files) {

                IStructuredDocument sdoc = modelManager.createStructuredDocumentFor(file);

                if (sdoc == null) {
                    status.addFatalError("XML structured document not found");     //$NON-NLS-1$
                    return null;
                }

                TextFileChange xmlChange = new TextFileChange(getName(), file);
                xmlChange.setTextType("xml");   //$NON-NLS-1$

                MultiTextEdit multiEdit = new MultiTextEdit();
                ArrayList<TextEditGroup> editGroups = new ArrayList<TextEditGroup>();

                String quotedReplacement = quotedAttrValue("@string/" + xmlStringId);

                // Prepare the change set
                try {
                    for (IStructuredDocumentRegion region : sdoc.getStructuredDocumentRegions()) {
                        // Only look at XML "top regions"
                        if (!DOMRegionContext.XML_TAG_NAME.equals(region.getType())) {
                            continue;
                        }

                        int nb = region.getNumberOfRegions();
                        ITextRegionList list = region.getRegions();
                        String lastAttrName = null;

                        for (int i = 0; i < nb; i++) {
                            ITextRegion subRegion = list.get(i);
                            String type = subRegion.getType();

                            if (DOMRegionContext.XML_TAG_ATTRIBUTE_NAME.equals(type)) {
                                // Memorize the last attribute name seen
                                lastAttrName = region.getText(subRegion);

                            } else if (DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE.equals(type)) {
                                // Check this is the attribute and the original string
                                String text = region.getText(subRegion);

                                int len = text.length();
                                if (len >= 2 &&
                                        text.charAt(0) == '"' &&
                                        text.charAt(len - 1) == '"') {
                                    text = text.substring(1, len - 1);
                                } else if (len >= 2 &&
                                        text.charAt(0) == '\'' &&
                                        text.charAt(len - 1) == '\'') {
                                    text = text.substring(1, len - 1);
                                }

                                if (xmlAttrName.equals(lastAttrName) && tokenString.equals(text)) {

                                    // Found an occurrence. Create a change for it.
                                    TextEdit edit = new ReplaceEdit(
                                            region.getStartOffset() + subRegion.getStart(),
                                            subRegion.getTextLength(),
                                            quotedReplacement);
                                    TextEditGroup editGroup = new TextEditGroup(
                                            "Replace attribute string by ID",
                                            edit);

                                    multiEdit.addChild(edit);
                                    editGroups.add(editGroup);
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    // Since we use some internal APIs, use a broad catch-all to report any
                    // unexpected issue rather than crash the whole refactoring.
                    status.addFatalError(
                            String.format("XML refactoring error: %1$s", t.getMessage()));
                } finally {
                    if (multiEdit.hasChildren()) {
                        xmlChange.setEdit(multiEdit);
                        for (TextEditGroup group : editGroups) {
                            xmlChange.addTextEditChangeGroup(
                                    new TextEditChangeGroup(xmlChange, group));
                        }
                        changes.add(xmlChange);
                    }
                    subMonitor.worked(1);
                }
            } // for files

        } catch (IOException e) {
            status.addFatalError(String.format("XML model IO error: %1$s.", e.getMessage()));
        } catch (CoreException e) {
            status.addFatalError(String.format("XML model core error: %1$s.", e.getMessage()));
        } finally {
            if (changes.size() > 0) {
                return changes;
            }
        }

        return null;
    }

    /**
     * Returns a quoted attribute value suitable to be placed after an attributeName=
     * statement in an XML stream.
     *
     * According to http://www.w3.org/TR/2008/REC-xml-20081126/#NT-AttValue
     * the attribute value can be either quoted using ' or " and the corresponding
     * entities &apos; or &quot; must be used inside.
     */
    private String quotedAttrValue(String attrValue) {
        if (attrValue.indexOf('"') == -1) {
            // no double-quotes inside, use double-quotes around.
            return '"' + attrValue + '"';
        }
        if (attrValue.indexOf('\'') == -1) {
            // no single-quotes inside, use single-quotes around.
            return '\'' + attrValue + '\'';
        }
        // If we get here, there's a mix. Opt for double-quote around and replace
        // inner double-quotes.
        attrValue = attrValue.replace("\"", "&quot;");  //$NON-NLS-1$ //$NON-NLS-2$
        return '"' + attrValue + '"';
    }

    /**
     * Computes the changes to be made to Java file(s) and returns a list of {@link Change}.
     */
    private List<Change> computeJavaChanges(ICompilationUnit unit,
            String xmlStringId,
            String tokenString,
            RefactoringStatus status,
            SubMonitor subMonitor) {

        // Get the Android package name from the Android Manifest. We need it to create
        // the FQCN of the R class.
        String packageName = null;
        String error = null;
        IResource manifestFile = mProject.findMember(SdkConstants.FN_ANDROID_MANIFEST_XML);
        if (manifestFile == null || manifestFile.getType() != IResource.FILE) {
            error = "File not found";
        } else {
            ManifestData manifestData = AndroidManifestHelper.parseForData((IFile) manifestFile);
            if (manifestData == null) {
                error = "Invalid content";
            } else {
                packageName = manifestData.getPackage();
                if (packageName == null) {
                    error = "Missing package definition";
                }
            }
        }

        if (error != null) {
            status.addFatalError(
                    String.format("Failed to parse file %1$s: %2$s.",
                            manifestFile == null ? "" : manifestFile.getFullPath(),  //$NON-NLS-1$
                            error));
            return null;
        }

        // TODO in a future version we might want to collect various Java files that
        // need to be updated in the same project and process them all together.
        // To do that we need to use an ASTRequestor and parser.createASTs, kind of
        // like this:
        //
        // ASTRequestor requestor = new ASTRequestor() {
        //    @Override
        //    public void acceptAST(ICompilationUnit sourceUnit, CompilationUnit astNode) {
        //        super.acceptAST(sourceUnit, astNode);
        //        // TODO process astNode
        //    }
        // };
        // ...
        // parser.createASTs(compilationUnits, bindingKeys, requestor, monitor)
        //
        // and then add multiple TextFileChange to the changes arraylist.

        // Right now the changes array will contain one TextFileChange at most.
        ArrayList<Change> changes = new ArrayList<Change>();

        // This is the unit that will be modified.
        TextFileChange change = new TextFileChange(getName(), (IFile) unit.getResource());
        change.setTextType("java"); //$NON-NLS-1$

        // Create an AST for this compilation unit
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setProject(unit.getJavaProject());
        parser.setSource(unit);
        parser.setResolveBindings(true);
        ASTNode node = parser.createAST(subMonitor.newChild(1));

        // The ASTNode must be a CompilationUnit, by design
        if (!(node instanceof CompilationUnit)) {
            status.addFatalError(String.format("Internal error: ASTNode class %s",  //$NON-NLS-1$
                    node.getClass()));
            return null;
        }

        // ImportRewrite will allow us to add the new type to the imports and will resolve
        // what the Java source must reference, e.g. the FQCN or just the simple name.
        ImportRewrite importRewrite = ImportRewrite.create((CompilationUnit) node, true);
        String Rqualifier = packageName + ".R"; //$NON-NLS-1$
        Rqualifier = importRewrite.addImport(Rqualifier);

        // Rewrite the AST itself via an ASTVisitor
        AST ast = node.getAST();
        ASTRewrite astRewrite = ASTRewrite.create(ast);
        ArrayList<TextEditGroup> astEditGroups = new ArrayList<TextEditGroup>();
        ReplaceStringsVisitor visitor = new ReplaceStringsVisitor(
                ast, astRewrite, astEditGroups,
                tokenString, Rqualifier, xmlStringId);
        node.accept(visitor);

        // Finally prepare the change set
        try {
            MultiTextEdit edit = new MultiTextEdit();

            // Create the edit to change the imports, only if anything changed
            TextEdit subEdit = importRewrite.rewriteImports(subMonitor.newChild(1));
            if (subEdit.hasChildren()) {
                edit.addChild(subEdit);
            }

            // Create the edit to change the Java source, only if anything changed
            subEdit = astRewrite.rewriteAST();
            if (subEdit.hasChildren()) {
                edit.addChild(subEdit);
            }

            // Only create a change set if any edit was collected
            if (edit.hasChildren()) {
                change.setEdit(edit);

                // Create TextEditChangeGroups which let the user turn changes on or off
                // individually. This must be done after the change.setEdit() call above.
                for (TextEditGroup editGroup : astEditGroups) {
                    change.addTextEditChangeGroup(new TextEditChangeGroup(change, editGroup));
                }

                changes.add(change);
            }

            // TODO to modify another Java source, loop back to the creation of the
            // TextFileChange and accumulate in changes. Right now only one source is
            // modified.

            subMonitor.worked(1);

            if (changes.size() > 0) {
                return changes;
            }

        } catch (CoreException e) {
            // ImportRewrite.rewriteImports failed.
            status.addFatalError(e.getMessage());
        }
        return null;
    }

    /**
     * Step 3 of 3 of the refactoring: returns the {@link Change} that will be able to do the
     * work and creates a descriptor that can be used to replay that refactoring later.
     *
     * @see org.eclipse.ltk.core.refactoring.Refactoring#createChange(org.eclipse.core.runtime.IProgressMonitor)
     *
     * @throws CoreException
     */
    @Override
    public Change createChange(IProgressMonitor monitor)
            throws CoreException, OperationCanceledException {

        try {
            monitor.beginTask("Applying changes...", 1);

            CompositeChange change = new CompositeChange(
                    getName(),
                    mChanges.toArray(new Change[mChanges.size()])) {
                @Override
                public ChangeDescriptor getDescriptor() {

                    String comment = String.format(
                            "Extracts string '%1$s' into R.string.%2$s",
                            mTokenString,
                            mXmlStringId);

                    ExtractStringDescriptor desc = new ExtractStringDescriptor(
                            mProject.getName(), //project
                            comment, //description
                            comment, //comment
                            createArgumentMap());

                    return new RefactoringChangeDescriptor(desc);
                }
            };

            monitor.worked(1);

            return change;

        } finally {
            monitor.done();
        }

    }

    /**
     * Given a file project path, returns its resource in the same project than the
     * compilation unit. The resource may not exist.
     */
    private IResource getTargetXmlResource(String xmlFileWsPath) {
        IResource resource = mProject.getFile(xmlFileWsPath);
        return resource;
    }

    /**
     * Sets the replacement string ID. Used by the wizard to set the user input.
     */
    public void setNewStringId(String newStringId) {
        mXmlStringId = newStringId;
    }

    /**
     * Sets the replacement string ID. Used by the wizard to set the user input.
     */
    public void setNewStringValue(String newStringValue) {
        mXmlStringValue = newStringValue;
    }

    /**
     * Sets the target file. This is a project path, e.g. "/res/values/strings.xml".
     * Used by the wizard to set the user input.
     */
    public void setTargetFile(String targetXmlFileWsPath) {
        mTargetXmlFileWsPath = targetXmlFileWsPath;
    }

}
