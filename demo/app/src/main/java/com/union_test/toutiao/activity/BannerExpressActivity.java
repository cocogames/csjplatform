package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.dialog.DislikeDialog;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * created by wuzejian on 2019-12-22
 */
@SuppressWarnings("unused")
public class BannerExpressActivity extends Activity {


    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;
    private Context mContext;

    private TTAdDislike mTTAdDislike;

    private TTNativeExpressAd mTTAd;
    private LoadMoreRecyclerView mListView;
    private List<AdSizeModel> mBannerAdSizeModelList;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_express_banner);
        mContext = this.getApplicationContext();
        initView();
        initData();
        initRecycleView();
        initTTSDKConfig();
        Button button = (Button)findViewById(R.id.btn_eb_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initTTSDKConfig() {
        //step2:??????TTAdNative?????????createAdNative(Context context) banner??????context????????????Activity??????

        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    private void initRecycleView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mListView.setLayoutManager(layoutManager);
        AdapterForBannerType adapterForBannerType = new AdapterForBannerType(this, mBannerAdSizeModelList);
        mListView.setAdapter(adapterForBannerType);

    }

    private void initView() {
        mExpressContainer = (FrameLayout) findViewById(R.id.express_container);
        mListView = findViewById(R.id.my_list);
        findViewById(R.id.showBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowBanner();
            }
        });
    }

    private void initData() {
        mBannerAdSizeModelList = new ArrayList<>();
        mBannerAdSizeModelList.add(new AdSizeModel("600*90", 300, 45, "901121246"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*150", 300, 75, "901121700"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*260", 300, 130, "901121148"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*300", 300, 150, "945509757"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*400", 300, 200, "945509751"));
        mBannerAdSizeModelList.add(new AdSizeModel("640*100", 320, 50, "901121223"));
        mBannerAdSizeModelList.add(new AdSizeModel("690*388", 345, 194, "945509738"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*500", 300, 250, "945509744"));
    }


    public static class AdapterForBannerType extends RecyclerView.Adapter<AdapterForBannerType.ViewHolder> {
        private List<AdSizeModel> mBannerSizeList;
        private BannerExpressActivity mActivity;

        public AdapterForBannerType(BannerExpressActivity activity, List<AdSizeModel> bannerSize) {
            this.mActivity = activity;
            this.mBannerSizeList = bannerSize;
        }

        @NonNull
        @Override
        public AdapterForBannerType.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.express_banner_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterForBannerType.ViewHolder viewHolder, int i) {
            final AdSizeModel bannerSize = mBannerSizeList == null ? null : mBannerSizeList.get(i);
            if (bannerSize != null) {
                viewHolder.btnSize.setText(bannerSize.adSizeName);
                viewHolder.btnSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //??????banner??????
                        mActivity.loadExpressAd(bannerSize.codeId, bannerSize.width, bannerSize.height);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mBannerSizeList != null ? mBannerSizeList.size() : 0;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            private Button btnSize;

            public ViewHolder(View view) {
                super(view);
                btnSize = view.findViewById(R.id.btn_banner_size);
            }

        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        mExpressContainer.removeAllViews();
        //step4:????????????????????????AdSlot,??????????????????????????????

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //?????????id
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //??????????????????view???size,??????dp
                .build();
        //step5:??????????????????????????????????????????????????????

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(BannerExpressActivity.this, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            @Override

            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                TToast.show(mContext,"load success!");
            }
        });
    }

    public void onClickShowBanner() {
        if (mTTAd != null) {
            mTTAd.render();
        } else {
            TToast.show(mContext, "??????????????????..");
        }
    }


    private void bindAdListener(TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override

            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "???????????????");
            }

            @Override

            public void onAdShow(View view, int type) {
                TToast.show(mContext, "????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //??????view????????? ?????? dp
                TToast.show(mContext, "????????????");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike??????
        bindDislike(ad, false);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(BannerExpressActivity.this, "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(BannerExpressActivity.this, "????????????????????????", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(BannerExpressActivity.this, "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(BannerExpressActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(BannerExpressActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(BannerExpressActivity.this, "????????????", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * ????????????????????????, ??????????????????????????????????????????????????????dislike???????????????????????????????????? dislike???????????????dislike?????????
     *
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */

    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //?????????????????????
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    TToast.show(mContext, "?????? " + filterWord.getName());
                    //???????????????????????????????????????????????????
                    mExpressContainer.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(mContext, "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????

        ad.setDislikeCallback(BannerExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                TToast.show(mContext, "?????? " + value);
                mExpressContainer.removeAllViews();
                //???????????????????????????????????????????????????
                if (enforce) {
                    TToast.show(mContext, "??????Banner ?????????sdk?????????view?????????");
                }
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "???????????? ");
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }


    public static class AdSizeModel {
        public AdSizeModel(String adSizeName, int width, int height, String codeId) {
            this.adSizeName = adSizeName;
            this.width = width;
            this.height = height;
            this.codeId = codeId;
        }

        public String adSizeName;
        public int width;
        public int height;
        public String codeId;
    }
}
