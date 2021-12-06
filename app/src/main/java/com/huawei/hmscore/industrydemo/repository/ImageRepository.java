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
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.entity.dao.ImageDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * Coupon Repository
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/11/03]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class ImageRepository {
    private final ImageDao imageDao;

    private final AppDatabase database;

    public ImageRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.imageDao = database.imageDao();
    }

    public List<Image> queryBuyRestIdAndImgType(int restId, int imgType) {
        return imageDao.queryBuyRestIdAndImgType(restId, imgType);
    }

    public List<Image> queryAllByRestId(int restId) {
        return imageDao.queryAllByRestId(restId);
    }

    public Image queryByImgId(int imgId, int imgType) {
        return imageDao.queryByImgId(imgId, imgType);
    }

    public void insert(List<Image> imageList) {
        imageDao.insert(imageList);
    }

    public void deleteAll(){
        imageDao.deleteAll();
    }
}
