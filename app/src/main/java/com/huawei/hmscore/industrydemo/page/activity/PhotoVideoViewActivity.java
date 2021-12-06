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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.page.viewmodel.PhotoVideoViewModel;

public class PhotoVideoViewActivity extends BaseActivity implements View.OnClickListener {

    private PhotoVideoViewModel mPhotoVideoViewModel;
    private int mPhotoType;
    private int mImageId;
    private String mImgUri;
    private int mPhotoPos;
    private int mRestId;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideoview);
        Intent intent = getIntent();
        if(null == intent){
            return;
        }
        mPhotoType = intent.getIntExtra(KeyConstants.PHOTO_TYPE,2);
        mImageId = intent.getIntExtra(KeyConstants.IMAGE_ID,1);
        mImgUri = intent.getStringExtra(KeyConstants.IMAGE_UIR);
        mPhotoPos = intent.getIntExtra(KeyConstants.PHOTO_POSITION, 1);
        mRestId = intent.getIntExtra(KeyConstants.RESTAURANT_ID, 1);
        mPhotoVideoViewModel = new PhotoVideoViewModel(PhotoVideoViewActivity.this);
        mPhotoVideoViewModel.initView();
        mPhotoVideoViewModel.initData(mPhotoType, mImageId, mImgUri, mPhotoPos, mRestId);
        Log.i(TAG, "restId:" + mRestId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPhotoVideoViewModel.pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoVideoViewModel.onDestroy();
    }

    @Override
    public void onClick(View v) {
        mPhotoVideoViewModel.onClickEvent(v.getId());
    }
}
