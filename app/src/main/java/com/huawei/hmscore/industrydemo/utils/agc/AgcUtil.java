/*
    Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.hmscore.industrydemo.utils.agc;

import static com.huawei.hmscore.industrydemo.constants.LogConfig.TAG;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CONTENT;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.agconnect.cloud.storage.core.StorageException;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmscore.industrydemo.MainApplication;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class AgcUtil {
    private static String apiKey;

    private static String projectId;

    private static String appId;

    private static String clientSecret;

    private static String clientId;

    private static volatile AGConnectConfig config;

    private static volatile AGConnectOptions options;

    private static volatile AGCStorageManagement storageManagement;

    public static synchronized AGConnectConfig getConfig() {
        if (config == null) {
            config = AGConnectConfig.getInstance();
        }
        return config;
    }

    private static synchronized AGConnectOptions getOptions(Context context) {
        if (options == null) {
            options = new AGConnectOptionsBuilder().build(context);
        }
        return options;
    }

    public static synchronized AGCStorageManagement getStorageManagement() {
        if (storageManagement == null) {
            storageManagement = AGCStorageManagement.getInstance();
        }
        return storageManagement;
    }

    /**
     * reportException
     *
     * @param tag TAG
     * @param throwable Throwable
     */
    public static void reportException(String tag, Throwable throwable) {
        reportException(tag, throwable.getMessage(), throwable);
    }

    /**
     * reportException
     *
     * @param tag TAG
     * @param msg message
     * @param throwable Throwable
     */
    public static void reportException(String tag, String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
        AGConnectCrash.getInstance().recordException(throwable);
    }

    /**
     * reportFailure
     *
     * @param tag TAG
     * @param failureMsg String
     */
    public static void reportFailure(String tag, String failureMsg) {
        Log.w(tag, failureMsg);
        AGConnectCrash.getInstance().log(Log.WARN, failureMsg);
    }

    /**
     * Obtain getProjectId.
     *
     * @param context context
     * @return project_id
     */
    public static synchronized String getProjectId(Context context) {
        if (projectId == null) {
            projectId = getOptions(context).getString("client/project_id");
        }
        return projectId;
    }

    /**
     * Obtain App Id.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getAppId(Context context) {
        if (appId == null) {
            appId = getOptions(context).getString("client/app_id");
        }
        return appId;
    }

    /**
     * Obtain Api Key.
     *
     * @param context context
     * @return apiKey
     */
    public static synchronized String getApiKey(Context context) {
        if (apiKey == null) {
            apiKey = getOptions(context).getString("client/api_key");
        }
        return apiKey;
    }

    public static String getApiKey() {
        // get apiKey from AppGallery Connect
        String apiKey = AgcUtil.getApiKey(MainApplication.getContext());
        // need encodeURI the apiKey
        try {
            return URLEncoder.encode(apiKey, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
            AgcUtil.reportException(TAG, e);
            return new String();
        }
    }


    /**
     * Downloading files from cloud storage
     *
     * @param context Context
     * @param sourcePath String
     * @param progressDialog ProgressDialog
     * @param onSuccessListener OnSuccessListener<Object>
     */
    @SuppressLint("WrongConstant")
    public static void downloadFile(Context context, String sourcePath, ProgressDialog progressDialog,
                                    @NonNull OnSuccessListener<Object> onSuccessListener) {
        Log.d(TAG, sourcePath);
        File targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + sourcePath);
        StorageReference reference = getStorageManagement().getStorageReference(sourcePath);
        DownloadTask downloadTask = reference.getFile(targetFile);
        downloadTask.addOnFailureListener(exception -> {
            reportException(TAG, exception);
            if (StorageException.fromException(exception)
                    .getErrorCode() == StorageException.ERROR_RANGE_UNSATISFIABLE) {
                onSuccessListener.onSuccess(new Object());
            } else {
                progressDialog.dismiss();
                Bundle data = new Bundle();
                data.putString(CONTENT, context.getString(R.string.download_failed));
                BaseDialog dialog = new BaseDialog(context, data, false);
                dialog.show();
            }
        }).addOnSuccessListener(downloadResult -> {
            Log.d(TAG, String.valueOf(downloadResult.getTotalByteCount()));
            onSuccessListener.onSuccess(downloadResult);
        }).addOnProgressListener(downloadResult -> {
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressNumberFormat("%1d/%2d");
            progressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
            int value = (int) (downloadResult.getBytesTransferred() * 100 / downloadResult.getTotalByteCount());
            progressDialog.setProgress(value);
        }).addOnCompleteListener(task -> {
            progressDialog.incrementSecondaryProgressBy(25);
        });
    }

    /**
     * Obtain getClientSecret.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getClientSecret(Context context) {
        if (clientSecret == null) {
            clientSecret = getOptions(context).getString("client/client_secret");
        }
        return clientSecret;
    }

    /**
     * Obtain getClientId.
     *
     * @param context context
     * @return appId
     */
    public static synchronized String getClientId(Context context) {
        if (clientId == null) {
            clientId = getOptions(context).getString("oauth_client/client_id");
        }
        return clientId;
    }
}
