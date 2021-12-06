/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hmscore.industrydemo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NavigationUtils {

    public static final String BAIDU_MAP = "com.baidu.BaiduMap";

    public static final String GOOGLE_MAP = "com.google.android.apps.maps";

    public static final String ALIBABA_MAP = "com.autonavi.minimap";

    public static final String PETAL_MAP = "com.huawei.maps.app";

    public static final String BAIDU_MARKET = "market://details?id=com.baidu.BaiduMap";

    public static final String PETAL_MAP_MARKET = "appmarket://details?id=com.huawei.maps.app";


    public static void baiduNavagition(Context context, String city, String lat, String lon, String address) {
        try {
            Intent intent = Intent.getIntent("intent://map/direction?" +
                    "destination=latlng:" + lat + "," + lon + "|name:" + address +
                    "&mode=driving&" +
                    "region=" + city +
                    "&src=便携生活#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            Log.e("intent", e.getMessage());
        }
    }

    public static void gaodeNavagition(Context context, String lat, String lon, String poiName, String packageName) {
        try {
            Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=" + packageName + "&poiname=" + poiName + "&lat=" + lat + "&lon=" + lon + "&dev=0");
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public static void googleNavigition(Context context, String lat, String lon) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static boolean isAvailable(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        List<String> packageInfos = new ArrayList<>();
        if (installedPackages != null) {
            for (int i = 0; i < installedPackages.size(); i++) {
                String pckName = installedPackages.get(i).packageName;
                packageInfos.add(pckName);
            }
        }
        Log.i("isAvailable", packageInfos.contains(packageName) + "");
        return packageInfos.contains(packageName);
    }
}
