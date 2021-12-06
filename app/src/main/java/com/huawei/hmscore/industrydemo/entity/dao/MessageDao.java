/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import androidx.room.Update;

import com.huawei.hmscore.industrydemo.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    /**
     * Query all Message.
     *
     * @return Message list
     */
    @Query("SELECT * FROM Message")
    List<Message> queryAll();

    /**
     * Query a Message based on number.
     *
     * @param number Message number
     * @return Message
     */
    @Query("SELECT * FROM Message WHERE (messageId=:number)")
    Message queryByMessageID(int number);


    /**
     * Query a Image based on number.
     *
     * @param number Message number
     * @return Image
     */
    @Query("SELECT * FROM Message WHERE (senderId=:number)")
    List<Message> queryBySenderID(int number);

    /**
     * Insert a Message.
     *
     * @param
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    /**
     * Insert  Messages.
     *
     * @param
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Message> messages);

    /**
     * deleteAll
     */
    @Query("DELETE FROM Message")
    void deleteAll();

    /**
     * update
     */
    @Query("UPDATE Message SET  unRead =:unRead WHERE messageId=:number")
    void update(boolean unRead, int number);



    @Update
    void update(Message messageInfo);

    /**
     * Query a Message based on number.
     *
     * @param
     * @return Image
     */
    @Query("SELECT * FROM Message group by senderId order by date DESC")
    List<Message> queryAllExcludeRepeats();

    /**
     * Query
     */
    @Query("SELECT COUNT(*)  FROM Message WHERE senderId=:number AND unRead =1")
    int queryUnreadAll(int number);


    /**
     * Query
     */
    @Query("SELECT * FROM Message WHERE senderId=:number order by date DESC")
    List<Message> queryAllBySenderID(int number);
}
