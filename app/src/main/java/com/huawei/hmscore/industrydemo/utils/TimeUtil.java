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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/25]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class TimeUtil {
    /**
     * Ms to second
     */
    public static final int MS_TO_SECOND = 1000;

    /**
     * Second to minute
     */
    private static final int SECOND_TO_MINUTE = 60;

    /**
     * Second to hour
     */
    private static final int SECOND_TO_HOUR = 60 * 60;

    public static final int DELIVERY_TIME = 1;

    /**
     * ms to 00:00:00
     *
     * @param time ms
     * @return String
     */
    public static String formatLongToTimeStr(int time) {
        int totalSeconds = time / MS_TO_SECOND;
        int seconds = totalSeconds % SECOND_TO_MINUTE;
        int minutes = totalSeconds / SECOND_TO_MINUTE;
        int hours = totalSeconds / SECOND_TO_HOUR;

        if (hours > 0) {
            minutes %= SECOND_TO_MINUTE;
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        }
    }


    /**
     * formatLongToMDGMTimeStr
     * @param time  long
     * @return time
     */
    public static String formatLongToMDGMTimeStr(long time) {
        String pat1 = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);  
        return sdf1.format(time);
    }

    /**
     * getCurrentTimeLong
     * @return time long
     */
    public static long getCurrentTimeLong() {
        return new Date().getTime();
    }

    public static long getTimeLongPlusMin(long time, int min) {
        return time + 60 * 1000 * min;
    }


    public static long getTimeLongPlusMinSend(long time) {
        return getTimeLongPlusMin(time, DELIVERY_TIME);
    }
}
