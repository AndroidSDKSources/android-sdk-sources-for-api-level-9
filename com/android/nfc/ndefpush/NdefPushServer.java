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

package com.android.nfc.ndefpush;

import com.android.internal.nfc.LlcpException;
import com.android.internal.nfc.LlcpServiceSocket;
import com.android.internal.nfc.LlcpSocket;
import com.android.nfc.NfcService;

import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple server that accepts NDEF messages pushed to it over an LLCP connection. Those messages
 * are typically set on the client side by using {@link NfcAdapter#setLocalNdefMessage}.
 */
public class NdefPushServer {
    private static final String TAG = "NdefPushServer";
    private static final boolean DBG = true;

    private static final int SERVICE_SAP = 0x10;
    private static final int MIU = 248;

    static final String SERVICE_NAME = "com.android.npp";

    NfcService mService = NfcService.getInstance();

    /** Protected by 'this', null when stopped, non-null when running */
    ServerThread mServerThread = null;

    /** Connection class, used to handle incoming connections */
    private class ConnectionThread extends Thread {
        private LlcpSocket mSock;

        ConnectionThread(LlcpSocket sock) {
            super(TAG);
            mSock = sock;
        }

        @Override
        public void run() {
            if (DBG) Log.d(TAG, "starting connection thread");
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
                byte[] partial = new byte[1024];
                int size;
                boolean connectionBroken = false;

                // Get raw data from remote server
                while(!connectionBroken) {
                    try {
                        size = mSock.receive(partial);
                        if (DBG) Log.d(TAG, "read " + size + " bytes");
                        if (size < 0) {
                            connectionBroken = true;
                            break;
                        } else {
                            buffer.write(partial, 0, size);
                        }
                    } catch (IOException e) {
                        // Connection broken
                        connectionBroken = true;
                        if (DBG) Log.d(TAG, "connection broken by IOException", e);
                    }
                }

                // Build NDEF message set from the stream
                NdefPushProtocol msg = new NdefPushProtocol(buffer.toByteArray());
                if (DBG) Log.d(TAG, "got message " + msg.toString());

                // Send the intent for the fake tag
                mService.sendMockNdefTag(msg.getImmediate());
            } catch (FormatException e) {
                Log.e(TAG, "badly formatted NDEF message, ignoring", e);
            } finally {
                try {
                    if (DBG) Log.d(TAG, "about to close");
                    mSock.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (DBG) Log.d(TAG, "finished connection thread");
        }
    };

    /** Server class, used to listen for incoming connection request */
    class ServerThread extends Thread {
        boolean mRunning = true;
        LlcpServiceSocket mServerSocket;

        @Override
        public void run() {
            while (mRunning) {
                if (DBG) Log.d(TAG, "about create LLCP service socket");
                mServerSocket = mService.createLlcpServiceSocket(SERVICE_SAP, SERVICE_NAME,
                        MIU, 1, 1024);
                if (mServerSocket == null) {
                    if (DBG) Log.d(TAG, "failed to create LLCP service socket");
                    return;
                }
                if (DBG) Log.d(TAG, "created LLCP service socket");
                try {
                    while (mRunning) {
                        if (DBG) Log.d(TAG, "about to accept");
                        LlcpSocket communicationSocket = mServerSocket.accept();
                        if (DBG) Log.d(TAG, "accept returned " + communicationSocket);
                        if (communicationSocket != null) {
                            new ConnectionThread(communicationSocket).start();
                        }
                    }
                    if (DBG) Log.d(TAG, "stop running");
                } catch (LlcpException e) {
                    Log.e(TAG, "llcp error", e);
                } catch (IOException e) {
                    Log.e(TAG, "IO error", e);
                } finally {
                    if (mServerSocket != null) {
                        if (DBG) Log.d(TAG, "about to close");
                        mServerSocket.close();
                        mServerSocket = null;
                    }
                }
            }
        }

        public void shutdown() {
            mRunning = false;
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        }
    };

    public void start() {
        synchronized (this) {
            if (DBG) Log.d(TAG, "start, thread = " + mServerThread);
            if (mServerThread == null) {
                if (DBG) Log.d(TAG, "starting new server thread");
                mServerThread = new ServerThread();
                mServerThread.start();
            }
        }
    }

    public void stop() {
        synchronized (this) {
            if (DBG) Log.d(TAG, "stop, thread = " + mServerThread);
            if (mServerThread != null) {
                if (DBG) Log.d(TAG, "shuting down server thread");
                mServerThread.shutdown();
                mServerThread = null;
            }
        }
    }
}
