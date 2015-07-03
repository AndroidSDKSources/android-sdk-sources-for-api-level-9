/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.sdkmanager;

import com.android.sdklib.ISdkLog;
import com.android.sdklib.SdkManager;
import com.android.sdklib.repository.SdkRepository;

import java.util.Arrays;


/**
 * Specific command-line flags for the {@link SdkManager}.
 */
class SdkCommandLine extends CommandLineProcessor {

    /*
     * Steps needed to add a new action:
     * - Each action is defined as a "verb object" followed by parameters.
     * - Either reuse a VERB_ constant or define a new one.
     * - Either reuse an OBJECT_ constant or define a new one.
     * - Add a new entry to mAction with a one-line help summary.
     * - In the constructor, add a define() call for each parameter (either mandatory
     *   or optional) for the given action.
     */

    public final static String VERB_LIST   = "list";
    public final static String VERB_CREATE = "create";
    public final static String VERB_MOVE   = "move";
    public final static String VERB_DELETE = "delete";
    public final static String VERB_UPDATE = "update";

    public static final String OBJECT_SDK            = "sdk";
    public static final String OBJECT_AVD            = "avd";
    public static final String OBJECT_AVDS           = "avds";
    public static final String OBJECT_TARGET         = "target";
    public static final String OBJECT_TARGETS        = "targets";
    public static final String OBJECT_PROJECT        = "project";
    public static final String OBJECT_TEST_PROJECT   = "test-project";
    public static final String OBJECT_LIB_PROJECT    = "lib-project";
    public static final String OBJECT_EXPORT_PROJECT = "export-project";
    public static final String OBJECT_ADB            = "adb";

    public static final String ARG_ALIAS        = "alias";
    public static final String ARG_ACTIVITY     = "activity";

    public static final String KEY_ACTIVITY     = ARG_ACTIVITY;
    public static final String KEY_PACKAGE      = "package";
    public static final String KEY_MODE         = "mode";
    public static final String KEY_TARGET_ID    = OBJECT_TARGET;
    public static final String KEY_NAME         = "name";
    public static final String KEY_LIBRARY      = "library";
    public static final String KEY_PATH         = "path";
    public static final String KEY_FILTER       = "filter";
    public static final String KEY_SKIN         = "skin";
    public static final String KEY_SDCARD       = "sdcard";
    public static final String KEY_FORCE        = "force";
    public static final String KEY_RENAME       = "rename";
    public static final String KEY_SUBPROJECTS  = "subprojects";
    public static final String KEY_MAIN_PROJECT = "main";
    public static final String KEY_NO_UI        = "no-ui";
    public static final String KEY_NO_HTTPS     = "no-https";
    public static final String KEY_DRY_MODE     = "dry-mode";
    public static final String KEY_OBSOLETE     = "obsolete";

    /**
     * Action definitions for SdkManager command line.
     * <p/>
     * This list serves two purposes: first it is used to know which verb/object
     * actions are acceptable on the command-line; second it provides a summary
     * for each action that is printed in the help.
     * <p/>
     * Each entry is a string array with:
     * <ul>
     * <li> the verb.
     * <li> an object (use #NO_VERB_OBJECT if there's no object).
     * <li> a description.
     * <li> an alternate form for the object (e.g. plural).
     * </ul>
     */
    private final static String[][] ACTIONS = {
            { VERB_LIST, NO_VERB_OBJECT,
                "Lists existing targets or virtual devices." },
            { VERB_LIST, OBJECT_AVD,
                "Lists existing Android Virtual Devices.",
                OBJECT_AVDS },
            { VERB_LIST, OBJECT_TARGET,
                "Lists existing targets.",
                OBJECT_TARGETS },

            { VERB_CREATE, OBJECT_AVD,
                "Creates a new Android Virtual Device." },
            { VERB_MOVE, OBJECT_AVD,
                "Moves or renames an Android Virtual Device." },
            { VERB_DELETE, OBJECT_AVD,
                "Deletes an Android Virtual Device." },
            { VERB_UPDATE, OBJECT_AVD,
                "Updates an Android Virtual Device to match the folders of a new SDK." },

            { VERB_CREATE, OBJECT_PROJECT,
                "Creates a new Android Project." },
            { VERB_UPDATE, OBJECT_PROJECT,
                "Updates an Android Project (must have an AndroidManifest.xml)." },

            { VERB_CREATE, OBJECT_TEST_PROJECT,
                "Creates a new Android Test Project." },
            { VERB_UPDATE, OBJECT_TEST_PROJECT,
                "Updates an Android Test Project (must have an AndroidManifest.xml)." },

            { VERB_CREATE, OBJECT_LIB_PROJECT,
                "Creates a new Android Library Project." },
            { VERB_UPDATE, OBJECT_LIB_PROJECT,
                "Updates an Android Library Project (must have an AndroidManifest.xml)." },
/*
 * disabled until the feature is officially supported.
            { VERB_CREATE, OBJECT_EXPORT_PROJECT,
                "Creates a new Android Export Project." },
            { VERB_UPDATE, OBJECT_EXPORT_PROJECT,
                "Updates an Android Export Project (must have an export.properties)." },
*/
            { VERB_UPDATE, OBJECT_ADB,
                "Updates adb to support the USB devices declared in the SDK add-ons." },

            { VERB_UPDATE, OBJECT_SDK,
                "Updates the SDK by suggesting new platforms to install if available." }
        };

    public SdkCommandLine(ISdkLog logger) {
        super(logger, ACTIONS);

        // The following defines the parameters of the actions defined in mAction.

        // --- create avd ---

        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_AVD, "p", KEY_PATH,
                "Location path of the directory where the new AVD will be created", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_AVD, "n", KEY_NAME,
                "Name of the new AVD", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_AVD, "t", KEY_TARGET_ID,
                "Target id of the new AVD", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_AVD, "s", KEY_SKIN,
                "Skin of the new AVD", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_AVD, "c", KEY_SDCARD,
                "Path to a shared SD card image, or size of a new sdcard for the new AVD", null);
        define(Mode.BOOLEAN, false,
                VERB_CREATE, OBJECT_AVD, "f", KEY_FORCE,
                "Force creation (override an existing AVD)", false);

        // --- delete avd ---

        define(Mode.STRING, true,
                VERB_DELETE, OBJECT_AVD, "n", KEY_NAME,
                "Name of the AVD to delete", null);

        // --- move avd ---

        define(Mode.STRING, true,
                VERB_MOVE, OBJECT_AVD, "n", KEY_NAME,
                "Name of the AVD to move or rename", null);
        define(Mode.STRING, false,
                VERB_MOVE, OBJECT_AVD, "r", KEY_RENAME,
                "New name of the AVD to rename", null);
        define(Mode.STRING, false,
                VERB_MOVE, OBJECT_AVD, "p", KEY_PATH,
                "New location path of the directory where to move the AVD", null);

        // --- update avd ---

        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_AVD, "n", KEY_NAME,
                "Name of the AVD to update", null);

        // --- update sdk ---

        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_SDK, "u", KEY_NO_UI,
                "Update from command-line, without any UI", false);

        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_SDK, "s", KEY_NO_HTTPS,
                "Use HTTP instead of the default HTTPS for downloads", false);

        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_SDK, "f", KEY_FORCE,
                "Force replacing things that have been modified (samples, adb)", false);

        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_SDK, "t", KEY_FILTER,
                "A coma-separated list of " + Arrays.toString(SdkRepository.NODES) +
                " to limit update to specified types of packages",
                null);

        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_SDK, "o", KEY_OBSOLETE,
                "Install obsolete packages",
                false);

        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_SDK, "n", KEY_DRY_MODE,
                "Only simulates what would be updated but does not download/install anything",
                false);

        // --- create project ---

        /* Disabled for ADT 0.9 / Cupcake SDK 1.5_r1 release. [bug #1795718].
           This currently does not work, the alias build rules need to be fixed.

        define(Mode.ENUM, true,
                VERB_CREATE, OBJECT_PROJECT, "m", KEY_MODE,
                "Project mode", new String[] { ARG_ACTIVITY, ARG_ALIAS });
        */
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_PROJECT,
                "p", KEY_PATH,
                "Location path of new project", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_PROJECT, "t", KEY_TARGET_ID,
                "Target id of the new project", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_PROJECT, "k", KEY_PACKAGE,
                "Package name", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_PROJECT, "a", KEY_ACTIVITY,
                "Activity name", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_PROJECT, "n", KEY_NAME,
                "Project name", null);

        // --- create test-project ---

        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_TEST_PROJECT,
                "p", KEY_PATH,
                "Location path of new project", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_TEST_PROJECT, "n", KEY_NAME,
                "Project name", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_TEST_PROJECT, "m", KEY_MAIN_PROJECT,
                "Location path of the project to test, relative to the new project", null);

        // --- create lib-project ---

        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_LIB_PROJECT,
                "p", KEY_PATH,
                "Location path of new project", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_LIB_PROJECT, "t", KEY_TARGET_ID,
                "Target id of the new project", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_LIB_PROJECT, "n", KEY_NAME,
                "Project name", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_LIB_PROJECT, "k", KEY_PACKAGE,
                "Package name", null);

        // --- create export-project ---
/*
 * disabled until the feature is officially supported.

        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_EXPORT_PROJECT,
                "p", KEY_PATH,
                "Location path of new project", null);
        define(Mode.STRING, false,
                VERB_CREATE, OBJECT_EXPORT_PROJECT, "n", KEY_NAME,
                "Project name", null);
        define(Mode.STRING, true,
                VERB_CREATE, OBJECT_EXPORT_PROJECT, "k", KEY_PACKAGE,
                "Package name", null);
*/
        // --- update project ---

        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_PROJECT,
                "p", KEY_PATH,
                "Location path of the project", null);
        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_PROJECT,
                "t", KEY_TARGET_ID,
                "Target id to set for the project", null);
        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_PROJECT,
                "n", KEY_NAME,
                "Project name", null);
        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_PROJECT,
                "s", KEY_SUBPROJECTS,
                "Also update any projects in sub-folders, such as test projects.", false);
        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_PROJECT,
                "l", KEY_LIBRARY,
                "Location path of an Android Library to add, relative to the main project", null);

        // --- update test project ---

        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_TEST_PROJECT,
                "p", KEY_PATH,
                "Location path of the project", null);
        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_TEST_PROJECT,
                "m", KEY_MAIN_PROJECT,
                "Location path of the project to test, relative to the new project", null);

        // --- update lib project ---

        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_LIB_PROJECT,
                "p", KEY_PATH,
                "Location path of the project", null);
        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_LIB_PROJECT,
                "t", KEY_TARGET_ID,
                "Target id to set for the project", null);

        // --- update export project ---
/*
 * disabled until the feature is officially supported.
        define(Mode.STRING, true,
                VERB_UPDATE, OBJECT_EXPORT_PROJECT,
                "p", KEY_PATH,
                "Location path of the project", null);
        define(Mode.STRING, false,
                VERB_UPDATE, OBJECT_EXPORT_PROJECT,
                "n", KEY_NAME,
                "Project name", null);
        define(Mode.BOOLEAN, false,
                VERB_UPDATE, OBJECT_EXPORT_PROJECT, "f", KEY_FORCE,
                "Force replacing the build.xml file", false);
*/
    }

    @Override
    public boolean acceptLackOfVerb() {
        return true;
    }

    // -- some helpers for generic action flags

    /** Helper to retrieve the --path value. */
    public String getParamLocationPath() {
        return (String) getValue(null, null, KEY_PATH);
    }

    /**
     * Helper to retrieve the --target id value.
     * The id is a string. It can be one of:
     * - an integer, in which case it's the index of the target (cf "android list targets")
     * - a symbolic name such as android-N for platforn API N
     * - a symbolic add-on name such as written in the avd/*.ini files,
     *   e.g. "Google Inc.:Google APIs:3"
     */
    public String getParamTargetId() {
        return (String) getValue(null, null, KEY_TARGET_ID);
    }

    /** Helper to retrieve the --name value. */
    public String getParamName() {
        return (String) getValue(null, null, KEY_NAME);
    }

    /** Helper to retrieve the --skin value. */
    public String getParamSkin() {
        return (String) getValue(null, null, KEY_SKIN);
    }

    /** Helper to retrieve the --sdcard value. */
    public String getParamSdCard() {
        return (String) getValue(null, null, KEY_SDCARD);
    }

    /** Helper to retrieve the --force flag. */
    public boolean getFlagForce() {
        return ((Boolean) getValue(null, null, KEY_FORCE)).booleanValue();
    }

    // -- some helpers for avd action flags

    /** Helper to retrieve the --rename value for a move verb. */
    public String getParamMoveNewName() {
        return (String) getValue(VERB_MOVE, null, KEY_RENAME);
    }


    // -- some helpers for project action flags

    /** Helper to retrieve the --package value.
     * @param directObject the directObject of the action, either {@link #OBJECT_PROJECT}
     * or {@link #OBJECT_LIB_PROJECT}.
     */
    public String getParamProjectPackage(String directObject) {
        return ((String) getValue(null, directObject, KEY_PACKAGE));
    }

    /** Helper to retrieve the --activity for any project action. */
    public String getParamProjectActivity() {
        return ((String) getValue(null, OBJECT_PROJECT, KEY_ACTIVITY));
    }

    /** Helper to retrieve the --library value.
     * @param directObject the directObject of the action, either {@link #OBJECT_PROJECT}
     * or {@link #OBJECT_LIB_PROJECT}.
     */
    public String getParamProjectLibrary(String directObject) {
        return ((String) getValue(null, directObject, KEY_LIBRARY));
    }


    /** Helper to retrieve the --subprojects for any project action. */
    public boolean getParamSubProject() {
        return ((Boolean) getValue(null, OBJECT_PROJECT, KEY_SUBPROJECTS)).booleanValue();
    }

    // -- some helpers for test-project action flags

    /** Helper to retrieve the --main value. */
    public String getParamTestProjectMain() {
        return ((String) getValue(null, null, KEY_MAIN_PROJECT));
    }


    // -- some helpers for update sdk flags

    /** Helper to retrieve the --force flag. */
    public boolean getFlagNoUI() {
        return ((Boolean) getValue(null, null, KEY_NO_UI)).booleanValue();
    }

    /** Helper to retrieve the --no-https flag. */
    public boolean getFlagNoHttps() {
        return ((Boolean) getValue(null, null, KEY_NO_HTTPS)).booleanValue();
    }

    /** Helper to retrieve the --dry-mode flag. */
    public boolean getFlagDryMode() {
        return ((Boolean) getValue(null, null, KEY_DRY_MODE)).booleanValue();
    }

    /** Helper to retrieve the --obsolete flag. */
    public boolean getFlagObsolete() {
        return ((Boolean) getValue(null, null, KEY_OBSOLETE)).booleanValue();
    }

    /** Helper to retrieve the --filter value. */
    public String getParamFilter() {
        return ((String) getValue(null, null, KEY_FILTER));
    }
}
