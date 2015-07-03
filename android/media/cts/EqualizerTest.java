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

package android.media.cts;

import android.media.audiofx.AudioEffect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.os.Looper;
import android.test.AndroidTestCase;
import android.util.Log;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

@TestTargetClass(Equalizer.class)
public class EqualizerTest extends AndroidTestCase {

    private String TAG = "EqualizerTest";
    private final static int MIN_NUMBER_OF_BANDS = 4;
    private final static int MAX_LEVEL_RANGE_LOW = -1200;         // -12dB
    private final static int MIN_LEVEL_RANGE_HIGH = 1200;         // +12dB
    private final static int TEST_FREQUENCY_MILLIHERTZ = 1000000; // 1kHz
    private final static int MIN_NUMBER_OF_PRESETS = 0;
    private final static float TOLERANCE = 100;                   // +/-1dB

    private Equalizer mEqualizer = null;
    private Equalizer mEqualizer2 = null;
    private int mSession = -1;
    private boolean mHasControl = false;
    private boolean mIsEnabled = false;
    private int mChangedParameter = -1;
    private boolean mInitialized = false;
    private Looper mLooper = null;
    private final Object mLock = new Object();


    //-----------------------------------------------------------------
    // EQUALIZER TESTS:
    //----------------------------------

    //-----------------------------------------------------------------
    // 0 - constructor
    //----------------------------------

    //Test case 0.0: test constructor and release
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "Equalizer",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getId",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "release",
            args = {}
        )
    })
    public void test0_0ConstructorAndRelease() throws Exception {
        Equalizer eq = null;
        try {
            eq = new Equalizer(0, 0);
            assertNotNull("could not create Equalizer", eq);
            try {
                assertTrue("invalid effect ID", (eq.getId() != 0));
            } catch (IllegalStateException e) {
                fail("Equalizer not initialized");
            }
        } catch (IllegalArgumentException e) {
            fail("Equalizer not found");
        } catch (UnsupportedOperationException e) {
            fail("Effect library not loaded");
        } finally {
            if (eq != null) {
                eq.release();
            }
        }
    }


    //-----------------------------------------------------------------
    // 1 - get/set parameters
    //----------------------------------

    //Test case 1.0: test setBandLevel() and getBandLevel()
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getNumberOfBands",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBandLevelRange",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setBandLevel",
            args = {short.class, short.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBandLevel",
            args = {short.class}
        )
    })
    public void test1_0BandLevel() throws Exception {
        getEqualizer(0);
        try {
            short numBands = mEqualizer.getNumberOfBands();
            assertTrue("not enough bands", numBands >= MIN_NUMBER_OF_BANDS);

            short[] levelRange = mEqualizer.getBandLevelRange();
            assertTrue("min level too high", levelRange[0] <= MAX_LEVEL_RANGE_LOW);
            assertTrue("max level too low", levelRange[1] >= MIN_LEVEL_RANGE_HIGH);

            mEqualizer.setBandLevel((short)0, levelRange[1]);
            short level = mEqualizer.getBandLevel((short)0);
            // allow +/- TOLERANCE margin on actual level compared to requested level
            assertTrue("setBandLevel failed",
                    (level >= (levelRange[1] - TOLERANCE)) &&
                    (level <= (levelRange[1] + TOLERANCE)));

        } catch (IllegalArgumentException e) {
            fail("Bad parameter value");
        } catch (UnsupportedOperationException e) {
            fail("get parameter() rejected");
        } catch (IllegalStateException e) {
            fail("get parameter() called in wrong state");
        } finally {
            releaseEqualizer();
        }
    }

    //Test case 1.1: test band frequency
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBand",
            args = {int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBandFreqRange",
            args = {short.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getCenterFreq",
            args = {short.class}
        )
    })
    public void test1_1BandFrequency() throws Exception {
        getEqualizer(0);
        try {
            short band = mEqualizer.getBand(TEST_FREQUENCY_MILLIHERTZ);
            assertTrue("getBand failed", band >= 0);
            int[] freqRange = mEqualizer.getBandFreqRange(band);
            assertTrue("getBandFreqRange failed",
                    (freqRange[0] <= TEST_FREQUENCY_MILLIHERTZ) &&
                    (freqRange[1] >= TEST_FREQUENCY_MILLIHERTZ));
            int freq = mEqualizer.getCenterFreq(band);
            assertTrue("getCenterFreq failed",
                    (freqRange[0] <= freq) && (freqRange[1] >= freq));

        } catch (IllegalArgumentException e) {
            fail("Bad parameter value");
        } catch (UnsupportedOperationException e) {
            fail("get parameter() rejected");
        } catch (IllegalStateException e) {
            fail("get parameter() called in wrong state");
        } finally {
            releaseEqualizer();
        }
    }

    //Test case 1.2: test presets
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getNumberOfPresets",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "usePreset",
            args = {short.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getCurrentPreset",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getPresetName",
            args = {short.class}
        )
    })
    public void test1_2Presets() throws Exception {
        getEqualizer(0);
        try {
            short numPresets = mEqualizer.getNumberOfPresets();
            assertTrue("getNumberOfPresets failed", numPresets >= MIN_NUMBER_OF_PRESETS);
            if (numPresets > 0) {
                mEqualizer.usePreset((short)(numPresets - 1));
                short preset = mEqualizer.getCurrentPreset();
                assertEquals("usePreset failed", preset, (short)(numPresets - 1));
                String name = mEqualizer.getPresetName(preset);
                assertNotNull("getPresetName failed", name);
            }

        } catch (IllegalArgumentException e) {
            fail("Bad parameter value");
        } catch (UnsupportedOperationException e) {
            fail("get parameter() rejected");
        } catch (IllegalStateException e) {
            fail("get parameter() called in wrong state");
        } finally {
            releaseEqualizer();
        }
    }

    //Test case 1.3: test properties
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getProperties",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setProperties",
            args = {Equalizer.Settings.class}
        )
    })
    public void test1_3Properties() throws Exception {
        getEqualizer(0);
        try {
            Equalizer.Settings settings = mEqualizer.getProperties();
            assertTrue("no enough bands", settings.numBands >= MIN_NUMBER_OF_BANDS);
            short newLevel = 0;
            if (settings.bandLevels[0] == 0) {
                newLevel = -600;
            }
            String str = settings.toString();
            settings = new Equalizer.Settings(str);
            settings.curPreset = (short)-1;
            settings.bandLevels[0] = newLevel;
            mEqualizer.setProperties(settings);
            settings = mEqualizer.getProperties();
            assertTrue("setProperties failed",
                    (settings.bandLevels[0] >= (newLevel - TOLERANCE)) &&
                    (settings.bandLevels[0] <= (newLevel + TOLERANCE)));

        } catch (IllegalArgumentException e) {
            fail("Bad parameter value");
        } catch (UnsupportedOperationException e) {
            fail("get parameter() rejected");
        } catch (IllegalStateException e) {
            fail("get parameter() called in wrong state");
        } finally {
            releaseEqualizer();
        }
    }

    //Test case 1.4: test setBandLevel() throws exception after release
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "release",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setBandLevel",
            args = {short.class, short.class}
        )
    })
    public void test1_4SetBandLevelAfterRelease() throws Exception {

        getEqualizer(0);
        mEqualizer.release();
        try {
            mEqualizer.setBandLevel((short)0, (short)0);
        } catch (IllegalStateException e) {
            // test passed
        } finally {
            releaseEqualizer();
        }
    }

    //-----------------------------------------------------------------
    // 2 - Effect enable/disable
    //----------------------------------

    //Test case 2.0: test setEnabled() and getEnabled() in valid state
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setEnabled",
            args = {boolean.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getEnabled",
            args = {}
        )
    })
    public void test2_0SetEnabledGetEnabled() throws Exception {
        getEqualizer(0);
        try {
            mEqualizer.setEnabled(true);
            assertTrue("invalid state from getEnabled", mEqualizer.getEnabled());
            mEqualizer.setEnabled(false);
            assertFalse("invalid state to getEnabled", mEqualizer.getEnabled());

        } catch (IllegalStateException e) {
            fail("setEnabled() in wrong state");
        } finally {
            releaseEqualizer();
        }
    }

    //Test case 2.1: test setEnabled() throws exception after release
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "release",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setEnabled",
            args = {boolean.class}
        )
    })
    public void test2_1SetEnabledAfterRelease() throws Exception {

        getEqualizer(0);
        mEqualizer.release();
        try {
            mEqualizer.setEnabled(true);
        } catch (IllegalStateException e) {
            // test passed
        } finally {
            releaseEqualizer();
        }
    }

    //-----------------------------------------------------------------
    // 3 priority and listeners
    //----------------------------------

    //Test case 3.0: test control status listener
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setControlStatusListener",
            args = {AudioEffect.OnControlStatusChangeListener.class}
        )
    })
    public void test3_0ControlStatusListener() throws Exception {
        mHasControl = true;
        createListenerLooper(true, false, false);
        synchronized(mLock) {
            try {
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Looper creation: wait was interrupted.");
            }
        }
        assertTrue(mInitialized);
        synchronized(mLock) {
            try {
                getEqualizer(0);
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Create second effect: wait was interrupted.");
            } finally {
                releaseEqualizer();
                terminateListenerLooper();
            }
        }
        assertFalse("effect control not lost by effect1", mHasControl);
    }

    //Test case 3.1: test enable status listener
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setEnableStatusListener",
            args = {AudioEffect.OnEnableStatusChangeListener.class}
        )
    })
    public void test3_1EnableStatusListener() throws Exception {
        createListenerLooper(false, true, false);
        synchronized(mLock) {
            try {
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Looper creation: wait was interrupted.");
            }
        }
        assertTrue(mInitialized);
        mEqualizer2.setEnabled(true);
        mIsEnabled = true;
        getEqualizer(0);
        synchronized(mLock) {
            try {
                mEqualizer.setEnabled(false);
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Create second effect: wait was interrupted.");
            } finally {
                releaseEqualizer();
                terminateListenerLooper();
            }
        }
        assertFalse("enable status not updated", mIsEnabled);
    }

    //Test case 3.2: test parameter changed listener
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setParameterListener",
            args = {Equalizer.OnParameterChangeListener.class}
        )
    })
    public void test3_2ParameterChangedListener() throws Exception {
        createListenerLooper(false, false, true);
        synchronized(mLock) {
            try {
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Looper creation: wait was interrupted.");
            }
        }
        assertTrue(mInitialized);
        getEqualizer(0);
        synchronized(mLock) {
            try {
                mChangedParameter = -1;
                mEqualizer.setBandLevel((short)0, (short)0);
                mLock.wait(1000);
            } catch(Exception e) {
                Log.e(TAG, "Create second effect: wait was interrupted.");
            } finally {
                releaseEqualizer();
                terminateListenerLooper();
            }
        }
        assertEquals("parameter change not received",
                Equalizer.PARAM_BAND_LEVEL, mChangedParameter);
    }

    //-----------------------------------------------------------------
    // private methods
    //----------------------------------

    private void getEqualizer(int session) {
         if (mEqualizer == null || session != mSession) {
             if (session != mSession && mEqualizer != null) {
                 mEqualizer.release();
                 mEqualizer = null;
             }
             try {
                mEqualizer = new Equalizer(0, session);
                mSession = session;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getEqualizer() Equalizer not found exception: "+e);
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "getEqualizer() Effect library not loaded exception: "+e);
            }
         }
         assertNotNull("could not create mEqualizer", mEqualizer);
    }

    private void releaseEqualizer() {
        if (mEqualizer != null) {
            mEqualizer.release();
            mEqualizer = null;
        }
    }

    // Initializes the equalizer listener looper
    class ListenerThread extends Thread {
        boolean mControl;
        boolean mEnable;
        boolean mParameter;

        public ListenerThread(boolean control, boolean enable, boolean parameter) {
            super();
            mControl = control;
            mEnable = enable;
            mParameter = parameter;
        }
    }

    private void createListenerLooper(boolean control, boolean enable, boolean parameter) {
        mInitialized = false;
        new ListenerThread(control, enable, parameter) {
            @Override
            public void run() {
                // Set up a looper
                Looper.prepare();

                // Save the looper so that we can terminate this thread
                // after we are done with it.
                mLooper = Looper.myLooper();

                mEqualizer2 = new Equalizer(0, 0);
                assertNotNull("could not create Equalizer2", mEqualizer2);

                if (mControl) {
                    mEqualizer2.setControlStatusListener(
                            new AudioEffect.OnControlStatusChangeListener() {
                        public void onControlStatusChange(
                                AudioEffect effect, boolean controlGranted) {
                            synchronized(mLock) {
                                if (effect == mEqualizer2) {
                                    mHasControl = controlGranted;
                                    mLock.notify();
                                }
                            }
                        }
                    });
                }
                if (mEnable) {
                    mEqualizer2.setEnableStatusListener(
                            new AudioEffect.OnEnableStatusChangeListener() {
                        public void onEnableStatusChange(AudioEffect effect, boolean enabled) {
                            synchronized(mLock) {
                                if (effect == mEqualizer2) {
                                    mIsEnabled = enabled;
                                    mLock.notify();
                                }
                            }
                        }
                    });
                }
                if (mParameter) {
                    mEqualizer2.setParameterListener(new Equalizer.OnParameterChangeListener() {
                        public void onParameterChange(Equalizer effect,
                                int status, int param1, int param2, int value)
                        {
                            synchronized(mLock) {
                                if (effect == mEqualizer2) {
                                    mChangedParameter = param1;
                                    mLock.notify();
                                }
                            }
                        }
                    });
                }

                synchronized(mLock) {
                    mInitialized = true;
                    mLock.notify();
                }
                Looper.loop();  // Blocks forever until Looper.quit() is called.
            }
        }.start();
    }

    // Terminates the listener looper thread.
    private void terminateListenerLooper() {
        if (mEqualizer2 != null) {
            mEqualizer2.release();
            mEqualizer2 = null;
        }
        if (mLooper != null) {
            mLooper.quit();
            mLooper = null;
        }
    }

}