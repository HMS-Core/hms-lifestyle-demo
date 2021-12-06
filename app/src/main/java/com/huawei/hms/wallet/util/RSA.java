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

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

    private static final int BASE_LENGTH = 128;

    private static final int LOOKUP_LENGTH = 64;

    private static final int TWENTY_FOUR_BIT_GROUP = 24;

    private static final int EIGHT_BIT = 8;

    private static final int SIXTEEN_BIT = 16;

    private static final int FOUR_BYTE = 4;

    private static final int SIGN = -128;

    private static final char PAD = '=';

    private static final byte[] BASE64_ALPHABET = new byte[BASE_LENGTH];

    private static final char[] LOOK_UP_BASE64_ALPHABET = new char[LOOKUP_LENGTH];

    /**
     * Signature algorithm.
     */
    private static final String SIGN_ALGORITHMS256 = "SHA256WithRSA";

    private static final String RSA_ECB_OAEP_SHA256_ALGORITHM = "RSA/ECB/OAEPwithSHA-256andMGF1Padding";

    /**
     * Sign content.
     *
     * @param content data to be signed.
     * @param privateKey merchant's private key.
     * @return Signed value.
     */
    public static String sign(String content, String privateKey, String signType) {
        String charset = "utf-8";
        try {
            PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(privatePKCS8);
            java.security.Signature signatureObj = java.security.Signature.getInstance(SIGN_ALGORITHMS256);
            signatureObj.initSign(priKey);
            signatureObj.update(content.getBytes(charset));
            byte[] signed = signatureObj.sign();
            return Base64.encodeToString(signed, Base64.DEFAULT);
        } catch (Exception ex) {
            Log.i("RSA", "SIGN Fail");
        }

        return "";
    }

    /**
     * encrypt bytes： src data
     *
     * @param bytes bytes
     * @param publicKey publicKey
     * @param algorithm algorithm
     * @param input_charset input_charset
     * @return encoded
     * @throws Exception
     */
    public static String encrypt(byte[] bytes, String publicKey, String algorithm, String input_charset)
        throws Exception {
        Key key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        if (CommonUtil.isNull(input_charset)) {
            input_charset = "utf-8";
        }
        byte[] b1 = cipher.doFinal(bytes);
        return Base64Hw.encode(b1);
    }

    /**
     * getPublicKey
     *
     * @param key key(by Base64)
     * @throws Exception
     */
    private static PublicKey getPublicKey(String key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Hw.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
