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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.page.fragment.HomeFragment.StoreAdapter;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SearchResultActivity extends BaseActivity {
    private String mSearchContent = "%null%";

    private RecyclerView mRecyclerView;

    private LinearLayout mLvNoProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent intent = getIntent();
        if (intent != null) {
            mSearchContent = intent.getStringExtra(KeyConstants.SEARCH_CONTENT);
            initView();
        }
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v->{
            finish();
        });
        findViewById(R.id.lv_search).setOnClickListener(v->{
            finish();
        });
        ((TextView) findViewById(R.id.tv_search_content)).setText(mSearchContent);

        mRecyclerView = findViewById(R.id.recycler_product);
        mLvNoProduct = findViewById(R.id.lv_no_product);

        RestaurantRepository restaurantRepository = new RestaurantRepository();
        List<Restaurant> list = restaurantRepository.queryByKeywords(mSearchContent);
        if (list == null || list.size() == 0) {
            mLvNoProduct.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mLvNoProduct.setVisibility(View.GONE);
        StoreAdapter storeAdapter = new StoreAdapter(this);
        storeAdapter.setShowAdsFlag(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(storeAdapter);
        storeAdapter.refresh(list);
    }
}