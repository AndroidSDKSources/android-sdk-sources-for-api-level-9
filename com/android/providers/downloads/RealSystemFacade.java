package com.android.providers.downloads;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

class RealSystemFacade implements SystemFacade {
    private Context mContext;
    private NotificationManager mNotificationManager;

    public RealSystemFacade(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public Integer getActiveNetworkType() {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return null;
        }

        NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "network is not available");
            }
            return null;
        }
        return activeInfo.getType();
    }

    public boolean isNetworkRoaming() {
        ConnectivityManager connectivity =
            (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        boolean isMobile = (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE);
        boolean isRoaming = isMobile && TelephonyManager.getDefault().isNetworkRoaming();
        if (Constants.LOGVV && isRoaming) {
            Log.v(Constants.TAG, "network is roaming");
        }
        return isRoaming;
    }

    public Long getMaxBytesOverMobile() {
        try {
            return Settings.Secure.getLong(mContext.getContentResolver(),
                    Settings.Secure.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
        } catch (SettingNotFoundException exc) {
            return null;
        }
    }

    @Override
    public Long getRecommendedMaxBytesOverMobile() {
        try {
            return Settings.Secure.getLong(mContext.getContentResolver(),
                    Settings.Secure.DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE);
        } catch (SettingNotFoundException exc) {
            return null;
        }
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mContext.sendBroadcast(intent);
    }

    @Override
    public boolean userOwnsPackage(int uid, String packageName) throws NameNotFoundException {
        return mContext.getPackageManager().getApplicationInfo(packageName, 0).uid == uid;
    }

    @Override
    public void postNotification(long id, Notification notification) {
        /**
         * TODO: The system notification manager takes ints, not longs, as IDs, but the download
         * manager uses IDs take straight from the database, which are longs.  This will have to be
         * dealt with at some point.
         */
        mNotificationManager.notify((int) id, notification);
    }

    @Override
    public void cancelNotification(long id) {
        mNotificationManager.cancel((int) id);
    }

    @Override
    public void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    @Override
    public void startThread(Thread thread) {
        thread.start();
    }
}
