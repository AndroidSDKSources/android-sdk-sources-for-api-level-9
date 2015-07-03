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

package dot.junit.opcodes.add_long;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.add_long.d.T_add_long_1;
import dot.junit.opcodes.add_long.d.T_add_long_6;

public class Test_add_long extends DxTestCase {
    /**
     * @title Arguments = 12345678l, 87654321l
     */
    public void testN1() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(99999999l, t.run(12345678l, 87654321l));
    }

    /**
     * @title Arguments = 0l, 87654321l
     */
    public void testN2() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(87654321l, t.run(0l, 87654321l));
    }

    /**
     * @title Arguments = -12345678l, 0l
     */
    public void testN3() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-12345678l, t.run(-12345678l, 0l));
    }

    /**
     * @title Types of arguments - long, double. Dalvik doens't distinguish 64-bits types internally,
     * so this sum of long and double makes no sense but shall not crash the VM.  
     */
    public void testN4() {
        T_add_long_6 t = new T_add_long_6();
        try {
            t.run();
        } catch (Throwable e) {
        }
    }
    
    /**
     * @title Arguments = 0 + Long.MAX_VALUE
     */
    public void testB1() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(9223372036854775807L, t.run(0l, Long.MAX_VALUE));
    }

    /**
     * @title Arguments = 0 + Long.MIN_VALUE
     */
    public void testB2() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-9223372036854775808L, t.run(0l, Long.MIN_VALUE));
    }

    /**
     * @title Arguments = 0 + 0
     */
    public void testB3() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(0l, t.run(0l, 0l));
    }

    /**
     * @title Arguments = Long.MAX_VALUE + Long.MAX_VALUE
     */
    public void testB4() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-2, t.run(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    /**
     * @title Arguments = Long.MAX_VALUE + Long.MIN_VALUE
     */
    public void testB5() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-1l, t.run(Long.MAX_VALUE, Long.MIN_VALUE));
    }

    /**
     * @title Arguments = Long.MIN_VALUE + Long.MIN_VALUE
     */
    public void testB6() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(0l, t.run(Long.MIN_VALUE, Long.MIN_VALUE));
    }

    /**
     * @title Arguments = Long.MIN_VALUE + 1
     */
    public void testB7() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-9223372036854775807l, t.run(Long.MIN_VALUE, 1l));
    }

    /**
     * @title Arguments = Long.MAX_VALUE + 1
     */
    public void testB8() {
        T_add_long_1 t = new T_add_long_1();
        assertEquals(-9223372036854775808l, t.run(Long.MAX_VALUE, 1l));
    }


    

    /**
     * @constraint A24 
     * @title number of registers
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.add_long.d.T_add_long_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - long / integer
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.add_long.d.T_add_long_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - long / float
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.add_long.d.T_add_long_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - reference / long
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.add_long.d.T_add_long_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
