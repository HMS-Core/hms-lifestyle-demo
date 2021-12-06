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

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.RESTAURANT_ID;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.page.VoucherMgtActivity;
import com.huawei.hmscore.industrydemo.page.activity.RestaurantDetailActivity;
import com.huawei.hmscore.industrydemo.page.fragment.vouchermanagement.CouponFragment;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;

import java.util.List;

/**
 * CouponList Adapter
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/11/3]
 * @see CouponFragment
 * @see [Related Classes/Methods]
 */
public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.CouponViewHolder> {
    private static final String TAG = CouponListAdapter.class.getSimpleName();

    private final VoucherMgtActivity mActivity;

    private List<Coupon> couponList;

    private final int[] coupon_bdg =
        {R.mipmap.coupon_bg_1, R.mipmap.coupon_bg_2, R.mipmap.coupon_bg_3, R.mipmap.coupon_bg_4};

    private final int[] coupon_use_bdg_color = {0xff295df1, 0xff8a99fb, 0xfffe5d52, 0xffffa42d};

    public CouponListAdapter(VoucherMgtActivity activity, User user) {
        this.mActivity = activity;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CouponViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);

        if (position == 0 || !coupon.getRestId().equals(couponList.get(position - 1).getRestId())) {
            holder.linearCouponRestName.setVisibility(View.VISIBLE);
            Restaurant restaurant = new RestaurantRepository().queryByNumber(coupon.getRestId());
            holder.restName.setText((restaurant != null) ? restaurant.getRestname() : "");
        }

        holder.linearCouponContent.setBackgroundResource(coupon_bdg[position % 4]);

        String discount = mActivity.getString(R.string.coupon_discount, coupon.getDiscount());
        Spannable spannable = new SpannableString(discount);
        spannable.setSpan(new AbsoluteSizeSpan(25, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 1, discount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.discount.setText(spannable);
        holder.condition.setText(mActivity.getString(R.string.coupon_condition, coupon.getCondition()));
        holder.validityPeriod
            .setText(mActivity.getString(R.string.coupon_validity_period, coupon.getStartDate(), coupon.getEndDate()));

        TextView tvUse = holder.use;
        tvUse.setText(mActivity.getString(R.string.coupon_use));
        if (coupon.isStatus()) {
            ((GradientDrawable) tvUse.getBackground()).setColor(coupon_use_bdg_color[position % 4]);
            tvUse.setOnClickListener(v -> {
                Intent intent = new Intent(mActivity, RestaurantDetailActivity.class);
                intent.putExtra(RESTAURANT_ID, coupon.getRestId());
                mActivity.startActivity(intent);
            });
        } else {
            ((GradientDrawable) tvUse.getBackground()).setColor(Color.parseColor("#c3c3c3"));
        }
    }

    @Override
    public int getItemCount() {
        if (couponList != null) {
            return couponList.size();
        } else {
            return 0;
        }
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearCouponGroup;

        LinearLayout linearCouponRestName;

        TextView restName;

        LinearLayout linearCouponContent;

        TextView discount;

        TextView condition;

        TextView validityPeriod;

        TextView use;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);

            linearCouponGroup = itemView.findViewById(R.id.linear_coupon_group);
            linearCouponRestName = itemView.findViewById(R.id.linear_coupon_rest_name);
            restName = itemView.findViewById(R.id.tv_rest_name);
            linearCouponContent = itemView.findViewById(R.id.linear_coupon);
            discount = itemView.findViewById(R.id.tv_discount);
            condition = itemView.findViewById(R.id.tv_condition);
            validityPeriod = itemView.findViewById(R.id.tv_validity_period);
            use = itemView.findViewById(R.id.tv_use);
        }
    }
}
