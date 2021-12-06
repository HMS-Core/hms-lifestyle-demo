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
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/28]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class TakeawayScrollView extends NestedScrollView {

    private float interceptY;

    public TakeawayScrollView(@NonNull Context context) {
        super(context);
    }

    public TakeawayScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TakeawayScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                interceptY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() < interceptY && canScrollVertically(1)) {
                    interceptY = ev.getY();
                    return true;
                }
                interceptY = ev.getY();
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
