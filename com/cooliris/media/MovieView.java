/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.cooliris.media;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cooliris.app.App;
import com.cooliris.app.Res;

/**
 * This activity plays a video from a specified URI.
 */
public class MovieView extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = "MovieView";

    private App mApp = null; 
    private MovieViewControl mControl;
    private boolean mFinishOnCompletion;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mApp = new App(MovieView.this);
        setContentView(Res.layout.movie_view);
        View rootView = findViewById(Res.id.root);
        Intent intent = getIntent();
        mControl = new MovieViewControl(rootView, this, intent.getData()) {
            @Override
            public void onCompletion() {
                if (mFinishOnCompletion) {
                    finish();
                }
            }
        };
        if (intent.hasExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)) {
            int orientation = intent.getIntExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            if (orientation != getRequestedOrientation()) {
                setRequestedOrientation(orientation);
            }
        }
        mFinishOnCompletion = intent.getBooleanExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
        win.setAttributes(winParams);
    }

    @Override
    public void onPause() {
        mControl.onPause();
        super.onPause();
    	mApp.onPause();
    }

    @Override
    public void onResume() {
        mControl.onResume();
        super.onResume();
    	mApp.onResume();
    }
    
    @Override
    public void onDestroy() {
        mControl.onDestroy();
    	mApp.shutdown();
    	super.onDestroy();
    }
}
