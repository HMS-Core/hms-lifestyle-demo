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
import com.huawei.hmscore.industrydemo.entity.Message;
import com.huawei.hmscore.industrydemo.entity.dao.MessageDao;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;

import java.util.List;

/**
 * App Config Repository
 *
 * @version [HMSCore-Demo 1.0.2.300, 2021/3/22]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 1.0.2.300]
 */
public class MessageRepository {
    private final MessageDao mMessageDao;
    private final AppDatabase database;

    public MessageRepository() {
        this.database = DatabaseUtil.getDatabase();
        this.mMessageDao = database.messageDao();
    }

    public List<Message> queryAll() {
        return mMessageDao.queryAll();
    }

    public void deleteAll() {
        mMessageDao.deleteAll();
    }

    public Message queryByMessageID(int number) {
        return mMessageDao.queryByMessageID(number);
    }


    public void insert(Message messageInfo) {
        mMessageDao.insert(messageInfo);
    }


    public void insert(List<Message> messageInfo) {
        mMessageDao.insert(messageInfo);
    }

    public List<Message> queryAllExcludeRepeat() {
        return mMessageDao.queryAllExcludeRepeats();
    }

    public void updateRedTips(boolean isRead, int messageid) {
        mMessageDao.update(isRead, messageid);
    }

    public void updateRedTips(Message message) {
        mMessageDao.update(message);
    }

    public int queryUnreadAll(int senderId) {
        return mMessageDao.queryUnreadAll(senderId);
    }

    public List<Message> queryAllBySenderID(int senderId) {
        return mMessageDao.queryAllBySenderID(senderId);
    }
}
