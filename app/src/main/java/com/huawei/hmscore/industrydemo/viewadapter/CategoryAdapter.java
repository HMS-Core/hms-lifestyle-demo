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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.inteface.OnItemClickListener;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<String> category;

    private final Context context;

    private OnItemClickListener onItemClickListener;

    private TextView currentType;

    private int showPosition;

    public CategoryAdapter(List<String> category, Context context, int showPosition) {
        this.category = category;
        this.context = context;
        this.showPosition = showPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textType.setText(category.get(position));
        holder.itemView.setOnClickListener(v -> {
            changeStatus(holder);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        if (position == showPosition) {
            changeStatus(holder);
        }
    }

    private void changeStatus(ViewHolder holder) {
        if (currentType != null) {
            currentType.setBackgroundResource(R.color.category_unselected);
        }
        currentType = holder.textType;
        currentType.setBackgroundResource(R.color.category_selected);
    }

    @Override
    public int getItemCount() {
        return category == null ? 0 : category.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.category_type);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setShowPosition(int showPosition) {
        this.showPosition = showPosition;
    }

    public int getShowPosition() {
        return showPosition;
    }
}
