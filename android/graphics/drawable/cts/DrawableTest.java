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
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@TestTargetClass(Drawable.class)
public class DrawableTest extends AndroidTestCase {
    Resources mResources;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mResources = mContext.getResources();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "clearColorFilter",
        args = {}
    )
    public void testClearColorFilter() {
        MockDrawable mockDrawable = new MockDrawable();
        mockDrawable.clearColorFilter();
        assertNull(mockDrawable.getColorFilter());

        ColorFilter cf = new ColorFilter();
        mockDrawable.setColorFilter(cf);
        assertEquals(cf, mockDrawable.getColorFilter());

        mockDrawable.clearColorFilter();
        assertNull(mockDrawable.getColorFilter());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "copyBounds",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "copyBounds",
            args = {android.graphics.Rect.class}
        )
    })
    @ToBeFixed(bug = "1417734", explanation = "NPE is not expected.")
    public void testCopyBounds() {
        MockDrawable mockDrawable = new MockDrawable();
        Rect rect1 = mockDrawable.copyBounds();
        Rect r1 = new Rect();
        mockDrawable.copyBounds(r1);
        assertEquals(0, rect1.bottom);
        assertEquals(0, rect1.left);
        assertEquals(0, rect1.right);
        assertEquals(0, rect1.top);
        assertEquals(0, r1.bottom);
        assertEquals(0, r1.left);
        assertEquals(0, r1.right);
        assertEquals(0, r1.top);

        mockDrawable.setBounds(10, 10, 100, 100);
        Rect rect2 = mockDrawable.copyBounds();
        Rect r2 = new Rect();
        mockDrawable.copyBounds(r2);
        assertEquals(100, rect2.bottom);
        assertEquals(10, rect2.left);
        assertEquals(100, rect2.right);
        assertEquals(10, rect2.top);
        assertEquals(100, r2.bottom);
        assertEquals(10, r2.left);
        assertEquals(100, r2.right);
        assertEquals(10, r2.top);

        mockDrawable.setBounds(new Rect(50, 50, 500, 500));
        Rect rect3 = mockDrawable.copyBounds();
        Rect r3 = new Rect();
        mockDrawable.copyBounds(r3);
        assertEquals(500, rect3.bottom);
        assertEquals(50, rect3.left);
        assertEquals(500, rect3.right);
        assertEquals(50, rect3.top);
        assertEquals(500, r3.bottom);
        assertEquals(50, r3.left);
        assertEquals(500, r3.right);
        assertEquals(50, r3.top);

        try {
            mockDrawable.copyBounds(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "createFromPath",
        args = {java.lang.String.class}
    )
    public void testCreateFromPath() throws IOException {
        assertNull(Drawable.createFromPath(null));

        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                mContext.getPackageName() + R.raw.testimage);
        assertNull(Drawable.createFromPath(uri.getPath()));

        File imageFile = new File(mContext.getFilesDir(), "tempimage.jpg");
        assertTrue(imageFile.createNewFile());
        assertTrue(imageFile.exists());
        writeSampleImage(imageFile);

        final String path = imageFile.getPath();
        Uri u = Uri.parse(path);
        assertNotNull(Drawable.createFromPath(u.toString()));
        assertTrue(imageFile.delete());
    }

    private void writeSampleImage(File imagefile) throws IOException {
        InputStream source = null;
        OutputStream target = null;

        try {
            source = mResources.openRawResource(R.raw.testimage);
            target = new FileOutputStream(imagefile);

            byte[] buffer = new byte[1024];
            for (int len = source.read(buffer); len >= 0; len = source.read(buffer)) {
                target.write(buffer, 0, len);
            }
        } finally {
            if (target != null) {
                target.close();
            }

            if (source != null) {
                source.close();
            }
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "createFromStream",
        args = {java.io.InputStream.class, java.lang.String.class}
    )
    public void testCreateFromStream() throws FileNotFoundException, IOException {
        FileInputStream inputEmptyStream = null;
        FileInputStream inputStream = null;
        File imageFile = null;
        OutputStream outputEmptyStream = null;

        assertNull(Drawable.createFromStream(null, "test.bmp"));

        File emptyFile = new File(mContext.getFilesDir(), "tempemptyimage.jpg");

        // write some random data.
        try {
            outputEmptyStream = new FileOutputStream(emptyFile);
            outputEmptyStream.write(10);

            inputEmptyStream = new FileInputStream(emptyFile);
            assertNull(Drawable.createFromStream(inputEmptyStream, "Sample"));

            imageFile = new File(mContext.getFilesDir(), "tempimage.jpg");

            writeSampleImage(imageFile);

            inputStream = new FileInputStream(imageFile);
            assertNotNull(Drawable.createFromStream(inputStream, "Sample"));
        } finally {

            if (null != outputEmptyStream) {
                outputEmptyStream.close();
            }
            if (null != inputEmptyStream) {
                inputEmptyStream.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (emptyFile.exists()) {
                assertTrue(emptyFile.delete());
            }
            if (imageFile.exists()) {
                assertTrue(imageFile.delete());
            }
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "createFromXml",
        args = {android.content.res.Resources.class, org.xmlpull.v1.XmlPullParser.class}
    )
    public void testCreateFromXml() throws XmlPullParserException, IOException {
        XmlPullParser parser = mResources.getXml(R.drawable.gradientdrawable);
        Drawable drawable = Drawable.createFromXml(mResources, parser);
        // values from gradientdrawable.xml
        assertEquals(42, drawable.getIntrinsicWidth());
        assertEquals(63, drawable.getIntrinsicHeight());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "createFromXmlInner",
        args = {android.content.res.Resources.class, org.xmlpull.v1.XmlPullParser.class,
                android.util.AttributeSet.class}
    )
    public void testCreateFromXmlInner() throws XmlPullParserException, IOException {
        XmlPullParser parser = mResources.getXml(R.drawable.gradientdrawable);
        while (parser.next() != XmlPullParser.START_TAG) {
            // ignore event, just seek to first tag
        }
        AttributeSet attrs = Xml.asAttributeSet(parser);
        Drawable drawable = Drawable.createFromXmlInner(mResources, parser, attrs);
        assertNotNull(drawable);

        Drawable expected = mResources.getDrawable(R.drawable.gradientdrawable);

        assertEquals(expected.getIntrinsicWidth(), drawable.getIntrinsicWidth());
        assertEquals(expected.getIntrinsicHeight(), drawable.getIntrinsicHeight());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBounds",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setBounds",
            args = {int.class, int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setBounds",
            args = {android.graphics.Rect.class}
        )
    })
    @ToBeFixed(bug = "1417734", explanation = "NPE is not expected.")
    public void testAccessBounds() {
        MockDrawable mockDrawable = new MockDrawable();
        mockDrawable.setBounds(0, 0, 100, 100);
        Rect r = mockDrawable.getBounds();
        assertEquals(0, r.left);
        assertEquals(0, r.top);
        assertEquals(100, r.bottom);
        assertEquals(100, r.right);

        mockDrawable.setBounds(new Rect(10, 10, 150, 150));
        r = mockDrawable.getBounds();
        assertEquals(10, r.left);
        assertEquals(10, r.top);
        assertEquals(150, r.bottom);
        assertEquals(150, r.right);

        try {
            mockDrawable.setBounds(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getChangingConfigurations",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setChangingConfigurations",
            args = {int.class}
        )
    })
    public void testAccessChangingConfigurations() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(0, mockDrawable.getChangingConfigurations());

        mockDrawable.setChangingConfigurations(1);
        assertEquals(1, mockDrawable.getChangingConfigurations());

        mockDrawable.setChangingConfigurations(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, mockDrawable.getChangingConfigurations());

        mockDrawable.setChangingConfigurations(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, mockDrawable.getChangingConfigurations());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getConstantState",
        args = {}
    )
    public void testGetConstantState() {
        MockDrawable mockDrawable = new MockDrawable();
        assertNull(mockDrawable.getConstantState());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getCurrent",
        args = {}
    )
    public void testGetCurrent() {
        MockDrawable mockDrawable = new MockDrawable();
        assertSame(mockDrawable, mockDrawable.getCurrent());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getIntrinsicHeight",
        args = {}
    )
    public void testGetIntrinsicHeight() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(-1, mockDrawable.getIntrinsicHeight());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getIntrinsicWidth",
        args = {}
    )
    public void testGetIntrinsicWidth() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(-1, mockDrawable.getIntrinsicWidth());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getLevel",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setLevel",
            args = {int.class}
        )
    })
    public void testAccessLevel() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(0, mockDrawable.getLevel());

        assertFalse(mockDrawable.setLevel(10));
        assertEquals(10, mockDrawable.getLevel());

        assertFalse(mockDrawable.setLevel(20));
        assertEquals(20, mockDrawable.getLevel());

        assertFalse(mockDrawable.setLevel(0));
        assertEquals(0, mockDrawable.getLevel());

        assertFalse(mockDrawable.setLevel(10000));
        assertEquals(10000, mockDrawable.getLevel());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getMinimumHeight",
        args = {}
    )
    public void testGetMinimumHeight() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(0, mockDrawable.getMinimumHeight());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getMinimumWidth",
        args = {}
    )
    public void testGetMinimumWidth() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(0, mockDrawable.getMinimumWidth());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPadding",
        args = {android.graphics.Rect.class}
    )
    @ToBeFixed(bug = "1417734", explanation = "NPE is not expected.")
    public void testGetPadding() {
        MockDrawable mockDrawable = new MockDrawable();
        Rect r = new Rect(10, 10, 20, 20);
        assertFalse(mockDrawable.getPadding(r));
        assertEquals(0, r.bottom);
        assertEquals(0, r.top);
        assertEquals(0, r.left);
        assertEquals(0, r.right);

        try {
            mockDrawable.getPadding(null);
            fail("should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getState",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setState",
            args = {int[].class}
        )
    })
    public void testAccessState() {
        MockDrawable mockDrawable = new MockDrawable();
        assertEquals(StateSet.WILD_CARD, mockDrawable.getState());

        int[] states = new int[] {1, 2, 3};
        assertFalse(mockDrawable.setState(states));
        assertEquals(states, mockDrawable.getState());

        mockDrawable.setState(null);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getTransparentRegion",
        args = {}
    )
    public void testGetTransparentRegion() {
        MockDrawable mockDrawable = new MockDrawable();
        assertNull(mockDrawable.getTransparentRegion());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "inflate",
        args = {android.content.res.Resources.class, org.xmlpull.v1.XmlPullParser.class,
                android.util.AttributeSet.class}
    )
    @ToBeFixed(bug = "", explanation = "the attribute visible has been set to false " +
            "in drawable_test.xml, but isVisible() still returns true")
    public void testInflate() throws XmlPullParserException, IOException {
        MockDrawable mockDrawable = new MockDrawable();

        XmlPullParser parser = mResources.getXml(R.xml.drawable_test);
        while (parser.next() != XmlPullParser.START_TAG) {
            // ignore event, just seek to first tag
        }
        AttributeSet attrs = Xml.asAttributeSet(parser);

        mockDrawable.inflate(mResources, parser, attrs);
        // visibility set to false in resource
        assertFalse(mockDrawable.isVisible());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "invalidateSelf",
        args = {}
    )
    public void testInvalidateSelf() {
        MockDrawable mockDrawable = new MockDrawable();
        // if setCallback() is not called, invalidateSelf() would do nothing,
        // so just call it to check whether it throws exceptions.
        mockDrawable.invalidateSelf();

        MockCallback mockCallback = new MockCallback();
        mockDrawable.setCallback(mockCallback);
        mockDrawable.invalidateSelf();
        assertEquals(mockDrawable, mockCallback.getInvalidateDrawable());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "isStateful",
        args = {}
    )
    public void testIsStateful() {
        MockDrawable mockDrawable = new MockDrawable();
        assertFalse(mockDrawable.isStateful());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "isVisible",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setVisible",
            args = {boolean.class, boolean.class}
        )
    })
    public void testVisible() {
        MockDrawable mockDrawable = new MockDrawable();
        assertTrue(mockDrawable.isVisible());

        assertTrue(mockDrawable.setVisible(false, false));
        assertFalse(mockDrawable.isVisible());

        assertFalse(mockDrawable.setVisible(false, false));
        assertFalse(mockDrawable.isVisible());

        assertTrue(mockDrawable.setVisible(true, false));
        assertTrue(mockDrawable.isVisible());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "onBoundsChange",
        args = {android.graphics.Rect.class}
    )
    public void testOnBoundsChange() {
        MockDrawable mockDrawable = new MockDrawable();

        // onBoundsChange is a non-operation function.
        mockDrawable.onBoundsChange(new Rect(0, 0, 10, 10));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "onLevelChange",
        args = {int.class}
    )
    public void testOnLevelChange() {
        MockDrawable mockDrawable = new MockDrawable();
        assertFalse(mockDrawable.onLevelChange(0));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "onStateChange",
        args = {int[].class}
    )
    public void testOnStateChange() {
        MockDrawable mockDrawable = new MockDrawable();
        assertFalse(mockDrawable.onStateChange(null));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "resolveOpacity",
        args = {int.class, int.class}
    )
    public void testResolveOpacity() {
        assertEquals(PixelFormat.TRANSLUCENT,
                Drawable.resolveOpacity(PixelFormat.TRANSLUCENT, PixelFormat.TRANSLUCENT));
        assertEquals(PixelFormat.UNKNOWN,
                Drawable.resolveOpacity(PixelFormat.UNKNOWN, PixelFormat.TRANSLUCENT));
        assertEquals(PixelFormat.TRANSLUCENT,
                Drawable.resolveOpacity(PixelFormat.OPAQUE, PixelFormat.TRANSLUCENT));
        assertEquals(PixelFormat.TRANSPARENT,
                Drawable.resolveOpacity(PixelFormat.OPAQUE, PixelFormat.TRANSPARENT));
        assertEquals(PixelFormat.OPAQUE,
                Drawable.resolveOpacity(PixelFormat.RGB_888, PixelFormat.RGB_565));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "scheduleSelf",
        args = {java.lang.Runnable.class, long.class}
    )
    public void testScheduleSelf() {
        MockDrawable mockDrawable = new MockDrawable();
        MockCallback mockCallback = new MockCallback();
        mockDrawable.setCallback(mockCallback);
        mockDrawable.scheduleSelf(null, 1000L);
        assertEquals(mockDrawable, mockCallback.getScheduleDrawable());
        assertNull(mockCallback.getRunnable());
        assertEquals(1000L, mockCallback.getWhen());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setCallback",
        args = {android.graphics.drawable.Drawable.Callback.class}
    )
    public void testSetCallback() {
        MockDrawable mockDrawable = new MockDrawable();

        MockCallback mockCallback = new MockCallback();
        mockDrawable.setCallback(mockCallback);
        mockDrawable.scheduleSelf(null, 1000L);
        assertEquals(mockDrawable, mockCallback.getScheduleDrawable());
        assertNull(mockCallback.getRunnable());
        assertEquals(1000L, mockCallback.getWhen());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setColorFilter",
        args = {int.class, android.graphics.PorterDuff.Mode.class}
    )
    @ToBeFixed(bug="1400249", explanation="It will be tested by functional test")
    public void testSetColorFilter() {
        MockDrawable mockDrawable = new MockDrawable();

        mockDrawable.setColorFilter(5, PorterDuff.Mode.CLEAR);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setDither",
        args = {boolean.class}
    )
    public void testSetDither() {
        MockDrawable mockDrawable = new MockDrawable();

        // setDither is a non-operation function.
        mockDrawable.setDither(false);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setFilterBitmap",
        args = {boolean.class}
    )
    public void testSetFilterBitmap() {
        MockDrawable mockDrawable = new MockDrawable();

        // setFilterBitmap is a non-operation function.
        mockDrawable.setFilterBitmap(false);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "unscheduleSelf",
        args = {java.lang.Runnable.class}
    )
    public void testUnscheduleSelf() {
        MockDrawable mockDrawable = new MockDrawable();
        MockCallback mockCallback = new MockCallback();
        mockDrawable.setCallback(mockCallback);
        mockDrawable.unscheduleSelf(null);
        assertEquals(mockDrawable, mockCallback.getScheduleDrawable());
        assertNull(mockCallback.getRunnable());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "mutate",
        args = {}
    )
    public void testMutate() {
        MockDrawable mockDrawable = new MockDrawable();

        assertSame(mockDrawable, mockDrawable.mutate());
    }

    private static class MockDrawable extends Drawable {
        private ColorFilter mColorFilter;

        public void draw(Canvas canvas) {
        }

        public void setAlpha(int alpha) {
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
    }

    private static class MockCallback implements Callback {
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
