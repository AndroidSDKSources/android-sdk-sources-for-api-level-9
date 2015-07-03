/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.content.cts;

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.view.animation.cts.DelayedCheck;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test {@link ContextWrapper}.
 */
@TestTargetClass(ContextWrapper.class)
public class ContextWrapperTest extends AndroidTestCase {
    private static final String PERMISSION_HARDWARE_TEST = "android.permission.HARDWARE_TEST";

    private static final String ACTUAL_RESULT = "ResultSetByReceiver";

    private static final String INTIAL_RESULT = "IntialResult";

    private static final String VALUE_ADDED = "ValueAdded";
    private static final String KEY_ADDED = "AddedByReceiver";

    private static final String VALUE_REMOVED = "ValueWillBeRemove";
    private static final String KEY_REMOVED = "ToBeRemoved";

    private static final String VALUE_KEPT = "ValueKept";
    private static final String KEY_KEPT = "ToBeKept";

    private static final String MOCK_STICKY_ACTION = "android.content.cts.ContextWrapperTest."
        + "STICKY_BROADCAST_RESULT";

    private static final String ACTION_BROADCAST_TESTORDER =
        "android.content.cts.ContextWrapperTest.BROADCAST_TESTORDER";
    private final static String MOCK_ACTION1 = ACTION_BROADCAST_TESTORDER + "1";
    private final static String MOCK_ACTION2 = ACTION_BROADCAST_TESTORDER + "2";

    public static final String PERMISSION_GRANTED = "android.app.cts.permission.TEST_GRANTED";
    public static final String PERMISSION_DENIED = "android.app.cts.permission.TEST_DENIED";

    private static final int BROADCAST_TIMEOUT = 10000;

    private Context mContext;

    private ContextWrapper mContextWrapper;
    private Object mLockObj;

    private ArrayList<BroadcastReceiver> mRegisteredReceiverList;

    private boolean mWallpaperChanged;
    private BitmapDrawable mOriginalWallpaper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mLockObj = new Object();
        mContext = getContext();
        mContextWrapper = new ContextWrapper(mContext);

        mRegisteredReceiverList = new ArrayList<BroadcastReceiver>();

        mOriginalWallpaper = (BitmapDrawable) mContextWrapper.getWallpaper();
    }

    @Override
    protected void tearDown() throws Exception {
        if (mWallpaperChanged) {
            mContextWrapper.setWallpaper(mOriginalWallpaper.getBitmap());
        }

        for (BroadcastReceiver receiver : mRegisteredReceiverList) {
            mContextWrapper.unregisterReceiver(receiver);
        }

        super.tearDown();
    }

    private void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        mContextWrapper.registerReceiver(receiver, filter);

        mRegisteredReceiverList.add(receiver);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "ContextWrapper",
        args = {android.content.Context.class}
    )
    public void testConstructor() {
        new ContextWrapper(mContext);

        // null param is allowed
        new ContextWrapper(null);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceCallingPermission",
        args = {String.class, String.class}
    )
    public void testEnforceCallingPermission() {
        try {
            mContextWrapper.enforceCallingPermission(
                    PERMISSION_HARDWARE_TEST,
                    "enforceCallingPermission is not working without possessing an IPC.");
            fail("enforceCallingPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // Currently no IPC is handled by this process, this exception is expected
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "sendOrderedBroadcast",
        args = {android.content.Intent.class, java.lang.String.class}
    )
    public void testSendOrderedBroadcast1() throws InterruptedException {
        final HighPriorityBroadcastReceiver highPriorityReceiver =
                new HighPriorityBroadcastReceiver();
        final LowPriorityBroadcastReceiver lowPriorityReceiver =
            new LowPriorityBroadcastReceiver();

        final IntentFilter filter = new IntentFilter(ResultReceiver.MOCK_ACTION);
        registerBroadcastReceiver(highPriorityReceiver, filter);
        registerBroadcastReceiver(lowPriorityReceiver, filter);

        final Intent broadcastIntent = new Intent(ResultReceiver.MOCK_ACTION);
        mContextWrapper.sendOrderedBroadcast(broadcastIntent, null);
        new DelayedCheck(BROADCAST_TIMEOUT) {
            @Override
            protected boolean check() {
                return highPriorityReceiver.hasReceivedBroadCast()
                        && !lowPriorityReceiver.hasReceivedBroadCast();
            }
        }.run();

        synchronized (highPriorityReceiver) {
            highPriorityReceiver.notify();
        }

        new DelayedCheck(BROADCAST_TIMEOUT) {
            @Override
            protected boolean check() {
                return highPriorityReceiver.hasReceivedBroadCast()
                        && lowPriorityReceiver.hasReceivedBroadCast();
            }
        }.run();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "sendOrderedBroadcast",
        args = {android.content.Intent.class, java.lang.String.class,
                android.content.BroadcastReceiver.class, android.os.Handler.class, int.class,
                java.lang.String.class, android.os.Bundle.class}
    )
    public void testSendOrderedBroadcast2() throws InterruptedException {
        final TestBroadcastReceiver broadcastReceiver = new TestBroadcastReceiver();
        broadcastReceiver.mIsOrderedBroadcasts = true;

        Bundle bundle = new Bundle();
        bundle.putString(KEY_KEPT, VALUE_KEPT);
        bundle.putString(KEY_REMOVED, VALUE_REMOVED);
        mContextWrapper.sendOrderedBroadcast(new Intent(ResultReceiver.MOCK_ACTION),
                null, broadcastReceiver, null, 1, INTIAL_RESULT, bundle);

        synchronized (mLockObj) {
            try {
                mLockObj.wait(BROADCAST_TIMEOUT);
            } catch (InterruptedException e) {
                fail("unexpected InterruptedException.");
            }
        }

        assertTrue("Receiver didn't make any response.", broadcastReceiver.hadReceivedBroadCast());
        assertEquals("Incorrect code: " + broadcastReceiver.getResultCode(), 3,
                broadcastReceiver.getResultCode());
        assertEquals(ACTUAL_RESULT, broadcastReceiver.getResultData());
        Bundle resultExtras = broadcastReceiver.getResultExtras(false);
        assertEquals(VALUE_ADDED, resultExtras.getString(KEY_ADDED));
        assertEquals(VALUE_KEPT, resultExtras.getString(KEY_KEPT));
        assertNull(resultExtras.getString(KEY_REMOVED));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "registerReceiver",
            args = {BroadcastReceiver.class, IntentFilter.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "unregisterReceiver",
            args = {BroadcastReceiver.class}
        )
    })
    public void testRegisterReceiver1() throws InterruptedException {
        final FilteredReceiver broadcastReceiver = new FilteredReceiver();
        final IntentFilter filter = new IntentFilter(MOCK_ACTION1);

        // Test registerReceiver
        mContextWrapper.registerReceiver(broadcastReceiver, filter);

        // Test unwanted intent(action = MOCK_ACTION2)
        broadcastReceiver.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver, MOCK_ACTION2);
        assertFalse(broadcastReceiver.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver.hadReceivedBroadCast2());

        // Send wanted intent(action = MOCK_ACTION1)
        broadcastReceiver.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver, MOCK_ACTION1);
        assertTrue(broadcastReceiver.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver.hadReceivedBroadCast2());

        mContextWrapper.unregisterReceiver(broadcastReceiver);

        // Test unregisterReceiver
        FilteredReceiver broadcastReceiver2 = new FilteredReceiver();
        mContextWrapper.registerReceiver(broadcastReceiver2, filter);
        mContextWrapper.unregisterReceiver(broadcastReceiver2);

        // Test unwanted intent(action = MOCK_ACTION2)
        broadcastReceiver2.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver2, MOCK_ACTION2);
        assertFalse(broadcastReceiver2.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver2.hadReceivedBroadCast2());

        // Send wanted intent(action = MOCK_ACTION1), but the receiver is unregistered.
        broadcastReceiver2.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver2, MOCK_ACTION1);
        assertFalse(broadcastReceiver2.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver2.hadReceivedBroadCast2());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "registerReceiver",
        args = {android.content.BroadcastReceiver.class, android.content.IntentFilter.class,
                java.lang.String.class, android.os.Handler.class}
    )
    public void testRegisterReceiver2() throws InterruptedException {
        FilteredReceiver broadcastReceiver = new FilteredReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MOCK_ACTION1);

        // Test registerReceiver
        mContextWrapper.registerReceiver(broadcastReceiver, filter, null, null);

        // Test unwanted intent(action = MOCK_ACTION2)
        broadcastReceiver.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver, MOCK_ACTION2);
        assertFalse(broadcastReceiver.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver.hadReceivedBroadCast2());

        // Send wanted intent(action = MOCK_ACTION1)
        broadcastReceiver.reset();
        waitForFilteredIntent(mContextWrapper, broadcastReceiver, MOCK_ACTION1);
        assertTrue(broadcastReceiver.hadReceivedBroadCast1());
        assertFalse(broadcastReceiver.hadReceivedBroadCast2());

        mContextWrapper.unregisterReceiver(broadcastReceiver);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceCallingOrSelfPermission",
        args = {String.class, String.class}
    )
    public void testEnforceCallingOrSelfPermission() {
        try {
            mContextWrapper.enforceCallingOrSelfPermission(PERMISSION_HARDWARE_TEST,
                    "enforceCallingOrSelfPermission is not working without possessing an IPC.");
            fail("enforceCallingOrSelfPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is OK, it should throw a SecurityException here because currently no
            // IPC is handled by this process.
        }
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setWallpaper",
            args = {Bitmap.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "setWallpaper",
            args = {InputStream.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "clearWallpaper",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getWallpaper",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "peekWallpaper",
            args = {}
        )
    })
    public void testAccessWallpaper() throws IOException, InterruptedException {
        // set Wallpaper by contextWrapper#setWallpaper(Bitmap)
        Bitmap bitmap = Bitmap.createBitmap(20, 30, Bitmap.Config.RGB_565);
        // Test getWallpaper
        Drawable testDrawable = mContextWrapper.getWallpaper();
        // Test peekWallpaper
        Drawable testDrawable2 = mContextWrapper.peekWallpaper();

        mContextWrapper.setWallpaper(bitmap);
        mWallpaperChanged = true;
        synchronized(this) {
            wait(500);
        }

        assertNotSame(testDrawable, mContextWrapper.peekWallpaper());
        assertNotNull(mContextWrapper.getWallpaper());
        assertNotSame(testDrawable2, mContextWrapper.peekWallpaper());
        assertNotNull(mContextWrapper.peekWallpaper());

        // set Wallpaper by contextWrapper#setWallpaper(InputStream)
        mContextWrapper.clearWallpaper();

        testDrawable = mContextWrapper.getWallpaper();
        InputStream stream = mContextWrapper.getResources().openRawResource(R.drawable.scenery);

        mContextWrapper.setWallpaper(stream);
        synchronized (this) {
            wait(1000);
        }

        assertNotSame(testDrawable, mContextWrapper.peekWallpaper());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "openOrCreateDatabase",
            args = {java.lang.String.class, int.class,
                    android.database.sqlite.SQLiteDatabase.CursorFactory.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getDatabasePath",
            args = {String.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "openOrCreateDatabase",
            args = {String.class, int.class,
                    android.database.sqlite.SQLiteDatabase.CursorFactory.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "databaseList",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "deleteDatabase",
            args = {String.class}
        )
    })
    public void testAccessDatabase() {
        String DATABASE_NAME = "databasetest";
        String DATABASE_NAME1 = DATABASE_NAME + "1";
        String DATABASE_NAME2 = DATABASE_NAME + "2";
        SQLiteDatabase mDatabase;
        File mDatabaseFile;

        SQLiteDatabase.CursorFactory factory = new SQLiteDatabase.CursorFactory() {
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
                    String editTable, SQLiteQuery query) {
                return new android.database.sqlite.SQLiteCursor(db, masterQuery, editTable, query) {
                    @Override
                    public boolean requery() {
                        setSelectionArguments(new String[] { "2" });
                        return super.requery();
                    }
                };
            }
        };

        // FIXME: Move cleanup into tearDown()
        for (String db : mContextWrapper.databaseList()) {
            File f = mContextWrapper.getDatabasePath(db);
            if (f.exists()) {
                mContextWrapper.deleteDatabase(db);
            }
        }

        // Test openOrCreateDatabase with null and actual factory
        mDatabase = mContextWrapper.openOrCreateDatabase(DATABASE_NAME1,
                ContextWrapper.MODE_WORLD_READABLE | ContextWrapper.MODE_WORLD_WRITEABLE, factory);
        assertNotNull(mDatabase);
        mDatabase.close();
        mDatabase = mContextWrapper.openOrCreateDatabase(DATABASE_NAME2,
                ContextWrapper.MODE_WORLD_READABLE | ContextWrapper.MODE_WORLD_WRITEABLE, factory);
        assertNotNull(mDatabase);
        mDatabase.close();

        // Test getDatabasePath
        File actualDBPath = mContextWrapper.getDatabasePath(DATABASE_NAME1);

        // Test databaseList()
        assertEquals(2, mContextWrapper.databaseList().length);
        ArrayList<String> list = new ArrayList<String>();
        // Don't know the items storing order
        list.add(mContextWrapper.databaseList()[0]);
        list.add(mContextWrapper.databaseList()[1]);
        assertTrue(list.contains(DATABASE_NAME1) && list.contains(DATABASE_NAME2));

        // Test deleteDatabase()
        for (int i = 1; i < 3; i++) {
            mDatabaseFile = mContextWrapper.getDatabasePath(DATABASE_NAME + i);
            assertTrue(mDatabaseFile.exists());
            mContextWrapper.deleteDatabase(DATABASE_NAME + i);
            mDatabaseFile = new File(actualDBPath, DATABASE_NAME + i);
            assertFalse(mDatabaseFile.exists());
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceUriPermission",
        args = {Uri.class, int.class, int.class, int.class, String.class}
    )
    public void testEnforceUriPermission1() {
        try {
            Uri uri = Uri.parse("content://ctstest");
            mContextWrapper.enforceUriPermission(uri, Binder.getCallingPid(),
                    Binder.getCallingUid(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    "enforceUriPermission is not working without possessing an IPC.");
            fail("enforceUriPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is OK, it should throw a SecurityException here because currently no
            // IPC is handled by this process.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceUriPermission",
        args = {android.net.Uri.class, java.lang.String.class, java.lang.String.class, int.class,
                int.class, int.class, java.lang.String.class}
    )
    public void testEnforceUriPermission2() {
        Uri uri = Uri.parse("content://ctstest");
        try {
            mContextWrapper.enforceUriPermission(uri, PERMISSION_HARDWARE_TEST,
                    PERMISSION_HARDWARE_TEST, Binder.getCallingPid(), Binder.getCallingUid(),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    "enforceUriPermission is not working without possessing an IPC.");
            fail("enforceUriPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is ok, it should throw a SecurityException here because currently no
            // IPC is handled by this process.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPackageResourcePath",
        args = {}
    )
    public void testGetPackageResourcePath() {
        assertNotNull(mContextWrapper.getPackageResourcePath());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "startActivity",
        args = {Intent.class}
    )
    public void testStartActivity() {
        Intent intent = new Intent(mContext, ContextWrapperStubActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContextWrapper.startActivity(intent);
            fail("Test startActivity should thow a ActivityNotFoundException here.");
        } catch (ActivityNotFoundException e) {
            // Because ContextWrapper is a wrapper class, so no need to test
            // the details of the function's performance. Getting a result
            // from the wrapped class is enough for testing.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "createPackageContext",
        args = {String.class, int.class}
    )
    public void testCreatePackageContext() throws PackageManager.NameNotFoundException {
        Context actualContext = mContextWrapper.createPackageContext(getValidPackageName(),
                Context.CONTEXT_IGNORE_SECURITY);

        assertNotNull(actualContext);
    }

    /**
     * Helper method to retrieve a valid application package name to use for tests.
     */
    private String getValidPackageName() {
        List<PackageInfo> packages = mContextWrapper.getPackageManager().getInstalledPackages(
                PackageManager.GET_ACTIVITIES);
        assertTrue(packages.size() >= 1);
        return packages.get(0).packageName;
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getMainLooper",
        args = {}
    )
    public void testGetMainLooper() {
        assertNotNull(mContextWrapper.getMainLooper());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getApplicationContext",
        args = {}
    )
    public void testGetApplicationContext() {
        assertSame(mContext.getApplicationContext(), mContextWrapper.getApplicationContext());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getSharedPreferences",
        args = {String.class, int.class}
    )
    public void testGetSharedPreferences() {
        SharedPreferences sp;
        SharedPreferences localSP;

        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String packageName = mContextWrapper.getPackageName();
        localSP = mContextWrapper.getSharedPreferences(packageName + "_preferences",
                Context.MODE_PRIVATE);
        assertSame(sp, localSP);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "revokeUriPermission",
        args = {Uri.class, int.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "Can't test the effect of this function, should be"
        + "tested by functional test.")
    public void testRevokeUriPermission() {
        Uri uri = Uri.parse("contents://ctstest");
        mContextWrapper.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "startService",
            args = {Intent.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "bindService",
            args = {android.content.Intent.class, android.content.ServiceConnection.class,
                    int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "stopService",
            args = {Intent.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "unbindService",
            args = {ServiceConnection.class}
        )
    })
    public void testAccessService() throws InterruptedException {
        MockContextWrapperService.reset();
        bindExpectResult(mContextWrapper, new Intent(mContext, MockContextWrapperService.class));

        // Check startService
        assertTrue(MockContextWrapperService.hadCalledOnStart());
        // Check bindService
        assertTrue(MockContextWrapperService.hadCalledOnBind());

        assertTrue(MockContextWrapperService.hadCalledOnDestory());
        // Check unbinService
        assertTrue(MockContextWrapperService.hadCalledOnUnbind());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPackageCodePath",
        args = {}
    )
    public void testGetPackageCodePath() {
        assertNotNull(mContextWrapper.getPackageCodePath());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPackageName",
        args = {}
    )
    public void testGetPackageName() {
        assertEquals("com.android.cts.stub", mContextWrapper.getPackageName());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getCacheDir",
        args = {}
    )
    public void testGetCacheDir() {
        assertNotNull(mContextWrapper.getCacheDir());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getContentResolver",
        args = {}
    )
    public void testGetContentResolver() {
        assertSame(mContext.getContentResolver(), mContextWrapper.getContentResolver());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "attachBaseContext",
            args = {Context.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getBaseContext",
            args = {}
        )
    })
    public void testAccessBaseContext() throws PackageManager.NameNotFoundException {
        MockContextWrapper testContextWrapper = new MockContextWrapper(mContext);

        // Test getBaseContext()
        assertSame(mContext, testContextWrapper.getBaseContext());

        Context secondContext = testContextWrapper.createPackageContext(getValidPackageName(),
                Context.CONTEXT_IGNORE_SECURITY);
        assertNotNull(secondContext);

        // Test attachBaseContext
        try {
            testContextWrapper.attachBaseContext(secondContext);
            fail("If base context has already been set, it should throw a IllegalStateException.");
        } catch (IllegalStateException e) {
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getFileStreamPath",
        args = {String.class}
    )
    public void testGetFileStreamPath() {
        String TEST_FILENAME = "TestGetFileStreamPath";

        // Test the path including the input filename
        String fileStreamPath = mContextWrapper.getFileStreamPath(TEST_FILENAME).toString();
        assertTrue(fileStreamPath.indexOf(TEST_FILENAME) >= 0);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getClassLoader",
        args = {}
    )
    public void testGetClassLoader() {
        assertSame(mContext.getClassLoader(), mContextWrapper.getClassLoader());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getWallpaperDesiredMinimumHeight",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "getWallpaperDesiredMinimumWidth",
            args = {}
        )
    })
    public void testGetWallpaperDesiredMinimumHeightAndWidth() {
        int height = mContextWrapper.getWallpaperDesiredMinimumHeight();
        int width = mContextWrapper.getWallpaperDesiredMinimumWidth();

        // returned value is <= 0, the caller should use the height of the
        // default display instead.
        // That is to say, the return values of desired minimumHeight and
        // minimunWidth are at the same side of 0-dividing line.
        assertTrue((height > 0 && width > 0) || (height <= 0 && width <= 0));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "sendStickyBroadcast",
            args = {Intent.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "removeStickyBroadcast",
            args = {Intent.class}
        )
    })
    public void testAccessStickyBroadcast() throws InterruptedException {
        ResultReceiver resultReceiver = new ResultReceiver();

        Intent intent = new Intent(MOCK_STICKY_ACTION);
        TestBroadcastReceiver stickyReceiver = new TestBroadcastReceiver();

        mContextWrapper.sendStickyBroadcast(intent);

        waitForReceiveBroadCast(resultReceiver);

        assertEquals(intent.getAction(), mContextWrapper.registerReceiver(stickyReceiver,
                new IntentFilter(MOCK_STICKY_ACTION)).getAction());

        synchronized (mLockObj) {
            mLockObj.wait(BROADCAST_TIMEOUT);
        }

        assertTrue("Receiver didn't make any response.", stickyReceiver.hadReceivedBroadCast());

        mContextWrapper.unregisterReceiver(stickyReceiver);
        mContextWrapper.removeStickyBroadcast(intent);

        assertNull(mContextWrapper.registerReceiver(stickyReceiver,
                new IntentFilter(MOCK_STICKY_ACTION)));
        mContextWrapper.unregisterReceiver(stickyReceiver);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkCallingOrSelfUriPermission",
        args = {Uri.class, int.class}
    )
    public void testCheckCallingOrSelfUriPermission() {
        Uri uri = Uri.parse("content://ctstest");

        int retValue = mContextWrapper.checkCallingOrSelfUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_DENIED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "grantUriPermission",
        args = {String.class, Uri.class, int.class}
    )
    @ToBeFixed(bug = "1400249", explanation = "Can't test the effect of this function,"
            + " should be tested by functional test.")
    public void testGrantUriPermission() {
        mContextWrapper.grantUriPermission("com.android.mms", Uri.parse("contents://ctstest"),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforcePermission",
        args = {String.class, int.class, int.class, String.class}
    )
    public void testEnforcePermission() {
        try {
            mContextWrapper.enforcePermission(
                    PERMISSION_HARDWARE_TEST, Binder.getCallingPid(),
                    Binder.getCallingUid(),
                    "enforcePermission is not working without possessing an IPC.");
            fail("enforcePermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is ok, it should throw a SecurityException here
            // because currently no IPC is handled by this process.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkUriPermission",
        args = {Uri.class, int.class, int.class, int.class}
    )
    public void testCheckUriPermission1() {
        Uri uri = Uri.parse("content://ctstest");

        int retValue = mContextWrapper.checkUriPermission(uri, Binder.getCallingPid(), 0,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_GRANTED, retValue);

        retValue = mContextWrapper.checkUriPermission(uri, Binder.getCallingPid(),
                Binder.getCallingUid(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_DENIED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkUriPermission",
        args = {Uri.class, String.class, String.class, int.class, int.class, int.class}
    )
    public void testCheckUriPermission2() {
        Uri uri = Uri.parse("content://ctstest");

        int retValue = mContextWrapper.checkUriPermission(uri, PERMISSION_HARDWARE_TEST,
                PERMISSION_HARDWARE_TEST, Binder.getCallingPid(), 0,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_GRANTED, retValue);

        retValue = mContextWrapper.checkUriPermission(uri, PERMISSION_HARDWARE_TEST,
                PERMISSION_HARDWARE_TEST, Binder.getCallingPid(), Binder.getCallingUid(),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_DENIED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkCallingPermission",
        args = {java.lang.String.class}
    )
    public void testCheckCallingPermission() {
        int retValue = mContextWrapper.checkCallingPermission(PERMISSION_HARDWARE_TEST);
        assertEquals(PackageManager.PERMISSION_DENIED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkCallingUriPermission",
        args = {Uri.class, int.class}
    )
    public void testCheckCallingUriPermission() {
        Uri uri = Uri.parse("content://ctstest");

        int retValue = mContextWrapper.checkCallingUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        assertEquals(PackageManager.PERMISSION_DENIED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceCallingUriPermission",
        args = {Uri.class, int.class, String.class}
    )
    public void testEnforceCallingUriPermission() {
        try {
            Uri uri = Uri.parse("content://ctstest");
            mContextWrapper.enforceCallingUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    "enforceCallingUriPermission is not working without possessing an IPC.");
            fail("enforceCallingUriPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is OK, it should throw a SecurityException here because currently no
            // IPC is handled by this process.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getDir",
        args = {String.class, int.class}
    )
    public void testGetDir() {
        File dir = mContextWrapper.getDir("testpath", Context.MODE_WORLD_WRITEABLE);
        assertNotNull(dir);
        dir.delete();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getPackageManager",
        args = {}
    )
    public void testGetPackageManager() {
        assertSame(mContext.getPackageManager(), mContextWrapper.getPackageManager());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkCallingOrSelfPermission",
        args = {String.class}
    )
    public void testCheckCallingOrSelfPermission() {
        int retValue = mContextWrapper.checkCallingOrSelfPermission("android.permission.GET_TASKS");
        assertEquals(PackageManager.PERMISSION_GRANTED, retValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "sendBroadcast",
        args = {Intent.class}
    )
    public void testSendBroadcast1() throws InterruptedException {
        final ResultReceiver receiver = new ResultReceiver();

        registerBroadcastReceiver(receiver, new IntentFilter(ResultReceiver.MOCK_ACTION));

        mContextWrapper.sendBroadcast(new Intent(ResultReceiver.MOCK_ACTION));

        new DelayedCheck(BROADCAST_TIMEOUT){
            @Override
            protected boolean check() {
                return receiver.hasReceivedBroadCast();
            }
        }.run();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "sendBroadcast",
        args = {Intent.class, String.class}
    )
    public void testSendBroadcast2() throws InterruptedException {
        final ResultReceiver receiver = new ResultReceiver();

        registerBroadcastReceiver(receiver, new IntentFilter(ResultReceiver.MOCK_ACTION));

        mContextWrapper.sendBroadcast(new Intent(ResultReceiver.MOCK_ACTION), null);

        new DelayedCheck(BROADCAST_TIMEOUT){
            @Override
            protected boolean check() {
                return receiver.hasReceivedBroadCast();
            }
        }.run();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "enforceCallingOrSelfUriPermission",
        args = {Uri.class, int.class, String.class}
    )
    public void testEnforceCallingOrSelfUriPermission() {
        try {
            Uri uri = Uri.parse("content://ctstest");
            mContextWrapper.enforceCallingOrSelfUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                    "enforceCallingOrSelfUriPermission is not working without possessing an IPC.");
            fail("enforceCallingOrSelfUriPermission is not working without possessing an IPC.");
        } catch (SecurityException e) {
            // If the function is OK, it should throw a SecurityException here because currently no
            // IPC is handled by this process.
        }
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "checkPermission",
        args = {String.class, int.class, int.class}
    )
    public void testCheckPermission() {
        // Test with root user, everything will be granted.
        int returnValue = mContextWrapper.checkPermission(PERMISSION_HARDWARE_TEST, 1, 0);
        assertEquals(PackageManager.PERMISSION_GRANTED, returnValue);

        // Test with non-root user, only included granted permission.
        returnValue = mContextWrapper.checkPermission(PERMISSION_HARDWARE_TEST, 1, 1);
        assertEquals(PackageManager.PERMISSION_DENIED, returnValue);

        // Test with null permission.
        try {
            returnValue = mContextWrapper.checkPermission(null, 0, 0);
            fail("checkPermission should not accept null permission");
        } catch (IllegalArgumentException e) {
        }

        // Test with invalid uid and included granted permission.
        returnValue = mContextWrapper.checkPermission("android.permission.GET_TASKS", 1, -11);
        assertEquals(PackageManager.PERMISSION_DENIED, returnValue);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getSystemService",
        args = {String.class}
    )
    public void testGetSystemService() {
        // Test invalid service name
        assertNull(mContextWrapper.getSystemService("invalid"));

        // Test valid service name
        assertNotNull(mContextWrapper.getSystemService(Context.WINDOW_SERVICE));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getAssets",
        args = {}
    )
    public void testGetAssets() {
        assertSame(mContext.getAssets(), mContextWrapper.getAssets());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getResources",
        args = {}
    )
    public void testGetResources() {
        assertSame(mContext.getResources(), mContextWrapper.getResources());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "startInstrumentation",
        args = {android.content.ComponentName.class, java.lang.String.class,
                android.os.Bundle.class}
    )
    public void testStartInstrumentation() {
        // Use wrong name
        ComponentName cn = new ComponentName("com.android",
                "com.android.content.FalseLocalSampleInstrumentation");
        assertNotNull(cn);
        assertNotNull(mContextWrapper);
        // If the target instrumentation is wrong, the function should return false.
        assertFalse(mContextWrapper.startInstrumentation(cn, null, null));
    }

    private void bindExpectResult(Context contextWrapper, Intent service)
            throws InterruptedException {
        if (service == null) {
            fail("No service created!");
        }
        TestConnection conn = new TestConnection(true, false);

        contextWrapper.bindService(service, conn, Context.BIND_AUTO_CREATE);
        contextWrapper.startService(service);

        // Wait for a short time, so the service related operations could be
        // working.
        synchronized (this) {
            wait(2500);
        }
        // Test stop Service
        assertTrue(contextWrapper.stopService(service));
        contextWrapper.unbindService(conn);

        synchronized (this) {
            wait(1000);
        }
    }

    private interface Condition {
        public boolean onCondition();
    }

    private synchronized void waitForCondition(Condition con) throws InterruptedException {
        // check the condition every 1 second until the condition is fulfilled
        // and wait for 3 seconds at most
        for (int i = 0; !con.onCondition() && i <= 3; i++) {
            wait(1000);
        }
    }

    private void waitForReceiveBroadCast(final ResultReceiver receiver)
            throws InterruptedException {
        Condition con = new Condition() {
            public boolean onCondition() {
                return receiver.hasReceivedBroadCast();
            }
        };
        waitForCondition(con);
    }

    private void waitForFilteredIntent(ContextWrapper contextWrapper,
            final FilteredReceiver receiver, final String action) throws InterruptedException {
        contextWrapper.sendOrderedBroadcast(new Intent(action), null);

        synchronized (mLockObj) {
            mLockObj.wait(BROADCAST_TIMEOUT);
        }
    }

    private static final class MockContextWrapper extends ContextWrapper {
        public MockContextWrapper(Context base) {
            super(base);
        }

        @Override
        public void attachBaseContext(Context base) {
            super.attachBaseContext(base);
        }
    }

    private final class TestBroadcastReceiver extends BroadcastReceiver {
        boolean mHadReceivedBroadCast;
        boolean mIsOrderedBroadcasts;

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                if (mIsOrderedBroadcasts) {
                    setResultCode(3);
                    setResultData(ACTUAL_RESULT);
                }

                Bundle map = getResultExtras(false);
                if (map != null) {
                    map.remove(KEY_REMOVED);
                    map.putString(KEY_ADDED, VALUE_ADDED);
                }
                mHadReceivedBroadCast = true;
                this.notifyAll();
            }

            synchronized (mLockObj) {
                mLockObj.notify();
            }
        }

        boolean hadReceivedBroadCast() {
            return mHadReceivedBroadCast;
        }

        void reset(){
            mHadReceivedBroadCast = false;
        }
    }

    private class FilteredReceiver extends BroadcastReceiver {
        private boolean mHadReceivedBroadCast1 = false;

        private boolean mHadReceivedBroadCast2 = false;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MOCK_ACTION1.equals(action)) {
                mHadReceivedBroadCast1 = true;
            } else if (MOCK_ACTION2.equals(action)) {
                mHadReceivedBroadCast2 = true;
            }

            synchronized (mLockObj) {
                mLockObj.notify();
            }
        }

        public boolean hadReceivedBroadCast1() {
            return mHadReceivedBroadCast1;
        }

        public boolean hadReceivedBroadCast2() {
            return mHadReceivedBroadCast2;
        }

        public void reset(){
            mHadReceivedBroadCast1 = false;
            mHadReceivedBroadCast2 = false;
        }
    }

    private class TestConnection implements ServiceConnection {
        public TestConnection(boolean expectDisconnect, boolean setReporter) {
        }

        void setMonitor(boolean v) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
