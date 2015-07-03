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

package android.graphics.drawable.cts;

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.ToBeFixed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import android.view.Gravity;

import java.io.IOException;

@TestTargetClass(android.graphics.drawable.ClipDrawable.class)
public class ClipDrawableTest extends AndroidTestCase {
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test Constructor",
        method = "ClipDrawable",
        args = {android.graphics.drawable.Drawable.class, int.class, int.class}
    )
    @SuppressWarnings("deprecation")
    public void testClipDrawable() {
        new ClipDrawable((Drawable) null, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);

        BitmapDrawable bmpDrawable = new BitmapDrawable();
        new ClipDrawable(bmpDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test draw(Canvas)",
        method = "draw",
        args = {android.graphics.Canvas.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "It will be tested by functional test, " +
            "and NPE is not expected.")
    public void testDraw() {
        MockDrawable mockDrawable = new MockDrawable();
        mockDrawable.setLevel(5000);
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        clipDrawable.setBounds(new Rect(0, 0, 100, 100));
        clipDrawable.setLevel(5000);
        assertFalse(mockDrawable.getCalledDraw());
        clipDrawable.draw(new Canvas());
        assertTrue(mockDrawable.getCalledDraw());

        try {
            clipDrawable.draw(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getChangingConfigurations()",
        method = "getChangingConfigurations",
        args = {}
    )
    public void testGetChangingConfigurations() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(0, clipDrawable.getChangingConfigurations());

        clipDrawable.setChangingConfigurations(1);
        assertEquals(1, clipDrawable.getChangingConfigurations());

        mockDrawable.setChangingConfigurations(2);
        clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        clipDrawable.setChangingConfigurations(1);
        assertEquals(3, clipDrawable.getChangingConfigurations());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getConstantState()",
        method = "getConstantState",
        args = {}
    )
    public void testGetConstantState() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertNull(clipDrawable.getConstantState());

        mockDrawable.setConstantState(new MockConstantState());
        clipDrawable = new ClipDrawable(mockDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        clipDrawable.setChangingConfigurations(1);
        assertNotNull(clipDrawable.getConstantState());
        assertEquals(1, clipDrawable.getConstantState().getChangingConfigurations());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getIntrinsicHeight()",
        method = "getIntrinsicHeight",
        args = {}
    )
    @SuppressWarnings("deprecation")
    public void testGetIntrinsicHeight() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(-1, clipDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(100, 50, Config.RGB_565);
        BitmapDrawable bmpDrawable = new BitmapDrawable(bitmap);
        bmpDrawable.setTargetDensity(bitmap.getDensity()); // avoid scaling
        clipDrawable = new ClipDrawable(bmpDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(50, clipDrawable.getIntrinsicHeight());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getIntrinsicWidth()",
        method = "getIntrinsicWidth",
        args = {}
    )
    @SuppressWarnings("deprecation")
    public void testGetIntrinsicWidth() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(-1, clipDrawable.getIntrinsicWidth());

        Bitmap bitmap = Bitmap.createBitmap(100, 50, Config.RGB_565);
        BitmapDrawable bmpDrawable = new BitmapDrawable(bitmap);
        bmpDrawable.setTargetDensity(bitmap.getDensity()); // avoid scaling
        clipDrawable = new ClipDrawable(bmpDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(100, clipDrawable.getIntrinsicWidth());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getOpacity()",
        method = "getOpacity",
        args = {}
    )
    @SuppressWarnings("deprecation")
    public void testGetOpacity() {
        BitmapDrawable bmpDrawable =
            new BitmapDrawable(Bitmap.createBitmap(100, 50, Config.RGB_565));
        ClipDrawable clipDrawable = new ClipDrawable(bmpDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(PixelFormat.OPAQUE, clipDrawable.getOpacity());

        bmpDrawable = new BitmapDrawable(Bitmap.createBitmap(100, 50, Config.RGB_565));
        bmpDrawable.setGravity(Gravity.CENTER);
        clipDrawable = new ClipDrawable(bmpDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(PixelFormat.TRANSLUCENT, clipDrawable.getOpacity());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getPadding(Rect)",
        method = "getPadding",
        args = {android.graphics.Rect.class}
    )
    @ToBeFixed(bug = "1417734", explanation = "NPE is not expected.")
    public void testGetPadding() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        Rect padding = new Rect(10, 10, 100, 100);
        assertFalse(clipDrawable.getPadding(padding));
        assertEquals(0, padding.left);
        assertEquals(0, padding.top);
        assertEquals(0, padding.bottom);
        assertEquals(0, padding.right);

        try {
            clipDrawable.getPadding(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test inflate(Resources, XmlPullParser, AttributeSet)",
        method = "inflate",
        args = {android.content.res.Resources.class, org.xmlpull.v1.XmlPullParser.class,
                android.util.AttributeSet.class}
    )
    @SuppressWarnings("deprecation")
    public void testInflate() throws XmlPullParserException, IOException {
        BitmapDrawable bmpDrawable = new BitmapDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(bmpDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);

        XmlPullParser parser = mContext.getResources().getXml(R.drawable.gradientdrawable);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        clipDrawable.inflate(mContext.getResources(), parser, attrs);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test invalidateDrawable(Drawable)",
        method = "invalidateDrawable",
        args = {android.graphics.drawable.Drawable.class}
    )
    public void testInvalidateDrawable() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        MockCallback callback = new MockCallback();
        clipDrawable.setCallback(callback);
        clipDrawable.invalidateDrawable(mockDrawable);
        assertSame(clipDrawable, callback.getInvalidateDrawable());

        clipDrawable.invalidateDrawable(null);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test isStateful()",
        method = "isStateful",
        args = {}
    )
    @SuppressWarnings("deprecation")
    public void testIsStateful() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertFalse(clipDrawable.isStateful());

        BitmapDrawable bmpDrawable =
                new BitmapDrawable(Bitmap.createBitmap(100, 50, Config.RGB_565));
        clipDrawable = new ClipDrawable(bmpDrawable, Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertFalse(clipDrawable.isStateful());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onBoundsChange(Rect)",
        method = "onBoundsChange",
        args = {android.graphics.Rect.class}
    )
    @ToBeFixed(bug = "1417734", explanation = "NPE is not expected.")
    public void testOnBoundsChange() {
        MockDrawable mockDrawable = new MockDrawable();
        MockClipDrawable mockClipDrawable = new MockClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(0, mockDrawable.getBounds().left);
        assertEquals(0, mockDrawable.getBounds().top);
        assertEquals(0, mockDrawable.getBounds().bottom);
        assertEquals(0, mockDrawable.getBounds().right);
        mockClipDrawable.onBoundsChange(new Rect(10, 10, 100, 100));
        assertEquals(10, mockDrawable.getBounds().left);
        assertEquals(10, mockDrawable.getBounds().top);
        assertEquals(100, mockDrawable.getBounds().bottom);
        assertEquals(100, mockDrawable.getBounds().right);

        try {
            mockClipDrawable.onBoundsChange(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onLevelChange(int)",
        method = "onLevelChange",
        args = {int.class}
    )
    public void testOnLevelChange() {
        MockDrawable mockDrawable = new MockDrawable();
        MockClipDrawable mockClipDrawable = new MockClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        MockCallback callback = new MockCallback();
        mockClipDrawable.setCallback(callback);

        assertEquals(0, mockDrawable.getLevel());
        mockClipDrawable.onLevelChange(1000);
        assertEquals(1000, mockDrawable.getLevel());
        assertSame(mockClipDrawable, callback.getInvalidateDrawable());

        mockClipDrawable.onLevelChange(0);
        assertEquals(0, mockDrawable.getLevel());

        mockClipDrawable.onLevelChange(10000);
        assertEquals(10000, mockDrawable.getLevel());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onStateChange(int[])",
        method = "onStateChange",
        args = {int[].class}
    )
    public void testOnStateChange() {
        MockDrawable mockDrawable = new MockDrawable();
        MockClipDrawable mockClipDrawable = new MockClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertEquals(StateSet.WILD_CARD, mockDrawable.getState());

        int[] states = new int[] {1, 2, 3};
        assertFalse(mockClipDrawable.onStateChange(states));
        assertEquals(states, mockDrawable.getState());

        mockClipDrawable.onStateChange(null);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test scheduleDrawable(Drawable, Runnable, long)",
        method = "scheduleDrawable",
        args = {android.graphics.drawable.Drawable.class, java.lang.Runnable.class, long.class}
    )
    public void testScheduleDrawable() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        MockCallback callback = new MockCallback();
        clipDrawable.setCallback(callback);
        clipDrawable.scheduleDrawable(mockDrawable, null, 1000L);
        assertEquals(clipDrawable, callback.getScheduleDrawable());
        assertNull(callback.getRunnable());
        assertEquals(1000L, callback.getWhen());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test setAlpha(int)",
        method = "setAlpha",
        args = {int.class}
    )
    public void testSetAlpha() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);

        clipDrawable.setAlpha(0);
        assertEquals(0, mockDrawable.getAlpha());

        clipDrawable.setAlpha(128);
        assertEquals(128, mockDrawable.getAlpha());

        clipDrawable.setAlpha(255);
        assertEquals(255, mockDrawable.getAlpha());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test setColorFilter(ColorFilter)",
        method = "setColorFilter",
        args = {android.graphics.ColorFilter.class}
    )
    public void testSetColorFilter() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);

        ColorFilter cf = new ColorFilter();
        clipDrawable.setColorFilter(cf);
        assertSame(cf, mockDrawable.getColorFilter());

        clipDrawable.setColorFilter(null);
        assertNull(mockDrawable.getColorFilter());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test setVisible(boolean, boolean)",
        method = "setVisible",
        args = {boolean.class, boolean.class}
    )
    public void testSetVisible() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        assertTrue(clipDrawable.isVisible());

        assertTrue(clipDrawable.setVisible(false, false));
        assertFalse(clipDrawable.isVisible());

        assertFalse(clipDrawable.setVisible(false, false));
        assertFalse(clipDrawable.isVisible());

        assertTrue(clipDrawable.setVisible(true, false));
        assertTrue(clipDrawable.isVisible());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test unscheduleDrawable(Drawable, Runnable)",
        method = "unscheduleDrawable",
        args = {android.graphics.drawable.Drawable.class, java.lang.Runnable.class}
    )
    public void testUnscheduleDrawable() {
        MockDrawable mockDrawable = new MockDrawable();
        ClipDrawable clipDrawable = new ClipDrawable(mockDrawable,
                Gravity.BOTTOM, ClipDrawable.HORIZONTAL);
        MockCallback callback = new MockCallback();
        clipDrawable.setCallback(callback);
        clipDrawable.unscheduleDrawable(mockDrawable, null);
        assertEquals(clipDrawable, callback.getScheduleDrawable());
        assertNull(callback.getRunnable());
    }

    private class MockClipDrawable extends ClipDrawable {
        public MockClipDrawable(Drawable drawable, int gravity, int orientation) {
            super(drawable, gravity, orientation);
        }

        @Override
        protected boolean onStateChange(int[] state) {
            return super.onStateChange(state);
        }

        @Override
        protected boolean onLevelChange(int level) {
            return super.onLevelChange(level);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }
    }

    private class MockDrawable extends Drawable {
        private ColorFilter mColorFilter;
        private ConstantState mConstantState;
        private boolean mCalledDraw = false;
        private int mAlpha;

        public boolean getCalledDraw() {
            return mCalledDraw;
        }

        public void draw(Canvas canvas) {
            mCalledDraw = true;
        }

        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        public int getAlpha() {
            return mAlpha;
        }

        public void setColorFilter(ColorFilter cf) {
            mColorFilter = cf;
        }

        public ColorFilter getColorFilter() {
            return mColorFilter;
        }

        public int getOpacity() {
            return 0;
        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }

        protected boolean onLevelChange(int level) {
            return super.onLevelChange(level);
        }

        protected boolean onStateChange(int[] state) {
            return super.onStateChange(state);
        }

        public ConstantState getConstantState() {
            return mConstantState;
        }

        public void setConstantState(ConstantState cs) {
            mConstantState = cs;
        }
    }

    private class MockConstantState extends ConstantState {
        public Drawable newDrawable() {
            return null;
        }

        public int getChangingConfigurations() {
            return 0;
        }
    }

    private class MockCallback implements Callback {
        private Drawable mInvalidateDrawable;
        private Drawable mScheduleDrawable;
        private Runnable mRunnable;
        private long mWhen;

        public Drawable getInvalidateDrawable() {
            return mInvalidateDrawable;
        }

        public Drawable getScheduleDrawable() {
            return mScheduleDrawable;
        }

        public Runnable getRunnable() {
            return mRunnable;
        }

        public long getWhen() {
            return mWhen;
        }

        public void invalidateDrawable(Drawable who) {
            mInvalidateDrawable = who;
        }

        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            mScheduleDrawable = who;
            mRunnable = what;
            mWhen = when;
        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
            mScheduleDrawable = who;
            mRunnable = what;
        }
    }
}
