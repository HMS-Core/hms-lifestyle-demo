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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HwHex {
    public static final Charset DEFAULT_CHARSET;

    private static final char[] DIGITS_LOWER;

    private static final char[] DIGITS_UPPER;
    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
        DIGITS_LOWER = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        DIGITS_UPPER = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    /**
     * encode Hex
     * 
     * @param data byte[]
     * @param toLowerCase boolean
     * @return char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int var5 = 0; i < l; ++i) {
            out[var5++] = toDigits[(240 & data[i]) >>> 4];
            out[var5++] = toDigits[15 & data[i]];
        }
        return out;
    }

    /**
     * decode Hex
     * 
     * @param data char[]
     * @return byte[]
     * @throws Exception Odd number of characters.
     */
    public static byte[] decodeHex(char[] data) throws Exception {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new Exception("Odd number of characters.");
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;
            for (int j = 0; j < len; ++i) {
                int f = toDigit(data[j], j) << 4;
                ++j;
                f |= toDigit(data[j], j);
                ++j;
                out[i] = (byte) (f & 255);
            }
            return out;
        }
    }

    protected static int toDigit(char ch, int index) throws Exception {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        } else {
            return digit;
        }
    }

    /**
     * encode Hex String
     * 
     * @param data byte[]
     * @return String
     */
    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    /**
     * encode Hex
     * 
     * @param data byte[]
     * @return char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }
}
