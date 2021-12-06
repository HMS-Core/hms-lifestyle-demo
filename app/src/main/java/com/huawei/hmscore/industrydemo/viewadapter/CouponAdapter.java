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
import com.huawei.hmscore.industrydemo.databinding.ItemMember2Binding;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.CouponMember;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.repository.CardRepository;
import com.huawei.hmscore.industrydemo.repository.CouponRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;

import java.util.List;

public class CouponAdapter extends BaseAdapter<ItemMember2Binding, CouponMember> {
    private boolean mLogin;
    private int mRestId;
    private Context mContext;
    private List<CouponMember> mCommentList;
    private OnItemClickLitener mOnItemClickLitener;

    public CouponAdapter(List<CouponMember> mCommentList, Context context, boolean login, int restid) {
        this.mContext = context;
        this.mLogin = login;
        this.mCommentList = mCommentList;
        this.mRestId = restid;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_member2;
    }

    @Override
    public void setItemHolder(@NonNull BaseViewHolder holder, int position, CouponMember couponMember) {
        if (!mLogin) {
            if (position == 0) {
                commonStatus(holder);
            } else {
                hasCouponStatus((BaseViewHolder) holder, View.INVISIBLE, View.INVISIBLE, R.color.yellow);
            }
        } else {
            switch (position) {
                case 0:
                    if(existMemberCard(new UserRepository().getCurrentUser())){
                        hasMemberStatus((BaseViewHolder) holder, View.GONE, View.VISIBLE, R.color.sales_volume);
                    }else{
                        commonStatus(holder);
                    }
                    break;
                case 1:
                    if (existCoupon(Constants.COUPON_CONDITION1, new UserRepository().getCurrentUser())) {
                        hasCouponStatus((BaseViewHolder) holder, View.GONE, View.VISIBLE, R.color.sales_volume);
                    } else {
                        commonStatus2(holder);
                    }
                    break;
                case 2:
                    if (existCoupon(Constants.COUPON_CONDITION2, new UserRepository().getCurrentUser())) {
                        hasCouponStatus((BaseViewHolder) holder, View.GONE, View.VISIBLE, R.color.sales_volume);
                    } else {
                        commonStatus2(holder);
                    }
                    break;
                case 3:
                    if (existCoupon(Constants.COUPON_CONDITION3, new UserRepository().getCurrentUser())) {
                        hasCouponStatus(holder, View.GONE, View.VISIBLE, R.color.sales_volume);
                    } else {
                        commonStatus2(holder);
                    }
                    break;
                default:
                    break;
            }
        }


        holder.bind.imageCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickLitener) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });

        holder.bind.imageCoupon.setImageResource(couponMember.getImageBackgroud());
        holder.bind.textCoupon.setText(couponMember.getDiscount());
        holder.bind.textCoupon.setBackgroundResource(couponMember.getImageBackgroud());
        holder.bind.textCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickLitener) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });
    }

    private void hasMemberStatus(BaseViewHolder holder, int gone, int visible, int sales_volume) {
        holder.bind.imageCoupon.setVisibility(visible);
        holder.bind.textBackgroud.setVisibility(visible);
        holder.bind.textCoupon.setVisibility(View.INVISIBLE);
        holder.bind.textPick.setVisibility(View.INVISIBLE);
    }

    private void hasCouponStatus(@NonNull BaseViewHolder holder, int gone, int visible, int p) {
        holder.bind.imageCoupon.setVisibility(gone);
        holder.bind.textBackgroud.setVisibility(visible);
        holder.bind.textCoupon.setVisibility(View.VISIBLE);
        holder.bind.textCoupon.setTextColor(mContext.getColor(p));
    }

    private void commonStatus2(@NonNull BaseViewHolder holder) {
        holder.bind.imageCoupon.setVisibility(View.GONE);
        holder.bind.textMember.setVisibility(View.GONE);
        holder.bind.textJoin.setVisibility(View.GONE);
        holder.bind.textBackgroud.setVisibility(View.INVISIBLE);
        holder.bind.textPick.setVisibility(View.VISIBLE);
        holder.bind.textCoupon.setVisibility(View.VISIBLE);
    }

    private void commonStatus(@NonNull BaseViewHolder holder) {
        holder.bind.imageCoupon.setVisibility(View.VISIBLE);
        holder.bind.textMember.setVisibility(View.VISIBLE);
        holder.bind.textJoin.setVisibility(View.VISIBLE);
        holder.bind.textPick.setVisibility(View.INVISIBLE);
        holder.bind.textCoupon.setVisibility(View.INVISIBLE);
        holder.bind.textBackgroud.setVisibility(View.INVISIBLE);
    }

    public void refreshView() {
        notifyDataSetChanged();
    }


    private boolean existCoupon(int couponCondition, User user) {
        List<Coupon> couponList =
                new CouponRepository().queryByUserAndRestAndCondition(user.getOpenId(), mRestId, couponCondition);
        return !couponList.isEmpty();
    }

    private boolean existMemberCard(User user) {
        List<Card> couponList =
                new CardRepository().queryByUserAndRest(user.getOpenId(),mRestId);
        return !couponList.isEmpty();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
