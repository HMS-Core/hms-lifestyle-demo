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

package com.huawei.hmscore.industrydemo.viewadapter;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.databinding.ItemOrderItemBinding;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.entity.OrderItem;
import com.huawei.hmscore.industrydemo.repository.FoodRepository;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/15]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderItemAdapter extends BaseAdapter<ItemOrderItemBinding, OrderItem> {
    private final FoodRepository foodRepository;

    public OrderItemAdapter() {
        foodRepository = new FoodRepository();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_order_item;
    }

    @Override
    public void setItemHolder(@NonNull BaseAdapter<ItemOrderItemBinding, OrderItem>.BaseViewHolder holder, int position,
        OrderItem orderItem) {
        Food food = foodRepository.queryByFoodId(orderItem.getFoodId());
        holder.bind.foodIcon.setImageResource(holder.itemView.getContext()
            .getResources()
            .getIdentifier(food.getImgAdd(), Constants.RESOURCE_TYPE_MIPMAP,
                holder.itemView.getContext().getPackageName()));
        holder.bind.foodName.setText(food.getFoodName());
        holder.bind.foodDes.setText(food.getFoodDes());
        holder.bind.count
            .setText(holder.itemView.getContext().getString(R.string.count, orderItem.getCount()));
        holder.bind.price
            .setText(holder.itemView.getContext().getString(R.string.total_price, orderItem.getPrice()));
    }
}
