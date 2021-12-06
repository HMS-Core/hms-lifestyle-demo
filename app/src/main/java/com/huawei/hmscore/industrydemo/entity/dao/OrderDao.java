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

import java.util.List;

import com.huawei.hmscore.industrydemo.entity.Order;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/11]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Dao
public interface OrderDao {
    /**
     * Query order list based on orderId.
     *
     * @param orderId
     * @return Order list
     */
    @Query("SELECT * FROM `Order` WHERE (orderId=:orderId)")
    Order queryByOrderId(int orderId);

    /**
     * Query order list based on openId.
     *
     * @param openId
     * @return Order
     */
    @Query("SELECT * FROM `Order` WHERE (openId=:openId)")
    List<Order> queryByOpenId(String openId);

    /**
     * queryAll orders
     * @return all orders
     */
    @Query("SELECT * FROM `Order` ")
    List<Order> queryAll();

    /**
     * Query order list based on openId and status.
     * 
     * @param openId
     * @param status
     * @return Order list
     */
    @Query("SELECT * FROM `Order` WHERE (openId=:openId AND status=:status)")
    List<Order> queryByOpenIdAndStatus(String openId, int status);


    /**
     * deleteById
     * @param id  orderId
     */
    @Query("DELETE FROM `Order` WHERE (orderId=:id)")
    void deleteById(int id);

    /**
     * Insert an Order.
     * 
     * @param order
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);
}
