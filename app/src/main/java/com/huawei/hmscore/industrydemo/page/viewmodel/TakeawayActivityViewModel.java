/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021 All rights reserved.
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

package com.huawei.hmscore.industrydemo.page.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hmscore.industrydemo.MainApplication;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.base.BaseFragment;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityTakeawayBinding;
import com.huawei.hmscore.industrydemo.entity.Bag;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.page.OrderSubmitActivity;
import com.huawei.hmscore.industrydemo.page.TakeawayActivity;
import com.huawei.hmscore.industrydemo.page.fragment.OrderDishesFragment;
import com.huawei.hmscore.industrydemo.repository.BagRepository;
import com.huawei.hmscore.industrydemo.repository.CouponRepository;
import com.huawei.hmscore.industrydemo.repository.ImageRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.viewadapter.RestPicsAdapter;
import com.huawei.hmscore.industrydemo.viewadapter.TakeawayViewPagerAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class TakeawayActivityViewModel extends BaseActivityViewModel<TakeawayActivity> {
    private ActivityTakeawayBinding binding;

    private List<View> dots;

    private final ImageHandler handler;

    private BagRepository bagRepository;

    private WisePlayer wisePlayer;

    private RestPicsAdapter adapter;

    private CouponRepository couponRepository;

    private User user;

    /**
     * constructor
     *
     * @param takeawayActivity Activity object
     */
    public TakeawayActivityViewModel(TakeawayActivity takeawayActivity) {
        super(takeawayActivity);
        handler = new ImageHandler(new WeakReference<>(this));
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void initView() {
        binding = mActivity.getBinding();
        binding.lTitle.tvBaseTitle.setVisibility(View.INVISIBLE);
        Restaurant restaurant = new RestaurantRepository().queryByNumber(mActivity.getRestId());
        binding.restName.setText(restaurant.getRestname());
        binding.restRating.setText(String.valueOf(restaurant.getRate()));
        binding.businessHours
            .setText(mActivity.getString(R.string.business_hours, restaurant.getWorktime()));
        initCoupons();
        binding.restIcon.setImageResource(
            mActivity.getResources().getIdentifier(restaurant.getLogo(), "mipmap", mActivity.getPackageName()));
        initRestPics();
        initViewPager();
    }

    private void initPlayer() {
        if (MainApplication.getWisePlayerFactory() == null) {
            return;
        }
        wisePlayer = MainApplication.getWisePlayerFactory().createWisePlayer();
    }

    private void initRestPics() {
        List<Image> imageList = new ImageRepository().queryBuyRestIdAndImgType(mActivity.getRestId(), 1);
        List<Image> videoList = new ImageRepository().queryBuyRestIdAndImgType(mActivity.getRestId(), 3);
        initPlayer();
        adapter = new RestPicsAdapter(imageList, videoList, mActivity, wisePlayer);
        boolean isHasVideo = adapter.isHasVideo();

        dots = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            // Initializing dots
            ImageView dot = (ImageView) LayoutInflater.from(MainApplication.getContext())
                .inflate(R.layout.dot_image_view, binding.layoutDot, false);
            binding.layoutDot.addView(dot);
            dots.add(dot);
        }
        showPoint(0);

        binding.restPics.setOffscreenPageLimit(imageList.size());
        if (isHasVideo && wisePlayer != null) { // has video
            adapter.setInitVideoInterface((videoUrl, surfaceView) -> {
                wisePlayer.setVideoType(0);
                wisePlayer.setBookmark(10000);
                wisePlayer.setCycleMode(1);
                wisePlayer.setPlayUrl(videoUrl);
                Log.d("playtest:", "setPlayUrl");
                wisePlayer.setPlayEndListener(wisePlayer -> adapter.updatePlayCompleteView());
                wisePlayer.ready();
                wisePlayer.setReadyListener(wisePlayer1 -> {
                    wisePlayer1.seek(0);
                    adapter.updatePlayView(wisePlayer, 0);
                    Log.d("playtest:", "ready");
                });
            });
        }
        int videoPagePosition = adapter.getVideoPosition();
        binding.restPics.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (isHasVideo && wisePlayer != null) {
                    if (videoPagePosition == position) { // on video page
                        Log.d("playtest:", "onPageSelected");
                    } else {
                        adapter.videoScrollOff();
                    }
                }

                showPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        binding.restPics.setAdapter(adapter);
    }

    private void initViewPager() {
        bagRepository = new BagRepository();
        updateSelectDishes();
        List<BaseFragment> fragments = new ArrayList<>();
        OrderDishesFragment orderDishesFragment = new OrderDishesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KeyConstants.RESTAURANT_ID, mActivity.getRestId());
        orderDishesFragment.setArguments(bundle);
        orderDishesFragment.setTakeawayScrollView(binding.takeawaySv);
        orderDishesFragment.setOnBagChangeListener(() -> {
            updateSelectDishes();
        });
        fragments.add(orderDishesFragment);
        String[] types = new String[] {mActivity.getString(R.string.order_dishes)};
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.restVp.getLayoutParams();
        layoutParams.height = getScreenHeight();
        binding.restVp.setLayoutParams(layoutParams);
        binding.restVp
            .setAdapter(new TakeawayViewPagerAdapter(mActivity.getSupportFragmentManager(), fragments, types));
        binding.restTl.setupWithViewPager(binding.restVp);

    }

    private void updateSelectDishes() {
        List<Bag> bagList = bagRepository.queryByRestId(mActivity.getRestId());
        if (null == bagList || bagList.isEmpty()) {
            binding.selectNon.setVisibility(View.VISIBLE);
            return;
        }
        int bagNum = bagList.size();
        int totalPrice = 0;
        for (Bag bag : bagList) {
            totalPrice += bag.getPrice() * bag.getQuantity();
        }
        binding.selectDishes.setText(mActivity.getString(R.string.statistic, bagNum, totalPrice));
        binding.selectNon.setVisibility(View.INVISIBLE);
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.heightPixels - 350;
    }

    private void showPoint(int position) {
        if (dots == null) {
            return;
        }
        int size = dots.size();
        for (int i = 0; i < size; i++) {
            dots.get(i).setBackgroundResource(R.drawable.dot_no_selected);
        }
        dots.get(position % size).setBackgroundResource(R.drawable.dot_selected);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_base_back:
                mActivity.onBackPressed();
                break;
            case R.id.to_pay:
                User user = new UserRepository().getCurrentUser();
                if (user == null) {
                    Toast
                        .makeText(mActivity, mActivity.getString(R.string.please_sign_first),
                            Toast.LENGTH_SHORT)
                        .show();
                    break;
                }
                OrderSubmitActivity.startOrderSubmitActivity(mActivity, mActivity.getRestId());
                break;
            case R.id.rest_coupon1:
                checkCoupons(Constants.COUPON_CONDITION1, Constants.COUPON_DISCOUNT1);
                break;
            case R.id.rest_coupon2:
                checkCoupons(Constants.COUPON_CONDITION2, Constants.COUPON_DISCOUNT2);
                break;
            case R.id.rest_coupon3:
                checkCoupons(Constants.COUPON_CONDITION3, Constants.COUPON_DISCOUNT3);
                break;
            default:
                break;
        }
    }

    private void initCoupons() {
        binding.couponRule1.setText(mActivity.getResources()
            .getString(R.string.coupon_rule, Constants.COUPON_CONDITION1, Constants.COUPON_DISCOUNT1));
        binding.couponRule2.setText(mActivity.getResources()
            .getString(R.string.coupon_rule, Constants.COUPON_CONDITION2, Constants.COUPON_DISCOUNT2));
        binding.couponRule3.setText(mActivity.getResources()
            .getString(R.string.coupon_rule, Constants.COUPON_CONDITION3, Constants.COUPON_DISCOUNT3));
        user = new UserRepository().getCurrentUser();
        if (user == null) {
            return;
        }
        couponRepository = new CouponRepository();
        if (hasCoupons(Constants.COUPON_CONDITION1)) {
            updateCoupons(binding.restCoupon1Masking, binding.couponRule1);
        }
        if (hasCoupons(Constants.COUPON_CONDITION2)) {
            updateCoupons(binding.restCoupon2Masking, binding.couponRule2);
        }
        if (hasCoupons(Constants.COUPON_CONDITION3)) {
            updateCoupons(binding.restCoupon3Masking, binding.couponRule3);
        }
    }

    private void checkCoupons(int condition, int discount) {
        if (user == null) {
            Toast
                .makeText(mActivity, mActivity.getString(R.string.please_sign_first), Toast.LENGTH_SHORT)
                .show();
            return;
        }
        if (hasCoupons(condition)) {
            return;
        }
        couponRepository.insert(createCoupon(user.getOpenId(), mActivity.getRestId(), condition, discount));
        switch (condition) {
            case Constants.COUPON_CONDITION1:
                updateCoupons(binding.restCoupon1Masking, binding.couponRule1);
                break;
            case Constants.COUPON_CONDITION2:
                updateCoupons(binding.restCoupon2Masking, binding.couponRule2);
                break;
            case Constants.COUPON_CONDITION3:
                updateCoupons(binding.restCoupon3Masking, binding.couponRule3);
                break;
            default:
                break;
        }
        Toast.makeText(mActivity, mActivity.getString(R.string.coupon_claimed), Toast.LENGTH_LONG)
            .show();
    }

    private void updateCoupons(View view, TextView textView) {
        view.setVisibility(View.VISIBLE);
        textView.setTextColor(mActivity.getColor(R.color.sales_volume));
    }

    private boolean hasCoupons(int condition) {
        List<Coupon> couponList =
            couponRepository.queryByUserAndRestAndCondition(user.getOpenId(), mActivity.getRestId(), condition);
        return !couponList.isEmpty();
    }

    private Coupon createCoupon(String openId, int restId, int condition, int discount) {
        Coupon coupon = new Coupon();
        coupon.setOpenId(openId);
        coupon.setRestId(restId);
        coupon.setCondition(condition);
        coupon.setDiscount(discount);
        coupon.setStatus(true);
        return coupon;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }

    public void pausePlay() {
        if (wisePlayer != null) {
            adapter.videoScrollOff();
        }
    }

    public void onDestroy() {
        if (wisePlayer != null) {
            wisePlayer.stop();
            wisePlayer.release();
        }
    }

    private void setCurrentPosition(int currentPosition) {
        setViewPageScrollTime(binding.restPics);
        binding.restPics.setCurrentItem(currentPosition, true);
    }

    private void setViewPageScrollTime(ViewPager mViewPager) {
        Field field = null;
        try {
            field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            field.set(mViewPager, scroller);
            scroller.setmDuration(1000);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageHandler getHandler() {
        return handler;
    }

    private class ImageHandler extends Handler {
        /**
         * Time interval
         */
        public static final long TIME_DELAY = 3000L;

        /**
         * Request to update the displayed viewPager
         */
        public static final int UPDATE = 1;

        /**
         * Request to pause
         */
        public static final int PAUSE = 2;

        /**
         * Request to restart
         */
        public static final int RESTART = 3;

        /**
         * Record the latest page number.
         */
        public static final int CHANGED = 4;

        /**
         * Weak reference is used to prevent handler leakage.
         */
        private final WeakReference<TakeawayActivityViewModel> wk;

        private int currentItem = Integer.MAX_VALUE / 2;

        public ImageHandler(WeakReference<TakeawayActivityViewModel> wk) {
            this.wk = wk;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TakeawayActivityViewModel viewModel = wk.get();
            if (viewModel == null) {
                return;
            }

            // Check the message queue and remove unsent messages to avoid duplicate messages in complex environments.
            // This part will eat the first auto-rotation event, so you can add a condition, The event is cleared only
            // when the value is Position!=Max/2.
            // Because the first position must be equal to Max/2.
            if ((viewModel.getHandler().hasMessages(UPDATE)) && (currentItem != Integer.MAX_VALUE / 2)) {
                viewModel.getHandler().removeMessages(UPDATE);
            }
            switch (msg.what) {
                case UPDATE:
                    currentItem++;
                    viewModel.setCurrentPosition(currentItem);
                    viewModel.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                    break;
                case RESTART:
                    viewModel.getHandler().sendEmptyMessageDelayed(UPDATE, TIME_DELAY);
                    break;
                case CHANGED:
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }

    private class FixedSpeedScroller extends Scroller {
        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        /**
         * setmDuration
         * 
         * @param time ms
         */
        public void setmDuration(int time) {
            mDuration = time;
        }

    }
}
