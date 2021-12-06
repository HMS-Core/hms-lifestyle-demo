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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.huawei.hms.image.vision.crop.CropLayoutView;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.secure.android.common.util.LogsUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PhotoEditActivity extends BaseActivity {
    private String inFilePath;

    private String outFilePath;

    private ImageView imageView;

    private RelativeLayout stickerScreenshot;

    private CropLayoutView cropLayoutView;

    private Bitmap bitmap;

    private String rootPath = "";

    private File filePic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        init();
        getDataFromIntent();
        cropLayoutView = findViewById(R.id.cropImageView);
        stickerScreenshot = findViewById(R.id.stikcerScreenShot);
        imageView = findViewById(R.id.image);

        stickerScreenshot.setDrawingCacheEnabled(true);
        stickerScreenshot.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        stickerScreenshot.layout(0, 0, stickerScreenshot.getMeasuredWidth(), stickerScreenshot.getMeasuredHeight());
        stickerScreenshot.buildDrawingCache();

        getDiskBitmap(inFilePath);
        imageView.setImageBitmap(bitmap);

        cropLayoutView.setVisibility(View.VISIBLE);
        Bitmap croppedImage = ((BitmapDrawable) (imageView).getDrawable()).getBitmap();
        imageView.setVisibility(View.GONE);
        cropLayoutView.setImageBitmap(croppedImage);
        setStatusColor();
    }

    private void setStatusColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getColor(android.R.color.black));
        }
    }

    private void init() {
        try {
            rootPath = getBaseContext().getFilesDir().getPath() + "/vgmap/";
            copyAssetsFileToDirs(getBaseContext(), "vgmap", rootPath);

        } catch (Exception e) {
            LogsUtil.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * copyAssetsFileToDirs
     *
     * @param context context
     * @param oldPath oldPath
     * @param newPath newPath
     * @return boolean
     */
    public static boolean copyAssetsFileToDirs(Context context, String oldPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                if (file.exists() || file.mkdirs()) {
                    for (String fileName : fileNames) {
                        copyAssetsFileToDirs(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                    }
                }
            } else {
                is = context.getAssets().open(oldPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
        return true;
    }

    private Bitmap getDiskBitmap(String pathString) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            File file = new File(pathString);
            if (file.exists()) {
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(pathString, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        inFilePath = intent.getStringExtra(KeyConstants.EXTRA_CROP_FILE_PATH_IN);
        outFilePath = intent.getStringExtra(KeyConstants.EXTRA_CROP_FILE_PATH_OUT);
    }

    /**
     * set Result Uri
     *
     * @param outPath set the uri to last activity
     */
    protected void setResultUri(String outPath) {
        setResult(RESULT_OK, new Intent().putExtra(KeyConstants.EXTRA_OUTPUT_URI, outPath));
    }

    /**
     * button onClick
     *
     * @param view view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save_crop:
                Bitmap croppedImage = cropLayoutView.getCroppedImage();
                bitmap = croppedImage;
                outFilePath = save();
                setResultUri(outFilePath);
                finish();
                break;
            default:
                break;
        }
    }

    private String save() {
        FileOutputStream fos = null;
        try {
            filePic = new File(outFilePath);
            fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            Log.d("IOException", ioException.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return filePic.getCanonicalPath();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
