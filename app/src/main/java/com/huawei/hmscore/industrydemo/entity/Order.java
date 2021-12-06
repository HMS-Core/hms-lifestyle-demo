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

package com.huawei.hmscore.industrydemo.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomWarnings;
import androidx.room.TypeConverters;

import com.huawei.hmscore.industrydemo.MainApplication;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.entity.converter.StringsConverter;
import com.huawei.hmscore.industrydemo.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/11]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Entity
@TypeConverters(StringsConverter.class)
public class Order {
    @PrimaryKey
    private int orderId;

    private int restId;

    private String openId;

    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
    @Embedded
    private UserAddress userAddress;

    private int totalPrice;

    private int actualPrice;

    private int discount1;

    private int discount2;

    private int couponId;

    private long time;

    @Constants.OrderStatus
    private int status;

    public Order() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss", Locale.ROOT);
        long time = new Date().getTime();
        this.time = time;
        String date = simpleDateFormat.format(time);
        this.orderId = Integer.parseInt(date);
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRestId() {
        return restId;
    }

    public void setRestId(int restId) {
        this.restId = restId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getDiscount1() {
        return discount1;
    }

    public void setDiscount1(int discount1) {
        this.discount1 = discount1;
    }

    public int getDiscount2() {
        return discount2;
    }

    public void setDiscount2(int discount2) {
        this.discount2 = discount2;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        if(isSendByMin() && status == Constants.HAVE_PAID ){
            setStatus(Constants.ORDER_HAVE_SENT);
        }
    }

    public boolean isSendByMin(){
        if(TimeUtil.getTimeLongPlusMinSend(getTime())<TimeUtil.getCurrentTimeLong()){
            return true;
        }
        return false;
    }


    public String getStatusText() {
        String s = MainApplication.getContext().getString(R.string.wait_send);
        switch (getStatus()) {
            case Constants.ORDER_HAVE_SENT:
                s = MainApplication.getContext().getString(R.string.have_sent);
                break;
            case Constants.ORDER_HAVE_ENSURE:
                s = MainApplication.getContext().getString(R.string.have_ensure);
                break;
            case Constants.ORDER_HAVE_COMMENT:
                s = MainApplication.getContext().getString(R.string.have_comment);
                break;
            case Constants.ORDER_WAIT_PAY:
                s = MainApplication.getContext().getString(R.string.wait_pay);
                break;
            case Constants.ORDER_WAIT_SEND:


            default:
                if(isSendByMin()){
                    s = MainApplication.getContext().getString(R.string.have_sent);
                    setStatus(Constants.ORDER_HAVE_SENT);
                }
                break;
        }
        return s;
    }
}
