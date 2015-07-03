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

package android.bluetooth;

import android.bluetooth.BluetoothHeadset.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import junit.framework.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothTestUtils extends Assert {

    /**
     * Timeout for {@link BluetoothAdapter#disable()} in ms.
     */
    private static final int DISABLE_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothAdapter#enable()} in ms.
     */
    private static final int ENABLE_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothAdapter#setScanMode(int)} in ms.
     */
    private static final int SET_SCAN_MODE_TIMEOUT = 5000;

    /**
     * Timeout for {@link BluetoothAdapter#startDiscovery()} in ms.
     */
    private static final int START_DISCOVERY_TIMEOUT = 5000;

    /**
     * Timeout for {@link BluetoothAdapter#cancelDiscovery()} in ms.
     */
    private static final int CANCEL_DISCOVERY_TIMEOUT = 5000;

    /**
     * Timeout for {@link BluetoothDevice#createBond()} in ms.
     */
    private static final int PAIR_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothDevice#removeBond()} in ms.
     */
    private static final int UNPAIR_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothA2dp#connectSink(BluetoothDevice)} in ms.
     */
    private static final int CONNECT_A2DP_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothA2dp#disconnectSink(BluetoothDevice)} in ms.
     */
    private static final int DISCONNECT_A2DP_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothHeadset#connectHeadset(BluetoothDevice)} in ms.
     */
    private static final int CONNECT_HEADSET_TIMEOUT = 20000;

    /**
     * Timeout for {@link BluetoothHeadset#disconnectHeadset(BluetoothDevice)} in ms.
     */
    private static final int DISCONNECT_HEADSET_TIMEOUT = 20000;

    /**
     * Time between polls in ms.
     */
    private static final int POLL_TIME = 100;

    private Context mContext;

    private BufferedWriter mOutputWriter;

    private BluetoothA2dp mA2dp;

    private BluetoothHeadset mHeadset;

    private String mOutputFile;
    private String mTag;
    private class HeadsetServiceListener implements ServiceListener {
        private boolean mConnected = false;

        public void onServiceConnected() {
            synchronized (this) {
                mConnected = true;
            }
        }

        public void onServiceDisconnected() {
            synchronized (this) {
                mConnected = false;
            }
        }

        public boolean isConnected() {
            synchronized (this) {
                return mConnected;
            }
        }
    }

    private HeadsetServiceListener mHeadsetServiceListener = new HeadsetServiceListener();

    private class BluetoothReceiver extends BroadcastReceiver {
        private static final int DISCOVERY_STARTED_FLAG = 1;
        private static final int DISCOVERY_FINISHED_FLAG = 1 << 1;
        private static final int SCAN_MODE_NONE_FLAG = 1 << 2;
        private static final int SCAN_MODE_CONNECTABLE_FLAG = 1 << 3;
        private static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE_FLAG = 1 << 4;
        private static final int STATE_OFF_FLAG = 1 << 5;
        private static final int STATE_TURNING_ON_FLAG = 1 << 6;
        private static final int STATE_ON_FLAG = 1 << 7;
        private static final int STATE_TURNING_OFF_FLAG = 1 << 8;
        private static final int PROFILE_A2DP_FLAG = 1 << 9;
        private static final int PROFILE_HEADSET_FLAG = 1 << 10;

        private static final int A2DP_STATE_DISCONNECTED = 1;
        private static final int A2DP_STATE_CONNECTING = 1 << 1;
        private static final int A2DP_STATE_CONNECTED = 1 << 2;
        private static final int A2DP_STATE_DISCONNECTING = 1 << 3;
        private static final int A2DP_STATE_PLAYING = 1 << 4;

        private static final int HEADSET_STATE_DISCONNECTED = 1;
        private static final int HEADSET_STATE_CONNECTING = 1 << 1;
        private static final int HEADSET_STATE_CONNECTED = 1 << 2;

        private int mFiredFlags = 0;
        private int mA2dpFiredFlags = 0;
        private int mHeadsetFiredFlags = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                    mFiredFlags |= DISCOVERY_STARTED_FLAG;
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    mFiredFlags |= DISCOVERY_FINISHED_FLAG;
                } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction())) {
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                            BluetoothAdapter.ERROR);
                    assertNotSame(mode, BluetoothAdapter.ERROR);
                    switch (mode) {
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            mFiredFlags |= SCAN_MODE_NONE_FLAG;
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            mFiredFlags |= SCAN_MODE_CONNECTABLE_FLAG;
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            mFiredFlags |= SCAN_MODE_CONNECTABLE_DISCOVERABLE_FLAG;
                            break;
                    }
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    assertNotSame(state, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            mFiredFlags |= STATE_OFF_FLAG;
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            mFiredFlags |= STATE_TURNING_ON_FLAG;
                            break;
                        case BluetoothAdapter.STATE_ON:
                            mFiredFlags |= STATE_ON_FLAG;
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            mFiredFlags |= STATE_TURNING_OFF_FLAG;
                            break;
                    }
                } else if (BluetoothA2dp.ACTION_SINK_STATE_CHANGED.equals(intent.getAction())) {
                    mFiredFlags |= PROFILE_A2DP_FLAG;
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_SINK_STATE, -1);
                    assertNotSame(state, -1);
                    switch (state) {
                        case BluetoothA2dp.STATE_DISCONNECTED:
                            mA2dpFiredFlags |= A2DP_STATE_DISCONNECTED;
                            break;
                        case BluetoothA2dp.STATE_CONNECTING:
                            mA2dpFiredFlags |= A2DP_STATE_CONNECTING;
                            break;
                        case BluetoothA2dp.STATE_CONNECTED:
                            mA2dpFiredFlags |= A2DP_STATE_CONNECTED;
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTING:
                            mA2dpFiredFlags |= A2DP_STATE_DISCONNECTING;
                            break;
                        case BluetoothA2dp.STATE_PLAYING:
                            mA2dpFiredFlags |= A2DP_STATE_PLAYING;
                            break;
                    }
                } else if (BluetoothHeadset.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                    mFiredFlags |= PROFILE_HEADSET_FLAG;
                    int state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE,
                            BluetoothHeadset.STATE_ERROR);
                    assertNotSame(state, BluetoothHeadset.STATE_ERROR);
                    switch (state) {
                        case BluetoothHeadset.STATE_DISCONNECTED:
                            mHeadsetFiredFlags |= HEADSET_STATE_DISCONNECTED;
                            break;
                        case BluetoothHeadset.STATE_CONNECTING:
                            mHeadsetFiredFlags |= HEADSET_STATE_CONNECTING;
                            break;
                        case BluetoothHeadset.STATE_CONNECTED:
                            mHeadsetFiredFlags |= HEADSET_STATE_CONNECTED;
                            break;
                    }
                }
            }
        }

        public int getFiredFlags() {
            synchronized (this) {
                return mFiredFlags;
            }
        }

        public int getA2dpFiredFlags() {
            synchronized (this) {
                return mA2dpFiredFlags;
            }
        }

        public int getHeadsetFiredFlags() {
            synchronized (this) {
                return mHeadsetFiredFlags;
            }
        }

        public void resetFiredFlags() {
            synchronized (this) {
                mFiredFlags = 0;
                mA2dpFiredFlags = 0;
                mHeadsetFiredFlags = 0;
            }
        }
    }

    private BluetoothReceiver mBluetoothReceiver = new BluetoothReceiver();

    private class PairReceiver extends BroadcastReceiver {
        private final static int PAIR_FLAG = 1;
        private static final int PAIR_STATE_BONDED = 1;
        private static final int PAIR_STATE_BONDING = 1 << 1;
        private static final int PAIR_STATE_NONE = 1 << 2;

        private int mFiredFlags = 0;
        private int mPairFiredFlags = 0;

        private BluetoothDevice mDevice;
        private int mPasskey;
        private byte[] mPin;

        public PairReceiver(BluetoothDevice device, int passkey, byte[] pin) {
            super();
            mDevice = device;
            mPasskey = passkey;
            mPin = pin;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())
                        && mDevice.equals(intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE))) {
                    int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
                            BluetoothDevice.ERROR);
                    assertNotSame(type, BluetoothDevice.ERROR);
                    switch (type) {
                        case BluetoothDevice.PAIRING_VARIANT_PIN:
                            mDevice.setPin(mPin);
                            break;
                        case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                            mDevice.setPasskey(mPasskey);
                            break;
                        case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                        case BluetoothDevice.PAIRING_VARIANT_CONSENT:
                            mDevice.setPairingConfirmation(true);
                            break;
                        case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                            mDevice.setRemoteOutOfBandData();
                            break;
                    }
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())
                        && mDevice.equals(intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE))) {
                    mFiredFlags |= PAIR_FLAG;
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                            BluetoothDevice.ERROR);
                    assertNotSame(state, BluetoothDevice.ERROR);
                    switch (state) {
                        case BluetoothDevice.BOND_BONDED:
                            mPairFiredFlags |= PAIR_STATE_BONDED;
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            mPairFiredFlags |= PAIR_STATE_BONDING;
                            break;
                        case BluetoothDevice.BOND_NONE:
                            mPairFiredFlags |= PAIR_STATE_NONE;
                            break;
                    }
                }
            }
        }

        public int getFiredFlags() {
            synchronized (this) {
                return mFiredFlags;
            }
        }

        public int getPairFiredFlags() {
            synchronized (this) {
                return mPairFiredFlags;
            }
        }

        public void resetFiredFlags() {
            synchronized (this) {
                mFiredFlags = 0;
                mPairFiredFlags = 0;
            }
        }
    }

    private List<BroadcastReceiver> mReceivers = new ArrayList<BroadcastReceiver>();

    public BluetoothTestUtils(Context context, String tag) {
        this(context, tag, null);
    }

    public BluetoothTestUtils(Context context, String tag, String outputFile) {
        mContext = context;
        mTag = tag;
        mOutputFile = outputFile;

        if (mOutputFile == null) {
            mOutputWriter = null;
        } else {
            try {
                mOutputWriter = new BufferedWriter(new FileWriter(new File(
                        Environment.getExternalStorageDirectory(), mOutputFile), true));
            } catch (IOException e) {
                Log.w(mTag, "Test output file could not be opened", e);
                mOutputWriter = null;
            }
        }

        mA2dp = new BluetoothA2dp(mContext);
        mHeadset = new BluetoothHeadset(mContext, mHeadsetServiceListener);
        mBluetoothReceiver = getBluetoothReceiver(mContext);
        mReceivers.add(mBluetoothReceiver);
    }

    public void close() {
        while (!mReceivers.isEmpty()) {
            mContext.unregisterReceiver(mReceivers.remove(0));
        }

        if (mOutputWriter != null) {
            try {
                mOutputWriter.close();
            } catch (IOException e) {
                Log.w(mTag, "Test output file could not be closed", e);
            }
        }
    }

    public void enable(BluetoothAdapter adapter) {
        int mask = (BluetoothReceiver.STATE_TURNING_ON_FLAG | BluetoothReceiver.STATE_ON_FLAG
                | BluetoothReceiver.SCAN_MODE_CONNECTABLE_FLAG);
        mBluetoothReceiver.resetFiredFlags();

        int state = adapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                assertTrue(adapter.isEnabled());
                return;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                assertFalse(adapter.isEnabled());
                assertTrue(adapter.enable());
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                assertFalse(adapter.isEnabled());
                mask = 0; // Don't check for received intents since we might have missed them.
                break;
            default:
                fail("enable() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < ENABLE_TIMEOUT) {
            state = adapter.getState();
            if (state == BluetoothAdapter.STATE_ON) {
                assertTrue(adapter.isEnabled());
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("enable() completed in %d ms",
                            (System.currentTimeMillis() - s)));
                    return;
                }
            } else {
                assertFalse(adapter.isEnabled());
                assertEquals(BluetoothAdapter.STATE_TURNING_ON, state);
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("enable() timeout: state=%d (expected %d), flags=0x%x (expected 0x%x)",
                state, BluetoothAdapter.STATE_ON, firedFlags, mask));
    }

    public void disable(BluetoothAdapter adapter) {
        int mask = (BluetoothReceiver.STATE_TURNING_OFF_FLAG | BluetoothReceiver.STATE_OFF_FLAG
                | BluetoothReceiver.SCAN_MODE_NONE_FLAG);
        mBluetoothReceiver.resetFiredFlags();

        int state = adapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                assertFalse(adapter.isEnabled());
                return;
            case BluetoothAdapter.STATE_ON:
                assertTrue(adapter.isEnabled());
                assertTrue(adapter.disable());
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                assertFalse(adapter.isEnabled());
                assertTrue(adapter.disable());
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                assertFalse(adapter.isEnabled());
                mask = 0; // Don't check for received intents since we might have missed them.
                break;
            default:
                fail("disable() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < DISABLE_TIMEOUT) {
            state = adapter.getState();
            if (state == BluetoothAdapter.STATE_OFF) {
                assertFalse(adapter.isEnabled());
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("disable() completed in %d ms",
                            (System.currentTimeMillis() - s)));
                    return;
                }
            } else {
                assertFalse(adapter.isEnabled());
                assertEquals(BluetoothAdapter.STATE_TURNING_OFF, state);
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("disable() timeout: state=%d (expected %d), flags=0x%x (expected 0x%x)",
                state, BluetoothAdapter.STATE_OFF, firedFlags, mask));
    }

    public void discoverable(BluetoothAdapter adapter) {
        int mask = BluetoothReceiver.SCAN_MODE_CONNECTABLE_DISCOVERABLE_FLAG;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("discoverable() bluetooth not enabled");
        }

        int scanMode = adapter.getScanMode();
        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            return;
        }

        assertEquals(scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        assertTrue(adapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE));

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < SET_SCAN_MODE_TIMEOUT) {
            scanMode = adapter.getScanMode();
            if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("discoverable() completed in %d ms",
                            (System.currentTimeMillis() - s)));
                    return;
                }
            } else {
                assertEquals(scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE);
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("discoverable() timeout: scanMode=%d (expected %d), flags=0x%x "
                + "(expected 0x%x)", scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,
                firedFlags, mask));
    }

    public void undiscoverable(BluetoothAdapter adapter) {
        int mask = BluetoothReceiver.SCAN_MODE_CONNECTABLE_FLAG;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("undiscoverable() bluetooth not enabled");
        }

        int scanMode = adapter.getScanMode();
        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            return;
        }

        assertEquals(scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        assertTrue(adapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE));

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < SET_SCAN_MODE_TIMEOUT) {
            scanMode = adapter.getScanMode();
            if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("undiscoverable() completed in %d ms",
                            (System.currentTimeMillis() - s)));
                    return;
                }
            } else {
                assertEquals(scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("undiscoverable() timeout: scanMode=%d (expected %d), flags=0x%x "
                + "(expected 0x%x)", scanMode, BluetoothAdapter.SCAN_MODE_CONNECTABLE, firedFlags,
                mask));
    }

    public void startScan(BluetoothAdapter adapter) {
        int mask = BluetoothReceiver.DISCOVERY_STARTED_FLAG;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("startScan() bluetooth not enabled");
        }

        if (adapter.isDiscovering()) {
            return;
        }

        assertTrue(adapter.startDiscovery());

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < START_DISCOVERY_TIMEOUT) {
            if (adapter.isDiscovering() && ((mBluetoothReceiver.getFiredFlags() & mask) == mask)) {
                mBluetoothReceiver.resetFiredFlags();
                writeOutput(String.format("startScan() completed in %d ms",
                        (System.currentTimeMillis() - s)));
                return;
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("startScan() timeout: isDiscovering=%b, flags=0x%x (expected 0x%x)",
                adapter.isDiscovering(), firedFlags, mask));
    }

    public void stopScan(BluetoothAdapter adapter) {
        int mask = BluetoothReceiver.DISCOVERY_FINISHED_FLAG;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("stopScan() bluetooth not enabled");
        }

        if (!adapter.isDiscovering()) {
            return;
        }

        // TODO: put assertTrue() around cancelDiscovery() once it starts returning true.
        adapter.cancelDiscovery();

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < CANCEL_DISCOVERY_TIMEOUT) {
            if (!adapter.isDiscovering() && ((mBluetoothReceiver.getFiredFlags() & mask) == mask)) {
                mBluetoothReceiver.resetFiredFlags();
                writeOutput(String.format("stopScan() completed in %d ms",
                        (System.currentTimeMillis() - s)));
                return;
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("stopScan() timeout: isDiscovering=%b, flags=0x%x (expected 0x%x)",
                adapter.isDiscovering(), firedFlags, mask));

    }

    public void pair(BluetoothAdapter adapter, BluetoothDevice device, int passkey, byte[] pin) {
        pairOrAcceptPair(adapter, device, passkey, pin, true);
    }

    public void acceptPair(BluetoothAdapter adapter, BluetoothDevice device, int passkey,
            byte[] pin) {
        pairOrAcceptPair(adapter, device, passkey, pin, false);
    }

    private void pairOrAcceptPair(BluetoothAdapter adapter, BluetoothDevice device, int passkey,
            byte[] pin, boolean pair) {
        String methodName = pair ? "pair()" : "acceptPair()";
        int mask = PairReceiver.PAIR_FLAG;
        int pairMask = PairReceiver.PAIR_STATE_BONDING | PairReceiver.PAIR_STATE_BONDED;

        PairReceiver pairReceiver = getPairReceiver(mContext, device, passkey, pin);
        mReceivers.add(pairReceiver);

        if (!adapter.isEnabled()) {
            fail(methodName + " bluetooth not enabled");
        }

        int state = device.getBondState();
        switch (state) {
            case BluetoothDevice.BOND_BONDED:
                assertTrue(adapter.getBondedDevices().contains(device));
                return;
            case BluetoothDevice.BOND_BONDING:
                // Don't check for received intents since we might have missed them.
                mask = pairMask = 0;
                break;
            case BluetoothDevice.BOND_NONE:
                assertFalse(adapter.getBondedDevices().contains(device));
                if (pair) {
                    assertTrue(device.createBond());
                }
                break;
            default:
                fail(methodName + " invalide state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < PAIR_TIMEOUT) {
            state = device.getBondState();
            if (state == BluetoothDevice.BOND_BONDED) {
                assertTrue(adapter.getBondedDevices().contains(device));
                if ((pairReceiver.getFiredFlags() & mask) == mask
                        && (pairReceiver.getPairFiredFlags() & pairMask) == pairMask) {
                    writeOutput(String.format("%s completed in %d ms: device=%s",
                            methodName, (System.currentTimeMillis() - s), device));
                    mReceivers.remove(pairReceiver);
                    mContext.unregisterReceiver(pairReceiver);
                    return;
                }
            }
            sleep(POLL_TIME);
        }

        int firedFlags = pairReceiver.getFiredFlags();
        int pairFiredFlags = pairReceiver.getPairFiredFlags();
        pairReceiver.resetFiredFlags();
        fail(String.format("%s timeout: state=%d (expected %d), flags=0x%x (expected 0x%x), "
                + "pairFlags=0x%x (expected 0x%x)", methodName, state, BluetoothDevice.BOND_BONDED,
                firedFlags, mask, pairFiredFlags, pairMask));
    }

    public void unpair(BluetoothAdapter adapter, BluetoothDevice device) {
        int mask = PairReceiver.PAIR_FLAG;
        int pairMask = PairReceiver.PAIR_STATE_NONE;

        PairReceiver pairReceiver = getPairReceiver(mContext, device, 0, null);
        mReceivers.add(pairReceiver);

        if (!adapter.isEnabled()) {
            fail("unpair() bluetooth not enabled");
        }

        int state = device.getBondState();
        switch (state) {
            case BluetoothDevice.BOND_BONDED:
                assertTrue(adapter.getBondedDevices().contains(device));
                assertTrue(device.removeBond());
                break;
            case BluetoothDevice.BOND_BONDING:
                assertTrue(device.removeBond());
                break;
            case BluetoothDevice.BOND_NONE:
                assertFalse(adapter.getBondedDevices().contains(device));
                return;
            default:
                fail("unpair() invalid state: state=" + state);
        }

        assertTrue(device.removeBond());

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < UNPAIR_TIMEOUT) {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                assertFalse(adapter.getBondedDevices().contains(device));
                if ((pairReceiver.getFiredFlags() & mask) == mask
                       && (pairReceiver.getPairFiredFlags() & pairMask) == pairMask) {
                    writeOutput(String.format("unpair() completed in %d ms: device=%s",
                            (System.currentTimeMillis() - s), device));
                    mReceivers.remove(pairReceiver);
                    mContext.unregisterReceiver(pairReceiver);
                    return;
                }
            }
        }

        int firedFlags = pairReceiver.getFiredFlags();
        int pairFiredFlags = pairReceiver.getPairFiredFlags();
        pairReceiver.resetFiredFlags();
        fail(String.format("unpair() timeout: state=%d (expected %d), flags=0x%x (expected 0x%x), "
                + "pairFlags=0x%x (expected 0x%x)", state, BluetoothDevice.BOND_BONDED, firedFlags,
                mask, pairFiredFlags, pairMask));
    }

    public void connectA2dp(BluetoothAdapter adapter, BluetoothDevice device) {
        int mask = BluetoothReceiver.PROFILE_A2DP_FLAG;
        int a2dpMask1 = (BluetoothReceiver.A2DP_STATE_CONNECTING
                | BluetoothReceiver.A2DP_STATE_CONNECTED | BluetoothReceiver.A2DP_STATE_PLAYING);
        int a2dpMask2 = a2dpMask1 ^ BluetoothReceiver.A2DP_STATE_CONNECTED;
        int a2dpMask3 = a2dpMask1 ^ BluetoothReceiver.A2DP_STATE_PLAYING;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("connectA2dp() bluetooth not enabled");
        }

        if (!adapter.getBondedDevices().contains(device)) {
            fail("connectA2dp() device not paired: device=" + device);
        }

        int state = mA2dp.getSinkState(device);
        switch (state) {
            case BluetoothA2dp.STATE_CONNECTED:
            case BluetoothA2dp.STATE_PLAYING:
                assertTrue(mA2dp.isSinkConnected(device));
                return;
            case BluetoothA2dp.STATE_DISCONNECTING:
            case BluetoothA2dp.STATE_DISCONNECTED:
                assertFalse(mA2dp.isSinkConnected(device));
                assertTrue(mA2dp.connectSink(device));
                break;
            case BluetoothA2dp.STATE_CONNECTING:
                assertFalse(mA2dp.isSinkConnected(device));
                // Don't check for received intents since we might have missed them.
                mask = a2dpMask1 = a2dpMask2 = a2dpMask3 = 0;
                break;
            default:
                fail("connectA2dp() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < CONNECT_A2DP_TIMEOUT) {
            state = mA2dp.getSinkState(device);
            if (state == BluetoothA2dp.STATE_CONNECTED || state == BluetoothA2dp.STATE_PLAYING) {
                assertTrue(mA2dp.isSinkConnected(device));
                // Check whether STATE_CONNECTING and (STATE_CONNECTED or STATE_PLAYING) intents
                // have fired if we are checking if intents should be fired.
                int firedFlags = mBluetoothReceiver.getFiredFlags();
                int a2dpFiredFlags = mBluetoothReceiver.getA2dpFiredFlags();
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask
                        && ((a2dpFiredFlags & a2dpMask1) == a2dpMask1
                                || (a2dpFiredFlags & a2dpMask2) == a2dpMask2
                                || (a2dpFiredFlags & a2dpMask3) == a2dpMask3)) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("connectA2dp() completed in %d ms: device=%s",
                            (System.currentTimeMillis() - s), device));
                    return;
                }
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        int a2dpFiredFlags = mBluetoothReceiver.getA2dpFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("connectA2dp() timeout: state=%d (expected %d or %d), "
                + "flags=0x%x (expected 0x%x), a2dpFlags=0x%x (expected 0x%x or 0x%x or 0x%x)",
                state, BluetoothHeadset.STATE_CONNECTED, BluetoothA2dp.STATE_PLAYING, firedFlags,
                mask, a2dpFiredFlags, a2dpMask1, a2dpMask2, a2dpMask3));
    }

    public void disconnectA2dp(BluetoothAdapter adapter, BluetoothDevice device) {
        int mask = BluetoothReceiver.PROFILE_A2DP_FLAG;
        int a2dpMask = (BluetoothReceiver.A2DP_STATE_DISCONNECTING
                | BluetoothReceiver.A2DP_STATE_DISCONNECTED);
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("disconnectA2dp() bluetooth not enabled");
        }

        if (!adapter.getBondedDevices().contains(device)) {
            fail("disconnectA2dp() device not paired: device=" + device);
        }

        int state = mA2dp.getSinkState(device);
        switch (state) {
            case BluetoothA2dp.STATE_DISCONNECTED:
                assertFalse(mA2dp.isSinkConnected(device));
                return;
            case BluetoothA2dp.STATE_CONNECTED:
            case BluetoothA2dp.STATE_PLAYING:
                assertTrue(mA2dp.isSinkConnected(device));
                assertTrue(mA2dp.disconnectSink(device));
                break;
            case BluetoothA2dp.STATE_CONNECTING:
                assertFalse(mA2dp.isSinkConnected(device));
                assertTrue(mA2dp.disconnectSink(device));
                break;
            case BluetoothA2dp.STATE_DISCONNECTING:
                assertFalse(mA2dp.isSinkConnected(device));
                // Don't check for received intents since we might have missed them.
                mask = a2dpMask = 0;
                break;
            default:
                fail("disconnectA2dp() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < DISCONNECT_A2DP_TIMEOUT) {
            state = mA2dp.getSinkState(device);
            if (state == BluetoothA2dp.STATE_DISCONNECTED) {
                assertFalse(mA2dp.isSinkConnected(device));
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask
                        && (mBluetoothReceiver.getA2dpFiredFlags() & a2dpMask) == a2dpMask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("disconnectA2dp() completed in %d ms: device=%s",
                            (System.currentTimeMillis() - s), device));
                    return;
                }
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        int a2dpFiredFlags = mBluetoothReceiver.getA2dpFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("disconnectA2dp() timeout: state=%d (expected %d), "
                + "flags=0x%x (expected 0x%x), a2dpFlags=0x%x (expected 0x%x)", state,
                BluetoothA2dp.STATE_DISCONNECTED, firedFlags, mask, a2dpFiredFlags, a2dpMask));
    }

    public void connectHeadset(BluetoothAdapter adapter, BluetoothDevice device) {
        int mask = BluetoothReceiver.PROFILE_HEADSET_FLAG;
        int headsetMask = (BluetoothReceiver.HEADSET_STATE_CONNECTING
                | BluetoothReceiver.HEADSET_STATE_CONNECTED);
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("connectHeadset() bluetooth not enabled");
        }

        if (!adapter.getBondedDevices().contains(device)) {
            fail("connectHeadset() device not paired: device=" + device);
        }

        while (!mHeadsetServiceListener.isConnected()) {
            sleep(POLL_TIME);
        }

        int state = mHeadset.getState(device);
        switch (state) {
            case BluetoothHeadset.STATE_CONNECTED:
                assertTrue(mHeadset.isConnected(device));
                return;
            case BluetoothHeadset.STATE_DISCONNECTED:
                assertFalse(mHeadset.isConnected(device));
                mHeadset.connectHeadset(device);
                break;
            case BluetoothHeadset.STATE_CONNECTING:
                assertFalse(mHeadset.isConnected(device));
                // Don't check for received intents since we might have missed them.
                mask = headsetMask = 0;
                break;
            case BluetoothHeadset.STATE_ERROR:
                fail("connectHeadset() error state");
                break;
            default:
                fail("connectHeadset() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < CONNECT_HEADSET_TIMEOUT) {
            state = mHeadset.getState(device);
            if (state == BluetoothHeadset.STATE_CONNECTED) {
                assertTrue(mHeadset.isConnected(device));
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask
                        && (mBluetoothReceiver.getHeadsetFiredFlags() & headsetMask) == headsetMask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("connectHeadset() completed in %d ms: device=%s",
                            (System.currentTimeMillis() - s), device));
                    return;
                }
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        int headsetFiredFlags = mBluetoothReceiver.getHeadsetFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("connectHeadset() timeout: state=%d (expected %d), "
                + "flags=0x%x (expected 0x%x), headsetFlags=0x%s (expected 0x%x)", state,
                BluetoothHeadset.STATE_CONNECTED, firedFlags, mask, headsetFiredFlags,
                headsetMask));
    }

    public void disconnectHeadset(BluetoothAdapter adapter, BluetoothDevice device) {
        int mask = BluetoothReceiver.PROFILE_HEADSET_FLAG;
        int headsetMask = BluetoothReceiver.HEADSET_STATE_DISCONNECTED;
        mBluetoothReceiver.resetFiredFlags();

        if (!adapter.isEnabled()) {
            fail("disconnectHeadset() bluetooth not enabled");
        }

        if (!adapter.getBondedDevices().contains(device)) {
            fail("disconnectHeadset() device not paired: device=" + device);
        }

        while (!mHeadsetServiceListener.isConnected()) {
            sleep(POLL_TIME);
        }

        int state = mHeadset.getState(device);
        switch (state) {
            case BluetoothHeadset.STATE_CONNECTED:
                mHeadset.disconnectHeadset(device);
                break;
            case BluetoothHeadset.STATE_CONNECTING:
                mHeadset.disconnectHeadset(device);
                break;
            case BluetoothHeadset.STATE_DISCONNECTED:
                return;
            case BluetoothHeadset.STATE_ERROR:
                fail("disconnectHeadset() error state");
                break;
            default:
                fail("disconnectHeadset() invalid state: state=" + state);
        }

        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < DISCONNECT_HEADSET_TIMEOUT) {
            state = mHeadset.getState(device);
            if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                assertFalse(mHeadset.isConnected(device));
                if ((mBluetoothReceiver.getFiredFlags() & mask) == mask
                        && (mBluetoothReceiver.getHeadsetFiredFlags() & headsetMask) == headsetMask) {
                    mBluetoothReceiver.resetFiredFlags();
                    writeOutput(String.format("disconnectHeadset() completed in %d ms: device=%s",
                            (System.currentTimeMillis() - s), device));
                    return;
                }
            }
            sleep(POLL_TIME);
        }

        int firedFlags = mBluetoothReceiver.getFiredFlags();
        int headsetFiredFlags = mBluetoothReceiver.getHeadsetFiredFlags();
        mBluetoothReceiver.resetFiredFlags();
        fail(String.format("disconnectHeadset() timeout: state=%d (expected %d), "
                + "flags=0x%x (expected 0x%x), headsetFlags=0x%s (expected 0x%x)", state,
                BluetoothHeadset.STATE_DISCONNECTED, firedFlags, mask, headsetFiredFlags,
                headsetMask));
    }

    public void writeOutput(String s) {
        Log.i(mTag, s);
        if (mOutputWriter == null) {
            return;
        }
        try {
            mOutputWriter.write(s + "\n");
            mOutputWriter.flush();
        } catch (IOException e) {
            Log.w(mTag, "Could not write to output file", e);
        }
    }

    private BluetoothReceiver getBluetoothReceiver(Context context) {
        BluetoothReceiver receiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_SINK_STATE_CHANGED);
        filter.addAction(BluetoothHeadset.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
        return receiver;
    }

    private PairReceiver getPairReceiver(Context context, BluetoothDevice device, int passkey,
            byte[] pin) {
        PairReceiver receiver = new PairReceiver(device, passkey, pin);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
        return receiver;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
