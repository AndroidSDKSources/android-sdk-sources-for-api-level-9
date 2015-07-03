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

package dxc.junit.verify.t482_8;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;

/**
 * 
 */
public class Test_t482_8 extends DxTestCase {
    /**
     * @constraint 4.8.2.8
     * @title uninitialized class instance must be on the stack
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.verify.t482_8.jm.T_t482_8_1");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.8
     * @title attempt to initialize initialized class
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.verify.t482_8.jm.T_t482_8_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
