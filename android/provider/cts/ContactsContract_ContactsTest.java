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

package android.provider.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.cts.ContactsContract_TestDataBuilder.TestContact;
import android.provider.cts.ContactsContract_TestDataBuilder.TestRawContact;
import android.test.InstrumentationTestCase;

import java.util.List;

@TestTargetClass(ContactsContract.Contacts.class)
public class ContactsContract_ContactsTest extends InstrumentationTestCase {
    private ContentResolver mContentResolver;
    private ContactsContract_TestDataBuilder mBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContentResolver = getInstrumentation().getTargetContext().getContentResolver();
        IContentProvider provider = mContentResolver.acquireProvider(ContactsContract.AUTHORITY);
        mBuilder = new ContactsContract_TestDataBuilder(provider);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mBuilder.cleanup();
    }

    @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test markAsContacted(ContentResolver resolver, long contactId)",
            method = "markAsContacted",
            args = {android.content.ContentResolver.class, long.class}
    )
    public void testMarkAsContacted() throws Exception {
        TestRawContact rawContact = mBuilder.newRawContact().insert().load();
        TestContact contact = rawContact.getContact().load();
        long oldLastContacted = contact.getLong(Contacts.LAST_TIME_CONTACTED);

        Contacts.markAsContacted(mContentResolver, contact.getId());
        contact.load(); // Reload

        long lastContacted = contact.getLong(Contacts.LAST_TIME_CONTACTED);
        assertTrue(oldLastContacted < lastContacted);
        oldLastContacted = lastContacted;

        Contacts.markAsContacted(mContentResolver, contact.getId());
        contact.load();

        lastContacted = contact.getLong(Contacts.LAST_TIME_CONTACTED);
        assertTrue(oldLastContacted < lastContacted);
    }

    public void testContentUri() {
        Instrumentation instrumentation = getInstrumentation();
        Context context = instrumentation.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        assertFalse("Device does not support the activity intent: " + intent,
                resolveInfos.isEmpty());
    }
}

