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

import android.view.View;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.databinding.ItemProductBinding;
import com.huawei.hmscore.industrydemo.entity.Bag;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.inteface.OnBagChangeListener;
import com.huawei.hmscore.industrydemo.inteface.OnItemClickListener;
import com.huawei.hmscore.industrydemo.repository.BagRepository;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class ProductsAdapter extends BaseAdapter<ItemProductBinding, Food> {

    private OnItemClickListener onItemClickListener;

    private OnBagChangeListener onBagChangeListener;

    private final BagRepository bagRepository;

    public ProductsAdapter() {
        bagRepository = new BagRepository();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnBagChangeListener(OnBagChangeListener onBagChangeListener) {
        this.onBagChangeListener = onBagChangeListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_product;
    }

    @Override
    public void setItemHolder(@NonNull BaseAdapter<ItemProductBinding, Food>.BaseViewHolder holder, int position,
        Food food) {
        holder.bind.iconProduct.setImageResource(holder.itemView.getContext()
            .getResources()
            .getIdentifier(food.getImgAdd(), Constants.RESOURCE_TYPE_MIPMAP,
                holder.itemView.getContext().getPackageName()));
        holder.bind.productTitle.setText(food.getFoodName());
        holder.bind.productDescription.setText(food.getFoodDes());
        holder.bind.productPrice
            .setText(holder.itemView.getContext().getString(R.string.total_price, food.getPrice()));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        Bag bag = bagRepository.queryByFoodId(food.getFoodId());
        if (null == bag) {
            holder.bind.productReduce.setVisibility(View.INVISIBLE);
            holder.bind.productNum.setVisibility(View.INVISIBLE);
        } else {
            holder.bind.productReduce.setVisibility(View.VISIBLE);
            holder.bind.productNum.setVisibility(View.VISIBLE);
            holder.bind.productNum.setText(String.valueOf(bag.getQuantity()));
        }
        holder.bind.productAdd.setOnClickListener(v -> {
            if (null == bag) {
                bagRepository.insert(new Bag(food.getFoodId(), 1, food.getRestId(), food.getPrice()));
            } else {
                bag.setQuantity(bag.getQuantity() + 1);
                bagRepository.update(bag);
            }
            notifyDataSetChanged();
            onBagChangeListener.onBagChange();
        });
        holder.bind.productReduce.setOnClickListener(v -> {
            if (1 == bag.getQuantity()) {
                bagRepository.delete(bag);
            } else {
                bag.setQuantity(bag.getQuantity() - 1);
                bagRepository.update(bag);
            }
            notifyDataSetChanged();
            onBagChangeListener.onBagChange();
        });
        if (position == getmData().size() - 1) {
            holder.bind.bottom.setVisibility(View.VISIBLE);
        } else {
            holder.bind.bottom.setVisibility(View.GONE);
        }
    }
}
