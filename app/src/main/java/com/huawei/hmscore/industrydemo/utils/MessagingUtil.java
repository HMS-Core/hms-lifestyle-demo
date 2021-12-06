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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.RequestBody;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.constants.MessageConstants;
import com.huawei.hmscore.industrydemo.entity.Message;
import com.huawei.hmscore.industrydemo.repository.AppConfigRepository;
import com.huawei.hmscore.industrydemo.repository.MessageRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.agc.RemoteConfigUtil;
import com.huawei.hmscore.industrydemo.utils.hms.AnalyticsUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.PUSH_TOKEN;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.CHAR_SET;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.DATE_PATTERN;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.GETAT_CONTENTTYPE;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.NOTIFICATION_BODY;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.NOTIFICATION_TITLE;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.PATTERN;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.REQUESTMETHOD;
import static com.huawei.hmscore.industrydemo.constants.MessageConstants.TIME;

public class MessagingUtil {
    private static String appSecret;
    private static String msgJson;
    private static final String TAG = MessagingUtil.class.getSimpleName();
    private static final String ICON = "/mipmap/";

    public static void saveNotificationMessage(Context context, String messageTitle, String messageBody, int restID, String restLogo) {
        saveNotificationMessage(context, MessageConstants.COLLECTION_INTENT, messageTitle, messageBody, restID, restLogo);
    }

    public static void saveNotificationMessage(Context context, String intent, String messageTitle, String messageBody, int restID, String restLogo) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (!getNotificationSetting(KeyConstants.NOTIFICATION)) {
                    Log.d(TAG, "getNotificationSetting");
                    return;
                }
                String appAT = getAppAT(context);
                if (!TextUtils.isEmpty(appAT)) {
                    Log.e(TAG, "getNotificationSetting" + appAT);
                    String msgIntent = String.format(Locale.ROOT, intent, messageTitle, restID);
                    String msgContent = getMsgContent(context, NOTIFICATION_TITLE, NOTIFICATION_BODY, messageTitle,
                            messageBody, "", ICON + restLogo, msgIntent);
                    saveToDB(messageTitle, messageBody, restID, restLogo);
                    String response = sendNotificationMessage(context, appAT, msgContent);
                    Log.e("MessagingUtil", "response:" + response);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }

    private static void saveToDB(String messageTitle, String messageBody, int restID, String restLogo) {
        SimpleDateFormat dateFormat;
        Date date1 = new Date(System.currentTimeMillis());
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        Message messageInfo = new Message();
        messageInfo.setDate(dateFormat.format(date1));
        messageInfo.setMessageBody(messageBody);
        messageInfo.setUnRead(true);
        messageInfo.setMessageTitle(new RestaurantRepository().queryByNumber(restID).getRestname());
        messageInfo.setSenderId(restID);
        messageInfo.setSenderLogo(restLogo);
        MessageRepository messageRepository = new MessageRepository();
        messageRepository.insert(messageInfo);
    }

    private static boolean getNotificationSetting(String key) {
        if (appSecret == null) {
            Log.e(TAG, "appSecret == null");
            refreshAppSecret();
            return false;
        }
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        return appConfigRepository.getBooleanValue(key, true);
    }

    private static String getMsgContent(Context context, String titleloc, String bodyloc, String titleArg1, String bodyArg1,
                                        String bodyArg2, String icon, String intent) {
        if (null == msgJson) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.message);
            msgJson = JsonUtils.getJson(inputStream);
        }
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String pushToken = appConfigRepository.getStringValue(PUSH_TOKEN);
        return String.format(Locale.ROOT, msgJson, titleArg1, bodyArg1, titleloc, bodyloc, titleArg1, bodyArg1, bodyArg2, icon, intent, pushToken);
    }


    public static String getAppAT(Context context) throws Exception {
        refreshAppSecret();
        String appId = AgcUtil.getAppId(context);
        String grant_type = "client_credentials";
        String msgBody = MessageFormat.format(PATTERN, grant_type, URLEncoder.encode(appSecret, CHAR_SET), appId);
        String response = httpPost(MessageConstants.GET_ACCESS_TOKEN_API, GETAT_CONTENTTYPE, msgBody, TIME, TIME, null);
        Log.d("MessagingUtil", "response:" + response);
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("access_token");
    }


    public static String httpPost(String httpUrl, String contentType, String data, int connectTimeout, int readTimeout, Map<String, String> headers) throws IOException {
        OutputStream output = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUESTMETHOD);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", contentType);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            output = urlConnection.getOutputStream();
            output.write(data.getBytes(CHAR_SET));
            output.flush();
            if (urlConnection.getResponseCode() < 400) {

                in = urlConnection.getInputStream();
            } else {
                in = urlConnection.getErrorStream();
            }
            inputStreamReader = new InputStreamReader(in, CHAR_SET);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder strBuf = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                strBuf.append(str);
            }
            return strBuf.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (output != null) {
                output.close();
            }
        }
    }


    private static String sendNotificationMessage(Context context, String accessToken, String msgContent) {
        Log.d(TAG, msgContent);
        String sendApi = MessageConstants.SEND_API_PRE + AgcUtil.getAppId(context) + MessageConstants.SEND_API_POST;
        HttpClient httpClient = new HttpClient.Builder().readTimeout(TIME).connectTimeout(TIME).build();
        Request.Builder requestBuilder = httpClient.newRequest().url(sendApi).method(REQUESTMETHOD);
        requestBuilder.addHeader(MessageConstants.AUTHORIZATION, accessToken);
        requestBuilder.requestBody(new RequestBody() {
            @Override
            public String contentType() {
                return MessageConstants.CONTENT_TYPE;
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                outputStream.write(msgContent.getBytes(Charset.defaultCharset()));
                outputStream.flush();
            }
        });

        try {
            Response<ResponseBody> response = httpClient.newSubmit(requestBuilder.build()).execute();
            if (response.getCode() == MessageConstants.STATUS_OK) {
                InputStream is = response.getBody().getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                return new String(message.toByteArray(), Charset.defaultCharset());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * refresh AppSecret from remote config
     */
    public static void refreshAppSecret() {
        AGConnectConfig config = AGConnectConfig.getInstance();
        if (config == null) {
            Log.e(TAG, "config is null!");
            return;
        }
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey("App_Secret")) {
            appSecret = config.getValueAsString("App_Secret");
        } else {
            Log.e(TAG, "App_Secret is null!");
            RemoteConfigUtil.fetch();
        }
    }

    /**
     * refresh local push token
     *
     * @param context Context
     * @param token   push tokena
     */
    public static void refreshedToken(Context context, String token) {
        Log.i(TAG, "sending token to local. token: " + token);
        HmsMessaging.getInstance(context).setAutoInitEnabled(false);
        MessagingUtil.refreshAppSecret();
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        if (!token.equals(appConfigRepository.getStringValue(PUSH_TOKEN))) {
            appConfigRepository.setStringValue(PUSH_TOKEN, token);
            AnalyticsUtil.getInstance(context).setPushToken(token);
        }
    }
}
