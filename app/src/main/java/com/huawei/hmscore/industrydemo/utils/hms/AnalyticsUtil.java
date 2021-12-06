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

package com.huawei.hmscore.industrydemo.utils.hms;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.type.HAEventType;
import com.huawei.hms.analytics.type.HAParamType;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class AnalyticsUtil {
    private static final String TAG = AnalyticsUtil.class.getSimpleName();

    private static final String FUNCTION_PRESENTATION = "FunctionPresentation";

    private static final String FUNCTION_NAME = "function_name";

    /*
     * Voice Search
     */
    private static final String VOICE_SEARCH = "voice_search";

    /*
     * Video playback (The statistics are collected after the video is played for 4s.)
     */
    private static final String VIDEO_PLAYBACK = "video_playback";

    /*
     * SUPER RESOLUTION
     */
    private static final String SUPER_RESOLUTION = "super_resolution";

    /*
     * Text Translation
     */
    private static final String TEXT_TRANSLATION = "text_translation";

    /*
     * Click each link on the contact page.
     */
    private static final String CONTENT_TYPE_LINK = "link";

    private static final String LINK_NAME = "link_name";

    /*
     * News tts read
     */
    private static final String NEWS_TTS = "news_tts";

    private static volatile HiAnalyticsInstance instance;

    public static HiAnalyticsInstance getInstance(Context context) {
        if (instance == null) {
            synchronized (AnalyticsUtil.class) {
                if (instance == null) {
                    instance = HiAnalytics.getInstance(context);
                }
            }
        }
        return instance;
    }

    public static void voiceSearch() {
        functionReport(VOICE_SEARCH);
    }

    public static void functionReport(String functionName) {
        Bundle bundle = new Bundle();
        bundle.putString(FUNCTION_NAME, functionName);
        instance.onEvent(FUNCTION_PRESENTATION, bundle);
    }

    public static void videoPlaybackReport() {
        functionReport(VIDEO_PLAYBACK);
    }

    public static void superResultionReport() {
        functionReport(SUPER_RESOLUTION);
    }

    public static void newsReadReport() {
        functionReport(NEWS_TTS);
    }

    public static void textTranslationReport() {
        functionReport(TEXT_TRANSLATION);
    }

    public static void viewContentReport(String linkName) {
        Bundle bundle = new Bundle();
        bundle.putString(HAParamType.CONTENTTYPE, CONTENT_TYPE_LINK);
        bundle.putString(LINK_NAME, linkName);
        instance.onEvent(HAEventType.VIEWCONTENT, bundle);
    }
}
