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

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.databinding.ActPoiBinding;
import com.huawei.hmscore.industrydemo.databinding.ItemPoiBinding;
import com.huawei.hmscore.industrydemo.utils.LocationUtil;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class PoiAct extends BaseActivity implements View.OnClickListener {

    ActPoiBinding binding;
    SiteAdapter siteAdapter = new SiteAdapter();
    private SearchService searchService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_poi);
        binding = DataBindingUtil.setContentView(this, R.layout.act_poi);
        binding.lTitle.tvBaseTitle.setText(getString(R.string.address_choose));

        searchService = SearchServiceFactory.create(this, AgcUtil.getApiKey());

        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.setAdapter(siteAdapter);
        setKitList(new String[]{KitConstants.LOCATION_LBS, KitConstants.SITE_GEO});

        LocationUtil locationUtil = new LocationUtil(this);
        locationUtil.addLocationChangedlister(new LocationUtil.ILocationChangedLister() {
            @Override
            public void locationChanged(Location latLng) {

                nearBySearch(latLng, null);

                binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        nearBySearch(latLng, newText);
                        return false;
                    }
                });
            }
        });

        locationUtil.getMyLoction();

    }

    private void nearBySearch(Location latLng, String query) {
        Log.d(TAG, "nearBySearch: " + getApplicationContext());

        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        if (query != null) {
            request.setQuery(query);
        }

        request.setLocation(new Coordinate(latLng.getLatitude(), latLng.getLongitude()));

        Integer radiusInt = 1000;
        request.setRadius(radiusInt);

        // PageIndex Must be between 1 and 60
        Integer pageIndexInt = 1;
        request.setPageIndex(pageIndexInt);

        // PageSize Must be between 1 and 20
        Integer pageSizeInt = 20;
        request.setPageSize(pageSizeInt);

        // Create a search result listener.
        SearchResultListener resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                List<Site> siteList;
                if (results == null || results.getTotalCount() <= 0 || (siteList = results.getSites()) == null
                    || siteList.size() <= 0) {
                    showZeroToast();
                    return;
                }
                updateClusterData(siteList);
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.e(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
                if (Constants.ERROR_ZERO_RESULTS.equals(status.getErrorCode())) {
                    showZeroToast();
                }
            }
        };

        // Call the place search API.
        searchService.nearbySearch(request, resultListener);
    }

    private void showZeroToast() {
        Toast.makeText(PoiAct.this, R.string.zero_result_toast, Toast.LENGTH_LONG).show();
    }

    private void updateClusterData(List<Site> siteList) {
        siteAdapter.refresh(siteList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                onBackPressed();
                break;
        }
    }

    class SiteAdapter extends BaseAdapter<ItemPoiBinding, Site> {

        @Override
        public int getLayoutId() {
            return R.layout.item_poi;
        }

        @Override
        public void setItemHolder(@NonNull BaseViewHolder holder, int position, Site o) {

            holder.bind.nameShort.setText(o.getName());
            holder.bind.address.setText(o.getFormatAddress());
            holder.bind.meter.setText(o.getDistance() + "m");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("Site", o);
                setResult(RESULT_OK, intent);
                finish();
            });
        }
    }
}