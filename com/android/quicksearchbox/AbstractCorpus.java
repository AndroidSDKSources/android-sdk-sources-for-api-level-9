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

package com.android.quicksearchbox;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Base class for corpus implementations.
 */
public abstract class AbstractCorpus implements Corpus {

    private final Context mContext;

    private final Config mConfig;

    public AbstractCorpus(Context context, Config config) {
        mContext = context;
        mConfig = config;
    }

    protected Context getContext() {
        return mContext;
    }

    public boolean isCorpusEnabled() {
        boolean defaultEnabled = isCorpusDefaultEnabled();
        String sourceEnabledPref = SearchSettings.getCorpusEnabledPreference(this);
        SharedPreferences prefs = SearchSettings.getSearchPreferences(mContext);
        return prefs.getBoolean(sourceEnabledPref, defaultEnabled);
    }

    public boolean isCorpusDefaultEnabled() {
        return mConfig.isCorpusEnabledByDefault(getName());
    }

    public boolean isCorpusHidden() {
        return mConfig.isCorpusHidden(getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            return getName().equals(((Corpus) o).getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

}
