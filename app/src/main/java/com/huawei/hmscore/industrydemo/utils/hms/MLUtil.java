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

package com.huawei.hmscore.industrydemo.utils.hms;

import static com.huawei.hms.analytics.type.HAEventType.SEARCH;
import static com.huawei.hms.analytics.type.HAParamType.SEARCHKEYWORDS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.constants.RequestCodeConstants;
import com.huawei.hmscore.industrydemo.page.activity.SearchResultActivity;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

public class MLUtil {

    public final String TAG = MLUtil.class.getSimpleName();

    private final int SEARCH_RESULT = 101;

    public void goToAsr(Activity activity) {
        Intent intentPlugin = new Intent(activity, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, MLAsrConstants.LAN_ZH_CN)
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        activity.startActivityForResult(intentPlugin, RequestCodeConstants.ML_ASR_CAPTURE_CODE);
        AnalyticsUtil.voiceSearch();
    }

    public String dealAsrResult(int resultCode, Intent data, Activity activity) {
        String text = "";
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            return text;
        }
        switch (resultCode) {
            // MLAsrCaptureConstants.ASR_SUCCESS: Recognition is successful.
            case MLAsrCaptureConstants.ASR_SUCCESS:
                // Obtain the text information recognized from speech.
                if (bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                    text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                }
                if (TextUtils.isEmpty(text)) {
                    text = "Result is null.";
                    Log.e(TAG, text);
                } else {
                    goSearch(text, true, activity);
                }
                // Process the recognized text information.
                break;
            case MLAsrCaptureConstants.ASR_FAILURE:
                // Check whether a result code is contained.
                if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                    text = text + bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                    // Perform troubleshooting based on the result code.
                }
                // Check whether error information is contained.
                if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                    String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                    // Perform troubleshooting based on the error information.
                    if (!TextUtils.isEmpty(errorMsg)) {
                        text = "[" + text + "]" + errorMsg;
                    }
                }
                // Check whether a sub-result code is contained.
                if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                    int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                    // Process the sub-result code.
                    text = "[" + text + "]" + subErrorCode;
                }
                AgcUtil.reportFailure(TAG,text);
                Log.e(TAG, text);
                break;
            default:
                break;
        }
        return text;
    }


    /**
     * Search Final Entry
     *
     * @param searchContent search content
     * @param needCheck Whether duplicate check is required
     */
    private void goSearch(String searchContent, boolean needCheck, Activity mActivity) {

        /* Report search event begin */
        HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
        Bundle bundle = new Bundle();

        bundle.putString(SEARCHKEYWORDS, searchContent);
        instance.onEvent(SEARCH, bundle);
        /* Report search event end */

        Intent intent = new Intent(mActivity, SearchResultActivity.class);
        intent.putExtra(KeyConstants.SEARCH_CONTENT, searchContent);
        mActivity.startActivityForResult(intent, SEARCH_RESULT);
    }

}
