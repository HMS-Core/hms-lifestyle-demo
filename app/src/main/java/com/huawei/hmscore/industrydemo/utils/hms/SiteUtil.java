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

package com.huawei.hmscore.industrydemo.utils.hms;

import android.app.Activity;
import android.util.Log;

import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import java.util.List;

public class SiteUtil {

    private final int MOCK_MARKER_NUM = 4;
    private final boolean IS_MOCK = true;
    private final double[][] ADDR_ADJUST_NUMBER = {{-0.001, -0.01}, {0.01, -0.001}, {0.01, 0.01}, {-0.01, 0.01}};


    private String TAG = SiteUtil.class.getSimpleName();

    private SearchService searchService;

    private Activity mActivity;

    private LatLng mLatLng;

    public SiteUtil(Activity activity, LatLng latLng) {
        searchService = SearchServiceFactory.create(activity, AgcUtil.getApiKey());
        mActivity = activity;
        mLatLng = latLng;
    }

    public void nearBySearch(LatLng latLng, final String query, SearchResultListener resultListener) {

        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        if (query != null) {
            request.setQuery(query);
        }

        request.setLocation(new Coordinate(latLng.latitude, latLng.longitude));

        Integer radiusInt = 5000;
        request.setRadius(radiusInt);

        // PageIndex Must be between 1 and 60
        Integer pageIndexInt = 1;
        request.setPageIndex(pageIndexInt);

        // PageSize Must be between 1 and 20
        Integer pageSizeInt = 20;
        request.setPageSize(pageSizeInt);

        // Call the place search API.
        searchService.nearbySearch(request, resultListener);
    }

    public List<Site> getMarkerAddr(List<Site> siteList) {
        String[] shopNameList = mActivity.getResources().getStringArray(R.array.shop_name_list);
        String[] shopAddrList = mActivity.getResources().getStringArray(R.array.shop_addr_list);

        // over 4 result
        if (IS_MOCK && siteList.size() > MOCK_MARKER_NUM) {
            double minDiff = 0.01d;
            if (Math.abs(siteList.get(0).getLocation().getLat() - siteList.get(1).getLocation().getLat()) < minDiff) {
                siteList.get(1).getLocation().setLat(siteList.get(1).getLocation().getLat() + minDiff);
            }

            if (Math.abs(siteList.get(2).getLocation().getLng() - siteList.get(0).getLocation().getLng()) < minDiff) {
                siteList.get(2).getLocation().setLng(siteList.get(2).getLocation().getLng() - minDiff);
            }


        } else { // If result is less than MOCK_MARKER_NUM, use mock data.
            Log.d(TAG, "getMarkerAddr: 0 result");

            for (int i = 0; i < MOCK_MARKER_NUM; i++) {
                Site site = new Site();
                site.setLocation(new Coordinate(mLatLng.latitude + ADDR_ADJUST_NUMBER[i][0],
                        mLatLng.longitude + ADDR_ADJUST_NUMBER[i][1]));
                site.setName(shopNameList[i]);
                site.setFormatAddress(shopAddrList[i]);
                siteList.add(site);
            }
        }

        return  siteList;
    }

}
