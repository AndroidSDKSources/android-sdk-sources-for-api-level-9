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

package com.android.email;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/**
 * Unit & small test suites for Email.  This is intended to run all tests that can be handled 
 * locally, without requiring any external email server.
 *
 * To run just this suite from the command line:
 * $ adb shell am instrument -w \
 *   -e class com.android.email.UnitTests \
 *   com.android.email.tests/android.test.InstrumentationTestRunner
 */
public class SmallTests extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(SmallTests.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
