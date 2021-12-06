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

package com.huawei.hmscore.industrydemo.page.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseFragment;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.inteface.OnBagChangeListener;
import com.huawei.hmscore.industrydemo.page.viewmodel.OrderDishesFragmentViewModel;
import com.huawei.hmscore.industrydemo.wight.TakeawayScrollView;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderDishesFragment extends BaseFragment {
    private OrderDishesFragmentViewModel viewModel;

    private int restId;

    private OnBagChangeListener onBagChangeListener;

    private TakeawayScrollView takeawayScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_dishes, container, false);
        restId = getArguments().getInt(KeyConstants.RESTAURANT_ID);
        viewModel = new OrderDishesFragmentViewModel(this);
        viewModel.initView(view);
        return view;
    }

    public int getRestId() {
        return restId;
    }

    public TakeawayScrollView getTakeawayScrollView() {
        return takeawayScrollView;
    }

    public void setTakeawayScrollView(TakeawayScrollView takeawayScrollView) {
        this.takeawayScrollView = takeawayScrollView;
    }

    public OnBagChangeListener getOnBagChangeListener() {
        return onBagChangeListener;
    }

    public void setOnBagChangeListener(OnBagChangeListener onBagChangeListener) {
        this.onBagChangeListener = onBagChangeListener;
    }
}