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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.constants.MessageConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityOrderSubmitBinding;
import com.huawei.hmscore.industrydemo.entity.Bag;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.OrderItem;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.page.AddressListActivity;
import com.huawei.hmscore.industrydemo.page.CashierActivity;
import com.huawei.hmscore.industrydemo.page.OrderSubmitActivity;
import com.huawei.hmscore.industrydemo.repository.BagRepository;
import com.huawei.hmscore.industrydemo.repository.CardRepository;
import com.huawei.hmscore.industrydemo.repository.CouponRepository;
import com.huawei.hmscore.industrydemo.repository.OrderRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserAddressRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.MessagingUtil;
import com.huawei.hmscore.industrydemo.viewadapter.OrderItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/12]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderSubmitActivityViewModel extends BaseActivityViewModel<OrderSubmitActivity> {
    private Order order;

    private List<OrderItem> orderItems;

    private ActivityOrderSubmitBinding binding;

    private UserAddressRepository addressRepository;

    private BagRepository bagRepository;

    private CouponRepository couponRepository;

    private Coupon coupon;

    private int addressId = -1;

    /**
     * constructor
     *
     * @param orderSubmitActivity Activity object
     */
    public OrderSubmitActivityViewModel(OrderSubmitActivity orderSubmitActivity) {
        super(orderSubmitActivity);
    }

    @Override
    public void initView() {
        binding = mActivity.getBinding();
        addressRepository = new UserAddressRepository();
        generateOrder();
        binding.lTitle.tvBaseTitle.setText(mActivity.getString(R.string.submit_order));
        binding.lTitle.baseTitle.setBackgroundColor(Color.TRANSPARENT);
        updateDeliveredTime();
        updateAddress();
        Restaurant restaurant = new RestaurantRepository().queryByNumber(mActivity.getRestId());
        binding.restName.setText(restaurant.getRestname());
        initOrderItems();
        binding.packingPrice
            .setText(mActivity.getString(R.string.total_price, Constants.PACKING_CHARGES));
        binding.deliveryPrice
            .setText(mActivity.getString(R.string.total_price, Constants.DELIVERY_CHARGES));
        binding.discount.setText(
            mActivity.getString(R.string.total_price, order.getDiscount2() + order.getDiscount1()));
        binding.subtotal.setText(mActivity.getString(R.string.total_price, order.getActualPrice()));
        binding.totalPrice.setText(mActivity.getString(R.string.total_price, order.getActualPrice()));
        binding.discountPrice.setText(mActivity.getResources()
            .getString(R.string.discounted_bottom, order.getDiscount2() + order.getDiscount1()));
    }

    private void generateOrder() {
        bagRepository = new BagRepository();
        List<Bag> bagList = bagRepository.queryByRestId(mActivity.getRestId());
        order = new Order();
        User user = new UserRepository().getCurrentUser();
        order.setStatus(Constants.NOT_PAID);
        order.setRestId(mActivity.getRestId());
        int totalPrice = 0;
        orderItems = new ArrayList<>();
        for (Bag bag : bagList) {
            totalPrice += bag.getPrice() * bag.getQuantity();
            OrderItem orderItem = new OrderItem(orderItems.size(), order.getOrderId(), bag.getFoodId(),
                bag.getQuantity(), bag.getPrice());
            orderItems.add(orderItem);
        }
        order.setTotalPrice(totalPrice + Constants.PACKING_CHARGES + Constants.DELIVERY_CHARGES);
        if (null != user) {
            order.setOpenId(user.getOpenId());
            checkCouponsAndCards(user);
        }
        order.setActualPrice(order.getTotalPrice() - order.getDiscount2() - order.getDiscount1());
    }

    private void checkCouponsAndCards(User user) {
        couponRepository = new CouponRepository();
        List<Coupon> couponList =
            couponRepository.queryByUserAndRestAndStatus(user.getOpenId(), mActivity.getRestId(), true);
        if (!couponList.isEmpty()) {
            couponList.sort((coupon1, coupon2) -> coupon2.getCondition() - coupon1.getCondition());
            for (Coupon coupon : couponList) {
                if (order.getTotalPrice() > coupon.getCondition()) {
                    this.coupon = coupon;
                    order.setCouponId(coupon.getCouponId());
                    order.setDiscount2(coupon.getDiscount());
                    break;
                }
            }
        }
        CardRepository cardRepository = new CardRepository();
        List<Card> cardList = cardRepository.queryByUserId(user.getOpenId());
        if (cardList.isEmpty()) {
            return;
        }
        order.setDiscount1(Math.round((order.getTotalPrice() - order.getDiscount2()) * 0.1f));
    }

    private void updateAddress() {
        UserAddress userAddress = null;
        if (addressId != -1) {
            userAddress = addressRepository.queryByNumber(addressId);
        }
        if (null == userAddress) {
            userAddress = addressRepository.queryByFlag(true);
        }
        if (null == userAddress) {
            binding.orderAddress.setText(Constants.EMPTY);
            binding.userInfo.setText(Constants.EMPTY);
            binding.configAddress.setVisibility(View.VISIBLE);
        } else {
            binding.orderAddress.setText(mActivity.getResources()
                .getString(R.string.address_info, userAddress.getRegion(), userAddress.getAddress()));
            binding.userInfo.setText(
                mActivity.getString(R.string.user_info, userAddress.getName(), userAddress.getPhone()));
            binding.configAddress.setVisibility(View.INVISIBLE);
            order.setUserAddress(userAddress);
            addressId = userAddress.getAddressId();
        }
    }

    private void initOrderItems() {
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        binding.rvOrderItems.setLayoutManager(layoutManager);
        binding.rvOrderItems.setAdapter(orderItemAdapter);
        orderItemAdapter.refresh(orderItems);
        binding.rvOrderItems.post(new Runnable() {
            @Override
            public void run() {
                int orderItemsMaxHeight = getItemsMaxHeight();
                if (binding.rvOrderItems.getHeight() > orderItemsMaxHeight) {
                    RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) binding.rvOrderItems.getLayoutParams();
                    layoutParams.height = orderItemsMaxHeight;
                    binding.rvOrderItems.setLayoutParams(layoutParams);
                }
            }
        });
    }

    private int getItemsMaxHeight() {
        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.heightPixels - 1200;
    }

    private void updateDeliveredTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        String date = simpleDateFormat.format(new Date().getTime() + 1000 * 60);
        binding.deliveredTime.setText(mActivity.getString(R.string.delivered_time, date));
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_base_back:
                mActivity.onBackPressed();
                break;
            case R.id.rl_address:
                User user = new UserRepository().getCurrentUser();
                if (user == null) {
                    Toast
                        .makeText(mActivity, mActivity.getString(R.string.please_sign_first),
                            Toast.LENGTH_SHORT)
                        .show();
                    break;
                }
                Intent intent = new Intent(mActivity, AddressListActivity.class);
                intent.putExtra(KeyConstants.FROM_PAGE_CODE, Constants.ADDRESS_RESULT_CODE);
                mActivity.startActivityForResult(intent, Constants.ADDRESS_RESULT_CODE);
                break;
            case R.id.delivered_time:
                updateDeliveredTime();
                break;
            case R.id.submit_order:
                UserAddress useraddress = addressRepository.queryByFlag(true);
                if (null == useraddress) {
                    Toast
                        .makeText(mActivity, mActivity.getString(R.string.please_select_address),
                            Toast.LENGTH_SHORT)
                        .show();
                    break;
                }
                OrderRepository orderRepository = new OrderRepository();
                orderRepository.insert(order, orderItems);
                if (null != coupon) {
                    coupon.setStatus(false);
                    couponRepository.update(coupon);
                }
                CashierActivity.startCashierActivity(mActivity, order.getOrderId());
                MessagingUtil.saveNotificationMessage(mActivity, MessageConstants.COLLECTION_INTENT_ORDERLIST,
                    mActivity.getString(R.string.order_status), mActivity.getString(R.string.order_status_submit),
                    mActivity.getRestId(), new RestaurantRepository().queryByNumber(mActivity.getRestId()).getLogo());
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Constants.ADDRESS_RESULT_CODE == requestCode) {
            if (null != data) {
                int addressId = data.getIntExtra(KeyConstants.ADDRESS_ID, -1);
                if (-1 != addressId) {
                    this.addressId = addressId;
                }
            }
            updateAddress();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}
