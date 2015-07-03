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

package dxc.junit.opcodes.lsub;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.lsub.jm.T_lsub_1;

public class Test_lsub extends DxTestCase {

    /**
     * @title Arguments = 1111127348242l, 11111111114l
     */
    public void testN1() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(1100016237128l, t.run(1111127348242l, 11111111114l));
    }

    /**
     * @title Arguments = 0, 1111127348242l
     */
    public void testN2() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(-1111127348242l, t.run(0, 1111127348242l));
    }

    /**
     * @title Arguments = 0, -11111111114l
     */
    public void testN3() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(11111111114l, t.run(0, -11111111114l));
    }

    /**
     * @title Arguments = 0l, Long.MAX_VALUE
     */
    public void testB1() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(-9223372036854775807L, t.run(0l, Long.MAX_VALUE));
    }
    /**
     * @title Arguments = 9223372036854775807L, Long.MAX_VALUE
     */
    public void testB2() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(0l, t.run(9223372036854775807L, Long.MAX_VALUE));
    }
    /**
     * @title Arguments = Long.MAX_VALUE, -1l
     */
    public void testB3() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(-9223372036854775808L, t.run(Long.MAX_VALUE, -1l));
    }
    /**
     * @title Arguments = Long.MIN_VALUE, 1l
     */
    public void testB4() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(9223372036854775807L, t.run(Long.MIN_VALUE, 1l));
    }
    /**
     * @title Arguments = 0l, 0l
     */
    public void testB5() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(0l, t.run(0l, 0l));
    }
    /**
     * @title Arguments = 0l, -Long.MIN_VALUE
     */
    public void testB6() {
        T_lsub_1 t = new T_lsub_1();
        assertEquals(-9223372036854775808L, t.run(0l, -Long.MIN_VALUE));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.lsub.jm.T_lsub_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - long, double
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.lsub.jm.T_lsub_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - int, long
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.lsub.jm.T_lsub_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - long, float
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.lsub.jm.T_lsub_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of arguments - long, reference
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.lsub.jm.T_lsub_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
