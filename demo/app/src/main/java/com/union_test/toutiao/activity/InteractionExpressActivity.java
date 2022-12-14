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
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象

        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
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
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理

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
            TToast.show(mContext,"请先加载广告");
        }
    }


    private void bindAdListener(final TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override

            public void onAdDismiss() {
                TToast.show(mContext, "广告关闭");
            }

            @Override

            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "广告被点击");
            }

            @Override

            public void onAdShow(View view, int type) {
                TToast.show(mContext, "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                TToast.show(mContext, "渲染成功");
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
                TToast.show(InteractionExpressActivity.this, "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(InteractionExpressActivity.this, "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(InteractionExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }


    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //使用自定义样式
            DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(this, dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    TToast.show(mContext, "点击 " + filterWord.getName());
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    TToast.show(mContext, "点击了为什么看到此广告");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式

        ad.setDislikeCallback(InteractionExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //TToast.show(mContext, "反馈了 " + value);
                TToast.show(mContext, "\t\t\t\t\t\t\t感谢您的反馈!\t\t\t\t\t\t\n我们将为您带来更优质的广告体验", 3);
                if (enforce) {
                    TToast.show(mContext, "InteractionExpressActivity 模版插屏，穿山甲sdk强制将view关闭了 ");
                }
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "点击取消 ");
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
