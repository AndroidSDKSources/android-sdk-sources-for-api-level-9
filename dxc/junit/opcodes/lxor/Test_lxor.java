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

package dxc.junit.opcodes.lxor;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.lxor.jm.T_lxor_1;

public class Test_lxor extends DxTestCase {

    /**
     * @title Arguments = 23423432423777l, 23423432423778l
     */
    public void testN1() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(3, t.run(23423432423777l, 23423432423778l));
    }

    /**
     * @title Arguments = 0xfffffff5, 0xfffffff1
     */
    public void testN2() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(4, t.run(0xfffffff5, 0xfffffff1));
    }

    /**
     * @title  Arguments = 0xABCDEFAB & -1
     */
    public void testN3() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(0x54321054, t.run(0xABCDEFAB, -1l));
    }

    /**
     * @title  Arguments = 0 & -1
     */
    public void testB1() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(-1l, t.run(0l, -1l));
    }

    /**
     * @title  Arguments = Long.MAX_VALUE & Long.MIN_VALUE
     */
    public void testB2() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(0xffffffff, t.run(Long.MAX_VALUE, Long.MIN_VALUE));
    }

    /**
     * @title  Arguments = Long.MAX_VALUE & Long.MAX_VALUE
     */
    public void testB3() {
        T_lxor_1 t = new T_lxor_1();
        assertEquals(0l, t.run(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.lxor.jm.T_lxor_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - double & long
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.lxor.jm.T_lxor_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - int & long
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.lxor.jm.T_lxor_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - float & long
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.lxor.jm.T_lxor_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - reference & long
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.lxor.jm.T_lxor_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
