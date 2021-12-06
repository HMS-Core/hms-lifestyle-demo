/*
    Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.

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

package com.huawei.hmscore.industrydemo.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hmscore.industrydemo.entity.converter.AuthAccountConverter;
import com.huawei.hmscore.industrydemo.entity.converter.StringsConverter;

import static com.huawei.hmscore.industrydemo.constants.Constants.EMPTY;

/**
 * User Entity
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Entity
@TypeConverters({AuthAccountConverter.class, StringsConverter.class})
public class User {
    @PrimaryKey
    @NonNull
    private String openId = EMPTY;

    private AuthAccount huaweiAccount;

    private boolean isMember;

    private boolean isAutoRenewing;

    private long expirationDate;

    private boolean privacyFlag = false;

    private String[] recentSearchList;

    @NonNull
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(@NonNull String openId) {
        this.openId = openId;
    }

    public AuthAccount getHuaweiAccount() {
        return huaweiAccount;
    }

    public void setHuaweiAccount(AuthAccount huaweiAccount) {
        this.openId = huaweiAccount.getOpenId();
        this.huaweiAccount = huaweiAccount;
    }

    public boolean isPrivacyFlag() {
        return privacyFlag;
    }

    public void setPrivacyFlag(boolean privacyFlag) {
        this.privacyFlag = privacyFlag;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public boolean isAutoRenewing() {
        return isAutoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        isAutoRenewing = autoRenewing;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String[] getRecentSearchList() {
        if (recentSearchList == null) {
            return new String[0];
        }
        return recentSearchList.clone();
    }

    public void setRecentSearchList(String[] recentSearchList) {
        if (recentSearchList == null) {
            return;
        }
        this.recentSearchList = recentSearchList.clone();
    }
}
