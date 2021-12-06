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

import com.huawei.hmscore.industrydemo.entity.OrderItem;

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
public interface OrderItemDao {
    /**
     * Query orderItem list based on orderId.
     * 
     * @param orderId
     * @return OrderItem list
     */
    @Query("SELECT * FROM OrderItem WHERE (orderId=:orderId)")
    List<OrderItem> queryByOrderId(int orderId);

    /**
     * Insert an orderItem.
     * 
     * @param orderItem
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderItem orderItem);

    /**
     * Insert an orderItem list
     * 
     * @param orderItems
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<OrderItem> orderItems);
}
