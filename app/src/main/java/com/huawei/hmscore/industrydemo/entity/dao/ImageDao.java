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

import com.huawei.hmscore.industrydemo.entity.Image;

import java.util.List;

/**
 * Coupon Dao
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/11/03]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Dao
public interface ImageDao {
    @Query("SELECT * FROM Image WHERE (restId=:restId AND imgType=:imgType)")
    List<Image> queryBuyRestIdAndImgType(int restId, int imgType);

    @Query("SELECT * FROM Image WHERE (imgId=:imgId AND imgType=:imgType)")
    Image queryByImgId(int imgId, int imgType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Image> imageList);

    @Query("DELETE FROM Image")
    void deleteAll();

    @Query("SELECT * FROM Image WHERE (restId=:restId)")
    List<Image> queryAllByRestId(int restId);

}
