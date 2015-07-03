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

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import android.content.Context;
import android.test.AndroidTestCase;
import android.text.ClipboardManager;

/**
 * Test {@link ClipboardManager}.
 */
@TestTargetClass(ClipboardManager.class)
public class ClipboardManagerTest extends AndroidTestCase {
    private ClipboardManager mClipboardManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setText",
            args = {CharSequence.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getText",
            args = {}
        )
    })
    public void testAccessText() {
        // set the expected value
        CharSequence expected = "test";
        mClipboardManager.setText(expected);
        assertEquals(expected, mClipboardManager.getText());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "hasText",
        args = {}
    )
    public void testHasText() {
        mClipboardManager.setText("");
        assertFalse(mClipboardManager.hasText());

        mClipboardManager.setText("test");
        assertTrue(mClipboardManager.hasText());

        mClipboardManager.setText(null);
        assertFalse(mClipboardManager.hasText());
    }
}
