/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

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
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.huawei.hmscore.industrydemo.entity.converter.StringsConverter;
import com.huawei.hmscore.industrydemo.repository.CommentRepository;

import java.util.List;

/**
 * Restaurant Entity
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/28]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
@Entity
@TypeConverters(StringsConverter.class)
public class Restaurant {
    @PrimaryKey
    private int restid;

    private String restname;

    private String worktime;

    private float rate;

    private String avgprice;

    private String foodtype;

    private Double poslat;

    private Double poslng;

    private String address;

    private String logo;

    // extra
    private String step;

    private String description;

    public int getRestid() {
        return restid;
    }

    public void setRestid(int restid) {
        this.restid = restid;
    }

    public void setFoodtype(String foodtype) {
        this.foodtype = foodtype;
    }

    public String getFoodtype() {
        return this.foodtype;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAvgprice() {
        return avgprice;
    }

    public void setAvgprice(String avgprice) {
        this.avgprice = avgprice;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRate() {
        CommentRepository commentRepository = new CommentRepository();
        List<Comment> comments = commentRepository.queryByRest(restid);
        rate = 5;
        float tempRate;
        if (comments != null) {
            int size = comments.size();
            int rateAmount = 0;
            float curRate;
            tempRate = 0;

            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    curRate = comments.get(i).getRate();
                    if(curRate > 0) {
                        tempRate += curRate;
                        rateAmount++;
                    }
                }
                if (rateAmount > 0) {
                    rate = tempRate / rateAmount;
                }
            }
        }

        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

    public Double getPoslat() {
        return poslat;
    }

    public void setPoslat(Double poslat) {
        this.poslat = poslat;
    }

    public Double getPoslng() {
        return poslng;
    }

    public void setPoslng(Double poslng) {
        this.poslng = poslng;
    }
}
