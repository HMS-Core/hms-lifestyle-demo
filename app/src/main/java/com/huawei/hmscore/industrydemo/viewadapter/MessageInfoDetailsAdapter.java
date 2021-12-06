/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hmscore.industrydemo.viewadapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.databinding.ItemMessageinfoBinding;
import com.huawei.hmscore.industrydemo.entity.Message;

import java.util.List;

public class MessageInfoDetailsAdapter extends BaseAdapter<ItemMessageinfoBinding, Message> {
    private Context mContext;
    private List<Message> mMessageInfos;
    private OnItemClickLitener mOnItemClickLitener;

    public MessageInfoDetailsAdapter(Context context, List<Message> messageInfos,OnItemClickLitener mOnItemClickLitener) {
        this.mContext = context;
        this.mMessageInfos = messageInfos;
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_messageinfo;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, Message message) {
        holder.bind.tvMessageinfoSend.setText(message.getMessageTitle());
        holder.bind.tvMessageinfoContent.setText(message.getMessageBody());
        holder.bind.tvMessageinfoTime.setText(message.getDate());

        if (message.getSenderLogo() != null) {
            holder.bind.ivMessageinfoLogo.setImageResource(
                    mContext.getResources().getIdentifier(message.getSenderLogo(), "mipmap", mContext.getPackageName()));
        }
        boolean unread = message.getUnRead();
        if (unread) {
            holder.bind.ivMessageinfoRead.setVisibility(View.VISIBLE);
        } else {
            holder.bind.ivMessageinfoRead.setVisibility(View.INVISIBLE);
        }

        holder.bind.llMessageinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mOnItemClickLitener", "onItemClick before");
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(view, position);
                    Log.i("mOnItemClickLitener", "onItemClick");
                }
            }
        });
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
