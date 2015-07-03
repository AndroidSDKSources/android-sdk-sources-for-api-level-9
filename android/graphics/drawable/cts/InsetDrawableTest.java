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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.util.Xml;

import java.io.IOException;

@TestTargetClass(android.graphics.drawable.InsetDrawable.class)
public class InsetDrawableTest extends AndroidTestCase {
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "InsetDrawable",
            args = {android.graphics.drawable.Drawable.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "InsetDrawable",
            args = {android.graphics.drawable.Drawable.class, int.class, int.class, int.class,
                    int.class}
        )
    })
    public void testConstructor() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        new InsetDrawable(d, 1);
        new InsetDrawable(d, 1, 1, 1, 1);

        new InsetDrawable(null, -1);
        new InsetDrawable(null, -1, -1, -1, -1);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "inflate",
        args = {android.content.res.Resources.class, org.xmlpull.v1.XmlPullParser.class,
                android.util.AttributeSet.class}
    )
    @ToBeFixed(bug = "1386429", explanation = "no getter can not be tested," +
            " and there should not be a NullPointerException thrown out.")
    public void testInflate() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        Resources r = mContext.getResources();
        XmlPullParser parser = r.getXml(R.layout.framelayout_layout);
        AttributeSet attrs = Xml.asAttributeSet(parser);

        try {
            insetDrawable.inflate(r, parser, attrs);
            fail("There should be a XmlPullParserException thrown out.");
        } catch (XmlPullParserException e) {
            // expected, test success
        } catch (IOException e) {
            fail("There should not be an IOException thrown out.");
        }

        // input null as params
        try {
            insetDrawable.inflate(null, null, null);
            fail("There should be a NullPointerException thrown out.");
        } catch (XmlPullParserException e) {
            fail("There should not be a XmlPullParserException thrown out.");
        } catch (IOException e) {
            fail("There should not be an IOException thrown out.");
        } catch (NullPointerException e) {
            // expected, test success
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "invalidateDrawable",
        args = {android.graphics.drawable.Drawable.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "it's hard to do unit test, should be tested by" +
            " functional test.")
    public void testInvalidateDrawable() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        insetDrawable.invalidateDrawable(d);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "scheduleDrawable",
        args = {android.graphics.drawable.Drawable.class, java.lang.Runnable.class, long.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "it's hard to do unit test, should be tested by" +
            " functional test.")
    public void testScheduleDrawable() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        Runnable runnable = new Runnable() {
            public void run() {
            }
        };
        insetDrawable.scheduleDrawable(d, runnable, 10);

        // input null as params
        insetDrawable.scheduleDrawable(null, null, -1);
        // expected, no Exception thrown out, test success
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "unscheduleDrawable",
        args = {android.graphics.drawable.Drawable.class, java.lang.Runnable.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "it's hard to do unit test, should be tested by" +
            " functional test.")
    public void testUnscheduleDrawable() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        Runnable runnable = new Runnable() {
            public void run() {
            }
        };
        insetDrawable.unscheduleDrawable(d, runnable);

        // input null as params
        insetDrawable.unscheduleDrawable(null, null);
        // expected, no Exception thrown out, test success
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "draw",
        args = {android.graphics.Canvas.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "it's hard to do unit test, should be tested by" +
            " functional test, and there should not be an NullPointerException thrown out.")
    public void testDraw() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        Canvas c = new Canvas();
        insetDrawable.draw(c);

        // input null as param
        try {
            insetDrawable.draw(null);
            fail("There should be a NullPointerException thrown out.");
        } catch (NullPointerException e) {
            // expected, test success
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getChangingConfigurations",
        args = {}
    )
    public void testGetChangingConfigurations() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        insetDrawable.setChangingConfigurations(11);
        assertEquals(11, insetDrawable.getChangingConfigurations());

        insetDrawable.setChangingConfigurations(-21);
        assertEquals(-21, insetDrawable.getChangingConfigurations());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPadding",
        args = {android.graphics.Rect.class}
    )
    @ToBeFixed(bug = "1371108", explanation = "There should not be a" +
            " NullPointerException thrown out.")
    public void testGetPadding() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 1, 2, 3, 4);

        Rect r = new Rect();
        assertEquals(0, r.left);
        assertEquals(0, r.top);
        assertEquals(0, r.right);
        assertEquals(0, r.bottom);

        assertTrue(insetDrawable.getPadding(r));

        assertEquals(1, r.left);
        assertEquals(2, r.top);
        assertEquals(3, r.right);
        assertEquals(4, r.bottom);

        // padding is set to 0, then return value should be false
        insetDrawable = new InsetDrawable(d, 0);

        r = new Rect();
        assertEquals(0, r.left);
        assertEquals(0, r.top);
        assertEquals(0, r.right);
        assertEquals(0, r.bottom);

        assertFalse(insetDrawable.getPadding(r));

        assertEquals(0, r.left);
        assertEquals(0, r.top);
        assertEquals(0, r.right);
        assertEquals(0, r.bottom);

        // input null as param
        try {
            insetDrawable.getPadding(null);
            fail("There should be a NullPointerException thrown out.");
        } catch (NullPointerException e) {
            // expected, test success
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setVisible",
        args = {boolean.class, boolean.class}
    )
    public void testSetVisible() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        assertFalse(insetDrawable.setVisible(true, true)); /* unchanged */
        assertTrue(insetDrawable.setVisible(false, true)); /* changed */
        assertFalse(insetDrawable.setVisible(false, true)); /* unchanged */
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setAlpha",
        args = {int.class}
    )
    @ToBeFixed(bug = "1386429", explanation = "no getter can not be tested")
    public void testSetAlpha() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        insetDrawable.setAlpha(1);
        insetDrawable.setAlpha(-1);

        insetDrawable.setAlpha(0);
        insetDrawable.setAlpha(Integer.MAX_VALUE);
        insetDrawable.setAlpha(Integer.MIN_VALUE);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "setColorFilter",
        args = {android.graphics.ColorFilter.class}
    )
    @ToBeFixed(bug = "1386429", explanation = "no getter can not be tested")
    public void testSetColorFilter() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        ColorFilter cf = new ColorFilter();
        insetDrawable.setColorFilter(cf);

        // input null as param
        insetDrawable.setColorFilter(null);
        // expected, no Exception thrown out, test success
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getOpacity",
        args = {}
    )
    public void testGetOpacity() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.testimage);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);
        insetDrawable.setAlpha(255);
        assertEquals(PixelFormat.OPAQUE, insetDrawable.getOpacity());

        insetDrawable.setAlpha(100);
        assertEquals(PixelFormat.TRANSLUCENT, insetDrawable.getOpacity());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "isStateful",
        args = {}
    )
    public void testIsStateful() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);
        assertFalse(insetDrawable.isStateful());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "onStateChange",
        args = {int[].class}
    )
    @ToBeFixed(bug = "", explanation = "The onStateChange will always return false.")
    public void testOnStateChange() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        MockInsetDrawable insetDrawable = new MockInsetDrawable(d, 10);

        Rect bounds = d.getBounds();
        assertEquals(0, bounds.left);
        assertEquals(0, bounds.top);
        assertEquals(0, bounds.right);
        assertEquals(0, bounds.bottom);

        int[] state = new int[] {1, 2, 3};
        assertFalse(insetDrawable.onStateChange(state));

        assertEquals(10, bounds.left);
        assertEquals(10, bounds.top);
        assertEquals(-10, bounds.right);
        assertEquals(-10, bounds.bottom);

        // input null as param
        insetDrawable.onStateChange(null);
        // expected, no Exception thrown out, test success
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "onBoundsChange",
        args = {android.graphics.Rect.class}
    )
    @ToBeFixed(bug = "1371108", explanation = "There should not be a" +
            " NullPointerException thrown out.")
    public void testOnBoundsChange() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        MockInsetDrawable insetDrawable = new MockInsetDrawable(d, 5);

        Rect bounds = d.getBounds();
        assertEquals(0, bounds.left);
        assertEquals(0, bounds.top);
        assertEquals(0, bounds.right);
        assertEquals(0, bounds.bottom);

        Rect r = new Rect();
        insetDrawable.onBoundsChange(r);

        assertEquals(5, bounds.left);
        assertEquals(5, bounds.top);
        assertEquals(-5, bounds.right);
        assertEquals(-5, bounds.bottom);

        // input null as param
        try {
            insetDrawable.onBoundsChange(null);
            fail("There should be a NullPointerException thrown out.");
        } catch (NullPointerException e) {
            // expected, test success
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getIntrinsicWidth",
        args = {}
    )
    public void testGetIntrinsicWidth() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        int expected = d.getIntrinsicWidth(); /* 31 */
        assertEquals(expected, insetDrawable.getIntrinsicWidth());

        d = mContext.getResources().getDrawable(R.drawable.scenery);
        insetDrawable = new InsetDrawable(d, 0);

        expected = d.getIntrinsicWidth(); /* 170 */
        assertEquals(expected, insetDrawable.getIntrinsicWidth());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getIntrinsicHeight",
        args = {}
    )
    public void testGetIntrinsicHeight() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        int expected = d.getIntrinsicHeight(); /* 31 */
        assertEquals(expected, insetDrawable.getIntrinsicHeight());

        d = mContext.getResources().getDrawable(R.drawable.scenery);
        insetDrawable = new InsetDrawable(d, 0);

        expected = d.getIntrinsicHeight(); /* 107 */
        assertEquals(expected, insetDrawable.getIntrinsicHeight());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getConstantState",
        args = {}
    )
    @ToBeFixed(bug = "", explanation = "can not assert the inner fields, becuase the class" +
            " InsetState is package protected.")
    public void testGetConstantState() {
        Drawable d = mContext.getResources().getDrawable(R.drawable.pass);
        InsetDrawable insetDrawable = new InsetDrawable(d, 0);

        ConstantState constantState = insetDrawable.getConstantState();
        assertNotNull(constantState);
    }

    private class MockInsetDrawable extends InsetDrawable {
        public MockInsetDrawable(Drawable drawable, int inset) {
            super(drawable, inset);
        }

        protected boolean onStateChange(int[] state) {
            return super.onStateChange(state);
        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }
    }
}
