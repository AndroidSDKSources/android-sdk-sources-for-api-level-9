/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.eclipse.adt.internal.resources.configurations;

import com.android.ide.eclipse.adt.internal.resources.manager.ResourceFolderType;


/**
 * Represents the configuration for Resource Folders. All the properties have a default
 * value which means that the property is not set.
 */
public final class FolderConfiguration implements Comparable<FolderConfiguration> {
    public final static String QUALIFIER_SEP = "-"; //$NON-NLS-1$

    private final ResourceQualifier[] mQualifiers = new ResourceQualifier[INDEX_COUNT];

    private final static int INDEX_COUNTRY_CODE       = 0;
    private final static int INDEX_NETWORK_CODE       = 1;
    private final static int INDEX_LANGUAGE           = 2;
    private final static int INDEX_REGION             = 3;
    private final static int INDEX_SCREEN_SIZE        = 4;
    private final static int INDEX_SCREEN_RATIO       = 5;
    private final static int INDEX_SCREEN_ORIENTATION = 6;
    private final static int INDEX_DOCK_MODE          = 7;
    private final static int INDEX_NIGHT_MODE         = 8;
    private final static int INDEX_PIXEL_DENSITY      = 9;
    private final static int INDEX_TOUCH_TYPE         = 10;
    private final static int INDEX_KEYBOARD_STATE     = 11;
    private final static int INDEX_TEXT_INPUT_METHOD  = 12;
    private final static int INDEX_NAVIGATION_STATE   = 14;
    private final static int INDEX_NAVIGATION_METHOD  = 15;
    private final static int INDEX_SCREEN_DIMENSION   = 16;
    private final static int INDEX_VERSION            = 17;
    private final static int INDEX_COUNT              = 18;

    /**
     * Returns the number of {@link ResourceQualifier} that make up a Folder configuration.
     */
    public static int getQualifierCount() {
        return INDEX_COUNT;
    }

    /**
     * Sets the config from the qualifiers of a given <var>config</var>.
     * <p/>This is equivalent to <code>set(config, false)</code>
     * @param config the configuration to set
     *
     * @see #set(FolderConfiguration, boolean)
     */
    public void set(FolderConfiguration config) {
        set(config, false /*nonFakeValuesOnly*/);
    }

    /**
     * Sets the config from the qualifiers of a given <var>config</var>.
     * @param config the configuration to set
     * @param nonFakeValuesOnly if set to true this ignore qualifiers for which the
     * current value is a fake value.
     *
     * @see ResourceQualifier#hasFakeValue()
     */
    public void set(FolderConfiguration config, boolean nonFakeValuesOnly) {
        if (config != null) {
            for (int i = 0 ; i < INDEX_COUNT ; i++) {
                ResourceQualifier q = config.mQualifiers[i];
                if (nonFakeValuesOnly == false || q == null || q.hasFakeValue() == false) {
                    mQualifiers[i] = q;
                }
            }
        }
    }

    /**
     * Removes the qualifiers from the receiver if they are present (and valid)
     * in the given configuration.
     */
    public void substract(FolderConfiguration config) {
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            if (config.mQualifiers[i] != null && config.mQualifiers[i].isValid()) {
                mQualifiers[i] = null;
            }
        }
    }

    /**
     * Returns the first invalid qualifier, or <code>null<code> if they are all valid (or if none
     * exists).
     */
    public ResourceQualifier getInvalidQualifier() {
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            if (mQualifiers[i] != null && mQualifiers[i].isValid() == false) {
                return mQualifiers[i];
            }
        }

        // all allocated qualifiers are valid, we return null.
        return null;
    }

    /**
     * Returns whether the Region qualifier is valid. Region qualifier can only be present if a
     * Language qualifier is present as well.
     * @return true if the Region qualifier is valid.
     */
    public boolean checkRegion() {
        if (mQualifiers[INDEX_LANGUAGE] == null && mQualifiers[INDEX_REGION] != null) {
            return false;
        }

        return true;
    }

    /**
     * Adds a qualifier to the {@link FolderConfiguration}
     * @param qualifier the {@link ResourceQualifier} to add.
     */
    public void addQualifier(ResourceQualifier qualifier) {
        if (qualifier instanceof CountryCodeQualifier) {
            mQualifiers[INDEX_COUNTRY_CODE] = qualifier;
        } else if (qualifier instanceof NetworkCodeQualifier) {
            mQualifiers[INDEX_NETWORK_CODE] = qualifier;
        } else if (qualifier instanceof LanguageQualifier) {
            mQualifiers[INDEX_LANGUAGE] = qualifier;
        } else if (qualifier instanceof RegionQualifier) {
            mQualifiers[INDEX_REGION] = qualifier;
        } else if (qualifier instanceof ScreenSizeQualifier) {
            mQualifiers[INDEX_SCREEN_SIZE] = qualifier;
        } else if (qualifier instanceof ScreenRatioQualifier) {
            mQualifiers[INDEX_SCREEN_RATIO] = qualifier;
        } else if (qualifier instanceof ScreenOrientationQualifier) {
            mQualifiers[INDEX_SCREEN_ORIENTATION] = qualifier;
        } else if (qualifier instanceof DockModeQualifier) {
            mQualifiers[INDEX_DOCK_MODE] = qualifier;
        } else if (qualifier instanceof NightModeQualifier) {
            mQualifiers[INDEX_NIGHT_MODE] = qualifier;
        } else if (qualifier instanceof PixelDensityQualifier) {
            mQualifiers[INDEX_PIXEL_DENSITY] = qualifier;
        } else if (qualifier instanceof TouchScreenQualifier) {
            mQualifiers[INDEX_TOUCH_TYPE] = qualifier;
        } else if (qualifier instanceof KeyboardStateQualifier) {
            mQualifiers[INDEX_KEYBOARD_STATE] = qualifier;
        } else if (qualifier instanceof TextInputMethodQualifier) {
            mQualifiers[INDEX_TEXT_INPUT_METHOD] = qualifier;
        } else if (qualifier instanceof NavigationStateQualifier) {
            mQualifiers[INDEX_NAVIGATION_STATE] = qualifier;
        } else if (qualifier instanceof NavigationMethodQualifier) {
            mQualifiers[INDEX_NAVIGATION_METHOD] = qualifier;
        } else if (qualifier instanceof ScreenDimensionQualifier) {
            mQualifiers[INDEX_SCREEN_DIMENSION] = qualifier;
        } else if (qualifier instanceof VersionQualifier) {
            mQualifiers[INDEX_VERSION] = qualifier;
        }
    }

    /**
     * Removes a given qualifier from the {@link FolderConfiguration}.
     * @param qualifier the {@link ResourceQualifier} to remove.
     */
    public void removeQualifier(ResourceQualifier qualifier) {
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            if (mQualifiers[i] == qualifier) {
                mQualifiers[i] = null;
                return;
            }
        }
    }

    /**
     * Returns a qualifier by its index. The total number of qualifiers can be accessed by
     * {@link #getQualifierCount()}.
     * @param index the index of the qualifier to return.
     * @return the qualifier or null if there are none at the index.
     */
    public ResourceQualifier getQualifier(int index) {
        return mQualifiers[index];
    }

    public void setCountryCodeQualifier(CountryCodeQualifier qualifier) {
        mQualifiers[INDEX_COUNTRY_CODE] = qualifier;
    }

    public CountryCodeQualifier getCountryCodeQualifier() {
        return (CountryCodeQualifier)mQualifiers[INDEX_COUNTRY_CODE];
    }

    public void setNetworkCodeQualifier(NetworkCodeQualifier qualifier) {
        mQualifiers[INDEX_NETWORK_CODE] = qualifier;
    }

    public NetworkCodeQualifier getNetworkCodeQualifier() {
        return (NetworkCodeQualifier)mQualifiers[INDEX_NETWORK_CODE];
    }

    public void setLanguageQualifier(LanguageQualifier qualifier) {
        mQualifiers[INDEX_LANGUAGE] = qualifier;
    }

    public LanguageQualifier getLanguageQualifier() {
        return (LanguageQualifier)mQualifiers[INDEX_LANGUAGE];
    }

    public void setRegionQualifier(RegionQualifier qualifier) {
        mQualifiers[INDEX_REGION] = qualifier;
    }

    public RegionQualifier getRegionQualifier() {
        return (RegionQualifier)mQualifiers[INDEX_REGION];
    }

    public void setScreenSizeQualifier(ScreenSizeQualifier qualifier) {
        mQualifiers[INDEX_SCREEN_SIZE] = qualifier;
    }

    public ScreenSizeQualifier getScreenSizeQualifier() {
        return (ScreenSizeQualifier)mQualifiers[INDEX_SCREEN_SIZE];
    }

    public void setScreenRatioQualifier(ScreenRatioQualifier qualifier) {
        mQualifiers[INDEX_SCREEN_RATIO] = qualifier;
    }

    public ScreenRatioQualifier getScreenRatioQualifier() {
        return (ScreenRatioQualifier)mQualifiers[INDEX_SCREEN_RATIO];
    }

    public void setScreenOrientationQualifier(ScreenOrientationQualifier qualifier) {
        mQualifiers[INDEX_SCREEN_ORIENTATION] = qualifier;
    }

    public ScreenOrientationQualifier getScreenOrientationQualifier() {
        return (ScreenOrientationQualifier)mQualifiers[INDEX_SCREEN_ORIENTATION];
    }

    public void setDockModeQualifier(DockModeQualifier qualifier) {
        mQualifiers[INDEX_DOCK_MODE] = qualifier;
    }

    public DockModeQualifier getDockModeQualifier() {
        return (DockModeQualifier)mQualifiers[INDEX_DOCK_MODE];
    }

    public void setNightModeQualifier(NightModeQualifier qualifier) {
        mQualifiers[INDEX_NIGHT_MODE] = qualifier;
    }

    public NightModeQualifier getNightModeQualifier() {
        return (NightModeQualifier)mQualifiers[INDEX_NIGHT_MODE];
    }

    public void setPixelDensityQualifier(PixelDensityQualifier qualifier) {
        mQualifiers[INDEX_PIXEL_DENSITY] = qualifier;
    }

    public PixelDensityQualifier getPixelDensityQualifier() {
        return (PixelDensityQualifier)mQualifiers[INDEX_PIXEL_DENSITY];
    }

    public void setTouchTypeQualifier(TouchScreenQualifier qualifier) {
        mQualifiers[INDEX_TOUCH_TYPE] = qualifier;
    }

    public TouchScreenQualifier getTouchTypeQualifier() {
        return (TouchScreenQualifier)mQualifiers[INDEX_TOUCH_TYPE];
    }

    public void setKeyboardStateQualifier(KeyboardStateQualifier qualifier) {
        mQualifiers[INDEX_KEYBOARD_STATE] = qualifier;
    }

    public KeyboardStateQualifier getKeyboardStateQualifier() {
        return (KeyboardStateQualifier)mQualifiers[INDEX_KEYBOARD_STATE];
    }

    public void setTextInputMethodQualifier(TextInputMethodQualifier qualifier) {
        mQualifiers[INDEX_TEXT_INPUT_METHOD] = qualifier;
    }

    public TextInputMethodQualifier getTextInputMethodQualifier() {
        return (TextInputMethodQualifier)mQualifiers[INDEX_TEXT_INPUT_METHOD];
    }

    public void setNavigationStateQualifier(NavigationStateQualifier qualifier) {
        mQualifiers[INDEX_NAVIGATION_STATE] = qualifier;
    }

    public NavigationStateQualifier getNavigationStateQualifier() {
        return (NavigationStateQualifier)mQualifiers[INDEX_NAVIGATION_STATE];
    }

    public void setNavigationMethodQualifier(NavigationMethodQualifier qualifier) {
        mQualifiers[INDEX_NAVIGATION_METHOD] = qualifier;
    }

    public NavigationMethodQualifier getNavigationMethodQualifier() {
        return (NavigationMethodQualifier)mQualifiers[INDEX_NAVIGATION_METHOD];
    }

    public void setScreenDimensionQualifier(ScreenDimensionQualifier qualifier) {
        mQualifiers[INDEX_SCREEN_DIMENSION] = qualifier;
    }

    public ScreenDimensionQualifier getScreenDimensionQualifier() {
        return (ScreenDimensionQualifier)mQualifiers[INDEX_SCREEN_DIMENSION];
    }

    public void setVersionQualifier(VersionQualifier qualifier) {
        mQualifiers[INDEX_VERSION] = qualifier;
    }

    public VersionQualifier getVersionQualifier() {
        return (VersionQualifier)mQualifiers[INDEX_VERSION];
    }

    /**
     * Returns whether an object is equals to the receiver.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof FolderConfiguration) {
            FolderConfiguration fc = (FolderConfiguration)obj;
            for (int i = 0 ; i < INDEX_COUNT ; i++) {
                ResourceQualifier qualifier = mQualifiers[i];
                ResourceQualifier fcQualifier = fc.mQualifiers[i];
                if (qualifier != null) {
                    if (qualifier.equals(fcQualifier) == false) {
                        return false;
                    }
                } else if (fcQualifier != null) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Returns whether the Configuration has only default values.
     */
    public boolean isDefault() {
        for (ResourceQualifier irq : mQualifiers) {
            if (irq != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the name of a folder with the configuration.
     */
    public String getFolderName(ResourceFolderType folder) {
        StringBuilder result = new StringBuilder(folder.getName());

        for (ResourceQualifier qualifier : mQualifiers) {
            if (qualifier != null) {
                String segment = qualifier.getFolderSegment();
                if (segment != null && segment.length() > 0) {
                    result.append(QUALIFIER_SEP);
                    result.append(segment);
                }
            }
        }

        return result.toString();
    }

    /**
     * Returns {@link #toDisplayString()}.
     */
    @Override
    public String toString() {
        return toDisplayString();
    }

    /**
     * Returns a string valid for display purpose.
     */
    public String toDisplayString() {
        if (isDefault()) {
            return "default";
        }

        StringBuilder result = null;
        int index = 0;
        ResourceQualifier qualifier = null;

        // pre- language/region qualifiers
        while (index < INDEX_LANGUAGE) {
            qualifier = mQualifiers[index++];
            if (qualifier != null) {
                if (result == null) {
                    result = new StringBuilder();
                } else {
                    result.append(", "); //$NON-NLS-1$
                }
                result.append(qualifier.getLongDisplayValue());

            }
        }

        // process the language/region qualifier in a custom way, if there are both non null.
        if (mQualifiers[INDEX_LANGUAGE] != null && mQualifiers[INDEX_REGION] != null) {
            String language = mQualifiers[INDEX_LANGUAGE].getLongDisplayValue();
            String region = mQualifiers[INDEX_REGION].getLongDisplayValue();

            if (result == null) {
                result = new StringBuilder();
            } else {
                result.append(", "); //$NON-NLS-1$
            }
            result.append(String.format("Locale %s_%s", language, region)); //$NON-NLS-1$

            index += 2;
        }

        // post language/region qualifiers.
        while (index < INDEX_COUNT) {
            qualifier = mQualifiers[index++];
            if (qualifier != null) {
                if (result == null) {
                    result = new StringBuilder();
                } else {
                    result.append(", "); //$NON-NLS-1$
                }
                result.append(qualifier.getLongDisplayValue());

            }
        }

        return result == null ? null : result.toString();
    }

    public int compareTo(FolderConfiguration folderConfig) {
        // default are always at the top.
        if (isDefault()) {
            if (folderConfig.isDefault()) {
                return 0;
            }
            return -1;
        }

        // now we compare the qualifiers
        for (int i = 0 ; i < INDEX_COUNT; i++) {
            ResourceQualifier qualifier1 = mQualifiers[i];
            ResourceQualifier qualifier2 = folderConfig.mQualifiers[i];

            if (qualifier1 == null) {
                if (qualifier2 == null) {
                    continue;
                } else {
                    return -1;
                }
            } else {
                if (qualifier2 == null) {
                    return 1;
                } else {
                    int result = qualifier1.compareTo(qualifier2);

                    if (result == 0) {
                        continue;
                    }

                    return result;
                }
            }
        }

        // if we arrive here, all the qualifier matches
        return 0;
    }

    /**
     * Returns whether the configuration is a match for the given reference config.
     * <p/>A match means that, for each qualifier of this config
     * <ul>
     * <li>The reference config has no value set
     * <li>or, the qualifier of the reference config is a match. Depending on the qualifier type
     * this does not mean the same exact value.</li>
     * </ul>
     * @param referenceConfig The reference configuration to test against.
     * @return true if the configuration matches.
     */
    public boolean isMatchFor(FolderConfiguration referenceConfig) {
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            ResourceQualifier testQualifier = mQualifiers[i];
            ResourceQualifier referenceQualifier = referenceConfig.mQualifiers[i];

            // it's only a non match if both qualifiers are non-null, and they don't match.
            if (testQualifier != null && referenceQualifier != null &&
                        testQualifier.isMatchFor(referenceQualifier) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the index of the first non null {@link ResourceQualifier} starting at index
     * <var>startIndex</var>
     * @param startIndex
     * @return -1 if no qualifier was found.
     */
    public int getHighestPriorityQualifier(int startIndex) {
        for (int i = startIndex ; i < INDEX_COUNT ; i++) {
            if (mQualifiers[i] != null) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Create default qualifiers.
     */
    public void createDefault() {
        mQualifiers[INDEX_COUNTRY_CODE] = new CountryCodeQualifier();
        mQualifiers[INDEX_NETWORK_CODE] = new NetworkCodeQualifier();
        mQualifiers[INDEX_LANGUAGE] = new LanguageQualifier();
        mQualifiers[INDEX_REGION] = new RegionQualifier();
        mQualifiers[INDEX_SCREEN_SIZE] = new ScreenSizeQualifier();
        mQualifiers[INDEX_SCREEN_RATIO] = new ScreenRatioQualifier();
        mQualifiers[INDEX_SCREEN_ORIENTATION] = new ScreenOrientationQualifier();
        mQualifiers[INDEX_DOCK_MODE] = new DockModeQualifier();
        mQualifiers[INDEX_NIGHT_MODE] = new NightModeQualifier();
        mQualifiers[INDEX_PIXEL_DENSITY] = new PixelDensityQualifier();
        mQualifiers[INDEX_TOUCH_TYPE] = new TouchScreenQualifier();
        mQualifiers[INDEX_KEYBOARD_STATE] = new KeyboardStateQualifier();
        mQualifiers[INDEX_TEXT_INPUT_METHOD] = new TextInputMethodQualifier();
        mQualifiers[INDEX_NAVIGATION_STATE] = new NavigationStateQualifier();
        mQualifiers[INDEX_NAVIGATION_METHOD] = new NavigationMethodQualifier();
        mQualifiers[INDEX_SCREEN_DIMENSION] = new ScreenDimensionQualifier();
        mQualifiers[INDEX_VERSION] = new VersionQualifier();
    }

    /**
     * Returns an array of all the non null qualifiers.
     */
    public ResourceQualifier[] getQualifiers() {
        int count = 0;
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            if (mQualifiers[i] != null) {
                count++;
            }
        }

        ResourceQualifier[] array = new ResourceQualifier[count];
        int index = 0;
        for (int i = 0 ; i < INDEX_COUNT ; i++) {
            if (mQualifiers[i] != null) {
                array[index++] = mQualifiers[i];
            }
        }

        return array;
    }
}
