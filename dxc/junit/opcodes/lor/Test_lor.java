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

package dxc.junit.opcodes.lor;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.lor.jm.T_lor_1;

public class Test_lor extends DxTestCase {

    /**
     * @title Arguments = 123456789121l, 2l
     */
    public void testN1() {
        T_lor_1 t = new T_lor_1();
        assertEquals(123456789123l, t.run(123456789121l, 2l));
    }

    /**
     * @title Arguments = 0xffffffffffffff8l, 0xffffffffffffff1l
     */
    public void testN2() {
        T_lor_1 t = new T_lor_1();
        assertEquals(0xffffffffffffff9l, t.run(0xffffffffffffff8l,
                0xffffffffffffff1l));
    }

    /**
     * @title  Arguments = 0xabcdefabcdef, -1
     */
    public void testN3() {
        T_lor_1 t = new T_lor_1();
        assertEquals(-1l, t.run(0xabcdefabcdefl, -1l));
    }

    /**
     * @title  Arguments = 0, -1
     */
    public void testB1() {
        T_lor_1 t = new T_lor_1();
        assertEquals(-1l, t.run(0l, -1l));
    }

    /**
     * @title  Arguments = Long.MAX_VALUE, Long.MIN_VALUE
     */
    public void testB2() {
        T_lor_1 t = new T_lor_1();
        assertEquals(-1l, t.run(Long.MAX_VALUE, Long.MIN_VALUE));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.lor.jm.T_lor_2");
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
            Class.forName("dxc.junit.opcodes.lor.jm.T_lor_3");
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
            Class.forName("dxc.junit.opcodes.lor.jm.T_lor_4");
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
            Class.forName("dxc.junit.opcodes.lor.jm.T_lor_5");
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
            Class.forName("dxc.junit.opcodes.lor.jm.T_lor_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
