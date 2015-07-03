/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.android.dumprendertree;

import com.android.dumprendertree.forwarder.ForwardService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class TestShellActivity extends Activity implements LayoutTestController {

    static enum DumpDataType {DUMP_AS_TEXT, EXT_REPR, NO_OP}

    // String constants for use with layoutTestController.overridePreferences
    private final String WEBKIT_OFFLINE_WEB_APPLICATION_CACHE_ENABLED = "WebKitOfflineWebApplicationCacheEnabled";

    public class AsyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMEOUT) {
                mTimedOut = true;
                if (mCallback != null)
                    mCallback.timedOut(mWebView.getUrl());
                if (!mRequestedWebKitData) {
                    requestWebKitData();
                } else {
                    // if timed out and webkit data has been dumped before
                    // finish directly
                    finished();
                }
                return;
            } else if (msg.what == MSG_WEBKIT_DATA) {
                TestShellActivity.this.dump(mTimedOut, (String)msg.obj);
                return;
            }

            super.handleMessage(msg);
        }
    }

    public void requestWebKitData() {
        Message callback = mHandler.obtainMessage(MSG_WEBKIT_DATA);

        if (mRequestedWebKitData)
            throw new AssertionError("Requested webkit data twice: " + mWebView.getUrl());

        mRequestedWebKitData = true;
        Log.v(LOGTAG, "message sent to WebView to dump text.");
        switch (mDumpDataType) {
            case DUMP_AS_TEXT:
                mWebView.documentAsText(callback);
                break;
            case EXT_REPR:
                mWebView.externalRepresentation(callback);
                break;
            default:
                finished();
                break;
        }
    }

    public void clearCache() {
      mWebView.freeMemory();
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);
        setContentView(contentView);

        mWebView = new WebView(this);
        mEventSender = new WebViewEventSender(mWebView);
        mCallbackProxy = new CallbackProxy(mEventSender, this);

        mWebView.addJavascriptInterface(mCallbackProxy, "layoutTestController");
        mWebView.addJavascriptInterface(mCallbackProxy, "eventSender");
        setupWebViewForLayoutTests(mWebView, mCallbackProxy);

        contentView.addView(mWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0f));

        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        // Expose window.gc function to JavaScript. JSC build exposes
        // this function by default, but V8 requires the flag to turn it on.
        // WebView::setJsFlags is noop in JSC build.
        mWebView.setJsFlags("--expose_gc");

        mHandler = new AsyncHandler();

        Intent intent = getIntent();
        if (intent != null) {
            executeIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        executeIntent(intent);
    }

    private void executeIntent(Intent intent) {
        resetTestStatus();
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) {
            return;
        }

        mTestUrl = intent.getStringExtra(TEST_URL);
        if (mTestUrl == null) {
            mUiAutoTestPath = intent.getStringExtra(UI_AUTO_TEST);
            if(mUiAutoTestPath != null) {
                beginUiAutoTest();
            }
            return;
        }

        mResultFile = intent.getStringExtra(RESULT_FILE);
        mTimeoutInMillis = intent.getIntExtra(TIMEOUT_IN_MILLIS, 0);
        mGetDrawtime = intent.getBooleanExtra(GET_DRAW_TIME, false);
        mSaveImagePath = intent.getStringExtra(SAVE_IMAGE);

        Log.v(LOGTAG, "  Loading " + mTestUrl);
        mWebView.loadUrl(mTestUrl);

        if (mTimeoutInMillis > 0) {
            // Create a timeout timer
            Message m = mHandler.obtainMessage(MSG_TIMEOUT);
            mHandler.sendMessageDelayed(m, mTimeoutInMillis);
        }
    }

    private void beginUiAutoTest() {
        try {
            mTestListReader = new BufferedReader(
                    new FileReader(mUiAutoTestPath));
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Failed to open test list for read.", ioe);
            finishUiAutoTest();
            return;
        }
        moveToNextTest();
    }

    private void finishUiAutoTest() {
        try {
            if(mTestListReader != null)
                mTestListReader.close();
        } catch (IOException ioe) {
            Log.w(LOGTAG, "Failed to close test list file.", ioe);
        }
        ForwardService.getForwardService().stopForwardService();
        finished();
    }

    private void moveToNextTest() {
        String url = null;
        try {
            url = mTestListReader.readLine();
        } catch (IOException ioe) {
            Log.e(LOGTAG, "Failed to read next test.", ioe);
            finishUiAutoTest();
            return;
        }
        if (url == null) {
            mUiAutoTestPath = null;
            finishUiAutoTest();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("All tests finished. Exit?")
                   .setCancelable(false)
                   .setPositiveButton("Yes", new OnClickListener(){
                       public void onClick(DialogInterface dialog, int which) {
                           TestShellActivity.this.finish();
                       }
                   })
                   .setNegativeButton("No", new OnClickListener(){
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.cancel();
                       }
                   });
            builder.create().show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(TestShellActivity.TEST_URL, FsUtils.getTestUrl(url));
        intent.putExtra(TIMEOUT_IN_MILLIS, 10000);
        executeIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(LOGTAG, "Low memory, clearing caches");
        mWebView.freeMemory();
    }

    // Dump the page
    public void dump(boolean timeout, String webkitData) {
        mDumpWebKitData = true;
        if (mResultFile == null || mResultFile.length() == 0) {
            finished();
            return;
        }

        try {
            File parentDir = new File(mResultFile).getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileOutputStream os = new FileOutputStream(mResultFile);
            if (timeout) {
                Log.w("Layout test: Timeout", mResultFile);
                os.write(TIMEOUT_STR.getBytes());
                os.write('\n');
            }
            if (mDumpTitleChanges)
                os.write(mTitleChanges.toString().getBytes());
            if (mDialogStrings != null)
                os.write(mDialogStrings.toString().getBytes());
            mDialogStrings = null;
            if (mDatabaseCallbackStrings != null)
                os.write(mDatabaseCallbackStrings.toString().getBytes());
            mDatabaseCallbackStrings = null;
            if (mConsoleMessages != null)
                os.write(mConsoleMessages.toString().getBytes());
            mConsoleMessages = null;
            if (webkitData != null)
                os.write(webkitData.getBytes());
            os.flush();
            os.close();
        } catch (IOException ex) {
            Log.e(LOGTAG, "Cannot write to " + mResultFile + ", " + ex.getMessage());
        }

        finished();
    }

    public void setCallback(TestShellCallback callback) {
        mCallback = callback;
    }

    public boolean finished() {
        if (canMoveToNextTest()) {
            mHandler.removeMessages(MSG_TIMEOUT);
            if (mUiAutoTestPath != null) {
                //don't really finish here
                moveToNextTest();
            } else {
                if (mCallback != null) {
                    mCallback.finished();
                }
            }
            return true;
        }
        return false;
    }

    public void setDefaultDumpDataType(DumpDataType defaultDumpDataType) {
        mDefaultDumpDataType = defaultDumpDataType;
    }

    // .......................................
    // LayoutTestController Functions
    public void dumpAsText() {
        mDumpDataType = DumpDataType.DUMP_AS_TEXT;
        if (mWebView != null) {
            String url = mWebView.getUrl();
            Log.v(LOGTAG, "dumpAsText called: "+url);
        }
    }

    public void waitUntilDone() {
        mWaitUntilDone = true;
        String url = mWebView.getUrl();
        Log.v(LOGTAG, "waitUntilDone called: " + url);
    }

    public void notifyDone() {
        String url = mWebView.getUrl();
        Log.v(LOGTAG, "notifyDone called: " + url);
        if (mWaitUntilDone) {
            mWaitUntilDone = false;
            mChromeClient.onProgressChanged(mWebView, 101);
        }
    }

    public void display() {
        mWebView.invalidate();
    }

    public void clearBackForwardList() {
        mWebView.clearHistory();

    }

    public void dumpBackForwardList() {
        //printf("\n============== Back Forward List ==============\n");
        // mWebHistory
        //printf("===============================================\n");

    }

    public void dumpChildFrameScrollPositions() {
        // TODO Auto-generated method stub

    }

    public void dumpEditingCallbacks() {
        // TODO Auto-generated method stub

    }

    public void dumpSelectionRect() {
        // TODO Auto-generated method stub

    }

    public void dumpTitleChanges() {
        if (!mDumpTitleChanges) {
            mTitleChanges = new StringBuffer();
        }
        mDumpTitleChanges = true;
    }

    public void keepWebHistory() {
        if (!mKeepWebHistory) {
            mWebHistory = new Vector();
        }
        mKeepWebHistory = true;
    }

    public void queueBackNavigation(int howfar) {
        // TODO Auto-generated method stub

    }

    public void queueForwardNavigation(int howfar) {
        // TODO Auto-generated method stub

    }

    public void queueLoad(String Url, String frameTarget) {
        // TODO Auto-generated method stub

    }

    public void queueReload() {
        mWebView.reload();
    }

    public void queueScript(String scriptToRunInCurrentContext) {
        mWebView.loadUrl("javascript:"+scriptToRunInCurrentContext);
    }

    public void repaintSweepHorizontally() {
        // TODO Auto-generated method stub

    }

    public void setAcceptsEditing(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setMainFrameIsFirstResponder(boolean b) {
        // TODO Auto-generated method stub

    }

    public void setWindowIsKey(boolean b) {
        // This is meant to show/hide the window. The best I can find
        // is setEnabled()
        mWebView.setEnabled(b);
    }

    public void testRepaint() {
        mWebView.invalidate();
    }

    public void dumpDatabaseCallbacks() {
        Log.v(LOGTAG, "dumpDatabaseCallbacks called.");
        mDumpDatabaseCallbacks = true;
    }

    public void setCanOpenWindows() {
        Log.v(LOGTAG, "setCanOpenWindows called.");
        mCanOpenWindows = true;
    }

    /**
     * Sets the Geolocation permission state to be used for all future requests.
     */
    public void setGeolocationPermission(boolean allow) {
        mGeolocationPermissionSet = true;
        mGeolocationPermission = allow;
    }

    public void overridePreference(String key, boolean value) {
        // TODO: We should look up the correct WebView for the frame which
        // called the layoutTestController method. Currently, we just use the
        // WebView for the main frame. EventSender suffers from the same
        // problem.
        if (key.equals(WEBKIT_OFFLINE_WEB_APPLICATION_CACHE_ENABLED)) {
            mWebView.getSettings().setAppCacheEnabled(value);
        }
    }

    private final WebViewClient mViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.v(LOGTAG, "onPageFinished, url=" + url);
            mPageFinished = true;
            // get page draw time
            if (FsUtils.isTestPageUrl(url)) {
                if (mGetDrawtime) {
                    long[] times = new long[DRAW_RUNS];
                    times = getDrawWebViewTime(mWebView, DRAW_RUNS);
                    FsUtils.writeDrawTime(DRAW_TIME_LOG, url, times);
                }
                if (mSaveImagePath != null) {
                    String name = FsUtils.getLastSegmentInPath(url);
                    drawPageToFile(mSaveImagePath + "/" + name + ".png", mWebView);
                }
            }
            // Calling finished() will check if we've met all the conditions for completing
            // this test and move to the next one if we are ready.
            if (finished()) {
                return;
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.v(LOGTAG, "onPageStarted, url=" + url);
            mPageFinished = false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            Log.v(LOGTAG, "onReceivedError, errorCode=" + errorCode
                    + ", desc=" + description + ", url=" + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler,
                String host, String realm) {
            if (handler.useHttpAuthUsernamePassword() && view != null) {
                String[] credentials = view.getHttpAuthUsernamePassword(host, realm);
                if (credentials != null && credentials.length == 2) {
                    handler.proceed(credentials[0], credentials[1]);
                    return;
                }
            }
            handler.cancel();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                SslError error) {
            handler.proceed();
        }
    };


    private final WebChromeClient mChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            // notifyDone calls this with 101%. We only want to update this flag if this
            // is the real call from WebCore.
            if (newProgress == 100) {
                mOneHundredPercentComplete = true;
            }

            // With the flag updated, we can now proceed as normal whether the progress update came from
            // WebCore or notifyDone.
            if (newProgress >= 100) {
                // finished() will check if we are ready to move to the next test and do so if we are.
                if (finished()) {
                    return;
                }

                if (!mTimedOut && !mWaitUntilDone && !mRequestedWebKitData) {
                    String url = mWebView.getUrl();
                    Log.v(LOGTAG, "Finished: "+ url);
                    requestWebKitData();
                } else {
                    String url = mWebView.getUrl();
                    if (mTimedOut) {
                        Log.v(LOGTAG, "Timed out before finishing: " + url);
                    } else if (mWaitUntilDone) {
                        Log.v(LOGTAG, "Waiting for notifyDone: " + url);
                    } else if (mRequestedWebKitData) {
                        Log.v(LOGTAG, "Requested webkit data ready: " + url);
                    }
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title.length() > 30)
                title = "..."+title.substring(title.length()-30);
            setTitle(title);
            if (mDumpTitleChanges) {
                mTitleChanges.append("TITLE CHANGED: ");
                mTitleChanges.append(title);
                mTitleChanges.append("\n");
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                JsResult result) {
            if (mDialogStrings == null) {
                mDialogStrings = new StringBuffer();
            }
            mDialogStrings.append("ALERT: ");
            mDialogStrings.append(message);
            mDialogStrings.append('\n');
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                JsResult result) {
            if (mDialogStrings == null) {
                mDialogStrings = new StringBuffer();
            }
            mDialogStrings.append("CONFIRM: ");
            mDialogStrings.append(message);
            mDialogStrings.append('\n');
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                String defaultValue, JsPromptResult result) {
            if (mDialogStrings == null) {
                mDialogStrings = new StringBuffer();
            }
            mDialogStrings.append("PROMPT: ");
            mDialogStrings.append(message);
            mDialogStrings.append(", default text: ");
            mDialogStrings.append(defaultValue);
            mDialogStrings.append('\n');
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsTimeout() {
            Log.v(LOGTAG, "JavaScript timeout");
            return false;
        }

        @Override
        public void onExceededDatabaseQuota(String url_str,
                String databaseIdentifier, long currentQuota,
                long estimatedSize, long totalUsedQuota,
                WebStorage.QuotaUpdater callback) {
            if (mDumpDatabaseCallbacks) {
                if (mDatabaseCallbackStrings == null) {
                    mDatabaseCallbackStrings = new StringBuffer();
                }

                String protocol = "";
                String host = "";
                int port = 0;

                try {
                    URL url = new URL(url_str);
                    protocol = url.getProtocol();
                    host = url.getHost();
                    if (url.getPort() > -1) {
                        port = url.getPort();
                    }
                } catch (MalformedURLException e) {}

                String databaseCallbackString =
                        "UI DELEGATE DATABASE CALLBACK: " +
                        "exceededDatabaseQuotaForSecurityOrigin:{" + protocol +
                        ", " + host + ", " + port + "} database:" +
                        databaseIdentifier + "\n";
                Log.v(LOGTAG, "LOG: "+databaseCallbackString);
                mDatabaseCallbackStrings.append(databaseCallbackString);
            }
            // Give 5MB more quota.
            callback.updateQuota(currentQuota + 1024 * 1024 * 5);
        }

        /**
         * Instructs the client to show a prompt to ask the user to set the
         * Geolocation permission state for the specified origin.
         */
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                GeolocationPermissions.Callback callback) {
            if (mGeolocationPermissionSet) {
                callback.invoke(origin, mGeolocationPermission, false);
            }
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber,
                String sourceID) {
            if (mConsoleMessages == null) {
                mConsoleMessages = new StringBuffer();
            }
            String consoleMessage = "CONSOLE MESSAGE: line "
                    + lineNumber +": "+ message +"\n";
            mConsoleMessages.append(consoleMessage);
            Log.v(LOGTAG, "LOG: "+consoleMessage);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog,
                boolean userGesture, Message resultMsg) {
            if (!mCanOpenWindows) {
                // We can't open windows, so just send null back.
                WebView.WebViewTransport transport =
                        (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(null);
                resultMsg.sendToTarget();
                return true;
            }

            // We never display the new window, just create the view and
            // allow it's content to execute and be recorded by the test
            // runner.

            HashMap<String, Object> jsIfaces = new HashMap<String, Object>();
            jsIfaces.put("layoutTestController", mCallbackProxy);
            jsIfaces.put("eventSender", mCallbackProxy);
            WebView newWindowView = new NewWindowWebView(TestShellActivity.this, jsIfaces);
            setupWebViewForLayoutTests(newWindowView, mCallbackProxy);
            WebView.WebViewTransport transport =
                    (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWindowView);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView view) {
            view.destroy();
        }
    };

    private static class NewWindowWebView extends WebView {
        public NewWindowWebView(Context context, Map<String, Object> jsIfaces) {
            super(context, null, 0, jsIfaces);
        }
    }

    private void resetTestStatus() {
        mWaitUntilDone = false;
        mDumpDataType = mDefaultDumpDataType;
        mTimedOut = false;
        mDumpTitleChanges = false;
        mRequestedWebKitData = false;
        mDumpDatabaseCallbacks = false;
        mCanOpenWindows = false;
        mEventSender.resetMouse();
        mEventSender.clearTouchPoints();
        mEventSender.clearTouchMetaState();
        mPageFinished = false;
        mOneHundredPercentComplete = false;
        mDumpWebKitData = false;
        mGetDrawtime = false;
        mSaveImagePath = null;
    }

    private long[] getDrawWebViewTime(WebView view, int count) {
        if (count == 0)
            return null;
        long[] ret = new long[count];
        long start;
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        for (int i = 0; i < count; i++) {
            start = System.currentTimeMillis();
            view.draw(canvas);
            ret[i] = System.currentTimeMillis() - start;
        }
        return ret;
    }

    private void drawPageToFile(String fileName, WebView view) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(view.getContentWidth(), view.getContentHeight(),
                Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        view.drawPage(canvas);
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            if(!bitmap.compress(CompressFormat.PNG, 90, fos)) {
                Log.w(LOGTAG, "Failed to compress and save image.");
            }
        } catch (IOException ioe) {
            Log.e(LOGTAG, "", ioe);
        }
        bitmap.recycle();
    }

    private boolean canMoveToNextTest() {
        return (mDumpWebKitData && mOneHundredPercentComplete && mPageFinished && !mWaitUntilDone) || mTimedOut;
    }

    private void setupWebViewForLayoutTests(WebView webview, CallbackProxy callbackProxy) {
        if (webview == null) {
            return;
        }

        WebSettings settings = webview.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getApplicationContext().getCacheDir().getPath());
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getDir("databases",0).getAbsolutePath());
        settings.setDomStorageEnabled(true);
        settings.setWorkersEnabled(false);

        webview.setWebChromeClient(mChromeClient);
        webview.setWebViewClient(mViewClient);
        // Setting a touch interval of -1 effectively disables the optimisation in WebView
        // that stops repeated touch events flooding WebCore. The Event Sender only sends a
        // single event rather than a stream of events (like what would generally happen in
        // a real use of touch events in a WebView)  and so if the WebView drops the event,
        // the test will fail as the test expects one callback for every touch it synthesizes.
        webview.setTouchInterval(-1);
    }

    private WebView mWebView;
    private WebViewEventSender mEventSender;
    private AsyncHandler mHandler;
    private TestShellCallback mCallback;

    private CallbackProxy mCallbackProxy;

    private String mTestUrl;
    private String mResultFile;
    private int mTimeoutInMillis;
    private String mUiAutoTestPath;
    private String mSaveImagePath;
    private BufferedReader mTestListReader;
    private boolean mGetDrawtime;

    // States
    private boolean mTimedOut;
    private boolean mRequestedWebKitData;
    private boolean mFinishedRunning;

    // Layout test controller variables.
    private DumpDataType mDumpDataType;
    private DumpDataType mDefaultDumpDataType = DumpDataType.EXT_REPR;
    private boolean mWaitUntilDone;
    private boolean mDumpTitleChanges;
    private StringBuffer mTitleChanges;
    private StringBuffer mDialogStrings;
    private boolean mKeepWebHistory;
    private Vector mWebHistory;
    private boolean mDumpDatabaseCallbacks;
    private StringBuffer mDatabaseCallbackStrings;
    private StringBuffer mConsoleMessages;
    private boolean mCanOpenWindows;

    private boolean mPageFinished = false;
    private boolean mDumpWebKitData = false;
    private boolean mOneHundredPercentComplete = false;

    static final String TIMEOUT_STR = "**Test timeout";

    static final int MSG_TIMEOUT = 0;
    static final int MSG_WEBKIT_DATA = 1;

    static final String LOGTAG="TestShell";

    static final String TEST_URL = "TestUrl";
    static final String RESULT_FILE = "ResultFile";
    static final String TIMEOUT_IN_MILLIS = "TimeoutInMillis";
    static final String UI_AUTO_TEST = "UiAutoTest";
    static final String GET_DRAW_TIME = "GetDrawTime";
    static final String SAVE_IMAGE = "SaveImage";

    static final int DRAW_RUNS = 5;
    static final String DRAW_TIME_LOG = "/sdcard/android/page_draw_time.txt";

    private boolean mGeolocationPermissionSet;
    private boolean mGeolocationPermission;
}
