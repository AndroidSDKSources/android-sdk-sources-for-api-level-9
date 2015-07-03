/* //device/content/providers/pim/EventRecurrenceTest.java
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.android.providers.calendar;

import android.pim.EventRecurrence;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Suppress;
import junit.framework.TestCase;

import java.util.Arrays;

public class EventRecurrenceTest extends TestCase {

    @SmallTest
    public void test0() throws Exception {
        verifyRecurType("FREQ=SECONDLY",
                /* int freq */         EventRecurrence.SECONDLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test1() throws Exception {
        verifyRecurType("FREQ=MINUTELY",
                /* int freq */         EventRecurrence.MINUTELY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test2() throws Exception {
        verifyRecurType("FREQ=HOURLY",
                /* int freq */         EventRecurrence.HOURLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test3() throws Exception {
        verifyRecurType("FREQ=DAILY",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test4() throws Exception {
        verifyRecurType("FREQ=WEEKLY",
                /* int freq */         EventRecurrence.WEEKLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test5() throws Exception {
        verifyRecurType("FREQ=MONTHLY",
                /* int freq */         EventRecurrence.MONTHLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test6() throws Exception {
        verifyRecurType("FREQ=YEARLY",
                /* int freq */         EventRecurrence.YEARLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test7() throws Exception {
        // with an until
        verifyRecurType("FREQ=DAILY;UNTIL=112233T223344Z",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     "112233T223344Z",
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test8() throws Exception {
        // with a count
        verifyRecurType("FREQ=DAILY;COUNT=334",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        334,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test9() throws Exception {
        // with a count
        verifyRecurType("FREQ=DAILY;INTERVAL=5000",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     5000,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @SmallTest
    public void test10() throws Exception {
        // verifyRecurType all of the BY* ones with one element
        verifyRecurType("FREQ=DAILY"
                + ";BYSECOND=0"
                + ";BYMINUTE=1"
                + ";BYHOUR=2"
                + ";BYMONTHDAY=30"
                + ";BYYEARDAY=300"
                + ";BYWEEKNO=53"
                + ";BYMONTH=12"
                + ";BYSETPOS=-15"
                + ";WKST=SU",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   new int[]{0},
                /* int[] byminute */   new int[]{1},
                /* int[] byhour */     new int[]{2},
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ new int[]{30},
                /* int[] byyearday */  new int[]{300},
                /* int[] byweekno */   new int[]{53},
                /* int[] bymonth */    new int[]{12},
                /* int[] bysetpos */   new int[]{-15},
                /* int wkst */         EventRecurrence.SU
        );
    }

    @SmallTest
    public void test11() throws Exception {
        // verifyRecurType all of the BY* ones with one element
        verifyRecurType("FREQ=DAILY"
                + ";BYSECOND=0,30,59"
                + ";BYMINUTE=0,41,59"
                + ";BYHOUR=0,4,23"
                + ";BYMONTHDAY=-31,-1,1,31"
                + ";BYYEARDAY=-366,-1,1,366"
                + ";BYWEEKNO=-53,-1,1,53"
                + ";BYMONTH=1,12"
                + ";BYSETPOS=1,2,3,4,500,10000"
                + ";WKST=SU",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   new int[]{0, 30, 59},
                /* int[] byminute */   new int[]{0, 41, 59},
                /* int[] byhour */     new int[]{0, 4, 23},
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ new int[]{-31, -1, 1, 31},
                /* int[] byyearday */  new int[]{-366, -1, 1, 366},
                /* int[] byweekno */   new int[]{-53, -1, 1, 53},
                /* int[] bymonth */    new int[]{1, 12},
                /* int[] bysetpos */   new int[]{1, 2, 3, 4, 500, 10000},
                /* int wkst */         EventRecurrence.SU
        );
    }

    private static class Check {
        Check(String k, int... v) {
            key = k;
            values = v;
        }

        String key;
        int[] values;
    }

    // this is a negative verifyRecurType case to verifyRecurType the range of the numbers accepted
    @SmallTest
    public void test12() throws Exception {
        Check[] checks = new Check[]{
                new Check("BYSECOND", -100, -1, 60, 100),
                new Check("BYMINUTE", -100, -1, 60, 100),
                new Check("BYHOUR", -100, -1, 24, 100),
                new Check("BYMONTHDAY", -100, -32, 0, 32, 100),
                new Check("BYYEARDAY", -400, -367, 0, 367, 400),
                new Check("BYWEEKNO", -100, -54, 0, 54, 100),
                new Check("BYMONTH", -100, -5, 0, 13, 100)
        };

        for (Check ck : checks) {
            for (int n : ck.values) {
                String recur = "FREQ=DAILY;" + ck.key + "=" + n;
                try {
                    EventRecurrence er = new EventRecurrence();
                    er.parse(recur);
                    fail("Negative verifyRecurType failed. "
                            + " parse failed to throw an exception for '"
                            + recur + "'");
                } catch (EventRecurrence.InvalidFormatException e) {
                    // expected
                }
            }
        }
    }

    // verifyRecurType BYDAY
    @SmallTest
    public void test13() throws Exception {
        verifyRecurType("FREQ=DAILY;BYDAY=1SU,-2MO,+33TU,WE,TH,FR,SA",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      new int[]{
                EventRecurrence.SU,
                EventRecurrence.MO,
                EventRecurrence.TU,
                EventRecurrence.WE,
                EventRecurrence.TH,
                EventRecurrence.FR,
                EventRecurrence.SA
        },
                /* int[] bydayNum */   new int[]{1, -2, 33, 0, 0, 0, 0},
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    @Suppress
    // Repro bug #2331761 - this should fail because of the last comma into BYDAY
    public void test14() throws Exception {
        verifyRecurType("FREQ=WEEKLY;WKST=MO;UNTIL=20100129T130000Z;INTERVAL=1;BYDAY=MO,TU,WE,",
                /* int freq */         EventRecurrence.WEEKLY,
                /* String until */     "20100129T130000Z",
                /* int count */        0,
                /* int interval */     1,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      new int[] {
                        EventRecurrence.MO,
                        EventRecurrence.TU,
                        EventRecurrence.WE,
                },
                /* int[] bydayNum */   new int[]{0, 0, 0},
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    // This test should pass
    public void test15() throws Exception {
        verifyRecurType("FREQ=WEEKLY;WKST=MO;UNTIL=20100129T130000Z;INTERVAL=1;"
                + "BYDAY=MO,TU,WE,TH,FR,SA,SU",
                /* int freq */         EventRecurrence.WEEKLY,
                /* String until */     "20100129T130000Z",
                /* int count */        0,
                /* int interval */     1,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      new int[] {
                        EventRecurrence.MO,
                        EventRecurrence.TU,
                        EventRecurrence.WE,
                        EventRecurrence.TH,
                        EventRecurrence.FR,
                        EventRecurrence.SA,
                        EventRecurrence.SU
                },
                /* int[] bydayNum */   new int[]{0, 0, 0, 0, 0, 0, 0},
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    // Sample coming from RFC2445
    public void test16() throws Exception {
        verifyRecurType("FREQ=MONTHLY;BYDAY=MO,TU,WE,TH,FR;BYSETPOS=-1",
                /* int freq */         EventRecurrence.MONTHLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      new int[] {
                        EventRecurrence.MO,
                        EventRecurrence.TU,
                        EventRecurrence.WE,
                        EventRecurrence.TH,
                        EventRecurrence.FR
                },
                /* int[] bydayNum */   new int[] {0, 0, 0, 0, 0},
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   new int[] { -1 },
                /* int wkst */         EventRecurrence.MO
        );
    }

    // Sample coming from RFC2445
    public void test17() throws Exception {
        verifyRecurType("FREQ=DAILY;COUNT=10;INTERVAL=2",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        10,
                /* int interval */     2,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    // Sample coming from RFC2445
    public void test18() throws Exception {
        verifyRecurType("FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10",
                /* int freq */         EventRecurrence.YEARLY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      new int[] {
                        EventRecurrence.SU
                },
                /* int[] bydayNum */   new int[] { -1 },
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    new int[] { 10 },
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    // for your copying pleasure
    public void fakeTestXX() throws Exception {
        verifyRecurType("FREQ=DAILY;",
                /* int freq */         EventRecurrence.DAILY,
                /* String until */     null,
                /* int count */        0,
                /* int interval */     0,
                /* int[] bysecond */   null,
                /* int[] byminute */   null,
                /* int[] byhour */     null,
                /* int[] byday */      null,
                /* int[] bydayNum */   null,
                /* int[] bymonthday */ null,
                /* int[] byyearday */  null,
                /* int[] byweekno */   null,
                /* int[] bymonth */    null,
                /* int[] bysetpos */   null,
                /* int wkst */         EventRecurrence.MO
        );
    }

    private static void cmp(int vlen, int[] v, int[] correct, String name) {
        if ((correct == null && v != null)
                || (correct != null && v == null)) {
            throw new RuntimeException("One is null, one isn't for " + name
                    + ": correct=" + Arrays.toString(correct)
                    + " actual=" + Arrays.toString(v));
        }
        if ((correct == null && vlen != 0)
                || (vlen != (correct == null ? 0 : correct.length))) {
            throw new RuntimeException("Reported length mismatch for " + name
                    + ": correct=" + ((correct == null) ? "null" : correct.length)
                    + " actual=" + vlen);
        }
        if (correct == null) {
            return;
        }
        if (v.length < correct.length) {
            throw new RuntimeException("Array length mismatch for " + name
                    + ": correct=" + Arrays.toString(correct)
                    + " actual=" + Arrays.toString(v));
        }
        for (int i = 0; i < correct.length; i++) {
            if (v[i] != correct[i]) {
                throw new RuntimeException("Array value mismatch for " + name
                        + ": correct=" + Arrays.toString(correct)
                        + " actual=" + Arrays.toString(v));
            }
        }
    }

    private static boolean eq(String a, String b) {
        if ((a == null && b != null) || (a != null && b == null)) {
            return false;
        } else {
            return a == b || a.equals(b);
        }
    }

    private static void verifyRecurType(String recur,
            int freq, String until, int count, int interval,
            int[] bysecond, int[] byminute, int[] byhour,
            int[] byday, int[] bydayNum, int[] bymonthday,
            int[] byyearday, int[] byweekno, int[] bymonth,
            int[] bysetpos, int wkst) {
        EventRecurrence eventRecurrence = new EventRecurrence();
        eventRecurrence.parse(recur);
        if (eventRecurrence.freq != freq
                || !eq(eventRecurrence.until, until)
                || eventRecurrence.count != count
                || eventRecurrence.interval != interval
                || eventRecurrence.wkst != wkst) {
            System.out.println("Error... got:");
            print(eventRecurrence);
            System.out.println("expected:");
            System.out.println("{");
            System.out.println("    freq=" + freq);
            System.out.println("    until=" + until);
            System.out.println("    count=" + count);
            System.out.println("    interval=" + interval);
            System.out.println("    wkst=" + wkst);
            System.out.println("    bysecond=" + Arrays.toString(bysecond));
            System.out.println("    byminute=" + Arrays.toString(byminute));
            System.out.println("    byhour=" + Arrays.toString(byhour));
            System.out.println("    byday=" + Arrays.toString(byday));
            System.out.println("    bydayNum=" + Arrays.toString(bydayNum));
            System.out.println("    bymonthday=" + Arrays.toString(bymonthday));
            System.out.println("    byyearday=" + Arrays.toString(byyearday));
            System.out.println("    byweekno=" + Arrays.toString(byweekno));
            System.out.println("    bymonth=" + Arrays.toString(bymonth));
            System.out.println("    bysetpos=" + Arrays.toString(bysetpos));
            System.out.println("}");
            throw new RuntimeException("Mismatch in fields");
        }
        cmp(eventRecurrence.bysecondCount, eventRecurrence.bysecond, bysecond, "bysecond");
        cmp(eventRecurrence.byminuteCount, eventRecurrence.byminute, byminute, "byminute");
        cmp(eventRecurrence.byhourCount, eventRecurrence.byhour, byhour, "byhour");
        cmp(eventRecurrence.bydayCount, eventRecurrence.byday, byday, "byday");
        cmp(eventRecurrence.bydayCount, eventRecurrence.bydayNum, bydayNum, "bydayNum");
        cmp(eventRecurrence.bymonthdayCount, eventRecurrence.bymonthday, bymonthday, "bymonthday");
        cmp(eventRecurrence.byyeardayCount, eventRecurrence.byyearday, byyearday, "byyearday");
        cmp(eventRecurrence.byweeknoCount, eventRecurrence.byweekno, byweekno, "byweekno");
        cmp(eventRecurrence.bymonthCount, eventRecurrence.bymonth, bymonth, "bymonth");
        cmp(eventRecurrence.bysetposCount, eventRecurrence.bysetpos, bysetpos, "bysetpos");
    }

    private static void print(EventRecurrence er) {
        System.out.println("{");
        System.out.println("    freq=" + er.freq);
        System.out.println("    until=" + er.until);
        System.out.println("    count=" + er.count);
        System.out.println("    interval=" + er.interval);
        System.out.println("    wkst=" + er.wkst);
        System.out.println("    bysecond=" + Arrays.toString(er.bysecond));
        System.out.println("    bysecondCount=" + er.bysecondCount);
        System.out.println("    byminute=" + Arrays.toString(er.byminute));
        System.out.println("    byminuteCount=" + er.byminuteCount);
        System.out.println("    byhour=" + Arrays.toString(er.byhour));
        System.out.println("    byhourCount=" + er.byhourCount);
        System.out.println("    byday=" + Arrays.toString(er.byday));
        System.out.println("    bydayNum=" + Arrays.toString(er.bydayNum));
        System.out.println("    bydayCount=" + er.bydayCount);
        System.out.println("    bymonthday=" + Arrays.toString(er.bymonthday));
        System.out.println("    bymonthdayCount=" + er.bymonthdayCount);
        System.out.println("    byyearday=" + Arrays.toString(er.byyearday));
        System.out.println("    byyeardayCount=" + er.byyeardayCount);
        System.out.println("    byweekno=" + Arrays.toString(er.byweekno));
        System.out.println("    byweeknoCount=" + er.byweeknoCount);
        System.out.println("    bymonth=" + Arrays.toString(er.bymonth));
        System.out.println("    bymonthCount=" + er.bymonthCount);
        System.out.println("    bysetpos=" + Arrays.toString(er.bysetpos));
        System.out.println("    bysetposCount=" + er.bysetposCount);
        System.out.println("}");
    }
}

