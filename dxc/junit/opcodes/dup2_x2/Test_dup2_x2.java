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

package dxc.junit.opcodes.dup2_x2;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_1;
import dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_2;
import dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_3;
import dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_4;
import dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_5;

public class Test_dup2_x2 extends DxTestCase {

    /**
     * @title  type of argument - int, int, int, int
     */
    public void testN1() {
        T_dup2_x2_1 t = new T_dup2_x2_1();
        assertTrue(t.run());
    }

    /**
     * @title  type of argument - float, float, float, float
     */
    public void testN2() {
        T_dup2_x2_2 t = new T_dup2_x2_2();
        assertTrue(t.run());
    }

    /**
     * @title  type of argument - double, float, int
     */
    public void testN3() {
        T_dup2_x2_3 t = new T_dup2_x2_3();
        assertTrue(t.run());
    }

    /**
     * @title type of argument - int, float, long
     */
    public void testN4() {
        T_dup2_x2_4 t = new T_dup2_x2_4();
        assertTrue(t.run());
    }

    /**
     * @title type of argument - double, long
     */
    public void testN5() {
        T_dup2_x2_5 t = new T_dup2_x2_5();
        assertTrue(t.run());
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.5
     * @title stack size
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.dup2_x2.jm.T_dup2_x2_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
