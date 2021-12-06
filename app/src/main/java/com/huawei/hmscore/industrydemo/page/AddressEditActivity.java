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
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.huawei.hms.site.api.model.Site;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.constants.KitConstants;
import com.huawei.hmscore.industrydemo.databinding.ActAddressEditBinding;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.repository.UserAddressRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.LocationUtil;
import com.huawei.hmscore.industrydemo.utils.SystemUtil;

import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class AddressEditActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_ID = "KEY_ID";

    private ActAddressEditBinding binding;

    private UserAddressRepository userAddressRepository;

    boolean defaultFlag = true;

    boolean isAddMode = true;

    UserAddress mUserAddress;

    double addressLat;

    double addressLng;

    public static void start(Context context, int addressId) {
        Intent starter = new Intent(context, AddressEditActivity.class);
        if (addressId != -1) {
            starter.putExtra(KEY_ID, addressId);
        }
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.act_address_edit);
        userAddressRepository = new UserAddressRepository();

        Intent intent = getIntent();
        int id = intent.getIntExtra(KEY_ID, -1);
        mUserAddress = userAddressRepository.queryByNumber(id);
        if (SystemUtil.isChinaPackage(this)) {
            binding.location.setVisibility(View.GONE);
        }
        if (id != -1) {
            binding.name.setText(mUserAddress.name);
            binding.phone.setText(mUserAddress.phone);
            binding.address.setText(mUserAddress.address);
            binding.region.setText(mUserAddress.region);
            binding.defaultflag.setChecked(mUserAddress.defaultflag);
            binding.deleteAddress.setVisibility(View.VISIBLE);
            binding.lTitle.tvBaseTitle.setText(getString(R.string.address_manage));
            isAddMode = false;
        } else {
            binding.deleteAddress.setVisibility(View.GONE);
            binding.lTitle.tvBaseTitle.setText(getString(R.string.address_add));
            binding.defaultflag.setChecked(true);
        }

        binding.defaultflag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defaultFlag = isChecked;
            }
        });

        LocationUtil locationUtil = new LocationUtil(this);
        locationUtil.addLocationChangedlister(new LocationUtil.ILocationChangedLister() {
            @Override
            public void locationChanged(Location latLng) {
                addressLat = latLng.getLatitude();
                addressLng = latLng.getLongitude();
            }
        });

        locationUtil.getMyLoction();
        setKitList(new String[]{KitConstants.SITE_GEO});

    }

    private final int requestCodePoi = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                Intent intent = new Intent(AddressEditActivity.this, PoiAct.class);
                startActivityForResult(intent, requestCodePoi);
                break;
            case R.id.addAddress:
                String name = binding.name.getText().toString();
                String phone = binding.phone.getText().toString();
                String region = binding.region.getText().toString();
                String address = binding.address.getText().toString();
                if (isNullEmpty(name) || isNullEmpty(phone) || isNullEmpty(region) || isNullEmpty(address)) {
                    Toast
                        .makeText(AddressEditActivity.this, getString(R.string.please_enter_info),
                            Toast.LENGTH_SHORT)
                        .show();
                    break;
                }
                UserAddress useraddress = new UserAddress();
                if (!isAddMode) {
                    useraddress = mUserAddress;
                }
                useraddress.setName(name)
                    .setAddress(address)
                    .setRegion(region)
                    .setPhone(phone)
                    .setDefaultflag(defaultFlag)
                    .setLat(addressLat)
                    .setLng(addressLng);
                User user = new UserRepository().getCurrentUser();
                if (user != null) {
                    useraddress.setOpened(user.getOpenId());
                }

                UserAddress useraddressLastDefault = userAddressRepository.queryByFlag(true);
                if (defaultFlag) {
                    setDefaultAddress(useraddress);
                } else {
                    userAddressRepository.insert(useraddress);
                }
                finish();
                break;
            case R.id.deleteAddress:
                userAddressRepository.deleteById(mUserAddress.addressId);
                onBackPressed();
                break;
            case R.id.iv_base_back:
                onBackPressed();
                break;
            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodePoi && resultCode == RESULT_OK) {
            if(data == null){
                return;
            }
            Site site = data.getParcelableExtra("Site");
            if(site == null){
                return;
            }
            binding.region.setText(site.getName());
            binding.address.setText(site.getFormatAddress());
            addressLat = site.getLocation().getLat();
            addressLng = site.getLocation().getLng();
        }
    }

    private boolean isNullEmpty(String name) {
        return name == null || "".equals(name.trim());
    }

    private void setDefaultAddress(UserAddress o) {
        List<UserAddress> useraddressList = userAddressRepository.queryById();
        for (UserAddress u : useraddressList) {
            u.setDefaultflag(false);
        }
        userAddressRepository.insert(useraddressList);
        userAddressRepository.insert(o);
        Log.d(TAG, ": " + o.addressId);
    }

}
