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

package android.text.method.cts;

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.ToBeFixed;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.text.InputType;
import android.text.method.DateTimeKeyListener;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Test {@link DateTimeKeyListener}.
 */
@TestTargetClass(DateTimeKeyListener.class)
public class DateTimeKeyListenerTest extends
        ActivityInstrumentationTestCase2<KeyListenerStubActivity> {
    private Activity mActivity;
    private Instrumentation mInstrumentation;
    private TextView mTextView;

    public DateTimeKeyListenerTest(){
        super("com.android.cts.stub", KeyListenerStubActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mInstrumentation = getInstrumentation();
        mTextView = (TextView) mActivity.findViewById(R.id.keylistener_textview);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "DateTimeKeyListener",
        args = {}
    )
    public void testConstructor() {
        new DateTimeKeyListener();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getInstance",
        args = {}
    )
    public void testGetInstance() {
        DateTimeKeyListener listener1 = DateTimeKeyListener.getInstance();
        DateTimeKeyListener listener2 = DateTimeKeyListener.getInstance();

        assertNotNull(listener1);
        assertNotNull(listener2);
        assertSame(listener1, listener2);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getAcceptedChars",
        args = {}
    )
    public void testGetAcceptedChars() {
        MyDateTimeKeyListener dataTimeKeyListener = new MyDateTimeKeyListener();

        TextMethodUtils.assertEquals(DateTimeKeyListener.CHARACTERS,
                dataTimeKeyListener.getAcceptedChars());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getInputType",
        args = {}
    )
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete.")
    public void testGetInputType() {
        DateTimeKeyListener listener = DateTimeKeyListener.getInstance();

        int expected = InputType.TYPE_CLASS_DATETIME
                | InputType.TYPE_DATETIME_VARIATION_NORMAL;
        assertEquals(expected, listener.getInputType());
    }

    /**
     * Scenario description:
     * 1. Press '1' key and check if the content of TextView becomes "1"
     * 2. Press '2' key and check if the content of TextView becomes "12"
     * 3. Press 'a' key and check if the content of TextView becomes "12a"
     * 4. Press an unaccepted key if it exists. and this key will not be accepted.
     * 5. remove DateKeyListener and Press '1' key, this key will not be accepted
     */
    public void testDateTimeKeyListener() {
        final DateTimeKeyListener dateTimeKeyListener = DateTimeKeyListener.getInstance();

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mTextView.setKeyListener(dateTimeKeyListener);
                mTextView.requestFocus();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals("", mTextView.getText().toString());

        // press '1' key.
        mInstrumentation.sendStringSync("1");
        assertEquals("1", mTextView.getText().toString());

        // press '2' key.
        mInstrumentation.sendStringSync("2");
        assertEquals("12", mTextView.getText().toString());

        // press 'a' key.
        mInstrumentation.sendStringSync("a");
        assertEquals("12a", mTextView.getText().toString());

        // press an unaccepted key if it exists.
        int keyCode = TextMethodUtils.getUnacceptedKeyCode(DateTimeKeyListener.CHARACTERS);
        if (-1 != keyCode) {
            sendKeys(keyCode);
            assertEquals("12a", mTextView.getText().toString());
        }

        // remove DateTimeKeyListener
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mTextView.setKeyListener(null);
                mTextView.requestFocus();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals("12a", mTextView.getText().toString());

        mInstrumentation.sendStringSync("1");
        assertEquals("12a", mTextView.getText().toString());
    }

    private class MyDateTimeKeyListener extends DateTimeKeyListener {
        @Override
        protected char[] getAcceptedChars() {
            return super.getAcceptedChars();
        }
    }
}
