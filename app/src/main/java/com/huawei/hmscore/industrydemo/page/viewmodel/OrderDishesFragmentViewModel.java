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

package com.huawei.hmscore.industrydemo.page.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseFragmentViewModel;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.page.fragment.OrderDishesFragment;
import com.huawei.hmscore.industrydemo.repository.FoodRepository;
import com.huawei.hmscore.industrydemo.viewadapter.CategoryAdapter;
import com.huawei.hmscore.industrydemo.viewadapter.ProductsAdapter;
import com.huawei.hmscore.industrydemo.wight.TakeawayScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderDishesFragmentViewModel extends BaseFragmentViewModel<OrderDishesFragment> {

    private RecyclerView categoryRV;

    private RecyclerView productRV;

    private List<Integer> categoryIndex;

    private List<String> category;

    private List<Food> foods;

    private boolean isPressCategory = false;

    private TakeawayScrollView takeawayScrollView;

    /**
     * constructor
     *
     * @param orderDishesFragment Fragment object
     */
    public OrderDishesFragmentViewModel(OrderDishesFragment orderDishesFragment) {
        super(orderDishesFragment);
    }

    @Override
    public void initView(View view) {
        categoryRV = view.findViewById(R.id.rv_category);
        productRV = view.findViewById(R.id.rv_product);
        takeawayScrollView = mFragment.getTakeawayScrollView();
        initCategory(0);
    }

    private void initCategory(int showPosition) {
        category = new ArrayList<>();
        categoryIndex = new ArrayList<>();
        FoodRepository foodRepository = new FoodRepository();
        foods = foodRepository.queryByRestId(mFragment.getRestId());
        if (null == foods) {
            return;
        }
        Food food;
        for (int i = 0; i < foods.size(); i++) {
            food = foods.get(i);
            if (category.indexOf(food.getFoodType()) < 0) {
                category.add(food.getFoodType());
                categoryIndex.add(i);
            }
        }
        CategoryAdapter categoryAdapter = new CategoryAdapter(category, mFragment.getContext(), showPosition);
        categoryAdapter.setOnItemClickListener(position -> {
            isPressCategory = true;
            if (position > categoryAdapter.getShowPosition() && !productRV.canScrollVertically(1)) {
                takeawayScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            } else {
                productRV.scrollToPosition(categoryIndex.get(position));
            }
            categoryAdapter.setShowPosition(position);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragment.getContext());
        categoryRV.setLayoutManager(layoutManager);
        categoryRV.setAdapter(categoryAdapter);
        initProduct(categoryAdapter);
    }

    private void initProduct(CategoryAdapter categoryAdapter) {
        ProductsAdapter productsAdapter = new ProductsAdapter();
        productsAdapter.setOnBagChangeListener(mFragment.getOnBagChangeListener());
        LinearLayoutManager layoutManager = new CenterLayoutManager(mFragment.getContext());
        productRV.setLayoutManager(layoutManager);
        productRV.setAdapter(productsAdapter);
        productsAdapter.refresh(foods);
        productRV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isPressCategory) {
                    isPressCategory = false;
                    return;
                }
                updateCategoryStatus(layoutManager, categoryAdapter);
            }
        });
    }

    private void updateCategoryStatus(LinearLayoutManager layoutManager, CategoryAdapter categoryAdapter) {
        int current = layoutManager.findFirstVisibleItemPosition();
        for (int i = 0; i < categoryIndex.size(); i++) {
            if (current >= categoryIndex.get(i)) {
                if (i == categoryIndex.size() - 1) {
                    categoryAdapter.setShowPosition(i);
                    categoryAdapter.notifyDataSetChanged();
                    return;
                }
                for (int j = i + 1; j < categoryIndex.size(); j++) {
                    if (current < categoryIndex.get(j)) {
                        categoryAdapter.setShowPosition(j - 1);
                        categoryAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onClickEvent(int viewId) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }

    private class CenterLayoutManager extends LinearLayoutManager {
        public CenterLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void scrollToPosition(int position) {
            scrollToPositionWithOffset(position, 0);
        }
    }
}
