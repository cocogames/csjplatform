package com.union_test.toutiao.onepointfive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.ComplianceInfo;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.DownloadStatusController;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTDislikeDialogAbstract;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linqiming on 6/26/22
 * Usage:
 * Doc:
 */

public class TTFeedAdOnePointFiveImpl implements TTFeedAd {

    private Context mContext;

    public List<TTFeedAd> mFeedAdList;


    public TTFeedAdOnePointFiveImpl(Context context, List<TTFeedAd> feedAds) {
        mContext = context;
        mFeedAdList = feedAds;
    }

    @Override

    public void win(Double auctionBidToWin) {

    }

    @Override

    public void loss(Double auctionPrice, String lossReason, String winBidder) {

    }

    @Override

    public void setPrice(Double auctionPrice) {

    }


    @Override

    public void setVideoAdListener(TTFeedAd.VideoAdListener videoAdListener) {

    }

    @Override

    public double getVideoDuration() {
        return 0;
    }

    @Override

    public CustomizeVideo getCustomVideo() {
        return null;
    }

    @Override

    public int getAdViewWidth() {
        return 0;
    }

    @Override

    public int getAdViewHeight() {
        return 0;
    }

    @Override

    public TTImage getVideoCoverImage() {
        return null;
    }

    @Override

    public Bitmap getAdLogo() {
        return null;
    }

    @Override

    public String getTitle() {
        return null;
    }

    @Override

    public String getDescription() {
        return null;
    }

    @Override

    public String getButtonText() {
        return null;
    }

    @Override

    public int getAppScore() {
        return 0;
    }

    @Override

    public int getAppCommentNum() {
        return 0;
    }

    @Override

    public int getAppSize() {
        return 0;
    }

    @Override

    public String getSource() {
        return null;
    }

    @Override

    public TTImage getIcon() {
        return null;
    }

    @Override

    public List<TTImage> getImageList() {
        return null;
    }

    @Override

    public int getInteractionType() {
        return 0;
    }

    @Override

    public int getImageMode() {
        return -1;
    }

    @Override

    public DislikeInfo getDislikeInfo() {
        return null;
    }

    @Override

    public ComplianceInfo getComplianceInfo() {
        return null;
    }

    @Override

    public TTAdDislike getDislikeDialog(Activity activity) {
        return null;
    }

    @Override

    public TTAdDislike getDislikeDialog(TTDislikeDialogAbstract dialog) {
        return null;
    }

    @Override

    public DownloadStatusController getDownloadStatusController() {
        return null;
    }

    @Override

    public void registerViewForInteraction(ViewGroup container, View clickView, AdInteractionListener listener) {

    }

    @Override

    public void registerViewForInteraction(ViewGroup container, List<View> clickViews, List<View> creativeViews, AdInteractionListener listener) {

    }

    @Override

    public void registerViewForInteraction(ViewGroup container, List<View> clickViews, List<View> creativeViews, View dislikeView, AdInteractionListener listener) {

    }

    @Override

    public void registerViewForInteraction(ViewGroup container, List<View> imageViews, List<View> clickViews, List<View> creativeViews, View dislikeView, AdInteractionListener listener) {

    }

    @Override

    public void registerViewForInteraction(ViewGroup container, List<View> imageViews, List<View> clickViews, List<View> creativeViews, List<View> directDownloadViews, View dislikeView, AdInteractionListener listener) {

    }

    @Override

    public void setDownloadListener(TTAppDownloadListener downloadListener) {

    }

    @Override

    public void setActivityForDownloadApp(Activity activity) {

    }

    @Override

    public View getAdView() {
        return null;
    }

    @Override

    public Map<String, Object> getMediaExtraInfo() {
        return addTagForMediaExtra();
    }

    @Override

    public void render() {

    }

    @Override

    public void setExpressRenderListener(ExpressRenderListener expressRenderListener) {

    }

    @Override

    public void setDislikeCallback(Activity activity, TTAdDislike.DislikeInteractionCallback dislikeInteractionCallback) {

    }

    @Override

    public void setDislikeDialog(TTDislikeDialogAbstract dialog) {

    }

    @Override

    public void showInteractionExpressAd(Activity activity) {

    }

    @Override

    public void destroy() {

    }

    /**
     * 自渲染场景，在MediaExtraInfo内添加 1.5卡标识，并返回 map
     * @return Map<String, Object>
     */
    public static Map<String, Object> addTagForMediaExtra() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("_tt_ad_type_onepointfive", true);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
