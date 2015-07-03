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


package com.android.musicvis;

import android.content.res.Resources;
import android.renderscript.RenderScriptGL;
import android.renderscript.ScriptC;
import android.view.MotionEvent;

public abstract class RenderScriptScene {
    protected int mWidth;
    protected int mHeight;
    protected boolean mPreview;
    protected Resources mResources;
    protected RenderScriptGL mRS;
    protected ScriptC mScript;

    public RenderScriptScene(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void init(RenderScriptGL rs, Resources res, boolean isPreview) {
        mRS = rs;
        mResources = res;
        mPreview = isPreview;
        mScript = createScript();
    }

    public boolean isPreview() {
        return mPreview;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Resources getResources() {
        return mResources;
    }

    public RenderScriptGL getRS() {
        return mRS;
    }

    public ScriptC getScript() {
        return mScript;
    }

    protected abstract ScriptC createScript();

    public void stop() {
        mRS.contextBindRootScript(null);
    }

    public void start() {
        mRS.contextBindRootScript(mScript);
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setOffset(float xOffset, float yOffset,
            float xStep, float yStep, int xPixels, int yPixels) {
    }
    public void onTouchEvent(MotionEvent event) {
    }

}
