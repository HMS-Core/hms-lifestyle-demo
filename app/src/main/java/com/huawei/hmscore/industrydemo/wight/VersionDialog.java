/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021 All rights reserved.
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

package com.huawei.hmscore.industrydemo.wight;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.huawei.hmscore.industrydemo.R;

/**
 * Base Dialog
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class VersionDialog {
    /**
     * CONFIRM_BUTTON
     */
    public static final String CONFIRM_BUTTON = "ConfirmButton";

    /**
     * CONTENT
     */
    public static final String CONTENT = "Content";

    /**
     * CANCEL_BUTTON
     */
    public static final String CANCEL_BUTTON = "CancelButton";

    private final AlertDialog dialog;

    private final TextView btnConfirm;

    public VersionDialog(@NonNull Context context, boolean updateFlag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.diag_version, null);
        builder.setView(view);

        builder.setCancelable(false);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.version);
        builder.setCancelable(true);

        ImageView btnClose = view.findViewById(R.id.imageClose);
        btnClose.setOnClickListener(v -> dismiss());

        btnConfirm = view.findViewById(R.id.diag_version_view);
        if (updateFlag) {
            btnConfirm.setText(R.string.is_the_latest_version);
            btnConfirm.setOnClickListener(v -> dismiss());
        } else {
            btnConfirm.setText(R.string.found_new_version);
        }

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * close the dialog
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * set the listener for the confirm button
     *
     * @param listener set the listener for the confirm button
     */
    public void setConfirmListener(TextView.OnClickListener listener) {
        btnConfirm.setOnClickListener(listener);
    }

    /**
     * Show the dialog
     */
    public void show() {
        dialog.show();
    }


    public static String getConfirmButton() {
        return CONFIRM_BUTTON;
    }

    public static String getCONTENT() {
        return CONTENT;
    }

    public static String getCancelButton() {
        return CANCEL_BUTTON;
    }
}
