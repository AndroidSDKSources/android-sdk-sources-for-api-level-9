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

/**
 * TODO: Move this to services.jar
 * and make the contructor package private again.
 * @hide
 */

package android.server;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BluetoothA2dpService extends IBluetoothA2dp.Stub {
    private static final String TAG = "BluetoothA2dpService";
    private static final boolean DBG = true;

    public static final String BLUETOOTH_A2DP_SERVICE = "bluetooth_a2dp";

    private static final String BLUETOOTH_ADMIN_PERM = android.Manifest.permission.BLUETOOTH_ADMIN;
    private static final String BLUETOOTH_PERM = android.Manifest.permission.BLUETOOTH;

    private static final String BLUETOOTH_ENABLED = "bluetooth_enabled";

    private static final String PROPERTY_STATE = "State";

    private static final String SINK_STATE_DISCONNECTED = "disconnected";
    private static final String SINK_STATE_CONNECTING = "connecting";
    private static final String SINK_STATE_CONNECTED = "connected";
    private static final String SINK_STATE_PLAYING = "playing";

    private static int mSinkCount;

    private final Context mContext;
    private final IntentFilter mIntentFilter;
    private HashMap<BluetoothDevice, Integer> mAudioDevices;
    private final AudioManager mAudioManager;
    private final BluetoothService mBluetoothService;
    private final BluetoothAdapter mAdapter;
    private int   mTargetA2dpState;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                               BluetoothAdapter.ERROR);
                switch (state) {
                case BluetoothAdapter.STATE_ON:
                    onBluetoothEnable();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    onBluetoothDisable();
                    break;
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                                   BluetoothDevice.ERROR);
                switch(bondState) {
                case BluetoothDevice.BOND_BONDED:
                    if (getSinkPriority(device) == BluetoothA2dp.PRIORITY_UNDEFINED) {
                        setSinkPriority(device, BluetoothA2dp.PRIORITY_ON);
                    }
                    break;
                case BluetoothDevice.BOND_NONE:
                    setSinkPriority(device, BluetoothA2dp.PRIORITY_UNDEFINED);
                    break;
                }
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                synchronized (this) {
                    if (mAudioDevices.containsKey(device)) {
                        int state = mAudioDevices.get(device);
                        handleSinkStateChange(device, state, BluetoothA2dp.STATE_DISCONNECTED);
                    }
                }
            } else if (action.equals(AudioManager.VOLUME_CHANGED_ACTION)) {
                int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                if (streamType == AudioManager.STREAM_MUSIC) {
                    BluetoothDevice sinks[] = getConnectedSinks();
                    if (sinks.length != 0 && isPhoneDocked(sinks[0])) {
                        String address = sinks[0].getAddress();
                        int newVolLevel =
                          intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, 0);
                        int oldVolLevel =
                          intent.getIntExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, 0);
                        String path = mBluetoothService.getObjectPathFromAddress(address);
                        if (newVolLevel > oldVolLevel) {
                            avrcpVolumeUpNative(path);
                        } else if (newVolLevel < oldVolLevel) {
                            avrcpVolumeDownNative(path);
                        }
                    }
                }
            }
        }
    };


    private boolean isPhoneDocked(BluetoothDevice device) {
        // This works only because these broadcast intents are "sticky"
        Intent i = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_DOCK_EVENT));
        if (i != null) {
            int state = i.getIntExtra(Intent.EXTRA_DOCK_STATE, Intent.EXTRA_DOCK_STATE_UNDOCKED);
            if (state != Intent.EXTRA_DOCK_STATE_UNDOCKED) {
                BluetoothDevice dockDevice = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (dockDevice != null && device.equals(dockDevice)) {
                    return true;
                }
            }
        }
        return false;
    }

    public BluetoothA2dpService(Context context, BluetoothService bluetoothService) {
        mContext = context;

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mBluetoothService = bluetoothService;
        if (mBluetoothService == null) {
            throw new RuntimeException("Platform does not support Bluetooth");
        }

        if (!initNative()) {
            throw new RuntimeException("Could not init BluetoothA2dpService");
        }

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mIntentFilter.addAction(AudioManager.VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mReceiver, mIntentFilter);

        mAudioDevices = new HashMap<BluetoothDevice, Integer>();

        if (mBluetoothService.isEnabled())
            onBluetoothEnable();
        mTargetA2dpState = -1;
        mBluetoothService.setA2dpService(this);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            cleanupNative();
        } finally {
            super.finalize();
        }
    }

    private int convertBluezSinkStringtoState(String value) {
        if (value.equalsIgnoreCase("disconnected"))
            return BluetoothA2dp.STATE_DISCONNECTED;
        if (value.equalsIgnoreCase("connecting"))
            return BluetoothA2dp.STATE_CONNECTING;
        if (value.equalsIgnoreCase("connected"))
            return BluetoothA2dp.STATE_CONNECTED;
        if (value.equalsIgnoreCase("playing"))
            return BluetoothA2dp.STATE_PLAYING;
        return -1;
    }

    private boolean isSinkDevice(BluetoothDevice device) {
        ParcelUuid[] uuids = mBluetoothService.getRemoteUuids(device.getAddress());
        if (uuids != null && BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.AudioSink)) {
            return true;
        }
        return false;
    }

    private synchronized boolean addAudioSink (BluetoothDevice device) {
        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());
        String propValues[] = (String []) getSinkPropertiesNative(path);
        if (propValues == null) {
            Log.e(TAG, "Error while getting AudioSink properties for device: " + device);
            return false;
        }
        Integer state = null;
        // Properties are name-value pairs
        for (int i = 0; i < propValues.length; i+=2) {
            if (propValues[i].equals(PROPERTY_STATE)) {
                state = new Integer(convertBluezSinkStringtoState(propValues[i+1]));
                break;
            }
        }
        mAudioDevices.put(device, state);
        handleSinkStateChange(device, BluetoothA2dp.STATE_DISCONNECTED, state);
        return true;
    }

    private synchronized void onBluetoothEnable() {
        String devices = mBluetoothService.getProperty("Devices");
        mSinkCount = 0;
        if (devices != null) {
            String [] paths = devices.split(",");
            for (String path: paths) {
                String address = mBluetoothService.getAddressFromObjectPath(path);
                BluetoothDevice device = mAdapter.getRemoteDevice(address);
                ParcelUuid[] remoteUuids = mBluetoothService.getRemoteUuids(address);
                if (remoteUuids != null)
                    if (BluetoothUuid.containsAnyUuid(remoteUuids,
                            new ParcelUuid[] {BluetoothUuid.AudioSink,
                                                BluetoothUuid.AdvAudioDist})) {
                        addAudioSink(device);
                    }
                }
        }
        mAudioManager.setParameters(BLUETOOTH_ENABLED+"=true");
        mAudioManager.setParameters("A2dpSuspended=false");
    }

    private synchronized void onBluetoothDisable() {
        if (!mAudioDevices.isEmpty()) {
            BluetoothDevice[] devices = new BluetoothDevice[mAudioDevices.size()];
            devices = mAudioDevices.keySet().toArray(devices);
            for (BluetoothDevice device : devices) {
                int state = getSinkState(device);
                switch (state) {
                    case BluetoothA2dp.STATE_CONNECTING:
                    case BluetoothA2dp.STATE_CONNECTED:
                    case BluetoothA2dp.STATE_PLAYING:
                        disconnectSinkNative(mBluetoothService.getObjectPathFromAddress(
                                device.getAddress()));
                        handleSinkStateChange(device, state, BluetoothA2dp.STATE_DISCONNECTED);
                        break;
                    case BluetoothA2dp.STATE_DISCONNECTING:
                        handleSinkStateChange(device, BluetoothA2dp.STATE_DISCONNECTING,
                                              BluetoothA2dp.STATE_DISCONNECTED);
                        break;
                }
            }
            mAudioDevices.clear();
        }

        mAudioManager.setParameters(BLUETOOTH_ENABLED + "=false");
    }

    private synchronized boolean isConnectSinkFeasible(BluetoothDevice device) {
        if (!mBluetoothService.isEnabled() || !isSinkDevice(device) ||
                getSinkPriority(device) == BluetoothA2dp.PRIORITY_OFF) {
                return false;
            }

            if (mAudioDevices.get(device) == null && !addAudioSink(device)) {
                return false;
            }

            String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());
            if (path == null) {
                return false;
            }
            return true;
    }

    public synchronized boolean connectSink(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                                "Need BLUETOOTH_ADMIN permission");
        if (DBG) log("connectSink(" + device + ")");
        if (!isConnectSinkFeasible(device)) return false;

        return mBluetoothService.connectSink(device.getAddress());
    }

    public synchronized boolean connectSinkInternal(BluetoothDevice device) {
        if (!mBluetoothService.isEnabled()) return false;

        int state = mAudioDevices.get(device);

        // ignore if there are any active sinks
        if (lookupSinksMatchingStates(new int[] {
                BluetoothA2dp.STATE_CONNECTING,
                BluetoothA2dp.STATE_CONNECTED,
                BluetoothA2dp.STATE_PLAYING,
                BluetoothA2dp.STATE_DISCONNECTING}).size() != 0) {
            return false;
        }

        switch (state) {
        case BluetoothA2dp.STATE_CONNECTED:
        case BluetoothA2dp.STATE_PLAYING:
        case BluetoothA2dp.STATE_DISCONNECTING:
            return false;
        case BluetoothA2dp.STATE_CONNECTING:
            return true;
        }

        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());

        // State is DISCONNECTED and we are connecting.
        if (getSinkPriority(device) < BluetoothA2dp.PRIORITY_AUTO_CONNECT) {
            setSinkPriority(device, BluetoothA2dp.PRIORITY_AUTO_CONNECT);
        }
        handleSinkStateChange(device, state, BluetoothA2dp.STATE_CONNECTING);

        if (!connectSinkNative(path)) {
            // Restore previous state
            handleSinkStateChange(device, mAudioDevices.get(device), state);
            return false;
        }
        return true;
    }

    private synchronized boolean isDisconnectSinkFeasible(BluetoothDevice device) {
        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());
        if (path == null) {
            return false;
        }

        int state = getSinkState(device);
        switch (state) {
        case BluetoothA2dp.STATE_DISCONNECTED:
            return false;
        case BluetoothA2dp.STATE_DISCONNECTING:
            return true;
        }
        return true;
    }

    public synchronized boolean disconnectSink(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                                "Need BLUETOOTH_ADMIN permission");
        if (DBG) log("disconnectSink(" + device + ")");
        if (!isDisconnectSinkFeasible(device)) return false;
        return mBluetoothService.disconnectSink(device.getAddress());
    }

    public synchronized boolean disconnectSinkInternal(BluetoothDevice device) {
        int state = getSinkState(device);
        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());

        switch (state) {
            case BluetoothA2dp.STATE_DISCONNECTED:
            case BluetoothA2dp.STATE_DISCONNECTING:
                return false;
        }
        // State is CONNECTING or CONNECTED or PLAYING
        handleSinkStateChange(device, state, BluetoothA2dp.STATE_DISCONNECTING);
        if (!disconnectSinkNative(path)) {
            // Restore previous state
            handleSinkStateChange(device, mAudioDevices.get(device), state);
            return false;
        }
        return true;
    }

    public synchronized boolean suspendSink(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                            "Need BLUETOOTH_ADMIN permission");
        if (DBG) log("suspendSink(" + device + "), mTargetA2dpState: "+mTargetA2dpState);
        if (device == null || mAudioDevices == null) {
            return false;
        }
        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());
        Integer state = mAudioDevices.get(device);
        if (path == null || state == null) {
            return false;
        }

        mTargetA2dpState = BluetoothA2dp.STATE_CONNECTED;
        return checkSinkSuspendState(state.intValue());
    }

    public synchronized boolean resumeSink(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                            "Need BLUETOOTH_ADMIN permission");
        if (DBG) log("resumeSink(" + device + "), mTargetA2dpState: "+mTargetA2dpState);
        if (device == null || mAudioDevices == null) {
            return false;
        }
        String path = mBluetoothService.getObjectPathFromAddress(device.getAddress());
        Integer state = mAudioDevices.get(device);
        if (path == null || state == null) {
            return false;
        }
        mTargetA2dpState = BluetoothA2dp.STATE_PLAYING;
        return checkSinkSuspendState(state.intValue());
    }

    public synchronized BluetoothDevice[] getConnectedSinks() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        Set<BluetoothDevice> sinks = lookupSinksMatchingStates(
                new int[] {BluetoothA2dp.STATE_CONNECTED, BluetoothA2dp.STATE_PLAYING});
        return sinks.toArray(new BluetoothDevice[sinks.size()]);
    }

    public synchronized BluetoothDevice[] getNonDisconnectedSinks() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        Set<BluetoothDevice> sinks = lookupSinksMatchingStates(
                new int[] {BluetoothA2dp.STATE_CONNECTED,
                           BluetoothA2dp.STATE_PLAYING,
                           BluetoothA2dp.STATE_CONNECTING,
                           BluetoothA2dp.STATE_DISCONNECTING});
        return sinks.toArray(new BluetoothDevice[sinks.size()]);
    }

    public synchronized int getSinkState(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        Integer state = mAudioDevices.get(device);
        if (state == null)
            return BluetoothA2dp.STATE_DISCONNECTED;
        return state;
    }

    public synchronized int getSinkPriority(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.getBluetoothA2dpSinkPriorityKey(device.getAddress()),
                BluetoothA2dp.PRIORITY_UNDEFINED);
    }

    public synchronized boolean setSinkPriority(BluetoothDevice device, int priority) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                                "Need BLUETOOTH_ADMIN permission");
        if (!BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {
            return false;
        }
        return Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.getBluetoothA2dpSinkPriorityKey(device.getAddress()), priority);
    }

    private synchronized void onSinkPropertyChanged(String path, String []propValues) {
        if (!mBluetoothService.isEnabled()) {
            return;
        }

        String name = propValues[0];
        String address = mBluetoothService.getAddressFromObjectPath(path);
        if (address == null) {
            Log.e(TAG, "onSinkPropertyChanged: Address of the remote device in null");
            return;
        }

        BluetoothDevice device = mAdapter.getRemoteDevice(address);

        if (name.equals(PROPERTY_STATE)) {
            int state = convertBluezSinkStringtoState(propValues[1]);
            if (mAudioDevices.get(device) == null) {
                // This is for an incoming connection for a device not known to us.
                // We have authorized it and bluez state has changed.
                addAudioSink(device);
            } else {
                int prevState = mAudioDevices.get(device);
                handleSinkStateChange(device, prevState, state);
            }
        }
    }

    private void handleSinkStateChange(BluetoothDevice device, int prevState, int state) {
        if (state != prevState) {
            if (state == BluetoothA2dp.STATE_DISCONNECTED ||
                    state == BluetoothA2dp.STATE_DISCONNECTING) {
                mSinkCount--;
            } else if (state == BluetoothA2dp.STATE_CONNECTED) {
                mSinkCount ++;
            }
            mAudioDevices.put(device, state);

            checkSinkSuspendState(state);
            mTargetA2dpState = -1;

            if (getSinkPriority(device) > BluetoothA2dp.PRIORITY_OFF &&
                    state == BluetoothA2dp.STATE_CONNECTED) {
                // We have connected or attempting to connect.
                // Bump priority
                setSinkPriority(device, BluetoothA2dp.PRIORITY_AUTO_CONNECT);
                // We will only have 1 device with AUTO_CONNECT priority
                // To be backward compatible set everyone else to have PRIORITY_ON
                adjustOtherSinkPriorities(device);
            }

            Intent intent = new Intent(BluetoothA2dp.ACTION_SINK_STATE_CHANGED);
            intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
            intent.putExtra(BluetoothA2dp.EXTRA_PREVIOUS_SINK_STATE, prevState);
            intent.putExtra(BluetoothA2dp.EXTRA_SINK_STATE, state);
            mContext.sendBroadcast(intent, BLUETOOTH_PERM);

            if (DBG) log("A2DP state : device: " + device + " State:" + prevState + "->" + state);
        }
    }

    private void adjustOtherSinkPriorities(BluetoothDevice connectedDevice) {
        for (BluetoothDevice device : mAdapter.getBondedDevices()) {
            if (getSinkPriority(device) >= BluetoothA2dp.PRIORITY_AUTO_CONNECT &&
                !device.equals(connectedDevice)) {
                setSinkPriority(device, BluetoothA2dp.PRIORITY_ON);
            }
        }
    }

    private synchronized Set<BluetoothDevice> lookupSinksMatchingStates(int[] states) {
        Set<BluetoothDevice> sinks = new HashSet<BluetoothDevice>();
        if (mAudioDevices.isEmpty()) {
            return sinks;
        }
        for (BluetoothDevice device: mAudioDevices.keySet()) {
            int sinkState = getSinkState(device);
            for (int state : states) {
                if (state == sinkState) {
                    sinks.add(device);
                    break;
                }
            }
        }
        return sinks;
    }

    private boolean checkSinkSuspendState(int state) {
        boolean result = true;

        if (state != mTargetA2dpState) {
            if (state == BluetoothA2dp.STATE_PLAYING &&
                mTargetA2dpState == BluetoothA2dp.STATE_CONNECTED) {
                mAudioManager.setParameters("A2dpSuspended=true");
            } else if (state == BluetoothA2dp.STATE_CONNECTED &&
                mTargetA2dpState == BluetoothA2dp.STATE_PLAYING) {
                mAudioManager.setParameters("A2dpSuspended=false");
            } else {
                result = false;
            }
        }
        return result;
    }

    private void onConnectSinkResult(String deviceObjectPath, boolean result) {
        // If the call was a success, ignore we will update the state
        // when we a Sink Property Change
        if (!result) {
            if (deviceObjectPath != null) {
                String address = mBluetoothService.getAddressFromObjectPath(deviceObjectPath);
                if (address == null) return;
                BluetoothDevice device = mAdapter.getRemoteDevice(address);
                int state = getSinkState(device);
                handleSinkStateChange(device, state, BluetoothA2dp.STATE_DISCONNECTED);
            }
        }
    }

    @Override
    protected synchronized void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (mAudioDevices.isEmpty()) return;
        pw.println("Cached audio devices:");
        for (BluetoothDevice device : mAudioDevices.keySet()) {
            int state = mAudioDevices.get(device);
            pw.println(device + " " + BluetoothA2dp.stateToString(state));
        }
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }

    private native boolean initNative();
    private native void cleanupNative();
    private synchronized native boolean connectSinkNative(String path);
    private synchronized native boolean disconnectSinkNative(String path);
    private synchronized native boolean suspendSinkNative(String path);
    private synchronized native boolean resumeSinkNative(String path);
    private synchronized native Object []getSinkPropertiesNative(String path);
    private synchronized native boolean avrcpVolumeUpNative(String path);
    private synchronized native boolean avrcpVolumeDownNative(String path);
}
