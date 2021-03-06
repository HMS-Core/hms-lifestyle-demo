/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.wallet.util;

import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-encryption utility class.
 *
 * @since 2019-12-12
 */
public class AESUtils {
    private static final String TAG = AESUtils.class.getSimpleName();

    /**
     * AES-GCM encryption.
     *
     * @param plainData the data to be encrypted.
     * @param secretKeyStr encryption secret key.
     * @param iv encryption random iv.
     * @return the encrypted string.
     */
    public static String encryptByGcm(String plainData, String secretKeyStr, byte[] iv) {
        try {
            byte[] secretKeyByte = secretKeyStr.getBytes(StandardCharsets.UTF_8);
            byte[] plainByte = plainData.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = new SecretKeySpec(secretKeyByte, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            AlgorithmParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(1, secretKey, spec);
            byte[] fBytes = cipher.doFinal(plainByte);
            return HwHex.encodeHexString(fBytes);
        } catch (Exception e) {
            AgcUtil.reportException(TAG, e);
            return "";
        }
    }

    /**
     * encryption random iv.
     *
     * @param size iv length
     * @return encryption the byte array.
     */
    public static byte[] getIvByte(int size) {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] bytes = new byte[size];
            sr.nextBytes(bytes);
            return bytes;
        } catch (NoSuchAlgorithmException e) {
            AgcUtil.reportException(TAG, e);
        }
        return null;
    }
}
