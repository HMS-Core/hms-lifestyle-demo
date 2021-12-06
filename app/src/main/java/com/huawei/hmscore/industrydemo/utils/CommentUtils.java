/*
 *     Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hmscore.industrydemo.utils;

import java.util.List;

public class CommentUtils {
    private static final String STYPE_MP4 = ".mp4";

    private static final String STYPE_MOV = ".mov";

    private static final String STYPE_AVI = ".avi";

    public static boolean judgeIsHasVideo(String[] split) {
        boolean isHasVideo = false;
        for (int i = 0; i < split.length; i++) {
            if (split[i].endsWith(STYPE_MP4) || split[i].endsWith(STYPE_MOV) || split[i].endsWith(STYPE_AVI)) {
                isHasVideo = true;
            }
        }
        return isHasVideo;
    }

    public static List<String> addImageResource(List<String> imageUriList, String[] imageUri) {
        for (int i = 0; i < imageUri.length; i++) {
            if (!(imageUri[i].endsWith(STYPE_MP4) || imageUri[i].endsWith(STYPE_MOV) || imageUri[i].endsWith(STYPE_AVI))) {
                imageUriList.add(imageUri[i]);
            }
        }
        return imageUriList;
    }

    public static String[] splitToArray(String sourceUri) {
        return sourceUri.split("\\|");
    }
}
