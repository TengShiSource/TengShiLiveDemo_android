package com.qm.qmclass.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JianaKongUtils{
    private static JianaKongUtils jianaKongUtils;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private TXCloudVideoView jkVideo;
    private static TXLivePlayer mLivePlayer;
    private TeacherLiveActivity teacherLiveActivity;
    private RefreshJianKongListener refreshJianKongListener;
    private LivePopupWindow QPPopupWindow;

    public static JianaKongUtils getInstance(TeacherLiveActivity activity) {
        if (jianaKongUtils == null) {
            synchronized (JianaKongUtils.class) {
                if (jianaKongUtils == null) {
                    jianaKongUtils = new JianaKongUtils(activity);
                }
            }
        }
        return jianaKongUtils;
    }

    private JianaKongUtils(TeacherLiveActivity activity) {
        teacherLiveActivity=activity;
        dataManager =DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
    }

    //    创建一个新学生视频View
    public View createVideoView(final String userCode) {
        //首先引入要添加的View
        final View view=View.inflate(teacherLiveActivity, R.layout.jiankong_video_item, null);
        //找到里面需要动态改变的控件
        jkVideo = (TXCloudVideoView) view.findViewById(R.id.jk_video);
        //mPlayerView 添加的界面 view
        mLivePlayer = null;
        if (mLivePlayer==null){
            //创建 player 对象
            mLivePlayer = new TXLivePlayer(teacherLiveActivity);
        }
        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        //极速模式
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(1);
        mLivePlayer.setConfig(mPlayConfig);
        if (mLivePlayer != null) {
            //关键 player 对象与界面 view
            mLivePlayer.setPlayerView(jkVideo);
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            mLivePlayer.setRenderRotation(0);
            //            静音播放
            mLivePlayer.setMute(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.MINUTE, 5);
            String timestamp = String.valueOf(nowTime.getTimeInMillis());
            String time=timestamp.substring(0,timestamp.length()-3);
            String safeUrl= PushUtils.getSafeUrl("8985c1e65a2ba7640888c911d49f8a2f","camera_"+dataManager.getUserCode(), Integer.parseInt(time));
            String flvUrl = "http://live.jledu.com/live/camera_"+userCode+".flv?"+safeUrl;
            Log.e("ShowBroadcastVideo",dataManager.getAppid()+flvUrl);
            mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int event, Bundle param) {
                    Log.e("拉流",String.valueOf(event));
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        jkVideo.setVisibility(View.GONE);
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                        jkVideo.setVisibility(View.VISIBLE);
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){

                    }else if (event == TXLiveConstants.PLAY_WARNING_VIDEO_PLAY_LAG){
//                        liveDataManager.getAllStudentsMap().get(userCode).setCameraOn(false);
//                        jkVideo.setVisibility(View.GONE);
//                        refreshJianKongListener.refreshSomeOne(userCode);
                    }
                }

                @Override
                public void onNetStatus(Bundle status) {
                }
            });
        }
        return view;
    }

    //    创建一个新学生视频View
    public View createJKItemView(final String userCode) {
        //首先引入要添加的View
        final View view=View.inflate(teacherLiveActivity, R.layout.jiankong_item, null);
        //找到里面需要动态改变的控件
        LinearLayout quxiaolm = (LinearLayout) view.findViewById(R.id.quxiaolm);
        LinearLayout jkguamai = (LinearLayout) view.findViewById(R.id.jkguamai);
        LinearLayout jkquanping = (LinearLayout) view.findViewById(R.id.jkquanping);

        LinearLayout lianmai = (LinearLayout) view.findViewById(R.id.lianmai);
        LinearLayout shangketixing = (LinearLayout) view.findViewById(R.id.shangketixing);

        lianmai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //老师向学生发起连麦
                Map<String, String> map = new HashMap<>();
                map.put("action", "micOpen");
                String str = JSON.toJSONString(map);
                final byte msg[] = str.getBytes();
                teacherLiveActivity.sendCustomMessage(userCode,msg);
                liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(2);
                teacherLiveActivity.changeStudentVideoLMstate(userCode);
            }
        });
        quxiaolm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //老师取消连麦
            }
        });
        jkguamai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //老师向学生发起挂麦
                Map<String, String> map = new HashMap<>();
                map.put("action", "micClose");
                String str = JSON.toJSONString(map);
                final byte msg[] = str.getBytes();
                teacherLiveActivity.sendCustomMessage(userCode,msg);
                liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(3);
                teacherLiveActivity.refuseLianMai(userCode);
                liveDataManager.getAllStudentsMap().get(userCode).setHuabiOn(false);
            }
        });
        jkquanping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //老师向学生发起全屏
                teacherLiveActivity.sendGroupCustomMessage("cameraFull","",userCode);
                DialogUtil.showJianKongQPDialog(teacherLiveActivity, userCode, false, new DialogUtil.AlertDialogBtnClickListener() {
                    @Override
                    public void clickPositive() {

                    }

                    @Override
                    public void clickNegative() {
                        teacherLiveActivity.sendGroupCustomMessage("cameraBack","",userCode);
                    }
                });
            }
        });
        shangketixing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    public void setRefreshJianKongListener(RefreshJianKongListener refreshJianKongListener) {
        this.refreshJianKongListener = refreshJianKongListener;
    }

    public void destroyInstance() {
        if (mLivePlayer!=null) {
            mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        }
        if (jkVideo!=null){
            jkVideo.onDestroy();
        }
        jianaKongUtils = null;
    }
}
