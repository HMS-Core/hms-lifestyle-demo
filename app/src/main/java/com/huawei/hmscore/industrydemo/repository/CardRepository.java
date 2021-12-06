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
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.dao.CardDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Card Repository
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/9]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CardRepository {

    private final CardDao cardDao;

    private final AppDatabase database;

    public CardRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.cardDao = database.cardDao();
    }

    public List<Card> queryAll() {
        return cardDao.queryAll();
    }

    /**
     * deleteAll
     */
    public void deleteAll() {
        cardDao.deleteAll();
    }

    public List<Card> queryByUser(User mUser) {
        return cardDao.queryByUser(mUser.getOpenId());
    }

    public List<Card> queryByUserAndRest(String openId, int restId) {
        return cardDao.queryByUserAndRest(openId, restId);
    }

    public void insert(Card card) {
        cardDao.insert(card);
    }
    
    public List<Card> queryByUserId(String userId) {
        List<Card> cardList = cardDao.queryByUserId(userId);
        if (cardList == null) {
            cardList = new ArrayList<>();
        }
        return cardList;
    }
    
    public Card queryByCardId(Integer cardId) {
        return cardDao.queryByCardId(cardId);
    }
}
