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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.databinding.ActAddressListBinding;
import com.huawei.hmscore.industrydemo.databinding.ItemAddressBinding;
import com.huawei.hmscore.industrydemo.entity.UserAddress;
import com.huawei.hmscore.industrydemo.repository.UserAddressRepository;

import java.util.Comparator;
import java.util.List;

public class AddressListActivity extends BaseActivity implements View.OnClickListener {

    ActAddressListBinding binding;

    UserAddressRepository userAddressRepository = new UserAddressRepository();

    AddressAdapter addressAdapter = new AddressAdapter();

    private int fromPageCode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromPageCode = getIntent().getIntExtra(KeyConstants.FROM_PAGE_CODE, -1);
        binding = DataBindingUtil.setContentView(this, R.layout.act_address_list);
        binding.lTitle.tvBaseTitle.setText(getString(R.string.address_manage));
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.setAdapter(addressAdapter);
        refreshDataFromRoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, ": onResume");
        refreshDataFromRoom();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAddress:
                AddressEditActivity.start(AddressListActivity.this, -1);
                break;
            case R.id.iv_base_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    class AddressAdapter extends BaseAdapter<ItemAddressBinding, UserAddress> {

        @Override
        public int getLayoutId() {
            return R.layout.item_address;
        }

        @Override
        public void setItemHolder(@NonNull BaseViewHolder holder, int position, UserAddress o) {

            holder.bind.name.setText(o.name);
            holder.bind.phone.setText(o.phone);
            holder.bind.address.setText(o.address);
            holder.bind.region.setText(o.region);
            holder.bind.defaultflag.setOnCheckedChangeListener(null);
            holder.bind.defaultflag.setChecked(o.defaultflag);
            if (o.defaultflag) {
                holder.bind.defaultflag.setVisibility(View.VISIBLE);
            } else {
                holder.bind.defaultflag.setVisibility(View.VISIBLE);
                Log.d(TAG, ": ");
            }

            holder.bind.defaultflag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    List<UserAddress> useraddressList = getmData();
                    if (isChecked) {
                        for (UserAddress u : useraddressList) {
                            u.setDefaultflag(u.addressId == o.addressId);
                        }
                    } else {
                        holder.bind.defaultflag.setChecked(true);
                        Toast.makeText(AddressListActivity.this, R.string.one_default_address, Toast.LENGTH_SHORT)
                            .show();
                    }
                    userAddressRepository.insert(useraddressList);
                    notifyDataSetChanged();
                }
            });

            holder.bind.delete.setOnClickListener(v -> {
                // TODO: 2021/10/13
                deleteById(o.addressId);
            });

            holder.bind.edit.setOnClickListener(v -> {
                AddressEditActivity.start(AddressListActivity.this, o.addressId);
            });

            holder.itemView.setOnClickListener(v -> {
                if (-1 == fromPageCode) {
                    AddressEditActivity.start(AddressListActivity.this, o.addressId);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.ADDRESS_ID, o.addressId);
                setResult(Constants.ADDRESS_RESULT_CODE, intent);
                finish();
            });
        }
    }

    private void deleteById(int addressid) {
        userAddressRepository.deleteById(addressid);
        refreshDataFromRoom();
    }

    private void refreshDataFromRoom() {
        List<UserAddress> useraddressList = userAddressRepository.queryById();
        useraddressList.sort(new Comparator<UserAddress>() {
            @Override
            public int compare(UserAddress o1, UserAddress o2) {
                return o2.addressId - o1.addressId;
            }
        });
        addressAdapter.refresh(useraddressList);
    }
}
