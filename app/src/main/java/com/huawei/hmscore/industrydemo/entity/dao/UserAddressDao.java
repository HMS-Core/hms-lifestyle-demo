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

package com.huawei.hmscore.industrydemo.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.huawei.hmscore.industrydemo.entity.UserAddress;

import java.util.List;

/**
 * @version [HMSCore-Demo 1.0.2.300, 2021/3/17]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 1.0.2.300]
 */
@Dao
public interface UserAddressDao {
    /**
     * Query all UserAddresss.
     * 
     * @return UserAddress list
     */
    @Query("SELECT * FROM UserAddress")
    List<UserAddress> queryAll();

    /**
     * Query a UserAddress based on number.
     * 
     * @param number UserAddress number
     * @return UserAddress
     */
    @Query("SELECT * FROM UserAddress WHERE (addressId=:number)")
    UserAddress queryByNumber(int number);

    /**
     * Query all UserAddresss.
     * 
     * @param id User opendi
     * @return UserAddress list
     */
    @Query("SELECT * FROM UserAddress  WHERE (opened=:id)")
    List<UserAddress> queryByOpenid(String id);

    /**
     * queryByFlag
     * 
     * @param flag bool
     * @return UserAddress
     */
    @Query("SELECT * FROM UserAddress WHERE (defaultflag=:flag)")
    UserAddress queryByFlag(boolean flag);

    /**
     * Insert a UserAddress.
     * 
     * @param UserAddress UserAddress
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserAddress UserAddress);

    /**
     * Insert a UserAddress.
     *
     * @param UserAddress UserAddress list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<UserAddress> UserAddress);

    /**
     * deleteAll
     */
    @Query("DELETE FROM UserAddress")
    void deleteAll();

    /**
     * deleteById
     * 
     * @param id addressid
     */
    @Query("DELETE FROM UserAddress WHERE (addressId=:id)")
    void deleteById(int id);
}