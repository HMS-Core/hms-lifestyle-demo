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

import static com.huawei.hmscore.industrydemo.constants.KitConstants.FIDO_FACE;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.FIDO_FINGER;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.ML_BANKCARD;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.constants.MessageConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityCashierBinding;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.repository.AppConfigRepository;
import com.huawei.hmscore.industrydemo.repository.OrderRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.MessagingUtil;
import com.huawei.hmscore.industrydemo.utils.TimeUtil;

import java.util.Date;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/18]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CashierActivity extends BasePaySettingActivity implements View.OnClickListener {
    private static final String TAG = CashierActivity.class.getSimpleName();

    private ActivityCashierBinding binding;

    private int orderId;

    private OrderRepository orderRepository;

    private AppConfigRepository mAppConfigRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cashier);
        orderId = getIntent().getIntExtra(KeyConstants.ORDER_ID, 0);
        orderRepository = new OrderRepository();
        mAppConfigRepository = new AppConfigRepository();
        setKitList(new String[] {FIDO_FACE, FIDO_FINGER, ML_BANKCARD});
        initView();
    }

    private void initView() {
        binding.lTitle.tvBaseTitle.setText(getString(R.string.cashier));
        binding.needToPay
            .setText(getString(R.string.need_to_pay, orderRepository.queryByOrderI(orderId).getActualPrice()));
        if (mAppConfigRepository.getBooleanValue(KeyConstants.SETTING_PAY_FACE_KEY, false)) {
            binding.brushFacePayment.setVisibility(View.VISIBLE);
        } else {
            binding.brushFacePayment.setVisibility(View.GONE);
        }
        if (mAppConfigRepository.getBooleanValue(KeyConstants.SETTING_PAY_FINGERPRINT_KEY, false)) {
            binding.fingerprintPayment.setVisibility(View.VISIBLE);
        } else {
            binding.fingerprintPayment.setVisibility(View.GONE);
        }
    }

    public static void startCashierActivity(Context context, int orderId) {
        Intent intent = new Intent(context, CashierActivity.class);
        intent.putExtra(KeyConstants.ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Order order = orderRepository.queryByOrderI(orderId);
        if (Constants.NOT_PAID != order.getStatus()) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                onBackPressed();
                break;
            case R.id.brush_face_payment:
                faceVerification();
                break;
            case R.id.fingerprint_payment:
                fingerprintVerification();
                break;
            case R.id.bank_card_payment:
                if (checkSDAndCameraPermission(Constants.CAMERA_PERMISSION_CODE)) {
                    bcrVerification();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.faceAuthHelp.getVisibility() == View.VISIBLE) {
            dismissAuthHelpTips();
        } else {
            super.onBackPressed();
        }
    }

    private void bcrVerification() {
        User user = new UserRepository().getCurrentUser();
        if (user != null) {
            AppConfigRepository appConfigRepository = new AppConfigRepository();
            String bcrNumber = appConfigRepository.getStringValue(user.getOpenId());
            if (null != bcrNumber) {
                paymentSucceed();
                return;
            }
        }
        startCaptureActivity();
    }

    @Override
    public String getBankConfirmBut() {
        return getString(R.string.card_pay);
    }

    @Override
    public void dealAuthSucceedResult(String flag) {
        paymentSucceed();
    }

    @Override
    protected void onAuthHelpTips(String msg) {
        binding.faceAuthHelp.setVisibility(View.VISIBLE);
        binding.tips.setText(msg);
    }

    @Override
    protected void dismissAuthHelpTips() {
        binding.faceAuthHelp.setVisibility(View.INVISIBLE);
    }

    private void paymentSucceed() {
        Order order = orderRepository.queryByOrderI(orderId);
        order.setTime(new Date().getTime());
        order.setStatus(Constants.HAVE_PAID);
        orderRepository.update(order);
        PaymentSucceedActivity.startPaymentSucceededActivity(CashierActivity.this, orderId);
        MessagingUtil.saveNotificationMessage(this, MessageConstants.COLLECTION_INTENT_ORDERLIST,
            getString(R.string.order_status), getString(R.string.order_status_paid), order.getRestId(),
            new RestaurantRepository().queryByNumber(order.getRestId()).getLogo());
        new Thread(() -> {
            try {
                Thread.sleep(TimeUtil.DELIVERY_TIME * 60 * 1000);
                order.setStatus(Constants.DELIVERED);
                orderRepository.update(order);
                MessagingUtil.saveNotificationMessage(this, MessageConstants.COLLECTION_INTENT_ORDERLIST,
                    getString(R.string.order_status), getString(R.string.order_status_delivered), order.getRestId(),
                    new RestaurantRepository().queryByNumber(order.getRestId()).getLogo());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            if (allGranted(grantResults)) {
                bcrVerification();
            } else {
                Log.i(TAG, "have no permissions");
            }
        }
    }
}