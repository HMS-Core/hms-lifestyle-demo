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

package com.huawei.hmscore.industrydemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationUtil {
    private final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_CODE_OPEN_GPS = 0x11;

    private List<ILocationChangedLister> locationChangedList = new ArrayList<>();

    // location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest mLocationRequest;

    private Activity mContext;

    public LocationUtil(Activity mContext) {
        this.mContext = mContext;
    }


    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult);
            Location location = locationResult.getLastLocation();
            updateLocationLister(location);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability: " + locationAvailability.toString());
        }
    };

    private final Random generator = new Random();

    public int getRandomNumber() {
        return generator.nextInt();
    }

    public void addLocationChangedlister(ILocationChangedLister iLocationChangedLister) {
        locationChangedList.add(iLocationChangedLister);
    }

    public void getMyLoction() {

        Log.d(TAG, "getMyLoction: ");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLastLocation();
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(1000);
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        // location setting
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(locationSettingsResponse -> fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: " + aVoid)))
            .addOnFailureListener(e -> {
                String sError = "onError Location: "  + e.getCause() + e.getMessage();
                Log.e(TAG, sError, e);
                int statusCodes = ((ApiException) e).getStatusCode();
                switch (statusCodes) {
                    // code 6, need open gps switch
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                            ResolvableApiException raes = (ResolvableApiException) e;
                            raes.startResolutionForResult(mContext, REQUEST_CODE_OPEN_GPS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request");
                        }
                        break;
                    default:
                        AgcUtil.reportFailure(TAG, sError);
                        getLngAndLatWithNetwork();
                        break;
                }
            });
    }

    public void getLastLocation() {
        Log.d(TAG, "getLastLocation: ");
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                updateLocationLister(location);
            }
        });
    }


    /**
     * Obtaining Location Availability
     */
    public void getLocationAvailability(OnSuccessListener onSuccessListener,OnFailureListener onFailureListener) {
        try {
            Task<LocationAvailability> locationAvailability = fusedLocationProviderClient.getLocationAvailability();
            locationAvailability.addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
        } catch (Exception e) {
            Log.e(TAG, "getLocationAvailability exception:" + e.getMessage());
        }
    }

    private  Location location;

    private void getLngAndLatWithNetwork() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "getLngAndLatWithNetwork " + location);
        updateLocationLister(location);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location loc) {
            Log.d(TAG, "==onLocationChanged==: " + loc);
            location = loc;
            updateLocationLister(loc);
        }
    };

    private void updateLocationLister(Location location) {
        if(location != null){
            Log.w(TAG, "onLocationResult:Latitude " + location.getLatitude());
            Log.w(TAG, "onLocationResult:Longitude " + location.getLongitude());
            for (ILocationChangedLister locationChanged : locationChangedList) {
                locationChanged.locationChanged(location);
            }
        }
    }

    public interface ILocationChangedLister {

        /**
         * Update the location information
         *
         * @param latLng The new location information
         */
        void locationChanged(Location latLng);
    }

}
