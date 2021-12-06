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

import android.util.Log;

import com.huawei.hmscore.industrydemo.AppDatabase;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.OrderItem;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.dao.OrderDao;
import com.huawei.hmscore.industrydemo.entity.dao.OrderItemDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/11]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderRepository {
    private static final String TAG = OrderRepository.class.getSimpleName();

    private final OrderDao orderDao;

    private final OrderItemDao orderItemDao;

    private final AppDatabase database;

    public OrderRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.orderDao = database.orderDao();
        this.orderItemDao = database.orderItemDao();
    }

    /**
     * Queries orders by orderId.
     * 
     * @param orderId orderId
     * @return Value of the String type
     */
    public Order queryByOrderI(int orderId) {
        return orderDao.queryByOrderId(orderId);
    }

    /**
     * Query orders by user.
     * 
     * @param user User
     * @return List<Order>
     */
    public List<Order> queryByUser(User user) {
        return orderDao.queryByOpenId(user.getOpenId());
    }

    /**
     * queryAll orders
     * @return all orders
     */
    public List<Order> queryAll() {
        return orderDao.queryAll();
    }

    public List<Order> queryByUserAndStatus(User user, int status) {
        return orderDao.queryByOpenIdAndStatus(user.getOpenId(), status);
    }

    public void insert(Order order, List<OrderItem> orderItemList) {
        orderDao.insert(order);
        orderItemDao.insert(orderItemList);
    }

    public void update(Order order) {
        if (order.getOpenId() == null) {
            Log.e(TAG, "OpenId is null!");
            return;
        }
        orderDao.insert(order);
    }

    /**
     * deleteById
     * @param id  orderId
     */
    public void deleteById(int id) {
        orderDao.deleteById(id);
    }

    public List<OrderItem> queryItemByOrder(Order order) {
        return orderItemDao.queryByOrderId(order.getOrderId());
    }
}
