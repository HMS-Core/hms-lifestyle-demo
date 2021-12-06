/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hmscore.industrydemo.page.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.MessageConstants;
import com.huawei.hmscore.industrydemo.entity.Message;
import com.huawei.hmscore.industrydemo.repository.MessageRepository;
import com.huawei.hmscore.industrydemo.viewadapter.MessageInfoAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MessageInfoActivity extends Activity implements MessageInfoAdapter.OnItemClickLitener, OnLoadMoreListener, View.OnClickListener {
    private static final String TAG = MessageInfoActivity.class.getSimpleName();
    private RecyclerView mRvView;
    private MessageRepository mMessageRepository;
    private MessageInfoAdapter mMessageInfoAdapter;
    private SmartRefreshLayout mSfView;
    private List<Message> mMessageInfos;
    private ImageView mClose;
    private TextView mTvNodata;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageinfo);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        setViewListenr();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessageInfos.clear();
        List<Message> messageInfos = mMessageRepository.queryAllExcludeRepeat();
        mMessageInfos.addAll(messageInfos);
        refreshView(mMessageInfos);
    }

    /**
     * set View
     */
    private void setViewListenr() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRvView.setLayoutManager(layoutManager);
        mRvView.setItemAnimator(new DefaultItemAnimator());
        mSfView.setEnableLoadMore(true);
        mSfView.setEnableRefresh(false);
        mClose.setOnClickListener(this);
    }

    /**
     * jump to sender page
     *
     * @param position
     * @param messageInfos
     */
    private void jumpToSenderPage(int position, List<Message> messageInfos) {
        Log.i(TAG, "jumpToSenderPage");

        Log.i(TAG, "jumpToSenderPage" + messageInfos.get(position).getSenderId() +"" );
        Intent intent = new Intent(MessageInfoActivity.this, MessageInfoDetailActivity.class);
        intent.putExtra(MessageConstants.SENDERID, messageInfos.get(position).getSenderId());
        intent.putExtra(MessageConstants.SENDERNAME, messageInfos.get(position).getMessageTitle());
        startActivity(intent);
    }

    /**
     * init data
     */
    private void initData() {
        mMessageInfos = new ArrayList<>();
        mMessageRepository = new MessageRepository();
    }

    private void refreshView(List<Message> messageInfos) {
        if (messageInfos == null) {
            Log.i(TAG, "messageInfos is null");
            return;
        }
        showOrHideView(messageInfos);
        if (null == mMessageInfoAdapter) {
            mMessageInfoAdapter = new MessageInfoAdapter(MessageInfoActivity.this,messageInfos);
            mMessageInfoAdapter.refresh(messageInfos);
            mMessageInfoAdapter.setOnItemClickLitener(this);
            mRvView.setAdapter(mMessageInfoAdapter);
        } else {
            mMessageInfoAdapter.notifyDataSetChanged();
        }
    }

    private void showOrHideView(List<Message> messageInfos) {
        if (messageInfos.size() == MessageConstants.ZERO) {
            mTvNodata.setVisibility(View.VISIBLE);
            mRvView.setVisibility(View.GONE);
        } else {
            mTvNodata.setVisibility(View.GONE);
            mRvView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mRvView = findViewById(R.id.rv_messageinfo);
        mSfView = findViewById(R.id.sf_messageinfo);
        mClose = findViewById(R.id.iv_messageinfo_close);
        mTvNodata = findViewById(R.id.tv_no_data);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "onItemClick:" + position);
        if (mMessageInfos == null || mMessageInfos.size() == MessageConstants.ZERO) {
            Log.i(TAG, "messageInfos is null");
            return;
        }
        refreshView(mMessageInfos);
        jumpToSenderPage(position, mMessageInfos);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (false) {
            mMessageInfos.clear();
            List<Message> messageInfos = mMessageRepository.queryAllExcludeRepeat();
            mMessageInfos.addAll(messageInfos);
            mSfView.autoRefresh();
            refreshView(mMessageInfos);
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
