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


package com.android.wallpaper.fall;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.renderscript.RenderScript;
import android.renderscript.RenderScriptGL;
import android.renderscript.RSSurfaceView;

class FallView extends RSSurfaceView {
    private FallRS mRender;

    public FallView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);

        RenderScriptGL RS = createRenderScript(false);
        mRender = new FallRS(w, h);
        mRender.init(RS, getResources(), false);
        mRender.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            //case MotionEvent.ACTION_MOVE:
                mRender.addDrop(event.getX(), event.getY());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    // Ignore
                }
                break;
        }
        return true;
    }
}