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

package com.android.certinstaller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.security.Credentials;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class that deals with certificate files on the SD card.
 */
public class CertFile extends PreferenceActivity implements FileFilter {
    static final int CERT_READ_ERROR = R.string.cert_read_error;
    static final int CERT_TOO_LARGE_ERROR = R.string.cert_too_large_error;
    static final int CERT_FILE_MISSING_ERROR = R.string.cert_missing_error;

    static final String DOWNLOAD_DIR = "download";

    private static final String TAG = "CertFile";

    private static final String CERT_EXT = ".crt";
    private static final String PKCS12_EXT = ".p12";

    private static final String CERT_FILE_KEY = "cf";
    private static final int MAX_FILE_SIZE = 1000000;
    private static final int REQUEST_INSTALL_CODE = 1;

    private File mCertFile;

    @Override
    protected void onSaveInstanceState(Bundle outStates) {
        super.onSaveInstanceState(outStates);
        if (mCertFile != null) {
            outStates.putString(CERT_FILE_KEY, mCertFile.getAbsolutePath());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedStates) {
        super.onRestoreInstanceState(savedStates);
        String path = savedStates.getString(CERT_FILE_KEY);
        if (path != null) mCertFile = new File(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INSTALL_CODE) {
            onInstallationDone((resultCode == RESULT_OK)
                    && Util.deleteFile(mCertFile));
            mCertFile = null;
        } else {
            Log.w(TAG, "unknown request code: " + requestCode);
        }
    }

    /**
     * Called when installation is done.
     *
     * @param success true if installation is done successfully
     */
    protected void onInstallationDone(boolean success) {
    }

    /**
     * Called when an error occurs when reading a certificate file.
     *
     * @param errorId one of {@link #CERT_READ_ERROR},
     *      {@link #CERT_TOO_LARGE_ERROR} and {@link #CERT_FILE_MISSING_ERROR}
     */
    protected void onError(int errorId) {
    }

    /**
     * Returns a list of certificate files found on the SD card.
     */
    protected List<File> getAllCertFiles() {
        List<File> allFiles = new ArrayList<File>();
        File root = Environment.getExternalStorageDirectory();

        File download = new File(root, DOWNLOAD_DIR);
        if (download != null) {
            File[] files = download.listFiles(this);
            if (files != null) Collections.addAll(allFiles, files);
        }

        File[] files = root.listFiles(this);
        if (files != null) Collections.addAll(allFiles, files);

        return allFiles;
    }

    /**
     * Invokes {@link CertInstaller} to install the certificate(s) in the file.
     *
     * @param file the certificate file
     */
    protected void installFromFile(File file) {
        Log.d(TAG, "install cert from " + file);

        if (file.exists()) {
            if (file.length() < MAX_FILE_SIZE) {
                byte[] data = Util.readFile(file);
                if (data == null) {
                    toastError(CERT_READ_ERROR);
                    onError(CERT_READ_ERROR);
                    return;
                }
                mCertFile = file;
                install(file.getName(), data);
            } else {
                Log.w(TAG, "cert file is too large: " + file.length());
                toastError(CERT_TOO_LARGE_ERROR);
                onError(CERT_TOO_LARGE_ERROR);
            }
        } else {
            Log.w(TAG, "cert file does not exist");
            toastError(CERT_FILE_MISSING_ERROR);
            onError(CERT_FILE_MISSING_ERROR);
        }
    }

    public boolean accept(File file) {
        if (!file.isDirectory()) {
            return isFileAcceptable(file.getPath());
        } else {
            return false;
        }
    }

    protected boolean isFileAcceptable(String path) {
        return (path.endsWith(PKCS12_EXT) || path.endsWith(CERT_EXT));
    }

    protected boolean isSdCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private void install(String fileName, byte[] value) {
        Intent intent = new Intent(this, CertInstaller.class);
        intent.putExtra(CredentialHelper.CERT_NAME_KEY, fileName);
        if (fileName.endsWith(PKCS12_EXT)) {
            intent.putExtra(Credentials.PKCS12, value);
        } else {
            intent.putExtra(Credentials.CERTIFICATE, value);
        }
        startActivityForResult(intent, REQUEST_INSTALL_CODE);
    }

    private void toastError(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_LONG).show();
    }
}
