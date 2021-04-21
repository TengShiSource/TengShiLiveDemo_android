package com.qm.qmclass.fragment;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.StudentLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.qm.qmclass.utils.PermissionUtils;
import com.qm.qmclass.utils.PushUtils;
import com.qm.qmclass.utils.ToastUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.rtmp.TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO;
import static com.tencent.rtmp.TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
import static com.tencent.trtc.TRTCCloudDef.TRTCSystemVolumeTypeMedia;


public class StudentVideoListFragment extends Fragment implements View.OnClickListener, StudentLiveActivity.VideoFragmentListener,ITXLivePushListener {
    private View view;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private StudentLiveActivity studentLiveActivity;
    TXCloudVideoView teacherVideoView;
    TXCloudVideoView myselfVideoview;
    private TextView teachername;
    private TextView myselfname;
    private RelativeLayout rlTools;
    private LinearLayout shouqi;
    private LinearLayout llTool;
    private LinearLayout myselfShexiangtou;
    private LinearLayout myselfLianmai;
    private LinearLayout myselfMaike;
    private LinearLayout myselfQiehuan;
    private LinearLayout classmateVideo;
    private ImageView ivShexiangtou;
    private ImageView ivLianmai;
    private ImageView ivMaike;
    private static TXLivePlayer mLivePlayer;
    private static TXLivePlayer classmatePlayer;
    TXCloudVideoView classMateVideoview;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;

    private HashMap<String, Object> map = new HashMap<>();
    private int teachercount=0;
    private int classmatecount=0;
    private boolean isVideoToolShow=true;
    private String safeUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_studentvideolist, container, false);
        dataManager=DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
        studentLiveActivity=(StudentLiveActivity) getActivity();
        studentLiveActivity.setVideoFragmentListener(this);
        teachername=(TextView) view.findViewById(R.id.teachername);
        teacherVideoView=(TXCloudVideoView) view.findViewById(R.id.teacher_videoview);
        myselfVideoview=(TXCloudVideoView) view.findViewById(R.id.myself_videoview);
        myselfname=(TextView) view.findViewById(R.id.myselfname);
        myselfname.setText(dataManager.getUserName());
        rlTools=(RelativeLayout) view.findViewById(R.id.rl_tools);
        shouqi=(LinearLayout) view.findViewById(R.id.shouqi);
        llTool=(LinearLayout) view.findViewById(R.id.ll_tool);
        myselfShexiangtou=(LinearLayout) view.findViewById(R.id.myself_shexiangtou);
        ivShexiangtou=(ImageView) view.findViewById(R.id.iv_shexiangtou);
        myselfLianmai=(LinearLayout) view.findViewById(R.id.myself_lianmai);
        ivLianmai=(ImageView) view.findViewById(R.id.iv_lianmai);
        myselfMaike=(LinearLayout) view.findViewById(R.id.myself_maike);
        ivMaike=(ImageView) view.findViewById(R.id.iv_maike);
        myselfQiehuan=(LinearLayout) view.findViewById(R.id.myself_qiehuan);
        shouqi.setOnClickListener(this);
        myselfShexiangtou.setOnClickListener(this);
        myselfLianmai.setOnClickListener(this);
        myselfMaike.setOnClickListener(this);
        myselfQiehuan.setOnClickListener(this);
        classmateVideo=(LinearLayout) view.findViewById(R.id.classmate_video);
        CheckPermission();
        hidenAnimation();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.shouqi){
            if (isVideoToolShow){
                hidenAnimation();
            }else {
                showAnimation();
            }
        }else if (v.getId()==R.id.myself_shexiangtou){
            if (liveDataManager.isCameraOn()){
                liveDataManager.setCameraOn(false);
                mLivePusher.stopCameraPreview(true);
                mLivePusher.pausePusher();
            }else {
                liveDataManager.setCameraOn(true);
                mLivePusher.startCameraPreview(myselfVideoview);
                mLivePusher.resumePusher();
            }

        }else if (v.getId()==R.id.myself_lianmai){
            Map<String, String> map = new HashMap<>();
            map.put("action", "micOpenRequest");
            map.put("nickName", dataManager.getUserName());
            String str = JSON.toJSONString(map);
            final byte msg[] = str.getBytes();
            studentLiveActivity.sendCustomMessage(dataManager.getTeacherCode(),msg);
            ToastUtil.showToast1(getActivity(), "", "连麦请求发送成功");
        }else if (v.getId()==R.id.myself_maike){

        }else if (v.getId()==R.id.myself_qiehuan){
            mLivePusher.switchCamera();
        }
    }
    private void ShowTeacherVideo(){
        //mPlayerView 添加的界面 view
        if (mLivePlayer==null){
            //创建 player 对象
            mLivePlayer = new TXLivePlayer(getActivity());
        }
        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        //极速模式
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(1);
        mLivePlayer.setConfig(mPlayConfig);
        if (mLivePlayer != null) {
            //关键 player 对象与界面 view
            mLivePlayer.setPlayerView(teacherVideoView);
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            //            静音播放
            mLivePlayer.setMute(true);
            String flvUrl = "http://live.jledu.com/live/camera_"+dataManager.getTeacherCode()+".flv";
            Log.e("ShowBroadcastVideo",dataManager.getAppid()+flvUrl);
            mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int event, Bundle param) {
                    Log.e("joinOpenClass",String.valueOf(event));
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        Log.e("joinOpenClass","DISCONNECT");
                        ShowTeacherVideo();
//                        Log.e("joinOpenClass",String.valueOf(count));
                        if (teachercount==0){
                            teachercount=1;
                            Log.e("joinOpenClass",String.valueOf(teachercount));
                            ToastUtil.showToast1(getActivity(), "", "当前课堂没有教师，请耐心等待");
                        }
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                        Log.e("joinOpenClass","BEGIN");
                        teacherVideoView.setVisibility(View.VISIBLE);
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
                        Log.e("joinOpenClass","LOADING");
                    }
                }

                @Override
                public void onNetStatus(Bundle status) {
                }
            });
        }
    }

    private void PushMyselfVideo(){
        if (mLivePushConfig==null){
            mLivePushConfig  = new TXLivePushConfig();
        }
        if (mLivePusher==null){
            mLivePusher = new TXLivePusher(getActivity());
            mLivePusher.setPushListener(this);
        }
// 一般情况下不需要修改 config 的默认配置
//        横屏推流
        mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
        mLivePushConfig.setPauseImg(BitmapFactory.decodeResource(getResources(), R.mipmap.maike, null));
        mLivePushConfig.setPauseFlag(PAUSE_FLAG_PAUSE_VIDEO);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setRenderRotation(-90);

//        设置分辨率
        mLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION,true,false);
        mLivePusher.startCameraPreview(myselfVideoview);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 5);
        String timestamp = String.valueOf(nowTime.getTimeInMillis());
        String time=timestamp.substring(0,timestamp.length()-3);
        Log.e("推流",sdf.format(nowTime.getTime())+"："+time);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2)

        safeUrl=PushUtils.getSafeUrl("8985c1e65a2ba7640888c911d49f8a2f","camera_"+dataManager.getUserCode(), Integer.parseInt(time));
        String rtmpURL = "rtmp://10613.livepush.myqcloud.com/live/camera_"+dataManager.getUserCode()+"?"+safeUrl; //此处填写您的 rtmp 推流地址
        Log.e("推流",rtmpURL);
        int ret = mLivePusher.startPusher(rtmpURL.trim());
        if (ret == -5) {
            Log.i("TAG", "startRTMPPush: license 校验失败");
        }else {

        }
    }
    @Override
    public void onPushEvent(int event, Bundle bundle) {
        if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            // 遇到以上错误，则停止推流
            ToastUtil.showToast1(getActivity(), "", "打开摄像头失败");
        }else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL){
            ToastUtil.showToast1(getActivity(), "", "打开麦克风失败");
        }else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
            //您当前的网络环境不佳，请尽快更换网络保证正常直播
            ToastUtil.showToast1(getActivity(), "", "您当前的网络环境不佳");
        }else if (event == TXLiveConstants.PUSH_EVT_OPEN_CAMERA_SUCC) {

        }
        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
        }

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
    private void StopPushMyselfVideo(){
        if (mLivePusher!=null){
            mLivePusher.stopPusher();
            //如果已经启动了摄像头预览，请在结束推流时将其关闭。
            mLivePusher.stopCameraPreview(true);
        }

    }
//    创建一个新学生视频View
    private View createView(final String userCode) {
        //首先引入要添加的View
        final View view=View.inflate(getActivity(), R.layout.classmate_video_item, null);
        map.put(userCode,view);
        //找到里面需要动态改变的控件
        classMateVideoview = (TXCloudVideoView) view.findViewById(R.id.classmate_videoview);
        TextView classmatename = (TextView) view.findViewById(R.id.classmatename);
        if (!liveDataManager.getOnLineStudentsMap().isEmpty()){
            //给控件赋值
            classmatename.setText(liveDataManager.getOnLineStudentsMap().get(userCode).getNickName());
        }
        ShowclassMateVideo(userCode);
        return view;
    }

    private void ShowclassMateVideo(final String classMateCode){
        //mPlayerView 添加的界面 view
        classmatePlayer = null;
        if (classmatePlayer==null){
            //创建 player 对象
            classmatePlayer = new TXLivePlayer(getActivity());
        }
        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        //极速模式
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(1);
        classmatePlayer.setConfig(mPlayConfig);
        if (classmatePlayer != null) {
            //关键 player 对象与界面 view
            classmatePlayer.setPlayerView(classMateVideoview);
            classmatePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            classmatePlayer.setRenderRotation(-90);
            //            静音播放
            classmatePlayer.setMute(true);
            String flvUrl = "http://live.jledu.com/live/camera_"+classMateCode+".flv?"+safeUrl;
            Log.e("ShowBroadcastVideo",dataManager.getAppid()+flvUrl);
            classmatePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
            classmatePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int event, Bundle param) {
                    Log.e("joinOpenClass",String.valueOf(event));
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        Log.e("joinOpenClass","DISCONNECT");
                        ShowclassMateVideo(classMateCode);
//                        Log.e("joinOpenClass",String.valueOf(count));
                        if (classmatecount==0){
                            classmatecount=1;
                            Log.e("joinOpenClass",String.valueOf(classmatecount));
                            ToastUtil.showToast1(getActivity(), "", "当前课堂没有教师，请耐心等待");
                        }
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                        Log.e("joinOpenClass","BEGIN");
                        teacherVideoView.setVisibility(View.VISIBLE);
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
                        Log.e("joinOpenClass","LOADING");
                    }
                }

                @Override
                public void onNetStatus(Bundle status) {
                }
            });
        }
    }
//    获取学生视频view
    private View getVideoView(String studentid) {
        view=(View) map.get(studentid);
        return view;
    }

    private void showAnimation() {
        isVideoToolShow=true;
        rlTools.setBackground(getResources().getDrawable(R.mipmap.videotool));
        shouqi.setRotation(360);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shouqi.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        shouqi.setLayoutParams(lp);
        TranslateAnimation show = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        show.setDuration(500);
        llTool.startAnimation(show);
    }

    private void hidenAnimation() {
        isVideoToolShow=false;
        rlTools.setBackgroundColor(getResources().getColor(R.color.hlfTransparent));
        TranslateAnimation hiden = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        hiden.setDuration(500);
        llTool.startAnimation(hiden);
        shouqi.setRotation(180);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shouqi.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        shouqi.setLayoutParams(lp);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(getActivity(),requestCode,permissions,grantResults);
    }
    private void CheckPermission(){
        //        把需要的权限添加到String[]数组中即可
        PermissionUtils.getInstance().fragmentChekPermissions(getActivity(),this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO}, new PermissionUtils.IPermissionsResult() {

            @Override
            public void passPermissons() {
//                通过权限的操作
                ShowTeacherVideo();
                PushMyselfVideo();
            }

            @Override
            public void forbitPermissons() {
//                拒绝权限的操作
            }
        });
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            ShowTeacherVideo();
            PushMyselfVideo();
        }
        super.onHiddenChanged(hidden);
    }
    @Override
    public void onDestroy() {
        StopPushMyselfVideo();
        if (mLivePlayer!=null) {
            mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
            teacherVideoView.onDestroy();
        }

        if (classmatePlayer!=null){
            classmatePlayer.stopPlay(true); // true 代表清除最后一帧画面
            classMateVideoview.onDestroy();
        }

        classmateVideo.removeAllViews();
        super.onDestroy();
    }


    @Override
    public void classmatelianMai(String classmatecode,String type) {
        if (type.equals("1")){
            createView(classmatecode);
        }else if (type.equals("2")){
            classmateVideo.removeView(getVideoView(classmatecode));
        }

    }
}
