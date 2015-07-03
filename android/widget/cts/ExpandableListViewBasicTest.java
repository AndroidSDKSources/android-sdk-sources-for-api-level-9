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

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.KeyEvent;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.cts.util.ExpandableListScenario;
import android.widget.cts.util.ListUtil;
import android.widget.cts.util.ExpandableListScenario.MyGroup;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;

@TestTargetClass(ExpandableListView.class)
public class ExpandableListViewBasicTest extends
        ActivityInstrumentationTestCase2<ExpandableListSimple> {
    private ExpandableListScenario mActivity;
    private ExpandableListView mListView;
    private ExpandableListAdapter mAdapter;
    private ListUtil mListUtil;

    public ExpandableListViewBasicTest() {
        super("com.android.cts.stub", ExpandableListSimple.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mListView = mActivity.getExpandableListView();
        mAdapter = mListView.getExpandableListAdapter();
        mListUtil = new ListUtil(mListView, getInstrumentation());
    }

    @MediumTest
    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(mListView);
    }

    private int expandGroup(int numChildren, boolean atLeastOneChild) {
        final int groupPos = mActivity.findGroupWithNumChildren(numChildren, atLeastOneChild);

        assertTrue("Could not find group to expand", groupPos >= 0);
        assertFalse("Group is already expanded", mListView.isGroupExpanded(groupPos));
        mListUtil.arrowScrollToSelectedPosition(groupPos);
        getInstrumentation().waitForIdleSync();
        sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        getInstrumentation().waitForIdleSync();
        assertTrue("Group did not expand", mListView.isGroupExpanded(groupPos));

        return groupPos;
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link ExpandableListView#expandGroup(int)}",
        method = "expandGroup",
        args = {int.class}
    )
    @MediumTest
    public void testExpandGroup() {
        expandGroup(-1, true);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link ExpandableListView#collapseGroup(int)}",
        method = "collapseGroup",
        args = {int.class}
    )
    @MediumTest
    public void testCollapseGroup() {
        final int groupPos = expandGroup(-1, true);

        sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        getInstrumentation().waitForIdleSync();
        assertFalse("Group did not collapse", mListView.isGroupExpanded(groupPos));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test {@link ExpandableListView#expandGroup(int)}",
            method = "expandGroup",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test {@link ExpandableListView#expandGroup(int)}",
            method = "isGroupExpanded",
            args = {int.class}
        )
    })
    @MediumTest
    public void testExpandedGroupMovement() {
        // Expand the first group
        mListUtil.arrowScrollToSelectedPosition(0);
        sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        getInstrumentation().waitForIdleSync();

        // Ensure it expanded
        assertTrue("Group did not expand", mListView.isGroupExpanded(0));

        // Wait until that's all good
        getInstrumentation().waitForIdleSync();

        // Make sure it expanded
        assertTrue("Group did not expand", mListView.isGroupExpanded(0));

        // Insert a collapsed group in front of the one just expanded
        List<MyGroup> groups = mActivity.getGroups();
        MyGroup insertedGroup = new MyGroup(1);
        groups.add(0, insertedGroup);

        // Notify data change
        assertTrue("Adapter is not an instance of the base adapter",
                mAdapter instanceof BaseExpandableListAdapter);
        final BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) mAdapter;

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        getInstrumentation().waitForIdleSync();

        // Make sure the right group is expanded
        assertTrue("The expanded state didn't stay with the proper group",
                mListView.isGroupExpanded(1));
        assertFalse("The expanded state was given to the inserted group",
                mListView.isGroupExpanded(0));
    }
}
