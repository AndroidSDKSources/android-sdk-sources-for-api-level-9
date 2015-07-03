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

package dot.junit.opcodes.aget_char;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.aget_char.d.T_aget_char_1;
import dot.junit.opcodes.aget_char.d.T_aget_char_8;

public class Test_aget_char extends DxTestCase {
    /**
     * @title get char from array 
     */
    public void testN1() {
        T_aget_char_1 t = new T_aget_char_1();
        char[] arr = new char[2];
        arr[1] = 'g';
        assertEquals('g', t.run(arr, 1));
    }

    /**
     * @title get char from array
     */
    public void testN2() {
        T_aget_char_1 t = new T_aget_char_1();
        char[] arr = new char[2];
        arr[0] = 'g';
        assertEquals('g', t.run(arr, 0));
    }
    
    /**
     * @title Type of index argument - float. Dalvik doens't distinguish 32-bits types internally,
     * so this array[float] makes no sense but shall not crash the VM.  
     */

    public void testN3() {
        char[] arr = new char[2];
        T_aget_char_8 t = new T_aget_char_8();
        try {
            t.run(arr, 3.14f);
        } catch (Throwable e) {
        }
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException
     */
    public void testE1() {
        T_aget_char_1 t = new T_aget_char_1();
        char[] arr = new char[2];
        try {
            t.run(arr, 2);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    /**
     * @title expected NullPointerException
     */
    public void testE2() {
        T_aget_char_1 t = new T_aget_char_1();
        try {
            t.run(null, 2);
            fail("expected NullPointerException");
        } catch (NullPointerException aie) {
            // expected
        }
    }

    /**
     * @title expected ArrayIndexOutOfBoundsException (negative index)
     */
    public void testE3() {
        T_aget_char_1 t = new T_aget_char_1();
        char[] arr = new char[2];
        try {
            t.run(arr, -1);
            fail("expected ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException aie) {
            // expected
        }
    }

    

    /**
     * @constraint B1 
     * @title types of arguments - array, double
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - array, long
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - Object, char
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - double[], char
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - int[], int
     */
    public void testVFE5() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - array, reference
     */
    public void testVFE6() {
        try {
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_7");
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
            Class.forName("dot.junit.opcodes.aget_char.d.T_aget_char_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
