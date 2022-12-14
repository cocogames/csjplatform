package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

@SuppressWarnings("unused")
public class InteractionExpressActivity extends Activity implements View.OnClickListener {


    private TTAdNative mTTAdNative;
    private Context mContext;

    private TTAdDislike mTTAdDislike;

    private TTNativeExpressAd mTTAd;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_express_intersitial);
        mContext = this.getApplicationContext();
        initView();
        initTTSDKConfig();
        Button button = (Button)findViewById(R.id.btn_ne_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        findViewById(R.id.btn_size_1_1).setOnClickListener(this);
        findViewById(R.id.btn_size_2_3).setOnClickListener(this);
        findViewById(R.id.btn_size_3_2).setOnClickListener(this);
        findViewById(R.id.btn_show_ad).setOnClickListener(this);
    }

    private void initTTSDKConfig() {
        //step2:??????TTAdNative?????????createAdNative(Context context) banner??????context????????????Activity??????

        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_size_1_1:
                loadExpressAd("945509693", 300, 300);
                break;
            case R.id.btn_size_2_3:
                loadExpressAd("945509702", 300, 450);
                break;
            case R.id.btn_size_3_2:
                loadExpressAd("945509833", 450, 300);
                break;
            case R.id.btn_show_ad:
                showAd();
                break;
        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        //step4:????????????????????????AdSlot,??????????????????????????????

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //?????????id
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //??????????????????view???size,??????dp
                .build();
        //step5:??????????????????????????????????????????????????????

        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(InteractionExpressActivity.this, "load error : " + code + ", " + message);
            }

            @Override

            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                TToast.show(mContext, "load success !");
            }
        });
    }

    private void showAd() {
        if (mTTAd != null) {
            mTTAd.render();
        }else {
            TToast.show(mContext,"??????????????????");
        }
    }


    private void bindAdListener(final TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override

            public void onAdDismiss() {
                TToast.show(mContext, "????????????");
            }

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
                ad.showInteractionExpressAd(InteractionExpressActivity.this);

            }
        });
        bindDislike(ad, false);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(InteractionExpressActivity.this, "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(InteractionExpressActivity.this, "????????????????????????", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "????????????", Toast.LENGTH_LONG);
            }
        });
    }


    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //?????????????????????
            DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    TToast.show(mContext, "?????? " + filterWord.getName());
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

        ad.setDislikeCallback(InteractionExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //TToast.show(mContext, "????????? " + value);
                TToast.show(mContext, "\t\t\t\t\t\t\t??????????????????!\t\t\t\t\t\t\n?????????????????????????????????????????????", 3);
                if (enforce) {
                    TToast.show(mContext, "InteractionExpressActivity ????????????????????????sdk?????????view????????? ");
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


}
