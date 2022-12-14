package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.union_test.toutiao.utils.UIUtils;

import java.util.List;

@SuppressWarnings("unused")
public class NativeExpressOnePointFiveActivity extends Activity {


    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;
    private Context mContext;

    private TTAdDislike mTTAdDislike;
    private Button mButtonLoadAd;
    private Button mButtonLoadAdVideo;
    private EditText mEtWidth;
    private EditText mEtHeight;

    private TTNativeExpressAd mTTAd;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_express);
        mContext = this.getApplicationContext();
        mExpressContainer = (FrameLayout) findViewById(R.id.express_container);
        mButtonLoadAd = (Button) findViewById(R.id.btn_express_load);
        mButtonLoadAd.setVisibility(View.GONE);
        mButtonLoadAdVideo = (Button) findViewById(R.id.btn_express_load_video);
        mEtHeight = (EditText) findViewById(R.id.express_height);
        mEtHeight.setVisibility(View.GONE);
        mEtWidth = (EditText) findViewById(R.id.express_width);
        mButtonLoadAd.setOnClickListener(mClickListener);
        mButtonLoadAdVideo.setOnClickListener(mClickListener);
        //step2:??????TTAdNative?????????createAdNative(Context context) banner??????context????????????Activity??????

        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        Button button = (Button)findViewById(R.id.btn_ane_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadExpressAd();
        }
    };

    private void loadExpressAd() {
        mExpressContainer.removeAllViews();
        float expressViewWidth;
        float expressViewHeight;
        try {
            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
            //expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
        } catch (Exception e) {
            expressViewWidth = UIUtils.getScreenWidthDp(this);
        }
        //1.5 ?????????????????????????????????
        expressViewHeight = 0;
        //step4:??????feed????????????????????????AdSlot,??????????????????????????????

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("949099102")
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //??????????????????view???size,??????dp
                .build();
        //step5:??????????????????????????????????????????????????????

        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(NativeExpressOnePointFiveActivity.this, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            @Override

            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }
    private long startTime = 0;

    private boolean mHasShowDownloadActive = false;


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
                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg+" code:"+code);
            }

            @Override

            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //??????view????????? ?????? dp
                TToast.show(mContext, "????????????");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });

        ad.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
            @Override

            public void onVideoLoad() {
                Log.d("videotag", "onVideoLoad");
            }

            @Override

            public void onVideoError(int errorCode, int extraCode) {
                Log.d("videotag", "onVideoError");
            }

            @Override

            public void onVideoAdStartPlay() {
                Log.d("videotag", "onVideoAdStartPlay");
            }

            @Override

            public void onVideoAdPaused() {
                Log.d("videotag", "onVideoAdPaused");
            }

            @Override

            public void onVideoAdContinuePlay() {
                Log.d("videotag", "onVideoAdContinuePlay");
            }

            @Override

            public void onProgressUpdate(long current, long duration) {
                Log.d("videotag", "onProgressUpdate: " + current);
            }

            @Override

            public void onVideoAdComplete() {
                Log.d("videotag", "onVideoAdComplete");
            }

            @Override

            public void onClickRetry() {
                Log.d("videotag", "onClickRetry");
            }
        });
        //dislike??????
        bindDislike(ad, false);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(NativeExpressOnePointFiveActivity.this, "??????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(NativeExpressOnePointFiveActivity.this, "????????????????????????", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(NativeExpressOnePointFiveActivity.this, "???????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(NativeExpressOnePointFiveActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(NativeExpressOnePointFiveActivity.this, "?????????????????????????????????", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(NativeExpressOnePointFiveActivity.this, "????????????", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????dislike???????????????????????????????????? dislike???????????????dislike?????????
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */

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

        ad.setDislikeCallback(NativeExpressOnePointFiveActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                TToast.show(mContext, "?????? " + value);
                //???????????????????????????????????????????????????
                mExpressContainer.removeAllViews();
                if (enforce) {
                    TToast.show(mContext, "NativeExpressActivity ??????????????? sdk????????????View ");
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
