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

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.test.InstrumentationTestCase;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

@TestTargetClass(MediaStore.Images.Media.class)
public class MediaStore_Images_MediaTest extends InstrumentationTestCase {
    private static final String MIME_TYPE_JPEG = "image/jpeg";

    private static final String TEST_TITLE1 = "test title1";

    private static final String TEST_DESCRIPTION1 = "test description1";

    private static final String TEST_TITLE2 = "test title2";

    private static final String TEST_DESCRIPTION2 = "test description2";

    private static final String TEST_TITLE3 = "test title3";

    private static final String TEST_DESCRIPTION3 = "test description3";

    private ArrayList<Uri> mRowsAdded;

    private Context mContext;

    private ContentResolver mContentResolver;

    private FileCopyHelper mHelper;

    @Override
    protected void tearDown() throws Exception {
        for (Uri row : mRowsAdded) {
            mContentResolver.delete(row, null, null);
        }

        mHelper.clear();
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getTargetContext();
        mContentResolver = mContext.getContentResolver();

        mHelper = new FileCopyHelper(mContext);
        mRowsAdded = new ArrayList<Uri>();
    }

    @TestTargets({
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "This test will fail if there is no sdcard attached because the method "
                    + "{@link Images#Media#insertImage(ContentResolver, String, String, String)} "
                    + "will store images on the sdcard",
        method = "insertImage",
        args = {ContentResolver.class, String.class, String.class, String.class}
      ),
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "query",
        args = {ContentResolver.class, Uri.class, String[].class}
      ),
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "query",
        args = {ContentResolver.class, Uri.class, String[].class, String.class, String.class}
      ),
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "query",
        args = {ContentResolver.class, Uri.class, String[].class,  String.class, String[].class,
                String.class}
      )
    })
    public void testInsertImageWithImagePath() {
        Cursor c = Media.query(mContentResolver, Media.EXTERNAL_CONTENT_URI, null, null,
                "_id ASC");
        int previousCount = c.getCount();
        c.close();

        // insert an image by path
        String path = mHelper.copy(R.raw.scenery, "mediaStoreTest1.jpg");
        String stringUrl = null;
        try {
            stringUrl = Media.insertImage(mContentResolver, path, TEST_TITLE1, TEST_DESCRIPTION1);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        } catch (UnsupportedOperationException e) {
            // the tests will be aborted because the image will be put in sdcard
            fail("There is no sdcard attached! " + e.getMessage());
        }
        assertInsertionSuccess(stringUrl);
        mRowsAdded.add(Uri.parse(stringUrl));

        // insert another image by path
        path = mHelper.copy(R.raw.scenery, "mediaStoreTest2.jpg");
        stringUrl = null;
        try {
            stringUrl = Media.insertImage(mContentResolver, path, TEST_TITLE2, TEST_DESCRIPTION2);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        } catch (UnsupportedOperationException e) {
            // the tests will be aborted because the image will be put in sdcard
            fail("There is no sdcard attached! " + e.getMessage());
        }
        assertInsertionSuccess(stringUrl);
        mRowsAdded.add(Uri.parse(stringUrl));

        // query the newly added image
        c = Media.query(mContentResolver, Uri.parse(stringUrl),
                new String[] { Media.TITLE, Media.DESCRIPTION, Media.MIME_TYPE });
        assertEquals(1, c.getCount());
        c.moveToFirst();
        assertEquals(TEST_TITLE2, c.getString(c.getColumnIndex(Media.TITLE)));
        assertEquals(TEST_DESCRIPTION2, c.getString(c.getColumnIndex(Media.DESCRIPTION)));
        assertEquals(MIME_TYPE_JPEG, c.getString(c.getColumnIndex(Media.MIME_TYPE)));
        c.close();

        // query all the images in external db and order them by descending id
        // (make the images added in test case in the first positions)
        c = Media.query(mContentResolver, Media.EXTERNAL_CONTENT_URI,
                new String[] { Media.TITLE, Media.DESCRIPTION, Media.MIME_TYPE }, null,
                "_id DESC");
        assertEquals(previousCount + 2, c.getCount());
        c.moveToFirst();
        assertEquals(TEST_TITLE2, c.getString(c.getColumnIndex(Media.TITLE)));
        assertEquals(TEST_DESCRIPTION2, c.getString(c.getColumnIndex(Media.DESCRIPTION)));
        assertEquals(MIME_TYPE_JPEG, c.getString(c.getColumnIndex(Media.MIME_TYPE)));
        c.moveToNext();
        assertEquals(TEST_TITLE1, c.getString(c.getColumnIndex(Media.TITLE)));
        assertEquals(TEST_DESCRIPTION1, c.getString(c.getColumnIndex(Media.DESCRIPTION)));
        assertEquals(MIME_TYPE_JPEG, c.getString(c.getColumnIndex(Media.MIME_TYPE)));
        c.close();

        // query the second image added in the test
        c = Media.query(mContentResolver, Uri.parse(stringUrl),
                new String[] { Media.DESCRIPTION, Media.MIME_TYPE }, Media.TITLE + "=?",
                new String[] { TEST_TITLE2 }, "_id ASC");
        assertEquals(1, c.getCount());
        c.moveToFirst();
        assertEquals(TEST_DESCRIPTION2, c.getString(c.getColumnIndex(Media.DESCRIPTION)));
        assertEquals(MIME_TYPE_JPEG, c.getString(c.getColumnIndex(Media.MIME_TYPE)));
        c.close();
    }

    @TestTargets({
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "This test will fail if there is no sdcard attached because the method "
                + "will store images on the sdcard",
        method = "insertImage",
        args = {ContentResolver.class, Bitmap.class, String.class, String.class}
      ),
      @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getBitmap",
        args = {ContentResolver.class, Uri.class}
      )
    })
    public void testInsertImageWithBitmap() {
        // insert the image by bitmap
        Bitmap src = BitmapFactory.decodeResource(mContext.getResources(), R.raw.scenery);
        String stringUrl = null;
        try{
            stringUrl = Media.insertImage(mContentResolver, src, TEST_TITLE3, TEST_DESCRIPTION3);
        } catch (UnsupportedOperationException e) {
            // the tests will be aborted because the image will be put in sdcard
            fail("There is no sdcard attached! " + e.getMessage());
        }
        assertInsertionSuccess(stringUrl);
        mRowsAdded.add(Uri.parse(stringUrl));

        Cursor c = Media.query(mContentResolver, Uri.parse(stringUrl), new String[] { Media.DATA },
                null, "_id ASC");
        c.moveToFirst();
        // get the bimap by the path
        Bitmap result = null;
        try {
            result = Media.getBitmap(mContentResolver,
                    Uri.fromFile(new File(c.getString(c.getColumnIndex(Media.DATA)))));
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        // can not check the identity between the result and source bitmap because
        // source bitmap is compressed before it is saved as result bitmap
        assertNotNull(result);
        assertEquals(src.getWidth(), result.getWidth());
        assertEquals(src.getHeight(), result.getHeight());
    }

    @TestTargetNew(
      level = TestLevel.COMPLETE,
      method = "getContentUri",
      args = {String.class}
    )
    public void testGetContentUri() {
        assertNotNull(mContentResolver.query(Media.getContentUri("internal"), null, null, null,
                null));
        assertNotNull(mContentResolver.query(Media.getContentUri("external"), null, null, null,
                null));

        // can not accept any other volume names
        String volume = "fakeVolume";
        assertNull(mContentResolver.query(Media.getContentUri(volume), null, null, null, null));
    }

    public void testStoreImagesMediaExternal() {
        final String externalPath = Environment.getExternalStorageDirectory().getPath() +
                "/testimage.jpg";
        final String externalPath2 = Environment.getExternalStorageDirectory().getPath() +
                "/testimage1.jpg";
        ContentValues values = new ContentValues();
        values.put(Media.ORIENTATION, 0);
        values.put(Media.PICASA_ID, 0);
        long dateTaken = System.currentTimeMillis();
        values.put(Media.DATE_TAKEN, dateTaken);
        values.put(Media.DESCRIPTION, "This is a image");
        values.put(Media.LATITUDE, 40.689060d);
        values.put(Media.LONGITUDE, -74.044636d);
        values.put(Media.IS_PRIVATE, 1);
        values.put(Media.MINI_THUMB_MAGIC, 0);
        values.put(Media.DATA, externalPath);
        values.put(Media.DISPLAY_NAME, "testimage");
        values.put(Media.MIME_TYPE, "image/jpeg");
        values.put(Media.SIZE, 86853);
        values.put(Media.TITLE, "testimage");
        long dateAdded = System.currentTimeMillis();
        values.put(Media.DATE_ADDED, dateAdded);
        long dateModified = System.currentTimeMillis();
        values.put(Media.DATE_MODIFIED, dateModified);

        // insert
        Uri uri = mContentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
        assertNotNull(uri);

        try {
            // query
            Cursor c = mContentResolver.query(uri, null, null, null, null);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            long id = c.getLong(c.getColumnIndex(Media._ID));
            assertTrue(id > 0);
            assertEquals(0, c.getInt(c.getColumnIndex(Media.ORIENTATION)));
            assertEquals(0, c.getLong(c.getColumnIndex(Media.PICASA_ID)));
            assertEquals(dateTaken, c.getLong(c.getColumnIndex(Media.DATE_TAKEN)));
            assertEquals("This is a image",
                    c.getString(c.getColumnIndex(Media.DESCRIPTION)));
            assertEquals(40.689060d, c.getDouble(c.getColumnIndex(Media.LATITUDE)), 0d);
            assertEquals(-74.044636d, c.getDouble(c.getColumnIndex(Media.LONGITUDE)), 0d);
            assertEquals(1, c.getInt(c.getColumnIndex(Media.IS_PRIVATE)));
            assertEquals(0, c.getLong(c.getColumnIndex(Media.MINI_THUMB_MAGIC)));
            assertEquals(externalPath, c.getString(c.getColumnIndex(Media.DATA)));
            assertEquals("testimage", c.getString(c.getColumnIndex(Media.DISPLAY_NAME)));
            assertEquals("image/jpeg", c.getString(c.getColumnIndex(Media.MIME_TYPE)));
            assertEquals("testimage", c.getString(c.getColumnIndex(Media.TITLE)));
            assertEquals(86853, c.getInt(c.getColumnIndex(Media.SIZE)));
            long realDateAdded = c.getLong(c.getColumnIndex(Media.DATE_ADDED));
            assertTrue(realDateAdded > 0);
            assertEquals(dateModified, c.getLong(c.getColumnIndex(Media.DATE_MODIFIED)));
            c.close();

            // update
            values.clear();
            values.put(Media.ORIENTATION, 90);
            values.put(Media.PICASA_ID, 10);
            dateTaken = System.currentTimeMillis();
            values.put(Media.DATE_TAKEN, dateTaken);
            values.put(Media.DESCRIPTION, "This is another image");
            values.put(Media.LATITUDE, 41.689060d);
            values.put(Media.LONGITUDE, -75.044636d);
            values.put(Media.IS_PRIVATE, 0);
            values.put(Media.MINI_THUMB_MAGIC, 2);
            values.put(Media.DATA, externalPath2);
            values.put(Media.DISPLAY_NAME, "testimage1");
            values.put(Media.MIME_TYPE, "image/jpeg");
            values.put(Media.SIZE, 86854);
            values.put(Media.TITLE, "testimage1");
            dateModified = System.currentTimeMillis();
            values.put(Media.DATE_MODIFIED, dateModified);
            assertEquals(1, mContentResolver.update(uri, values, null, null));

            c = mContentResolver.query(uri, null, null, null, null);
            assertEquals(1, c.getCount());
            c.moveToFirst();
            assertEquals(id, c.getLong(c.getColumnIndex(Media._ID)));
            assertEquals(90, c.getInt(c.getColumnIndex(Media.ORIENTATION)));
            assertEquals(10, c.getInt(c.getColumnIndex(Media.PICASA_ID)));
            assertEquals(dateTaken, c.getLong(c.getColumnIndex(Media.DATE_TAKEN)));
            assertEquals("This is another image",
                    c.getString(c.getColumnIndex(Media.DESCRIPTION)));
            assertEquals(41.689060d, c.getDouble(c.getColumnIndex(Media.LATITUDE)), 0d);
            assertEquals(-75.044636d, c.getDouble(c.getColumnIndex(Media.LONGITUDE)), 0d);
            assertEquals(0, c.getInt(c.getColumnIndex(Media.IS_PRIVATE)));
            assertEquals(2, c.getLong(c.getColumnIndex(Media.MINI_THUMB_MAGIC)));
            assertEquals(externalPath2,
                    c.getString(c.getColumnIndex(Media.DATA)));
            assertEquals("testimage1", c.getString(c.getColumnIndex(Media.DISPLAY_NAME)));
            assertEquals("image/jpeg", c.getString(c.getColumnIndex(Media.MIME_TYPE)));
            assertEquals("testimage1", c.getString(c.getColumnIndex(Media.TITLE)));
            assertEquals(86854, c.getInt(c.getColumnIndex(Media.SIZE)));
            assertEquals(realDateAdded, c.getLong(c.getColumnIndex(Media.DATE_ADDED)));
            assertEquals(dateModified, c.getLong(c.getColumnIndex(Media.DATE_MODIFIED)));
            c.close();
        } finally {
            // delete
            assertEquals(1, mContentResolver.delete(uri, null, null));
        }
    }

    public void testStoreImagesMediaInternal() {
        // can not insert any data, so other operations can not be tested
        try {
            mContentResolver.insert(Media.INTERNAL_CONTENT_URI, new ContentValues());
            fail("Should throw UnsupportedOperationException when inserting into internal "
                    + "database");
        } catch (UnsupportedOperationException e) {
        }
    }

    private void assertInsertionSuccess(String stringUrl) {
        assertNotNull(stringUrl);
        // check whether the thumbnails are generated
        Cursor c = mContentResolver.query(Uri.parse(stringUrl), new String[]{ Media._ID }, null,
                null, null);
        assertTrue(c.moveToFirst());
        long imageId = c.getLong(c.getColumnIndex(Media._ID));
        c.close();
        assertNotNull(Thumbnails.getThumbnail(mContentResolver, imageId,
                Thumbnails.MINI_KIND, null));
        assertNotNull(Thumbnails.getThumbnail(mContentResolver, imageId,
                Thumbnails.MICRO_KIND, null));
        c = mContentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, null,
                Thumbnails.IMAGE_ID + "=" + imageId, null, null);
        assertEquals(2, c.getCount());
        c.close();
    }
}
