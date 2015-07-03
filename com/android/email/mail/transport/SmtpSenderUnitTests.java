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

package com.android.email.mail.transport;

import com.android.email.mail.Address;
import com.android.email.mail.MessagingException;
import com.android.email.mail.Transport;
import com.android.email.provider.EmailProvider;
import com.android.email.provider.EmailContent.Attachment;
import com.android.email.provider.EmailContent.Body;
import com.android.email.provider.EmailContent.Message;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * This is a series of unit tests for the SMTP Sender class.  These tests must be locally
 * complete - no server(s) required.
 *
 * These tests can be run with the following command:
 *   runtest -c com.android.email.mail.transport.SmtpSenderUnitTests email
 */
@SmallTest
public class SmtpSenderUnitTests extends ProviderTestCase2<EmailProvider> {

    EmailProvider mProvider;
    Context mProviderContext;
    Context mContext;
    private static final String LOCAL_ADDRESS = "1.2.3.4";

    /* These values are provided by setUp() */
    private SmtpSender mSender = null;

    /* Simple test string and its base64 equivalent */
    private final static String TEST_STRING = "Hello, world";
    private final static String TEST_STRING_BASE64 = "SGVsbG8sIHdvcmxk";

    public SmtpSenderUnitTests() {
        super(EmailProvider.class, EmailProvider.EMAIL_AUTHORITY);
    }

    /**
     * Setup code.  We generate a lightweight SmtpSender for testing.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mProviderContext = getMockContext();
        mContext = getContext();
        
        // These are needed so we can get at the inner classes
        mSender = (SmtpSender) SmtpSender.newInstance(mProviderContext,
                "smtp://user:password@server:999");
    }

    /**
     * Confirms simple non-SSL non-TLS login
     */
    public void testSimpleLogin() throws Exception {
        
        MockTransport mockTransport = openAndInjectMockTransport();
        
        // try to open it
        setupOpen(mockTransport, null);
        mSender.open();
    }
    
    /**
     * TODO: Test with SSL negotiation (faked)
     * TODO: Test with SSL required but not supported
     * TODO: Test with TLS negotiation (faked)
     * TODO: Test with TLS required but not supported
     * TODO: Test other capabilities.
     * TODO: Test AUTH LOGIN
     */
    
    /**
     * Test:  Open and send a single message (sunny day)
     */
    public void testSendMessageWithBody() throws Exception {
        MockTransport mockTransport = openAndInjectMockTransport();

        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        setupOpen(mockTransport, null);

        Message message = setupSimpleMessage();
        message.save(mProviderContext);

        Body body = new Body();
        body.mMessageKey = message.mId;
        body.mTextContent = TEST_STRING;
        body.save(mProviderContext);

        // prepare for the message traffic we'll see
        // TODO The test is a bit fragile, as we are order-dependent (and headers are not)
        expectSimpleMessage(mockTransport);
        mockTransport.expect("Content-Type: text/plain; charset=utf-8");
        mockTransport.expect("Content-Transfer-Encoding: base64");
        mockTransport.expect("");
        mockTransport.expect(TEST_STRING_BASE64);
        mockTransport.expect("\r\n\\.", "250 2.0.0 kv2f1a00C02Rf8w3Vv mail accepted for delivery");

        // Now trigger the transmission
        mSender.sendMessage(message.mId);
    }

    /**
     * Test:  Open and send a single message with an empty attachment (no file) (sunny day)
     */
    public void testSendMessageWithEmptyAttachment() throws MessagingException, IOException {
        MockTransport mockTransport = openAndInjectMockTransport();

        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        setupOpen(mockTransport, null);

        Message message = setupSimpleMessage();
        message.save(mProviderContext);

        // Creates an attachment with a bogus file (so we get headers only)
        Attachment attachment = setupSimpleAttachment(mProviderContext, message.mId, false);
        attachment.save(mProviderContext);

        expectSimpleMessage(mockTransport);
        mockTransport.expect("Content-Type: multipart/mixed; boundary=\".*");
        mockTransport.expect("");
        mockTransport.expect("----.*");
        expectSimpleAttachment(mockTransport, attachment);
        mockTransport.expect("");
        mockTransport.expect("----.*--");
        mockTransport.expect("\r\n\\.", "250 2.0.0 kv2f1a00C02Rf8w3Vv mail accepted for delivery");

        // Now trigger the transmission
        mSender.sendMessage(message.mId);
    }

    /**
     * Test:  Open and send a single message with an attachment (sunny day)
     */
    public void testSendMessageWithAttachment() throws MessagingException, IOException {
        MockTransport mockTransport = openAndInjectMockTransport();

        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        setupOpen(mockTransport, null);

        Message message = setupSimpleMessage();
        message.save(mProviderContext);

        // Creates an attachment with a real file
        Attachment attachment = setupSimpleAttachment(mProviderContext, message.mId, true);
        attachment.save(mProviderContext);

        expectSimpleMessage(mockTransport);
        mockTransport.expect("Content-Type: multipart/mixed; boundary=\".*");
        mockTransport.expect("");
        mockTransport.expect("----.*");
        expectSimpleAttachment(mockTransport, attachment);
        mockTransport.expect("");
        mockTransport.expect("----.*--");
        mockTransport.expect("\r\n\\.", "250 2.0.0 kv2f1a00C02Rf8w3Vv mail accepted for delivery");

        // Now trigger the transmission
        mSender.sendMessage(message.mId);
    }

    /**
     * Test:  Open and send a single message with two attachments
     */
    public void testSendMessageWithTwoAttachments() throws MessagingException, IOException {
        MockTransport mockTransport = openAndInjectMockTransport();

        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        setupOpen(mockTransport, null);

        Message message = setupSimpleMessage();
        message.save(mProviderContext);

        // Creates an attachment with a real file
        Attachment attachment = setupSimpleAttachment(mProviderContext, message.mId, true);
        attachment.save(mProviderContext);

        // Creates an attachment with a real file
        Attachment attachment2 = setupSimpleAttachment(mProviderContext, message.mId, true);
        attachment2.save(mProviderContext);

        expectSimpleMessage(mockTransport);
        mockTransport.expect("Content-Type: multipart/mixed; boundary=\".*");
        mockTransport.expect("");
        mockTransport.expect("----.*");
        expectSimpleAttachment(mockTransport, attachment);
        mockTransport.expect("");
        mockTransport.expect("----.*");
        expectSimpleAttachment(mockTransport, attachment2);
        mockTransport.expect("");
        mockTransport.expect("----.*--");
        mockTransport.expect("\r\n\\.", "250 2.0.0 kv2f1a00C02Rf8w3Vv mail accepted for delivery");

        // Now trigger the transmission
        mSender.sendMessage(message.mId);
    }

    /**
     * Test:  Open and send a single message with body & attachment (sunny day)
     */
    public void testSendMessageWithBodyAndAttachment() throws MessagingException, IOException {
        MockTransport mockTransport = openAndInjectMockTransport();

        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        setupOpen(mockTransport, null);

        Message message = setupSimpleMessage();
        message.save(mProviderContext);

        Body body = new Body();
        body.mMessageKey = message.mId;
        body.mTextContent = TEST_STRING;
        body.save(mProviderContext);

        Attachment attachment = setupSimpleAttachment(mProviderContext, message.mId, true);
        attachment.save(mProviderContext);

        // prepare for the message traffic we'll see
        expectSimpleMessage(mockTransport);
        mockTransport.expect("Content-Type: multipart/mixed; boundary=\".*");
        mockTransport.expect("");
        mockTransport.expect("----.*");
        mockTransport.expect("Content-Type: text/plain; charset=utf-8");
        mockTransport.expect("Content-Transfer-Encoding: base64");
        mockTransport.expect("");
        mockTransport.expect(TEST_STRING_BASE64);
        mockTransport.expect("----.*");
        expectSimpleAttachment(mockTransport, attachment);
        mockTransport.expect("");
        mockTransport.expect("----.*--");
        mockTransport.expect("\r\n\\.", "250 2.0.0 kv2f1a00C02Rf8w3Vv mail accepted for delivery");

        // Now trigger the transmission
        mSender.sendMessage(message.mId);
    }

    /**
     * Prepare to send a simple message (see setReceiveSimpleMessage)
     */
    private Message setupSimpleMessage() {
        Message message = new Message();
        message.mTimeStamp = System.currentTimeMillis();
        message.mFrom = Address.parseAndPack("Jones@Registry.Org");
        message.mTo = Address.parseAndPack("Smith@Registry.Org");
        message.mMessageId = "1234567890";
        return message;
    }

    /**
     * Prepare to receive a simple message (see setupSimpleMessage)
     */
    private void expectSimpleMessage(MockTransport mockTransport) {
        mockTransport.expect("MAIL FROM: <Jones@Registry.Org>", 
                "250 2.1.0 <Jones@Registry.Org> sender ok");
        mockTransport.expect("RCPT TO: <Smith@Registry.Org>",
                "250 2.1.5 <Smith@Registry.Org> recipient ok");
        mockTransport.expect("DATA", "354 enter mail, end with . on a line by itself");
        mockTransport.expect("Date: .*");
        mockTransport.expect("Message-ID: .*");
        mockTransport.expect("From: Jones@Registry.Org");
        mockTransport.expect("To: Smith@Registry.Org");
        mockTransport.expect("MIME-Version: 1.0");
    }

    /**
     * Prepare to send a simple attachment
     */
    private Attachment setupSimpleAttachment(Context context, long messageId, boolean withBody)
            throws IOException {
        Attachment attachment = new Attachment();
        attachment.mFileName = "the file.jpg";
        attachment.mMimeType = "image/jpg";
        attachment.mSize = 0;
        attachment.mContentId = null;
        attachment.mContentUri = "content://com.android.email/1/1";
        attachment.mMessageKey = messageId;
        attachment.mLocation = null;
        attachment.mEncoding = null;

        if (withBody) {
            // Is there an easier way to set up a temp file?
            InputStream inStream = new ByteArrayInputStream(TEST_STRING.getBytes());
            File cacheDir = context.getCacheDir();
            File tmpFile = File.createTempFile("setupSimpleAttachment", "tmp", cacheDir);
            OutputStream outStream = new FileOutputStream(tmpFile);

            IOUtils.copy(inStream, outStream);
            attachment.mContentUri = "file://" + tmpFile.getAbsolutePath();
        }

        return attachment;
    }

    /**
     * Prepare to receive a simple attachment (note, no multipart support here)
     */
    private void expectSimpleAttachment(MockTransport mockTransport, Attachment attachment) {
        mockTransport.expect("Content-Type: " + attachment.mMimeType + ";");
        mockTransport.expect(" name=\"" + attachment.mFileName + "\"");
        mockTransport.expect("Content-Transfer-Encoding: base64");
        mockTransport.expect("Content-Disposition: attachment;");
        mockTransport.expect(" filename=\"" + attachment.mFileName + "\";");
        mockTransport.expect(" size=" + Long.toString(attachment.mSize));
        mockTransport.expect("");
        if (attachment.mContentUri != null && attachment.mContentUri.startsWith("file://")) {
            mockTransport.expect(TEST_STRING_BASE64);
        }
    }

    /**
     * Test:  Recover from a server closing early (or returning an empty string)
     */
    public void testEmptyLineResponse() throws Exception {
        MockTransport mockTransport = openAndInjectMockTransport();
        
        // Since SmtpSender.sendMessage() does a close then open, we need to preset for the open
        mockTransport.expectClose();
        
        // Load up just the bare minimum to expose the error
        mockTransport.expect(null, "220 MockTransport 2000 Ready To Assist You Peewee");
        mockTransport.expect("EHLO " + Pattern.quote(LOCAL_ADDRESS), "");
        
        // Now trigger the transmission
        // Note, a null message is sufficient here, as we won't even get past open()
        try {
            mSender.sendMessage(-1);
            fail("Should not be able to send with failed open()");
        } catch (MessagingException me) {
            // good - expected
            // TODO maybe expect a particular exception?
        }
    }
    
    /**
     * Set up a basic MockTransport. open it, and inject it into mStore
     */
    private MockTransport openAndInjectMockTransport() throws UnknownHostException {
        // Create mock transport and inject it into the SmtpSender that's already set up
        MockTransport mockTransport = new MockTransport();
        mockTransport.setSecurity(Transport.CONNECTION_SECURITY_NONE, false);
        mSender.setTransport(mockTransport);
        mockTransport.setMockLocalAddress(InetAddress.getByName(LOCAL_ADDRESS));
        return mockTransport;
    }
    
    /**
     * Helper which stuffs the mock with enough strings to satisfy a call to SmtpSender.open()
     * 
     * @param mockTransport the mock transport we're using
     * @param capabilities if non-null, comma-separated list of capabilities
     */
    private void setupOpen(MockTransport mockTransport, String capabilities) {
        mockTransport.expect(null, "220 MockTransport 2000 Ready To Assist You Peewee");
        mockTransport.expect("EHLO .*", "250-10.20.30.40 hello");
        if (capabilities == null) {
            mockTransport.expect(null, "250-HELP");
            mockTransport.expect(null, "250-AUTH LOGIN PLAIN CRAM-MD5");
            mockTransport.expect(null, "250-SIZE 15728640");
            mockTransport.expect(null, "250-ENHANCEDSTATUSCODES");
            mockTransport.expect(null, "250-8BITMIME");
        } else {
            for (String capability : capabilities.split(",")) {
                mockTransport.expect(null, "250-" + capability);
            }
        }
        mockTransport.expect(null, "250+OK");
        mockTransport.expect("AUTH PLAIN .*", "235 2.7.0 ... authentication succeeded");
    }
}
