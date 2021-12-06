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

package com.huawei.hmscore.industrydemo.page.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.page.BasePaySettingActivity;
import com.huawei.hmscore.industrydemo.repository.AppConfigRepository;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PaySettingActivity  extends BasePaySettingActivity {
    public final static String TAG = "PaySettingActivitys";
    private AppConfigRepository mAppConfigRepository;
    private Switch mFaceSwitch;
    private Switch mFingerSwitch;
    private final static int RESULTCODE_CHECK_PERMISSION = 1000;
    private LinearLayout mFaceAuthHelp;
    private TextView mFacialTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_setting_layout);
        initView();
    }

    private void initView() {
        mAppConfigRepository = new AppConfigRepository();
        ((TextView) (findViewById(R.id.pay_setting_title))).setText(R.string.pay_setting);
        findViewById(R.id.pay_setting_back).setOnClickListener(v -> finish());
        findViewById(R.id.setting_bank).setOnClickListener(v -> goToBankPayActivity());

        mFaceSwitch = findViewById(R.id.switch_face);

        mFaceSwitch.setChecked(mAppConfigRepository.getBooleanValue(KeyConstants.SETTING_PAY_FACE_KEY, false));
        mFaceSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                faceAuth(isChecked));

        mFingerSwitch = findViewById(R.id.switch_fingerprints);
        mFingerSwitch.setChecked(mAppConfigRepository.getBooleanValue(KeyConstants.SETTING_PAY_FINGERPRINT_KEY, false));
        mFingerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerAuth();
            }
        });
        mFaceAuthHelp = findViewById(R.id.face_auth_help);
        mFacialTips = findViewById(R.id.tips);
    }

    private void goToBankPayActivity() {
        Log.i(TAG,"goToBankPayActivity");
        if (checkSDAndCameraPermission(RESULTCODE_CHECK_PERMISSION)) {
            startCaptureActivity();
        }
    }

    private void faceAuth(boolean isChecked) {
        Log.i(TAG,"faceAuth isChecked = " + isChecked);
        if(!isChecked) {
            // 关闭人脸不需要校验
            mAppConfigRepository.setBooleanValue(KeyConstants.SETTING_PAY_FACE_KEY, false);
        } else {
            mFaceSwitch.setChecked(!isChecked);
            faceVerification();
        }
    }

    private void fingerAuth() {
        boolean isChecked = mFingerSwitch.isChecked();
        Log.i(TAG,"fingerAuth isChecked = " + isChecked);
        if(!isChecked) {
            // 关闭指纹不需要校验
            mAppConfigRepository.setBooleanValue(KeyConstants.SETTING_PAY_FINGERPRINT_KEY, false);
        } else {
            mFingerSwitch.setChecked(!isChecked);
            fingerprintVerification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULTCODE_CHECK_PERMISSION) {
            if (allGranted(grantResults)) {
                startCaptureActivity();
            } else {
                Log.i(TAG,"no permission");
            }
        }
    }

    @Override
    public void dealAuthSucceedResult(String flag) {
        if(KeyConstants.SETTING_PAY_FINGERPRINT_KEY.equals(flag)) {
            Toast.makeText(this,R.string.pay_auth_success,Toast.LENGTH_SHORT).show();
            mFingerSwitch.setChecked(true);
            mAppConfigRepository.setBooleanValue(KeyConstants.SETTING_PAY_FINGERPRINT_KEY, true);
        } else if(KeyConstants.SETTING_PAY_FACE_KEY.equals(flag)) {
            showResult(getString(R.string.pay_auth_success));
            mFaceSwitch.setChecked(true);
            mAppConfigRepository.setBooleanValue(KeyConstants.SETTING_PAY_FACE_KEY, true);
        }
    }

    @Override
    protected void onAuthHelpTips(String msg) {
        mFaceAuthHelp.setVisibility(View.VISIBLE);
        mFacialTips.setText(msg);
    }

    @Override
    protected void dismissAuthHelpTips() {
        mFaceAuthHelp.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mFaceAuthHelp.getVisibility() == View.VISIBLE) {
            dismissAuthHelpTips();
        } else {
            super.onBackPressed();
        }
    }
}
