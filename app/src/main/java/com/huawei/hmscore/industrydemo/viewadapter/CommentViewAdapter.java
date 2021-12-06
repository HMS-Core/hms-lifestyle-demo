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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.entity.Images;
import com.huawei.hmscore.industrydemo.inteface.CommentListener;
import com.huawei.hmscore.industrydemo.utils.PictureUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.ViewHolder> {

    public final static String TAG = "CommentActivitys";

    public List<Images> mImageList;

    private final Context mContext;

    private final CommentListener mListener;

    public CommentViewAdapter(Context context, List<Images> imageList, CommentListener listener) {
        mListener = listener;
        this.mImageList = imageList;
        this.mContext = context;
    }

    public void refreshList(List<Images> imageList) {
        mImageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Images images = mImageList.get(position);
        String path = images.getPath();
        if (TextUtils.isEmpty(path)) {
            holder.closeView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.comment_add);
            holder.imageView.setClickable(true);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.selectPicture();
                }
            });
        } else {
            holder.closeView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.agc_iam_banner);
            String type = images.getType();
            if ("video".equals(type)) {
                holder.imageView.setImageBitmap(PictureUtil.getVideoThumbnail(path));
                holder.videoView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setImageBitmap(PictureUtil.getBitmapByPath(path, 2));
                holder.videoView.setVisibility(View.GONE);
            }
            holder.imageView.setClickable(false);
            holder.closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageList.remove(holder.getAdapterPosition());
                    if (mImageList.size() > 0) {
                        if (!TextUtils.isEmpty(mImageList.get(0).getPath())) {
                            mImageList.add(0, new Images());
                        }
                    } else {
                        mImageList.add(0, new Images());
                    }
                    mListener.refreshImageList(mImageList);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageView closeView;

        ImageView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            closeView = itemView.findViewById(R.id.closeView);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}
