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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.AudioFocusType;
import com.huawei.hms.ads.splash.SplashAdDisplayListener;
import com.huawei.hms.ads.splash.SplashView;
import com.huawei.hmscore.industrydemo.MainActivity;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivitys";

    private static final String AD_ID = "testq6zq98hecj";

    private static final int AD_TIMEOUT = 5000;

    private static final int MSG_AD_TIMEOUT = 1001;

    private boolean hasPaused = false;

    private final Handler timeoutHandler = new Handler(msg -> {
        if (SplashActivity.this.hasWindowFocus()) {
            jump();
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_splash);
        setBackGround();
        loadAd();
    }

    private void setBackGround() {
        if(!Constants.LANGUAGE_ZH.equals(SystemUtil.getLanguage())) {
            View layout = findViewById(R.id.cur_layout);
            layout.setBackgroundResource(R.drawable.x_splash_en);
        }
    }

    private static class MySplashAdDisplayListener extends SplashAdDisplayListener {
        @Override
        public void onAdShowed() {
            // Call this method when an ad is displayed.
            Log.i(TAG, "onAdShowed");
        }

        @Override
        public void onAdClick() {
            // Call this method when an ad is clicked.
            Log.i(TAG, "onAdClick");
        }
    }

    private void loadAd() {
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        AdParam adParam = new AdParam.Builder().build();
        Log.i(TAG, "loadAd");
        SplashView.SplashAdLoadListener splashAdLoadListener = new SplashView.SplashAdLoadListener() {
            @Override
            public void onAdLoaded() {
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i(TAG, "onAdFailedToLoad");
                jump();
            }

            @Override
            public void onAdDismissed() {
                Log.i(TAG, "onAdDismissed");
                jump();
            }
        };
        SplashView splashView = findViewById(R.id.splash_ad_view);
        splashView.setAdDisplayListener(new MySplashAdDisplayListener());
        splashView.setLogo(findViewById(R.id.logo_area), View.VISIBLE);

        splashView.setAudioFocusType(AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE);
        splashView.load(AD_ID, orientation, adParam, splashAdLoadListener);
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        timeoutHandler.sendEmptyMessageDelayed(MSG_AD_TIMEOUT, AD_TIMEOUT);
    }

    private void jump() {
        if (!hasPaused) {
            hasPaused = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            Handler mainHandler = new Handler();
            mainHandler.postDelayed(this::finish, 1000);
        }
    }

    @Override
    protected void onStop() {
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT);
        hasPaused = true;
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hasPaused = false;
        jump();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
