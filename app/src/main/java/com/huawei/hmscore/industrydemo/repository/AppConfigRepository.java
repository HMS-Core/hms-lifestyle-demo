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

package com.huawei.hmscore.industrydemo.repository;

import com.huawei.hmscore.industrydemo.AppDatabase;
import com.huawei.hmscore.industrydemo.entity.AppConfig;
import com.huawei.hmscore.industrydemo.entity.dao.AppConfigDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

/**
 * App Config Repository
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class AppConfigRepository {
    private final AppConfigDao appConfigDao;

    private final AppDatabase database;

    public AppConfigRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.appConfigDao = database.appConfigDao();
    }

    /**
     * @param keyword Keyword
     * @return Value of the String type
     */
    public String getStringValue(String keyword) {
        return appConfigDao.getValue(keyword);
    }

    /**
     * @param keyword Keyword
     * @param value Value
     */
    public void setStringValue(String keyword, String value) {
        appConfigDao.addValue(new AppConfig(keyword, value));
    }

    /**
     * @param keyword Keyword
     * @param defValue Default value
     * @return Value of the boolean type
     */
    public boolean getBooleanValue(String keyword, boolean defValue) {
        String value = appConfigDao.getValue(keyword);
        return (value == null) ? defValue : Boolean.parseBoolean(value);
    }

    /**
     * @param keyword Keyword
     * @param value Value
     */
    public void setBooleanValue(String keyword, boolean value) {
        appConfigDao.addValue(new AppConfig(keyword, String.valueOf(value)));
    }
}
