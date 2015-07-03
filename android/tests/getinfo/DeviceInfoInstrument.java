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

package android.tests.getinfo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceInfoInstrument extends Instrumentation {

    private static final String TAG = "DeviceInfoInstrument";

    // constants for device info attributes to be sent as instrumentation keys
    // these values should correspond to attributes defined in cts_result.xsd
    private static final String OPEN_GL_ES_VERSION = "openGlEsVersion";
    private static final String PROCESSES = "processes";
    private static final String FEATURES = "features";
    private static final String PHONE_NUMBER = "phoneNumber";
    public static final String LOCALES = "locales";
    private static final String IMSI = "imsi";
    private static final String IMEI = "imei";
    private static final String NETWORK = "network";
    public static final String KEYPAD = "keypad";
    public static final String NAVIGATION = "navigation";
    public static final String TOUCH_SCREEN = "touch";
    private static final String SCREEN_Y_DENSITY = "Ydpi";
    private static final String SCREEN_X_DENSITY = "Xdpi";
    private static final String SCREEN_SIZE = "screen_size";
    private static final String SCREEN_DENSITY_BUCKET = "screen_density_bucket";
    private static final String SCREEN_DENSITY = "screen_density";
    private static final String SCREEN_HEIGHT = "screen_height";
    private static final String SCREEN_WIDTH = "screen_width";
    private static final String VERSION_SDK = "androidPlatformVersion";
    private static final String VERSION_RELEASE = "buildVersion";
    private static final String BUILD_ABI = "build_abi";
    private static final String BUILD_ABI2 = "build_abi2";
    private static final String BUILD_FINGERPRINT = "build_fingerprint";
    private static final String BUILD_TYPE = "build_type";
    private static final String BUILD_MODEL = "build_model";
    private static final String BUILD_BRAND = "build_brand";
    private static final String BUILD_MANUFACTURER = "build_manufacturer";
    private static final String BUILD_BOARD = "build_board";
    private static final String BUILD_DEVICE = "build_device";
    private static final String PRODUCT_NAME = "buildName";
    private static final String BUILD_ID = "buildID";
    private static Bundle mResults = new Bundle();

    public DeviceInfoInstrument() {
        super();
    }

    @Override
    public void onCreate(Bundle arguments) {
        start();
    }

    @Override
    public void onStart() {

        addResult(BUILD_ID, Build.ID);
        addResult(PRODUCT_NAME, Build.PRODUCT);
        addResult(BUILD_DEVICE, Build.DEVICE);
        addResult(BUILD_BOARD, Build.BOARD);
        addResult(BUILD_MANUFACTURER, Build.MANUFACTURER);
        addResult(BUILD_BRAND, Build.BRAND);
        addResult(BUILD_MODEL, Build.MODEL);
        addResult(BUILD_TYPE, Build.TYPE);
        addResult(BUILD_FINGERPRINT, Build.FINGERPRINT);
        addResult(BUILD_ABI, Build.CPU_ABI);
        addResult(BUILD_ABI2, Build.CPU_ABI2);

        addResult(VERSION_RELEASE, Build.VERSION.RELEASE);
        addResult(VERSION_SDK, Build.VERSION.SDK);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        d.getMetrics(metrics);
        addResult(SCREEN_WIDTH, metrics.widthPixels);
        addResult(SCREEN_HEIGHT, metrics.heightPixels);
        addResult(SCREEN_DENSITY, metrics.density);
        addResult(SCREEN_X_DENSITY, metrics.xdpi);
        addResult(SCREEN_Y_DENSITY, metrics.ydpi);

        String screenDensityBucket = getScreenDensityBucket(metrics);
        addResult(SCREEN_DENSITY_BUCKET, screenDensityBucket);

        String screenSize = getScreenSize();
        addResult(SCREEN_SIZE, screenSize);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this.getContext(), DeviceInfoActivity.class);

        DeviceInfoActivity activity = (DeviceInfoActivity) startActivitySync(intent);
        waitForIdleSync();
        activity.waitForAcitityToFinish();

        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        // network
        String network = tm.getNetworkOperatorName();
        addResult(NETWORK, network);

        // imei
        String imei = tm.getDeviceId();
        addResult(IMEI, imei);

        // imsi
        String imsi = tm.getSubscriberId();
        addResult(IMSI, imsi);

        // phone number
        String phoneNumber = tm.getLine1Number();
        addResult(PHONE_NUMBER, phoneNumber);

        // features
        String features = getFeatures();
        addResult(FEATURES, features);

        // processes
        String processes = getProcesses();
        addResult(PROCESSES, processes);

        // OpenGL ES version
        String openGlEsVersion = getOpenGlEsVersion();
        addResult(OPEN_GL_ES_VERSION, openGlEsVersion);


        finish(Activity.RESULT_OK, mResults);
    }

    /**
     * Add string result.
     *
     * @param key the string of the key name.
     * @param value string value.
     */
    static void addResult(final String key, final String value){
        mResults.putString(key, value);
    }

    /**
     * Add integer result.
     *
     * @param key the string of the key name.
     * @param value integer value.
     */
    private void addResult(final String key, final int value){
        mResults.putInt(key, value);
    }

    /**
     * Add float result.
     *
     * @param key the string of the key name.
     * @param value float value.
     */
    private void addResult(final String key, final float value){
        mResults.putFloat(key, value);
    }

    private String getScreenSize() {
        Configuration config = getContext().getResources().getConfiguration();
        int screenLayout = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        String screenSize = String.format("0x%x", screenLayout);
        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                screenSize = "small";
                break;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                screenSize = "normal";
                break;

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                screenSize = "large";
                break;

            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                screenSize = "undefined";
                break;
        }
        return screenSize;
    }

    private String getScreenDensityBucket(DisplayMetrics metrics) {
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";

            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";

            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";

            case 320:
                return "xdpi";

            default:
                return "" + metrics.densityDpi;
        }
    }

    /**
     * Return a summary of the device's feature as a semi-colon-delimited list of colon separated
     * name and availability pairs like "feature1:sdk:true;feature2:sdk:false;feature3:other:true;".
     */
    private String getFeatures() {
        StringBuilder features = new StringBuilder();

        try {
            Set<String> checkedFeatures = new HashSet<String>();

            PackageManager packageManager = getContext().getPackageManager();
            for (String featureName : getPackageManagerFeatures()) {
                checkedFeatures.add(featureName);
                boolean hasFeature = packageManager.hasSystemFeature(featureName);
                addFeature(features, featureName, "sdk", hasFeature);
            }

            FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
            if (featureInfos != null) {
                for (FeatureInfo featureInfo : featureInfos) {
                    if (featureInfo.name != null && !checkedFeatures.contains(featureInfo.name)) {
                        addFeature(features, featureInfo.name, "other", true);
                    }
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error getting features: " + exception.getMessage(), exception);
        }

        return features.toString();
    }

    private static void addFeature(StringBuilder features, String name, String type,
            boolean available) {
        features.append(name).append(':').append(type).append(':').append(available).append(';');
    }

    /**
     * Use reflection to get the features defined by the SDK. If there are features that do not fit
     * the convention of starting with "FEATURE_" then they will still be shown under the
     * "Other Features" section.
     *
     * @return list of feature names from sdk
     */
    private List<String> getPackageManagerFeatures() {
        try {
            List<String> features = new ArrayList<String>();
            Field[] fields = PackageManager.class.getFields();
            for (Field field : fields) {
                if (field.getName().startsWith("FEATURE_")) {
                    String feature = (String) field.get(null);
                    features.add(feature);
                }
            }
            return features;
        } catch (IllegalAccessException illegalAccess) {
            throw new RuntimeException(illegalAccess);
        }
    }

    /**
     * Return a semi-colon-delimited list of the root processes that were running on the phone
     * or an error message.
     */
    private static String getProcesses() {
        StringBuilder builder = new StringBuilder();

        try {
            String[] rootProcesses = RootProcessScanner.getRootProcesses();
            for (String rootProcess : rootProcesses) {
                builder.append(rootProcess).append(';');
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error getting processes: " + exception.getMessage(), exception);
            builder.append(exception.getMessage());
        }

        return builder.toString();
    }

    /** @return a string containing the Open GL ES version number or an error message */
    private String getOpenGlEsVersion() {
        PackageManager packageManager = getContext().getPackageManager();
        FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open gl es version feature.
                if (featureInfo.name == null) {
                    return featureInfo.getGlEsVersion();
                }
            }
        }
        return "No feature for Open GL ES version.";
    }
}
