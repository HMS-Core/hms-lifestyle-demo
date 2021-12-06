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

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.entity.Comment;
import com.huawei.hmscore.industrydemo.entity.Images;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.inteface.CommentListener;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.DatabaseUtil;
import com.huawei.hmscore.industrydemo.utils.PictureUtil;
import com.huawei.hmscore.industrydemo.viewadapter.CommentViewAdapter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener, CommentListener {

    public final static String TAG = "CommentActivitys";
    private View mSelectPictureView;
    private View mSelectPictureEmptyView;

    private View mTakePictureView;
    private View mChooseFromGallery;

    private static final String SELECT_GALLERY_PIC_TYPE = "image/*";

    private static final String SELECT_GALLERY_MINI_TYPE = "image/* video/*";

    private final static int RESULTCODE_FOR_LESS5 = 1001;

    /**
     * 照相机结果码
     */
    public final static int RESULTCODE_FORM_CAMERA = 1003;

    /**
     * 库的结果代码
     */
    public final static int RESULTCODE_FOR_GALLERY = 1004;

    public final static int RESULTCODE_CROP_IMAGE = 1005;

    public final static int RESULTCODE_SELECT_RESTAURANT = 1006;

    public final static int IMAGES_LIMIT = 4;

    private Uri mPicUri;

    private List<Images> mImageList = new ArrayList<>();

    private EditText mCommentEdit;
    private RatingBar mRatingBar;
    private TextView mRatingResultText;

    private TextView mCommitBut;
    private final static float BUTTON_ENABLE = 0.3f;
    private final static float BUTTON_ABLE = 1.0f;

    private int mRestId;
    private String mRestName;

    private CommentViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mSelectRestaurantText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        initView();
    }

    private void initView() {
        mSelectPictureView = findViewById(R.id.select_picture_view);
        mSelectPictureEmptyView = findViewById(R.id.select_empty_view);
        mTakePictureView = findViewById(R.id.take_picture);
        mChooseFromGallery = findViewById(R.id.choose_from_gallery);
        mSelectRestaurantText = findViewById(R.id.select_restaurant_text);

        mSelectPictureEmptyView.setOnClickListener(this);
        mChooseFromGallery.setOnClickListener(this);
        mTakePictureView.setOnClickListener(this);
        findViewById(R.id.select_restaurant_but).setOnClickListener(this);
        findViewById(R.id.cancel_text).setOnClickListener(this);
        mRecyclerView = findViewById(R.id.add_comment_view);
        mCommentEdit = findViewById(R.id.ed_evaluate);
        mRatingBar = findViewById(R.id.rating_bar);
        mRatingResultText = findViewById(R.id.rating_result_text);
        mCommitBut = findViewById(R.id.tv_evaluate);
        mCommitBut.setOnClickListener(this);
        mCommitBut.setClickable(false);
        mCommitBut.setAlpha(BUTTON_ENABLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setRatingLevel(rating);
            }
        });

        mImageList.add(new Images());
        mAdapter = new CommentViewAdapter(this, mImageList, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mCommentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateReleaseButton();
            }
        });
    }

    private void updateReleaseButton() {
        String text = mCommentEdit.getText().toString();
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(mRestName)) {
            mCommitBut.setClickable(false);
            mCommitBut.setAlpha(BUTTON_ENABLE);
        } else {
            mCommitBut.setClickable(true);
            mCommitBut.setAlpha(BUTTON_ABLE);
        }
    }


    private void setRatingLevel(float rating) {
        Log.i(TAG,"setRatingLevel rating = " + rating);
        String ratingText = "";
        mRatingResultText.setVisibility(View.VISIBLE);
        if(rating >= 5.0f) {
            ratingText = getString(R.string.comment_rating_leval5);
        } else if(rating >= 4.0f) {
            ratingText = getString(R.string.comment_rating_leval4);
        } else if(rating >= 3.0f) {
            ratingText = getString(R.string.comment_rating_leval3);
        } else if(rating >= 2.0f) {
            ratingText = getString(R.string.comment_rating_leval2);
        } else if(rating >= 1.0f) {
            ratingText = getString(R.string.comment_rating_leval1);
        }
        mRatingResultText.setText(ratingText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_text:
                finish();
                break;
            case R.id.select_empty_view:
                hideSelectPictureView();
                break;
            case R.id.take_picture:
                startCamare();
                break;
            case R.id.choose_from_gallery:
                startGallery();
                break;
            case R.id.tv_evaluate:
                commitComment();
                break;
            case R.id.select_restaurant_but:
                goToSelectRestaurant();
                break;
            default:
                break;
        }
    }

    private void goToSelectRestaurant() {
        Log.i(TAG, "goToSelectRestaurant");
        Intent intent = new Intent(this, SelectResturantActivity.class);
        startActivityForResult(intent, RESULTCODE_SELECT_RESTAURANT);
    }

    private void hideSelectPictureView() {
        mSelectPictureView.setVisibility(View.GONE);
    }

    private void showSelectPictureView() {
        mSelectPictureView.setVisibility(View.VISIBLE);
    }

    /**
     * 权限请求确认回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RESULTCODE_FOR_LESS5) {
            if (allGranted(grantResults)) {
                showSelectPictureView();
            } else {
                Log.i(TAG,"no permission");
            }
        }
    }

    /**
     * 检查SD和相机权限
     *
     * @param requestCode int
     * @return true/false
     */
    public boolean checkSDAndCameraPermission(int requestCode) {
        if (checkAndRequestPermission(this,
            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, requestCode)) {
            Log.i(TAG, "checkSDAndCameraPermission return true");
            return true;
        }
        Log.i(TAG, "checkSDAndCameraPermission return false");
        return false;
    }

    /**
     * 启动照相
     */
    private void startCamare() {
        mPicUri = PictureUtil.getFileUri(this);
        Log.i(TAG, "startCamare mPicUri");
        if (mPicUri == null) {
            return;
        }
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                String authority = getPackageName() + ".life.fileProvider";
                Uri tmpCropUri =
                    FileProvider.getUriForFile(CommentActivity.this, authority, new File(mPicUri.getPath()));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpCropUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);
            }
            startActivityForResult(intent, RESULTCODE_FORM_CAMERA);
        } catch (RuntimeException e) {
            Log.e(TAG, "RuntimeException startCamare :" + e.getMessage());
            hideSelectPictureView();
        } catch (Exception e) {
            Log.e(TAG, "Exception startCamare :" + e.getMessage());
            hideSelectPictureView();
        }
    }

    /**
     * 启动gallery
     */
    private void startGallery() {
        Log.i(TAG, "startGallery");
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getGallerySelectType());
        try {
            startActivityForResult(intent, RESULTCODE_FOR_GALLERY);
        } catch (RuntimeException e) {
            Log.e(TAG, "RuntimeException startGallery :" + e.getMessage());
            hideSelectPictureView();
        } catch (Exception e) {
            Log.e(TAG, "Exception  startGallery" + e.getMessage());
            hideSelectPictureView();
        }
    }

    private String getGallerySelectType() {
        for (Images images : mImageList) {
            if ("video".equals(images.getType())) {
                return SELECT_GALLERY_PIC_TYPE;
            }
        }
        return SELECT_GALLERY_MINI_TYPE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult " + requestCode + " resultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULTCODE_FOR_GALLERY:
                Log.i(TAG, "onActivityResult RESULTCODE_FOR_GALLERY");
                hideSelectPictureView();
                if (data != null && data.getData() != null) {
                    doCropImage(data.getData(), true);
                }
                break;
            case RESULTCODE_FORM_CAMERA:
                Log.i(TAG, "onActivityResult RESULTCODE_FORM_CAMERA");
                hideSelectPictureView();
                if (resultCode == RESULT_OK) {
                    doCropImage(mPicUri, false);
                }
                break;
            case RESULTCODE_CROP_IMAGE:
                Log.i(TAG, "onActivityResult RESULTCODE_CROP_IMAGE");
                if (data == null) {
                    return;
                }
                String path = data.getStringExtra(KeyConstants.EXTRA_OUTPUT_URI);
                File file = new File(path);
                if (file.exists()) {
                    addImageList(path, "");
                }
                break;
            case RESULTCODE_SELECT_RESTAURANT:
                Log.i(TAG, "onActivityResult RESULTCODE_SELECT_RESTAURANT");
                if (resultCode == RESULT_OK) {
                    dealSelectRestaurantResult(data);
                    updateReleaseButton();
                }
                break;
            default:
                break;
        }
    }

    private void dealSelectRestaurantResult(Intent data) {
        if (null != data) {
            mRestId = data.getIntExtra(KeyConstants.COMMENT_SELECT_ID, 0);
            mRestName = data.getStringExtra(KeyConstants.COMMENT_SELECT_NAME);
            if (!TextUtils.isEmpty(mRestName)) {
                mSelectRestaurantText.setText(mRestName);
            }
        }
    }


    private void goCropActivity(String currentUrl) {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra(KeyConstants.EXTRA_CROP_FILE_PATH_IN, currentUrl);
        String destDir = "";
        try {
            destDir = getFilesDir().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg";
        String path = destDir + fileName;
        intent.putExtra(KeyConstants.EXTRA_CROP_FILE_PATH_OUT, path);
        startActivityForResult(intent, RESULTCODE_CROP_IMAGE);
    }

    private void doCropImage(Uri uri, boolean isFromGallery) {
        Log.i(TAG, "doCropImage");
        if (isFromGallery) {
            Log.i(TAG, "from gallery");
            String viedoFilePathByUri = PictureUtil.getViedoFilePathByUri(uri, this);
            if (!TextUtils.isEmpty(viedoFilePathByUri)) {
                addImageList(viedoFilePathByUri, "video");
                return;
            }
            goCropActivity(PictureUtil.getPicFilePathByUri(uri, this));
        } else {
            goCropActivity(uri.getPath());
        }
    }

    @Override
    public void selectPicture() {
        if (checkSDAndCameraPermission(RESULTCODE_FOR_LESS5)) {
            showSelectPictureView();
        }
    }

    @Override
    public void refreshImageList(List<Images> imageList) {
        mImageList = imageList;
        mAdapter.notifyDataSetChanged();
    }

    private void addImageList(String path, String type) {
        Log.i(TAG, "refreshFile");
        if (mImageList.size() < IMAGES_LIMIT) {
            Images images = new Images();
            images.setPath(path);
            images.setType(type);
            mImageList.add(images);
        } else if (mImageList.size() == IMAGES_LIMIT) {
            mImageList.remove(0);
            Images images = new Images();
            images.setType(type);
            images.setPath(path);
            mImageList.add(images);
        }
        mAdapter.refreshList(mImageList);
    }

    private void commitComment() {
        Log.i(TAG, "commitComment");
        User user = new UserRepository().getCurrentUser();
        String openId = "";
        if (null != user && null != user.getHuaweiAccount()) {
            Log.i(TAG, "commitComment no login");
            openId = user.getOpenId();
        }
        Comment comment = new Comment();
        comment.setContent(mCommentEdit.getText().toString());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        comment.setDate(df.format(new Date()));
        comment.setRate(mRatingBar.getRating());
        comment.setRestId(mRestId);
        comment.setOpenid(openId);
        String imageUri = getImageUri();
        comment.setImgUri(imageUri);
        DatabaseUtil.getDatabase().commentDao().addComment(comment);
        setResult(RESULT_OK);
        finish();
    }

    private String getImageUri() {
        int imageSize = mImageList.size();
        if (imageSize == 0) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < imageSize; i++) {
                String path = mImageList.get(i).getPath();
                if (!TextUtils.isEmpty(path)) {
                    builder.append(path);
                    if (i != mImageList.size() - 1) {
                        builder.append("|");
                    }
                }
            }
            return builder.toString();
        }
    }
}
