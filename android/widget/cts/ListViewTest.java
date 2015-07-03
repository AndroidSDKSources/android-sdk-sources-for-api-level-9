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

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.cts.stub.R;
import com.google.android.collect.Lists;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

@TestTargetClass(ListView.class)
public class ListViewTest extends ActivityInstrumentationTestCase2<ListViewStubActivity> {
    private final String[] mCountryList = new String[] {
        "Argentina", "Australia", "China", "France", "Germany", "Italy", "Japan", "United States"
    };
    private final String[] mNameList = new String[] {
        "Jacky", "David", "Kevin", "Michael", "Andy"
    };
    private final String[] mEmptyList = new String[0];

    private ListView mListView;
    private Activity mActivity;
    private Instrumentation mInstrumentation;
    private AttributeSet mAttributeSet;
    private ArrayAdapter<String> mAdapter_countries;
    private ArrayAdapter<String> mAdapter_names;
    private ArrayAdapter<String> mAdapter_empty;

    public ListViewTest() {
        super("com.android.cts.stub", ListViewStubActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mInstrumentation = getInstrumentation();
        XmlPullParser parser = mActivity.getResources().getXml(R.layout.listview_layout);
        mAttributeSet = Xml.asAttributeSet(parser);

        mAdapter_countries = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_list_item_1, mCountryList);
        mAdapter_names = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1,
                mNameList);
        mAdapter_empty = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1,
                mEmptyList);

        mListView = (ListView) mActivity.findViewById(R.id.listview_default);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ListView",
            args = {android.content.Context.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ListView",
            args = {android.content.Context.class, android.util.AttributeSet.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ListView",
            args = {android.content.Context.class, android.util.AttributeSet.class, int.class}
        )
    })
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete")
    public void testConstructor() {
        new ListView(mActivity);
        new ListView(mActivity, mAttributeSet);
        new ListView(mActivity, mAttributeSet, 0);

        try {
            new ListView(null);
            fail("There should be a NullPointerException thrown out. ");
        } catch (NullPointerException e) {
            // expected, test success.
        }

        try {
            new ListView(null, null);
            fail("There should be a NullPointerException thrown out. ");
        } catch (NullPointerException e) {
            // expected, test success.
        }

        try {
            new ListView(null, null, -1);
            fail("There should be a NullPointerException thrown out. ");
        } catch (NullPointerException e) {
            // expected, test success.
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setAdapter",
            args = {android.widget.ListAdapter.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getMaxScrollAmount",
            args = {}
        )
    })
    public void testGetMaxScrollAmount() {
        setAdapter(mAdapter_empty);
        int scrollAmount = mListView.getMaxScrollAmount();
        assertEquals(0, scrollAmount);

        setAdapter(mAdapter_names);
        scrollAmount = mListView.getMaxScrollAmount();
        assertTrue(scrollAmount > 0);
    }

    private void setAdapter(final ArrayAdapter<String> adapter) {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(adapter);
            }
        });
        mInstrumentation.waitForIdleSync();
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setDividerHeight",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getDividerHeight",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getDivider",
            args = {}
        )
    })
    public void testAccessDividerHeight() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        Drawable d = mListView.getDivider();
        Rect r = d.getBounds();
        assertTrue(r.bottom - r.top > 0);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setDividerHeight(20);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(20, mListView.getDividerHeight());
        assertEquals(20, r.bottom - r.top);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setDividerHeight(10);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(10, mListView.getDividerHeight());
        assertEquals(10, r.bottom - r.top);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            method = "setItemsCanFocus",
            args = {boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            method = "getItemsCanFocus",
            args = {}
        )
    })
    public void testAccessItemsCanFocus() {
        mListView.setItemsCanFocus(true);
        assertTrue(mListView.getItemsCanFocus());

        mListView.setItemsCanFocus(false);
        assertFalse(mListView.getItemsCanFocus());

        // TODO: how to check?
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setAdapter",
            args = {android.widget.ListAdapter.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getAdapter",
            args = {}
        )
    })
    public void testAccessAdapter() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        assertSame(mAdapter_countries, mListView.getAdapter());
        assertEquals(mCountryList.length, mListView.getCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_names);
            }
        });
        mInstrumentation.waitForIdleSync();

        assertSame(mAdapter_names, mListView.getAdapter());
        assertEquals(mNameList.length, mListView.getCount());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setItemChecked",
            args = {int.class, boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setChoiceMode",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getChoiceMode",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getCheckedItemPosition",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "isItemChecked",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getCheckedItemPositions",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "clearChoices",
            args = {}
        )
    })
    @UiThreadTest
    @ToBeFixed(bug="2031502", explanation="setItemChecked(i,false) always unchecks all items")
    public void testAccessItemChecked() {
        // NONE mode
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        assertEquals(ListView.CHOICE_MODE_NONE, mListView.getChoiceMode());

        mListView.setItemChecked(1, true);
        assertEquals(ListView.INVALID_POSITION, mListView.getCheckedItemPosition());
        assertFalse(mListView.isItemChecked(1));

        // SINGLE mode
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        assertEquals(ListView.CHOICE_MODE_SINGLE, mListView.getChoiceMode());

        mListView.setItemChecked(2, true);
        assertEquals(2, mListView.getCheckedItemPosition());
        assertTrue(mListView.isItemChecked(2));

        mListView.setItemChecked(3, true);
        assertEquals(3, mListView.getCheckedItemPosition());
        assertTrue(mListView.isItemChecked(3));
        assertFalse(mListView.isItemChecked(2));

        // test attempt to uncheck a item that wasn't checked to begin with
        mListView.setItemChecked(4, false);
        // item three should still be checked
        assertEquals(3, mListView.getCheckedItemPosition());
        assertFalse(mListView.isItemChecked(4));
        assertTrue(mListView.isItemChecked(3));
        assertFalse(mListView.isItemChecked(2));

        mListView.setItemChecked(4, true);
        assertTrue(mListView.isItemChecked(4));
        mListView.clearChoices();
        assertEquals(ListView.INVALID_POSITION, mListView.getCheckedItemPosition());
        assertFalse(mListView.isItemChecked(4));

        // MULTIPLE mode
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        assertEquals(ListView.CHOICE_MODE_MULTIPLE, mListView.getChoiceMode());

        mListView.setItemChecked(1, true);
        assertEquals(ListView.INVALID_POSITION, mListView.getCheckedItemPosition());
        SparseBooleanArray array = mListView.getCheckedItemPositions();
        assertTrue(array.get(1));
        assertFalse(array.get(2));
        assertTrue(mListView.isItemChecked(1));
        assertFalse(mListView.isItemChecked(2));

        mListView.setItemChecked(2, true);
        mListView.setItemChecked(3, false);
        mListView.setItemChecked(4, true);

        assertTrue(array.get(1));
        assertTrue(array.get(2));
        assertFalse(array.get(3));
        assertTrue(array.get(4));
        assertTrue(mListView.isItemChecked(1));
        assertTrue(mListView.isItemChecked(2));
        assertFalse(mListView.isItemChecked(3));
        assertTrue(mListView.isItemChecked(4));

        mListView.clearChoices();
        assertFalse(array.get(1));
        assertFalse(array.get(2));
        assertFalse(array.get(3));
        assertFalse(array.get(4));
        assertFalse(mListView.isItemChecked(1));
        assertFalse(mListView.isItemChecked(2));
        assertFalse(mListView.isItemChecked(3));
        assertFalse(mListView.isItemChecked(4));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setFooterDividersEnabled",
            args = {boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addFooterView",
            args = {android.view.View.class, java.lang.Object.class, boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addFooterView",
            args = {android.view.View.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getFooterViewsCount",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "removeFooterView",
            args = {android.view.View.class}
        )
    })
    public void testAccessFooterView() {
        final TextView footerView1 = new TextView(mActivity);
        footerView1.setText("footerview1");
        final TextView footerView2 = new TextView(mActivity);
        footerView2.setText("footerview2");

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setFooterDividersEnabled(true);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(0, mListView.getFooterViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.addFooterView(footerView1, null, true);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(1, mListView.getFooterViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.addFooterView(footerView2);
            }
        });

        mInstrumentation.waitForIdleSync();
        assertEquals(2, mListView.getFooterViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.removeFooterView(footerView1);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(1, mListView.getFooterViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.removeFooterView(footerView2);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(0, mListView.getFooterViewsCount());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setHeaderDividersEnabled",
            args = {boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addHeaderView",
            args = {android.view.View.class, java.lang.Object.class, boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addHeaderView",
            args = {android.view.View.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getHeaderViewsCount",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "removeHeaderView",
            args = {android.view.View.class}
        )
    })
    @ToBeFixed(bug = "", explanation = "After add two header views, the setAdapter will fail, " +
            "and throws out an java.lang.ClassCastException.")
    public void testAccessHeaderView() {
        final TextView headerView1 = (TextView) mActivity.findViewById(R.id.headerview1);
        final TextView headerView2 = (TextView) mActivity.findViewById(R.id.headerview2);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setHeaderDividersEnabled(true);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(0, mListView.getHeaderViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.addHeaderView(headerView2, null, true);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(1, mListView.getHeaderViewsCount());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.addHeaderView(headerView1);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(2, mListView.getHeaderViewsCount());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setDivider",
            args = {android.graphics.drawable.Drawable.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getDivider",
            args = {}
        )
    })
    public void testAccessDivider() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        Drawable defaultDrawable = mListView.getDivider();
        Rect r = defaultDrawable.getBounds();
        assertTrue(r.bottom - r.top > 0);

        final Drawable d = mActivity.getResources().getDrawable(R.drawable.scenery);
        r = d.getBounds();
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setDivider(d);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertSame(d, mListView.getDivider());
        assertEquals(r.bottom - r.top, mListView.getDividerHeight());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setDividerHeight(10);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(10, mListView.getDividerHeight());
        assertEquals(10, r.bottom - r.top);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setSelection",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setSelectionFromTop",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setSelectionAfterHeaderView",
            args = {}
        )
    })
    public void testSetSelection() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setSelection(1);
            }
        });
        mInstrumentation.waitForIdleSync();
        String item = (String) mListView.getSelectedItem();
        assertEquals(mCountryList[1], item);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setSelectionFromTop(5, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        item = (String) mListView.getSelectedItem();
        assertEquals(mCountryList[5], item);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setSelectionAfterHeaderView();
            }
        });
        mInstrumentation.waitForIdleSync();
        item = (String) mListView.getSelectedItem();
        assertEquals(mCountryList[0], item);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.NOT_NECESSARY,
            method = "onKeyDown",
            args = {int.class, android.view.KeyEvent.class}
        ),
        @TestTargetNew(
            level = TestLevel.NOT_NECESSARY,
            method = "onKeyUp",
            args = {int.class, android.view.KeyEvent.class}
        ),
        @TestTargetNew(
            level = TestLevel.NOT_NECESSARY,
            method = "onKeyMultiple",
            args = {int.class, int.class, android.view.KeyEvent.class}
        )
    })
    public void testOnKeyUpDown() {
        // implementation details, do NOT test
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "performItemClick",
        args = {android.view.View.class, int.class, long.class}
    )
    public void testPerformItemClick() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setSelection(2);
            }
        });
        mInstrumentation.waitForIdleSync();

        final TextView child = (TextView) mAdapter_countries.getView(2, null, mListView);
        assertNotNull(child);
        assertEquals(mCountryList[2], child.getText().toString());
        final long itemID = mAdapter_countries.getItemId(2);
        assertEquals(2, itemID);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.performItemClick(child, 2, itemID);
            }
        });
        mInstrumentation.waitForIdleSync();

        MockOnItemClickListener onClickListener = new MockOnItemClickListener();
        mListView.setOnItemClickListener(onClickListener);

        assertNull(onClickListener.getView());
        assertEquals(0, onClickListener.getPosition());
        assertEquals(0, onClickListener.getID());

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.performItemClick(child, 2, itemID);
            }
        });
        mInstrumentation.waitForIdleSync();

        assertSame(child, onClickListener.getView());
        assertEquals(2, onClickListener.getPosition());
        assertEquals(2, onClickListener.getID());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.NOT_NECESSARY,
            method = "onRestoreInstanceState",
            args = {android.os.Parcelable.class}
        ),
        @TestTargetNew(
            level = TestLevel.NOT_NECESSARY,
            method = "onSaveInstanceState",
            args = {}
        )
    })
    public void testSaveAndRestoreInstanceState() {
        // implementation details, do NOT test
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "dispatchKeyEvent",
        args = {android.view.KeyEvent.class}
    )
    public void testDispatchKeyEvent() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setSelection(1);
            }
        });
        mInstrumentation.waitForIdleSync();
        String item = (String) mListView.getSelectedItem();
        assertEquals(mCountryList[1], item);

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A);
                mListView.dispatchKeyEvent(keyEvent);
            }
        });
        mInstrumentation.waitForIdleSync();

        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
                mListView.dispatchKeyEvent(keyEvent);
                mListView.dispatchKeyEvent(keyEvent);
                mListView.dispatchKeyEvent(keyEvent);
            }
        });
        mInstrumentation.waitForIdleSync();
        item = (String)mListView.getSelectedItem();
        assertEquals(mCountryList[4], item);
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL,
        method = "requestChildRectangleOnScreen",
        args = {android.view.View.class, android.graphics.Rect.class, boolean.class}
    )
    public void testRequestChildRectangleOnScreen() {
        mInstrumentation.runOnMainSync(new Runnable() {
            public void run() {
                mListView.setAdapter(mAdapter_countries);
            }
        });
        mInstrumentation.waitForIdleSync();

        TextView child = (TextView) mAdapter_countries.getView(0, null, mListView);
        assertNotNull(child);
        assertEquals(mCountryList[0], child.getText().toString());

        Rect rect = new Rect(0, 0, 10, 10);
        assertFalse(mListView.requestChildRectangleOnScreen(child, rect, false));

        // TODO: how to check?
    }

    @TestTargetNew(
        level = TestLevel.NOT_NECESSARY,
        method = "onTouchEvent",
        args = {android.view.MotionEvent.class}
    )
    public void testOnTouchEvent() {
        // implementation details, do NOT test
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "canAnimate",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setAdapter",
            args = {android.widget.ListAdapter.class}
        )
    })
    @UiThreadTest
    public void testCanAnimate() {
        MyListView listView = new MyListView(mActivity, mAttributeSet);

        assertFalse(listView.canAnimate());
        listView.setAdapter(mAdapter_countries);
        assertFalse(listView.canAnimate());

        LayoutAnimationController controller = new LayoutAnimationController(
                mActivity, mAttributeSet);
        listView.setLayoutAnimation(controller);

        assertTrue(listView.canAnimate());
    }

    @TestTargetNew(
        level = TestLevel.NOT_NECESSARY,
        method = "dispatchDraw",
        args = {android.graphics.Canvas.class}
    )
    @UiThreadTest
    public void testDispatchDraw() {
        // implementation details, do NOT test
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "findViewTraversal",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addHeaderView",
            args = {android.view.View.class}
        )
    })
    @UiThreadTest
    public void testFindViewTraversal() {
        MyListView listView = new MyListView(mActivity, mAttributeSet);
        TextView headerView = (TextView) mActivity.findViewById(R.id.headerview1);

        assertNull(listView.findViewTraversal(R.id.headerview1));

        listView.addHeaderView(headerView);
        assertNotNull(listView.findViewTraversal(R.id.headerview1));
        assertSame(headerView, listView.findViewTraversal(R.id.headerview1));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "findViewWithTagTraversal",
            args = {java.lang.Object.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "addHeaderView",
            args = {android.view.View.class}
        )
    })
    @UiThreadTest
    public void testFindViewWithTagTraversal() {
        MyListView listView = new MyListView(mActivity, mAttributeSet);
        TextView headerView = (TextView) mActivity.findViewById(R.id.headerview1);

        assertNull(listView.findViewWithTagTraversal("header"));

        headerView.setTag("header");
        listView.addHeaderView(headerView);
        assertNotNull(listView.findViewWithTagTraversal("header"));
        assertSame(headerView, listView.findViewWithTagTraversal("header"));
    }

    @TestTargetNew(
        level = TestLevel.TODO,
        method = "layoutChildren",
        args = {}
    )
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete")
    public void testLayoutChildren() {
        // TODO: how to test?
    }

    @TestTargetNew(
        level = TestLevel.NOT_NECESSARY,
        method = "onFinishInflate",
        args = {}
    )
    public void testOnFinishInflate() {
        // implementation details, do NOT test
    }

    @TestTargetNew(
        level = TestLevel.NOT_NECESSARY,
        method = "onFocusChanged",
        args = {boolean.class, int.class, android.graphics.Rect.class}
    )
    public void testOnFocusChanged() {
        // implementation details, do NOT test
    }

    @TestTargetNew(
        level = TestLevel.NOT_NECESSARY,
        method = "onMeasure",
        args = {int.class, int.class}
    )
    public void testOnMeasure() {
        // implementation details, do NOT test
    }

    /**
     * MyListView for test
     */
    private static class MyListView extends ListView {
        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean canAnimate() {
            return super.canAnimate();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
        }

        @Override
        protected View findViewTraversal(int id) {
            return super.findViewTraversal(id);
        }

        @Override
        protected View findViewWithTagTraversal(Object tag) {
            return super.findViewWithTagTraversal(tag);
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
        }
    }

    private static class MockOnItemClickListener implements OnItemClickListener {
        private View mView;
        private int mPosition;
        private long mID;

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mView = view;
            mPosition = position;
            mID = id;
        }

        public View getView() {
            return mView;
        }

        public int getPosition() {
            return mPosition;
        }

        public long getID() {
            return mID;
        }
    }

    /**
     * The following functions are merged from frameworktest.
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "layoutChildren",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setAdapter",
            args = {android.widget.ListAdapter.class}
        )
    })
    @MediumTest
    public void testRequestLayout() throws Exception {
        ListView listView = new ListView(mActivity);
        List<String> items = Lists.newArrayList("hello");
        Adapter<String> adapter = new Adapter<String>(mActivity, 0, items);
        listView.setAdapter(adapter);

        int measureSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);

        adapter.notifyDataSetChanged();
        listView.measure(measureSpec, measureSpec);
        listView.layout(0, 0, 100, 100);

        MockView childView = (MockView) listView.getChildAt(0);

        childView.requestLayout();
        childView.onMeasureCalled = false;
        listView.measure(measureSpec, measureSpec);
        listView.layout(0, 0, 100, 100);
        Assert.assertTrue(childView.onMeasureCalled);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setSelection",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setAdapter",
            args = {android.widget.ListAdapter.class}
        )
    })
    @MediumTest
    public void testNoSelectableItems() throws Exception {
        ListView listView = new ListView(mActivity);
        // We use a header as the unselectable item to remain after the selectable one is removed.
        listView.addHeaderView(new View(mActivity), null, false);
        List<String> items = Lists.newArrayList("hello");
        Adapter<String> adapter = new Adapter<String>(mActivity, 0, items);
        listView.setAdapter(adapter);

        listView.setSelection(1);

        int measureSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);

        adapter.notifyDataSetChanged();
        listView.measure(measureSpec, measureSpec);
        listView.layout(0, 0, 100, 100);

        items.remove(0);

        adapter.notifyDataSetChanged();
        listView.measure(measureSpec, measureSpec);
        listView.layout(0, 0, 100, 100);
    }

    private class MockView extends View {

        public boolean onMeasureCalled = false;

        public MockView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            onMeasureCalled = true;
        }
    }

    private class Adapter<T> extends ArrayAdapter<T> {

        public Adapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new MockView(getContext());
        }
    }
}
