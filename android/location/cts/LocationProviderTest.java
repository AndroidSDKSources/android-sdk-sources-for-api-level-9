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

package android.location.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.test.AndroidTestCase;

@TestTargetClass(LocationProvider.class)
public class LocationProviderTest extends AndroidTestCase {
    private LocationManager mLocationManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLocationManager =
            (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getName",
        args = {}
    )
    public void testGetName() {
        String name = "gps";
        LocationProvider locationProvider = mLocationManager.getProvider(name);
        assertEquals(name, locationProvider.getName());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "meetsCriteria",
        args = {android.location.Criteria.class}
    )
    public void testMeetsCriteria() {
        LocationProvider locationProvider = mLocationManager.getProvider("gps");

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        assertTrue(locationProvider.meetsCriteria(criteria));
    }
}
