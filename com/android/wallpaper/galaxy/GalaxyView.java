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


package com.android.wallpaper.galaxy;

import android.renderscript.RSSurfaceView;
import android.renderscript.RenderScriptGL;
import android.content.Context;
import android.view.SurfaceHolder;

class GalaxyView extends RSSurfaceView {

    public GalaxyView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);

        RenderScriptGL RS = createRenderScript(false);
        GalaxyRS render = new GalaxyRS(w, h);
        render.init(RS, getResources(), false);
        render.setOffset(0.5f, 0.0f, 0, 0);
        render.start();
    }
}

