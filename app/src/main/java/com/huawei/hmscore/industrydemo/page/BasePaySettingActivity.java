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

package com.huawei.hmscore.industrydemo.page;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnCallback;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnResult;
import com.huawei.hms.support.api.fido.bioauthn.CryptoObject;
import com.huawei.hms.support.api.fido.bioauthn.FaceManager;
import com.huawei.hms.support.api.fido.bioauthn.FingerprintManager;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.utils.BiometricAuthenticationUtil;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class BasePaySettingActivity extends BaseActivity {

    public final static String TAG = "BasePaySettingActivity";

    public void startCaptureActivity() {
        MLBcrCaptureConfig config = new MLBcrCaptureConfig.Factory()
                // Set the expected result type of bank card recognition.
                // MLBcrCaptureConfig.SIMPLE_RESULT: Recognize only the card number and effective date.
                // MLBcrCaptureConfig.ALL_RESULT: Recognize information such as the card number, effective date, card
                // issuing bank, card organization, and card type.
                .setResultType(MLBcrCaptureConfig.RESULT_ALL)
                // Set the screen orientation of the plugin page.
                // MLBcrCaptureConfig.ORIENTATION_AUTO: Adaptive mode, the display direction is determined by the physical
                // sensor.
                // MLBcrCaptureConfig.ORIENTATION_LANDSCAPE: Horizontal screen.
                // MLBcrCaptureConfig.ORIENTATION_PORTRAIT: Vertical screen.
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .create();
        MLBcrCapture bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config);
        bcrCapture.captureFrame(this, mBankCallback);
    }

    /**
     * 检查SD和相机权限
     *
     * @param requestCode int
     * @return true/false
     */
    public boolean checkSDAndCameraPermission(int requestCode) {
        if (checkAndRequestPermission(this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, requestCode)) {
            return true;
        }
        return false;
    }

    private final MLBcrCapture.Callback mBankCallback = new MLBcrCapture.Callback() {
        // Identify successful processing.
        @Override
        public void onSuccess(MLBcrCaptureResult bankCardResult) {
            Log.i(TAG, "CallBack onRecSuccess");
            if (bankCardResult == null) {
                Log.i(TAG, "CallBack onRecSuccess idCardResult is null");
                return;
            }
            bankCardResultDialog(formatIdCardResult(bankCardResult));
        }

        // User cancellation processing.
        @Override
        public void onCanceled() {
            Log.i(TAG, "CallBackonRecCanceled");
        }

        // Identify failure processing.
        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            Toast.makeText(BasePaySettingActivity.this, getString(R.string.add_bank_card_failed, ", " + retCode), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Bank Card CallBack onFailure retCode is " + retCode);
            AGConnectCrash.getInstance().log(Log.INFO,"Bank Card CallBack onFailure retCode is " + retCode);
        }

        @Override
        public void onDenied() {
            Log.i(TAG, "CallBack onDenied ");
            Toast.makeText(BasePaySettingActivity.this, getString(R.string.add_bank_card_failed), Toast.LENGTH_SHORT).show();
            AGConnectCrash.getInstance().log("Bank Card CallBack onDeny");

        }
    };

    private String formatIdCardResult(MLBcrCaptureResult bankCardResult) {
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append(getString(R.string.card_number));
        resultBuilder.append(bankCardResult.getNumber());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append(getString(R.string.card_issuer));
        resultBuilder.append(bankCardResult.getIssuer());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append(getString(R.string.card_expire));
        resultBuilder.append(bankCardResult.getExpire());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append(getString(R.string.card_type));
        resultBuilder.append(bankCardResult.getType());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append(getString(R.string.card_organization));
        resultBuilder.append(bankCardResult.getOrganization());
        resultBuilder.append(System.lineSeparator());

        return resultBuilder.toString();
    }

    private void bankCardResultDialog(String result) {
        Bundle data = new Bundle();
        data.putString(BaseDialog.CONTENT, getString(R.string.bank_card_result, result));
        data.putString(BaseDialog.CONFIRM_BUTTON, getBankConfirmBut());

        BaseDialog dialog = new BaseDialog(this, data, false);

        dialog.setConfirmListener(v -> {
            dealAuthSucceedResult(KeyConstants.SETTING_PAY_BANK_KEY);
            dialog.dismiss();
        });
        dialog.show();
    }

    protected String getBankConfirmBut() {
        return  getString(R.string.confirm);
    }

    public void faceVerification() {
        // Cancellation Signal
        CancellationSignal cancellationSignal = new CancellationSignal();
        FaceManager faceManager = new FaceManager(this);
        // flags
        int flags = 0;
        // Authentication messsage handler.
        Handler handler = null;
        // Recommended CryptoObject to be set to null. KeyStore is not associated with face authentication in current
        // version. KeyGenParameterSpec.Builder.setUserAuthenticationRequired() must be set false in this scenario.
        CryptoObject crypto = null;
        faceManager.auth(crypto, cancellationSignal, flags, mFaceCallBack, handler);
    }

    // call back
    BioAuthnCallback mFaceCallBack = new BioAuthnCallback() {
        @Override
        public void onAuthError(int errMsgId, CharSequence errString) {
            Log.i(TAG,"face onFaceAuthError errMsgId = " + errMsgId);
            dismissAuthHelpTips();
            switch (errMsgId) {
                case FaceManager.FACE_ERROR_NOT_ENROLLED:
                    showResult(getString(R.string.facial_function_tips), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            BiometricAuthenticationUtil.startFace(BasePaySettingActivity.this);
                        }
                    });
                    break;
                case FaceManager.FACE_ERROR_TIMEOUT:
                    showResult(getString(R.string.time_out));
                    break;
                default:
                    showResult("" + errString + (errMsgId == 1012 ? " The camera permission may not be enabled." : ""));
                    break;
            }
        }
        @Override
        public void onAuthHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "Authentication help. helpMsgId=" + helpMsgId + ",helpString=" + helpString + "\n");
            String msg = "";
            switch (helpMsgId) {
                case FaceManager.FACE_ACQUIRED_GOOD:
                case FaceManager.FACE_ACQUIRED_VENDOR:
                    msg = getString(R.string.face_acquired_good);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_BRIGHT:
                    msg = getString(R.string.face_acquired_too_bright);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_DARK:
                    msg = getString(R.string.face_acquired_too_dark);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_CLOSE:
                    msg = getString(R.string.face_acquired_too_close);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_FAR:
                    msg = getString(R.string.face_acquired_too_far);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_HIGH:
                    msg = getString(R.string.face_acquired_too_high);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_LOW:
                    msg = getString(R.string.face_acquired_too_low);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_RIGHT:
                    msg = getString(R.string.face_acquired_too_right);
                    break;
                case FaceManager.FACE_ACQUIRED_TOO_LEFT:
                    msg = getString(R.string.face_acquired_too_left);
                    break;
                default:
                    msg = String.valueOf(helpString);
                    break;
            }
            onAuthHelpTips(msg);
        }
        @Override
        public void onAuthSucceeded(BioAuthnResult result) {
            Log.i(TAG,"onFaceAuthSucceeded ");
            dismissAuthHelpTips();
            dealAuthSucceedResult(KeyConstants.SETTING_PAY_FACE_KEY);
        }
        @Override
        public void onAuthFailed() {
            Log.i(TAG,"onFaceAuthFailed ");
            dismissAuthHelpTips();
            showResult(getString(R.string.pay_auth_fail));
        }
    };

    public void fingerprintVerification() {
        createFingerprintManager().auth();
    }

    private FingerprintManager createFingerprintManager() {
        // call back
        BioAuthnCallback callback = new BioAuthnCallback() {
            @Override
            public void onAuthError(int errMsgId, CharSequence errString) {
                Log.i(TAG,"onFingerAuthError errMsgId = " + errMsgId);
                switch (errMsgId) {
                    case FingerprintManager.FINGERPRINT_ERROR_NO_FINGERPRINTS:
                        showResult(getString(R.string.fingerprint_function_tips),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    BiometricAuthenticationUtil.startFingerprint(BasePaySettingActivity.this);
                                }
                            });
                        break;
                    case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                        showResult(getString(R.string.time_out));
                        break;
                    default:
                        showResult("" + errString);
                        break;
                }
            }

            @Override
            public void onAuthSucceeded(BioAuthnResult result) {
                Log.i(TAG,"onFingerAuthSucceeded ");
                dealAuthSucceedResult(KeyConstants.SETTING_PAY_FINGERPRINT_KEY);
            }

            @Override
            public void onAuthFailed() {
                Log.i(TAG,"onFingerAuthFailed ");
                showResult(getString(R.string.pay_auth_fail));
            }
        };
        return new FingerprintManager(this, ContextCompat.getMainExecutor(this), callback);
    }

    protected  void dealAuthSucceedResult(String flag) {
        Log.i(TAG,"dealAuthSucceedResult flag = " +flag);
    }

    protected void onAuthHelpTips(String msg) {
    }

    protected void dismissAuthHelpTips() {
    }

    public void showResult(final String msg, DialogInterface.OnClickListener onClickListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(BasePaySettingActivity.this);
                builder.setTitle(getString(R.string.pay_auth_result));
                builder.setMessage(msg);
                builder.setPositiveButton(android.R.string.ok, onClickListener);
                builder.show();
            }
        });
    }

    public void showResult(final String msg) {
        showResult(msg, null);
    }
}
