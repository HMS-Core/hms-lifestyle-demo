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

import static com.huawei.hmscore.industrydemo.utils.NavigationUtils.PETAL_MAP_MARKET;
import static com.huawei.hmscore.industrydemo.utils.NavigationUtils.isAvailable;
import static com.huawei.hmscore.industrydemo.utils.SystemUtil.isLogin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Comment;
import com.huawei.hmscore.industrydemo.entity.Coupon;
import com.huawei.hmscore.industrydemo.entity.CouponMember;
import com.huawei.hmscore.industrydemo.entity.Image;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.page.TakeawayActivity;
import com.huawei.hmscore.industrydemo.page.activity.PanaromaActivity;
import com.huawei.hmscore.industrydemo.page.activity.PhotoVideoViewActivity;
import com.huawei.hmscore.industrydemo.page.activity.RestaurantDetailActivity;
import com.huawei.hmscore.industrydemo.repository.CardRepository;
import com.huawei.hmscore.industrydemo.repository.CommentRepository;
import com.huawei.hmscore.industrydemo.repository.CouponRepository;
import com.huawei.hmscore.industrydemo.repository.ImageRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.LocationUtil;
import com.huawei.hmscore.industrydemo.utils.MlTranslateUtils;
import com.huawei.hmscore.industrydemo.utils.NavigationUtils;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.agc.RemoteConfigUtil;
import com.huawei.hmscore.industrydemo.viewadapter.Comment2Adapter;
import com.huawei.hmscore.industrydemo.viewadapter.CouponAdapter;
import com.huawei.hmscore.industrydemo.viewadapter.MyViewPagerAdapter;
import com.huawei.hmscore.industrydemo.viewadapter.RestaurantInfoAdapter;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestaurantDetailViewModel extends BaseActivityViewModel<RestaurantDetailActivity> implements MyViewPagerAdapter.OnItemClickLitener {
    private RestaurantInfoAdapter mRestaurantInfoAdapter;
    private View mNavigation;
    private View mTakeAway;
    private ImageView mBack;
    private TextView mRestaurantName;
    private TextView mConsumeFee;
    private TextView mRestaurantRank;
    private TextView mRestaurantWorkTime;
    private TextView mRestaurantAddress;
    private static final String TAG = RestaurantDetailViewModel.class.getSimpleName();
    private RestaurantRepository mRestaurantRepository;
    private RatingBar mRestaurantRating;
    private TextView mRestaurantMark;
    private TextView mRestaurantNumber;
    private RecyclerView mRestaurantRvMark;
    private TextView mTvNoData;
    private MLRemoteTranslator mRemoteTranslator;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mCommentLayoutManager;
    private GridLayoutManager mCouponLayoutManager;
    private Restaurant mRestaurant;
    private Double lan;
    private Double lon;
    private RecyclerView mRestaurantCoupon;
    private CouponAdapter mCouponAdapter;
    private Comment2Adapter mCommentAdapter;
    private int mRestid;
    private double mAddressLat;
    private double mAddressLng;
    private ImageRepository mImageRepository;
    private List<Comment> mComments;
    private CommentRepository mCommentRepository;
    private String mSendText;
    private CouponRepository mCouponRepository;
    private View mViewLine;
    private TextView mComment;
    private int mImageId;
    private int mIamgeType;
    private List<Image> mImageDataFromLocal;
    private ArrayList<View> dotLists;
    private ViewPager mVpRes;
    private LinearLayout mDotContent;
    private List<ImageView> mImageViewList;
    private int olderIndex = 0;

    public RestaurantDetailViewModel(RestaurantDetailActivity RestaurantDetailActivity) {
        super(RestaurantDetailActivity);
    }

    @Override
    public void initView() {
        mNavigation = mActivity.findViewById(R.id.tv_navigation);
        mVpRes = mActivity.findViewById(R.id.vp_restaurant);
        mTakeAway = mActivity.findViewById(R.id.tv_takeaway);
        mDotContent = mActivity.findViewById(R.id.dot_content);
        mBack = mActivity.findViewById(R.id.iv_Restaurant_back);
        mRestaurantName = mActivity.findViewById(R.id.tv_Restaurant_name);
        mConsumeFee = mActivity.findViewById(R.id.tv_Restaurant_fee);
        mRestaurantRank = mActivity.findViewById(R.id.tv_Restaurant_rank);
        mRestaurantWorkTime = mActivity.findViewById(R.id.tv_Restaurant_worktime);
        mRestaurantAddress = mActivity.findViewById(R.id.tv_Restaurant_address);
        mRestaurantRating = mActivity.findViewById(R.id.rt_Restaurant);
        mRestaurantMark = mActivity.findViewById(R.id.tv_Restaurant_mark);
        mRestaurantNumber = mActivity.findViewById(R.id.tv_Restaurant_number);
        mRestaurantRvMark = mActivity.findViewById(R.id.rv_Restaurant_mark);
        mRestaurantCoupon = mActivity.findViewById(R.id.rv_coupon);
        mTvNoData = mActivity.findViewById(R.id.tv_no_data);
        mViewLine = mActivity.findViewById(R.id.view_Restaurant);
        mComment = mActivity.findViewById(R.id.tv_comment);
    }

    @Override
    public void onClickEvent(int viewId) {
        switch (viewId) {
            case R.id.tv_navigation:
                Double destinationPoslng = mRestaurant.getPoslng();
                Double destinationPosLat = mRestaurant.getPoslat();
                if (TextUtils.isEmpty(mSendText)) {
                    mSendText = mRestaurant.getAddress();
                }

                Bundle overSeaData = new Bundle();
                Bundle chinaData = new Bundle();
                overSeaData.putString(BaseDialog.CANCEL_BUTTON, mActivity.getString(R.string.cancel));
                overSeaData.putString(BaseDialog.CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
                overSeaData.putString(BaseDialog.CONTENT, mActivity.getString(R.string.navigation_hint));

                chinaData.putString(BaseDialog.CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
                chinaData.putString(BaseDialog.CONTENT, mActivity.getString(R.string.navigation_not_support));
                if (SystemUtil.isChinaPackage(mActivity)) {
                    BaseDialog dialog = new BaseDialog(mActivity, chinaData, false);
                    dialog.setConfirmListener(v -> {
                        dialog.dismiss();
                    });
                    dialog.show();
                } else {
                    BaseDialog dialog = new BaseDialog(mActivity, overSeaData, true);
                    dialog.setConfirmListener(v -> {
                        jumpToOverSeaNavigation(destinationPoslng, destinationPosLat);
                        dialog.dismiss();
                    });
                    dialog.setCancelListener(v -> dialog.dismiss());
                    dialog.show();
                }
                break;
            case R.id.tv_takeaway:
                TakeawayActivity.startTakeawayActivity(mActivity, mRestid);
                break;
            case R.id.iv_Restaurant_back:
                mActivity.finish();
                break;
        }
    }

    private void jumpToOverSeaNavigation(Double destinationPoslng, Double destinationPosLat) {
        if (isAvailable(mActivity, NavigationUtils.PETAL_MAP)) {
            Uri content_url = Uri.parse("petalmaps://navigation?daddr=" + destinationPosLat + "," + destinationPoslng + "&type=drive&utm_source=fb");
            Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                mActivity.startActivity(intent);
            }
        } else if (isAvailable(mActivity, NavigationUtils.GOOGLE_MAP)) {
            NavigationUtils.googleNavigition(mActivity, destinationPosLat + "", destinationPoslng + "");
        } else {
            Uri content_url = Uri.parse(PETAL_MAP_MARKET);
            Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
            mActivity.startActivity(intent);
        }
    }


    private void jumpToMarket() {
        Uri uri = Uri.parse(NavigationUtils.BAIDU_MARKET);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }


    public void initData() {
        Intent intent = mActivity.getIntent();
        if (null == intent) {
            Log.i(TAG, "intent is null");
            return;
        }

        getImageDataFromNet();
        mRestid = intent.getIntExtra(KeyConstants.RESTAURANT_ID, 1);
        refreshView();
        mImageDataFromLocal = getImageDataFromLocal();
        List<CouponMember> mCouponViews = new ArrayList<>();
        getDataFromLocal(mCouponViews);
        setRecycleView(mImageDataFromLocal, mCouponViews);
    }

    private void getImageDataFromNet() {
        String key = mActivity.getString(R.string.comment_image_key);
        AGConnectConfig config = AgcUtil.getConfig();
        Map<String, Object> results = config.getMergedAll();
        if (results.containsKey(key)) {
            String json = config.getValueAsString(key);
            RestaurantDetailActivity.RestaurantReference listTypeToken = new RestaurantDetailActivity.RestaurantReference();
            ArrayList<Image> lists = new Gson().fromJson(json, listTypeToken.getType());
            if(lists == null || lists.size() ==0){
                return;
            }
            for (Image image : lists) {
                Log.e(TAG, key + "image" + image.getImgAdd());
            }
        } else {
            Log.e(TAG, key + " is null!");
            RemoteConfigUtil.fetch();
        }
    }

    private void refreshView() {
        mRestaurantRepository = new RestaurantRepository();
        mRestaurant = mRestaurantRepository.queryByNumber(mRestid);
        mRestaurantName.setText(mRestaurant.getRestname());
        mRestaurantRank.setText(mRestaurant.getDescription());
        mRestaurantWorkTime.setText(mActivity.getString(R.string.business_hours, mRestaurant.getWorktime()));
        if (!TextUtils.isEmpty(mRestaurant.getAddress())) {
            mRestaurantAddress.setText(mRestaurant.getAddress());
        }
        mRestaurantRating.setRating(mRestaurant.getRate());
        mConsumeFee.setText(mRestaurant.getAvgprice());

        BigDecimal bigDecimal = new BigDecimal(mRestaurant.getRate());
        double rateValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        mRestaurantNumber.setText(rateValue + "");
    }

    private List<Image> getImageDataFromLocal() {
        mImageRepository = new ImageRepository();
        return mImageRepository.queryAllByRestId(mRestaurant.getRestid());
    }

    private void getDataFromLocal(List<CouponMember> mCouponViews) {
        CouponMember couponMember1 = new CouponMember("",
                mActivity.getResources().getIdentifier("member2", "mipmap", mActivity.getPackageName()));
        CouponMember couponMember2 = new CouponMember(mActivity.getString(R.string.coupon_five2one),
                mActivity.getResources().getIdentifier("coupon_bg_1", "mipmap", mActivity.getPackageName()));
        CouponMember couponMember3 = new CouponMember((mActivity.getString(R.string.coupon_thirty2five)),
                mActivity.getResources().getIdentifier("coupon_bg_2", "mipmap", mActivity.getPackageName()));
        CouponMember couponMember4 = new CouponMember((mActivity.getString(R.string.coupon_fifty2ten)),
                mActivity.getResources().getIdentifier("coupon_bg_3", "mipmap", mActivity.getPackageName()));

        mCouponViews.add(couponMember1);
        mCouponViews.add(couponMember2);
        mCouponViews.add(couponMember3);
        mCouponViews.add(couponMember4);
    }

    public void initListener() {
        mNavigation.setOnClickListener(mActivity);
        mTakeAway.setOnClickListener(mActivity);
        mBack.setOnClickListener(mActivity);
    }

    public void initRemoteTranslateSetting(String targetLanguageCode) {
        MlTranslateUtils.getInstance().initRemoteTranslateSetting(targetLanguageCode);
    }

    private void getDotList(List<Image> views) {
        dotLists = new ArrayList<>();
        for (int i = 0; i < views.size(); i++) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_dot, null);
            View dot = view.findViewById(R.id.dotView);
            dotLists.add(dot);
            mDotContent.addView(view);
        }
    }

    private List<ImageView> getImageView(List<Image> views) {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < views.size(); i++) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageViewList.add(imageView);
        }
        return mImageViewList;
    };


    private void setRecycleView(List<Image> views, List<CouponMember> couponMembers) {
        getDotList(views);
        getImageView(views);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter((views), mActivity);
        myViewPagerAdapter.setOnItemClickLitener(this);
        mVpRes.setAdapter(myViewPagerAdapter);
        dotLists.get(olderIndex).setBackgroundResource(R.drawable.focus);
        mVpRes.setCurrentItem(0);
        mVpRes.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled " + position );
            }

            @Override
            public void onPageSelected(int position) {
                int currentIndex = position % mImageViewList.size();
                dotLists.get(currentIndex).setBackgroundResource(R.drawable.focus);
                dotLists.get(olderIndex % mImageViewList.size()).setBackgroundResource(R.drawable.normal);
                olderIndex = currentIndex;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged " + state );
            }
        });
        mCouponLayoutManager = new GridLayoutManager(mActivity, 1, RecyclerView.HORIZONTAL, false);
        mRestaurantCoupon.setLayoutManager(mCouponLayoutManager);
        mRestaurantCoupon.setItemAnimator(new DefaultItemAnimator());
        mCouponAdapter = new CouponAdapter(couponMembers, mActivity, isLogin(), mRestaurant.getRestid());
        mCouponAdapter.setOnItemClickLitener(new CouponAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                User user = isUserLogin();
                if (user == null) {
                    return;
                }
                switch (position) {
                    case 0:
                        if (!existMemberCard(user)) {
                            getMemberCard(user);
                        }
                        mCouponAdapter.refreshView();
                        break;
                    case 1:
                        checkCoupons(Constants.COUPON_CONDITION1, Constants.COUPON_DISCOUNT1, 1);
                        break;
                    case 2:
                        checkCoupons(Constants.COUPON_CONDITION2, Constants.COUPON_DISCOUNT2, 2);
                        break;
                    case 3:
                        checkCoupons(Constants.COUPON_CONDITION3, Constants.COUPON_DISCOUNT3, 3);
                        break;
                    default:
                        break;
                }
            }
        });
        mCouponAdapter.refresh(couponMembers);
        mRestaurantCoupon.setAdapter(mCouponAdapter);


        mCommentLayoutManager = new LinearLayoutManager(mActivity);
        mCommentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentRepository = new CommentRepository();
        mComments = mCommentRepository.queryByRest(mRestid);
        mRestaurantMark.setText(mActivity.getString(R.string.title_comment_count, mComments.size()));
        showOrHideComments(mComments);
        mCommentAdapter = new Comment2Adapter(mComments, mActivity, mRestid);
        mCommentAdapter.refresh(mComments);
        mCommentLayoutManager.setSmoothScrollbarEnabled(true);
        mRestaurantRvMark.setAdapter(mCommentAdapter);
        mRestaurantRvMark.setLayoutManager(mCommentLayoutManager);
        mRestaurantRvMark.setItemAnimator(new DefaultItemAnimator());
    }


    private boolean existCoupon(int couponCondition, User user) {
        List<Coupon> couponList =
                mCouponRepository.queryByUserAndRestAndCondition(user.getOpenId(), mRestaurant.getRestid(), couponCondition);
        return !couponList.isEmpty();
    }

    private boolean existMemberCard(User user) {
        List<Card> couponList =
                new CardRepository().queryByUserAndRest(user.getOpenId(), mRestaurant.getRestid());
        return !couponList.isEmpty();
    }


    private void getMemberCard(User user) {
        CardRepository cardRepository = new CardRepository();
        Card card = new Card();
        card.setOpenId(user.getOpenId());
        card.setRestId(mRestaurant.getRestid());
        card.setLevel(1);
        card.setStatus(true);
        cardRepository.insert(card);
        Toast.makeText(mActivity, mActivity.
                getString(R.string.comment_membercard), Toast.LENGTH_SHORT)
                .show();
    }

    private void showOrHideComments(List<Comment> comments) {
        if (comments.size() == 0) {
            mTvNoData.setVisibility(View.VISIBLE);
            mRestaurantRvMark.setVisibility(View.GONE);
        } else {
            mTvNoData.setVisibility(View.GONE);
            mRestaurantRvMark.setVisibility(View.VISIBLE);
        }
    }

    private void checkCoupons(int condition, int discount, int pos) {
        mCouponRepository = new CouponRepository();
        User user = isUserLogin();
        if (user == null) {
            return;
        }
        if (existCoupon(condition, user)) {
            return;
        }
        mCouponAdapter.refreshView();
        mCouponRepository.insert(createCoupon(user.getOpenId(), mRestaurant.getRestid(), condition, discount));
        Toast.makeText(mActivity, mActivity.getString(R.string.coupon_claimed), Toast.LENGTH_SHORT)
                .show();
    }

    private User isUserLogin() {
        User user = new UserRepository().getCurrentUser();
        if (user == null) {
            Toast.makeText(mActivity, mActivity.getString(R.string.please_sign_first), Toast.LENGTH_SHORT).show();
            return null;
        }
        return user;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                displayInHms(mImageId, mIamgeType);
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, int imgId, int imageType) {
        Intent intent = new Intent(mActivity, PhotoVideoViewActivity.class);
        mImageId = imgId;
        mIamgeType = imageType;
        switch (imageType) {
            case 1:
                intent.putExtra(KeyConstants.PHOTO_TYPE, 2);
                intent.putExtra(KeyConstants.IMAGE_ID, imgId);
                intent.putExtra(KeyConstants.RESTAURANT_ID, mRestid);
                mActivity.startActivity(intent);
                break;
            case 2:
                checkPermission(mImageId, mIamgeType);
                break;
            case 3:
                intent.putExtra(KeyConstants.PHOTO_TYPE, 3);
                intent.putExtra(KeyConstants.IMAGE_ID, imgId);
                intent.putExtra(KeyConstants.RESTAURANT_ID, mRestid);
                mActivity.startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void displayInHms(int imageId, int imageType) {
        String sourceUrl = new ImageRepository().queryByImgId(imageId, imageType).getImgAdd();
        ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.image_downloading));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        AgcUtil.downloadFile(mActivity, sourceUrl, progressDialog, new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                progressDialog.dismiss();
                Bundle data = new Bundle();
                BaseDialog dialog = new BaseDialog(mActivity, data, false);
                dialog.setConfirmListener(v -> {
                    dialog.dismiss();
                    gotoPanoramoPage(sourceUrl);
                });
                dialog.show();
            }

            ;
        });
    }


    private void gotoPanoramoPage(String url) {
        Intent intent = new Intent(mActivity, PanaromaActivity.class);
        intent.putExtra("sourceUri", url);
        mActivity.startActivity(intent);
    }


    private void checkPermission(int iamgeId, int imageType) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Log.i("", "permission ok");
            displayInHms(iamgeId, imageType);
        }
    }

    public void closeRemoteTranslate() {
        MlTranslateUtils.getInstance().closeRemoteTranslate();
        if (mRemoteTranslator != null) {
            mRemoteTranslator.stop();
        }
    }

    public void initLoc() {
        LocationUtil locationUtil = new LocationUtil(mActivity);
        locationUtil.addLocationChangedlister(new LocationUtil.ILocationChangedLister() {
            @Override
            public void locationChanged(Location latLng) {
                String locationToGeoCoder = transLocationToGeoCoder(latLng);
                if (TextUtils.isEmpty(mRestaurantAddress.getText())) {
                    mRestaurantAddress.setText(locationToGeoCoder);
                }
                mAddressLat = latLng.getLatitude();
                mAddressLng = latLng.getLongitude();
            }
        });
        locationUtil.getMyLoction();
    }

    private String transLocationToGeoCoder(Location location) {
        mSendText = "";
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

    public void onResume() {
        mComment.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewLine.getLayoutParams();
                layoutParams.height = 3;
                layoutParams.width = mComment.getMeasuredWidth();
                mViewLine.requestLayout();
            }
        });

    }
}
