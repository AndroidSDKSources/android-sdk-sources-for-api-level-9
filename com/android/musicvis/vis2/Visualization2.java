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

package com.android.musicvis.vis2;

import com.android.musicvis.RenderScriptWallpaper;
import com.android.musicvis.RenderScriptScene;

public class Visualization2 extends RenderScriptWallpaper<Visualization2RS> {

    @Override
    protected Visualization2RS createScene(int width, int height) {
        return new Visualization2RS(width, height);
    }
}

