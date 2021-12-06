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

import static com.huawei.hmscore.industrydemo.constants.Constants.ORDER_HAVE_COMMENT;
import static com.huawei.hmscore.industrydemo.constants.Constants.ORDER_HAVE_ENSURE;
import static com.huawei.hmscore.industrydemo.constants.Constants.ORDER_HAVE_SENT;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivity;
import com.huawei.hmscore.industrydemo.base.BaseAdapter;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.databinding.ActOrderListBinding;
import com.huawei.hmscore.industrydemo.databinding.ItemOrderBinding;
import com.huawei.hmscore.industrydemo.entity.Order;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.repository.OrderRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.repository.UserAddressRepository;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.TimeUtil;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

import java.util.Comparator;
import java.util.List;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class OrderListActivity extends BaseActivity implements View.OnClickListener {

    ActOrderListBinding binding;

    UserAddressRepository userAddressRepository = new UserAddressRepository();
    OrderRepository orderRepository = new OrderRepository();

    OrderAdapter orderAdapter = new OrderAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.act_order_list);
        binding.lTitle.tvBaseTitle.setText(getString(R.string.order_center));
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.setAdapter(orderAdapter);
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
            case R.id.iv_base_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    class OrderAdapter extends BaseAdapter<ItemOrderBinding, Order> {

        @Override
        public int getLayoutId() {
            return R.layout.item_order;
        }

        @Override
        public void setItemHolder(@NonNull BaseViewHolder holder, int position, Order o) {

            Restaurant restaurant = new RestaurantRepository().queryByNumber(o.getRestId());
            if (restaurant != null) {
                holder.bind.name.setText(restaurant.getRestname());
                holder.bind.img.setImageResource(
                        getResources().getIdentifier(restaurant.getLogo(), "mipmap", getPackageName()));
            }

            holder.bind.status.setText(o.getStatusText());
            holder.bind.time.setText(TimeUtil.formatLongToMDGMTimeStr(o.getTime()));
            String price = String.valueOf(o.getActualPrice()) + getString(R.string.money_unit);
            holder.bind.price.setText(price);

            if (o.getStatus() == ORDER_HAVE_SENT || o.getStatus() == ORDER_HAVE_ENSURE || o.getStatus() == ORDER_HAVE_COMMENT) {
                holder.bind.delete.setVisibility(View.VISIBLE);
                holder.bind.delete.setOnClickListener(v->{
                    deleteOrder(o.getOrderId());
                });
            }

            holder.itemView.setOnClickListener(v -> {
                switch (o.getStatus()){
                    case Constants.ORDER_WAIT_PAY:
                        CashierActivity.startCashierActivity(OrderListActivity.this, o.getOrderId());
                        break;
                    default:
                        OrderDetailAct.start(OrderListActivity.this, o.getOrderId());
                        break;
                }

            });
        }
    }

    private void deleteOrder(int orderid) {
        Bundle deleteConfirm = new Bundle();
        deleteConfirm.putString(BaseDialog.CANCEL_BUTTON, getString(R.string.cancel));
        deleteConfirm.putString(BaseDialog.CONFIRM_BUTTON,getString(R.string.confirm));
        deleteConfirm.putString(BaseDialog.CONTENT, getString(R.string.order_delete_confirm));
        BaseDialog dialog = new BaseDialog(this, deleteConfirm, true);
        dialog.setConfirmListener(v -> {
            deleteById(orderid);
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void deleteById(int orderid) {
        orderRepository.deleteById(orderid);
        refreshDataFromRoom();
    }

    private void refreshDataFromRoom() {
        List<Order> orders = orderRepository.queryByUser(new UserRepository().getCurrentUser());
        if (orders == null || orders.size() == 0) {
            orders = orderRepository.queryAll();
        }
        if (orders == null || orders.size() == 0) {
            Toast.makeText(this, getString(R.string.zero_result_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        orders.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getOrderId() - o1.getOrderId();
            }
        });
        orderAdapter.refresh(orders);
    }
}
