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
import com.huawei.hmscore.industrydemo.entity.Comment;
import com.huawei.hmscore.industrydemo.entity.dao.CommentDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;
import java.util.List;

public class CommentRepository {

    private CommentDao mCommentDao;

    private final AppDatabase database;

    public CommentRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.mCommentDao = database.commentDao();
    }

    public List<Comment> queryAll() {
        return mCommentDao.queryAll();
    }


    public List<Comment> queryByRest(int restId) {
        return mCommentDao.queryByRest(restId);
    }
}
