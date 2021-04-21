package com.qm.qmclass.tencent;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;

/**
 * Module:   TRTCVideoViewLayout
 * <p>
 * Function: 用于计算每个视频画面的位置排布和大小尺寸
 */
public class TICVideoRootView extends LinearLayout {
    private final static String TAG = TICVideoRootView.class.getSimpleName();
    public static final int MAX_USER = 1;
    private Context mContext;
    private ArrayList<TXCloudVideoView> mVideoViewList;
    private String mSelfUserId;

    public TICVideoRootView(Context context) {
        super(context);
        initView(context);
    }

    public TICVideoRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TICVideoRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setUserId(String userId) {
        mSelfUserId = userId;
    }

    private void initView(Context context) {
        mContext = context;

        initTXCloudVideoView();

        showView();
    }

    private void showView() {

        int screenW = getScreenWidth(mContext);
        int screenH = dip2px(160);//getScreenHeight(mContext);
        int margin = dip2px(10);

        int grid4W = (screenW - margin * 2) / 3;
        int grid4H = (screenH - margin * 2);

        LayoutParams layoutParams0 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams0.topMargin = 0;
        layoutParams0.leftMargin = 0;

        for (int i = 0; i < mVideoViewList.size(); i++) {
            TXCloudVideoView cloudVideoView = mVideoViewList.get(i);
            cloudVideoView.setLayoutParams(layoutParams0);
            addView(cloudVideoView);
        }
    }

private int getScreenWidth(Context context) {
    if (context == null) return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
}

public void initTXCloudVideoView() {
    mVideoViewList = new ArrayList<TXCloudVideoView>();
    for (int i = 0; i < MAX_USER; i++) {
        TXCloudVideoView cloudVideoView = new TXCloudVideoView(mContext);
        cloudVideoView.setVisibility(GONE);
        cloudVideoView.setId(1000 + i);
        cloudVideoView.setClickable(true);
        cloudVideoView.setTag(i);
        cloudVideoView.setBackgroundColor(Color.BLACK);
        mVideoViewList.add(i, cloudVideoView);
    }
}

    public TXCloudVideoView getCloudVideoViewByIndex(int index) {
        return mVideoViewList.get(index);
    }

    public TXCloudVideoView getCloudVideoViewByUseId(String userId) {
        for (TXCloudVideoView videoView : mVideoViewList) {
            String tempUserID = videoView.getUserId();
            if (tempUserID != null && tempUserID.equalsIgnoreCase(userId)) {
                return videoView;
            }
        }
        return null;
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 更新进入房间人数
     */
    public TXCloudVideoView onMemberEnter(String userId) {
        Log.e(TAG, "onMemberEnter: userId = " + userId);

        if (TextUtils.isEmpty(userId)) return null;
        TXCloudVideoView videoView = null;
        for (int i = 0; i < mVideoViewList.size(); i++) {
            TXCloudVideoView renderView = mVideoViewList.get(i);
            if (renderView != null) {
                String vUserId = renderView.getUserId();
                if (userId.equalsIgnoreCase(vUserId)) {
                    return renderView;
                }
                if (videoView == null && TextUtils.isEmpty(vUserId)) {
                    renderView.setUserId(userId);
                    videoView = renderView;
                }
            }
        }

        return videoView;
    }

    public void onMemberLeave(String userId) {
        Log.e(TAG, "onMemberLeave: userId = " + userId);

        for (int i = 0; i < mVideoViewList.size(); i++) {
            TXCloudVideoView renderView = mVideoViewList.get(i);
            if (renderView != null && null != renderView.getUserId()) {
                if (renderView.getUserId().equals(userId)) {
                    renderView.setUserId(null);
                    renderView.setVisibility(View.GONE);
                }
            }
        }
    }
}
