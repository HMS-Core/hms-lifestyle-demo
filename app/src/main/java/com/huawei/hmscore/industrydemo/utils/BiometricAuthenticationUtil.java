/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hmscore.industrydemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.huawei.hmscore.industrydemo.R;

import java.util.Locale;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/11/11]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class BiometricAuthenticationUtil {
    private static final String SONY = "sony";

    private static final String OPPO = "oppo";

    private static final String HUAWEI = "huawei";

    private static final String HONOR = "honor";

    private static final String SETTINGS_PCG = "com.android.settings";

    private static final String SETTINGS_CLS = "com.android.settings.Settings";

    public static void startFingerprint(Context context) {
        String brand = android.os.Build.BRAND;
        String pcgName = null;
        String clsName = null;
        if (compareTextSame(SONY, brand)) {
            pcgName = "com.android.settings";
            clsName = "com.android.settings.Settings$FingerprintEnrollSuggestionActivity";
        } else if (compareTextSame(OPPO, brand)) {
            pcgName = "com.coloros.fingerprint";
            clsName = "com.coloros.fingerprint.FingerLockActivity";
        } else if (compareTextSame(HUAWEI, brand)) {
            pcgName = "com.android.settings";
            clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
        } else if (compareTextSame(HONOR, brand)) {
            pcgName = "com.android.settings";
            clsName = "com.android.settings.fingerprint.FingerprintSettingsActivity";
        }

        if (!TextUtils.isEmpty(pcgName) && !TextUtils.isEmpty(clsName)) {
            startActivity(context, pcgName, clsName);
        } else {
            showDialog(context, context.getString(R.string.enroll_fingerprint),
                context.getString(R.string.enroll_fingerprint_coaching));
        }
    }

    public static void startFace(Context context) {
        showDialog(context, context.getString(R.string.enroll_face), context.getString(R.string.enroll_face_coaching));
    }

    private static boolean compareTextSame(String value, String brand) {
        return value.toUpperCase(Locale.ROOT).indexOf(brand.toUpperCase(Locale.ROOT)) >= 0;
    }

    private static void startActivity(Context context, String pcgName, String clsName) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pcgName, clsName);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setComponent(componentName);
        context.startActivity(intent);
    }

    private static void showDialog(Context context, String title, String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                    dialog.dismiss();
                    startActivity(context, SETTINGS_PCG, SETTINGS_CLS);
                }));
                builder.show();
            }
        });
    }
}
