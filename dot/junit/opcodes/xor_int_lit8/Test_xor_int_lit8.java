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

package dot.junit.opcodes.xor_int_lit8;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_1;
import dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_2;
import dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_3;
import dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_4;
import dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_7;

public class Test_xor_int_lit8 extends DxTestCase {
    /**
     * @title Arguments = 15, 8
     */
    public void testN1() {
         T_xor_int_lit8_1 t = new T_xor_int_lit8_1();
         assertEquals(7, t.run());
     }

    /**
     * @title Arguments = 0xfffffff8, 0xf1
     */
    public void testN2() {
         T_xor_int_lit8_2 t = new T_xor_int_lit8_2();
         assertEquals(9, t.run());
     }
    
    /**
     * @title Type of argument - float. Dalvik doens't distinguish 32-bits types internally,
     * so this (float ^ int) makes no sense but shall not crash the VM.  
     */
    public void testN3() {
        T_xor_int_lit8_7 t = new T_xor_int_lit8_7();
        try {
            t.run(3.14f);
        } catch (Throwable e) {
        }
    }
    
    /**
     * @title Arguments = 0, -1
     */
    public void testB1() {
        T_xor_int_lit8_3 t = new T_xor_int_lit8_3();
            assertEquals(-1, t.run());
    }

    /**
     * @title Arguments = Integer.MAX_VALUE, Byte.MIN_VALUE
     */
    public void testB2() {
        T_xor_int_lit8_4 t = new T_xor_int_lit8_4();
            assertEquals(-2147483521, t.run());
    }

    

    /**
     * @constraint B1
     * @title types of arguments - long, int
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title  types of arguments - reference, int
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint A23 
     * @title  number of registers
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.xor_int_lit8.d.T_xor_int_lit8_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
