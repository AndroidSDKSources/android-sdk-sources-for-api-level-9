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

package android.app.cts;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.drawable.Drawable;
import android.provider.Contacts.People;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

@TestTargetClass(Builder.class)
public class AlertDialog_BuilderTest extends ActivityInstrumentationTestCase2<DialogStubActivity> {
    private Builder mBuilder;
    private Context mContext;
    private Instrumentation mInstrumentation;
    private final CharSequence mTitle = "title";
    private Drawable mDrawable;
    private AlertDialog mDialog;
    private Button mButton;
    private boolean mResult;
    private boolean mItemSelected;
    private CharSequence mSelectedItem;
    private final String[] mPROJECTION = new String[] {
            People._ID, People.NAME
    };

    private View mView;
    private ListView mListView;
    private ArrayList<Integer> mSelectedItems;
    private FrameLayout mFrameLayout;

    private OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            mResult = true;
        }
    };

    private OnCancelListener mOnCancelListener = new OnCancelListener() {
        public void onCancel(DialogInterface dialog) {
            mResult = true;
        }
    };

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int key, KeyEvent envnt) {
            mResult = true;
            return true;
        }
    };

    private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            mItemSelected = true;
        }

        public void onNothingSelected(AdapterView parent) {
        }

    };

    private OnMultiChoiceClickListener mOnMultiChoiceClickListener =
        new OnMultiChoiceClickListener() {
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            mSelectedItems.add(which);
            mResult = true;
        }
    };

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mBuilder = null;
        mInstrumentation = getInstrumentation();
        mContext = getActivity();
        mButton = null;
        mView = null;
        mListView = null;
        mDialog = null;
        mItemSelected = false;
        mSelectedItem = null;
        mSelectedItems = new ArrayList<Integer>();
    }

    public AlertDialog_BuilderTest() {
        super("com.android.cts.stub", DialogStubActivity.class);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "AlertDialog.Builder",
        args = {Context.class}
    )
    public void testConstructor() {
        new AlertDialog.Builder(mContext);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setIcon",
        args = {int.class}
    )
    public void testSetIconWithParamInt() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mDrawable = mContext.getResources().getDrawable(android.R.drawable.btn_default);
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setIcon(android.R.drawable.btn_default);
                mDialog = mBuilder.show();
            }
        });
        mInstrumentation.waitForIdleSync();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setIcon",
        args = {Drawable.class}
    )
    public void testSetIconWithParamDrawable() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mDrawable = mContext.getResources().getDrawable(android.R.drawable.btn_default);
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setIcon(mDrawable);
                mDialog = mBuilder.show();
            }
        });
        mInstrumentation.waitForIdleSync();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setPositiveButton",
        args = {int.class, OnClickListener.class}
    )
    public void testSetPositiveButtonWithParamInt() throws Throwable {
       runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setPositiveButton(android.R.string.yes, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();

        assertEquals(mContext.getText(android.R.string.yes), mButton.getText());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setPositiveButton",
        args = {CharSequence.class, OnClickListener.class}
    )
    public void testSetPositiveButtonWithParamCharSequence() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setPositiveButton(android.R.string.yes, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(mContext.getText(android.R.string.yes), mButton.getText());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setNegativeButton",
        args = {CharSequence.class, OnClickListener.class}
    )
    public void testSetNegativeButtonWithParamCharSequence() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setNegativeButton(mTitle, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(mTitle, mButton.getText());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setNegativeButton",
        args = {int.class, OnClickListener.class}
    )
    public void testSetNegativeButtonWithParamInt() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setNegativeButton(com.android.cts.stub.R.string.notify, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(mContext.getText(com.android.cts.stub.R.string.notify), mButton.getText());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setNeutralButton",
        args = {int.class, OnClickListener.class}
    )
    public void testSetNeutralButtonWithParamInt() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setNeutralButton(com.android.cts.stub.R.string.notify, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(mContext.getText(com.android.cts.stub.R.string.notify), mButton.getText());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setNeutralButton",
        args = {CharSequence.class, OnClickListener.class}
    )
    public void testSetNeutralButtonWithParamCharSequence() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setNeutralButton(mTitle, mOnClickListener);
                mDialog = mBuilder.show();
                mButton = mDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                mButton.performClick();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(mTitle, mButton.getText());
        assertTrue(mResult);
    }

    private void setCancelable(final boolean cancelable) throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setCancelable(cancelable);
                mDialog = mBuilder.show();
            }
        });
        mInstrumentation.waitForIdleSync();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setCancelable",
        args = {boolean.class}
    )
    public void testSetCancelable() throws Throwable {
        setCancelable(true);
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        assertFalse(mDialog.isShowing());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setCancelable",
        args = {boolean.class}
    )
    public void testDisableCancelable() throws Throwable {
        setCancelable(false);
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        assertTrue(mDialog.isShowing());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setOnCancelListener",
        args = {OnCancelListener.class}
    )
    public void testSetOnCancelListener() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setOnCancelListener(mOnCancelListener);
                mDialog = mBuilder.show();
                mDialog.cancel();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setOnKeyListener",
        args = {OnKeyListener.class}
    )
    public void testSetOnKeyListener() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setOnKeyListener(mOnKeyListener);
                mDialog = mBuilder.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        sendKeys(KeyEvent.ACTION_DOWN, KeyEvent.ACTION_DOWN);
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setItems",
        args = {int.class, OnClickListener.class}
    )
    public void testSetItemsWithParamInt() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setItems(com.android.cts.stub.R.array.difficultyLevel, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
            }
        });
        mInstrumentation.waitForIdleSync();

        final CharSequence[] levels = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);
        assertEquals(levels[0], mListView.getItemAtPosition(0));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setItems",
        args = {CharSequence[].class, OnClickListener.class}
    )
    public void testSetItemsWithParamCharSequence() throws Throwable {
        final CharSequence[] expect = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setItems(expect, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(expect[0], mListView.getItemAtPosition(0));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setAdapter",
        args = {ListAdapter.class, OnClickListener.class}
    )
    public void testSetAdapter() throws Throwable {
        final ListAdapter adapter = new AdapterTest();
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setAdapter(adapter, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(adapter, mListView.getAdapter());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setCursor",
        args = {Cursor.class, OnClickListener.class, String.class}
    )
    public void testSetCursor() throws Throwable {
        preparePeople();
        final Cursor c = mContext.getContentResolver().query(People.CONTENT_URI, mPROJECTION, null,
                null, null);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setCursor(c, mOnClickListener, People.NAME);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mListView.performItemClick(null, 0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        final CursorWrapper selected = (CursorWrapper)mListView.getSelectedItem();
        assertEquals(c.getString(1), selected.getString(1));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setMultiChoiceItems",
        args = {int.class, boolean[].class, OnMultiChoiceClickListener.class}
    )
    public void testSetMultiChoiceItemsWithParamInt() throws Throwable {

        final CharSequence[] items = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setMultiChoiceItems(com.android.cts.stub.R.array.difficultyLevel, null,
                        mOnMultiChoiceClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mSelectedItem = (CharSequence)mListView.getSelectedItem();
                mListView.performItemClick(null, 0, 0);
                mListView.performItemClick(null, 1, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(items[0], mSelectedItem);
        assertEquals(2, mSelectedItems.size());
        assertEquals(items[0], mListView.getItemAtPosition(0));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setMultiChoiceItems",
        args = {CharSequence[].class, boolean[].class, OnMultiChoiceClickListener.class}
    )
    public void testSetMultiChoiceItemsWithParamCharSequence() throws Throwable {
        final CharSequence[] items = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setMultiChoiceItems(items, null, mOnMultiChoiceClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mSelectedItem = (CharSequence)mListView.getSelectedItem();
                mListView.performItemClick(null, 0, 0);
                mListView.performItemClick(null, 1, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(items[0], mSelectedItem);
        assertEquals(2, mSelectedItems.size());
        assertEquals(items[0], mListView.getItemAtPosition(0));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setMultiChoiceItems",
        args = {Cursor.class, String.class, String.class, OnMultiChoiceClickListener.class}
    )
    public void testSetMultiChoiceItemsWithParamCursor() throws Throwable {
        preparePeople();
        final Cursor c = mContext.getContentResolver().query(People.CONTENT_URI, mPROJECTION, null,
                null, null);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setMultiChoiceItems(c, People.NAME, People.NAME,
                        mOnMultiChoiceClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mListView.performItemClick(null, 0, 0);
                mListView.performItemClick(null, 1, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        final CursorWrapper selected = (CursorWrapper)mListView.getSelectedItem();
        assertEquals(c.getString(1), selected.getString(1));
        assertEquals(2, mSelectedItems.size());
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setSingleChoiceItems",
        args = {int.class, int.class, OnClickListener.class}
    )
    public void testSetSingleChoiceItemsWithParamInt() throws Throwable {
        final CharSequence[] items = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setSingleChoiceItems(com.android.cts.stub.R.array.difficultyLevel, 0,
                        mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mSelectedItem = (CharSequence)mListView.getSelectedItem();
                mListView.performItemClick(null, 0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(items[0], mSelectedItem);
        assertEquals(items[0], mListView.getItemAtPosition(0));
        assertTrue(mResult);
    }

    private void preparePeople() {
        final ContentResolver mResolver = mContext.getContentResolver();
        mResolver.delete(People.CONTENT_URI, null, null);
        final ContentValues valuse = new ContentValues();
        valuse.put(People._ID, "1");
        valuse.put(People.NAME, "name");
        mResolver.insert(People.CONTENT_URI, valuse);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setSingleChoiceItems",
        args = {Cursor.class, int.class, String.class, OnClickListener.class}
    )
    public void testSetSingleChoiceItemsWithParamCursor() throws Throwable {
        final String[] PROJECTION = new String[] {
                People._ID, People.NAME
        };
        preparePeople();
        final Cursor c = mContext.getContentResolver().query(People.CONTENT_URI, PROJECTION, null,
                null, null);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setSingleChoiceItems(c, 0, People.NAME, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mListView.performItemClick(null, 0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        final CursorWrapper selected = (CursorWrapper)mListView.getSelectedItem();
        assertEquals(c.getString(1), selected.getString(1));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setSingleChoiceItems",
        args = {CharSequence[].class, int.class, OnClickListener.class}
    )
    public void testSetSingleChoiceItemsWithParamCharSequence() throws Throwable {
        final CharSequence[] items = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setSingleChoiceItems(items, 0, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mSelectedItem = (CharSequence)mListView.getSelectedItem();
                mListView.performItemClick(null, 0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(items[0], mSelectedItem);
        assertEquals(items[0], mListView.getItemAtPosition(0));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setSingleChoiceItems",
        args = {ListAdapter.class, int.class, OnClickListener.class}
    )
    public void testSetSingleChoiceItems() throws Throwable {
        final CharSequence[] items = mContext.getResources().getTextArray(
                com.android.cts.stub.R.array.difficultyLevel);

        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setSingleChoiceItems(new ArrayAdapter<CharSequence>(mContext,
                        android.R.layout.select_dialog_singlechoice, android.R.id.text1, items), 0,
                        mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mSelectedItem = (CharSequence)mListView.getSelectedItem();
                mListView.performItemClick(null, 0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(items[0], mSelectedItem);
        assertEquals(items[0], mListView.getItemAtPosition(0));
        assertTrue(mResult);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setOnItemSelectedListener",
        args = {AdapterView.OnItemSelectedListener.class}
    )
    public void testSetOnItemSelectedListener() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setOnItemSelectedListener(mOnItemSelectedListener);
                mBuilder.setItems(com.android.cts.stub.R.array.difficultyLevel, mOnClickListener);
                mDialog = mBuilder.show();
                mListView = mDialog.getListView();
                mListView.pointToPosition(0, 0);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertTrue(mItemSelected);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setView",
        args = {View.class}
    )
    public void testSetView() throws Throwable {
        final View view = new View(mContext);
        view.setId(100);
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setView(view);
                mDialog = mBuilder.show();
                mView = mDialog.getWindow().findViewById(100);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(view, mView);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setView",
        args = {View.class, int.class, int.class, int.class, int.class}
    )
    public void testSetViewCustom() throws Throwable {
        final int viewSpacingLeft = 10;
        final int viewSpacingTop = 20;
        final int viewSpacingRight = 30;
        final int viewSpacingBottom = 40;
        final View view = new View(mContext);
        view.setId(100);
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight,
                        viewSpacingBottom);
                mDialog = mBuilder.show();
                mView = mDialog.getWindow().findViewById(100);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(view, mView);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setInverseBackgroundForced",
        args = {boolean.class}
    )
    public void testSetInverseBackgroundForced() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setInverseBackgroundForced(true);
                mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        mInstrumentation.waitForIdleSync();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "create",
        args = {}
    )
    public void testCreate() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertNotNull(mDialog);
        assertTrue(mDialog.isShowing());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "show",
        args = {}
    )
    public void testShow() throws Throwable {
        runTestOnUiThread(new Runnable() {
            public void run() {
                mBuilder = new AlertDialog.Builder(mContext);
                mDialog = mBuilder.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertTrue(mDialog.isShowing());
    }

    private static class AdapterTest implements android.widget.ListAdapter {
        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int position) {
            return false;
        }

        public int getCount() {
            return 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public android.view.View getView( int position,
                                          android.view.View convertView,
                                          android.view.ViewGroup parent){
            return null;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return true;
        }

        public void registerDataSetObserver(
            android.database.DataSetObserver observer) {
        }

        public void unregisterDataSetObserver(
            android.database.DataSetObserver observer) {
        }
    }
}
