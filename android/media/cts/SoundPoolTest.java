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

package android.media.cts;

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;

@TestTargetClass(SoundPool.class)
public class SoundPoolTest extends AndroidTestCase {

    private static final int SOUNDPOOL_STREAMS = 4;
    private static final int SOUND_A = R.raw.a_4;
    private static final int SOUND_CS = R.raw.c_sharp_5;
    private static final int SOUND_E = R.raw.e_5;
    private static final int SOUND_B = R.raw.b_5;
    private static final int SOUND_GS = R.raw.g_sharp_5;
    private static final int PRIORITY = 1;
    private static final int LOUD = 20;
    private static final int QUIET = LOUD / 2;
    private static final int SILENT = 0;

    private static final int[] SOUNDS = { SOUND_A, SOUND_CS, SOUND_E, SOUND_B, SOUND_GS };

    private static final String FILE_NAME = "a_4.ogg";
    private File mFile;
    private SoundPool mSoundPool;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mFile = new File(mContext.getFilesDir(), FILE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (mFile.exists()) {
            mFile.delete();
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
            return;
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {AssetFileDescriptor.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {Context.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {FileDescriptor.class, long.class, long.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {String.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.SUFFICIENT,
            method = "unload",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "release",
            args = {}
        )
    })
    @ToBeFixed(explanation = "unload() does not return true as specified")
    public void testLoad() throws Exception {
        int srcQuality = 100;
        mSoundPool = new SoundPool(SOUNDPOOL_STREAMS, AudioManager.STREAM_MUSIC, srcQuality);
        int sampleId1 = mSoundPool.load(mContext, SOUND_A, PRIORITY);
        waitUntilLoaded(sampleId1);
        // should return true, but returns false
        mSoundPool.unload(sampleId1);

        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(SOUND_CS);
        int sampleId2;
        sampleId2 = mSoundPool.load(afd, PRIORITY);
        waitUntilLoaded(sampleId2);
        mSoundPool.unload(sampleId2);

        FileDescriptor fd = afd.getFileDescriptor();
        long offset = afd.getStartOffset();
        long length = afd.getLength();
        int sampleId3;
        sampleId3 = mSoundPool.load(fd, offset, length, PRIORITY);
        waitUntilLoaded(sampleId3);
        mSoundPool.unload(sampleId3);

        String path = mFile.getAbsolutePath();
        createSoundFile(mFile);
        int sampleId4;
        sampleId4 = mSoundPool.load(path, PRIORITY);
        waitUntilLoaded(sampleId4);
        mSoundPool.unload(sampleId4);
    }

    private void createSoundFile(File f) throws Exception {
        FileOutputStream fOutput = null;
        try {
            fOutput = new FileOutputStream(f);
            InputStream is = mContext.getResources().openRawResource(SOUND_A);
            byte[] buffer = new byte[1024];
            int length = is.read(buffer);
            while (length != -1) {
                fOutput.write(buffer, 0, length);
                length = is.read(buffer);
            }
        } finally {
            if (fOutput != null) {
                fOutput.flush();
                fOutput.close();
            }
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {Context.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "pause",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "play",
            args = {int.class, float.class, float.class, int.class, int.class, float.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "resume",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setLoop",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setPriority",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setRate",
            args = {int.class, float.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setVolume",
            args = {int.class, float.class, float.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "SoundPool",
            args = {int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "stop",
            args = {int.class}
        )
    })
    public void testSoundPoolOp() throws Exception {
        int srcQuality = 100;
        mSoundPool = new SoundPool(SOUNDPOOL_STREAMS, AudioManager.STREAM_MUSIC, srcQuality);
        int sampleID = loadSampleSync(SOUND_A, PRIORITY);

        int waitMsec = 1000;
        float leftVolume = SILENT;
        float rightVolume = LOUD;
        int priority = 1;
        int loop = 0;
        float rate = 1f;
        int streamID = mSoundPool.play(sampleID, leftVolume, rightVolume, priority, loop, rate);
        assertTrue(streamID != 0);
        Thread.sleep(waitMsec);
        rate = 1.4f;
        mSoundPool.setRate(streamID, rate);
        Thread.sleep(waitMsec);
        mSoundPool.setRate(streamID, 1f);
        Thread.sleep(waitMsec);
        mSoundPool.pause(streamID);
        Thread.sleep(waitMsec);
        mSoundPool.resume(streamID);
        Thread.sleep(waitMsec);
        mSoundPool.stop(streamID);

        streamID = mSoundPool.play(sampleID, leftVolume, rightVolume, priority, loop, rate);
        assertTrue(streamID != 0);
        loop = -1;// loop forever
        mSoundPool.setLoop(streamID, loop);
        Thread.sleep(waitMsec);
        leftVolume = SILENT;
        rightVolume = SILENT;
        mSoundPool.setVolume(streamID, leftVolume, rightVolume);
        Thread.sleep(waitMsec);
        rightVolume = LOUD;
        mSoundPool.setVolume(streamID, leftVolume, rightVolume);
        priority = 0;
        mSoundPool.setPriority(streamID, priority);
        Thread.sleep(waitMsec * 10);
        mSoundPool.stop(streamID);
        mSoundPool.unload(sampleID);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {Context.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "play",
            args = {int.class, float.class, float.class, int.class, int.class, float.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "SoundPool",
            args = {int.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "stop",
            args = {int.class}
        )
    })
    public void testMultiSound() throws Exception {
        int srcQuality = 100;
        mSoundPool = new SoundPool(SOUNDPOOL_STREAMS, AudioManager.STREAM_MUSIC, srcQuality);
        int sampleID1 = loadSampleSync(SOUND_A, PRIORITY);
        int sampleID2 = loadSampleSync(SOUND_CS, PRIORITY);
        long waitMsec = 1000;
        Thread.sleep(waitMsec);

        // play sounds one at a time
        int streamID1 = mSoundPool.play(sampleID1, LOUD, QUIET, PRIORITY, -1, 1);
        assertTrue(streamID1 != 0);
        Thread.sleep(waitMsec * 4);
        mSoundPool.stop(streamID1);
        int streamID2 = mSoundPool.play(sampleID2, QUIET, LOUD, PRIORITY, -1, 1);
        assertTrue(streamID2 != 0);
        Thread.sleep(waitMsec * 4);
        mSoundPool.stop(streamID2);

        // play both at once repeating the first, but not the second
        streamID1 = mSoundPool.play(sampleID1, LOUD, QUIET, PRIORITY, 1, 1);
        streamID2 = mSoundPool.play(sampleID2, QUIET, LOUD, PRIORITY, 0, 1);
        assertTrue(streamID1 != 0);
        assertTrue(streamID2 != 0);
        Thread.sleep(4000);
        // both streams should have stopped by themselves; no way to check

        mSoundPool.release();
        mSoundPool = null;
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "load",
            args = {Context.class, int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "SoundPool",
            args = {int.class, int.class, int.class}
        )
    })
    public void testLoadMore() throws Exception {
        mSoundPool = new SoundPool(SOUNDPOOL_STREAMS, AudioManager.STREAM_MUSIC, 0);
        int[] soundIds = new int[SOUNDS.length];
        int[] streamIds = new int[SOUNDS.length];
        for (int i = 0; i < SOUNDS.length; i++) {
            soundIds[i] = loadSampleSync(SOUNDS[i], PRIORITY);
            System.out.println("load: " + soundIds[i]);
        }
        for (int i = 0; i < soundIds.length; i++) {
            streamIds[i] = mSoundPool.play(soundIds[i], LOUD, LOUD, PRIORITY, -1, 1);
        }
        Thread.sleep(3000);
        for (int stream : streamIds) {
            assertTrue(stream != 0);
            mSoundPool.stop(stream);
        }
        for (int sound : soundIds) {
            mSoundPool.unload(sound);
        }
        mSoundPool.release();
    }

    /**
     * Load a sample and wait until it is ready to be played.
     * @return The sample ID.
     * @throws InterruptedException
     */
    private int loadSampleSync(int sampleId, int prio) throws InterruptedException {
        int sample = mSoundPool.load(mContext, sampleId, prio);
        waitUntilLoaded(sample);
        return sample;
    }

    /**
     * Wait until the specified sample is loaded.
     * @param sampleId The sample ID.
     * @throws InterruptedException
     */
    private void waitUntilLoaded(int sampleId) throws InterruptedException {
        int stream = 0;
        while (stream == 0) {
            Thread.sleep(500);
            stream = mSoundPool.play(sampleId, SILENT, SILENT, 1, 0, 1);
        }
        mSoundPool.stop(stream);
    }
}
