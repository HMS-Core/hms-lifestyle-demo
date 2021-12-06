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

package com.huawei.hmscore.industrydemo.page.viewmodel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.base.BaseActivityViewModel;
import com.huawei.hmscore.industrydemo.constants.Constants;
import com.huawei.hmscore.industrydemo.constants.KeyConstants;
import com.huawei.hmscore.industrydemo.constants.RequestCodeConstants;
import com.huawei.hmscore.industrydemo.page.activity.SearchActivity;
import com.huawei.hmscore.industrydemo.page.activity.SearchResultActivity;
import com.huawei.hmscore.industrydemo.repository.UserRepository;
import com.huawei.hmscore.industrydemo.utils.agc.AgcUtil;
import com.huawei.hmscore.industrydemo.utils.hms.MLUtil;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;
import com.huawei.hmscore.industrydemo.wight.SearchContentLayout;
import com.huawei.hmscore.industrydemo.wight.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.huawei.hms.analytics.type.HAEventType.SEARCH;
import static com.huawei.hms.analytics.type.HAParamType.SEARCHKEYWORDS;

/**
 * @version [HMSCore-Demo 3.0.0.300, 2021/9/27]
 * @see [Related Classes/Methods]
 * @since [HMSCore-Demo 3.0.0.300]
 */
public class SearchActivityViewModel extends BaseActivityViewModel<SearchActivity> {
    private static final String TAG = "SearchActivityViewModel";

    private static final int MAX_SIZE = 3;

    private static final int VOICE_ICON_TO_BOTTOM = 100;

    private static final int SEARCH_RESULT = 101;

    private EditText searchEdit;

    private SearchView searchView;

    private ImageView ivDelete;

    private SearchContentLayout scHistory;

    private TextView tvHistory;

    private SearchContentLayout scHot;

    private TextView tvHot;

    private List<String> historyList;

    private String searchContent;

    private boolean needCheck;

    private ImageView ivVoice;

    private RelativeLayout rlSearch;

    private final UserRepository mUserRepository;

    /**
     * constructor
     *
     * @param searchActivity Activity object
     */
    public SearchActivityViewModel(SearchActivity searchActivity) {
        super(searchActivity);
        MLApplication.getInstance().setApiKey(AgcUtil.getApiKey(mActivity));

        mUserRepository = new UserRepository();
    }

    @Override
    public void initView() {
        searchView = mActivity.findViewById(R.id.view_search);
        searchEdit = searchView.findViewById(R.id.et_search);
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                edSearch();
                return true;
            }
            return false;
        });
        searchView.setIvBackListener(v -> mActivity.finish());
        tvHistory = mActivity.findViewById(R.id.tv_history);
        tvHot = mActivity.findViewById(R.id.tv_hot);
        scHistory = mActivity.findViewById(R.id.view_history);
        scHot = mActivity.findViewById(R.id.view_hot);

        ivDelete = mActivity.findViewById(R.id.search_delete);
        ivDelete.setOnClickListener(v -> initDialog());

        ivVoice = mActivity.findViewById(R.id.iv_voice);
        ivVoice.setOnClickListener(v -> startAsr());
        ivVoice.setOnLongClickListener(v -> {
            startAsr();
            return true;
        });

        rlSearch = mActivity.findViewById(R.id.rl_search);
        rlSearch.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rlSearch.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rlSearch.getRootView().getHeight();
            int softHeight = screenHeight - (rect.bottom - rect.top) - VOICE_ICON_TO_BOTTOM + 24;
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(142, 142);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (softHeight > 200) {
                rl.setMargins(0, 0, 0, softHeight);
            } else {
                rl.setMargins(0, 0, 0, VOICE_ICON_TO_BOTTOM);
            }
            ivVoice.setLayoutParams(rl);
        });
    }

    /**
     * initHotList
     */
    public void initHotList() {
        String[] hots = mActivity.getResources().getStringArray(R.array.search_hot);
        if (hots.length > 1) {
            for (String item : hots) {
                scHot.addView(addTagItem(item, true));
            }
        } else {
            scHot.setVisibility(View.GONE);
            tvHot.setVisibility(View.GONE);
        }
    }

    /**
     * initHistoryList
     */
    public void initHistoryList() {
        if (historyList == null) {
            historyList = new ArrayList<>();
        }
        String[] data = mUserRepository.getHistorySearch();
        if (data.length > 0) {
            historyList.clear();
            scHistory.removeAllViews();
            historyList.addAll(Arrays.asList(data));
        }
        if (historyList.size() <= 0) {
            scHistory.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            return;
        }
        scHistory.setVisibility(View.VISIBLE);
        tvHistory.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        for (int i = 0; i < historyList.size(); i++) {
            scHistory.addView(addTagItem(historyList.get(i), false));
        }
    }

    private View addTagItem(String item, boolean needCheck) {
        TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_search_conent, scHistory, false);
        tv.setText(item);
        tv.setOnClickListener(v -> goSearch(item, needCheck));
        return tv;
    }

    /**
     * Search Final Entry
     *
     * @param content search content
     * @param needCheck Whether duplicate check is required
     */
    private void goSearch(String content, boolean needCheck) {
        this.searchContent = content;
        this.needCheck = needCheck;

        /* Report search event begin */
        HiAnalyticsInstance instance = HiAnalytics.getInstance(mActivity);
        Bundle bundle = new Bundle();

        bundle.putString(SEARCHKEYWORDS, searchContent);
        instance.onEvent(SEARCH, bundle);
        /* Report search event end */

        Intent intent = new Intent(mActivity, SearchResultActivity.class);
        intent.putExtra(KeyConstants.SEARCH_CONTENT, searchContent);
        mActivity.startActivityForResult(intent, SEARCH_RESULT);
    }

    /**
     * addTagItem
     */
    public void addTagItem() {
        if (needCheck && checkSearchItem(searchContent)) {
            historyList.add(0, searchContent);
            if (historyList.size() > MAX_SIZE) {
                historyList.remove(MAX_SIZE);
                scHistory.removeViewAt(MAX_SIZE - 1);
            }

            mUserRepository.setHistorySearch(historyList.toArray(new String[0]));
            if (scHistory.getVisibility() == View.GONE) {
                scHistory.setVisibility(View.VISIBLE);
                tvHistory.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }
            scHistory.addView(addTagItem(searchContent, false), 0);
        }
        searchEdit.setText("");
    }

    /**
     * Check for duplicate items.
     *
     * @param item item
     * @return true:No repetition false：Duplicate
     */
    private boolean checkSearchItem(String item) {
        if (item == null || "".equals(item)) {
            return false;
        }
        if (null == historyList || historyList.size() <= 0) {
            return true;
        }
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).equals(item)) {
                return false;
            }
        }
        return true;
    }

    private void edSearch() {
        String content = searchEdit.getText().toString();
        if ("".equals(content)) {
            Toast.makeText(mActivity, R.string.search_no_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        goSearch(content, true);
    }

    private void initDialog() {
        Bundle data = new Bundle();

        data.putString(BaseDialog.CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
        data.putString(BaseDialog.CONTENT, mActivity.getString(R.string.check_delete_history));
        data.putString(BaseDialog.CANCEL_BUTTON, mActivity.getString(R.string.cancel));

        BaseDialog dialog = new BaseDialog(mActivity, data, true);
        dialog.setConfirmListener(v -> {
            mUserRepository.setHistorySearch(new String[0]);
            historyList.clear();
            scHistory.removeAllViews();
            initHistoryList();
            dialog.dismiss();
        });
        dialog.setCancelListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void startAsr() {
        if ((ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            goToAsr();
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.RECORD_AUDIO},
                    Constants.CAMERA_RECORD_AUDIO_CODE);
        }
    }

    private void goToAsr() {
        new MLUtil().goToAsr(mActivity);
    }

    @Override
    public void onClickEvent(int viewId) {
    }

    private void dealAsrResult(int resultCode, Intent data) {
        String text = new MLUtil().dealAsrResult(resultCode, data, mActivity);
        searchEdit.setText(text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SEARCH_RESULT) {
            addTagItem();
        }
        if (null == data) {
            return;
        }
        if (requestCode == RequestCodeConstants.ML_ASR_CAPTURE_CODE) {
            dealAsrResult(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        goToAsr();
    }
}
