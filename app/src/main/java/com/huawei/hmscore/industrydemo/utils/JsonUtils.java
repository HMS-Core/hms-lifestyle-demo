/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.hmscore.industrydemo.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON processing
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static String getJson(Context context, String filePath) {
        StringBuilder jsonString = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(assetManager.open(filePath), Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            AgcUtil.reportException(TAG, e);
        }
        return jsonString.toString();
    }

    public static String getJson(InputStream inputStream) {
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            AgcUtil.reportException(TAG, e);
        }
        return jsonString.toString();
    }

    /**
     * @param s json list string
     * @param type type list string new TypeToken<ArrayList<Restaurant>>() {}.getType()
     * @param <T> Object entity
     * @return List of object
     */
    public static <T> List<T> jsonArray2Object(String s, Type type) {
        return new Gson().fromJson(s, type);
    }

    /**
     * jsonArray2Object
     * 
     * @param jsonStr jsonStr
     * @param tClass tClass
     * @param <T> T
     * @return
     */
    public static <T> List<T> jsonArray2Object(String jsonStr, Class<T> tClass) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(jsonStr).getAsJsonArray();
        List<T> list = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            T t = new Gson().fromJson(jsonElement, tClass);
            list.add(t);
        }
        return list;
    }

}
