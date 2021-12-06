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
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.dao.CouponDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * Coupon Repository
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/9]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CouponRepository {

    private final CouponDao couponDao;

    private final AppDatabase database;

    public CouponRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.couponDao = database.couponDao();
    }

    public List<Coupon> queryAll() {
        return couponDao.queryAll();
    }

    /**
     * deleteAll
     */
    public void deleteAll() {
        couponDao.deleteAll();
    }

    public List<Coupon> queryByUserId(String userId) {
        return couponDao.queryByUserId(userId);
    }

    public List<Coupon> queryByUserAndRest(String openId, int restId) {
        return couponDao.queryByUserAndRest(openId, restId);
    }

    public List<Coupon> queryByUserAndRestAndCondition(String openId, int restId, int condition) {
        return couponDao.queryByUserAndRestAndCondition(openId, restId, condition);
    }

    public List<Coupon> queryByUserAndRestAndStatus(String openId, int restId, boolean status) {
        return couponDao.queryByUserAndRestAndStatus(openId, restId, status);
    }

    public void update(Coupon coupon) {
        couponDao.update(coupon);
    }

    public void insert(Coupon coupon) {
        couponDao.insert(coupon);
    }

    public void insert(List<Coupon> couponList) {
        couponDao.insert(couponList);
    }
}
