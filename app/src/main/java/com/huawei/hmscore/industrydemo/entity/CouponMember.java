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

public class CouponMember {
    private String discount;
    private Integer imageBackgroud;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getImageBackgroud() {
        return imageBackgroud;
    }

    public void setImageBackgroud(Integer imageBackgroud) {
        this.imageBackgroud = imageBackgroud;
    }

    public CouponMember(String discount, Integer imageBackgroud) {
        this.discount = discount;
        this.imageBackgroud = imageBackgroud;
    }

    @Override
    public String toString() {
        return "CouponMember{" +
                "discount='" + discount + '\'' +
                ", imageBackgroud=" + imageBackgroud +
                '}';
    }
}
