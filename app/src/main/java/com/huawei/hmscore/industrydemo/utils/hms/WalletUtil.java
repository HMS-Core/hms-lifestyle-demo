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

package com.huawei.hmscore.industrydemo.utils.hms;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.hms.wallet.constant.WalletPassConstant;
import com.huawei.hms.wallet.pass.BarCode;
import com.huawei.hms.wallet.pass.CommonField;
import com.huawei.hms.wallet.pass.PassObject;
import com.huawei.hms.wallet.pass.PassStatus;
import com.huawei.hms.wallet.util.JweUtil;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Wallet Util
 * 
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class WalletUtil {
    private static final String TAG = WalletUtil.class.getSimpleName();

    public static final int SAVE_FLAG = 4567;

    public static Card card;

    public static String getJwe(Context context, Card card, Restaurant restaurant, String schema) {
        try {
            PassObject passObject = createCouponInstance(context, card, restaurant);
            // Generate a JWE.
            String jwe = JweUtil.generateJwe(AgcUtil.getAppId(context), passObject.toJson(),
                AgcUtil.getConfig().getValueAsString("card_private_key"));
            Log.d(TAG, "JWE String: " + jwe);
            return jwe;
        } catch (JSONException e) {
            AgcUtil.reportException(TAG, e);
            return "";
        }
    }

    private static PassObject createCouponInstance(Context context, @NonNull Card card, Restaurant restaurant)
        throws UnsupportedOperationException, JSONException {
        JSONObject qrCode = new JSONObject();
        qrCode.put("restid", String.valueOf(restaurant.getRestid()));
        qrCode.put("cardid", String.valueOf(card.getCardId()));
        WalletUtil.card = card;
        // For details about the PassObject definition, see the sample code for the client.
        PassObject.Builder passBuilder = PassObject.getBuilder();
        passBuilder.setStatus(PassStatus.getBuilder().setState(WalletPassConstant.PASS_STATE_ACTIVE).build())
            .setSerialNumber(card.getWalletId())
            .setOrganizationPassId(card.getWalletId()) // Instance ID
            .setPassStyleIdentifier("test1") // Model ID.
            .setPassTypeIdentifier(context.getString(R.string.wallet_card_service_id)) // Service ID
            .setBarCode(BarCode.getBuilder()
                .setText(context.getString(R.string.wallet_scan))
                .setValue(qrCode.toString())
                .setType("qrCod")
                .build());
        List<CommonField> commonFields = new ArrayList<>();

        CommonField logoField = CommonField.getBuilder().setKey("logo").setValue(restaurant.getLogo()).build();
        commonFields.add(logoField);

        CommonField merchantNameField =
            CommonField.getBuilder().setKey("merchantName").setValue(restaurant.getRestname()).build();
        commonFields.add(merchantNameField);

        CommonField memberNameField = CommonField.getBuilder()
            .setKey("memberName")
            .setLabel(context.getString(R.string.wallet_card_number))
            .setValue(String.valueOf(card.getCardId()))
            .build();
        commonFields.add(memberNameField);

        CommonField useType = CommonField.getBuilder().setKey("use_type").setValue("10").build();
        // 暂时不添加按钮 commonFields.add(useType);

        CommonField useButton = CommonField.getBuilder()
            .setKey("use_button")
            .setValue("{\"packageName\":\"com.huawei.hmscore.industrydemo\",\"appName\":\"Life\","
                + "\",\"className\":\"com.huawei.hmscore.industrydemo.MainActivity\"}")
            .setLabel("")
            .build();
        // 暂时不添加按钮 commonFields.add(useButton);

        passBuilder.addCommonFields(commonFields);
        // Create a loyalty card instance.
        return passBuilder.build();
    }
}
