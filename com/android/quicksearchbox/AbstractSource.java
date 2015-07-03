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

package com.android.quicksearchbox;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Abstract suggestion source implementation.
 */
public abstract class AbstractSource implements Source {

    private static final String TAG = "QSB.AbstractSource";

    private final Context mContext;

    private IconLoader mIconLoader;

    public AbstractSource(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    protected IconLoader getIconLoader() {
        if (mIconLoader == null) {
            String iconPackage = getIconPackage();
            mIconLoader = new CachingIconLoader(new PackageIconLoader(mContext, iconPackage));
        }
        return mIconLoader;
    }

    protected abstract String getIconPackage();

    public boolean isVersionCodeCompatible(int version) {
        return getVersionCode() == version;
    }

    public Drawable getIcon(String drawableId) {
        return getIconLoader().getIcon(drawableId);
    }

    public Uri getIconUri(String drawableId) {
        return getIconLoader().getIconUri(drawableId);
    }

    public Intent createSearchIntent(String query, Bundle appData) {
        return createSourceSearchIntent(getIntentComponent(), query, appData);
    }

    public static Intent createSourceSearchIntent(ComponentName activity, String query,
            Bundle appData) {
        if (activity == null) {
            Log.w(TAG, "Tried to create search intent with no target activity");
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setComponent(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // We need CLEAR_TOP to avoid reusing an old task that has other activities
        // on top of the one we want.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SearchManager.USER_QUERY, query);
        intent.putExtra(SearchManager.QUERY, query);
        if (appData != null) {
            intent.putExtra(SearchManager.APP_DATA, appData);
        }
        return intent;
    }

    protected Intent createVoiceWebSearchIntent(Bundle appData) {
        return QsbApplication.get(mContext).getVoiceSearch()
                .createVoiceWebSearchIntent(appData);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            AbstractSource s = (AbstractSource) o;
            return s.getName().equals(getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "Source{name=" + getName() + "}";
    }

}
