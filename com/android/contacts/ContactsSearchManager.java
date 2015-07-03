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

package com.android.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.UI;

/**
 * A convenience class that helps launch contact search from within the app.
 */
public class ContactsSearchManager {

    /**
     * An extra that provides context for search UI and defines the scope for
     * the search queries.
     */
    public static final String ORIGINAL_ACTION_EXTRA_KEY = "originalAction";

    /**
     * An extra that provides context for search UI and defines the scope for
     * the search queries.
     */
    public static final String ORIGINAL_COMPONENT_EXTRA_KEY = "originalComponent";

    /**
     * Starts the contact list activity in the search mode.
     */
    public static void startSearch(Activity context, String initialQuery) {
        context.startActivity(buildIntent(context, initialQuery));
    }

    public static void startSearchForResult(Activity context, String initialQuery,
            int requestCode) {
        context.startActivityForResult(buildIntent(context, initialQuery), requestCode);
    }

    private static Intent buildIntent(Activity context, String initialQuery) {
        Intent intent = new Intent();
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        intent.setAction(UI.FILTER_CONTACTS_ACTION);

        Intent originalIntent = context.getIntent();
        Bundle originalExtras = originalIntent.getExtras();
        if (originalExtras != null) {
            intent.putExtras(originalExtras);
        }
        intent.putExtra(UI.FILTER_TEXT_EXTRA_KEY, initialQuery);
        intent.putExtra(ORIGINAL_ACTION_EXTRA_KEY, originalIntent.getAction());
        intent.putExtra(ORIGINAL_COMPONENT_EXTRA_KEY, originalIntent.getComponent().getClassName());
        return intent;
    }
}
