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

package dxc.junit.opcodes.aload_0;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.aload_0.jm.T_aload_0_1;
import dxc.junit.opcodes.aload_0.jm.T_aload_0_6;

public class Test_aload_0 extends DxTestCase {

    /**
     * @title value is pushed onto the operand stack
     */
    public void testN1() {
        assertEquals("hello", T_aload_0_1.run());
    }

    /**
     * @title equality of aload_<n> and aload <n>
     */
    public void testN2() {
        assertTrue(T_aload_0_6.run());
    }

    /**
     * @constraint 4.8.1.22
     * @title index must be no greater than the value
     * of max_locals-1
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }


    /**
     * @constraint 4.8.2.1
     * @title types of argument - float
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - long
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - int
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.5
     * @title stack size
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.21
     * @title returnAddress may not be loaded from
     * local variable
     */
    public void testVFE6() {
        try {
            Class.forName("dxc.junit.opcodes.aload_0.jm.T_aload_0_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
