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

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.cts.DelayedCheck;
import android.widget.ImageView;
import android.widget.Toast;

@TestTargetClass(android.widget.Toast.class)
public class ToastTest extends ActivityInstrumentationTestCase2<StubActivity> {
    private static final String TEST_TOAST_TEXT = "test toast";
    private static final long TIME_FOR_UI_OPERATION  = 1000L;
    private static final long TIME_OUT = 5000L;
    private Toast mToast;
    private Activity mActivity;
    private Instrumentation mInstrumentation;

    public ToastTest() {
        super("com.android.cts.stub", StubActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mInstrumentation = getInstrumentation();
        mToast = new Toast(mActivity);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "Toast",
        args = {android.content.Context.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc of " +
            "Toast constructor when the input Context is null")
    public void testConstructor() {
        new Toast(mActivity);

        try {
            new Toast(null);
            fail("did not throw NullPointerException when context is null.");
        } catch (NullPointerException e) {
            // expected, test success
        }
    }

    private void assertShowToast(final View view) {
        new DelayedCheck(TIME_OUT) {
            @Override
            protected boolean check() {
                return null != view.getParent();
            }
        }.run();
    }

    private void assertShowAndHide(final View view) {
        assertShowToast(view);
        new DelayedCheck(TIME_OUT) {
            @Override
            protected boolean check() {
                return null == view.getParent();
            }
        }.run();
    }

    private void assertNotShowToast(final View view) throws InterruptedException {
        // sleep a while and then make sure do not show toast
        Thread.sleep(TIME_FOR_UI_OPERATION);
        assertNull(view.getParent());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "show",
        args = {}
    )
    public void testShow() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_LONG);
            }
        });
        mInstrumentation.waitForIdleSync();

        final View view = mToast.getView();

        // view has not been attached to screen yet
        assertNull(view.getParent());
        assertEquals(View.VISIBLE, view.getVisibility());

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();

        // view will be attached to screen when show it
        assertEquals(View.VISIBLE, view.getVisibility());
        assertShowToast(view);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "show",
        args = {}
    )
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete. It can" +
            " not be shown if did not set any view.")
    @UiThreadTest
    public void testShowFailure() {
        // do not have any views.
        assertNull(mToast.getView());
        try {
            mToast.show();
            fail("did not throw RuntimeException when did not set any views.");
        } catch (RuntimeException e) {
            // expected, test success.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "cancel",
        args = {}
    )
    public void testCancel() throws InterruptedException {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_LONG);
            }
        });
        mInstrumentation.waitForIdleSync();

        final View view = mToast.getView();

        // view has not been attached to screen yet
        assertNull(view.getParent());
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.show();
                mToast.cancel();
            }
        });
        mInstrumentation.waitForIdleSync();

        assertNotShowToast(view);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setView",
            args = {android.view.View.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getView",
            args = {}
        )
    })
    public void testAccessView() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_LONG);
            }
        });
        mInstrumentation.waitForIdleSync();
        assertFalse(mToast.getView() instanceof ImageView);

        final ImageView imageView = new ImageView(mActivity);
        Drawable drawable = mActivity.getResources().getDrawable(R.drawable.pass);
        imageView.setImageDrawable(drawable);

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setView(imageView);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertSame(imageView, mToast.getView());
        assertShowAndHide(imageView);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setDuration",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getDuration",
            args = {}
        )
    })
    public void testAccessDuration() {
        long start = SystemClock.uptimeMillis();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_LONG);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(Toast.LENGTH_LONG, mToast.getDuration());

        View view = mToast.getView();
        assertShowAndHide(view);
        long longDuration = SystemClock.uptimeMillis() - start;

        start = SystemClock.uptimeMillis();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertEquals(Toast.LENGTH_SHORT, mToast.getDuration());

        view = mToast.getView();
        assertShowAndHide(view);
        long shortDuration = SystemClock.uptimeMillis() - start;

        assertTrue(longDuration > shortDuration);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setMargin",
            args = {float.class, float.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getHorizontalMargin",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getVerticalMargin",
            args = {}
        )
    })
    public void testAccessMargin() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_SHORT);
            }
        });
        mInstrumentation.waitForIdleSync();
        View view = mToast.getView();
        assertFalse(view.getLayoutParams() instanceof WindowManager.LayoutParams);

        final float horizontal1 = 1.0f;
        final float vertical1 = 1.0f;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setMargin(horizontal1, vertical1);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertShowToast(view);

        assertEquals(horizontal1, mToast.getHorizontalMargin());
        assertEquals(vertical1, mToast.getVerticalMargin());
        WindowManager.LayoutParams params1 = (WindowManager.LayoutParams) view.getLayoutParams();
        assertEquals(horizontal1, params1.horizontalMargin);
        assertEquals(vertical1, params1.verticalMargin);
        int[] xy1 = new int[2];
        view.getLocationOnScreen(xy1);
        assertShowAndHide(view);

        final float horizontal2 = 0.1f;
        final float vertical2 = 0.1f;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setMargin(horizontal2, vertical2);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        assertShowToast(view);

        assertEquals(horizontal2, mToast.getHorizontalMargin());
        assertEquals(vertical2, mToast.getVerticalMargin());
        WindowManager.LayoutParams params2 = (WindowManager.LayoutParams) view.getLayoutParams();
        assertEquals(horizontal2, params2.horizontalMargin);
        assertEquals(vertical2, params2.verticalMargin);

        int[] xy2 = new int[2];
        view.getLocationOnScreen(xy2);
        assertShowAndHide(view);

        assertTrue(xy1[0] > xy2[0]);
        assertTrue(xy1[1] < xy2[1]);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setGravity",
            args = {int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getGravity",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getXOffset",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getYOffset",
            args = {}
        )
    })
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete." +
            " It's not clear about how do xOffset and yOffset take effect.")
    public void testAccessGravity() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast = Toast.makeText(mActivity, TEST_TOAST_TEXT, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        View view = mToast.getView();
        assertShowToast(view);
        assertEquals(Gravity.CENTER, mToast.getGravity());
        assertEquals(0, mToast.getXOffset());
        assertEquals(0, mToast.getYOffset());
        int[] centerXY = new int[2];
        view.getLocationOnScreen(centerXY);
        assertShowAndHide(view);

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        view = mToast.getView();
        assertShowToast(view);
        assertEquals(Gravity.BOTTOM, mToast.getGravity());
        assertEquals(0, mToast.getXOffset());
        assertEquals(0, mToast.getYOffset());
        int[] bottomXY = new int[2];
        view.getLocationOnScreen(bottomXY);
        assertShowAndHide(view);

        // x coordinate is the same
        assertEquals(centerXY[0], bottomXY[0]);
        // bottom view is below of center view
        assertTrue(centerXY[1] < bottomXY[1]);

        final int xOffset = 20;
        final int yOffset = 10;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mToast.setGravity(Gravity.BOTTOM, xOffset, yOffset);
                mToast.show();
            }
        });
        mInstrumentation.waitForIdleSync();
        view = mToast.getView();
        assertShowToast(view);
        assertEquals(Gravity.BOTTOM, mToast.getGravity());
        assertEquals(xOffset, mToast.getXOffset());
        assertEquals(yOffset, mToast.getYOffset());
        int[] bottomOffsetXY = new int[2];
        view.getLocationOnScreen(bottomOffsetXY);
        assertShowAndHide(view);

        assertEquals(bottomXY[0] + xOffset, bottomOffsetXY[0]);
        assertEquals(bottomXY[1] - yOffset, bottomOffsetXY[1]);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "makeText",
        args = {android.content.Context.class, java.lang.CharSequence.class, int.class}
    )
    @UiThreadTest
    @ToBeFixed(bug = "1695243", explanation = "1. should add @throws clause into javadoc of " +
            "Toast#makeText(Context, CharSequence, int) when context is null. 2. javadoc " +
            "declares it just contains a text view, but actually it's a LinearLayout")
    public void testMakeText1() {
        mToast = Toast.makeText(mActivity, "android", Toast.LENGTH_SHORT);
        assertNotNull(mToast);
        assertEquals(Toast.LENGTH_SHORT, mToast.getDuration());
        View view = mToast.getView();
        assertNotNull(view);

        mToast = Toast.makeText(mActivity, "cts", Toast.LENGTH_LONG);
        assertNotNull(mToast);
        assertEquals(Toast.LENGTH_LONG, mToast.getDuration());
        view = mToast.getView();
        assertNotNull(view);

        mToast = Toast.makeText(mActivity, null, Toast.LENGTH_LONG);
        assertNotNull(mToast);
        assertEquals(Toast.LENGTH_LONG, mToast.getDuration());
        view = mToast.getView();
        assertNotNull(view);

        try {
            mToast = Toast.makeText(null, "test", Toast.LENGTH_LONG);
            fail("did not throw NullPointerException when context is null.");
        } catch (NullPointerException e) {
            //expected, test success.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "makeText",
        args = {android.content.Context.class, int.class, int.class}
    )
    @UiThreadTest
    @ToBeFixed(bug = "1695243", explanation = "1. should add @throws clause into javadoc of " +
            "Toast#makeText(Context, CharSequence, int) when context is null. 2. javadoc " +
            "declares it just contains a text view, but actually it's a LinearLayout")
    public void testMakeText2() {
        mToast = Toast.makeText(mActivity, R.string.hello_world, Toast.LENGTH_LONG);

        assertNotNull(mToast);
        assertEquals(Toast.LENGTH_LONG, mToast.getDuration());
        View view = mToast.getView();
        assertNotNull(view);

        mToast = Toast.makeText(mActivity, R.string.hello_android, Toast.LENGTH_SHORT);
        assertNotNull(mToast);
        assertEquals(Toast.LENGTH_SHORT, mToast.getDuration());
        view = mToast.getView();
        assertNotNull(view);

        try {
            mToast = Toast.makeText(null, R.string.hello_android, Toast.LENGTH_SHORT);
            fail("did not throw NullPointerException when context is null.");
        } catch (NullPointerException e) {
            //expected, test success.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setText",
        args = {int.class}
    )
    @UiThreadTest
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc of " +
            "Toast#setText(int) when set a negative resouce id")
    public void testSetText1() {
        mToast = Toast.makeText(mActivity, R.string.text, Toast.LENGTH_LONG);

        mToast.setText(R.string.hello_world);
        // TODO: how to getText to assert?

        mToast.setText(R.string.hello_android);
        // TODO: how to getText to assert?

        try {
            mToast.setText(-1);
            fail("did not throw RuntimeException when resource id is negative.");
        } catch (RuntimeException e) {
            //expected, test success.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setText",
        args = {java.lang.CharSequence.class}
    )
    @UiThreadTest
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc of " +
            "Toast#setText(CharSequence) when view is null")
    public void testSetText2() {
        mToast = Toast.makeText(mActivity, R.string.text, Toast.LENGTH_LONG);

        mToast.setText("cts");
        // TODO: how to getText to assert?

        mToast.setText("android");
        // TODO: how to getText to assert?

        try {
            mToast.setView(null);
            mToast.setText(null);
            fail("did not throw RuntimeException when view is null.");
        } catch (RuntimeException e) {
            //expected, test success.
        }
    }
}
