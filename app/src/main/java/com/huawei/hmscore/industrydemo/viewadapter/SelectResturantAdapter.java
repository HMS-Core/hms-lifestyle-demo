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
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.inteface.CommentSelectListener;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SelectResturantAdapter extends RecyclerView.Adapter<SelectResturantAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Restaurant> mRestList;
    private final CommentSelectListener mListener;

    public SelectResturantAdapter(Context context, List<Restaurant> restList, CommentSelectListener listener) {
        this.mContext = context;
        this.mRestList = restList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.comment_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = mRestList.get(position);
        int id = restaurant.getRestid();
        String name = restaurant.getRestname();
        holder.name.setText( restaurant.getRestname());
        if (restaurant.getStep() != null && !"NaN".equals(restaurant.getStep())) {
            holder.distance.setText(mContext.getString(R.string.rest_step, restaurant.getStep()));
        } else {
            holder.distance.setText(mContext.getString(R.string.rest_step, "5000"));
        }
        holder.address.setText(restaurant.getAddress());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.exit(id, name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mRestList ? 0 : mRestList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView distance;
        TextView address;
        View item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            name = itemView.findViewById(R.id.name);
            distance = itemView.findViewById(R.id.distance);
            address = itemView.findViewById(R.id.address);
        }
    }
}
