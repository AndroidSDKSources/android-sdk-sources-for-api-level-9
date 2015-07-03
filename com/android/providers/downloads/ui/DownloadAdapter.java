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

package com.android.providers.downloads.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.drm.mobile1.DrmRawContent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.providers.downloads.ui.DownloadItem.DownloadSelectListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * List adapter for Cursors returned by {@link DownloadManager}.
 */
public class DownloadAdapter extends CursorAdapter {
    private Context mContext;
    private Cursor mCursor;
    private DownloadSelectListener mDownloadSelectionListener;
    private Resources mResources;
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;

    private int mTitleColumnId;
    private int mDescriptionColumnId;
    private int mStatusColumnId;
    private int mReasonColumnId;
    private int mTotalBytesColumnId;
    private int mMediaTypeColumnId;
    private int mDateColumnId;
    private int mIdColumnId;

    public DownloadAdapter(Context context, Cursor cursor,
            DownloadSelectListener selectionListener) {
        super(context, cursor);
        mContext = context;
        mCursor = cursor;
        mResources = mContext.getResources();
        mDownloadSelectionListener = selectionListener;
        mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        mIdColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
        mTitleColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE);
        mDescriptionColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION);
        mStatusColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
        mReasonColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
        mTotalBytesColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        mMediaTypeColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
        mDateColumnId =
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);
    }

    public View newView() {
        DownloadItem view = (DownloadItem) LayoutInflater.from(mContext)
                .inflate(R.layout.download_list_item, null);
        view.setSelectListener(mDownloadSelectionListener);
        return view;
    }

    public void bindView(View convertView) {
        if (!(convertView instanceof DownloadItem)) {
            return;
        }

        long downloadId = mCursor.getLong(mIdColumnId);
        ((DownloadItem) convertView).setDownloadId(downloadId);

        // Retrieve the icon for this download
        retrieveAndSetIcon(convertView);

        String title = mCursor.getString(mTitleColumnId);
        if (title.isEmpty()) {
            title = mResources.getString(R.string.missing_title);
        }
        setTextForView(convertView, R.id.download_title, title);
        setTextForView(convertView, R.id.domain, mCursor.getString(mDescriptionColumnId));
        setTextForView(convertView, R.id.size_text, getSizeText());
        setTextForView(convertView, R.id.status_text, mResources.getString(getStatusStringId()));
        setTextForView(convertView, R.id.last_modified_date, getDateString());

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.download_checkbox);
        checkBox.setChecked(mDownloadSelectionListener.isDownloadSelected(downloadId));
    }

    private String getDateString() {
        Date date = new Date(mCursor.getLong(mDateColumnId));
        if (date.before(getStartOfToday())) {
            return mDateFormat.format(date);
        } else {
            return mTimeFormat.format(date);
        }
    }

    private Date getStartOfToday() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTime();
    }

    private String getSizeText() {
        long totalBytes = mCursor.getLong(mTotalBytesColumnId);
        String sizeText = "";
        if (totalBytes >= 0) {
            sizeText = Formatter.formatFileSize(mContext, totalBytes);
        }
        return sizeText;
    }

    private int getStatusStringId() {
        switch (mCursor.getInt(mStatusColumnId)) {
            case DownloadManager.STATUS_FAILED:
                return R.string.download_error;

            case DownloadManager.STATUS_SUCCESSFUL:
                return R.string.download_success;

            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                return R.string.download_running;

            case DownloadManager.STATUS_PAUSED:
                if (mCursor.getInt(mReasonColumnId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI) {
                    return R.string.download_queued;
                } else {
                    return R.string.download_running;
                }
        }
        throw new IllegalStateException("Unknown status: " + mCursor.getInt(mStatusColumnId));
    }

    private void retrieveAndSetIcon(View convertView) {
        String mediaType = mCursor.getString(mMediaTypeColumnId);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.download_icon);
        iconView.setVisibility(View.INVISIBLE);

        if (mediaType == null) {
            return;
        }

        if (DrmRawContent.DRM_MIMETYPE_MESSAGE_STRING.equalsIgnoreCase(mediaType)) {
            iconView.setImageResource(R.drawable.ic_launcher_drm_file);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromParts("file", "", null), mediaType);
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() == 0) {
                // no icon found for this mediatype. use "unknown" icon
                iconView.setImageResource(R.drawable.ic_download_misc_file_type);
            } else {
                Drawable icon = list.get(0).activityInfo.loadIcon(pm);
                iconView.setImageDrawable(icon);
            }
        }
        iconView.setVisibility(View.VISIBLE);
    }

    private void setTextForView(View parent, int textViewId, String text) {
        TextView view = (TextView) parent.findViewById(textViewId);
        view.setText(text);
    }

    // CursorAdapter overrides

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return newView();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindView(view);
    }
}
