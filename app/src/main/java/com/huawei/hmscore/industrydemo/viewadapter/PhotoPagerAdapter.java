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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PhotoPagerAdapter extends PagerAdapter {

    private  OnViewItemClockListener mOnViewItemClockListener;
    private List<String> mImageUri;
    private Context mContext;

    public PhotoPagerAdapter(Context context, List<String> imageUri,OnViewItemClockListener onViewItemClockListener) {
        this.mOnViewItemClockListener = onViewItemClockListener;
        this.mContext = context;
        this.mImageUri = imageUri;
    }

    @Override
    public int getCount() {
        if (mImageUri != null && mImageUri.size() > 0) {
            return mImageUri.size();
        }
        return 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(mContext).load(mImageUri.get(position)).into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mOnViewItemClockListener){
                    mOnViewItemClockListener.onItemClick(v,position);
                }

            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface OnViewItemClockListener {
        void onItemClick(View view, int position);
    }


}
