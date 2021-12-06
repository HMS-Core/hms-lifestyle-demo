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

package com.huawei.hmscore.industrydemo.repository;

import com.huawei.hmscore.industrydemo.AppDatabase;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.entity.dao.UserAddressDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * App UserAddress Repository
 *
 * @version [HMSCore-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 1.0.2.300]
 */
public class UserAddressRepository {
    private static final double MAX_SCORE = 100.0d;

    private static final double ITERATION_SCORE = 0.01d;

    private final UserAddressDao userAddressDao;

    private final AppDatabase database;

    public UserAddressRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.userAddressDao = database.useraddressDao();
    }

    public List<UserAddress> queryById() {
        User user = new UserRepository().getCurrentUser();
        if (user != null) {
            return userAddressDao.queryByOpenid(user.getOpenId());
        }
        return userAddressDao.queryAll();
    }

    /**
     * deleteAll
     */
    public void deleteAll() {
        userAddressDao.deleteAll();
    }

    /**
     * deleteById
     * 
     * @param id addressid
     */
    public void deleteById(int id) {
        userAddressDao.deleteById(id);
    }

    /**
     * Query UserAddress by UserAddress number
     *
     * @param number UserAddress number
     * @return UserAddress
     */
    public UserAddress queryByNumber(int number) {
        return userAddressDao.queryByNumber(number);
    }

    /**
     * queryByFlag
     * 
     * @param flag bool
     * @return UserAddress
     */
    public UserAddress queryByFlag(boolean flag) {
        return userAddressDao.queryByFlag(flag);
    }

    /**
     * Insert new UserAddress
     *
     * @param useraddress UserAddress
     */
    public void insert(UserAddress useraddress) {
        userAddressDao.insert(useraddress);
    }

    /**
     * Insert new UserAddress
     *
     * @param useraddress UserAddress list
     */
    public void insert(List<UserAddress> useraddress) {
        userAddressDao.insert(useraddress);
    }
}
