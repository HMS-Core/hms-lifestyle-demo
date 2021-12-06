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

import androidx.databinding.DataBindingUtil;

import com.huawei.hmscore.industrydemo.MainActivity;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityPaymentSucceedBinding;
import com.huawei.hmscore.industrydemo.repository.OrderRepository;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/19]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PaymentSucceedActivity extends BaseActivity implements View.OnClickListener {
    private ActivityPaymentSucceedBinding binding;

    private int orderId;

    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_succeed);
        orderId = getIntent().getIntExtra(KeyConstants.ORDER_ID, 0);
        orderRepository = new OrderRepository();
        initView();
    }

    private void initView() {
        binding.lTitle.ivBaseBack.setVisibility(View.INVISIBLE);
        binding.lTitle.tvBaseTitle.setText(getString(R.string.cashier));
        binding.paid
            .setText(getString(R.string.paid, orderRepository.queryByOrderI(orderId).getActualPrice()));
    }

    public static void startPaymentSucceededActivity(Context context, int orderId) {
        Intent intent = new Intent(context, PaymentSucceedActivity.class);
        intent.putExtra(KeyConstants.ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_orders:
                startActivity(new Intent(this, OrderListActivity.class));
                finish();
                break;
            case R.id.to_home_page:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }
}