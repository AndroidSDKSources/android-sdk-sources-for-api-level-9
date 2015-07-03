package dot.junit.opcodes.shr_int_2addr;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_1;
import dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_6;

public class Test_shr_int_2addr extends DxTestCase {

    /**
     * @title 15 >> 1
     */
    public void testN1() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(7, t.run(15, 1));
    }

    /**
     * @title 33 >> 2
     */
    public void testN2() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(8, t.run(33, 2));
    }

    /**
     * @title -15 >> 1
     */
    public void testN3() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(-8, t.run(-15, 1));
    }

    /**
     * @title Arguments = 1 & -1
     */
    public void testN4() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(0, t.run(1, -1));
    }

    /**
     * @title Verify that shift distance is actually in range 0 to 32.
     */
    public void testN5() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(16, t.run(33, 33));
    }
    
    /**
     * @title Types of arguments - float, float. Dalvik doens't distinguish 32-bits types internally,
     * so this operation of float makes no sense but shall not crash the VM.  
     */
    public void testN6() {
        T_shr_int_2addr_6 t = new T_shr_int_2addr_6();
        try {
            t.run(3.14f, 1.2f);
        } catch (Throwable e) {
        }
    }



    /**
     * @title Arguments = 0 & -1
     */
    public void testB1() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(0, t.run(0, -1));
    }

    /**
     * @title Arguments = Integer.MAX_VALUE & 1
     */
    public void testB2() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(0x3FFFFFFF, t.run(Integer.MAX_VALUE, 1));
    }

    /**
     * @title Arguments = Integer.MIN_VALUE & 1
     */
    public void testB3() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(0xc0000000, t.run(Integer.MIN_VALUE, 1));
    }

    /**
     * @title Arguments = 1 & 0
     */
    public void testB4() {
        T_shr_int_2addr_1 t = new T_shr_int_2addr_1();
        assertEquals(1, t.run(1, 0));
    }

    /**
     * @constraint A23 
     * @title number of registers
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    

    /**
     * @constraint B1 
     * @title types of arguments - double, int
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - long, int
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B1 
     * @title types of arguments - reference, int
     */
    public void testVFE4() {
        try {
            Class.forName("dot.junit.opcodes.shr_int_2addr.d.T_shr_int_2addr_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

}
