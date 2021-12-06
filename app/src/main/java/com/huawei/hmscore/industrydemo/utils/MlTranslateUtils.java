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
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.translate.MLTranslateLanguage;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.inteface.OnTranslateCallBack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MlTranslateUtils {

    private MlTranslateUtils() {
    }

    private MLRemoteTranslator mRemoteTranslator;
    public static MlTranslateUtils mMessagingUtil = new MlTranslateUtils();

    public static MlTranslateUtils getInstance() {
        return mMessagingUtil;
    }

    public void initRemoteTranslateSetting(String targetLanguageCode) {
        MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting
                .Factory()
                .setTargetLangCode(targetLanguageCode)
                .create();
        mRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
    }

    ;

    public void translateSourceText(String sourceText, OnTranslateCallBack translateCallBack) {
        final Task<String> task = mRemoteTranslator.asyncTranslate(sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                Log.i("apiKey", text);
                translateCallBack.translate(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                try {
                    MLException mlException = (MLException) e;
                    int errorCode = mlException.getErrCode();
                    Log.i("MlTranslateUtils", errorCode + "");
                    String errorMessage = mlException.getMessage();
                    translateCallBack.onError(errorMessage);
                    Log.i("MlTranslateUtils", errorMessage);

                } catch (Exception error) {
                    // 转换错误处理。
                    Log.i("MlTranslateUtils", error.getMessage());
                }
            }
        });
    }

    public void translateSourceText(String sourceText) {
        final Task<String> task = mRemoteTranslator.asyncTranslate(sourceText);

        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                // 识别成功的处理逻辑。
                Log.i("apiKey", text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                try {
                    MLException mlException = (MLException) e;
                    int errorCode = mlException.getErrCode();
                    String errorMessage = mlException.getMessage();
                    Log.i("MlTranslateUtils", errorMessage);
                    Log.i("MlTranslateUtils", errorCode + "");
                } catch (Exception error) {
                    // 转换错误处理。
                    Log.i("MlTranslateUtils", error.getMessage());
                }
            }
        });
    }

    public void getSupportLanguage() {
        MLTranslateLanguage.getCloudAllLanguages().addOnSuccessListener(
                new OnSuccessListener<Set<String>>() {
                    @Override
                    public void onSuccess(Set<String> result) {
                        // 成功获取在线翻译所支持的语种。
                        for (int i = 0; i < result.size(); i++) {
                            Iterator<String> iterator = result.iterator();
                            while (iterator.hasNext()) {
                                String next = iterator.next();
                                Log.i("RestaurantDetailViewModel", next);
                            }
                        }
                    }
                });
    }

    public void closeRemoteTranslate() {
        if (mRemoteTranslator != null) {
            mRemoteTranslator.stop();
        }
    }

    public static List<String> getCountryName(Context context){
        String[] languageName = context.getResources().getStringArray(R.array.language_name);
        List<String> langLists = new ArrayList<>();
        for (String str : languageName
        ) {
            langLists.add(str);
        }
        return langLists;
    }

}
