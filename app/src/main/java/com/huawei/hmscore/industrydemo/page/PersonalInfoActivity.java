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

import static com.huawei.hmscore.industrydemo.utils.SystemUtil.isLogin;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CANCEL_BUTTON;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CONTENT;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.inteface.OnLoginListener;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

import java.util.UUID;

/**
 * Personal Information
 * 
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/8]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PersonalInfoActivity extends BaseActivity {
    private ImageView ivHead;

    private TextView tvNickName;

    private ConstraintLayout personalMgt;

    private final RequestOptions option = new RequestOptions().circleCrop()
        .placeholder(R.mipmap.head_load)
        .error(R.mipmap.head_my)
        .signature(new ObjectKey(UUID.randomUUID().toString()))
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initView();
        initData();
    }

    private void initView() {
        TextView pageTitle = findViewById(R.id.tv_base_title);
        pageTitle.setText(R.string.personal_information);
        pageTitle.setTextColor(0xff323232);
        pageTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        findViewById(R.id.iv_base_back).setOnClickListener(v -> finish());
        setKitList(new String[] {KitConstants.ACCOUNT_LOGIN});
        ivHead = findViewById(R.id.user_icon);
        tvNickName = findViewById(R.id.nickname);
        personalMgt = findViewById(R.id.personal_mgt);
    }

    public void openAddressList(View view) {
        startActivity(new Intent(this, AddressListActivity.class));
    }

    public void signOut(View view) {
        Bundle data = new Bundle();
        data.putString(CONFIRM_BUTTON, getString(R.string.confirm));
        data.putString(CANCEL_BUTTON, getString(R.string.cancel));
        data.putString(CONTENT, getString(R.string.confirm_log_out));

        BaseDialog dialog = new BaseDialog(this, data, true);
        dialog.setConfirmListener(v -> {
            signOut(() -> {
                initData();
                Toast.makeText(this, R.string.log_out_success, Toast.LENGTH_SHORT).show();
            });
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void initData() {
        User currentUser = new UserRepository().getCurrentUser();
        if (currentUser != null) {
            AuthAccount huaweiAccount = currentUser.getHuaweiAccount();
            if (currentUser.getHuaweiAccount().getAvatarUri() == Uri.EMPTY) {
                Glide.with(this).load(R.mipmap.head_my).apply(option).into(ivHead);
            } else {
                Glide.with(this).load(huaweiAccount.getAvatarUriString()).apply(option).into(ivHead);
            }
            tvNickName.setText(huaweiAccount.getDisplayName());
            personalMgt.setVisibility(View.VISIBLE);
        } else {
            Glide.with(this).load(R.mipmap.head_my).apply(option).into(ivHead);
            tvNickName.setText(R.string.click_to_login);
            personalMgt.setVisibility(View.GONE);
        }
    }

    public void login(View view) {
        if (isLogin()) {
            return;
        }
        signIn(new OnLoginListener() {
            @Override
            public void loginSuccess(AuthAccount authAccount) {
                initData();
                Toast.makeText(PersonalInfoActivity.this, R.string.log_in_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loginFailed(String errorMsg) {
                Log.d(TAG, errorMsg);
            }
        });
    }
}