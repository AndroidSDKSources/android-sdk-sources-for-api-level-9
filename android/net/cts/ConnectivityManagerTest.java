/*
 * Copyright (C) 2009 The Android Open Source Project
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

package android.net.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestTargetClass(ConnectivityManager.class)
public class ConnectivityManagerTest extends AndroidTestCase {

    private static final String TAG = ConnectivityManagerTest.class.getSimpleName();

    private static final String FEATURE_ENABLE_HIPRI = "enableHIPRI";

    public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;
    public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
    private static final int HOST_ADDRESS = 0x7f000001;// represent ip 127.0.0.1
    private ConnectivityManager mCm;
    private WifiManager mWifiManager;
    private PackageManager mPackageManager;
    // must include both mobile data + wifi
    private static final int MIN_NUM_NETWORK_TYPES = 2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mPackageManager = getContext().getPackageManager();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getNetworkInfo",
        args = {int.class}
    )
    public void testGetNetworkInfo() {

        // this test assumes that there are at least two network types.
        assertTrue(mCm.getAllNetworkInfo().length >= MIN_NUM_NETWORK_TYPES);
        NetworkInfo ni = mCm.getNetworkInfo(1);
        State state = ni.getState();
        assertTrue(State.UNKNOWN.ordinal() >= state.ordinal()
                && state.ordinal() >= State.CONNECTING.ordinal());
        DetailedState ds = ni.getDetailedState();
        assertTrue(DetailedState.FAILED.ordinal() >= ds.ordinal()
                && ds.ordinal() >= DetailedState.IDLE.ordinal());

        ni = mCm.getNetworkInfo(0);
        state = ni.getState();
        assertTrue(State.UNKNOWN.ordinal() >= state.ordinal()
                && state.ordinal() >= State.CONNECTING.ordinal());
        ds = ni.getDetailedState();
        assertTrue(DetailedState.FAILED.ordinal() >= ds.ordinal()
                && ds.ordinal() >= DetailedState.IDLE.ordinal());

        ni = mCm.getNetworkInfo(-1);
        assertNull(ni);

    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "isNetworkTypeValid",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getAllNetworkInfo",
            args = {}
        )
    })
    public void testIsNetworkTypeValid() {

        NetworkInfo[] ni = mCm.getAllNetworkInfo();

        for (NetworkInfo n : ni) {
            assertTrue(ConnectivityManager.isNetworkTypeValid(n.getType()));
        }
        assertFalse(ConnectivityManager.isNetworkTypeValid(-1));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test getAllNetworkInfo().",
        method = "getAllNetworkInfo",
        args = {}
    )
    public void testGetAllNetworkInfo() {
        NetworkInfo[] ni = mCm.getAllNetworkInfo();
        assertTrue(ni.length >= MIN_NUM_NETWORK_TYPES);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "startUsingNetworkFeature",
            args = {int.class, java.lang.String.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "stopUsingNetworkFeature",
            args = {int.class, java.lang.String.class}
        )
    })
    public void testStartUsingNetworkFeature() {

        final String invalidateFeature = "invalidateFeature";
        final String mmsFeature = "enableMMS";
        final int failureCode = -1;

        assertEquals(failureCode, mCm.startUsingNetworkFeature(TYPE_MOBILE, invalidateFeature));
        assertEquals(failureCode, mCm.stopUsingNetworkFeature(TYPE_MOBILE, invalidateFeature));

        // Should return failure(-1) because MMS is not supported on WIFI.
        assertEquals(failureCode, mCm.startUsingNetworkFeature(TYPE_WIFI, mmsFeature));
        assertEquals(failureCode, mCm.stopUsingNetworkFeature(TYPE_WIFI, mmsFeature));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "requestRouteToHost",
        args = {int.class, int.class}
    )
    public void testRequestRouteToHost() {

        NetworkInfo[] ni = mCm.getAllNetworkInfo();
        for (NetworkInfo n : ni) {
            // make sure network is up (except WIFI due to always fail)
            if (n.isConnected() && (n.getType() != TYPE_WIFI)) {
                assertTrue(mCm.requestRouteToHost(n.getType(), HOST_ADDRESS));
            }
        }

        assertFalse(mCm.requestRouteToHost(-1, HOST_ADDRESS));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getActiveNetworkInfo",
        args = {}
    )
    @ToBeFixed(bug="1695243", explanation="No Javadoc")
    public void testGetActiveNetworkInfo() {
        NetworkInfo ni = mCm.getActiveNetworkInfo();

        if (ni != null) {
            assertTrue(ni.getType() >= 0);
        }
    }

    @TestTargetNew(
        level = TestLevel.SUFFICIENT,
        method = "getBackgroundDataSetting",
        args = {}
    )
    public void testTest() {
        mCm.getBackgroundDataSetting();
    }

    /** Test that hipri can be brought up when Wifi is enabled. */
    public void testStartUsingNetworkFeature_enableHipri() throws Exception {
        if (!mPackageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
                || !mPackageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            // This test requires a mobile data connection and WiFi.
            return;
        }

        boolean isWifiConnected = mWifiManager.isWifiEnabled()
                && mWifiManager.getConnectionInfo().getSSID() != null;

        try {
            // Make sure WiFi is connected to an access point.
            if (!isWifiConnected) {
                connectToWifi();
            }

            // Register a receiver that will capture the connectivity change for hipri.
            ConnectivityActionReceiver receiver =
                    new ConnectivityActionReceiver(ConnectivityManager.TYPE_MOBILE_HIPRI);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(receiver, filter);

            // Try to start using the hipri feature...
            int result = mCm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,
                    FEATURE_ENABLE_HIPRI);
            assertTrue("Couldn't start using the HIPRI feature.", result != -1);

            // Check that the ConnectivityManager reported that it connected using hipri...
            assertTrue("Couldn't connect using hipri...", receiver.waitForConnection());

            assertTrue("Couldn't requestRouteToHost using HIPRI.",
                    mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI, HOST_ADDRESS));

        } catch (InterruptedException e) {
            fail("Broadcast receiver waiting for ConnectivityManager interrupted.");
        } finally {
            mCm.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,
                    FEATURE_ENABLE_HIPRI);
            if (!isWifiConnected) {
                mWifiManager.setWifiEnabled(false);
            }
        }
    }

    private void connectToWifi() throws InterruptedException {
        ConnectivityActionReceiver receiver =
                new ConnectivityActionReceiver(ConnectivityManager.TYPE_WIFI);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(receiver, filter);

        assertTrue(mWifiManager.setWifiEnabled(true));
        assertTrue("Wifi must be configured to connect to an access point for this test.",
                receiver.waitForConnection());

        mContext.unregisterReceiver(receiver);
    }

    /** Receiver that captures the last connectivity change's network type and state. */
    private class ConnectivityActionReceiver extends BroadcastReceiver {

        private final CountDownLatch mReceiveLatch = new CountDownLatch(1);

        private final int mNetworkType;

        ConnectivityActionReceiver(int networkType) {
            mNetworkType = networkType;
        }

        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getExtras()
                    .getParcelable(ConnectivityManager.EXTRA_NETWORK_INFO);
            int networkType = networkInfo.getType();
            State networkState = networkInfo.getState();
            Log.i(TAG, "Network type: " + networkType + " state: " + networkState);
            if (networkType == mNetworkType && networkInfo.getState() == State.CONNECTED) {
                mReceiveLatch.countDown();
            }
        }

        public boolean waitForConnection() throws InterruptedException {
            return mReceiveLatch.await(10, TimeUnit.SECONDS);
        }
    }
}
