package com.qm.qmclass.tencent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.utils.JianaKongUtils;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.sql.Time;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Module:   TRTCVideoViewLayout
 * <p>
 * Function: 用于计算每个视频画面的位置排布和大小尺寸
 */
public class TRTCView {
    private final static String TAG = TRTCView.class.getSimpleName();

    private static volatile TRTCView trtcView;
    private Context mContext;

    public static TRTCView getInstance(Context context) {
        if (trtcView == null) {
            synchronized (TRTCView.class) {
                if (trtcView == null) {
                    trtcView = new TRTCView(context);
                }
            }
        }
        return trtcView;
    }
    private TRTCView(Context context) {
        mContext=context;
    }

    public TXCloudVideoView getTRTCView() {
        TXCloudVideoView cloudVideoView = new TXCloudVideoView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = 0;
        layoutParams.leftMargin = 0;
        cloudVideoView.setLayoutParams(layoutParams);
        cloudVideoView.setVisibility(GONE);
        cloudVideoView.setClickable(true);
        cloudVideoView.setTag(0);
        cloudVideoView.setBackgroundColor(mContext.getResources().getColor(R.color.livebgdark));
        return cloudVideoView;
    }

}
