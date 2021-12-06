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
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.inteface.CommentSelectListener;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.viewadapter.SelectResturantAdapter;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SelectResturantActivity extends BaseActivity implements CommentSelectListener {
    private final static String TAG= "SelectResturantActivity";
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_resturant);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_base_back).setOnClickListener(v->{
            finish();
        });
        TextView titleText = findViewById(R.id.tv_base_title);
        titleText.setText(R.string.comment_merchant);
        mRecyclerView = findViewById(R.id.recycler_rest);

        RestaurantRepository restaurantRepository = new RestaurantRepository();
        List<Restaurant> list = restaurantRepository.queryAll();

        SelectResturantAdapter adapter = new SelectResturantAdapter(this, list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void exit(int id, String name) {
        Log.i(TAG,"exit");
        Intent intent = new Intent();
        intent.putExtra(KeyConstants.COMMENT_SELECT_ID, id);
        intent.putExtra(KeyConstants.COMMENT_SELECT_NAME, name);
        setResult(RESULT_OK, intent);
        finish();
    }
}