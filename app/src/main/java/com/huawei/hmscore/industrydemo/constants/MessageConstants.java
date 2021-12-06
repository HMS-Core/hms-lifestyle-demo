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

public interface MessageConstants {
    String SEND_API_PRE = "https://push-api.cloud.huawei.com/v1/";

    String SEND_API_POST = "/messages:send";

    String GET_ACCESS_TOKEN_API = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    String COLLECTION_INTENT =
            "intent://com.huawei.hmscore.industrydemo/messageinfo?#Intent;scheme=pushscheme;launchFlags=0x4000000;S.senderName=%s;i.senderID=%d;end";

    String COLLECTION_INTENT_ORDERLIST =
        "intent://com.huawei.hmscore.industrydemo/orderList?#Intent;scheme=pushscheme;launchFlags=0x4000000;S.senderName=%s;i.senderID=%d;end";

    String SENDERID = "senderID";

    String SENDERNAME = "senderName";

    String PATTERN = "grant_type={0}&client_secret={1}&client_id={2}";

    String AUTHORIZATION = "Authorization";

    String CONTENT_TYPE = "application/json";

    String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    String NOTIFICATION_TITLE = "title_messagetitle";

    String NOTIFICATION_BODY = "title_messagebody";

    String GETAT_CONTENTTYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    String CHAR_SET = "UTF-8";

    String REQUESTMETHOD = "POST";

    int TIME = 5000;

    int STATUS_OK = 200;

    int ZERO = 0;
}