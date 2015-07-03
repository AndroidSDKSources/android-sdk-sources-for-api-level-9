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

package dxc.junit.opcodes.invokestatic.jm;

public class T_invokestatic_12 implements Runnable {
    public final static int CNT = 1000;
    static int value = 0;
    static boolean failed = false;
    
    public void run() {
        for(int i = 0; i < CNT; i++) {
            test();
        }
    }
    
    private synchronized static void test()    {
        value++;
        int c = value;
        Thread.yield();
        if(c != value)
            failed = true;
    }
    
    public static boolean execute() {
        T_invokestatic_12 test = new T_invokestatic_12();
        Thread t1 = new Thread(test);
        Thread t2 = new Thread(test);
        
        t1.start();
        t2.start();
        
        try
        {
            Thread.sleep(5000);
        }
        catch(InterruptedException ie) {
            return false;
        }
        
        if(value != CNT * 2)
            return false;
        return !failed;
    }
}