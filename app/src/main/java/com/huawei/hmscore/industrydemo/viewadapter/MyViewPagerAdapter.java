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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.entity.Image;

import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {
    private List<Image> mImages;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;

    public MyViewPagerAdapter(List<Image> mImages, Context context) {
        this.mContext = context;
        this.mImages = mImages;
    }

    @Override
    public int getCount() {
        if (mImages != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.item_restaurant, null);
        FrameLayout mFlRes = view.findViewById(R.id.fl_restaurent);
        ImageView mIvRes = view.findViewById(R.id.image_Restaurant);
        ImageView mIvPlay = view.findViewById(R.id.image_ivplay);
        int item = position % mImages.size();
        switch (mImages.get(item).getImgType()) {
            case 1:
                mIvPlay.setVisibility(View.GONE);
                mIvRes.setImageResource(mContext.getResources()
                        .getIdentifier(mImages.get(item).getImgAdd(), Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            case 2:
                mIvPlay.setVisibility(View.VISIBLE);
                mIvPlay.setImageResource(R.mipmap.panaroma);
                mIvRes.setImageResource(mContext.getResources()
                        .getIdentifier("rest2_food5", Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            case 3:
                mIvPlay.setVisibility(View.VISIBLE);
                mIvPlay.setImageResource(R.mipmap.video_play);
                mIvRes.setImageResource(mContext.getResources()
                        .getIdentifier("rest2_food4", Constants.RESOURCE_TYPE_MIPMAP,
                                mContext.getPackageName()));
                break;
            default:
                mIvPlay.setVisibility(View.VISIBLE);
                break;
        }


        mFlRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(mIvRes, position, mImages.get(item).getImgId(), mImages.get(item).getImgType());
                }
            }
        });
        container.addView(view);
        return view;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, int imageId, int imageType);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
