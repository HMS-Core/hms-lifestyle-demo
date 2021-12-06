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

package com.huawei.hmscore.industrydemo.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PictureUtil {

    public final static String TAG = "PictureUtil";

    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    /**
     * 获取文件uri
     *
     * @param context 上下文
     * @return 文件uri
     */
    public static Uri getFileUri(Context context) {
        String status = "";
        try {
            status = Environment.getExternalStorageState();
            if(status.equals(Environment.MEDIA_MOUNTED)) {
                Log.i(TAG, "getFileUri media mounted");
                String dirPath = "";
                File file = context.getExternalCacheDir();
                if (file != null) {
                    dirPath =  file.getCanonicalPath();
                    Log.i(TAG, "getFileUri getExternalCacheDir");
                } else {
                    dirPath = Environment.getExternalStorageDirectory().getPath() + "/" + context.getPackageName();
                    File temPicFileDir = new File(dirPath);
                    if (!temPicFileDir.exists() && !temPicFileDir.mkdir()) {
                        // 目录不存在，并且创建失败
                        dirPath  = "";
                        Log.i(TAG, "getFileUri create file fail");
                    }
                }
                if (!TextUtils.isEmpty(dirPath)) {
                    String path = dirPath + "/" + getPhotoFileName(false);
                    return Uri.fromFile(new File(path));
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "getExternalStorageState failed");
            return null;
        } catch (IOException e) {
            Log.i(TAG, "IOException " + e.getClass().getSimpleName());
            return null;
        }
        return null;
    }


    /**
     * 获取保存的文件名
     * @param isCrop 是否裁剪
     * @return String
     */
    private static String getPhotoFileName(boolean isCrop) {
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        if(isCrop) {
            return "Crop-" + dateFormat.format(date) + ".jpg";
        } else {
            return "Life-" + dateFormat.format(date) + ".jpg";
        }
    }

    /**
     * 根据文件名解析成bitmap
     *
     * @param fileName 文件名
     * @param inSampleSize inSampleSize
     * @return Bitmap
     */
    public static Bitmap getBitmapByPath(String fileName, int inSampleSize) {
        if (TextUtils.isEmpty(fileName)) {
            Log.i(TAG, "fileName is can not null");
            return null;
        }
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            Log.i(TAG, "the fileName is illegal");
            return null;
        }
        BitmapFactory.Options mOptions= new BitmapFactory.Options();
        Bitmap bitmap = null;
        try {
            mOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, mOptions);

            // 计算 inSampleSize
            if (inSampleSize <= 0) {
                mOptions.inSampleSize = 1;
            } else {
                mOptions.inSampleSize = inSampleSize;
            }

            // Decode bitmap with inSampleSize set
            mOptions.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileName, mOptions);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError");
        }
        return bitmap;
    }

    /**
     * 根据视频uri获取路径
     * @param uri uri
     * @param context 上下文
     * @return String
     */
    public static String getViedoFilePathByUri(Uri uri, Context context) {
        Log.i(TAG,"getViedoFilePathByUri");
        String path = "";
        Cursor cursor = null;
        String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};
        try {
            cursor = context.getContentResolver().query(uri, proj, null,null, null);
            if (cursor != null && cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(proj[1]));
                if(isFileTypeVideo(type)) {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(proj[0]));
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG,"getfilePathByUri SecurityException");
        } catch (RuntimeException e) {
            Log.e(TAG,"getfilePathByUri RuntimeException");
        } finally {
            if(null != cursor) {
                cursor.close();
            }
        }
        return path;
    }

    public static String getPicFilePathByUri(Uri uri, Context context) {
        Log.i(TAG,"getPicFilePathByUri");
        String path = "";
        Cursor cursor = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, proj, null,null, null);
            if (cursor != null && cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(proj[0]));
            }
        } catch (SecurityException e) {
            Log.e(TAG,"getfilePathByUri SecurityException");
        } catch (RuntimeException e) {
            Log.e(TAG,"getfilePathByUri RuntimeException");
        } finally {
            if(null != cursor) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * 判断是否是视频文件
     * @param mineType 类型
     * @return true or false
     */
    public static boolean isFileTypeVideo(String mineType) {
        return !TextUtils.isEmpty(mineType) &&mineType.toLowerCase(Locale.ROOT).startsWith("video");
    }

    /**
     * 获取视频文件的缩略图
     * @param filePath 文件路径
     * @return String
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap= null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            Log.e(TAG,"getVideoThumbnail IllegalArgumentException");
        } finally {
            retriever.release();
        }
        return bitmap;
    }
}
