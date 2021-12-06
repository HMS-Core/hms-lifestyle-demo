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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserAddress {

    @PrimaryKey(autoGenerate = true)
    public int addressId;

    public String opened;

    public String name;

    @ColumnInfo(name = "contact")
    public String phone;

    public String region;

    public String address;

    public double lat;

    public double lng;

    public boolean defaultflag;

    public int getAddressId() {
        return addressId;
    }

    public UserAddress setAddressId(int addressId) {
        this.addressId = addressId;
        return this;
    }

    public String getOpened() {
        return opened;
    }

    public UserAddress setOpened(String opened) {
        this.opened = opened;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserAddress setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserAddress setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public UserAddress setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserAddress setAddress(String address) {
        this.address = address;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public UserAddress setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public UserAddress setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public boolean isDefaultflag() {
        return defaultflag;
    }

    public UserAddress setDefaultflag(boolean defaultflag) {
        this.defaultflag = defaultflag;
        return this;
    }
}
