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

package com.huawei.hmscore.industrydemo.entity.converter;

import static com.huawei.hmscore.industrydemo.constants.Constants.COMMA;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class StringsConverter {
    @TypeConverter
    public static String[] revertStrings(String value) {
        if (value == null) {
            return new String[0];
        }
        return value.split(COMMA);
    }

    @Nullable
    @TypeConverter
    public static String converterStrings(String[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (String temp : value) {
            result.append(temp).append(COMMA);
        }
        return result.substring(0, result.length() - 1);
    }
}
