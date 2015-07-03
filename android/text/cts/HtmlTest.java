/*
 * Copyright (C) 2009 The Android Open Source Project
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

import android.graphics.Typeface;
import android.test.AndroidTestCase;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.style.ForegroundColorSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

@TestTargetClass(Html.class)
public class HtmlTest extends AndroidTestCase {
    private final static int SPAN_EXCLUSIVE_INCLUSIVE = Spannable.SPAN_EXCLUSIVE_INCLUSIVE;

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class, ImageGetter.class, TagHandler.class}
        )
    })
    public void testSingleTagOnWhileString() {
        final String source = "<b>hello</b>";

        Spanned spanned = Html.fromHtml(source);
        assertSingleTagOnWhileString(spanned);
        spanned = Html.fromHtml(source, null, null);
        assertSingleTagOnWhileString(spanned);
    }

    private void assertSingleTagOnWhileString(Spanned spanned) {
        final int expectStart = 0;
        final int expectEnd = 5;
        final int expectLen = 1;
        final int start = -1;
        final int end = 100;

        Object[] spans = spanned.getSpans(start, end, Object.class);
        assertEquals(expectLen, spans.length);
        assertEquals(expectStart, spanned.getSpanStart(spans[0]));
        assertEquals(expectEnd, spanned.getSpanEnd(spans[0]));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class, ImageGetter.class, TagHandler.class}
        )
    })
    public void testBadHtml() {
        final String source = "Hello <b>b<i>bi</b>i</i>";

        Spanned spanned = Html.fromHtml(source);
        assertBadHtml(spanned);
        spanned = Html.fromHtml(source, null, null);
        assertBadHtml(spanned);
    }

    private void assertBadHtml(Spanned spanned) {
        final int start = 0;
        final int end = 100;
        final int spansLen = 3;

        Object[] spans = spanned.getSpans(start, end, Object.class);
        assertEquals(spansLen, spans.length);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class, ImageGetter.class, TagHandler.class}
        )
    })
    public void testSymbols() {
        final String source = "&copy; &gt; &lt";
        final String expected = "\u00a9 > <";

        String spanned = Html.fromHtml(source).toString();
        assertEquals(expected, spanned);
        spanned = Html.fromHtml(source, null, null).toString();
        assertEquals(expected, spanned);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            method = "fromHtml",
            args = {String.class, ImageGetter.class, TagHandler.class}
        )
    })
    public void testColor() throws Exception {
        final int start = 0;

        Class<ForegroundColorSpan> type = ForegroundColorSpan.class;
        ForegroundColorSpan[] colors;
        Spanned s = Html.fromHtml("<font color=\"#00FF00\">something</font>");
        int end = s.length();
        colors = s.getSpans(start, end, type);
        int expectedColor = 0xFF00FF00;
        assertEquals(expectedColor, colors[0].getForegroundColor());

        s = Html.fromHtml("<font color=\"navy\">something</font>");
        end = s.length();
        colors = s.getSpans(start, end, type);
        expectedColor = 0xFF000080;
        assertEquals(0xFF000080, colors[0].getForegroundColor());

        s = Html.fromHtml("<font color=\"gibberish\">something</font>");
        end = s.length();
        colors = s.getSpans(start, end, type);
        assertEquals(0, colors.length);
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testParagraphs() throws Exception {
        SpannableString s = new SpannableString("Hello world");
        assertEquals("<p>Hello world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello world\nor something");
        assertEquals("<p>Hello world<br>\nor something</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello world\n\nor something");
        assertEquals("<p>Hello world</p>\n<p>or something</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello world\n\n\nor something");
        assertEquals("<p>Hello world<br></p>\n<p>or something</p>\n", Html.toHtml(s));
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testBlockquote() throws Exception {
        final int start = 0;

        SpannableString s = new SpannableString("Hello world");
        int end = s.length();
        s.setSpan(new QuoteSpan(), start, end, Spannable.SPAN_PARAGRAPH);
        assertEquals("<blockquote><p>Hello world</p>\n</blockquote>\n", Html.toHtml(s));

        s = new SpannableString("Hello\n\nworld");
        end = 7;
        s.setSpan(new QuoteSpan(), start, end, Spannable.SPAN_PARAGRAPH);
        assertEquals("<blockquote><p>Hello</p>\n</blockquote>\n<p>world</p>\n", Html.toHtml(s));
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testEntities() throws Exception {
        SpannableString s = new SpannableString("Hello <&> world");
        assertEquals("<p>Hello &lt;&amp;&gt; world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello \u03D5 world");
        assertEquals("<p>Hello &#981; world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello  world");
        assertEquals("<p>Hello&nbsp; world</p>\n", Html.toHtml(s));
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testMarkup() throws Exception {
        final int start = 6;

        SpannableString s = new SpannableString("Hello bold world");
        int end = s.length() - start;
        s.setSpan(new StyleSpan(Typeface.BOLD), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <b>bold</b> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello italic world");
        end = s.length() - start;
        s.setSpan(new StyleSpan(Typeface.ITALIC), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <i>italic</i> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello monospace world");
        end = s.length() - start;
        s.setSpan(new TypefaceSpan("monospace"), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <tt>monospace</tt> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello superscript world");
        end = s.length() - start;
        s.setSpan(new SuperscriptSpan(), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <sup>superscript</sup> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello subscript world");
        end = s.length() - start;
        s.setSpan(new SubscriptSpan(), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <sub>subscript</sub> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello underline world");
        end = s.length() - start;
        s.setSpan(new UnderlineSpan(), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <u>underline</u> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello struck world");
        end = s.length() - start;
        s.setSpan(new StrikethroughSpan(), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        assertEquals("<p>Hello <strike>struck</strike> world</p>\n", Html.toHtml(s));

        s = new SpannableString("Hello linky world");
        end = s.length() - start;
        s.setSpan(new URLSpan("http://www.google.com"), start, end, SPAN_EXCLUSIVE_INCLUSIVE);
        String ret = Html.toHtml(s);
        assertEquals("<p>Hello <a href=\"http://www.google.com\">linky</a> world</p>\n", ret);
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testImg() throws Exception {
        Spanned s = Html.fromHtml("yes<img src=\"http://example.com/foo.gif\">no");
        assertEquals("<p>yes<img src=\"http://example.com/foo.gif\">no</p>\n", Html.toHtml(s));
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Test method: toHtml",
        method = "toHtml",
        args = {Spanned.class}
    )
    public void testUtf8() throws Exception {
        Spanned s = Html.fromHtml("<p>\u0124\u00eb\u0142\u0142o, world!</p>");
        assertEquals("<p>&#292;&#235;&#322;&#322;o, world!</p>\n", Html.toHtml(s));
    }
}
