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

package com.huawei.hmscore.industrydemo.viewadapter;

import static com.huawei.hmscore.industrydemo.utils.hms.WalletUtil.SAVE_FLAG;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CANCEL_BUTTON;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CONFIRM_BUTTON;
import static com.huawei.hmscore.industrydemo.wight.BaseDialog.CONTENT;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.wallet.AutoResolvableForegroundIntentResult;
import com.huawei.hms.wallet.CreateWalletPassRequest;
import com.huawei.hms.wallet.ResolveTaskHelper;
import com.huawei.hms.wallet.Wallet;
import com.huawei.hms.wallet.WalletPassClient;
import com.huawei.hmscore.industrydemo.R;
import com.huawei.hmscore.industrydemo.entity.Card;
import com.huawei.hmscore.industrydemo.entity.Restaurant;
import com.huawei.hmscore.industrydemo.entity.User;
import com.huawei.hmscore.industrydemo.inteface.OnNonDoubleClickListener;
import com.huawei.hmscore.industrydemo.page.VoucherMgtActivity;
import com.huawei.hmscore.industrydemo.page.fragment.vouchermanagement.CardFragment;
import com.huawei.hmscore.industrydemo.repository.CardRepository;
import com.huawei.hmscore.industrydemo.repository.RestaurantRepository;
import com.huawei.hmscore.industrydemo.utils.hms.WalletUtil;
import com.huawei.hmscore.industrydemo.wight.BaseDialog;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * CardList Adapter
 *
 * @version [HMSCore-Demo 3.0.0.300, 2021/10/9]
 * @see CardFragment
 * @see [Related Classes/Methods]
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder> {
    private static final String TAG = CardListAdapter.class.getSimpleName();

    private final VoucherMgtActivity mActivity;

    private final int[] card_bdg = {R.mipmap.card_bg_1, R.mipmap.card_bg_2, R.mipmap.card_bg_3};

    private List<Card> cardList;

    private final RequestOptions option = new RequestOptions().circleCrop()
        .placeholder(R.mipmap.head_load)
        .error(R.mipmap.head_my)
        .signature(new ObjectKey(UUID.randomUUID().toString()))
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true);

    public CardListAdapter(VoucherMgtActivity activity, User user) {
        this.mActivity = activity;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        Restaurant restaurant = restaurantRepository.queryByNumber(card.getRestId());
        holder.card.setBackgroundResource(card_bdg[position % 3]);
        int logoId = mActivity.getResources().getIdentifier(restaurant.getLogo(), "mipmap", mActivity.getPackageName());
        Glide.with(mActivity).load(logoId).apply(option).into(holder.cardIcon);
        holder.cardName.setText(restaurant.getRestname());
        holder.cardNumber.setText(String.valueOf(card.getCardId()));

        if (!TextUtils.isEmpty(card.getWalletId())) {
            holder.addToWallet.setVisibility(View.GONE);
        }

        holder.addToWallet.setOnClickListener(new OnNonDoubleClickListener(2000) {
            @Override
            public void run(View v) {
                String walletId = String.valueOf(System.currentTimeMillis()) + new SecureRandom().nextInt();
                Card tempCard = new Card();
                tempCard.setCardId(card.getCardId());
                tempCard.setWalletId(walletId);

                String jwe = WalletUtil.getJwe(mActivity, tempCard, restaurant, "");
                if (!jwe.isEmpty()) {
                    CreateWalletPassRequest request = CreateWalletPassRequest.getBuilder().setContent(jwe).build();
                    WalletPassClient walletObjectsClient = Wallet.getWalletPassClient(mActivity);
                    Task<AutoResolvableForegroundIntentResult> task = walletObjectsClient.createWalletPass(request);
                    // SAVE_FLAG is a task execution flag used to receive the task execution result.
                    ResolveTaskHelper.excuteTask(task, mActivity, SAVE_FLAG);
                }
            }
        });

        holder.cardRepair.setOnClickListener(new View.OnClickListener() {
            private final int COUNTS = 5; // Number of clicks

            private long[] mHits = new long[COUNTS]; // Record clicks

            private final long DURATION = 2000L; // Validity Period

            @Override
            public void onClick(View view) {
                Log.d(TAG, Arrays.toString(mHits));
                // Moves all elements in the mHints array to the left by one position
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                // Obtains the time when the current system has been started.
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    // Related Logical Operations
                    Bundle data = new Bundle();
                    data.putString(CONFIRM_BUTTON, mActivity.getString(R.string.confirm));
                    data.putString(CANCEL_BUTTON, mActivity.getString(R.string.cancel));
                    data.putString(CONTENT, "NOTICE");

                    BaseDialog dialog = new BaseDialog(mActivity, data, true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setConfirmListener(v -> {
                        card.setWalletId("");
                        new CardRepository().insert(card);
                        WalletUtil.card = null;
                        dialog.dismiss();
                    });
                    dialog.setCancelListener(v -> dialog.dismiss());
                    dialog.show();
                    // Initialize clicks
                    mHits = new long[COUNTS];
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cardList != null) {
            return cardList.size();
        } else {
            return 0;
        }
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout card;

        ImageView cardIcon;

        TextView cardName;

        TextView cardNumber;

        ConstraintLayout addToWallet;

        TextView cardRepair;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.layout_card);
            cardIcon = itemView.findViewById(R.id.iv_card_icon);
            cardName = itemView.findViewById(R.id.tv_card_name);
            cardNumber = itemView.findViewById(R.id.tv_card_number);
            addToWallet = itemView.findViewById(R.id.layout_add_to_wallet);
            cardRepair = itemView.findViewById(R.id.card_repair);
        }
    }
}
