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

package android.text.cts;

import android.test.AndroidTestCase;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.Layout.Alignment;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

@TestTargetClass(StaticLayout.class)
public class StaticLayoutTest extends AndroidTestCase {
    private static final float SPACE_MULTI = 1.0f;
    private static final float SPACE_ADD = 0.0f;
    private static final int DEFAULT_OUTER_WIDTH = 150;

    private static final int LAST_LINE = 5;
    private static final int LINE_COUNT = 6;
    private static final int LARGER_THAN_LINE_COUNT  = 50;

    /* the first line must have one tab. the others not. totally 6 lines
     */
    private static final CharSequence LAYOUT_TEXT = "CharSe\tq\nChar"
            + "Sequence\nCharSequence\nHelllo\n, world\nLongLongLong";

    private static final int VERTICAL_BELOW_TEXT = 1000;

    private static final Alignment DEFAULT_ALIGN = Alignment.ALIGN_CENTER;

    private static final int ELLIPSIZE_WIDTH = 8;

    private StaticLayout mDefaultLayout;
    private TextPaint mDefaultPaint;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (mDefaultPaint == null) {
            mDefaultPaint = new TextPaint();
        }
        if (mDefaultLayout == null) {
            mDefaultLayout = createDefaultStaticLayout();
        }
    }

    private StaticLayout createDefaultStaticLayout() {
        return new StaticLayout(LAYOUT_TEXT, mDefaultPaint,
                DEFAULT_OUTER_WIDTH, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, true);
    }

    private StaticLayout createEllipsizeStaticLayout() {
        return new StaticLayout(LAYOUT_TEXT, 0, LAYOUT_TEXT.length(), mDefaultPaint,
                DEFAULT_OUTER_WIDTH, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, true,
                TextUtils.TruncateAt.MIDDLE, ELLIPSIZE_WIDTH);
    }

    /**
     * Constructor test
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "StaticLayout",
            args = {java.lang.CharSequence.class, int.class, int.class,
                    android.text.TextPaint.class, int.class, android.text.Layout.Alignment.class,
                    float.class, float.class, boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "StaticLayout",
            args = {java.lang.CharSequence.class, int.class, int.class,
                    android.text.TextPaint.class, int.class, android.text.Layout.Alignment.class,
                    float.class, float.class, boolean.class,
                    android.text.TextUtils.TruncateAt.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "StaticLayout",
            args = {java.lang.CharSequence.class, android.text.TextPaint.class, int.class,
            android.text.Layout.Alignment.class, float.class, float.class, boolean.class}
        )
    })
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout constructors when input null parameters")
    public void testConstructor() {
        new StaticLayout(LAYOUT_TEXT, mDefaultPaint, DEFAULT_OUTER_WIDTH,
                DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, true);

        new StaticLayout(LAYOUT_TEXT, 0, LAYOUT_TEXT.length(), mDefaultPaint,
                DEFAULT_OUTER_WIDTH, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, true);

        new StaticLayout(LAYOUT_TEXT, 0, LAYOUT_TEXT.length(), mDefaultPaint,
                DEFAULT_OUTER_WIDTH, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, false, null, 0);

        try {
            new StaticLayout(null, null, -1, null, 0, 0, true);
            fail("should throw NullPointerException here");
        } catch (NullPointerException e) {
        }
    }

    /*
     * Get the line number corresponding to the specified vertical position.
     *  If you ask for a position above 0, you get 0. above 0 means pixel above the fire line
     *  if you ask for a position in the range of the height, return the pixel in line
     *  if you ask for a position below the bottom of the text, you get the last line.
     *  Test 4 values containing -1, 0, normal number and > count
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineForVertical",
        args = {int.class}
    )
     @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete")
    public void testGetLineForVertical() {
        assertEquals(0, mDefaultLayout.getLineForVertical(-1));
        assertEquals(0, mDefaultLayout.getLineForVertical(0));
        assertTrue(mDefaultLayout.getLineForVertical(50) > 0);
        assertEquals(LAST_LINE, mDefaultLayout.getLineForVertical(VERTICAL_BELOW_TEXT));
    }

    /**
     * Return the number of lines of text in this layout.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineCount",
        args = {}
    )
    public void testGetLineCount() {
        assertEquals(LINE_COUNT, mDefaultLayout.getLineCount());
    }

    /*
     * Return the vertical position of the top of the specified line.
     * If the specified line is one beyond the last line, returns the bottom of the last line.
     * A line of text contains top and bottom in height. this method just get the top of a line
     * Test 4 values containing -1, 0, normal number and > count
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineTop",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        +    " of StaticLayout#getLineTop(int) when line is out of bound")
    public void testGetLineTop() {
        assertTrue(mDefaultLayout.getLineTop(0) >= 0);
        assertTrue(mDefaultLayout.getLineTop(1) > mDefaultLayout.getLineTop(0));

        try {
            mDefaultLayout.getLineTop(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getLineTop(LARGER_THAN_LINE_COUNT );
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Return the descent of the specified line.
     * This method just like getLineTop, descent means the bottom pixel of the line
     * Test 4 values containing -1, 0, normal number and > count
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineDescent",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getLineDescent(int) when line is out of bound")
    public void testGetLineDescent() {
        assertTrue(mDefaultLayout.getLineDescent(0) > 0);
        assertTrue(mDefaultLayout.getLineDescent(1) > 0);

        try {
            mDefaultLayout.getLineDescent(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getLineDescent(LARGER_THAN_LINE_COUNT );
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Returns the primary directionality of the paragraph containing the specified line.
     * By default, each line should be same
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getParagraphDirection",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getParagraphDirection(int) when line is out of bound")
    public void testGetParagraphDirection() {
        assertEquals(mDefaultLayout.getParagraphDirection(0),
                mDefaultLayout.getParagraphDirection(1));
        try {
            mDefaultLayout.getParagraphDirection(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getParagraphDirection(LARGER_THAN_LINE_COUNT);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Return the text offset of the beginning of the specified line.
     * If the specified line is one beyond the last line, returns the end of the last line.
     * Test 4 values containing -1, 0, normal number and > count
     * Each line's offset must >= 0
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineStart",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getLineStart(int) when line is out of bound")
    public void testGetLineStart() {
        assertTrue(mDefaultLayout.getLineStart(0) >= 0);
        assertTrue(mDefaultLayout.getLineStart(1) >= 0);

        try {
            mDefaultLayout.getLineStart(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getLineStart(LARGER_THAN_LINE_COUNT);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /*
     * Returns whether the specified line contains one or more tabs.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineContainsTab",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getLineContainsTab(int) when line is out of bound")
    public void testGetContainsTab() {
        assertTrue(mDefaultLayout.getLineContainsTab(0));
        assertFalse(mDefaultLayout.getLineContainsTab(1));

        try {
            mDefaultLayout.getLineContainsTab(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getLineContainsTab(LARGER_THAN_LINE_COUNT );
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Returns an array of directionalities for the specified line.
     * The array alternates counts of characters in left-to-right
     * and right-to-left segments of the line.
     * We can not check the return value, for Directions's field is package private
     * So only check it not null
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getLineDirections",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getLineDirections(int) when line is out of bound")
    public void testGetLineDirections() {
        assertNotNull(mDefaultLayout.getLineDirections(0));
        assertNotNull(mDefaultLayout.getLineDirections(1));

        try {
            mDefaultLayout.getLineDirections(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getLineDirections(LARGER_THAN_LINE_COUNT);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /**
     * Returns the (negative) number of extra pixels of ascent padding
     * in the top line of the Layout.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getTopPadding",
        args = {}
    )
    public void testGetTopPadding() {
        assertTrue(mDefaultLayout.getTopPadding() < 0);
    }

    /**
     * Returns the number of extra pixels of descent padding in the bottom line of the Layout.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getBottomPadding",
        args = {}
    )
    public void testGetBottomPadding() {
        assertTrue(mDefaultLayout.getBottomPadding() > 0);
    }

    /*
     * Returns the number of characters to be ellipsized away, or 0 if no ellipsis is to take place.
     * So each line must >= 0
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getEllipsisCount",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getEllipsisCount(int) when line is out of bound")
    public void testGetEllipsisCount() {
        mDefaultLayout = createEllipsizeStaticLayout();
        assertTrue(mDefaultLayout.getEllipsisCount(0) > 0);
        assertTrue(mDefaultLayout.getEllipsisCount(1) > 0);

        try {
            mDefaultLayout.getEllipsisCount(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getEllipsisCount(LARGER_THAN_LINE_COUNT);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /*
     * Return the offset of the first character to be ellipsized away
     * relative to the start of the line.
     * (So 0 if the beginning of the line is ellipsized, not getLineStart().)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getEllipsisStart",
        args = {int.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "should add @throws clause into javadoc "
        + " of StaticLayout#getEllipsisStart(int) when line is out of bound")
    public void testGetEllipsisStart() {
        mDefaultLayout = createEllipsizeStaticLayout();
        assertTrue(mDefaultLayout.getEllipsisStart(0) >= 0);
        assertTrue(mDefaultLayout.getEllipsisStart(1) >= 0);

        try {
            mDefaultLayout.getEllipsisStart(-1);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mDefaultLayout.getEllipsisStart(LARGER_THAN_LINE_COUNT);
            fail("should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    /*
     * Return the width to which this Layout is ellipsizing
     * or getWidth() if it is not doing anything special.
     * The constructor's Argument TextUtils.TruncateAt defines which EllipsizedWidth to use
     * ellipsizedWidth if argument is not null
     * outerWidth if argument is null
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getEllipsizedWidth",
        args = {}
    )
    public void testGetEllipsizedWidth() {
        int ellipsizedWidth = 60;
        int outerWidth = 100;
        StaticLayout layout = new StaticLayout(LAYOUT_TEXT, 0, LAYOUT_TEXT.length(),
                mDefaultPaint, outerWidth, DEFAULT_ALIGN, SPACE_MULTI,
                SPACE_ADD, false, TextUtils.TruncateAt.END, ellipsizedWidth);
        assertEquals(ellipsizedWidth, layout.getEllipsizedWidth());

        layout = new StaticLayout(LAYOUT_TEXT, 0, LAYOUT_TEXT.length(),
                mDefaultPaint, outerWidth, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD,
                false, null, ellipsizedWidth);
        assertEquals(outerWidth, layout.getEllipsizedWidth());
    }

    /**
     * scenario description:
     * 1. set the text.
     * 2. change the text
     * 3. Check the text won't change to the StaticLayout
    */
    public void testImmutableStaticLayout() {
        Editable editable =  Editable.Factory.getInstance().newEditable("123\t\n555");
        StaticLayout layout = new StaticLayout(editable, mDefaultPaint,
                DEFAULT_OUTER_WIDTH, DEFAULT_ALIGN, SPACE_MULTI, SPACE_ADD, true);

        assertEquals(2, layout.getLineCount());
        assertTrue(mDefaultLayout.getLineContainsTab(0));

        // change the text
        editable.delete(0, editable.length() - 1);

        assertEquals(2, layout.getLineCount());
        assertTrue(layout.getLineContainsTab(0));

    }
}
