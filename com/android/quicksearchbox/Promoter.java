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

import java.util.ArrayList;

/**
 * Promoters choose which suggestions to promote from all the available suggestions.
 *
 */
public interface Promoter {

    /**
     * Gets the promoted suggestions.
     *
     * @param shortcuts The shortcuts for the query.
     * @param suggestions The suggestions from each source.
     * @param maxPromoted The maximum number of suggestions to promote.
     * @param promoted List to add the promoted suggestions to.
     */
    void pickPromoted(SuggestionCursor shortcuts,
            ArrayList<CorpusResult> suggestions, int maxPromoted,
            ListSuggestionCursor promoted);

}
