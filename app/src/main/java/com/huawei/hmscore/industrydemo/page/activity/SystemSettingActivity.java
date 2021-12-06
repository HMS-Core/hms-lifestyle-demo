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

import static com.huawei.hmscore.industrydemo.utils.SystemUtil.isLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.repository.AppConfigRepository;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mAbountUs;
    private LinearLayout mPrivacy;
    private LinearLayout mVersionUpdate;
    private ImageView mBack;
    private LinearLayout mSettingPay;
    private Switch mSettingNotification;
    private AppConfigRepository mAppConfigRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        mAbountUs.setOnClickListener(this);
        mPrivacy.setOnClickListener(this);
        mVersionUpdate.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mSettingPay.setOnClickListener(this);
    }

    private void initData() {
        mAppConfigRepository = new AppConfigRepository();
        initContentSwitch(KeyConstants.NOTIFICATION);
    
        if (isLogin()) {
            findViewById(R.id.rl_setting_messageinfo).setVisibility(View.VISIBLE);
            mSettingPay.setVisibility(View.VISIBLE);
        }
    }

    private void initContentSwitch(String key) {
        mSettingNotification.setChecked(mAppConfigRepository.getBooleanValue(key, true));
        mSettingNotification.setOnCheckedChangeListener(
            (buttonView, isChecked) -> mAppConfigRepository.setBooleanValue(key, isChecked));
    }

    private void initView() {
        mAbountUs = findViewById(R.id.tv_setting_aboutus);
        mSettingNotification = findViewById(R.id.sw_messageinfo_item);
        mPrivacy = findViewById(R.id.tv_setting_privacy);
        mVersionUpdate = findViewById(R.id.tv_setting_versionupdate);
        mBack = findViewById(R.id.iv_setting_close);
        mSettingPay = findViewById(R.id.tv_setting_pay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_aboutus:
                startActivity(new Intent(this, ContactUsActivity.class));
                break;
            case R.id.tv_setting_privacy:
                Intent intent = new Intent(this, PrivacyActivity.class);
                intent.putExtra("innerFlag",1);
                intent.putExtra("isFromSetting",true);
                startActivity(intent);
                break;
            case R.id.tv_setting_pay:
                startActivity(new Intent(this, PaySettingActivity.class));
                break;
            case R.id.iv_setting_close:
                finish();
                break;
            case R.id.tv_setting_versionupdate:
                SystemUtil.checkForUpdates(this);
                break;

        }
    }
}
