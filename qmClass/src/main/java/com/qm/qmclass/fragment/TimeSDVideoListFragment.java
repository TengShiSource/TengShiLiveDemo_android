package com.qm.qmclass.fragment;

import android.Manifest;
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
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.StudentLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.qm.qmclass.utils.PermissionUtils;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.trtc.TRTCCloudDef.TRTCSystemVolumeTypeMedia;


public class TimeSDVideoListFragment extends Fragment implements TICManager.TICEventListener, View.OnClickListener {
    private View view;
    private TICManager mTicManager;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private StudentLiveActivity studentLiveActivity;
    //trtc
    TRTCCloud mTrtcCloud;
    // 实时音视频视图控件
    TICVideoRootView teacherVideoView;
    TICVideoRootView myselfVideoView;
//    private RecyclerView studentVideolist;
    private TextView teachername;
    private TextView myselfname;
    private RelativeLayout rlTools;
    private LinearLayout shouqi;
    private LinearLayout llTool;
    private LinearLayout myselfShexiangtou;
    private LinearLayout myselfGuamai;
    private LinearLayout myselfMaike;
    private LinearLayout myselfQiehuan;
    private LinearLayout classmateVideo;

    private HashMap<String, Object> map = new HashMap<>();
    boolean mEnableFrontCamera = true;
    private boolean isVideoToolShow=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timesdvideolist, container, false);
        dataManager =DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
        studentLiveActivity=(StudentLiveActivity) getActivity();
        teachername=(TextView) view.findViewById(R.id.teachername);

        rlTools=(RelativeLayout) view.findViewById(R.id.rl_tools);
        shouqi=(LinearLayout) view.findViewById(R.id.shouqi);
        llTool=(LinearLayout) view.findViewById(R.id.ll_tool);
        myselfname=(TextView) view.findViewById(R.id.myselfname);
        myselfname.setText(dataManager.getUserName());
        myselfShexiangtou=(LinearLayout) view.findViewById(R.id.myself_shexiangtou);
        myselfGuamai=(LinearLayout) view.findViewById(R.id.myself_guamai);
        myselfMaike=(LinearLayout) view.findViewById(R.id.myself_maike);
        myselfQiehuan=(LinearLayout) view.findViewById(R.id.myself_qiehuan);

        classmateVideo=(LinearLayout) view.findViewById(R.id.classmate_video);

        shouqi.setOnClickListener(this);
        myselfShexiangtou.setOnClickListener(this);
        myselfGuamai.setOnClickListener(this);
        myselfMaike.setOnClickListener(this);
        myselfQiehuan.setOnClickListener(this);

        mTicManager= QMSDK.getTICManager();
        mTicManager.addEventListener(TimeSDVideoListFragment.this);

        CheckPermission();
        hidenAnimation();
        return view;
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.shouqi){
            if (isVideoToolShow){
                hidenAnimation();
            }else {
                showAnimation();
            }

        }else if (v.getId()==R.id.teacher_shexiangtou){


        }else if (v.getId()==R.id.myself_guamai){
            studentLiveActivity.toOpenClass();
        }else if (v.getId()==R.id.teacher_maike){

        }else if (v.getId()==R.id.teacher_qiehuan){

        }
    }

//    创建一个新学生视频View
    private View createView(final String studentId) {
        //首先引入要添加的View
        final View view=View.inflate(getActivity(), R.layout.time_student_video_item, null);
        map.put(studentId,view);
        //找到里面需要动态改变的控件
        TICVideoRootView studentVideoview = (TICVideoRootView) view.findViewById(R.id.classmate_videoview);
        TXCloudVideoView localVideoView = studentVideoview.getCloudVideoViewByIndex(0);
        localVideoView.setUserId(studentId);
        if (localVideoView != null) {
            // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
            mTrtcCloud.setRemoteViewFillMode(studentId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            mTrtcCloud.startRemoteView(studentId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, localVideoView);
            localVideoView.setVisibility(View.VISIBLE);
        }
        TextView studentname = (TextView) view.findViewById(R.id.classmatename);
        if (!liveDataManager.getOnLineStudentsMap().isEmpty()) {
            //给控件赋值
            studentname.setText(liveDataManager.getOnLineStudentsMap().get(studentId).getNickName());
        }
        return view;
    }


    //---------trtc--------------
    private void initTrtc() {
        //1、获取trtc
        mTrtcCloud = mTicManager.getTRTCClound();
//        设置使用媒体音量
        mTrtcCloud.setSystemVolumeType(TRTCSystemVolumeTypeMedia);

        if (mTrtcCloud != null) {
            myselfVideoView = (TICVideoRootView) view.findViewById(R.id.myself_videoview);
            TXCloudVideoView localVideoView = myselfVideoView.getCloudVideoViewByIndex(0);
            localVideoView.setUserId(dataManager.getUserCode());
            //3、开始本地视频图像
            startLocalVideo(true);

            //4. 开始音频
            enableAudioCapture(true);
        }
    }

    private void unInitTrtc() {
        if (mTrtcCloud != null) {
            //3、停止本地视频图像
            mTrtcCloud.stopLocalPreview();
            enableAudioCapture(false);
        }
    }

    private void startLocalVideo(boolean enable) {
        if (mTrtcCloud != null) {
            // 大画面的编码器参数设置
            TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
            encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
            encParam.videoFps = 15;
            encParam.videoBitrate = 550;
            // videoResolutionMode 设置为横屏
            encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE;
            mTrtcCloud.setVideoEncoderParam(encParam);
            final String usrid = dataManager.getUserCode();
            TXCloudVideoView localVideoView = myselfVideoView.getCloudVideoViewByUseId(usrid);
            localVideoView.setUserId(usrid);
            localVideoView.setVisibility(View.VISIBLE);
            if (enable) {
                mTrtcCloud.startLocalPreview(mEnableFrontCamera, localVideoView);
            } else {
                mTrtcCloud.stopLocalPreview();
            }
        }
    }

    private void enableAudioCapture(boolean bEnable) {
        if (mTrtcCloud != null) {
            if (bEnable) {
                mTrtcCloud.startLocalAudio();
            } else {
                mTrtcCloud.stopLocalAudio();
            }
        }

    }
    //    对应的远端主路（即摄像头）画面的状态通知
    @Override
    public void onTICUserVideoAvailable(String userId, boolean available) {
        String broadId=dataManager.getAppid() + "_" + dataManager.getCourseId() + "_pusher";
        if (available) {
            if (!userId.equals(broadId)&&!userId.equals(dataManager.getTeacherCode())){
                classmateVideo.addView(createView(userId));
            }else if(userId.equals(dataManager.getTeacherCode())){
                teacherVideoView = (TICVideoRootView) view.findViewById(R.id.teacher_videoview);
                TXCloudVideoView localteacherVideoView = teacherVideoView.getCloudVideoViewByIndex(0);
                localteacherVideoView.setUserId(dataManager.getTeacherCode());
                if (localteacherVideoView != null) {
                    // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                    mTrtcCloud.setRemoteViewFillMode(dataManager.getTeacherCode(), TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                    mTrtcCloud.startRemoteView(dataManager.getTeacherCode(), TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, localteacherVideoView);
                    localteacherVideoView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (!userId.equals(dataManager.getTeacherCode())&&!userId.equals(broadId)){
                TICVideoRootView ticVideoRootView=(TICVideoRootView)getVideoView(userId).findViewById(R.id.classmate_videoview);
                if (ticVideoRootView.getCloudVideoViewByUseId(userId)!=null){
                    mTrtcCloud.stopRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                    classmateVideo.removeView(getVideoView(userId));
                    ticVideoRootView.getCloudVideoViewByUseId(userId).setVisibility(View.GONE);
                }
            }else if (userId.equals(dataManager.getTeacherCode())){
                TXCloudVideoView localteacherVideoView =teacherVideoView.getCloudVideoViewByUseId(dataManager.getTeacherCode());
                if (localteacherVideoView!=null){
                    mTrtcCloud.stopRemoteView(dataManager.getTeacherCode(), TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                    localteacherVideoView.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public void onUserEnter(String userId) {

    }

    @Override
    public void onUserExit(String userId, int reason) {

    }
    //    获取学生视频view
    private View getVideoView(String studentid) {
        view=(View) map.get(studentid);
        return view;
    }
    @Override
    public void onTICUserSubStreamAvailable(String userId, boolean available) {

    }

    @Override
    public void onTICUserAudioAvailable(String userId, boolean available) {

    }

    @Override
    public void onTICMemberJoin(List<String> userList) {

    }

    @Override
    public void onTICMemberQuit(List<String> userList) {

    }

    @Override
    public void onTICVideoDisconnect(int errCode, String errMsg) {

    }

    @Override
    public void onTICClassroomDestroy() {

    }

    @Override
    public void onTICSendOfflineRecordInfo(int code, String desc) {

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
                initTrtc();
            }

            @Override
            public void forbitPermissons() {
//                拒绝权限的操作
            }
        });
    }

    @Override
    public void onStart() {
        Log.e("生命周期","VideoListFragment-onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e("生命周期","VideoListFragment-onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("生命周期","VideoListFragment-onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e("生命周期","VideoListFragment-onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.e("生命周期","VideoListFragment-onDestroyView");
        unInitTrtc();
        if (classmateVideo!=null){
            classmateVideo.removeAllViews();
        }
        if(mTicManager!=null){
            mTicManager.removeEventListener(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e("生命周期","VideoListFragment-onDestroy");
        super.onDestroy();
    }


}
