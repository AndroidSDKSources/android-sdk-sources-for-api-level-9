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

import android.database.Cursor;

public class CursorBackedSourceResult extends CursorBackedSuggestionCursor
        implements SourceResult {

    private final Source mSource;

    public CursorBackedSourceResult(Source source, String userQuery) {
        this(source, userQuery, null);
    }

    public CursorBackedSourceResult(Source source, String userQuery, Cursor cursor) {
        super(userQuery, cursor);
        mSource = source;
    }

    public Source getSource() {
        return mSource;
    }

    @Override
    public Source getSuggestionSource() {
        return mSource;
    }

    public boolean isSuggestionShortcut() {
        return false;
    }

    @Override
    public String toString() {
        return mSource + "[" + getUserQuery() + "]";
    }

}