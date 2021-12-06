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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ItemCommentBinding;
import com.huawei.hmscore.industrydemo.entity.Comment;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.inteface.OnTranslateCallBack;
import com.huawei.hmscore.industrydemo.page.activity.PhotoVideoViewActivity;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.CommentUtils;
import com.huawei.hmscore.industrydemo.utils.MlTranslateUtils;

import java.math.BigDecimal;
import java.util.List;

public class Comment2Adapter extends BaseAdapter<ItemCommentBinding, Comment> {
    private Activity mActivity;
    private List<Comment> mCommentList;
    private LanguageAdapter mLanguageAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mMTranslateLanguage;
    private PopupWindow mPopupWindow;
    private boolean mIsHasVideo;
    private int mRestId;
    private int mHeight;

    public Comment2Adapter(List<Comment> mCommentList, Activity activity, int restId) {
        this.mActivity = activity;
        this.mCommentList = mCommentList;
        this.mRestId = restId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_comment;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, Comment comment) {
        refreshView(holder, comment, position);

        holder.bind.ivImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPhotoViewActivity(comment, mIsHasVideo, 0);
            }
        });
        holder.bind.ivImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPhotoViewActivity(comment, false, 1);
            }
        });
        holder.bind.ivImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPhotoViewActivity(comment, false, 2);
            }
        });
        holder.bind.tvCommentTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(holder.bind.tvCommentTranslate, holder.bind.tvRestaurantContent, holder.bind.tvItemRestaurantTime,
                        holder.bind.tvItemRestaurantScore, holder.bind.tvRestaurantNumber, holder.bind.tvCommentUsername, comment);
            }
        });
    }

    private void gotoPhotoViewActivity(Comment comment, boolean isHasVideo, int pos) {
        Intent intent = new Intent(mActivity, PhotoVideoViewActivity.class);
        if (isHasVideo) {
            intent.putExtra(KeyConstants.PHOTO_TYPE, 3);
        } else {
            intent.putExtra(KeyConstants.PHOTO_TYPE, 4);
            intent.putExtra(KeyConstants.PHOTO_POSITION, pos);
        }
        intent.putExtra(KeyConstants.IMAGE_UIR, comment.getImgUri());
        intent.putExtra(KeyConstants.RESTAURANT_ID, mRestId);
        mActivity.startActivity(intent);
    }

    private void refreshView(@NonNull BaseViewHolder holder, Comment comment, int position) {
        UserRepository userRepository = new UserRepository();
        User user = userRepository.queryByOpenId(comment.getOpenid());
        String[] split = CommentUtils.splitToArray(comment.getImgUri());
        mIsHasVideo = CommentUtils.judgeIsHasVideo(split);
        if (split.length > 0) {
            Glide.with(mActivity).load(split[0]).into(holder.bind.ivImage1);
            holder.bind.ivImage1.setVisibility(View.VISIBLE);
            if (mIsHasVideo) {
                holder.bind.ivVideo.setVisibility(View.VISIBLE);
            }
        }

        if (split.length > 1) {
            Glide.with(mActivity).load(split[1]).into(holder.bind.ivImage2);
            holder.bind.ivImage2.setVisibility(View.VISIBLE);
        }

        if (split.length > 2) {
            Glide.with(mActivity).load(split[2]).into(holder.bind.ivImage3);
            holder.bind.ivImage3.setVisibility(View.VISIBLE);
            holder.bind.tvImage.setVisibility(View.VISIBLE);
        }

        holder.bind.tvImage.setText(split.length + "");
        holder.bind.tvRestaurantContent.setText(comment.getContent());
        holder.bind.tvItemRestaurantTime.setText(mActivity.getResources().getString(R.string.comment_publish_time) + " " + comment.getDate());
        holder.bind.rtRestaurant.setRating(comment.getRate());


        BigDecimal bigDecimal = new BigDecimal(comment.getRate());
        double rateValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        holder.bind.tvRestaurantNumber.setText(rateValue + mActivity.getString(R.string.comment_score));
        if (comment.getRate() == 0) {
            holder.bind.tvRestaurantNumber.setText(mActivity.getString(R.string.comment_no_score));
        }

        if (user != null && user.getHuaweiAccount() != null) {
            Glide.with(mActivity).load(user.getHuaweiAccount().getAvatarUri()).error(R.mipmap.default_icon).placeholder(R.mipmap.default_icon).into(holder.bind.civCommentHeader);
            holder.bind.tvCommentUsername.setText(user.getHuaweiAccount().getDisplayName());
        } else {
            holder.bind.tvCommentUsername.setText(mActivity.getString(R.string.comment_username));
        }

        if (comment.getImgUri().length() > 0) {
            setVisibility(true, holder.bind.llImage);
        } else {
            setVisibility(false, holder.bind.llImage);
        }
    }


    public void setVisibility(boolean isVisible, View itemView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isVisible) {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            layoutParams.height = 0;
            layoutParams.width = 0;
            itemView.setVisibility(View.GONE);
        }
        itemView.setLayoutParams(layoutParams);
    }


    private void showPopupWindow(TextView translate, TextView commentTranslate, TextView commentTime, TextView commentScore, TextView commentGrade, TextView conmmentUserName, Comment comment) {
        LinearLayout relativeLayout = new LinearLayout(mActivity);
        View translateItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_translate, relativeLayout, false);
        mMTranslateLanguage = translateItemView.findViewById(R.id.rv_translate_language);
        mPopupWindow = new PopupWindow(mActivity);
        mPopupWindow.setContentView(translateItemView);
        initRvSetting(MlTranslateUtils.getCountryName(mActivity));
        int measuredWidth = translate.getMeasuredWidth();
        mPopupWindow.setWidth(measuredWidth);
        mPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        mPopupWindow.setOutsideTouchable(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#00f2f2f2"));
        mPopupWindow.setBackgroundDrawable(colorDrawable);
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(translate);
        };



        mLanguageAdapter.setOnItemClickLitener(new LanguageAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                String[] languageCode = mActivity.getResources().getStringArray(R.array.language_code);
                MlTranslateUtils.getInstance().initRemoteTranslateSetting(languageCode[position]);
                translate(commentTranslate, comment.getContent());
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                };
            }
        });
    }

    private void translate(TextView commentTranslate, String textContent) {
        MlTranslateUtils.getInstance().translateSourceText(textContent, new OnTranslateCallBack() {
            @Override
            public void translate(String str) {
                mActivity.runOnUiThread(() -> {
                    commentTranslate.setText(str);
                });
            }

            @Override
            public void onError(String errMessage) {
                Toast.makeText(mActivity, mActivity.getString(R.string.comment_networkerror), Toast.LENGTH_SHORT).show();
                Log.d("Comment2Adapter", errMessage);
            }
        });
    }

    private void initRvSetting(List<String> langLists) {
        mLanguageAdapter = new LanguageAdapter(langLists, mActivity);
        mLanguageAdapter.refresh(langLists);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMTranslateLanguage.setLayoutManager(mLayoutManager);
        mMTranslateLanguage.setItemAnimator(new DefaultItemAnimator());
        mMTranslateLanguage.setAdapter(mLanguageAdapter);

        mMTranslateLanguage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMTranslateLanguage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeight = mMTranslateLanguage.getHeight();
                int width = mMTranslateLanguage.getWidth();
                Log.d("Comment2Adapter", mHeight + "###");
                Log.d("Comment2Adapter", width + "$$$");
            }
        });
    }
}
