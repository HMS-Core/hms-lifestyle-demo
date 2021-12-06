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

import com.huawei.hmscore.industrydemo.entity.Card;

import java.util.List;

/**
 * Card Dao
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/9]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Dao
public interface CardDao {
    /**
     * Query all Cards.
     *
     * @return Card list
     */
    @Query("SELECT * FROM Card")
    List<Card> queryAll();

    /**
     * Query Cards by UserId.
     *
     * @return Card list
     */
    @Query("SELECT * FROM Card WHERE (openId=:userId)")
    List<Card> queryByUserId(String userId);

    /**
     * Query Cards by CardId.
     *
     * @return Card list
     */
    @Query("SELECT * FROM Card WHERE (cardId=:cardId)")
    Card queryByCardId(Integer cardId);

    /**
     * deleteAll
     */
    @Query("DELETE FROM Card")
    void deleteAll();

    /**
     * Insert an Card.
     *
     * @param card Card
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Card card);

    @Query("SELECT * FROM Card WHERE (openId=:openId)")
    List<Card> queryByUser(String openId);

    @Query("SELECT * FROM Card WHERE (openId=:openId AND restId=:restId)")
    List<Card> queryByUserAndRest(String openId, int restId);
}
