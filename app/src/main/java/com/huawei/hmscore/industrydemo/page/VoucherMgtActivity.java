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

package com.huawei.hmscore.industrydemo.page;

import static com.huawei.hmscore.industrydemo.constants.Constants.CARD_INDEX;
import static com.huawei.hmscore.industrydemo.constants.Constants.COUPON_INDEX;
import static com.huawei.hmscore.industrydemo.constants.KeyConstants.PAGE_INDEX;
import static com.huawei.hmscore.industrydemo.utils.hms.WalletUtil.SAVE_FLAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.huawei.hms.wallet.constant.WalletPassConstant;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.page.fragment.vouchermanagement.CardFragment;
import com.huawei.hmscore.industrydemo.page.fragment.vouchermanagement.CouponFragment;
import com.huawei.hmscore.industrydemo.repository.CardRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.hms.WalletUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Voucher Management
 * 
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/9]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class VoucherMgtActivity extends BaseActivity {

    private ViewPager mViewPager;

    private FragmentPagerAdapter mAdapter;

    private List<Fragment> mFragments;

    private TextView textCard;

    private View viewCard;

    private TextView textCoupon;

    private View viewCoupon;

    private User mUser;

    private CardRepository mCardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_mgt);
        int startPage = getIntent().getIntExtra(PAGE_INDEX, CARD_INDEX);
        initViews();
        initEvents();
        setKitList(new String[]{KitConstants.WALLET_MEMBER});
        initData(startPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CardFragment) mAdapter.getItem(0)).onDataRefresh();
        ((CouponFragment) mAdapter.getItem(1)).onDataRefresh();
    }

    private void initViews() {
        mViewPager = findViewById(R.id.viewpager_card_coupon);

        TextView pageTitle = findViewById(R.id.tv_base_title);
        pageTitle.setText(R.string.voucher_management);
        pageTitle.setTextColor(0xff323232);
        pageTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textCard = findViewById(R.id.tab_card);
        viewCard = findViewById(R.id.view_card);
        textCoupon = findViewById(R.id.tab_coupon);
        viewCoupon = findViewById(R.id.view_coupon);
    }

    private void initEvents() {
        findViewById(R.id.iv_base_back).setOnClickListener(v -> finish());
        textCard.setOnClickListener(v -> selectTab(CARD_INDEX));
        textCoupon.setOnClickListener(v -> selectTab(COUPON_INDEX));
    }

    private void initData(int startPage) {
        mUser = new UserRepository().getCurrentUser();
        mCardRepository = new CardRepository();
        mFragments = new ArrayList<>();
        mFragments.add(new CardFragment(this, mUser));
        mFragments.add(new CouponFragment(this, mUser));

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) { // 从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() { // 获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        mViewPager.setAdapter(mAdapter);
        selectTab(startPage);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                resetColor();
                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * reset text color to black
     */
    private void resetColor() {
        textCard.setTextColor(getColor(R.color.color_0f0f0f));
        viewCard.setBackgroundColor(getColor(R.color.transparent));
        textCoupon.setTextColor(getColor(R.color.color_0f0f0f));
        viewCoupon.setBackgroundColor(getColor(R.color.transparent));
    }

    private void selectTab(int i) {
        switch (i) {
            case CARD_INDEX:
                textCard.setTextColor(getColor(R.color.color_de4c4d));
                viewCard.setBackgroundColor(getColor(R.color.color_ff0000));
                break;
            case COUPON_INDEX:
                textCoupon.setTextColor(getColor(R.color.color_de4c4d));
                viewCoupon.setBackgroundColor(getColor(R.color.color_ff0000));
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVE_FLAG) {
            // SAVE_FLAG is the constant value set in step 6.
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_LONG).show();
                    if (WalletUtil.card != null) {
                        Card card = mCardRepository.queryByCardId(WalletUtil.card.getCardId());
                        card.setWalletId(WalletUtil.card.getWalletId());
                        mCardRepository.insert(card);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, getString(R.string.cancel_by_user), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                    if (data != null) {
                        int errorCode = data.getIntExtra(WalletPassConstant.EXTRA_ERROR_CODE, -1);
                        AgcUtil.reportFailure(TAG, "fail, [" + errorCode + "]：" + errorCode);
                    } else {
                        AgcUtil.reportFailure(TAG, "fail：data is null");
                    }
                    break;
            }
        }
    }
}