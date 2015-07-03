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

package android.provider.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.ToBeFixed;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Artists.Albums;
import android.provider.cts.MediaStoreAudioTestHelper.Audio1;
import android.provider.cts.MediaStoreAudioTestHelper.Audio2;
import android.test.InstrumentationTestCase;

@TestTargetClass(Albums.class)
public class MediaStore_Audio_Artists_AlbumsTest extends InstrumentationTestCase {
    private ContentResolver mContentResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContentResolver = getInstrumentation().getContext().getContentResolver();
    }

    @TestTargetNew(
      level = TestLevel.COMPLETE,
      method = "getContentUri",
      args = {String.class, long.class}
    )
    @ToBeFixed(bug = "1695243", explanation = "Android API javadocs are incomplete. This is no "
            + "document which describs possible values of the param volumeName.")
    public void testGetContentUri() {
        Uri contentUri = MediaStore.Audio.Artists.Albums.getContentUri(
                MediaStoreAudioTestHelper.INTERNAL_VOLUME_NAME, 1);
        assertNotNull(mContentResolver.query(contentUri, null, null, null, null));
        contentUri = MediaStore.Audio.Artists.Albums.getContentUri(
                MediaStoreAudioTestHelper.EXTERNAL_VOLUME_NAME, 1);
        assertNotNull(mContentResolver.query(contentUri, null, null, null, null));

        // can not accept any other volume names
        String volume = "fakeVolume";
        assertNull(mContentResolver.query(MediaStore.Audio.Artists.Albums.getContentUri(volume, 1),
                null, null, null, null));
    }

    @ToBeFixed(bug = "", explanation = "The result cursor of query for all columns does not "
            + "contain the column Albums.ALBUM_ID.")
    public void testStoreAudioArtistsAlbumsInternal() {
        testStoreAudioArtistsAlbums(true);
    }

    @ToBeFixed(bug = "", explanation = "The result cursor of query for all columns does not "
            + "contain the column Albums.ALBUM_ID.")
    public void testStoreAudioArtistsAlbumsExternal() {
        testStoreAudioArtistsAlbums(false);
    }

    private void testStoreAudioArtistsAlbums(boolean isInternal) {
        // the album item is inserted when inserting audio media
        Uri audioMediaUri = isInternal ? Audio1.getInstance().insertToInternal(mContentResolver)
                : Audio1.getInstance().insertToExternal(mContentResolver);
        // get artist id
        Cursor c = mContentResolver.query(audioMediaUri, new String[] { Media.ARTIST_ID }, null,
                null, null);
        c.moveToFirst();
        Long artistId = c.getLong(c.getColumnIndex(Media.ARTIST_ID));
        c.close();
        Uri artistsAlbumsUri = MediaStore.Audio.Artists.Albums.getContentUri(isInternal ?
                MediaStoreAudioTestHelper.INTERNAL_VOLUME_NAME :
                    MediaStoreAudioTestHelper.EXTERNAL_VOLUME_NAME, artistId);
        // do not support insert operation of the albums
        try {
            mContentResolver.insert(artistsAlbumsUri, new ContentValues());
            fail("Should throw UnsupportedOperationException!");
        } catch (UnsupportedOperationException e) {
            // expected
        }

        try {
            // query
            c = mContentResolver.query(artistsAlbumsUri, null, null, null, null);
            assertEquals(1, c.getCount());
            c.moveToFirst();

            assertEquals(Audio1.ALBUM, c.getString(c.getColumnIndex(Albums.ALBUM)));
            assertNull(c.getString(c.getColumnIndex(Albums.ALBUM_ART)));
            assertNotNull(c.getString(c.getColumnIndex(Albums.ALBUM_KEY)));
            assertEquals(Audio1.ARTIST, c.getString(c.getColumnIndex(Albums.ARTIST)));
            assertEquals(Audio1.YEAR, c.getInt(c.getColumnIndex(Albums.FIRST_YEAR)));
            assertEquals(Audio1.YEAR, c.getInt(c.getColumnIndex(Albums.LAST_YEAR)));
            assertEquals(1, c.getInt(c.getColumnIndex(Albums.NUMBER_OF_SONGS)));
            assertEquals(1, c.getInt(c.getColumnIndex(Albums.NUMBER_OF_SONGS_FOR_ARTIST)));
            // the ALBUM_ID column does not exist
            try {
                c.getColumnIndexOrThrow(Albums.ALBUM_ID);
                fail("Should throw IllegalArgumentException because there is no column with name"
                        + " \"Albums.ALBUM_ID\" in the table");
            } catch (IllegalArgumentException e) {
                // expected
            }
            c.close();

            // do not support update operation of the albums
            ContentValues albumValues = new ContentValues();
            albumValues.put(Albums.ALBUM, Audio2.ALBUM);
            try {
                mContentResolver.update(artistsAlbumsUri, albumValues, null, null);
                fail("Should throw UnsupportedOperationException!");
            } catch (UnsupportedOperationException e) {
                // expected
            }

            // do not support delete operation of the albums
            try {
                mContentResolver.delete(artistsAlbumsUri, null, null);
                fail("Should throw UnsupportedOperationException!");
            } catch (UnsupportedOperationException e) {
                // expected
            }
        } finally {
            mContentResolver.delete(audioMediaUri, null, null);
        }
        // the album items are deleted when deleting the audio media which belongs to the album
        c = mContentResolver.query(artistsAlbumsUri, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();
    }
}
