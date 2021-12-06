/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hmscore.industrydemo.page.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huawei.hms.panorama.Panorama;
import com.huawei.hms.panorama.PanoramaInterface;
import com.huawei.hms.panorama.ResultCode;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.provider.LifeFileProvider;

import java.io.File;

public class PanaromaActivity extends BaseActivity implements View.OnTouchListener {
    private static final String LOG_TAG = "LocalDisplayActivity";

    private RelativeLayout mLayout;

    private View mPanoramaView;

    private PanoramaInterface.PanoramaLocalInterface mLocalInstance;
    private ImageView mBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doFullScreen();
        setContentView(R.layout.activity_panaroma);

        Intent intent = getIntent();
        if(null == intent){
            return;
        }
        String sourceUri = intent.getStringExtra("sourceUri");
        mLayout = findViewById(R.id.rl_panaroma);
        mBack = findViewById(R.id.iv_Restaurant_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLocalInstance = Panorama.getInstance().getLocalInstance(this);
        if (mLocalInstance == null) {
            logAndToast("mLocalInstance is null");
            return;
        }

        int ret = mLocalInstance.init();
        if (ret != ResultCode.SUCCEED) {
            logAndToast("mLocalInstance init failed " + ret);
            return;
        }
        initControlMode();
        doDisplaySpherical(sourceUri);
    }

    private void initControlMode() {
        mLocalInstance.setControlMode(PanoramaInterface.IMAGE_TYPE_SPHERICAL);
    }


    private void addViewToLayout() {
        mPanoramaView = mLocalInstance.getView();
        if (mPanoramaView == null) {
            logAndToast("getView failed");
            return;
        }

        mPanoramaView.setOnTouchListener(this);
        mLayout.addView(mPanoramaView);
    }

    private void doDisplaySpherical(String sourcePath) {
        String targetFilePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + sourcePath;
        Uri uriForFile = LifeFileProvider.getUriForFile(this, getPackageName() + ".life.fileProvider",
                new File(targetFilePath));

        Log.i("targetFilePath", "permission ok" + targetFilePath);
        int ret = mLocalInstance.setImage(uriForFile, PanoramaInterface.IMAGE_TYPE_SPHERICAL);
        if (ret != ResultCode.SUCCEED) {
            logAndToast("doDisplaySpherical setImage failed " + ret);
            return;
        }
        addViewToLayout();
    }


    private void doFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final Window win = getWindow();
        win.getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void logAndToast(String message) {
        Log.e(LOG_TAG, message);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mPanoramaView != null && mPanoramaView.equals(view)) {
            if (mLocalInstance != null) {
                mLocalInstance.updateTouchEvent(motionEvent);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocalInstance != null) {
            mLocalInstance.deInit();
        }
    }
}
