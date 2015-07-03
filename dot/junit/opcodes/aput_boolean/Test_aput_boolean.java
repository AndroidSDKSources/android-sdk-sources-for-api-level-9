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

package dot.junit.opcodes.aput_boolean;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.aput_boolean.d.T_aput_boolean_1;
import dot.junit.opcodes.aput_boolean.d.T_aput_boolean_8;

public class Test_aput_boolean extends DxTestCase {
    /**  
     * @title put boolean into array 
     */
    public void testN1() {
        T_aput_boolean_1 t = new T_aput_boolean_1();
        boolean[] arr = new boolean[2];
        t.run(arr, 1, true);
        assertEquals(true, arr[1]);
    }

    /**
     * @title put boolean into array
     */
    public void testN2() {
        T_aput_boolean_1 t = new T_aput_boolean_1();
        boolean[] arr = new boolean[2];
        t.run(arr, 0, true);
        assertEquals(true, arr[0]);
    }
    
    /**
     * @title Type of index argument - float. Dalvik doens't distinguish 32-bits types internally,
     * so this array[float]=value makes no sense but shall not crash the VM.  
     */

    public void testN3() {
        boolean[] arr = new boolean[2];
        T_aput_boolean_8 t = new T_aput_boolean_8();
        try {
            t.run(arr, 3.14f, true);
        } catch (Throwable e) {
        }
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException
     */
    public void testE1() {
        T_aput_boolean_1 t = new T_aput_boolean_1();
        boolean[] arr = new boolean[2];
        try {
            t.run(arr, 2, true);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    /**
     * @title expected NullPointerException
     */
    public void testE2() {
        T_aput_boolean_1 t = new T_aput_boolean_1();
        try {
            t.run(null, 2, true);
            fail("expected NullPointerException");
        } catch (NullPointerException aie) {
            // expected
        }
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException (negative index)
     */
    public void testE3() {
        T_aput_boolean_1 t = new T_aput_boolean_1();
        boolean[] arr = new boolean[2];
        try {
            t.run(arr, -1, true);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    

    /**
     * @constraint B1 
     * @title types of arguments - array, double, int
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1
     * @title types of arguments - array, int, long
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - object, int, int
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - double[], int, int
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - long[], int, int
     */
    public void testVFE5() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - array, reference, int
     */
    public void testVFE6() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint A23 
     * @title number of registers
     */
    public void testVFE7() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B15 
     * @title put value 2 into boolean array
     */
    public void testVFE9() {
        try {
            Class.forName("dot.junit.opcodes.aput_boolean.d.T_aput_boolean_10");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
