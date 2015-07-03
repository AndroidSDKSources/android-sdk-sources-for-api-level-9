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

package signature.model;

/**
 * Type of the modeled class.
 */
public enum Kind {
    CLASS("class"), INTERFACE("interface"), ANNOTATION("@interface"), ENUM(
            "enum"), UNINITIALIZED("UNINITIALIZED");

    private final String name;

    Kind(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
