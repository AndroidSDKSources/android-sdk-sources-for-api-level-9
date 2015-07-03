package dot.junit.opcodes.if_gtz;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.if_gtz.d.T_if_gtz_1;
import dot.junit.opcodes.if_gtz.d.T_if_gtz_2;

public class Test_if_gtz extends DxTestCase {

    /**
     * @title Argument = 5
     */
    public void testN1() {
        T_if_gtz_1 t = new T_if_gtz_1();
        assertEquals(1, t.run(5));
    }

    /**
     * @title Argument = -5
     */
    public void testN2() {
        T_if_gtz_1 t = new T_if_gtz_1();
        assertEquals(1234, t.run(-5));
    }

    /**
     * @title Types of arguments - float. Dalvik doens't distinguish 32-bits types internally,
     * so this operation of float makes no sense but shall not crash the VM.  
     */
    public void testN3() {
        T_if_gtz_2 t = new T_if_gtz_2();
        assertEquals(1, t.run(1.123f));
    }
    
    /**
     * @title Arguments = Integer.MAX_VALUE
     */
    public void testB1() {
        T_if_gtz_1 t = new T_if_gtz_1();
        assertEquals(1, t.run(Integer.MAX_VALUE));
    }

    /**
     * @title Arguments = Integer.MIN_VALUE
     */
    public void testB2() {
        T_if_gtz_1 t = new T_if_gtz_1();
        assertEquals(1234, t.run(Integer.MIN_VALUE));
    }
    
    /**
     * @title Arguments = 0
     */
    public void testB3() {
        T_if_gtz_1 t = new T_if_gtz_1();
        assertEquals(1234, t.run(0));
    }
    
    /**
     * @constraint A23 
     * @title  number of registers
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }


    /**
     * @constraint B1 
     * @title  types of arguments - double
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title  types of arguments - long
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B1 
     * @title  types of arguments - reference
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint A6 
     * @title  branch target shall be inside the method
     */
    public void testVFE5() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint A6 
     * @title  branch target shall not be "inside" instruction
     */
    public void testVFE6() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_9");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint n/a
     * @title  branch must not be 0
     */
    public void testVFE7() {
        try {
            Class.forName("dot.junit.opcodes.if_gtz.d.T_if_gtz_10");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
