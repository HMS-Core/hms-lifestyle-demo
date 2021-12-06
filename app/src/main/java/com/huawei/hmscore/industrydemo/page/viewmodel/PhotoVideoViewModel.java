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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.hmscore.industrydemo.MainApplication;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.page.activity.PhotoVideoViewActivity;
import com.huawei.hmscore.industrydemo.repository.ImageRepository;
import com.huawei.hmscore.industrydemo.utils.CommentUtils;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;
import com.huawei.hmscore.industrydemo.utils.TimeUtil;
import com.huawei.hmscore.industrydemo.viewadapter.PhotoPagerAdapter;
import com.huawei.hmscore.industrydemo.wight.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.SYSTEM_UI_FLAG_VISIBLE;
import static android.view.View.VISIBLE;

public class PhotoVideoViewModel extends BaseActivityViewModel<PhotoVideoViewActivity> implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    private boolean isUserTrackingTouch = false;
    private int startTime = -1;
    private int playbackTimeWhenTrackingTouch = 0;
    private boolean hasReported = false;
    private boolean isSuspend = false;
    private boolean isPlaying = false;
    private String videoUrl = "";
    private WisePlayer mWisePlayer;
    private ImageView mImageView;
    private SurfaceView mSurfaceView;
    private RelativeLayout mRlVideo;
    private TextView mNotice;
    private RelativeLayout mBufferRl;
    private SeekBar mSeekBar;
    private ImageView mPlayImg;
    private TextView mTotalTimeTv;
    private TextView mCurrentTimeTv;
    private RelativeLayout mRlPhoto;
    private PhotoView mIvPhoto;
    private int mImageId;
    private RelativeLayout mRlPhotoView;
    private CustomViewPager mVpPhoto;
    private String[] mImageUri;
    private int mPhotoType;
    private boolean mIsHasVideo;
    private int mRestId;
    private boolean isReady = false;
    private View mIvBack;
    private View mIvFullScreen;

    /**
     * constructor
     *
     * @param photoVideoViewActivity Activity object
     */
    public PhotoVideoViewModel(PhotoVideoViewActivity photoVideoViewActivity) {
        super(photoVideoViewActivity);
    }

    @Override
    public void initView() {
        initPlayer();
        mSurfaceView = mActivity.findViewById(R.id.sf_video);
        mRlVideo = mActivity.findViewById(R.id.rl_video);
        mRlPhoto = mActivity.findViewById(R.id.rl_photo);
        mNotice = mActivity.findViewById(R.id.video_notice);
        mBufferRl = mActivity.findViewById(R.id.buffer_rl);
        mCurrentTimeTv = mActivity.findViewById(R.id.current_time_tv);
        mTotalTimeTv = mActivity.findViewById(R.id.total_time_tv);
        mPlayImg = mActivity.findViewById(R.id.play_btn);
        mSeekBar = mActivity.findViewById(R.id.seek_bar);
        mIvPhoto = mActivity.findViewById(R.id.iv_photo);
        mRlPhotoView = mActivity.findViewById(R.id.rl_photoViewPager);
        mVpPhoto = mActivity.findViewById(R.id.vp_photo);
        mIvBack = mActivity.findViewById(R.id.iv_back);
        mIvFullScreen = mActivity.findViewById(R.id.fullscreen_btn);
        mIvPhoto.setOnClickListener(mActivity);
        mIvBack.setOnClickListener(mActivity);
        mIvFullScreen.setOnClickListener(mActivity);
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlayImg.setOnClickListener(v -> changePlayState());
    }

    private void initVideo() {
        List<Image> images = new ImageRepository().queryBuyRestIdAndImgType(mRestId, 3);
        if (mWisePlayer != null) {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.addCallback(this);
            mWisePlayer.setVideoType(0);
            mWisePlayer.setBookmark(10000);
            mWisePlayer.setCycleMode(1);
            changePlayState();
            loadPlayUrl(images);
            mWisePlayer.ready();
            mWisePlayer.setReadyListener(wisePlayer1 -> {
                wisePlayer1.seek(0);
                updatePlayView(mWisePlayer, 0);
            });
            mWisePlayer.setPlayEndListener(wisePlayer1 -> {
                updatePlayCompleteView();
            });
            mWisePlayer.setLoadingListener(new WisePlayer.LoadingListener() {
                @Override
                public void onLoadingUpdate(WisePlayer wisePlayer, int i) {
                    return;
                }

                @Override
                public void onStartPlaying(WisePlayer wisePlayer) {
                    mPlayImg.setImageResource(R.drawable.ic_pause);
                    mBufferRl.setVisibility(View.GONE);
                    int totalTime = wisePlayer.getDuration();
                    mTotalTimeTv.setText(TimeUtil.formatLongToTimeStr(totalTime));
                }
            });
            mWisePlayer.setView(mSurfaceView);
            mWisePlayer.start();
        }
        ;
    }

    private void loadPlayUrl(List<Image> images) {
        if (mPhotoType == 3 && mImageUri != null && mImageUri.length > 0) {
            videoUrl = mImageUri[0];
            mWisePlayer.setPlayUrl(mImageUri[0]);
        } else {
            if (images.size() > 0) {
                videoUrl = images.get(0).getImgAdd();
                mWisePlayer.setPlayUrl(images.get(0).getImgAdd());
            }
        }
    }

    public void updatePlayView(WisePlayer wisePlayer, int progress) {
        if (wisePlayer != null) {
            isReady = true;
            int totalTime = wisePlayer.getDuration();
            mSeekBar.setMax(totalTime);
            mTotalTimeTv.setText(TimeUtil.formatLongToTimeStr(totalTime));
            mCurrentTimeTv.setText(TimeUtil.formatLongToTimeStr(0));
            updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING, Constants.DELAY_MILLIS_500);
            mSeekBar.setProgress(progress);
            mPlayImg.setImageResource(R.drawable.ic_play);
            mBufferRl.setVisibility(View.GONE);
            wisePlayer.setPlayEndListener(mWisePlayer -> updatePlayCompleteView());
        }
    }

    private void changePlayState() {
        if (!isReady) {
            return;
        }
        if (isPlaying) {
            if (mWisePlayer != null) {
                mWisePlayer.pause();
            }
            isPlaying = false;
            updateViewHandler.removeCallbacksAndMessages(null);
            mPlayImg.setImageResource(R.drawable.ic_play);
        } else {
            if (mWisePlayer != null) {
                mWisePlayer.start();
            }
            isPlaying = true;
            updateViewHandler.sendEmptyMessage(Constants.PLAYING);
            mPlayImg.setImageResource(R.drawable.ic_pause);
        }
    }

    private Handler updateViewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == Constants.PLAYING) {
                if (!isUserTrackingTouch) {
                    updatePlayProgressView(mWisePlayer.getCurrentTime(), mWisePlayer.getBufferTime());
                    updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING, Constants.DELAY_MILLIS_500);
                    if (startTime == -1) {
                        startTime = mWisePlayer.getCurrentTime();
                    } else if (!hasReported
                            && (mWisePlayer.getCurrentTime() - startTime + playbackTimeWhenTrackingTouch >= 4000)) {
                        hasReported = true;
                    }
                }
            }
            return false;
        }
    });

    public void updatePlayProgressView(int progress, int bufferPosition) {
        mSeekBar.setProgress(progress);
        mSeekBar.setSecondaryProgress(bufferPosition);
        mCurrentTimeTv.setText(TimeUtil.formatLongToTimeStr(progress));
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.iv_back:
                if (!SystemUtil.isPortrait(mActivity)) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mIvFullScreen.setVisibility(VISIBLE);
                    mRlVideo.setLayoutParams(
                            new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    (int) mActivity.getResources().getDimension(R.dimen.video_size)));
                    mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_VISIBLE);
                    if (mWisePlayer != null) {
                        mWisePlayer.setSurfaceChange();
                    }

                } else {
                    mActivity.finish();
                }
                break;
            case R.id.iv_photo:
                mActivity.finish();
                break;
            case R.id.fullscreen_btn:
                setFullScreen();
                break;
            default:
                break;
        }
    }

    private void setFullScreen() {
        if (SystemUtil.isPortrait(mActivity)) {
            mIvFullScreen.setVisibility(GONE);
            mRlVideo.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mActivity
                    .getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            if (mWisePlayer != null) {
                mWisePlayer.setSurfaceChange();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    private void initPlayer() {
        if (MainApplication.getWisePlayerFactory() == null) {
            return;
        }
        mWisePlayer = MainApplication.getWisePlayerFactory().createWisePlayer();
    }

    public void pausePlay() {
        if (mWisePlayer != null) {
            if (isPlaying) {
                changePlayState();
            }
        }
    }

    public void onDestroy() {
        if (mWisePlayer != null) {
            mWisePlayer.stop();
            mWisePlayer.release();
        }
    }


    public void loadPicture(int imageId) {
        ImageRepository imageRepository = new ImageRepository();
        Image image = imageRepository.queryByImgId(imageId, 1);
        if (image != null) {
            Drawable bitmap = ContextCompat.getDrawable(mActivity, mActivity.getResources()
                    .getIdentifier(image.getImgAdd(), Constants.RESOURCE_TYPE_MIPMAP,
                            mActivity.getPackageName()));
            mIvPhoto.setImageDrawable(bitmap);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (mWisePlayer != null) {
            if (mSurfaceView != null) {
                mWisePlayer.setView(mSurfaceView);
            }
            if (isSuspend) {
                isSuspend = false;
                mWisePlayer.resume(PlayerConstants.ResumeType.KEEP);
            }
        }
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {


    }


    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isSuspend = true;
        isPlaying = false;
        mPlayImg.setImageResource(R.drawable.ic_play);
        if (mWisePlayer != null) {
            mWisePlayer.suspend();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserTrackingTouch = true;
        if (!hasReported && mWisePlayer != null) {
            playbackTimeWhenTrackingTouch += mWisePlayer.getCurrentTime() - startTime;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!hasReported) {
            startTime = seekBar.getProgress();
        }
        isUserTrackingTouch = false;
        if (mWisePlayer != null) {
            mWisePlayer.seek(seekBar.getProgress());
        }

        updateViewHandler.sendEmptyMessage(Constants.PLAYING);
    }

    public void updatePlayCompleteView() {
        mPlayImg.setImageResource(R.drawable.ic_play);
        isPlaying = false;
        mWisePlayer.reset();
        mWisePlayer.setPlayUrl(videoUrl);
        mSeekBar.setProgress(0);
        mWisePlayer.setVideoType(0);
        mWisePlayer.setBookmark(10000);
        mWisePlayer.setCycleMode(0);
        mWisePlayer.ready();

        updateViewHandler.removeCallbacksAndMessages(null);
    }

    public void initData(int photoType, int imageId, String imageUri, int photoPos, int restId) {
        mPhotoType = photoType;
        mRestId = restId;
        splitImageUrl(imageUri, photoPos);
        if (mPhotoType == 3) {
            mRlPhoto.setVisibility(View.GONE);
            mRlVideo.setVisibility(View.VISIBLE);
            mRlPhotoView.setVisibility(View.GONE);
            loadPicture(imageId);
        } else if (mPhotoType == 2) {
            mRlPhoto.setVisibility(View.VISIBLE);
            mRlVideo.setVisibility(View.GONE);
            mRlPhotoView.setVisibility(View.GONE);
            loadPicture(imageId);
        } else if (mPhotoType == 4) {
            mRlPhoto.setVisibility(View.GONE);
            mRlVideo.setVisibility(View.GONE);
            mRlPhotoView.setVisibility(View.VISIBLE);
        }
        initVideo();
    }

    private void splitImageUrl(String imageUri, int photoPos) {
        if (TextUtils.isEmpty(imageUri)) {
            return;
        }
        List<String> imageUriList = new ArrayList<>();
        mImageUri = CommentUtils.splitToArray(imageUri);
        CommentUtils.addImageResource(imageUriList, mImageUri);
        mVpPhoto.setAdapter(new PhotoPagerAdapter(mActivity, imageUriList, new PhotoPagerAdapter.OnViewItemClockListener() {
            @Override
            public void onItemClick(View view, int position) {
                mActivity.finish();
            }
        }));

        if (CommentUtils.judgeIsHasVideo(mImageUri)) {
            mVpPhoto.setCurrentItem(photoPos - 1);
        } else {
            mVpPhoto.setCurrentItem(photoPos);
        }
    }

}
