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

package com.huawei.hmscore.industrydemo.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityOrderSubmitBinding;
import com.huawei.hmscore.industrydemo.page.viewmodel.OrderSubmitActivityViewModel;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/12]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderSubmitActivity extends BaseActivity implements View.OnClickListener {
    private OrderSubmitActivityViewModel viewModel;

    private ActivityOrderSubmitBinding binding;

    private int restId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_submit);
        restId = getIntent().getIntExtra(KeyConstants.RESTAURANT_ID, 0);
        viewModel = new OrderSubmitActivityViewModel(this);
        viewModel.initView();
    }

    public static void startOrderSubmitActivity(Context context, int restId) {
        Intent intent = new Intent(context, OrderSubmitActivity.class);
        intent.putExtra(KeyConstants.RESTAURANT_ID, restId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        viewModel.onClickEvent(v.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    public int getRestId() {
        return restId;
    }

    public ActivityOrderSubmitBinding getBinding() {
        return binding;
    }
}