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

package android.media.cts;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.test.AndroidTestCase;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

@TestTargetClass(AudioRecord.class)
public class AudioRecordTest extends AndroidTestCase {

    private AudioRecord mAudioRecord;
    private int mHz = 44100;
    private boolean mIsOnMarkerReachedCalled;
    private boolean mIsOnPeriodicNotificationCalled;
    private boolean mIsHandleMessageCalled;
    private Looper mLooper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        /*
         * InstrumentationTestRunner.onStart() calls Looper.prepare(), which creates a looper
         * for the current thread. However, since we don't actually call loop() in the test,
         * any messages queued with that looper will never be consumed. Therefore, we must
         * create the instance in another thread, either without a looper, so the main looper is
         * used, or with an active looper.
         */
        Thread t = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mLooper = Looper.myLooper();
                synchronized(this) {
                    mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mHz,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            AudioRecord.getMinBufferSize(mHz,
                                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT) * 10);
                    this.notify();
                }
                Looper.loop();
            }
        };
        synchronized(t) {
            t.start(); // will block until we wait
            t.wait();
        }
        assertNotNull(mAudioRecord);
    }

    @Override
    protected void tearDown() throws Exception {
        mAudioRecord.release();
        mLooper.quit();
        super.tearDown();
    }

    private void reset() {
        mIsOnMarkerReachedCalled = false;
        mIsOnPeriodicNotificationCalled = false;
        mIsHandleMessageCalled = false;
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "AudioRecord",
            args = {int.class, int.class, int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getAudioFormat",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getAudioSource",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getState",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getSampleRate",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getRecordingState",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getMinBufferSize",
            args = {int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getChannelCount",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getChannelConfiguration",
            args = {}
        )
    })
    public void testAudioRecordProperties() throws Exception {
        assertEquals(AudioFormat.ENCODING_PCM_16BIT, mAudioRecord.getAudioFormat());
        assertEquals(MediaRecorder.AudioSource.DEFAULT, mAudioRecord.getAudioSource());
        assertEquals(1, mAudioRecord.getChannelCount());
        assertEquals(AudioFormat.CHANNEL_CONFIGURATION_MONO,
                mAudioRecord.getChannelConfiguration());
        assertEquals(AudioRecord.STATE_INITIALIZED, mAudioRecord.getState());
        assertEquals(mHz, mAudioRecord.getSampleRate());
        assertEquals(AudioRecord.RECORDSTATE_STOPPED, mAudioRecord.getRecordingState());

        int bufferSize = AudioRecord.getMinBufferSize(mHz,
                AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
        assertTrue(bufferSize > 0);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "AudioRecord",
            args = {int.class, int.class, int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "release",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "startRecording",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "stop",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setNotificationMarkerPosition",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setPositionNotificationPeriod",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getNotificationMarkerPosition",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getPositionNotificationPeriod",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            method = "read",
            args = {byte[].class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            method = "read",
            args = {short[].class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            method = "read",
            args = {ByteBuffer.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getRecordingState",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setRecordPositionUpdateListener",
            args = {OnRecordPositionUpdateListener.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setRecordPositionUpdateListener",
            args = {OnRecordPositionUpdateListener.class, Handler.class}
        )
    })
    public void testAudioRecordOP() throws Exception {
        final int SLEEP_TIME = 10;
        final int RECORD_TIME = 10000;
        assertEquals(AudioRecord.STATE_INITIALIZED, mAudioRecord.getState());

        int markerInFrames = mAudioRecord.getSampleRate() / 2;
        assertEquals(AudioRecord.SUCCESS,
                mAudioRecord.setNotificationMarkerPosition(markerInFrames));
        assertEquals(markerInFrames, mAudioRecord.getNotificationMarkerPosition());
        int periodInFrames = mAudioRecord.getSampleRate();
        assertEquals(AudioRecord.SUCCESS,
                mAudioRecord.setPositionNotificationPeriod(periodInFrames));
        assertEquals(periodInFrames, mAudioRecord.getPositionNotificationPeriod());
        OnRecordPositionUpdateListener listener = new OnRecordPositionUpdateListener() {

            public void onMarkerReached(AudioRecord recorder) {
                mIsOnMarkerReachedCalled = true;
            }

            public void onPeriodicNotification(AudioRecord recorder) {
                mIsOnPeriodicNotificationCalled = true;
            }
        };
        mAudioRecord.setRecordPositionUpdateListener(listener);

        // use byte array as buffer
        final int BUFFER_SIZE = 102400;
        byte[] byteData = new byte[BUFFER_SIZE];
        long time = System.currentTimeMillis();
        mAudioRecord.startRecording();
        assertEquals(AudioRecord.RECORDSTATE_RECORDING, mAudioRecord.getRecordingState());
        while (System.currentTimeMillis() - time < RECORD_TIME) {
            Thread.sleep(SLEEP_TIME);
            mAudioRecord.read(byteData, 0, BUFFER_SIZE);
        }
        mAudioRecord.stop();
        assertEquals(AudioRecord.RECORDSTATE_STOPPED, mAudioRecord.getRecordingState());
        assertTrue(mIsOnMarkerReachedCalled);
        assertTrue(mIsOnPeriodicNotificationCalled);
        reset();

        // use short array as buffer
        short[] shortData = new short[BUFFER_SIZE];
        time = System.currentTimeMillis();
        mAudioRecord.startRecording();
        assertEquals(AudioRecord.RECORDSTATE_RECORDING, mAudioRecord.getRecordingState());
        while (System.currentTimeMillis() - time < RECORD_TIME) {
            Thread.sleep(SLEEP_TIME);
            mAudioRecord.read(shortData, 0, BUFFER_SIZE);
        }
        mAudioRecord.stop();
        assertEquals(AudioRecord.RECORDSTATE_STOPPED, mAudioRecord.getRecordingState());
        assertTrue(mIsOnMarkerReachedCalled);
        assertTrue(mIsOnPeriodicNotificationCalled);
        reset();

        // use ByteBuffer as buffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        time = System.currentTimeMillis();
        mAudioRecord.startRecording();
        assertEquals(AudioRecord.RECORDSTATE_RECORDING, mAudioRecord.getRecordingState());
        while (System.currentTimeMillis() - time < RECORD_TIME) {
            Thread.sleep(SLEEP_TIME);
            mAudioRecord.read(byteBuffer, BUFFER_SIZE);
        }
        mAudioRecord.stop();
        assertEquals(AudioRecord.RECORDSTATE_STOPPED, mAudioRecord.getRecordingState());
        assertTrue(mIsOnMarkerReachedCalled);
        assertTrue(mIsOnPeriodicNotificationCalled);
        reset();

        // use handler
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mIsHandleMessageCalled = true;
                super.handleMessage(msg);
            }
        };

        mAudioRecord.setRecordPositionUpdateListener(listener, handler);
        time = System.currentTimeMillis();
        mAudioRecord.startRecording();
        assertEquals(AudioRecord.RECORDSTATE_RECORDING, mAudioRecord.getRecordingState());
        while (System.currentTimeMillis() - time < RECORD_TIME) {
            Thread.sleep(SLEEP_TIME);
            mAudioRecord.read(byteData, 0, BUFFER_SIZE);
        }
        mAudioRecord.stop();
        assertEquals(AudioRecord.RECORDSTATE_STOPPED, mAudioRecord.getRecordingState());
        assertTrue(mIsOnMarkerReachedCalled);
        assertTrue(mIsOnPeriodicNotificationCalled);
        // The handler argument is only ever used for getting the associated Looper
        assertFalse(mIsHandleMessageCalled);

        mAudioRecord.release();
        assertEquals(AudioRecord.STATE_UNINITIALIZED, mAudioRecord.getState());
    }
}
