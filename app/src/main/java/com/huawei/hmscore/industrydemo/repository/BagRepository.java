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
import com.huawei.hmscore.industrydemo.entity.Bag;
import com.huawei.hmscore.industrydemo.entity.dao.BagDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/14]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class BagRepository {
    private final AppDatabase database;

    private final BagDao bagDao;

    public BagRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.bagDao = database.bagDao();
    }

    public List<Bag> queryByRestId(int restId) {
        return bagDao.queryByRestId(restId);
    }

    public Bag queryByFoodId(int foodId) {
        return bagDao.queryByFoodId(foodId);
    }

    public void insert(Bag bag) {
        bagDao.insert(bag);
    }

    public void insert(List<Bag> bagList) {
        bagDao.insert(bagList);
    }

    public void deleteByRestId(int restId) {
        bagDao.deleteByRestId(restId);
    }

    public void delete(Bag bag) {
        bagDao.delete(bag);
    }

    public void update(Bag bag) {
        bagDao.update(bag);
    }
}
