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

package com.huawei.hmscore.industrydemo.page.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.base.BaseFragment;
import com.huawei.hmscore.industrydemo.databinding.ItemStoreBinding;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.event.EventUpdateRest;
import com.huawei.hmscore.industrydemo.page.activity.RestaurantDetailActivity;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;

import static com.huawei.hmscore.industrydemo.constants.KeyConstants.RESTAURANT_ID;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/8/30]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class HomeFragment extends BaseFragment {

    private Activity mContext;

    private RecyclerView mRecyclerView;

    StoreAdapter storeAdapter;

    public HomeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, ": onResume");
        updateRestListByRoom();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdateRest(EventUpdateRest event) {
        updateRestListByRoom();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null) {
            mContext = getActivity();
        }
        EventBus.getDefault().register(this);
        storeAdapter = new StoreAdapter(mContext);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(storeAdapter);

        updateRestListByRoom();

    }

    private void updateRestListByRoom() {
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        List<Restaurant> restaurants = restaurantRepository.queryAll();
        storeAdapter.refresh(restaurants);
    }

    public static class StoreAdapter extends BaseAdapter<ItemStoreBinding, Restaurant> {

        Activity context;
        private boolean showAdsFlag = false;

        public void setShowAdsFlag(boolean showAdsFlag) {
            this.showAdsFlag = showAdsFlag;
        }

        public StoreAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_store;
        }

        @Override
        public void setItemHolder(@NonNull BaseViewHolder holder, int position, Restaurant restaurant) {

            holder.bind.name.setText(String.valueOf(restaurant.getRestname()));

            holder.bind.price.setText(restaurant.getAvgprice());
            holder.bind.location.setText(restaurant.getAddress());
            holder.bind.rate.setRating(restaurant.getRate());
            holder.bind.cate.setText(restaurant.getFoodtype());
            if (restaurant.getStep() != null && !"NaN".equals(restaurant.getStep())) {
                holder.bind.step.setText(context.getString(R.string.rest_step, restaurant.getStep()));
            } else {
                holder.bind.step.setText(context.getString(R.string.rest_step, "5000"));
            }
            holder.bind.description.setText(restaurant.getDescription());
            holder.bind.img.setImageResource(
                context.getResources().getIdentifier(restaurant.getLogo(), "mipmap", context.getPackageName()));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra(RESTAURANT_ID,restaurant.getRestid());
                context.startActivity(intent);
            });

            int adsPosition = 1;
            if (!showAdsFlag && (position == adsPosition || (getItemCount() <= adsPosition && position == getItemCount() - 1))) {
                holder.bind.scrollViewAd.setVisibility(View.VISIBLE);
                new NativeAdTool(R.layout.native_video_template, context, holder.bind.scrollViewAd);
            } else {
                holder.bind.scrollViewAd.setVisibility(View.GONE);
            }
        }

        public static class NativeAdTool {

            private final String TAG = NativeAdTool.class.getSimpleName();

            private final ScrollView adScrollView;

            Activity mActivity;

            public NativeAdTool(int layoutId, Activity activity, ScrollView scrollView) {
                this.layoutId = layoutId;
                mActivity = activity;
                adScrollView = scrollView;
                loadAd("testu7m3hc4gvm");
                Log.d(TAG, "NativeAdTool: ");
            }

            public int layoutId;

            public NativeAd globalNativeAd;

            public VideoOperator.VideoLifecycleListener videoLifecycleListener;

            public VideoOperator.VideoLifecycleListener getVideoLifecycleListener() {
                return videoLifecycleListener;
            }

            /**
             * setVideoLifecycleListener
             *
             * @param videoLifecycleListener videoLifecycleListener
             */
            public void setVideoLifecycleListener(VideoOperator.VideoLifecycleListener videoLifecycleListener) {
                this.videoLifecycleListener = videoLifecycleListener;
            }

            /**
             * Load a native ad.
             *
             * @param adId ad slot ID.
             */
            public void loadAd(String adId) {
                NativeAdLoader.Builder builder = new NativeAdLoader.Builder(mActivity, adId);
                builder.setNativeAdLoadedListener(nativeAd -> {
                    // Display native ad.
                    showNativeAd(nativeAd);
                }).setAdListener(new AdListener() {
                    @Override
                    public void onAdFailed(int errorCode) {
                        Log.e(TAG, "onAdFailed: " + errorCode);
                    }
                });

                NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                    .build();

                NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
                nativeAdLoader.loadAd(new AdParam.Builder().build());

            }

            /**
             * Display native ad.
             *
             * @param nativeAd native ad object that contains ad materials.
             */
            public void showNativeAd(NativeAd nativeAd) {
                // Destroy the original native ad.
                if (null != globalNativeAd) {
                    globalNativeAd.destroy();
                }
                globalNativeAd = nativeAd;

                // Obtain NativeView.
                final NativeView nativeView = (NativeView) mActivity.getLayoutInflater().inflate(layoutId, null);

                // Register and populate a native ad material view.
                initNativeAdView(globalNativeAd, nativeView);
                globalNativeAd.setDislikeAdListener(new DislikeAdListener() {
                    @Override
                    public void onAdDisliked() {
                        // Call this method when an ad is closed.
                        adScrollView.removeView(nativeView);
                    }
                });

                // Add NativeView to the app UI.
                adScrollView.removeAllViews();
                adScrollView.addView(nativeView);
            }

            /**
             * Register and populate a native ad material view.
             *
             * @param nativeAd native ad object that contains ad materials.
             * @param nativeView native ad view to be populated into.
             */
            public void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
                // Register a native ad material view.
                nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
                nativeView.setMediaView(nativeView.findViewById(R.id.ad_media));
                nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
                nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

                // Populate a native ad material view.
                ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
                nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

                if (null != nativeAd.getAdSource()) {
                    ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
                }
                nativeView.getAdSourceView()
                    .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

                if (null != nativeAd.getCallToAction()) {
                    ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
                nativeView.getCallToActionView()
                    .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

                // Obtain a video controller.
                VideoOperator videoOperator = nativeAd.getVideoOperator();

                // Check whether a native ad contains video materials.
                if (videoOperator.hasVideo()) {
                    // Add a video lifecycle event listener.
                    videoOperator.setVideoLifecycleListener(videoLifecycleListener);
                }

                // Register a native ad object.
                nativeView.setNativeAd(nativeAd);
            }

        }

    }

}