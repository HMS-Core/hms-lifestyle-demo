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

import static com.huawei.hmscore.industrydemo.constants.KitConstants.ML_TRANSLATION;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.PANORAMA_FULLVIEW;
import static com.huawei.hmscore.industrydemo.constants.KitConstants.VIDEO_PLAY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ActivityTakeawayBinding;
import com.huawei.hmscore.industrydemo.page.viewmodel.TakeawayActivityViewModel;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/08]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class TakeawayActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTakeawayBinding binding;

    private TakeawayActivityViewModel viewModel;

    private int restId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_takeaway);
        restId = getIntent().getIntExtra(KeyConstants.RESTAURANT_ID, 0);
        viewModel = new TakeawayActivityViewModel(this);
        viewModel.initView();
        setKitList(new String[]{VIDEO_PLAY});

    }

    public ActivityTakeawayBinding getBinding() {
        return binding;
    }

    public int getRestId() {
        return restId;
    }

    @Override
    public void onClick(View v) {
        viewModel.onClickEvent(v.getId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    public static void startTakeawayActivity(Context context, int restId) {
        Intent intent = new Intent(context, TakeawayActivity.class);
        intent.putExtra(KeyConstants.RESTAURANT_ID, restId);
        context.startActivity(intent);
    }
}