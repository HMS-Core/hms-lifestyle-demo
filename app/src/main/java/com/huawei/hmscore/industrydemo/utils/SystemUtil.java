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

package com.huawei.hmscore.industrydemo.utils;

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.LATEST_VERSION_NUM;
import static com.huawei.hmscore.industrydemo.constants.LogConfig.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hmscore.industrydemo.BuildConfig;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.agc.RemoteConfigUtil;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;
import com.huawei.hmscore.industrydemo.wight.VersionDialog;

import java.util.Locale;
import java.util.Map;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SystemUtil {
    private static final UserRepository USER_REPOSITORY = new UserRepository();

    public static final String PACKAGE_NAME_CN = "com.huawei.hmscore.industrydemo.cn";

    /**
     * just package region
     * @param context context
     * @return bool
     */
    public static boolean isChinaPackage(Context context){
        String page = context.getPackageName();
        return page.equals(PACKAGE_NAME_CN);
    }

    /**
     * getLanguage
     *
     * @return Language
     */
    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (Constants.LANGUAGE_ZH.equals(language)) {
            return language;
        }
        return Constants.LANGUAGE_EN;
    }

    /**
     * Version update
     *
     * @param mActivity Activity
     */
    public static void checkForUpdates(Activity mActivity) {
        Log.i(TAG,"checkForUpdates");
        StatusDialogUtil statusDialog = new StatusDialogUtil(mActivity);
        statusDialog.show(mActivity.getString(R.string.version_checking));
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        Bundle data = new Bundle();
        if (results.containsKey(LATEST_VERSION_NUM)) {
            statusDialog.dismiss();
            if (config.getValueAsLong(LATEST_VERSION_NUM) <= BuildConfig.VERSION_CODE) {
                VersionDialog dialog = new VersionDialog(mActivity, true);
                dialog.show();
            } else {
                VersionDialog dialog = new VersionDialog(mActivity, false);
                dialog.setConfirmListener(v -> {
                    Uri uri = Uri.parse(config.getValueAsString(KeyConstants.DOWNLOAD_LINK));
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    dialog.dismiss();
                });
                dialog.show();
            }
        } else {
            statusDialog.dismiss();
            Log.e(TAG, LATEST_VERSION_NUM + " is null!");
            data.putString("Content", mActivity.getString(R.string.no_found_new_version));
            BaseDialog dialog = new BaseDialog(mActivity, data, false);
            dialog.setConfirmListener(v -> dialog.dismiss());
            dialog.show();
            RemoteConfigUtil.fetch();
        }
    }

    /**
     * Version update when demo is started
     *
     * @param mActivity Activity
     */
    public static void checkForUpdatesWhenStart(Activity mActivity) {
        Log.i(TAG,"checkForUpdatesWhenStart");
        StatusDialogUtil statusDialog = new StatusDialogUtil(mActivity);
        statusDialog.show(mActivity.getString(R.string.version_checking));
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey(LATEST_VERSION_NUM)) {
            statusDialog.dismiss();
            if (config.getValueAsLong(LATEST_VERSION_NUM) > BuildConfig.VERSION_CODE) {
                VersionDialog dialog = new VersionDialog(mActivity, false);
                dialog.setConfirmListener(v -> {
                    Uri uri = Uri.parse(config.getValueAsString(KeyConstants.DOWNLOAD_LINK));
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    dialog.dismiss();
                });
                dialog.show();
            }
        } else {
            Log.i(TAG,"checkForUpdatesWhenStart fail");
            statusDialog.dismiss();
        }
    }

    /**
     * set StatusBar Color
     *
     * @param activity activity
     * @param colorId colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(colorId, null));

    }

    /**
     * setAndroidNativeLightStatusBar
     *
     * @param activity activity
     * @param dark isDark
     */
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * Whether the vertical screen
     *
     * @param context Context
     * @return boolean Whether the vertical screen
     */
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    /**
     * @return user is login
     */
    public static boolean isLogin(){
        User user = USER_REPOSITORY.getCurrentUser();
        return user != null;
    }
}
