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

package com.huawei.hmscore.industrydemo.page.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;

import static com.huawei.hmscore.industrydemo.constants.KitConstants.ML_TRANSLATION;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.PANORAMA_FULLVIEW;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.VIDEO_PLAY;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.page.viewmodel.RestaurantDetailViewModel;

import java.util.List;

public class RestaurantDetailActivity extends BaseActivity implements View.OnClickListener {
    private RestaurantDetailViewModel mRestaurantDetailViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantdetail);
        mRestaurantDetailViewModel = new RestaurantDetailViewModel(this);
        mRestaurantDetailViewModel.initView();
        mRestaurantDetailViewModel.initListener();
        mRestaurantDetailViewModel.initLoc();
        mRestaurantDetailViewModel.initData();
        setKitList(new String[]{VIDEO_PLAY,PANORAMA_FULLVIEW,ML_TRANSLATION});
    }


    @Override
    public void onClick(View v) {
        mRestaurantDetailViewModel.onClickEvent(v.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRestaurantDetailViewModel.initRemoteTranslateSetting("en");
        mRestaurantDetailViewModel.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRestaurantDetailViewModel.closeRemoteTranslate();
    }

    public static class RestaurantReference extends TypeReference<List<Image>> {
        public RestaurantReference() {
        }
    }

}
