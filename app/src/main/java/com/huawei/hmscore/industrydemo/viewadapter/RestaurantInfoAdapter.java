/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hmscore.industrydemo.viewadapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.databinding.ItemRestaurantBinding;
import com.huawei.hmscore.industrydemo.entity.Image;

import java.util.List;

public class RestaurantInfoAdapter extends BaseAdapter<ItemRestaurantBinding, Image> {
    private List<Image> mImages;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;

    public RestaurantInfoAdapter(List<Image> mImages, Context context) {
        this.mContext = context;
        this.mImages = mImages;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_restaurant;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, Image image) {
        switch (image.getImgType()) {
            case 1:
                holder.bind.imageIvplay.setVisibility(View.GONE);
                holder.bind.imageRestaurant.setImageResource(mContext.getResources()
                        .getIdentifier(mImages.get(position).getImgAdd(), Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            case 2:
                holder.bind.imageIvplay.setVisibility(View.GONE);
                holder.bind.imageRestaurant.setImageResource(mContext.getResources()
                        .getIdentifier("rest2_food4", Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            case 3:
                holder.bind.imageIvplay.setVisibility(View.VISIBLE);
                holder.bind.imageRestaurant.setImageResource(mContext.getResources()
                        .getIdentifier("rest2_food4", Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            default:
                holder.bind.imageIvplay.setVisibility(View.VISIBLE);
                break;
        }
        holder.bind.imageRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(holder.bind.imageRestaurant, position, image.getImgId(), image.getImgType());
                }
            }
        });
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, int imageId, int imageType);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
