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

package android.widget.cts;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.cts.stub.R;

import dalvik.annotation.BrokenTest;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.ToBeFixed;

/**
 * Test {@link TableLayout}.
 */
@TestTargetClass(TableLayout.class)
public class TableLayoutTest extends ActivityInstrumentationTestCase2<TableStubActivity> {
    private Context mContext;

    public TableLayoutTest() {
        super("com.android.cts.stub", TableStubActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructor(s) of {@link TableLayout}",
            method = "TableLayout",
            args = {android.content.Context.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructor(s) of {@link TableLayout}",
            method = "TableLayout",
            args = {android.content.Context.class, android.util.AttributeSet.class}
        )
    })
    @UiThreadTest
    public void testConstructor() {
        new TableLayout(mContext);

        new TableLayout(mContext, null);

        TableStubActivity activity = getActivity();
        activity.setContentView(com.android.cts.stub.R.layout.table_layout_1);
        TableLayout tableLayout = (TableLayout) activity
                .findViewById(com.android.cts.stub.R.id.table1);
        assertTrue(tableLayout.isColumnCollapsed(0));
        assertTrue(tableLayout.isColumnStretchable(2));

        activity.setContentView(com.android.cts.stub.R.layout.table_layout_2);
        tableLayout = (TableLayout) activity.findViewById(com.android.cts.stub.R.id.table2);
        assertTrue(tableLayout.isColumnShrinkable(1));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test setOnHierarchyChangeListener(OnHierarchyChangeListener listener)",
        method = "setOnHierarchyChangeListener",
        args = {android.view.ViewGroup.OnHierarchyChangeListener.class}
    )
    public void testSetOnHierarchyChangeListener() {
        TableLayout tableLayout = new TableLayout(mContext);

        MockOnHierarchyChangeListener listener = new MockOnHierarchyChangeListener();
        tableLayout.setOnHierarchyChangeListener(listener);

        tableLayout.addView(new TextView(mContext));
        assertTrue(listener.hasCalledOnChildViewAdded());
        tableLayout.removeViewAt(0);
        assertTrue(listener.hasCalledOnChildViewRemoved());

        listener.reset();

        tableLayout.setOnHierarchyChangeListener(null);
        tableLayout.addView(new TextView(mContext));
        assertFalse(listener.hasCalledOnChildViewAdded());
        tableLayout.removeViewAt(0);
        assertFalse(listener.hasCalledOnChildViewRemoved());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test requestLayout()",
        method = "requestLayout",
        args = {}
    )
    public void testRequestLayout() {
        TableLayout tableLayout = new TableLayout(mContext);
        tableLayout.addView(new TextView(mContext));
        tableLayout.addView(new ListView(mContext));
        tableLayout.layout(0, 0, 200, 300);
        assertFalse(tableLayout.isLayoutRequested());
        assertFalse(tableLayout.getChildAt(0).isLayoutRequested());
        assertFalse(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.requestLayout();
        assertTrue(tableLayout.isLayoutRequested());
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ShrinkAllColumns",
            method = "isShrinkAllColumns",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ShrinkAllColumns",
            method = "setShrinkAllColumns",
            args = {boolean.class}
        )
    })
    public void testAccessShrinkAllColumns() {
        TableLayout tableLayout = new TableLayout(mContext);
        assertFalse(tableLayout.isShrinkAllColumns());

        tableLayout.setShrinkAllColumns(true);
        assertTrue(tableLayout.isShrinkAllColumns());
        tableLayout.setShrinkAllColumns(false);
        assertFalse(tableLayout.isShrinkAllColumns());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access StretchAllColumns",
            method = "isStretchAllColumns",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access StretchAllColumns",
            method = "setStretchAllColumns",
            args = {boolean.class}
        )
    })
    public void testAccessStretchAllColumns() {
        TableLayout tableLayout = new TableLayout(mContext);
        assertFalse(tableLayout.isStretchAllColumns());

        tableLayout.setStretchAllColumns(true);
        assertTrue(tableLayout.isStretchAllColumns());
        tableLayout.setStretchAllColumns(false);
        assertFalse(tableLayout.isStretchAllColumns());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnCollapsed",
            method = "isColumnCollapsed",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnCollapsed",
            method = "setColumnCollapsed",
            args = {int.class, boolean.class}
        )
    })
    public void testAccessColumnCollapsed() {
        TableLayout tableLayout = new TableLayout(mContext);
        tableLayout.addView(new TextView(mContext));
        tableLayout.addView(new TextView(mContext));
        assertFalse(tableLayout.isColumnCollapsed(0));
        assertFalse(tableLayout.isColumnCollapsed(1));

        tableLayout.layout(0, 0, 200, 300);
        assertFalse(tableLayout.getChildAt(0).isLayoutRequested());
        assertFalse(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.setColumnCollapsed(0, true);
        assertTrue(tableLayout.isColumnCollapsed(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnCollapsed(1, true);
        assertTrue(tableLayout.isColumnCollapsed(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnCollapsed(0, false);
        assertFalse(tableLayout.isColumnCollapsed(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnCollapsed(1, false);
        assertFalse(tableLayout.isColumnCollapsed(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnStretchable",
            method = "isColumnStretchable",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnStretchable",
            method = "setColumnStretchable",
            args = {int.class, boolean.class}
        )
    })
    public void testAccessColumnStretchable() {
        TableLayout tableLayout = new TableLayout(mContext);
        tableLayout.addView(new TableRow(mContext));
        tableLayout.addView(new TableRow(mContext));
        assertFalse(tableLayout.isColumnStretchable(0));
        assertFalse(tableLayout.isColumnStretchable(1));

        tableLayout.layout(0, 0, 200, 300);
        assertFalse(tableLayout.getChildAt(0).isLayoutRequested());
        assertFalse(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.setColumnStretchable(0, true);
        assertTrue(tableLayout.isColumnStretchable(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnStretchable(1, true);
        assertTrue(tableLayout.isColumnStretchable(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnStretchable(0, false);
        assertFalse(tableLayout.isColumnStretchable(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnStretchable(1, false);
        assertFalse(tableLayout.isColumnStretchable(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "test whether columns are actually stretched",
            method = "setColumnStretchable",
            args = {java.lang.Integer.class, java.lang.Boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "test whether columns are actually stretched",
            method = "setStretchAllColumns",
            args = {java.lang.Boolean.class}
        )
    })
    @ToBeFixed( bug = "", explanation = "After set a column unable to be stretched," +
            " the other strtchable columns are not strtched as much as available.")
    public void testColumnStretchableEffect() {
        final TableStubActivity activity = getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.setContentView(com.android.cts.stub.R.layout.table_layout_1);
            }
        });
        getInstrumentation().waitForIdleSync();
        final TableLayout tableLayout =
                (TableLayout) activity.findViewById(com.android.cts.stub.R.id.table1);

        // Preparation: remove Collapsed mark for column 0.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnCollapsed(0, false);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertFalse(tableLayout.isColumnStretchable(0));
        assertFalse(tableLayout.isColumnStretchable(1));
        assertTrue(tableLayout.isColumnStretchable(2));

        TextView column0 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(0);
        TextView column1 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(1);
        TextView column2 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(2);
        int oldWidth0 = column0.getWidth();
        int oldWidth1 = column1.getWidth();
        int oldWidth2 = column2.getWidth();
        column0.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth0 = column0.getMeasuredWidth();
        column1.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth1 = column1.getMeasuredWidth();
        column2.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth2 = column2.getMeasuredWidth();
        int totalSpace = tableLayout.getWidth() - orignalWidth0
                - orignalWidth1 - orignalWidth2;

        // Test: set column 1 is able to be stretched.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnStretchable(1, true);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertEquals(oldWidth0, column0.getWidth());
        assertTrue(oldWidth1 < column1.getWidth());
        assertTrue(oldWidth2 > column2.getWidth());
        int extraSpace = totalSpace / 2;
        assertEquals(orignalWidth0, column0.getWidth());
        assertEquals(orignalWidth1 + extraSpace, column1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, column2.getWidth());
        oldWidth0 = column0.getWidth();
        oldWidth1 = column1.getWidth();
        oldWidth2 = column2.getWidth();

        // Test: set column 0 is able to be stretched.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnStretchable(0, true);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < column0.getWidth());
        assertTrue(oldWidth1 > column1.getWidth());
        assertTrue(oldWidth2 > column2.getWidth());
        extraSpace = totalSpace / 3;
        assertEquals(orignalWidth0 + extraSpace, column0.getWidth());
        assertEquals(orignalWidth1 + extraSpace, column1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, column2.getWidth());
        oldWidth0 = column0.getWidth();
        oldWidth1 = column1.getWidth();
        oldWidth2 = column2.getWidth();

        // Test: set column 2 is unable to be stretched.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnStretchable(2, false);
            }
        });
        getInstrumentation().waitForIdleSync();
        // assertTrue(oldWidth0 < column0.getWidth());
        // assertTrue(oldWidth1 < column1.getWidth());
        assertEquals(oldWidth0, column0.getWidth());
        assertEquals(oldWidth1, column1.getWidth());
        assertTrue(oldWidth2 > column2.getWidth());
        // extraSpace = totalSpace / 2;
        extraSpace = totalSpace / 3;
        assertEquals(orignalWidth0 + extraSpace, column0.getWidth());
        assertEquals(orignalWidth1 + extraSpace, column1.getWidth());
        assertEquals(orignalWidth2, column2.getWidth());
        oldWidth0 = column0.getWidth();
        oldWidth1 = column1.getWidth();
        oldWidth2 = column2.getWidth();

        // Test: mark all columns are able to be stretched.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setStretchAllColumns(true);
                tableLayout.requestLayout();
            }
        });
        getInstrumentation().waitForIdleSync();
        // assertTrue(oldWidth0 > column0.getWidth());
        // assertTrue(oldWidth1 > column1.getWidth());
        assertEquals(oldWidth0, column0.getWidth());
        assertEquals(oldWidth1, column1.getWidth());
        assertTrue(oldWidth2 < column2.getWidth());
        extraSpace = totalSpace / 3;
        assertEquals(orignalWidth0 + extraSpace, column0.getWidth());
        assertEquals(orignalWidth1 + extraSpace, column1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, column2.getWidth());
        oldWidth0 = column0.getWidth();
        oldWidth1 = column1.getWidth();
        oldWidth2 = column2.getWidth();

        // Test: Remove the mark for all columns are able to be stretched.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setStretchAllColumns(false);
                tableLayout.requestLayout();
            }
        });
        getInstrumentation().waitForIdleSync();
        // assertTrue(oldWidth0 > column0.getWidth());
        // assertTrue(oldWidth1 > column1.getWidth());
        assertEquals(oldWidth0, column0.getWidth());
        assertEquals(oldWidth1, column1.getWidth());
        assertTrue(oldWidth2 > column2.getWidth());
        assertEquals(orignalWidth0 + extraSpace, column0.getWidth());
        assertEquals(orignalWidth1 + extraSpace, column1.getWidth());
        assertEquals(orignalWidth2, column2.getWidth());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnShrinkable",
            method = "isColumnShrinkable",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test methods which access ColumnShrinkable",
            method = "setColumnShrinkable",
            args = {int.class, boolean.class}
        )
    })
    public void testAccessColumnShrinkable() {
        TableLayout tableLayout = new TableLayout(mContext);
        tableLayout.addView(new TableRow(mContext));
        tableLayout.addView(new TableRow(mContext));
        assertFalse(tableLayout.isColumnShrinkable(0));
        assertFalse(tableLayout.isColumnShrinkable(1));

        tableLayout.layout(0, 0, 200, 300);
        assertFalse(tableLayout.getChildAt(0).isLayoutRequested());
        assertFalse(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.setColumnShrinkable(0, true);
        assertTrue(tableLayout.isColumnShrinkable(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnShrinkable(1, true);
        assertTrue(tableLayout.isColumnShrinkable(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnShrinkable(0, false);
        assertFalse(tableLayout.isColumnShrinkable(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        tableLayout.setColumnShrinkable(1, false);
        assertFalse(tableLayout.isColumnShrinkable(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "test whether columns are actually shrunk",
            method = "setColumnShrinkable",
            args = {java.lang.Integer.class, java.lang.Boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "test whether columns are actually shrunk",
            method = "setShrinkAllColumns",
            args = {java.lang.Boolean.class}
        )
    })
    @ToBeFixed( bug = "", explanation = "After set a column unable to be shrunk," +
            " the other shrinkable columns are not shrunk more.")
    @BrokenTest("fails consistently")
    public void testColumnShrinkableEffect() {
        final TableStubActivity activity = getActivity();
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.setContentView(com.android.cts.stub.R.layout.table_layout_2);
            }
        });
        getInstrumentation().waitForIdleSync();
        final TableLayout tableLayout =
                (TableLayout) activity.findViewById(com.android.cts.stub.R.id.table2);

        final int columnVirtualIndex0 = 1;
        final int columnVirtualIndex1 = 2;
        final int columnVirtualIndex2 = 4;
        final TextView child0 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(0);
        final TextView child1 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(1);
        final TextView child2 = (TextView) ((TableRow) tableLayout.getChildAt(0)).getChildAt(2);

        // get the original width of each child.
        int oldWidth0 = child0.getWidth();
        int oldWidth1 = child1.getWidth();
        int oldWidth2 = child2.getWidth();
        child0.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth0 = child0.getMeasuredWidth();
        // child1 has 2 columns.
        child1.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        TextView column12 = (TextView) ((TableRow) tableLayout.getChildAt(1)).getChildAt(2);
        column12.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth1 = child1.getMeasuredWidth() + column12.getMeasuredWidth();
        child2.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
        int orignalWidth2 = child2.getMeasuredWidth();
        int totalSpace = tableLayout.getWidth() - orignalWidth0
                - orignalWidth1 - orignalWidth2;

        // Test: set column 2 which is the start column for child 1 is able to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnShrinkable(columnVirtualIndex1, true);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < child0.getWidth());
        assertTrue(oldWidth1 > child1.getWidth());
        assertEquals(oldWidth2, child2.getWidth());
        int extraSpace = totalSpace / 2;
        assertEquals(dropNegative(orignalWidth0 + extraSpace), child0.getWidth());
        assertEquals(dropNegative(orignalWidth1 + extraSpace), child1.getWidth());
        assertEquals(orignalWidth2, child2.getWidth());
        oldWidth0 = child0.getWidth();
        oldWidth1 = child1.getWidth();
        oldWidth2 = child2.getWidth();

        // Test: set column 4 which is the column for child 2 is able to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnShrinkable(columnVirtualIndex2, true);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < child0.getWidth());
        assertTrue(oldWidth1 < child1.getWidth());
        assertTrue(oldWidth2 > child2.getWidth());
        extraSpace = totalSpace / 3;
        assertEquals(dropNegative(orignalWidth0 + extraSpace), child0.getWidth());
        assertEquals(dropNegative(orignalWidth1 + extraSpace), child1.getWidth());
        assertEquals(dropNegative(orignalWidth2 + extraSpace), child2.getWidth());
        oldWidth0 = child0.getWidth();
        oldWidth1 = child1.getWidth();
        oldWidth2 = child2.getWidth();

        // Test: set column 3 which is the end column for child 1 is able to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnShrinkable(columnVirtualIndex1+1, true);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < child0.getWidth());
        assertTrue(oldWidth1 > child1.getWidth());
        assertTrue(oldWidth2 < child2.getWidth());
        extraSpace = totalSpace / 4;
        assertEquals(dropNegative(orignalWidth0 + extraSpace), child0.getWidth());
        assertEquals(dropNegative(orignalWidth1 + extraSpace * 2), child1.getWidth());
        assertEquals(dropNegative(orignalWidth2 + extraSpace), child2.getWidth());
        oldWidth0 = child0.getWidth();
        oldWidth1 = child1.getWidth();
        oldWidth2 = child2.getWidth();

        // Test: set column 1 which is the column for child 0 is unable to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setColumnShrinkable(columnVirtualIndex0, false);
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < child0.getWidth());
        // assertTrue(oldWidth1 > column1.getWidth());
        // assertTrue(oldWidth2 > column2.getWidth());
        assertEquals(oldWidth1, child1.getWidth());
        assertEquals(oldWidth2, child2.getWidth());
        // extraSpace = totalSpace / 3;
        extraSpace = totalSpace / 4;
        assertEquals(orignalWidth0, child0.getWidth());
        assertEquals(orignalWidth1 + extraSpace * 2, child1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, child2.getWidth());
        oldWidth0 = child0.getWidth();
        oldWidth1 = child1.getWidth();
        oldWidth2 = child2.getWidth();

        // Test: mark all columns are able to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setShrinkAllColumns(true);
                tableLayout.requestLayout();
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 > child0.getWidth());
        assertTrue(oldWidth1 < child1.getWidth());
        assertTrue(oldWidth2 < child2.getWidth());
        extraSpace = totalSpace / 5;
        assertEquals(orignalWidth0 + extraSpace, child0.getWidth());
        assertEquals(orignalWidth1 + extraSpace * 2, child1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, child2.getWidth());
        oldWidth0 = child0.getWidth();
        oldWidth1 = child1.getWidth();
        oldWidth2 = child2.getWidth();

        // Test: Remove the mark for all columns are able to be shrunk.
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                tableLayout.setShrinkAllColumns(false);
                tableLayout.requestLayout();
            }
        });
        getInstrumentation().waitForIdleSync();
        assertTrue(oldWidth0 < child0.getWidth());
        assertTrue(oldWidth1 > child1.getWidth());
        assertTrue(oldWidth2 > child2.getWidth());
        // extraSpace = totalSpace / 3;
        extraSpace = totalSpace / 4;
        assertEquals(orignalWidth0, child0.getWidth());
        assertEquals(orignalWidth1 + extraSpace * 2, child1.getWidth());
        assertEquals(orignalWidth2 + extraSpace, child2.getWidth());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test addView(View child)",
        method = "addView",
        args = {android.view.View.class}
    )
    @ToBeFixed( bug = "1417734", explanation = "NullPointerException issue")
    public void testAddView1() {
        TableLayout tableLayout = new TableLayout(mContext);

        View child1 = new TextView(mContext);
        tableLayout.addView(child1);
        assertSame(child1, tableLayout.getChildAt(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child2 = new RelativeLayout(mContext);
        tableLayout.addView(child2);
        assertSame(child1, tableLayout.getChildAt(0));
        assertSame(child2, tableLayout.getChildAt(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child3 = new ListView(mContext);
        tableLayout.addView(child3);
        assertSame(child1, tableLayout.getChildAt(0));
        assertSame(child2, tableLayout.getChildAt(1));
        assertSame(child3, tableLayout.getChildAt(2));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(2).isLayoutRequested());

        // exceptional
        try {
            tableLayout.addView(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test addView(View child, int index)",
        method = "addView",
        args = {android.view.View.class, int.class}
    )
    @ToBeFixed( bug = "1417734", explanation = "IndexOutOfBoundsException and " +
            "NullPointerException issue")
    public void testAddView2() {
        TableLayout tableLayout = new TableLayout(mContext);

        View child1 = new TextView(mContext);
        tableLayout.addView(child1, 0);
        assertSame(child1, tableLayout.getChildAt(0));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child2 = new RelativeLayout(mContext);
        tableLayout.addView(child2, 0);
        assertSame(child2, tableLayout.getChildAt(0));
        assertSame(child1, tableLayout.getChildAt(1));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child3 = new ListView(mContext);
        tableLayout.addView(child3, -1);
        assertSame(child2, tableLayout.getChildAt(0));
        assertSame(child1, tableLayout.getChildAt(1));
        assertSame(child3, tableLayout.getChildAt(2));
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(2).isLayoutRequested());

        try {
            tableLayout.addView(new ListView(mContext), Integer.MAX_VALUE);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            tableLayout.addView(null, -1);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test addView(View child, ViewGroup.LayoutParams params)",
        method = "addView",
        args = {android.view.View.class, android.view.ViewGroup.LayoutParams.class}
    )
    @ToBeFixed( bug = "1417734", explanation = "NullPointerException issue")
    public void testAddView3() {
        TableLayout tableLayout = new TableLayout(mContext);

        View child1 = new TextView(mContext);
        assertNull(child1.getLayoutParams());
        tableLayout.addView(child1, new ViewGroup.LayoutParams(100, 200));
        assertSame(child1, tableLayout.getChildAt(0));
        assertEquals(100, tableLayout.getChildAt(0).getLayoutParams().width);
        assertEquals(200, tableLayout.getChildAt(0).getLayoutParams().height);
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child2 = new TableRow(mContext);
        assertNull(child2.getLayoutParams());
        tableLayout.addView(child2, new TableRow.LayoutParams(200, 300, 1));
        assertSame(child1, tableLayout.getChildAt(0));
        assertSame(child2, tableLayout.getChildAt(1));
        assertEquals(100, tableLayout.getChildAt(0).getLayoutParams().width);
        assertEquals(200, tableLayout.getChildAt(0).getLayoutParams().height);
        assertEquals(200, tableLayout.getChildAt(1).getLayoutParams().width);
        assertEquals(300, tableLayout.getChildAt(1).getLayoutParams().height);
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        try {
            tableLayout.addView(null, new TableLayout.LayoutParams(200, 300));
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            tableLayout.addView(new ListView(mContext), null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test addView(View child, int index, ViewGroup.LayoutParams params)",
        method = "addView",
        args = {android.view.View.class, int.class, android.view.ViewGroup.LayoutParams.class}
    )
    @ToBeFixed( bug = "1417734", explanation = "IndexOutOfBoundsException and " +
            "NullPointerException issue")
    public void testAddView4() {
        TableLayout tableLayout = new TableLayout(mContext);

        View child1 = new TextView(mContext);
        assertNull(child1.getLayoutParams());
        tableLayout.addView(child1, 0, new ViewGroup.LayoutParams(100, 200));
        assertSame(child1, tableLayout.getChildAt(0));
        assertEquals(100, tableLayout.getChildAt(0).getLayoutParams().width);
        assertEquals(200, tableLayout.getChildAt(0).getLayoutParams().height);
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child2 = new TableRow(mContext);
        assertNull(child2.getLayoutParams());
        tableLayout.addView(child2, 0, new TableRow.LayoutParams(200, 300, 1));
        assertSame(child2, tableLayout.getChildAt(0));
        assertSame(child1, tableLayout.getChildAt(1));
        assertEquals(200, tableLayout.getChildAt(0).getLayoutParams().width);
        assertEquals(300, tableLayout.getChildAt(0).getLayoutParams().height);
        assertEquals(100, tableLayout.getChildAt(1).getLayoutParams().width);
        assertEquals(200, tableLayout.getChildAt(1).getLayoutParams().height);
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());

        tableLayout.layout(0, 0, 200, 300);

        View child3 = new ListView(mContext);
        assertNull(child3.getLayoutParams());
        tableLayout.addView(child3, -1, new ListView.LayoutParams(300, 400));
        assertSame(child2, tableLayout.getChildAt(0));
        assertSame(child1, tableLayout.getChildAt(1));
        assertSame(child3, tableLayout.getChildAt(2));
        assertEquals(200, tableLayout.getChildAt(0).getLayoutParams().width);
        assertEquals(300, tableLayout.getChildAt(0).getLayoutParams().height);
        assertEquals(100, tableLayout.getChildAt(1).getLayoutParams().width);
        assertEquals(200, tableLayout.getChildAt(1).getLayoutParams().height);
        assertEquals(300, tableLayout.getChildAt(2).getLayoutParams().width);
        assertEquals(400, tableLayout.getChildAt(2).getLayoutParams().height);
        assertTrue(tableLayout.getChildAt(0).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(1).isLayoutRequested());
        assertTrue(tableLayout.getChildAt(2).isLayoutRequested());

        try {
            tableLayout.addView(new ListView(mContext), Integer.MAX_VALUE,
                    new TableLayout.LayoutParams(200, 300));
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            tableLayout.addView(null, -1, new TableLayout.LayoutParams(200, 300));
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }

        try {
            tableLayout.addView(new ListView(mContext), -1, null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test generateLayoutParams(AttributeSet attrs)",
        method = "generateLayoutParams",
        args = {android.util.AttributeSet.class}
    )
    public void testGenerateLayoutParams1() {
        TableLayout tableLayout = new TableLayout(mContext);

        TableStubActivity activity = getActivity();
        XmlResourceParser parser = activity.getResources().getLayout(R.layout.table_layout_1);
        AttributeSet attr = Xml.asAttributeSet(parser);

        assertNotNull(tableLayout.generateLayoutParams(attr));

        assertNotNull(tableLayout.generateLayoutParams((AttributeSet) null));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test checkLayoutParams(ViewGroup.LayoutParams p)",
        method = "checkLayoutParams",
        args = {android.view.ViewGroup.LayoutParams.class}
    )
    public void testCheckLayoutParams() {
        MockTableLayout mockTableLayout = new MockTableLayout(mContext);

        assertTrue(mockTableLayout.checkLayoutParams(new TableLayout.LayoutParams(200, 300)));

        assertFalse(mockTableLayout.checkLayoutParams(new ViewGroup.LayoutParams(200, 300)));

        assertFalse(mockTableLayout.checkLayoutParams(new RelativeLayout.LayoutParams(200, 300)));

        assertFalse(mockTableLayout.checkLayoutParams(null));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test generateDefaultLayoutParams()",
        method = "generateDefaultLayoutParams",
        args = {}
    )
    public void testGenerateDefaultLayoutParams() {
        MockTableLayout mockTableLayout = new MockTableLayout(mContext);

        LinearLayout.LayoutParams layoutParams = mockTableLayout.generateDefaultLayoutParams();
        assertNotNull(layoutParams);
        assertTrue(layoutParams instanceof TableLayout.LayoutParams);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test generateLayoutParams(ViewGroup.LayoutParams p)",
        method = "generateLayoutParams",
        args = {android.view.ViewGroup.LayoutParams.class}
    )
    @ToBeFixed( bug = "1417734", explanation = "NullPointerException issue")
    public void testGenerateLayoutParams2() {
        MockTableLayout mockTableLayout = new MockTableLayout(mContext);

        LinearLayout.LayoutParams layoutParams = mockTableLayout.generateLayoutParams(
                new ViewGroup.LayoutParams(200, 300));
        assertNotNull(layoutParams);
        assertEquals(200, layoutParams.width);
        assertEquals(300, layoutParams.height);
        assertTrue(layoutParams instanceof TableLayout.LayoutParams);

        try {
            layoutParams = mockTableLayout.generateLayoutParams((ViewGroup.LayoutParams) null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onLayout(boolean changed, int l, int t, int r, int b)",
        method = "onLayout",
        args = {boolean.class, int.class, int.class, int.class, int.class}
    )
    @ToBeFixed( bug = "1400249", explanation = "hard to do unit test," +
            " will be tested by functional test.")
    public void testOnLayout() {
        MockTableLayout mockTableLayout = new MockTableLayout(mContext);

        mockTableLayout.onLayout(false, 0, 0, 20, 20);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onMeasure(int widthMeasureSpec, int heightMeasureSpec)",
        method = "onMeasure",
        args = {int.class, int.class}
    )
    @ToBeFixed( bug = "1400249", explanation = "hard to do unit test," +
            " will be tested by functional test.")
    public void testOnMeasure() {
        MockTableLayout mockTableLayout = new MockTableLayout(mContext);

        mockTableLayout.onMeasure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
    }

    private int dropNegative(int number) {
        return (number > 0 ? number : 0);
    }

    private class MockOnHierarchyChangeListener implements OnHierarchyChangeListener {
        private boolean mCalledOnChildViewAdded = false;
        private boolean mCalledOnChildViewRemoved = false;

        /*
         * (non-Javadoc)
         *
         * @see
         * android.view.ViewGroup.OnHierarchyChangeListener#onChildViewAdded
         * (View, View)
         */
        public void onChildViewAdded(View parent, View child) {
            mCalledOnChildViewAdded = true;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.view.ViewGroup.OnHierarchyChangeListener#onChildViewRemoved
         * (View, View)
         */
        public void onChildViewRemoved(View parent, View child) {
            mCalledOnChildViewRemoved = true;
        }

        public boolean hasCalledOnChildViewAdded() {
            return mCalledOnChildViewAdded;
        }

        public boolean hasCalledOnChildViewRemoved() {
            return mCalledOnChildViewRemoved;
        }

        public void reset() {
            mCalledOnChildViewAdded = false;
            mCalledOnChildViewRemoved = false;
        }
    }

    /*
     * Mock class for TableLayout to test protected methods
     */
    private class MockTableLayout extends TableLayout {
        public MockTableLayout(Context context) {
            super(context);
        }

        @Override
        protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
            return super.checkLayoutParams(p);
        }

        @Override
        protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
            return super.generateDefaultLayoutParams();
        }

        @Override
        protected LinearLayout.LayoutParams generateLayoutParams(
                ViewGroup.LayoutParams p) {
            return super.generateLayoutParams(p);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

