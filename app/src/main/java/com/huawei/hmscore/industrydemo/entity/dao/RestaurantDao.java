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

package com.huawei.hmscore.industrydemo.entity.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.huawei.hmscore.industrydemo.entity.Restaurant;

import java.util.List;

/**
 * @version [HMSCore-Demo 1.0.2.300, 2021/3/17]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 1.0.2.300]
 */
@Dao
public interface RestaurantDao {
    /**
     * Query all Restaurants.
     * 
     * @return Restaurant list
     */
    @Query("SELECT * FROM Restaurant")
    List<Restaurant> queryAll();

    /**
     * Query a Restaurant based on number.
     * 
     * @param number Restaurant number
     * @return Restaurant
     */
    @Query("SELECT * FROM Restaurant WHERE (restid=:number)")
    Restaurant queryByNumber(int number);

    /**
     * Query Restaurant list based on and category.
     * 
     * @param category Restaurant category
     * @return Restaurant list
     */
    @Query("SELECT * FROM Restaurant WHERE (foodtype=:category)")
    List<Restaurant> queryByCategory(String category);

    /**
     * Insert a Restaurant.
     * 
     * @param restaurant Restaurant
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Restaurant restaurant);

    /**
     * Insert a Restaurant.
     *
     * @param restaurant Restaurant list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Restaurant> restaurant);

    /**
     * deleteAll
     */
    @Query("DELETE FROM Restaurant")
    void deleteAll();
}