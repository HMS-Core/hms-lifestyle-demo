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

import com.huawei.hmscore.industrydemo.entity.Food;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Dao
public interface FoodDao {
    /**
     * Query all foods
     * 
     * @return Food list
     */
    @Query("SELECT * FROM Food")
    List<Food> queryAll();

    /**
     * Query food list based on restId.
     * 
     * @param restId
     * @return Food list
     */
    @Query("SELECT * FROM Food WHERE (restId=:restId)")
    List<Food> queryByRestId(int restId);

    @Query("SELECT * FROM Food WHERE (foodId=:foodId)")
    Food queryByFoodId(int foodId);

    /**
     * Insert a food.
     * 
     * @param food
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Food food);

    /**
     * deleteAll
     */
    @Query("DELETE FROM Food")
    void deleteAll();
}
