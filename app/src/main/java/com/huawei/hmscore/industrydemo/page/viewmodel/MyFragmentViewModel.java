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

package com.huawei.hmscore.industrydemo.page.viewmodel;

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.WEB_URL;
import static com.huawei.hmscore.industrydemo.utils.SystemUtil.isLogin;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.base.BaseFragmentViewModel;
import com.huawei.hmscore.industrydemo.page.OrderListActivity;
import com.huawei.hmscore.industrydemo.page.PersonalInfoActivity;
import com.huawei.hmscore.industrydemo.page.VoucherMgtActivity;
import com.huawei.hmscore.industrydemo.page.activity.MessageInfoActivity;
import com.huawei.hmscore.industrydemo.page.activity.SystemSettingActivity;
import com.huawei.hmscore.industrydemo.page.activity.WebViewActivity;
import com.huawei.hmscore.industrydemo.page.fragment.MyFragment;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class MyFragmentViewModel extends BaseFragmentViewModel<MyFragment> {
    private final static String TAG = MyFragmentViewModel.class.getSimpleName();

    private LinearLayout voucherManagement;

    private LinearLayout messageBox;

    private LinearLayout orderCenter;

    /**
     * constructor
     *
     * @param myFragment Fragment object
     */
    public MyFragmentViewModel(MyFragment myFragment) {
        super(myFragment);
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.layout_personal_information).setOnClickListener(mFragment);
        voucherManagement = view.findViewById(R.id.layout_voucher_management);
        messageBox = view.findViewById(R.id.layout_message_box);
        view.findViewById(R.id.layout_settings).setOnClickListener(mFragment);
        orderCenter = view.findViewById(R.id.layout_order_center);
        view.findViewById(R.id.layout_survey).setOnClickListener(mFragment);

        TextView title = view.findViewById(R.id.tv_base_title);
        title.setText(R.string.user_center);
        title.setTextColor(0xff272727);
        view.findViewById(R.id.iv_base_back).setVisibility(View.GONE);
    }

    public void initData() {
        if (isLogin()) {
            voucherManagement.setVisibility(View.VISIBLE);
            voucherManagement.setOnClickListener(mFragment);
            messageBox.setVisibility(View.VISIBLE);
            messageBox.setOnClickListener(mFragment);
            orderCenter.setVisibility(View.VISIBLE);
            orderCenter.setOnClickListener(mFragment);
        }
    }

    @Override
    public void onClickEvent(int viewId) {
        BaseActivity activity = (BaseActivity) mFragment.requireActivity();
        switch (viewId) {
            case R.id.layout_personal_information:
                activity.startActivity(new Intent(activity, PersonalInfoActivity.class));
                break;
            case R.id.layout_voucher_management:
                activity.startActivity(new Intent(activity, VoucherMgtActivity.class));
                break;
            case R.id.layout_message_box:
                activity.startActivity(new Intent(activity, MessageInfoActivity.class));
                break;
            case R.id.layout_settings:
                activity.startActivity(new Intent(activity, SystemSettingActivity.class));
                break;
            case R.id.layout_order_center:
                activity.startActivity(new Intent(activity, OrderListActivity.class));
                break;
            case R.id.layout_survey:
                // Used for Public test, it should be removed before release
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(WEB_URL, mFragment.getString(R.string.survey_link));
                intent.putExtra("useBrowser", true);
                intent.putExtra("isShowFloat", false);
                activity.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {

    }
}
