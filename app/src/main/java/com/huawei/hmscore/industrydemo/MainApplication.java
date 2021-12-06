/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021 All rights reserved.
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

package com.huawei.hmscore.industrydemo;

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.LAST_LANGUAGE;
import static com.huawei.hmscore.industrydemo.constants.LogConfig.TAG;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.opendevice.OpenDevice;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.repository.AppConfigRepository;
import com.huawei.hmscore.industrydemo.repository.FoodRepository;
import com.huawei.hmscore.industrydemo.repository.ImageRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;
import com.huawei.hmscore.industrydemo.utils.JsonUtils;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.agc.RemoteConfigUtil;
import com.huawei.hmscore.industrydemo.utils.hms.AnalyticsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class MainApplication extends Application {
    private static MainApplication mApplication;
    private static WisePlayerFactory wisePlayerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(this);
        DatabaseUtil.init(this);
        AnalyticsUtil.getInstance(this).setAnalyticsEnabled(false);
        AGConnectInstance.initialize(this);
        if( MLApplication.getInstance() !=null){
            MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(this));
        }

        refreshProductInfo(SystemUtil.getLanguage());
        RemoteConfigUtil.init();
        initWisePlayer();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
        refreshProductInfo(SystemUtil.getLanguage());
    }

    private void refreshProductInfo(String newLanguage) {
        AppConfigRepository appConfigRepository = new AppConfigRepository();
        String lastLan = appConfigRepository.getStringValue(LAST_LANGUAGE);
        initFoodInfo();
        initImgInfo();
        initializeRestInfo();
        if (lastLan == null || !lastLan.equals(newLanguage)) {
            Log.d(TAG, "on Language Changed");
            appConfigRepository.setStringValue(LAST_LANGUAGE, newLanguage);
            return;
        }

    }

    /**
     * Read local resources and initialize preconfigured product information.
     */
    private void initializeRestInfo() {
        AssetManager assetManager = this.getAssets();
        String productFilePath = getString(R.string.product_file_path);
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        try {
            String[] productFiles = assetManager.list(productFilePath);
            if (null == productFiles) {
                return;
            }
            restaurantRepository.deleteAll();
            for (String productFile : productFiles) {
                String json = JsonUtils.getJson(this, productFilePath + File.separator + productFile);
                List<Restaurant> restaurants =
                    JsonUtils.jsonArray2Object(json, new TypeToken<ArrayList<Restaurant>>() {}.getType());
                if (null == restaurants) {
                    return;
                }
                for (Restaurant r : restaurants) {
                    restaurantRepository.insert(r);
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.data_init_fail, Toast.LENGTH_SHORT).show();
            AgcUtil.reportException(TAG, e.getMessage(), e);
        }
    }

    private void initFoodInfo() {
        AssetManager assetManager = this.getAssets();
        String foodFilePath = getString(R.string.food_file_path);
        FoodRepository foodRepository = new FoodRepository();
        try {
            String[] foodFiles = assetManager.list(foodFilePath);
            if (null == foodFiles) {
                return;
            }
            foodRepository.deleteAll();
            for (String foodFile : foodFiles) {
                String json = JsonUtils.getJson(this, foodFilePath + File.separator + foodFile);
                List<Food> foods = JsonUtils.jsonArray2Object(json, new TypeToken<ArrayList<Food>>() {}.getType());
                if (null == foods) {
                    return;
                }
                for (Food food : foods) {
                    foodRepository.insert(food);
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.data_init_fail, Toast.LENGTH_SHORT).show();
            AgcUtil.reportException(TAG, e.getMessage(), e);
        }
    }

    private void initImgInfo() {
        AssetManager assetManager = this.getAssets();
        String imgFilePath = getString(R.string.img_file_path);
        ImageRepository imageRepository = new ImageRepository();
        try {
            String[] imgFiles = assetManager.list(imgFilePath);
            if (null == imgFiles) {
                return;
            }
            imageRepository.deleteAll();
            for (String imgFile : imgFiles) {
                String json = JsonUtils.getJson(this, imgFilePath + File.separator + imgFile);
                List<Image> images = JsonUtils.jsonArray2Object(json, new TypeToken<ArrayList<Image>>() {}.getType());
                if (null == images) {
                    return;
                }
                imageRepository.insert(images);
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.data_init_fail, Toast.LENGTH_SHORT).show();
            AgcUtil.reportException(TAG, e.getMessage(), e);
        }
    }

    static class MyInitFactoryCallback implements InitFactoryCallback {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.d(TAG, "onSuccess wisePlayerFactory:" + wisePlayerFactory);
            setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            Log.d(TAG, "onFailure errorcode:" + errorCode + " reason:" + reason);
            AgcUtil.reportFailure(TAG, "onFailure errorcode:" + errorCode + " reason:" + reason);
        }
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        MainApplication.wisePlayerFactory = wisePlayerFactory;
    }

    /**
     * init video kit
     */
    public void initWisePlayer() {
        Log.d(TAG, "initWisePlayer");
        // Call the getOdid method to obtain the ODID.
        OpenDevice.getOpenDeviceClient(this).getOdid().addOnSuccessListener(odidResult -> {
            String odid = odidResult.getId();
            Log.d(TAG, "getODID successfully, the ODID is " + odid);
            // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
            WisePlayerFactoryOptionsExt factoryOptions =
                new WisePlayerFactoryOptionsExt.Builder().setDeviceId(odid).build();
            WisePlayerFactory.initFactory(this, factoryOptions, new MyInitFactoryCallback());
        }).addOnFailureListener(myException -> AgcUtil.reportException(TAG, myException));
    }

    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setContext(MainApplication application) {
        mApplication = application;
    }

    public static Context getContext() {
        return mApplication.getApplicationContext();
    }
}
