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

package dot.junit.opcodes.invoke_virtual_range;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_1;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_10;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_14;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_15;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_17;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_18;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_19;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_2;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_20;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_4;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_5;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_6;
import dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_7;

public class Test_invoke_virtual_range extends DxTestCase {

    /**
     * @title invoke virtual method
     */
    public void testN1() {
        T_invoke_virtual_range_1 t = new T_invoke_virtual_range_1();
        int a = 1;
        String sa = "a" + a;
        String sb = "a1";
        assertTrue(t.run(sa, sb));
        assertFalse(t.run(t, sa));
        assertFalse(t.run(sb, t));
    }

    /**
     * @title Invoke protected method of superclass
     */
    public void testN3() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_7
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
        T_invoke_virtual_range_7 t = new T_invoke_virtual_range_7();
        assertEquals(5, t.run());
    }

    /**
     * @title Check that new frame is created by invoke_virtual_range and
     * arguments are passed to method
     */
    public void testN5() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_14
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
        T_invoke_virtual_range_14 t = new T_invoke_virtual_range_14();
        assertTrue(t.run());
    }

    /**
     * @title Recursion of method lookup procedure
     */
    public void testN6() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_17
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
        T_invoke_virtual_range_17 t = new T_invoke_virtual_range_17();
        assertEquals(5, t.run());
    }

    /**
     * @title Big number of arguments
     */
    public void testN7() {
        T_invoke_virtual_range_2 t = new T_invoke_virtual_range_2();
        assertEquals(1, t.run());
    }

    /**
     * @title expected NullPointerException
     */
    public void testE1() {
        T_invoke_virtual_range_1 t = new T_invoke_virtual_range_1();
        String s = "s";
        try {
            t.run(null, s);
            fail("expected NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * @title Native method can't be linked
     */
    public void testE2() {
        T_invoke_virtual_range_4 t = new T_invoke_virtual_range_4();
        try {
            t.run();
            fail("expected UnsatisfiedLinkError");
        } catch (UnsatisfiedLinkError ule) {
            // expected
        }
    }

    /**
     * @title Attempt to invoke abstract method
     */
    public void testE4() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_6
        //@uses dot.junit.opcodes.invoke_virtual_range.ATest
        T_invoke_virtual_range_6 t = new T_invoke_virtual_range_6();
        try {
            t.run();
            fail("expected AbstractMethodError");
        } catch (AbstractMethodError iae) {
            // expected
        }
    }

    /**
     * @constraint A14
     * @title invalid constant pool index
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint A15
     * @title &lt;clinit&gt; may not be called using invoke-virtual
     */
    public void testVFE3() {
        try {
            new T_invoke_virtual_range_10().run();
            fail("expected IncompatibleClassChangeError");
        } catch (IncompatibleClassChangeError t) {
        }
    }

    /**
     * @constraint B1
     * @title number of arguments passed to method
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_11");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B9
     * @title types of arguments passed to method
     */
    public void testVFE5() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_12");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint A15
     * @title &lt;init&gt; may not be called using invoke_virtual_range
     */
    public void testVFE6() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_16");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B10
     * @title assignment incompatible references when accessing
     *                  protected method
     */
    public void testVFE8() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_22
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TPlain
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_22");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B10
     * @title assignment incompatible references when accessing
     *                  public method
     */
    public void testVFE9() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_23
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper2
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_23");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint n/a
     * @title Attempt to call static method.
     */
    public void testVFE10() {
         try {
             new T_invoke_virtual_range_5().run();
             fail("expected IncompatibleClassChangeError");
         } catch (IncompatibleClassChangeError t) {
         }
    }


    /**
     * @constraint n/a
     * @title Attempt to invoke non-existing method.
     */
    public void testVFE12() {
         try {
             new T_invoke_virtual_range_15().run();
             fail("expected NoSuchMethodError");
         } catch (NoSuchMethodError t) {
         }
    }

    /**
     * @constraint n/a
     * @title Attempt to invoke private method of other class.
     */
    public void testVFE13() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_18
        //@uses dot.junit.opcodes.invoke_virtual_range.TestStubs
         try {
             new T_invoke_virtual_range_18().run(new TestStubs());
             fail("expected IllegalAccessError");
         } catch (IllegalAccessError t) {
         }
    }

    /**
     * @constraint B12
     * @title Attempt to invoke protected method of unrelated class.
     */
    public void testVFE14() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_20
        //@uses dot.junit.opcodes.invoke_virtual_range.TestStubs
         try {
             new T_invoke_virtual_range_20().run(new TestStubs());
             fail("expected IllegalAccessError");
         } catch (IllegalAccessError t) {
         }
    }

    /**
     * @constraint n/a
     * @title Method has different signature.
     */
    public void testVFE15() {
        //@uses dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_19
        //@uses dot.junit.opcodes.invoke_virtual_range.d.TSuper
         try {
             new T_invoke_virtual_range_19().run();
             fail("expected NoSuchMethodError");
         } catch (NoSuchMethodError t) {
         }
    }

    /**
     * @constraint n/a
     * @title invoke-virtual/range shall be used to invoke private methods
     */
    public void testVFE16() {
         try {
             Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_13");
             fail("expected a verification exception");
         } catch (Throwable t) {
             DxUtil.checkVerifyException(t);
         }
    }

    /**
     * @constraint A23
     * @title number of registers
     */
    public void testVFE17() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint A14
     * @title attempt to invoke interface method
     */
    public void testVFE18() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_24");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B6
     * @title instance methods may only be invoked on already initialized instances.
     */
    public void testVFE19() {
        try {
            Class.forName("dot.junit.opcodes.invoke_virtual_range.d.T_invoke_virtual_range_25");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
