/*
    Copyright 2020-2021. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.hmscore.industrydemo.inteface;

import com.huawei.hms.support.account.result.AuthAccount;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public interface OnLoginListener {

    /**
     * Inform the login is successful
     *
     * @param authAccount The login huawei ID information
     */
    void loginSuccess(AuthAccount authAccount);

    /**
     * Inform the login is failed
     *
     * @param errorMsg The login huawei ID information
     */
    void loginFailed(String errorMsg);
}
