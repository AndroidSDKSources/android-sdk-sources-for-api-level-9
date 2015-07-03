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

package dxc.junit.opcodes.lshl;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.lshl.jm.T_lshl_1;

public class Test_lshl extends DxTestCase {

    /**
     * @title Arguments = 5000000000l, 3
     */
    public void testN1() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(40000000000l, t.run(5000000000l, 3));
    }

    /**
     * @title Arguments = 5000000000l, 1
     */
    public void testN2() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(10000000000l, t.run(5000000000l, 1));
    }

    /**
     * @title Arguments = -5000000000l, 1
     */
    public void testN3() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(-10000000000l, t.run(-5000000000l, 1));
    }

    /**
     * @title  Arguments = 1, -1
     */
    public void testN4() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(0x8000000000000000l, t.run(1l, -1));
    }

    /**
     * @title  Verify that shift distance is actually in range 0 to 64.
     */
    public void testN5() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(130l, t.run(65l, 65));
    }

    /**
     * @title  Arguments = 0, -1
     */
    public void testB1() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(0, t.run(0, -1));
    }

    /**
     * @title  Arguments = 1, 0
     */
    public void testB2() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(1, t.run(1, 0));
    }

    /**
     * @title  Arguments = Long.MAX_VALUE, 1
     */
    public void testB3() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(0xfffffffe, t.run(Long.MAX_VALUE, 1));
    }

    /**
     * @title  Arguments = Long.MIN_VALUE, 1
     */
    public void testB4() {
        T_lshl_1 t = new T_lshl_1();
        assertEquals(0l, t.run(Long.MIN_VALUE, 1));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.lshl.jm.T_lshl_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - double & int
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.lshl.jm.T_lshl_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - int & int
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.lshl.jm.T_lshl_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - float & int
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.lshl.jm.T_lshl_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - reference & int
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.lshl.jm.T_lshl_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
