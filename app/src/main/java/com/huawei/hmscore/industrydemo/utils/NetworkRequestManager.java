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

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hms.maps.model.LatLng;

import android.util.Log;

import okhttp3.Response;

/**
 * NetworkRequestManager
 */
public class NetworkRequestManager {
    private static final String TAG = "NetworkRequestManager";

    // Maximum number of retries.
    private static final int MAX_TIMES = 10;

    public NetworkRequestManager() {
    }

    /**
     * @param latLng1  origin latitude and longitude
     * @param latLng2  destination latitude and longitude
     * @param listener network listener
     */
    public static void getWalkingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2, final OnNetworkListener listener) {
        getWalkingRoutePlanningResult(latLng1, latLng2, listener, 0, false);
    }

    /**
     * @param latLng1    origin latitude and longitude
     * @param latLng2    destination latitude and longitude
     * @param listener   network listener
     * @param count      last number of retries
     * @param needEncode dose the api key need to be encoded
     */
    private static void getWalkingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2, final OnNetworkListener listener, int count, final boolean needEncode) {
        final int curCount = ++count;
        Log.e(TAG, "current count: " + curCount);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response =
                        NetClient.getNetClient().getWalkingRoutePlanningResult(latLng1, latLng2);
                String result = "";
                String returnCode = "";
                String returnDesc = "";


                if (response == null) {
                    returnDesc = "Request Fail! response =null";
                    if (listener != null) {
                        listener.requestFail(returnDesc);
                    }
                    return;
                }
                if (response.body() != null) {
                    try {
                        result = response.body().string();
                        Log.d(TAG, "result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        returnCode = jsonObject.optString("returnCode");
                        returnDesc = jsonObject.optString("returnDesc");
                        if (returnCode.equals("0")) {
                            if (listener != null) {
                                listener.requestSuccess(result);
                            }
                        }
                    } catch (NullPointerException e) {
                        returnDesc = "Request Fail!";
                    } catch (IOException e) {
                        returnDesc = "Request Fail!";
                    } catch (JSONException e) {
                        returnDesc = "Request Fail! JSONException ";
                        Log.e(TAG, e.getMessage());
                    }
                }
                if (listener != null) {
                    listener.requestFail(returnDesc);
                }
                response.close();
            }

        }).start();
    }


    public interface OnNetworkListener {
        void requestSuccess(String result);

        void requestFail(String errorMsg);
    }
}
