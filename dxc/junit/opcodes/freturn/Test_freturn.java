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

package dxc.junit.opcodes.freturn;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.freturn.jm.T_freturn_1;
import dxc.junit.opcodes.freturn.jm.T_freturn_6;
import dxc.junit.opcodes.freturn.jm.T_freturn_7;
import dxc.junit.opcodes.freturn.jm.T_freturn_8;
import dxc.junit.opcodes.freturn.jm.T_freturn_9;

public class Test_freturn extends DxTestCase {

    /**
     * @title  simple
     */
    public void testN1() {
        T_freturn_1 t = new T_freturn_1();
        assertEquals(123456f, t.run());
    }

    /**
     * @title  check that frames are discarded and reinstananted correctly
     */
    public void testN2() {
        T_freturn_6 t = new T_freturn_6();
        assertEquals(123456f, t.run());
    }

    /**
     * @title  check that monitor is released by freturn
     */
    public void testN3() {
        assertTrue(T_freturn_7.execute());
    }

    /**
     * @title  Method is synchronized but thread is not monitor owner
     */
    public void testE1() {
        T_freturn_8 t = new T_freturn_8();
        try {
            assertTrue(t.run());
            fail("expected IllegalMonitorStateException");
        } catch (IllegalMonitorStateException imse) {
            // expected
        }
    }

    /**
     * @title  Lock structural rule 1 is violated
     */
    public void testE2() {
        T_freturn_9 t = new T_freturn_9();
        try {
            assertEquals(1f, t.run());
            // the JVM spec says that it is optional to implement the structural
            // lock rules, see JVM spec 8.13 and monitorenter/exit opcodes.
            System.out.print("dvmvfe:");
            //fail("expected IllegalMonitorStateException");
        } catch (IllegalMonitorStateException imse) {
            // expected
        }
    }


    /**
     * @constraint 4.8.2.14
     * @title method's return type - void
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.14
     * @title method's return type - double
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - double
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_5");
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
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_10");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - reference
     */
    public void testVFE6() {
        try {
            Class.forName("dxc.junit.opcodes.freturn.jm.T_freturn_11");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
