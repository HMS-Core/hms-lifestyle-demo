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

package com.huawei.hmscore.industrydemo.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public interface Constants {
    int OFF_LOAD_PAGE_COUNT = 1;

    int REQUEST_NEWS_SIZE = 10;

    String RESOURCE_TYPE_MIPMAP = "mipmap";

    String LANGUAGE_EN = "en";

    String LANGUAGE_ZH = "zh";

    String EMPTY = "";

    String COMMA = ",";

    String DOT = ".";

    String CNY = "CNY";

    int CARD_INDEX = 0;

    int COUPON_INDEX = 1;

    int NOT_PAID = 0;
    int HAVE_PAID = 1;
    int DELIVERED = 2;
    int COMPLETED = 3;
    int COMMENTED = 4;
    int ORDER_WAIT_PAY = 0;
    int ORDER_WAIT_SEND = 1;
    int ORDER_HAVE_SENT = 2;
    int ORDER_HAVE_ENSURE = 3;
    int ORDER_HAVE_COMMENT = 4;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ORDER_WAIT_PAY,ORDER_WAIT_SEND,ORDER_HAVE_SENT, ORDER_HAVE_ENSURE, ORDER_HAVE_COMMENT})
    @interface OrderStatus {
    }

    int PACKING_CHARGES = 1;

    int DELIVERY_CHARGES = 3;

    int CAMERA_PERMISSION_CODE = 1;

    int CAMERA_RECORD_AUDIO_CODE = 6;

    int WRITE_EXTERNAL_STORAGE_CODE = 2;

    int PLAYING = 1;

    int DELAY_MILLIS_500 = 500;

    int DETECTING = 0;

    int IS_INTEGRITY = 1;

    int IS_NOT_INTEGRITY = 2;

    int COUPON_CONDITION1 = 10;

    int COUPON_CONDITION2 = 30;

    int COUPON_CONDITION3 = 50;

    int COUPON_DISCOUNT1 = 1;

    int COUPON_DISCOUNT2 = 5;

    int COUPON_DISCOUNT3 = 10;

    String ERROR_ZERO_RESULTS = "010004";

    int ADDRESS_RESULT_CODE = 1000;

    String SOF_LINK = "https://stackoverflow.com/questions/tagged/huawei-mobile-services?tab=Newest&ha_source=hms7";

}
