package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.RewardBundleModel;
import com.union_test.toutiao.utils.RewardAdvancedInfo;
import com.union_test.toutiao.utils.TToast;

/**
 * Created by bytedance on 2018/2/1.
 * 激励视频接入类
 */

public class RewardVideoActivity extends Activity {
    private static final String TAG = "RewardVideoActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;

    private TTAdNative mTTAdNative;

    private TTRewardVideoAd mttRewardVideoAd;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;

    // 是否开放进阶奖励功能
    private final boolean isEnableAdvancedReward = false;
    private RewardAdvancedInfo mRewardAdvancedInfo;

    private boolean mHasShowDownloadActive = false;


    private int mNowPlayAgainCount = 0;
    private int mNextPlayAgainCount = 0;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        findViewById(R.id.btn_arv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoadAd = (Button) findViewById(R.id.btn_reward_load);
        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        getExtraInfo();
        initClickEvent();

        //step1:初始化sdk

        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //step3:创建TTAdNative对象,用于调用广告请求接口

        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mHorizontalCodeId = intent.getStringExtra("horizontal_rit");
        mVerticalCodeId = intent.getStringExtra("vertical_rit");
    }

    private void initClickEvent() {
        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadAd(mHorizontalCodeId);
            }
        });
        mLoadAdVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadAd(mVerticalCodeId);
            }
        });
        mShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景

                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                    mttRewardVideoAd = null;
                } else {
                    TToast.show(RewardVideoActivity.this, "请先加载广告");
                }
            }
        });
    }

    private void loadAd(final String codeId) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                //此次加载广告的用途是实时加载，当用来作为缓存时，请使用：TTAdLoadType.PRELOAD
                .setAdLoadType(TTAdLoadType.LOAD)
                .build();

        //step5:请求广告

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                TToast.show(RewardVideoActivity.this, message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override

            public void onRewardVideoCached() {
                Log.e(TAG, "Callback --> onRewardVideoCached");
                TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
            }

            @Override

            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Log.e(TAG, "Callback --> onRewardVideoCached");
                TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
               // ad.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");

            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override

            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.e(TAG, "Callback --> onRewardVideoAdLoad");


                TToast.show(RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
                mttRewardVideoAd = ad;
                mRewardAdvancedInfo = new RewardAdvancedInfo();

                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override

                    public void onAdShow() {
                        Log.d(TAG, "Callback --> rewardVideoAd show");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    @Override

                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> rewardVideoAd bar click");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    @Override

                    public void onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd close");
                        if (isEnableAdvancedReward && mRewardAdvancedInfo != null) {
                            Log.d(TAG, "本次奖励共发放：" + mRewardAdvancedInfo.getRewardAdvancedAmount());
                        }
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> rewardVideoAd complete");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "Callback --> rewardVideoAd error");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override

                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Log.e(TAG, "Callback --> " + logString);
                        TToast.show(RewardVideoActivity.this, logString);
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        RewardBundleModel rewardBundleModel = new RewardBundleModel(extraInfo);
                        Log.e(TAG, "Callback --> rewardVideoAd has onRewardArrived " +
                                "\n奖励是否有效：" + isRewardValid +
                                "\n奖励类型：" + rewardType +
                                "\n奖励名称：" + rewardBundleModel.getRewardName() +
                                "\n奖励数量：" + rewardBundleModel.getRewardAmount() +
                                "\n建议奖励百分比：" + rewardBundleModel.getRewardPropose());
                        if (!isRewardValid) {
                            Log.d(TAG, "发送奖励失败 code：" + rewardBundleModel.getServerErrorCode() +
                                    "\n msg：" + rewardBundleModel.getServerErrorMsg());
                            return;
                        }

                        if (!isEnableAdvancedReward) {
                            // 未使用进阶奖励功能

                            if (rewardType == TTRewardVideoAd.REWARD_TYPE_DEFAULT) {
                                Log.d(TAG, "普通奖励发放，name:" + rewardBundleModel.getRewardName() +
                                        "\namount:" + rewardBundleModel.getRewardAmount());
                            }
                        } else {
                            // 使用了进阶奖励功能
                            if (mRewardAdvancedInfo != null) {
                                mRewardAdvancedInfo.proxyRewardModel(rewardBundleModel, false);
                            }
                        }
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });

                mttRewardVideoAd.setRewardPlayAgainInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override

                    public void onAdShow() {
                        mNowPlayAgainCount = mNextPlayAgainCount;
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain show");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    @Override

                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain bar click");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    @Override

                    public void onAdClose() {
                        // 再看广告不会调到这个回调
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain complete");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain error");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override

                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "rewardPlayAgain verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 " + logString);
                        TToast.show(RewardVideoActivity.this, logString);
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        RewardBundleModel rewardBundleModel = new RewardBundleModel(extraInfo);
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain has onRewardArrived " +
                                "\n奖励是否有效：" + isRewardValid +
                                "\n奖励类型：" + rewardType +
                                "\n奖励名称：" + rewardBundleModel.getRewardName() +
                                "\n奖励数量：" + rewardBundleModel.getRewardAmount() +
                                "\n建议奖励百分比：" + rewardBundleModel.getRewardPropose());

                        if (!isEnableAdvancedReward) {
                            // 再看一个未使用进阶奖励功能

                            if (rewardType == TTRewardVideoAd.REWARD_TYPE_DEFAULT) {
                                Log.d(TAG, "再看一个普通奖励发放，name:" + rewardBundleModel.getRewardName() +
                                        "\namount:" + rewardBundleModel.getRewardAmount());
                            }
                        } else {
                            // 再看一个使用了进阶奖励功能
                            if (mRewardAdvancedInfo != null) {
                                mRewardAdvancedInfo.proxyRewardModel(rewardBundleModel, true);
                            }
                        }
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain has onSkippedVideo");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });

                mttRewardVideoAd.setRewardPlayAgainController(new TTRewardVideoAd.RewardAdPlayAgainController() {
                    @Override
                    public void getPlayAgainCondition(int nextPlayAgainCount, Callback callback) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(KEY_PLAY_AGAIN_ALLOW, true);
                        bundle.putString(KEY_PLAY_AGAIN_REWARD_NAME, "金币");
                        bundle.putString(KEY_PLAY_AGAIN_REWARD_AMOUNT, nextPlayAgainCount + "个");
                        mNextPlayAgainCount = nextPlayAgainCount;
                        callback.onConditionReturn(bundle);
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            TToast.show(RewardVideoActivity.this, "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(RewardVideoActivity.this, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(RewardVideoActivity.this, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(RewardVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(RewardVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }


    private String getAdType(int type) {
        switch (type) {

            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;

            case TTAdConstant.AD_TYPE_LIVE:
                return "直播流，type=" + type;
        }

        return "未知类型+type=" + type;
    }
}
