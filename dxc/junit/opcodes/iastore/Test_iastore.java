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

package dxc.junit.opcodes.iastore;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.iastore.jm.T_iastore_1;

public class Test_iastore extends DxTestCase {

    /**
     * @title Normal test/. Trying different indexes
     */
    public void testN1() {
        T_iastore_1 t = new T_iastore_1();
        int[] arr = new int[2];
        t.run(arr, 1, 100000000);
        assertEquals(100000000, arr[1]);
    }

    /**
     * @title Normal test/. Trying different indexes
     */
    public void testN2() {
        T_iastore_1 t = new T_iastore_1();
        int[] arr = new int[2];
        t.run(arr, 0, 100000000);
        assertEquals(100000000, arr[0]);
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException
     */
    public void testE1() {
        T_iastore_1 t = new T_iastore_1();
        int[] arr = new int[2];
        try {
            t.run(arr, 2, 100000000);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    /**
     * @title expected NullPointerException
     */
    public void testE2() {
        T_iastore_1 t = new T_iastore_1();
        try {
            t.run(null, 2, 100000000);
            fail("expected NullPointerException");
        } catch (NullPointerException aie) {
            // expected
        }
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException
     */
    public void testE3() {
        T_iastore_1 t = new T_iastore_1();
        int[] arr = new int[2];
        try {
            t.run(arr, -1, 100000000);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - array, double,
     * int
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - array, int, long
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - object, int, int
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - double[], int,
     * int
     */
    public void testVFE6() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - long[], int, int
     */
    public void testVFE7() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - array, reference,
     * long
     */
    public void testVFE8() {
        try {
            Class.forName("dxc.junit.opcodes.iastore.jm.T_iastore_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
