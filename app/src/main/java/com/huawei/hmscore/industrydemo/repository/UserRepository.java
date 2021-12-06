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

import android.text.TextUtils;

import com.huawei.hmscore.industrydemo.AppDatabase;
import com.huawei.hmscore.industrydemo.entity.AppConfig;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.dao.AppConfigDao;
import com.huawei.hmscore.industrydemo.entity.dao.UserDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import static com.huawei.hmscore.industrydemo.constants.Constants.COMMA;
import static com.huawei.hmscore.industrydemo.constants.KeyConstants.SEARCH_CONTENT;
import static com.huawei.hmscore.industrydemo.constants.KeyConstants.USER_KEY;

import androidx.annotation.Nullable;

/**
 * @author l00447576
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class UserRepository {
    private final UserDao userDao;

    private final AppDatabase database;

    public UserRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.userDao = database.userDao();
    }

    /**
     * Obtains the current login user.
     *
     * @return User
     */
    @Nullable
    public User getCurrentUser() {
        String openId = database.appConfigDao().getValue(USER_KEY);
        if (openId == null) {
            return null;
        } else {
            return userDao.queryByOpenId(openId);
        }
    }

    /**
     * Save the current login user.
     *
     * @param user current login user
     */
    public void setCurrentUser(User user) {
        AppConfigDao appConfigDao = database.appConfigDao();
        if (user == null) {
            appConfigDao.addValue(new AppConfig(USER_KEY, null));
        } else {
            userDao.insert(user);
            appConfigDao.addValue(new AppConfig(USER_KEY, user.getOpenId()));
        }
    }

    public User queryByOpenId(String openId) {
        return userDao.queryByOpenId(openId);
    }

    public String[] getHistorySearch() {
        User user = getCurrentUser();
        if (user == null) {
            String res = database.appConfigDao().getValue(SEARCH_CONTENT);
            return (res == null || "".equals(res)) ? new String[0] : res.split(COMMA);
        }
        return user.getRecentSearchList();
    }

    public void setHistorySearch(String[] arrs) {
        User user = getCurrentUser();
        if (user == null) {
            database.appConfigDao().addValue(new AppConfig(SEARCH_CONTENT, TextUtils.join(COMMA, arrs)));
        } else {
            user.setRecentSearchList(arrs);
            setCurrentUser(user);
        }
    }

}
