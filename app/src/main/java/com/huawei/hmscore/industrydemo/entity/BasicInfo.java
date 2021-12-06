/*
    Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

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

import androidx.room.Entity;

/**
 * Product Basic Information Entity
 * 
 * @version [HMSCore-Demo 3.0.0.300, 2020/9/21]
 * @see Restaurant
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Entity
public class BasicInfo {
    private String shortName;

    private String name;

    private String thumbnail;

    private String price;

    private int displayPrice;

    private String link;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
    }

    public int getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(int displayPrice) {
        this.displayPrice = displayPrice;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }
}
