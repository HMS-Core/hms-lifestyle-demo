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

package com.huawei.hmscore.industrydemo.page.viewmodel;

import static com.huawei.hmscore.industrydemo.utils.SystemUtil.isLogin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.agconnect.crash.BuildConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.network.NetworkKit;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsListResp;
import com.huawei.hms.support.api.entity.safetydetect.WifiDetectResponse;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;
import com.huawei.hmscore.industrydemo.MainActivity;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.base.BaseFragment;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.constants.LogConfig;
import com.huawei.hmscore.industrydemo.constants.RequestCodeConstants;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.ScanRoot;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.event.EventUpdateRest;
import com.huawei.hmscore.industrydemo.page.TakeawayActivity;
import com.huawei.hmscore.industrydemo.page.activity.CommentActivity;
import com.huawei.hmscore.industrydemo.page.activity.MessageInfoActivity;
import com.huawei.hmscore.industrydemo.page.activity.SearchActivity;
import com.huawei.hmscore.industrydemo.page.fragment.HomeFragment;
import com.huawei.hmscore.industrydemo.page.fragment.MyFragment;
import com.huawei.hmscore.industrydemo.repository.CouponRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.LocationUtil;
import com.huawei.hmscore.industrydemo.utils.MessagingUtil;
import com.huawei.hmscore.industrydemo.utils.StatusDialogUtil;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.hms.AnalyticsUtil;
import com.huawei.hmscore.industrydemo.utils.hms.MLUtil;
import com.huawei.hmscore.industrydemo.utils.hms.SiteUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class MainActivityViewModel extends BaseActivityViewModel<MainActivity> implements KitConstants, OnSuccessListener<MaliciousAppsListResp> {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();
    private static final int HOME_INDEX = 0;
    private static final int MY_INDEX = 2;

    private boolean integrityFlag = false;

    private StatusDialogUtil mStatusDialog;
    public static final int CAMERA_REQ_CODE = 111;
    public static final int DECODE = 1;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private static final int REQUEST_CODE_COMMENT = 0X02;

    // Home
    private HomeFragment homeFragment;

    // Mine
    private MyFragment myFragment;

    private CardView homeBar;

    // Records created fragments.
    private final List<Fragment> fragmentList = new ArrayList<>();

    private RadioGroup mTabRadioGroup;

    private final Map<Integer, Integer> pageIndex = new HashMap<>();

    private User user;

    private LocationUtil locationUtil;

    TextView textViewLocation;
    private final static int REQUEST_LOCATION = 2;

    private boolean mIsSelectHome = true;

    /**
     * constructor
     *
     * @param mainActivity Activity object
     */
    public MainActivityViewModel(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void initView() {
        homeBar = mActivity.findViewById(R.id.bar_home);
        textViewLocation = mActivity.findViewById(R.id.location);
        mActivity.findViewById(R.id.bar_rv_search).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.bar_release).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.vScan).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.vVoice).setOnClickListener(mActivity);
        mActivity.findViewById(R.id.iv_hms).setOnClickListener(mActivity);

        mTabRadioGroup = mActivity.findViewById(R.id.tabs_rg);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        pageIndex.put(R.id.tab_home, HOME_INDEX);
        pageIndex.put(R.id.tab_my, MY_INDEX);
        mActivity.findViewById(R.id.tab_video).setOnClickListener(mActivity);

        isGetLocationPermission();
        initAnalytics();
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(mActivity));

    }

    public void initFragment() {
        homeFragment = new HomeFragment();
        addFragment(homeFragment);
        showFragment(homeFragment);
    }

    private SiteUtil siteUtil;
    
    private void initLocation() {
        locationUtil = new LocationUtil(mActivity);
        locationUtil.addLocationChangedlister(new LocationUtil.ILocationChangedLister() {
            @Override
            public void locationChanged(Location location) {
                if(location == null){
                    return;
                }
                String textLocation = transLocationToGeoCoder(location);
                if(textLocation != null){
                    textViewLocation.setText(textLocation);
                }
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                siteUtil = new SiteUtil(mActivity, latLng);
                siteUtil.nearBySearch(latLng, "Food", searchResultListener);
            }
        });
        locationUtil.getMyLoction();
    }

    SearchResultListener searchResultListener = new SearchResultListener<NearbySearchResponse>() {
        @Override
        public void onSearchResult(NearbySearchResponse results) {
            List<Site> siteList = results.getSites() ;
            handleSiteList(siteUtil.getMarkerAddr(siteList));
            if ("".equals(textViewLocation.getText()) && siteList.size() > 0) {
                Site s = siteList.get(0);
                String address = s.getFormatAddress();
                if(address.contains(",")){
                    String[] aList = address.split(",");
                    address = aList[aList.length-1];
                }
                textViewLocation.setText(address);
            }
        }

        @Override
        public void onSearchError(SearchStatus status) {
            if (Constants.ERROR_ZERO_RESULTS.equals(status.getErrorCode())) {
                List<Site> siteList = new ArrayList<>();
                handleSiteList(siteUtil.getMarkerAddr(siteList));
            }else {
                String err = "Site Error : " + status.getErrorCode() + " " + status.getErrorMessage();
                Log.e(TAG, err);
                AgcUtil.reportFailure(TAG, AgcUtil.getApiKey() + err);
            }
        }
    };

    private void handleSiteList(List<Site> sites) {
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        List<Restaurant> restaurants =restaurantRepository.queryAll();
        for (int i = 0; i < restaurants.size(); i++) {
            if (i < sites.size()) {
                Site site = sites.get(i);
                Restaurant restaurant = restaurants.get(i);
                restaurant.setAddress(site.getFormatAddress());
                restaurant.setStep(String.valueOf(site.getDistance()));
                restaurant.setPoslat(site.getLocation().getLat());
                restaurant.setPoslng(site.getLocation().getLng());
            }
        }
        restaurantRepository.insert(restaurants);
        EventBus.getDefault().post(new EventUpdateRest());
    }

    @Override
    public void onClickEvent(int viewId) {
        Intent intent;
        switch (viewId) {
            case R.id.bar_rv_search:
                mActivity.startActivity(new Intent(mActivity, SearchActivity.class));
                break;
            case R.id.bar_release:
                if (!isLogin()) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.please_sign_first), Toast.LENGTH_SHORT)
                        .show();
                    break;
                }
                intent = new Intent(mActivity, MessageInfoActivity.class);
                mActivity.startActivity(intent);
                break;
            case R.id.vScan:
                requestPermission(CAMERA_REQ_CODE, DECODE);
                break;
            case R.id.vVoice:
                startAsr();
                break;
            case R.id.tab_video:
                intent = new Intent(mActivity, CommentActivity.class);
                mActivity.startActivityForResult(intent, REQUEST_CODE_COMMENT);
                break;
            default:
                break;
        }
    }

    private void startAsr() {
        if ((ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            goToAsr();
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.RECORD_AUDIO},
                    Constants.CAMERA_RECORD_AUDIO_CODE);
        }
    }

    private void goToAsr() {
        new MLUtil().goToAsr(mActivity);
    }

    private void addFragment(BaseFragment fragment) {
        if (!fragment.isAdded()) {
            mActivity.getSupportFragmentManager().beginTransaction().add(R.id.frame_main, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    private void showFragment(BaseFragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                mActivity.getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        mActivity.getSupportFragmentManager().beginTransaction().show(fragment).commit();
        if (fragment == homeFragment) {
            mActivity.setKitList(new String[]{ADS_ADS, LOCATION_LBS, SCAN_QR ,NETWORK_CONNECT, SAFETY_DETECT_SYS, ML_ASR});
        } else {
            mActivity.setKitList(new String[]{ACCOUNT_LOGIN});
        }
    }

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Log.d(LogConfig.TAG, "onCheckedChanged");
                    switch (checkedId) {
                        case R.id.tab_home: // Home
                            mIsSelectHome = true;
                            if (homeFragment == null) {
                                homeFragment = new HomeFragment();
                            }
                            homeBar.setVisibility(View.VISIBLE);
                            addFragment(homeFragment);
                            showFragment(homeFragment);
                            break;
                        case R.id.tab_my: // My
                            mIsSelectHome = false;
                            if (myFragment == null) {
                                myFragment = new MyFragment();
                            }
                            homeBar.setVisibility(View.GONE);
                            addFragment(myFragment);
                            showFragment(myFragment);
                            break;
                        default:
                            break;
                    }
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case LocationUtil.REQUEST_CODE_OPEN_GPS:
                locationUtil.getLocationAvailability(new OnSuccessListener<LocationAvailability>() {
                    @Override
                    public void onSuccess(LocationAvailability locationAvailability) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        if (flag) {
                            locationUtil.getMyLoction();
                        } else {
                            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.gps_is_off), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
                break;

            case REQUEST_CODE_COMMENT:
                if(mIsSelectHome) {
                    mTabRadioGroup.check(R.id.tab_home);
                } else {
                    mTabRadioGroup.check(R.id.tab_my);
                }
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.comment_public_success),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case RequestCodeConstants.ML_ASR_CAPTURE_CODE:
                new MLUtil().dealAsrResult(resultCode, data, mActivity);
                break;
            case REQUEST_CODE_SCAN_ONE:
                if (data == null) {
                    break;
                }
                HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
                if (hmsScan == null) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.please_use_correct_qr_code),
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
                    String res = hmsScan.getOriginalValue();
                    try {
                        ScanRoot scanRoot = new Gson().fromJson(res, ScanRoot.class);
                        Log.d(TAG, ": " + scanRoot);
                        if (scanRoot != null && scanRoot.getRestId() != null) {
                            int id = Integer.parseInt(scanRoot.getRestId());
                            if (new RestaurantRepository().queryByNumber(id) != null) {
                                if (scanRoot.getCondition() != null) {
                                    boolean b = checkCoupons(Integer.parseInt(scanRoot.getRestId()), scanRoot.getCondition(), scanRoot.getDiscount());
                                    if (b) {
                                        TakeawayActivity.startTakeawayActivity(mActivity, id);
                                        Toast.makeText(mActivity, mActivity.getString(R.string.add_coupon_success), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    TakeawayActivity.startTakeawayActivity(mActivity, id);
                                }
                            } else {
                                Toast.makeText(mActivity, mActivity.getString(R.string.restaurant_not_existence), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        Toast.makeText(mActivity, mActivity.getString(R.string.please_use_correct_qr_code), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                break;
            default:
                break;
        }
    }

    private boolean checkCoupons(int restId, int condition, int discount) {
        User user = new UserRepository().getCurrentUser();
        if (user == null) {
            Toast.makeText(mActivity, mActivity.getString(R.string.please_sign_first), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        CouponRepository couponRepository = new CouponRepository();
        List<Coupon> couponList =
                couponRepository.queryByUserAndRestAndCondition(user.getOpenId(), restId, condition);
        if (couponList.isEmpty()) {
            couponRepository.insert(createCoupon(user.getOpenId(), restId, condition, discount));
        }
        Toast.makeText(mActivity, mActivity.getString(R.string.coupon_claimed), Toast.LENGTH_SHORT)
                .show();
        return true;
    }

    private Coupon createCoupon(String openId, int restId, int condition, int discount) {
        Coupon coupon = new Coupon();
        coupon.setOpenId(openId);
        coupon.setRestId(restId);
        coupon.setCondition(condition);
        coupon.setDiscount(discount);
        coupon.setStatus(true);
        return coupon;
    }


    private boolean isGetLocationPermission() {
        Log.d(TAG, "isGetLocationPermission: b");
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            ActivityCompat.requestPermissions(mActivity, strings, REQUEST_LOCATION);
            Log.d(TAG, "isGetLocationPermission: false");
            return false;
        }
        initLocation();
        Log.d(TAG, "isGetLocationPermission: ture");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        homeFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case Constants.CAMERA_RECORD_AUDIO_CODE:
                goToAsr();
                break;
            case CAMERA_REQ_CODE:
                ScanUtil.startScan(mActivity, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
                break;
            case REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initLocation();
                } else {
                    Log.i(TAG, "onRequestPermissionsResult denied");
                }
                break;
            
            default:
                break;
        }
    }


    public boolean backToHomeFragment() {
        if (mTabRadioGroup.getCheckedRadioButtonId() != R.id.tab_home) {
            mTabRadioGroup.check(R.id.tab_home);
            return true;
        }
        return false;
    }

    private String transLocationToGeoCoder(Location location) {
        String mSendText = "";
        try {
            Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            mSendText = addressList != null && addressList.size() > 0 ? addressList.get(0).getLocality()
                    : "";
            Log.i(TAG, "geocoder  : " + mSendText);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return mSendText;
        }
        return mSendText;
    }

    private void initAnalytics() {
        if (BuildConfig.DEBUG) {
            HiAnalyticsTools.enableLog();
            AGConnectCrash.getInstance().enableCrashCollection(false);
        }
        AnalyticsUtil.getInstance(mActivity).setAnalyticsEnabled(true);
    }


    /**
     * Apply for permissions.
     */
    private void requestPermission(int requestCode, int mode) {
        if (mode == DECODE) {
            decodePermission(requestCode);
        }
    }

    /**
     * Apply for permissions.
     */
    private void decodePermission(int requestCode) {
        ActivityCompat.requestPermissions(
                mActivity,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    public void initHmsKits() {
        initNetWorkKit();
        getPushToken();
        initSysDetect();
        invokeGetMaliciousApps();
        invokeGetWifiDetectStatus();
    }

    private void invokeGetWifiDetectStatus() {
        Log.i(TAG, "Start to getWifiDetectStatus!");
        SafetyDetectClient client = SafetyDetect.getClient(mActivity);
        Task<WifiDetectResponse> wifiDetectStatus = client.getWifiDetectStatus();
        wifiDetectStatus.addOnSuccessListener(new OnSuccessListener<WifiDetectResponse>() {
            @Override
            public void onSuccess(WifiDetectResponse wifiDetectResponse) {
                int wifiDetectStatus = wifiDetectResponse.getWifiDetectStatus();
                if (wifiDetectStatus == 2) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.wifi_status_hint), Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "wifiDetectStatus is: " + wifiDetectStatus);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(TAG,
                            "Error: " + apiException.getStatusCode() + ":"
                                    + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                                    + apiException.getStatusMessage());
                } else {
                    Log.e(TAG, "ERROR! " + e.getMessage());
                }
            }
        });
    }

    private void initNetWorkKit() {
        NetworkKit.init(mActivity, new NetworkKit.Callback() {
            @Override
            public void onResult(boolean result) {
                if (result) {
                    Log.i(TAG, "Network kit init success");
                } else {
                    AgcUtil.reportFailure(TAG, "Network kit init failed");
                }
            }
        });
    }

    private void getPushToken() {
        new Thread(() -> {
            try {
                Log.i(TAG, "getPushToken");
                String token = HmsInstanceId.getInstance(mActivity)
                        .getToken(AgcUtil.getAppId(mActivity), HmsMessaging.DEFAULT_TOKEN_SCOPE);
                Log.i(TAG, "get token: " + token);
                if (!TextUtils.isEmpty(token)) {
                    MessagingUtil.refreshedToken(mActivity, token);
                }
            } catch (ApiException e) {
                AgcUtil.reportException(TAG, e);
            }
        }).start();
    }

    private void invokeGetMaliciousApps() {
        SafetyDetectClient client = SafetyDetect.getClient(mActivity);
        Task<MaliciousAppsListResp> maliciousAppsList = client.getMaliciousAppsList();
        maliciousAppsList.addOnSuccessListener(this);
    }

    private void initSysDetect() {
        if (integrityFlag) {
            return;
        }
        showStatusDialog(Constants.DETECTING, mActivity.getString(R.string.sys_integrity_detecting));
        byte[] nonce = new byte[24];
        try {
            SecureRandom random;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                random = SecureRandom.getInstanceStrong();
            } else {
                random = SecureRandom.getInstance("SHA1PRNG");
            }
            random.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            AgcUtil.reportException(TAG, e.getMessage(), e);
        }
        String appId = AgcUtil.getAppId(mActivity);
        SafetyDetect.getClient(mActivity).sysIntegrity(nonce, appId).addOnSuccessListener(response -> {
            String jwsStr = response.getResult();
            String[] jwsSplit = jwsStr.split("\\.");
            String jwsPayloadStr = jwsSplit[1];
            String payloadDetail = new String(
                Base64.decode(jwsPayloadStr.getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE), StandardCharsets.UTF_8);
            try {
                final JSONObject jsonObject = new JSONObject(payloadDetail);
                final boolean basicIntegrity = jsonObject.getBoolean("basicIntegrity");
                if (!basicIntegrity) {
                    String advice = "Advice: " + jsonObject.getString("advice");
                    showStatusDialog(Constants.IS_NOT_INTEGRITY, advice);
                } else {
                    showStatusDialog(Constants.IS_INTEGRITY,
                        mActivity.getString(R.string.sys_integrity_detect_success));
                    integrityFlag = true;
                }
            } catch (JSONException e) {
                AgcUtil.reportException(TAG, e.getMessage(), e);
                if (mStatusDialog.isShowing()) {
                    mStatusDialog.dismiss();
                }
            }
        }).addOnFailureListener(e -> {
            if (mStatusDialog.isShowing()) {
                mStatusDialog.dismiss();
            }
            String errorMsg;
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                errorMsg = SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                    + apiException.getMessage();
            } else {
                errorMsg = e.getMessage();
            }
            AgcUtil.reportException(TAG, errorMsg, e);
        });
    }

    private void showStatusDialog(int status, String msg) {
        if (mStatusDialog == null) {
            mStatusDialog = new StatusDialogUtil(mActivity);
        }

        switch (status) {
            case Constants.DETECTING:
                mStatusDialog.show(msg);
                break;
            case Constants.IS_INTEGRITY:
                mStatusDialog.show(msg, true, 2000, R.color.light_green_1);
                break;
            case Constants.IS_NOT_INTEGRITY:
                Log.e(TAG, "  Log.e(TAG, errorMsg != null ? errorMsg : \"unknown error\");");
                mStatusDialog.show(msg, false, 2000, R.color.light_red_1);
                break;
            default:
                break;
        }
        mStatusDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSuccess(MaliciousAppsListResp maliciousAppsListResp) {
        List<MaliciousAppsData> maliciousAppsList1 = maliciousAppsListResp.getMaliciousAppsList();
        if (maliciousAppsListResp.getRtnCode() == CommonCode.OK) {
            if (maliciousAppsList1.isEmpty()) {
                Log.i(TAG, "There are no known potentially malicious apps installed.");
            } else {
                Log.i(TAG, "Potentially malicious apps are installed!");
                for (MaliciousAppsData maliciousApp : maliciousAppsList1) {
                    Log.i(TAG, "Information about a malicious app:");
                }
            }
        } else {
            Log.e(TAG, "getMaliciousAppsList failed: " + maliciousAppsListResp.getErrorReason());
        }
    }
}
