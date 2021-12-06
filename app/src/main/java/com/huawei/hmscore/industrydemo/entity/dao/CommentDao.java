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
import androidx.room.Query;

import com.huawei.hmscore.industrydemo.entity.Comment;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Dao
public interface CommentDao {

    /**
     * Query all comment list.
     *
     * @return comment list of product
     */
    @Query("SELECT * FROM comment")
    List<Comment> queryAll();

    /**
     * Insert an comment.
     *
     * @param data comment
     */
    @Insert
    void addComment(Comment data);



    @Query("SELECT * FROM comment WHERE (restId=:restId)")
    List<Comment> queryByRest( int restId);
}
