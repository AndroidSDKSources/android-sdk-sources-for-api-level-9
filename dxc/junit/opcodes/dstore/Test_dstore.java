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

package dxc.junit.opcodes.dstore;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.dstore.jm.T_dstore_1;
import dxc.junit.opcodes.dstore.jm.T_dstore_1_w;
import dxc.junit.opcodes.dstore.jm.T_dstore_5;
import dxc.junit.opcodes.dstore.jm.T_dstore_5_w;

public class Test_dstore extends DxTestCase {

    /*
     * NORMAL dstore VERSION
     */

    /**
     * @title  dstore 0
     */
    public void testN1() {
        assertEquals(1d, T_dstore_1.run());
    }

    /**
     * @title  dstore 255
     */
    public void testN2() {
        assertEquals(1d, T_dstore_5.run());
    }

    /**
     * @constraint 4.8.1.22
     * @title index must be no greater than the value
     * of max_locals-1
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_2");
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
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_3");
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
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /*
     * WIDE dstore VERSION
     */

    /**
     * @title  dstore_w 0
     */
    public void testN3() {
        assertEquals(1d, T_dstore_1_w.run());
    }

    /**
     * @title  dstore 257
     */
    public void testN4() {
        assertEquals(1d, T_dstore_5_w.run());
    }

    /**
     * @constraint 4.8.1.25
     * @title index must be no greater than the value
     * of max_locals-1
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_2_w");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - float
     */
    public void testVFE5() {
        try {
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_3_w");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title types of argument - long
     */
    public void testVFE6() {
        try {
            Class.forName("dxc.junit.opcodes.dstore.jm.T_dstore_4_w");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
