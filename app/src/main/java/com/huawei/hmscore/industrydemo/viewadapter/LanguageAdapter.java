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

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.databinding.ItemLanguageBinding;

import java.util.List;

public class LanguageAdapter extends BaseAdapter<ItemLanguageBinding,String> {
    private List<String> mCommentList;
    private OnItemClickLitener mOnItemClickLitener;

    public LanguageAdapter(List<String> mCommentList, Activity activity) {
        this.mCommentList = mCommentList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_language;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, String str) {
        Log.e("languageAdapter", "langLists" + str);
        holder.bind.tvTranslateArabian.setText(str);
        holder.bind.tvTranslateArabian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickLitener !=null){
                    mOnItemClickLitener.onItemClick(holder.itemView,position);
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
