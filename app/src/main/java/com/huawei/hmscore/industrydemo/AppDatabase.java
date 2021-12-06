/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021 All rights reserved.
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

package com.huawei.hmscore.industrydemo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.huawei.hmscore.industrydemo.entity.AppConfig;
import com.huawei.hmscore.industrydemo.entity.Bag;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Comment;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.entity.Message;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.OrderItem;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.entity.dao.AppConfigDao;
import com.huawei.hmscore.industrydemo.entity.dao.BagDao;
import com.huawei.hmscore.industrydemo.entity.dao.CardDao;
import com.huawei.hmscore.industrydemo.entity.dao.CommentDao;
import com.huawei.hmscore.industrydemo.entity.dao.CouponDao;
import com.huawei.hmscore.industrydemo.entity.dao.FoodDao;
import com.huawei.hmscore.industrydemo.entity.dao.ImageDao;
import com.huawei.hmscore.industrydemo.entity.dao.MessageDao;
import com.huawei.hmscore.industrydemo.entity.dao.OrderDao;
import com.huawei.hmscore.industrydemo.entity.dao.OrderItemDao;
import com.huawei.hmscore.industrydemo.entity.dao.RestaurantDao;
import com.huawei.hmscore.industrydemo.entity.dao.UserDao;
import com.huawei.hmscore.industrydemo.entity.dao.UserAddressDao;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Database(entities = {AppConfig.class, Bag.class, Card.class, Comment.class, Coupon.class, Food.class, Restaurant.class,
    Order.class, OrderItem.class, User.class, UserAddress.class, Message.class, Image.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Get AppConfigDao
     *
     * @return AppConfigDao
     */
    public abstract AppConfigDao appConfigDao();

    /**
     * Get UserDao
     * 
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * Get RestaurantDao
     *
     * @return RestaurantDao
     */
    public abstract RestaurantDao restaurantDao();

    /**
     * Get useraddressDao
     *
     * @return useraddressDao
     */
    public abstract UserAddressDao useraddressDao();

    /**
     * Get foodDao
     *
     * @return FoodDao
     */
    public abstract FoodDao foodDao();

    /**
     * Get CardDao
     *
     * @return CardDao
     */
    public abstract CardDao cardDao();

    /**
     * Get CouponDao
     *
     * @return CouponDao
     */
    public abstract CouponDao couponDao();

    /**
     * Get OrderDao
     *
     * @return OrderDao
     */
    public abstract OrderDao orderDao();

    /**
     * Get OrderItemDao
     *
     * @return OrderItem
     */
    public abstract OrderItemDao orderItemDao();

    /**
     * Get BagDao
     *
     * @return BagDao
     */
    public abstract BagDao bagDao();

    /**
     * Get CommentDao
     *
     * @return CommentDao
     */
    public abstract CommentDao commentDao();


    /**
     * Get MessageDao
     *
     * @return MessageDao
     */
    public abstract MessageDao messageDao();

    /**
     * Get ImageDao
     *
     * @return ImageDao
     */
    public abstract ImageDao imageDao();
}
