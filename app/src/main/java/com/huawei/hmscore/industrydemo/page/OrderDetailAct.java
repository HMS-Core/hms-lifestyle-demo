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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.databinding.ItemAddressBinding;
import com.huawei.hmscore.industrydemo.databinding.OrderDetailActBinding;
import com.huawei.hmscore.industrydemo.entity.Food;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.OrderItem;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.repository.FoodRepository;
import com.huawei.hmscore.industrydemo.repository.OrderRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserAddressRepository;
import com.huawei.hmscore.industrydemo.utils.NetworkRequestManager;
import com.huawei.hmscore.industrydemo.utils.TimeUtil;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.hms.SiteUtil;
import com.huawei.hmscore.industrydemo.viewadapter.OrderItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderDetailAct extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    private OrderDetailActBinding mBinding;

    private final int mAddressId = 1;
    private static final String KEY_ID = "KEY_ID";

    private final UserAddressRepository userAddressRepository = new UserAddressRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final RestaurantRepository restaurantRepository = new RestaurantRepository();
    private Order mOrder;
    int mOrderId;
    UserAddress mUserAddress;
    Restaurant restaurant;
    OrderItemAdapter orderItemAdapter = new OrderItemAdapter();

    private HuaweiMap hMap;
    private LatLng mLatLngBegin;
    private LatLng mLatLngEnd;
    private final List<Polyline> mPolylines = new ArrayList<>();
    private final List<List<LatLng>> mPaths = new ArrayList<>();
    private LatLngBounds mLatLngBounds;

    private SiteUtil siteUtil;

    public static void start(Context context, int id) {
        Intent starter = new Intent(context, OrderDetailAct.class);
        if (id != -1) {
            starter.putExtra(KEY_ID, id);
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(starter);
        }
    }

    private final int DEFAULT_ORDER_ID = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.order_detail_act);
        mBinding.lTitle.tvBaseTitle.setText(getString(R.string.my_order));

        Intent intent = getIntent();
        mOrderId = intent.getIntExtra(KEY_ID, DEFAULT_ORDER_ID);
        if (mOrderId != DEFAULT_ORDER_ID) {
            mOrder = orderRepository.queryByOrderI(mOrderId);
            mUserAddress = mOrder.getUserAddress();
            restaurant = restaurantRepository.queryByNumber(mOrder.getRestId());
            updateRestAddressInfo();
            mBinding.lTitle.tvBaseTitle.setText(mOrder.getStatusText());
        }
        setKitList(new String[]{KitConstants.MAP_DELIVERY});
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);


        if (mOrderId != DEFAULT_ORDER_ID) {
            updateMapAndRouter(mUserAddress);
        }
    }

    private void updateRestAddressInfo() {
        mBinding.address.setText(mUserAddress.getAddress());
        mBinding.phone.setText(mUserAddress.getPhone());
        mBinding.name.setText(mUserAddress.getName());

        String priceActual = String.valueOf(mOrder.getActualPrice()) + getString(R.string.money_unit);
        String discount = String.valueOf(mOrder.getDiscount1()+mOrder.getDiscount2()) + getString(R.string.money_unit);
        String restName = restaurant.getRestname();
        String time = TimeUtil.formatLongToMDGMTimeStr(mOrder.getTime());
        mBinding.restName.setText(restName);
        mBinding.price.setText(priceActual);
        mBinding.discountAll.setText(discount);
        mBinding.time.setText(time);
        String timeSend =
            TimeUtil.formatLongToMDGMTimeStr(TimeUtil.getTimeLongPlusMin(mOrder.getTime(), TimeUtil.DELIVERY_TIME));
        mBinding.timeSend.setText(timeSend);

        mLatLngBegin = new LatLng(restaurant.getPoslat(),restaurant.getPoslng());

        // have sent
        mBinding.rvOrderItems.setLayoutManager(new LinearLayoutManager(OrderDetailAct.this));
        mBinding.rvOrderItems.setAdapter(orderItemAdapter);
        List<OrderItem> orderItems = new OrderRepository().queryItemByOrder(mOrder);
        orderItemAdapter.refresh(orderItems);
        mBinding.packingPrice
                .setText(getString(R.string.total_price, Constants.PACKING_CHARGES));
        mBinding.deliveryPrice
                .setText(getString(R.string.total_price, Constants.DELIVERY_CHARGES));
        mBinding.discount.setText(getString(R.string.total_price, (mOrder.getDiscount1()+mOrder.getDiscount2())));
        mBinding.subtotal.setText(getString(R.string.total_price, mOrder.getActualPrice()));
        mBinding.restName2.setText(restName);

        switch (mOrder.getStatus()) {
            case Constants.ORDER_HAVE_SENT:
            case Constants.ORDER_HAVE_ENSURE:
            case Constants.ORDER_HAVE_COMMENT:
                mBinding.lStatusSending.setVisibility(View.GONE);
                mBinding.lStatusReceived.setVisibility(View.VISIBLE);
                mBinding.map.setVisibility(View.GONE);
                break;

            case Constants.ORDER_WAIT_SEND:
            default: // wait pay
                // load map
                mBinding.map.onCreate(null);
                mBinding.map.getMapAsync(this);
                mBinding.lStatusSending.setVisibility(View.VISIBLE);
                mBinding.lStatusReceived.setVisibility(View.GONE);
                break;
        }

    }

    private void updateMapAndRouter(UserAddress address) {
        LatLng latLng = new LatLng(address.lat, address.lng);
        mLatLngEnd = latLng;
        updateLocation(latLng);
        getWalkingRouteResult();
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).clusterable(false);
        hMap.addMarker(markerOptions);
    }



    public void getWalkingRouteResult() {
        addMarker(mLatLngBegin);
        addMarker(mLatLngEnd);

        removePolylines();
        NetworkRequestManager.getWalkingRoutePlanningResult(mLatLngBegin, mLatLngEnd,
            new NetworkRequestManager.OnNetworkListener() {
                @Override
                public void requestSuccess(String result) {
                    generateRoute(result);
                }

                @Override
                public void requestFail(String errorMsg) {
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("errorMsg", errorMsg);
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });
    }

    private void generateRoute(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.optJSONArray("routes");
            if (null == routes || routes.length() == 0) {
                return;
            }
            JSONObject route = routes.getJSONObject(0);

            // get route bounds
            JSONObject bounds = route.optJSONObject("bounds");
            if (null != bounds && bounds.has("southwest") && bounds.has("northeast")) {
                JSONObject southwest = bounds.optJSONObject("southwest");
                JSONObject northeast = bounds.optJSONObject("northeast");
                LatLng sw = new LatLng(southwest.optDouble("lat"), southwest.optDouble("lng"));
                LatLng ne = new LatLng(northeast.optDouble("lat"), northeast.optDouble("lng"));
                mLatLngBounds = new LatLngBounds(sw, ne);
            }
            // get paths
            JSONArray paths = route.optJSONArray("paths");
            for (int i = 0; i < paths.length(); i++) {
                JSONObject path = paths.optJSONObject(i);
                List<LatLng> mPath = new ArrayList<>();

                JSONArray steps = path.optJSONArray("steps");
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = steps.optJSONObject(j);

                    JSONArray polyline = step.optJSONArray("polyline");
                    for (int k = 0; k < polyline.length(); k++) {
                        if (j > 0 && k == 0) {
                            continue;
                        }
                        JSONObject line = polyline.getJSONObject(k);
                        double lat = line.optDouble("lat");
                        double lng = line.optDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        mPath.add(latLng);
                    }
                }
                mPaths.add(i, mPath);
            }
            mHandler.sendEmptyMessage(0);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException" + e.toString());
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    renderRoute(mPaths, mLatLngBounds);
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    AgcUtil.reportFailure(TAG, errorMsg);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Render the route planning result
     *
     * @param paths
     * @param latLngBounds
     */
    private void renderRoute(List<List<LatLng>> paths, LatLngBounds latLngBounds) {
        if (null == paths || paths.size() <= 0 || paths.get(0).size() <= 0) {
            return;
        }

        for (int i = 0; i < paths.size(); i++) {
            List<LatLng> path = paths.get(i);
            PolylineOptions options = new PolylineOptions().color(Color.BLUE).width(5);
            for (LatLng latLng : path) {
                options.add(latLng);
            }

            Polyline polyline = hMap.addPolyline(options);
            mPolylines.add(i, polyline);
        }

        if (null != latLngBounds) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 5);
            hMap.moveCamera(cameraUpdate);
        } else {
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paths.get(0).get(0), 13));
        }

    }

    private void removePolylines() {
        for (Polyline polyline : mPolylines) {
            polyline.remove();
        }

        mPolylines.clear();
        mPaths.clear();
        mLatLngBounds = null;
    }

    private void updateLocation(LatLng latLng) {
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.9f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    class FoodAdapter extends BaseAdapter<ItemAddressBinding, OrderItem> {

        @Override
        public int getLayoutId() {
            return R.layout.item_address;
        }

        @Override
        public void setItemHolder(@NonNull BaseViewHolder holder, int position, OrderItem o) {

            FoodRepository foodRepository = new FoodRepository();
            Food food = foodRepository.queryByFoodId(o.getFoodId());
            holder.bind.name.setText(food.getFoodName());

        }
    }

}
