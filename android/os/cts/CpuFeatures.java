/*
 * Copyright (C) 2010 The Android Open Source Project
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

package android.os.cts;

public class CpuFeatures {

    public static final String ARMEABI_V7 = "armeabi-v7a";

    public static final String ARMEABI = "armeabi";

    static {
        System.loadLibrary("cts_jni");
    }

    public static native boolean isArmCpu();

    public static native boolean isArm7Compatible();
}
