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

package android.util.cts;

import android.test.AndroidTestCase;
import android.util.SparseIntArray;

import java.util.Arrays;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

@TestTargetClass(SparseIntArray.class)
public class SparseIntArrayTest extends AndroidTestCase {
    private static final int[] KEYS   = {12, 23, 4, 6, 8, 1, 3, -12, 0, -3, 11, 14, -23};
    private static final int[] VALUES = {0,  1,  2, 3, 4, 5, 6, 7,   8,  9, 10, 11,  12};
    private static final int   NON_EXISTED_KEY = 123;
    private static final int   VALUE_FOR_NON_EXISTED_KEY = -1;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "SparseIntArray",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "append",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "clear",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "delete",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "get",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "get",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "indexOfKey",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "indexOfValue",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "keyAt",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "put",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "size",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with default capacity.",
            method = "valueAt",
            args = {int.class}
        )
    })
    public void testSparseIntArrayWithDefaultCapacity() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        assertEquals(0, sparseIntArray.size());

        int length = VALUES.length;
        for (int i = 0; i < length; i++) {
            sparseIntArray.put(KEYS[i], VALUES[i]);
            assertEquals(i + 1, sparseIntArray.size());
        }
        for (int i = 0; i < length; i++) {
            assertEquals(VALUES[i], sparseIntArray.get(KEYS[i]));
        }
        for (int i = 0; i < length; i++) {
            assertEquals(sparseIntArray.indexOfValue(VALUES[i]),
                    sparseIntArray.indexOfKey(KEYS[i]));
        }

        // for key already exist, old value will be replaced
        int existKey = KEYS[0];
        int oldValue = VALUES[0]; // 0
        int newValue = 23;
        assertEquals(oldValue, sparseIntArray.get(existKey));
        assertEquals(13, sparseIntArray.size());
        sparseIntArray.put(existKey, newValue);
        assertEquals(newValue, sparseIntArray.get(existKey));
        assertEquals(13, sparseIntArray.size());

        assertEquals(VALUE_FOR_NON_EXISTED_KEY,
                     sparseIntArray.get(NON_EXISTED_KEY, VALUE_FOR_NON_EXISTED_KEY));
        assertEquals(0, sparseIntArray.get(NON_EXISTED_KEY)); // the default value is 0

        int size = sparseIntArray.size();
        sparseIntArray.append(NON_EXISTED_KEY, VALUE_FOR_NON_EXISTED_KEY);
        assertEquals(size + 1, sparseIntArray.size());
        assertEquals(size, sparseIntArray.indexOfKey(NON_EXISTED_KEY));
        assertEquals(size, sparseIntArray.indexOfValue(VALUE_FOR_NON_EXISTED_KEY));
        assertEquals(NON_EXISTED_KEY, sparseIntArray.keyAt(size));
        assertEquals(VALUE_FOR_NON_EXISTED_KEY, sparseIntArray.valueAt(size));

        assertEquals(VALUES[1], sparseIntArray.get(KEYS[1]));
        assertFalse(VALUE_FOR_NON_EXISTED_KEY == VALUES[1]);
        sparseIntArray.delete(KEYS[1]);
        assertEquals(VALUE_FOR_NON_EXISTED_KEY,
                sparseIntArray.get(KEYS[1], VALUE_FOR_NON_EXISTED_KEY));

        sparseIntArray.clear();
        assertEquals(0, sparseIntArray.size());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "SparseIntArray",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "append",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "clear",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "delete",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "get",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "get",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "indexOfKey",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "indexOfValue",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "keyAt",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "put",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "size",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "valueAt",
            args = {int.class}
        )
    })
    public void testSparseIntArrayWithSpecifiedCapacity() {
        SparseIntArray sparseIntArray = new SparseIntArray(5);
        assertEquals(0, sparseIntArray.size());

        int length = VALUES.length;
        for (int i = 0; i < length; i++) {
            sparseIntArray.put(KEYS[i], VALUES[i]);
            assertEquals(i + 1, sparseIntArray.size());
        }
        for (int i = 0; i < length; i++) {
            assertEquals(VALUES[i], sparseIntArray.get(KEYS[i]));
        }
        for (int i = 0; i < length; i++) {
            assertEquals(sparseIntArray.indexOfValue(VALUES[i]), sparseIntArray.indexOfKey(KEYS[i]));
        }

        // for key already exist, old value will be replaced
        int existKey = KEYS[0];
        int oldValue = VALUES[0]; // 0
        int newValue = 23;
        assertEquals(oldValue, sparseIntArray.get(existKey));
        assertEquals(13, sparseIntArray.size());
        sparseIntArray.put(existKey, newValue);
        assertEquals(newValue, sparseIntArray.get(existKey));
        assertEquals(13, sparseIntArray.size());

        assertEquals(VALUE_FOR_NON_EXISTED_KEY,
                     sparseIntArray.get(NON_EXISTED_KEY, VALUE_FOR_NON_EXISTED_KEY));
        assertEquals(0, sparseIntArray.get(NON_EXISTED_KEY)); // the default value is 0

        int size = sparseIntArray.size();
        sparseIntArray.append(NON_EXISTED_KEY, VALUE_FOR_NON_EXISTED_KEY);
        assertEquals(size + 1, sparseIntArray.size());
        assertEquals(size, sparseIntArray.indexOfKey(NON_EXISTED_KEY));
        assertEquals(size, sparseIntArray.indexOfValue(VALUE_FOR_NON_EXISTED_KEY));
        assertEquals(NON_EXISTED_KEY, sparseIntArray.keyAt(size));
        assertEquals(VALUE_FOR_NON_EXISTED_KEY, sparseIntArray.valueAt(size));

        assertEquals(VALUES[1], sparseIntArray.get(KEYS[1]));
        assertFalse(VALUE_FOR_NON_EXISTED_KEY == VALUES[1]);
        sparseIntArray.delete(KEYS[1]);
        assertEquals(VALUE_FOR_NON_EXISTED_KEY,
                sparseIntArray.get(KEYS[1], VALUE_FOR_NON_EXISTED_KEY));

        sparseIntArray.clear();
        assertEquals(0, sparseIntArray.size());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test SparseIntArray with specified capacity.",
            method = "removeAt",
            args = {int.class}
        )
    })
    public void testSparseIntArrayRemoveAt() {
        final int[] testData = {
            13, 42, 85932, 885932, -6, Integer.MAX_VALUE, 0, Integer.MIN_VALUE };

        // test removal of one key/value pair, varying the index
        for (int i = 0; i < testData.length; i++) {
            SparseIntArray sia = new SparseIntArray();
            for (int value : testData) {
                sia.put(value, value);
            }
            int size = testData.length;
            assertEquals(size, sia.size());
            int key = sia.keyAt(i);
            assertEquals(key, sia.get(key));
            sia.removeAt(i);
            assertEquals(21, sia.get(key, 21));
            assertEquals(size-1, sia.size());
        }

        // remove the 0th pair repeatedly until the array is empty
        SparseIntArray sia = new SparseIntArray();
        for (int value : testData) {
            sia.put(value, value);
        }
        for (int i = 0; i < testData.length; i++) {
            sia.removeAt(0);
        }
        assertEquals(0, sia.size());
        // make sure all pairs have been removed
        for (int value : testData) {
            assertEquals(21, sia.get(value, 21));
        }

        // test removal of a pair from an empty array
        try {
            new SparseIntArray().removeAt(0);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // expected
        }
    }

}

