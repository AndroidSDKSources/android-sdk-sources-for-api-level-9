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

package android.content.pm.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.test.AndroidTestCase;

@TestTargetClass(PermissionGroupInfo.class)
public class PermissionGroupInfoTest extends AndroidTestCase {
    private static final String PERMISSIONGROUP_NAME = "android.permission-group.COST_MONEY";
    private static final String DEFAULT_DISCRIPTION =
        "Allow applications to do things that can cost you money.";

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test describeContents",
            method = "describeContents",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test loadDescription",
            method = "loadDescription",
            args = {android.content.pm.PackageManager.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructors",
            method = "PermissionGroupInfo",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructors",
            method = "PermissionGroupInfo",
            args = {android.content.pm.PermissionGroupInfo.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test toString",
            method = "toString",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test writeToParcel",
            method = "writeToParcel",
            args = {android.os.Parcel.class, int.class}
        )
    })
    public void testPermissionGroupInfo() throws NameNotFoundException {
        PackageManager pm = getContext().getPackageManager();
        Parcel p = Parcel.obtain();
        // Test constructors
        new PermissionGroupInfo();
        PermissionGroupInfo permissionGroupInfo = pm
                .getPermissionGroupInfo(PERMISSIONGROUP_NAME, 0);
        PermissionGroupInfo infoFromExisted = new PermissionGroupInfo(permissionGroupInfo);
        checkInfoSame(permissionGroupInfo, infoFromExisted);

        // Test toString, describeContents, loadDescription
        assertNotNull(permissionGroupInfo.toString());
        assertEquals(0, permissionGroupInfo.describeContents());
        assertEquals(DEFAULT_DISCRIPTION, permissionGroupInfo.loadDescription(pm));

        // Test writeToParcel
        permissionGroupInfo.writeToParcel(p, 0);
        p.setDataPosition(0);
        PermissionGroupInfo infoFromParcel = PermissionGroupInfo.CREATOR.createFromParcel(p);
        checkInfoSame(permissionGroupInfo, infoFromParcel);
        p.recycle();
    }

    private void checkInfoSame(PermissionGroupInfo expected, PermissionGroupInfo actual) {
        assertEquals(expected.descriptionRes, actual.descriptionRes);
        assertEquals(expected.nonLocalizedDescription, actual.nonLocalizedDescription);
    }
}
