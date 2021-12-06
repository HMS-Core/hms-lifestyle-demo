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
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.entity.dao.FoodDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class FoodRepository {
    private final AppDatabase database;

    private final FoodDao foodDao;

    public FoodRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.foodDao = database.foodDao();
    }

    public List<Food> queryAll() {
        return foodDao.queryAll();
    }

    public List<Food> queryByRestId(int restId) {
        return foodDao.queryByRestId(restId);
    }

    public Food queryByFoodId(int foodId){
        return foodDao.queryByFoodId(foodId);
    }

    public void insert(Food food) {
        foodDao.insert(food);
    }

    public void deleteAll() {
        foodDao.deleteAll();
    }
}
