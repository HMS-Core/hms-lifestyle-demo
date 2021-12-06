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

package com.huawei.hmscore.industrydemo.repository;

import com.huawei.hmscore.industrydemo.AppDatabase;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.dao.RestaurantDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * App Config Repository
 *
 * @version [HMSCore-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 1.0.2.300]
 */
public class RestaurantRepository {
    private static final double MAX_SCORE = 100.0d;

    private static final double ITERATION_SCORE = 0.01d;

    private final RestaurantDao restaurantDao;

    private final AppDatabase database;

    public RestaurantRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.restaurantDao = database.restaurantDao();
    }

    public List<Restaurant> queryAll() {
        return restaurantDao.queryAll();
    }

    /**
     * deleteAll
     */
    public void deleteAll() {
        restaurantDao.deleteAll();
    }

    /**
     * Query Restaurant by Restaurant number
     *
     * @param number Restaurant number
     * @return Restaurant
     */
    public Restaurant queryByNumber(int number) {
        return restaurantDao.queryByNumber(number);
    }


    /**
     * Query Restaurant in specific category
     *
     * @param category category
     * @return Restaurant list
     */
    public List<Restaurant> queryByCategory(String category) {
        return restaurantDao.queryByCategory(category);
    }

    /**
     * Search Restaurant with key word
     *
     * @param keywords keywords
     * @return Restaurant list
     */
    public List<Restaurant> queryByKeywords(String keywords) {
        List<Restaurant> restaurantList = queryAll();
        if (null == keywords || null == restaurantList) {
            return new ArrayList<>();
        }
        Map<Double, Restaurant> RestaurantScores = new TreeMap<>(Collections.reverseOrder());
        for (Restaurant restaurant : restaurantList) {
            String matchingStr =
                restaurant.getRestname() + restaurant.getFoodtype() + restaurant.getRestname();
            int index = matchingStr.toLowerCase(Locale.getDefault()).indexOf(keywords.toLowerCase(Locale.getDefault()));
            if (index != -1) {
                double score = MAX_SCORE / (index + 1);
                while (null != RestaurantScores.get(score)) {
                    score -= ITERATION_SCORE;
                }
                RestaurantScores.put(score, restaurant);
            }
        }
        return new ArrayList<>(RestaurantScores.values());
    }

    /**
     * Insert new Restaurant
     *
     * @param restaurant Restaurant
     */
    public void insert(Restaurant restaurant) {
        restaurantDao.insert(restaurant);
    }


    /**
     * Insert new Restaurant
     *
     * @param restaurant Restaurant list
     */
    public void insert(List<Restaurant> restaurant) {
        restaurantDao.insert(restaurant);
    }
}
