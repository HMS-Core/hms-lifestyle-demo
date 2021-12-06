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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.constants.MessageConstants;
import com.huawei.hmscore.industrydemo.entity.Message;
import com.huawei.hmscore.industrydemo.repository.MessageRepository;
import com.huawei.hmscore.industrydemo.viewadapter.MessageInfoDetailsAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MessageInfoDetailActivity extends Activity implements View.OnClickListener, OnLoadMoreListener, MessageInfoDetailsAdapter.OnItemClickLitener {
    private RecyclerView mRvView;
    private MessageRepository mMessageRepository;
    private MessageInfoDetailsAdapter mMessageInfoAdapter;
    private SmartRefreshLayout mSfView;
    private static final String TAG = MessageInfoDetailActivity.class.getSimpleName();
    private List<Message> mMessageInfos;
    private TextView mMessageTitle;
    private String mSenderName;
    private ImageView mMessageClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageinfo);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mMessageClose.setOnClickListener(this);
        setListener();
    }

    private void setListener() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRvView.setLayoutManager(layoutManager);
        mRvView.setItemAnimator(new DefaultItemAnimator());
        mSfView.setEnableLoadMore(true);
        mSfView.setEnableRefresh(false);
    }

    private void initData() {
        mMessageInfos = new ArrayList<>();
        Intent intent = getIntent();
        if (null == intent) {
            Log.i(TAG, "intent is null");
            return;
        }
        mMessageRepository = new MessageRepository();
        int senderID = intent.getIntExtra(MessageConstants.SENDERID, 1);
        mSenderName = intent.getStringExtra(MessageConstants.SENDERNAME);
        mMessageInfos = mMessageRepository.queryAllBySenderID(senderID);
        refreshView(mMessageInfos);
    }

    private void refreshView(List<Message> messageInfos) {
        mMessageTitle.setText(mSenderName);
        mMessageInfoAdapter = new MessageInfoDetailsAdapter(this, messageInfos,this);
        mMessageInfoAdapter.refresh(messageInfos);
        mRvView.setAdapter(mMessageInfoAdapter);
    }

    private void initView() {
        mRvView = findViewById(R.id.rv_messageinfo);
        mSfView = findViewById(R.id.sf_messageinfo);
        mMessageTitle = findViewById(R.id.tv_messageinfo_title);
        mMessageClose = findViewById(R.id.iv_messageinfo_close);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(MessageInfoDetailActivity.this, getString(R.string.messageinfo_hints), Toast.LENGTH_SHORT).show();
        if (mMessageInfos == null || mMessageInfos.size() == 0) {
            Log.i(TAG, "messageInfos is null");
            return;
        }
        Message messageInfo = mMessageInfos.get(position);
        messageInfo.setUnRead(false);
        messageInfo.setSenderId(messageInfo.getSenderId());
        messageInfo.setMessageId(messageInfo.getMessageId());
        mMessageRepository.updateRedTips(messageInfo);
        refreshView(mMessageInfos);
    }
}
