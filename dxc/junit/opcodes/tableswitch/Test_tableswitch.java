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

package dxc.junit.opcodes.tableswitch;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.tableswitch.jm.T_tableswitch_1;

public class Test_tableswitch extends DxTestCase {

    /**
     * @title normal test
     */
    public void testN1() {
        T_tableswitch_1 t = new T_tableswitch_1();
        assertEquals(2, t.run(-1));

        assertEquals(-1, t.run(4));
        assertEquals(20, t.run(2));
        assertEquals(-1, t.run(5));

        assertEquals(-1, t.run(6));
        assertEquals(20, t.run(3));
        assertEquals(-1, t.run(7));
    }

    /**
     * @title check Integer.MAX_VALUE
     */
    public void testB1() {
        T_tableswitch_1 t = new T_tableswitch_1();
        assertEquals(-1, t.run(Integer.MAX_VALUE));
    }

    /**
     * @title check Integer.MIN_VALUE
     */
    public void testB2() {
        T_tableswitch_1 t = new T_tableswitch_1();
        assertEquals(-1, t.run(Integer.MIN_VALUE));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title type of argument - float
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.1.8
     * @title branch target shall be inside the
     * method
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.1.8
     * @title branch target shall not be "inside" wide
     * instruction
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.1.8
     * @title low value shall be less than high
     * value
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.1.8
     * @title non-zero padding
     */
    public void testVFE6() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title type of argument - reference
     */
    public void testVFE7() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.1.8
     * @title number of entries in jump table
     */
    public void testVFE8() {
        try {
            Class.forName("dxc.junit.opcodes.tableswitch.jm.T_tableswitch_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
