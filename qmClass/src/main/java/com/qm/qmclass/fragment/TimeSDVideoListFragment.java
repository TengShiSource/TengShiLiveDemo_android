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
import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.StudentLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.qm.qmclass.tencent.TRTCView;
import com.qm.qmclass.utils.PermissionUtils;
import com.qm.qmclass.utils.RoundImageView;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private RelativeLayout teacherVideoView;
    private RelativeLayout myselfVideoView;
    TXCloudVideoView myselfTRTCView;
//    private RecyclerView studentVideolist;
    private TextView teachername;
    private RoundImageView teacherIcon;
    private RoundImageView myselfIcon;
    private TextView myselfname;
    private RelativeLayout rlTools;
    private LinearLayout shouqi;
    private LinearLayout llTool;
    private LinearLayout myselfShexiangtou;
    private ImageView ivShexiangtou;
    private LinearLayout myselfGuamai;
    private LinearLayout myselfMaike;
    private ImageView ivMaike;
    private LinearLayout myselfQiehuan;
    private LinearLayout classmateVideo;

    private LinkedHashMap<String, View> classMateItemMap = new LinkedHashMap<>();
    boolean isFrontCamera = true;
    private boolean isVideoToolShow=true;
    private TRTCView trtcView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timesdvideolist, container, false);
        dataManager =DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
        studentLiveActivity=(StudentLiveActivity) getActivity();
        trtcView= TRTCView.getInstance(studentLiveActivity);
        teacherVideoView = (RelativeLayout) view.findViewById(R.id.teacher_videoview);
        teachername=(TextView) view.findViewById(R.id.teachername);
        teacherIcon=(RoundImageView) view.findViewById(R.id.teacher_icon);
        rlTools=(RelativeLayout) view.findViewById(R.id.rl_tools);
        shouqi=(LinearLayout) view.findViewById(R.id.shouqi);
        llTool=(LinearLayout) view.findViewById(R.id.ll_tool);
        myselfVideoView = (RelativeLayout) view.findViewById(R.id.myself_videoview);
        myselfname=(TextView) view.findViewById(R.id.myselfname);
        myselfIcon=(RoundImageView) view.findViewById(R.id.myself_icon);
        myselfname.setText(dataManager.getUserName());
        myselfShexiangtou=(LinearLayout) view.findViewById(R.id.myself_shexiangtou);
        ivShexiangtou=(ImageView) view.findViewById(R.id.iv_shexiangtou);
        myselfGuamai=(LinearLayout) view.findViewById(R.id.myself_guamai);
        myselfMaike=(LinearLayout) view.findViewById(R.id.myself_maike);
        ivMaike=(ImageView) view.findViewById(R.id.iv_maike);
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
        teachername.setText(dataManager.getTeacherName());
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

        }else if (v.getId()==R.id.myself_shexiangtou){
            if (liveDataManager.isCameraOn()){
                liveDataManager.setCameraOn(false);
                startLocalVideo(false);
                studentLiveActivity.sendGroupCustomMessage("studentVideoClose",dataManager.getUserCode(),"");
            }else {
                liveDataManager.setCameraOn(true);
                startLocalVideo(true);
                studentLiveActivity.sendGroupCustomMessage("studentVideoOpen",dataManager.getUserCode(),"");
            }

        }else if (v.getId()==R.id.myself_guamai){
            studentLiveActivity.toOpenClass();
        }else if (v.getId()==R.id.myself_maike){
            if (liveDataManager.isMaikeOn()){
                liveDataManager.setMaikeOn(false);
                enableAudioCapture(false);
            }else {
                liveDataManager.setMaikeOn(true);
                enableAudioCapture(true);
            }
        }else if (v.getId()==R.id.myself_qiehuan){
            if (isFrontCamera){
                isFrontCamera=false;
                mTrtcCloud.switchCamera();
            }else {
                isFrontCamera=true;
                mTrtcCloud.switchCamera();
            }

        }
    }

    //---------trtc--------------
    private void initTrtc() {
        //1、获取trtc
        mTrtcCloud = mTicManager.getTRTCClound();
//        设置使用媒体音量
//        mTrtcCloud.setSystemVolumeType(TRTCSystemVolumeTypeMedia);

        if (mTrtcCloud != null) {
            if (liveDataManager.isCameraOn()){
                //3、开始本地视频图像
                startLocalVideo(true);
            }else {
                startLocalVideo(false);
            }
            if (liveDataManager.isMaikeOn()){
                //4. 开始音频
                enableAudioCapture(true);
            }else {
                //4. 开始音频
                enableAudioCapture(false);
            }

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
            String userCode=dataManager.getUserCode();
            if (enable) {
                ivShexiangtou.setImageDrawable(studentLiveActivity.getResources().getDrawable(R.mipmap.shexiangtou));
                if (liveDataManager.getTrtcViewmap().get(userCode)==null){
                    //创建TRTC视图
                    myselfTRTCView = trtcView.getTRTCView();
                    myselfTRTCView.setUserId(userCode);
                    myselfTRTCView.setVisibility(View.VISIBLE);
                    myselfVideoView.addView(myselfTRTCView);
                    //添加TRTC视图
                    liveDataManager.getTrtcViewmap().put(userCode,myselfTRTCView);
                }
                myselfIcon.setVisibility(View.GONE);
                myselfVideoView.setVisibility(View.VISIBLE);
                mTrtcCloud.startLocalPreview(isFrontCamera, myselfTRTCView);
            } else {
                ivShexiangtou.setImageDrawable(studentLiveActivity.getResources().getDrawable(R.mipmap.shexiangtou_guan));
                myselfVideoView.setVisibility(View.GONE);
                myselfIcon.setVisibility(View.VISIBLE);
                Glide.with(studentLiveActivity).load(dataManager.getUserIcon()).skipMemoryCache(true).into(myselfIcon);
                mTrtcCloud.stopLocalPreview();
            }
        }
    }

    private void enableAudioCapture(boolean bEnable) {
        if (mTrtcCloud != null) {
            if (bEnable) {
                ivMaike.setImageDrawable(studentLiveActivity.getDrawable(R.mipmap.maike));
                mTrtcCloud.startLocalAudio();
            } else {
                ivMaike.setImageDrawable(studentLiveActivity.getDrawable(R.mipmap.maike_guan));
                mTrtcCloud.stopLocalAudio();
            }
        }

    }
    //设置美颜
    public void setBeauty(){
        TXBeautyManager txBeautyManager=mTrtcCloud.getBeautyManager();
        if (liveDataManager.isOpenBeauty()){
            txBeautyManager.setBeautyLevel(7);
            txBeautyManager.setBeautyStyle(2);
            txBeautyManager.setWhitenessLevel(7);
            txBeautyManager.setRuddyLevel(3);
        }else {
            txBeautyManager.setBeautyLevel(0);
            txBeautyManager.setBeautyStyle(1);
            txBeautyManager.setWhitenessLevel(0);
            txBeautyManager.setRuddyLevel(0);
        }
    }
    //    创建一个新学生视频View
    private View createView(final String studentId) {
        //首先引入要添加的View
        final View view=View.inflate(getActivity(), R.layout.time_student_video_item, null);
        RoundImageView classmateIcon = (RoundImageView) view.findViewById(R.id.time_classmate_icon);
        //找到里面需要动态改变的控件
        RelativeLayout studentVideoview = (RelativeLayout) view.findViewById(R.id.time_classmate_videoview);
        TextView studentname = (TextView) view.findViewById(R.id.time_classmate_name);
        //            创建TRTC视图
        TXCloudVideoView classmateTRTCView = trtcView.getTRTCView();
        classmateTRTCView.setUserId(studentId);
        if (classmateTRTCView != null) {
            // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
            mTrtcCloud.setRemoteViewFillMode(studentId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            mTrtcCloud.startRemoteView(studentId, classmateTRTCView);
            classmateTRTCView.setVisibility(View.VISIBLE);
        }
        studentVideoview.addView(classmateTRTCView);
//            添加TRTC视图
        liveDataManager.getTrtcViewmap().put(studentId,classmateTRTCView);

        if (!liveDataManager.getOnLineStudentsMap().isEmpty()&&liveDataManager.getOnLineStudentsMap().get(studentId)!=null) {
            //给控件赋值
            studentname.setText(liveDataManager.getOnLineStudentsMap().get(studentId).getNickName());
        }
        classMateItemMap.put(studentId,view);
        return view;
    }
    /**
     * 教师摄像头状态
     */
    public void teacherCameraState(){
        if (liveDataManager.isTCameraOn()){
            teacherIcon.setVisibility(View.GONE);
            teacherVideoView.setVisibility(View.VISIBLE);
            TXCloudVideoView teacherTRTCView = liveDataManager.getTrtcViewmap().get(dataManager.getTeacherCode());
            teacherTRTCView.setUserId(dataManager.getTeacherCode());
            if (teacherTRTCView != null) {
                // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                mTrtcCloud.setRemoteViewFillMode(dataManager.getTeacherCode(), TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                mTrtcCloud.startRemoteView(dataManager.getTeacherCode(), teacherTRTCView);
                teacherTRTCView.setVisibility(View.VISIBLE);
                ViewGroup teacherTRTCParent=(ViewGroup) teacherTRTCView.getParent();
                if (teacherTRTCParent!=null){
                    teacherTRTCParent.removeAllViews();
                }
                teacherVideoView.addView(teacherTRTCView);
            }
        }else {
            teacherVideoView.setVisibility(View.GONE);
            teacherIcon.setVisibility(View.VISIBLE);
            if (dataManager.getTeacherIcon()!=null&&!dataManager.getTeacherIcon().equals("")){
                Glide.with(studentLiveActivity).load(dataManager.getTeacherIcon()).skipMemoryCache(true).into(teacherIcon);
            }else {
                teacherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.defu_icon));
            }
        }
    }
    /**
     * 同学摄像头状态
     */
    public void classmateCameraState(String userCode){
        View classmateView =getVideoView(userCode);
        RoundImageView classmateIcon=(RoundImageView)classmateView.findViewById(R.id.time_classmate_icon);
        RelativeLayout classmateVideoview = (RelativeLayout) classmateView.findViewById(R.id.time_classmate_videoview);
        if (liveDataManager.getOnLineStudentsMap().get(userCode).isCameraOn()){
            classmateIcon.setVisibility(View.GONE);
            classmateVideoview.setVisibility(View.VISIBLE);
            TXCloudVideoView classmateTRTCview=liveDataManager.getTrtcViewmap().get(userCode);
            classmateTRTCview.setUserId(userCode);
            if (classmateTRTCview != null) {
                // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                mTrtcCloud.setRemoteViewFillMode(userCode, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                mTrtcCloud.startRemoteView(userCode, classmateTRTCview);
                classmateTRTCview.setVisibility(View.VISIBLE);
                ViewGroup classmateTRTCParent=(ViewGroup) classmateTRTCview.getParent();
                if (classmateTRTCParent!=null){
                    classmateTRTCParent.removeAllViews();
                }
                classmateVideoview.addView(classmateTRTCview);
            }
        }else {
            classmateIcon.setVisibility(View.VISIBLE);
            classmateVideoview.setVisibility(View.GONE);
            Glide.with(studentLiveActivity).load(liveDataManager.getOnLineStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(classmateIcon);
        }
    }
    //    对应的远端主路（即摄像头）画面的状态通知
    @Override
    public void onTICUserVideoAvailable(String userId, boolean available) {
    }

    @Override
    public void onUserEnter(String userId) {
        String broadId=dataManager.getAppid() + "_" + dataManager.getCourseId() + "_pusher";
        if (!userId.equals(broadId)&&!userId.equals(dataManager.getTeacherCode())){
            classmateVideo.addView(createView(userId));
        }else if(userId.equals(dataManager.getTeacherCode())){
            //            创建TRTC视图
            TXCloudVideoView teacherTRTCView = trtcView.getTRTCView();
            teacherTRTCView.setUserId(userId);
            if (teacherTRTCView != null) {
                // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                mTrtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                mTrtcCloud.startRemoteView(userId, teacherTRTCView);
                teacherTRTCView.setVisibility(View.VISIBLE);
            }
            teacherVideoView.addView(teacherTRTCView);
//            添加TRTC视图
            liveDataManager.getTrtcViewmap().put(userId,teacherTRTCView);
            if (liveDataManager.isTCameraOn()){
                teacherIcon.setVisibility(View.GONE);
                teacherVideoView.setVisibility(View.VISIBLE);
            }else {
                teacherIcon.setVisibility(View.VISIBLE);
                teacherVideoView.setVisibility(View.GONE);
                Glide.with(studentLiveActivity).load(dataManager.getTeacherIcon()).skipMemoryCache(true).into(teacherIcon);
            }

        }
    }

    @Override
    public void onUserExit(String userId, int reason) {
        String broadId=dataManager.getAppid() + "_" + dataManager.getCourseId() + "_pusher";
        if (!userId.equals(dataManager.getTeacherCode())&&!userId.equals(broadId)){
            TXCloudVideoView classmateTRTCView =liveDataManager.getTrtcViewmap().get(userId);
            if (classmateTRTCView!=null){
                mTrtcCloud.stopRemoteView(userId);
                classmateTRTCView.setVisibility(View.GONE);
                classmateVideo.removeView(getVideoView(userId));
                liveDataManager.getTrtcViewmap().remove(userId);
                classMateItemMap.remove(userId);
            }
        }else if (userId.equals(dataManager.getTeacherCode())){
            TXCloudVideoView localTeacherTRTCView =liveDataManager.getTrtcViewmap().get(userId);
            if (localTeacherTRTCView!=null){
                mTrtcCloud.stopRemoteView(dataManager.getTeacherCode());
                localTeacherTRTCView.setVisibility(View.GONE);
                liveDataManager.getTrtcViewmap().remove(userId);
            }
            teacherVideoView.setVisibility(View.GONE);
            teacherIcon.setVisibility(View.VISIBLE);
            if (dataManager.getTeacherIcon()!=null&&!dataManager.getTeacherIcon().equals("")){
                Glide.with(studentLiveActivity).load(dataManager.getTeacherIcon()).skipMemoryCache(true).into(teacherIcon);
            }else {
                teacherIcon.setImageDrawable(getResources().getDrawable(R.mipmap.defu_icon));
            }
        }
    }
    //    获取学生视频view
    private View getVideoView(String studentid) {
        view=classMateItemMap.get(studentid);
        return view;
    }

    public void showView(String userCode){
        View trtcView=liveDataManager.getTrtcViewmap().get(userCode);
        if (trtcView!=null){
            ViewGroup trtcViewParent=(ViewGroup) trtcView.getParent();
            if (trtcViewParent!=null){
                trtcViewParent.removeAllViews();
            }
        }
        if (userCode.equals(dataManager.getTeacherCode())){
            teacherVideoView.addView(trtcView);
            if (!liveDataManager.getOnLineStudentsMap().get(userCode).isCameraOn()){
                teacherVideoView.setVisibility(View.GONE);
                teacherIcon.setVisibility(View.VISIBLE);
                Glide.with(studentLiveActivity).load(dataManager.getTeacherIcon()).skipMemoryCache(true).into(teacherIcon);
            }
        }else if (userCode.equals(dataManager.getUserCode())){
            myselfVideoView.addView(trtcView);
            if (!liveDataManager.getOnLineStudentsMap().get(userCode).isCameraOn()){
                myselfVideoView.setVisibility(View.GONE);
                myselfIcon.setVisibility(View.VISIBLE);
                Glide.with(studentLiveActivity).load(dataManager.getUserIcon()).skipMemoryCache(true).into(myselfIcon);
            }
        }else {
            View classmateItemView=getVideoView(userCode);
            RelativeLayout classMateVideoview = (RelativeLayout) classmateItemView.findViewById(R.id.time_classmate_videoview);
            RoundImageView classmateIcon=(RoundImageView)classmateItemView.findViewById(R.id.time_classmate_icon);
            classMateVideoview.addView(trtcView);
            if (!liveDataManager.getOnLineStudentsMap().get(userCode).isCameraOn()){
                classmateIcon.setVisibility(View.VISIBLE);
                classMateVideoview.setVisibility(View.GONE);
                Glide.with(studentLiveActivity).load(liveDataManager.getOnLineStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(classmateIcon);
            }
        }
    }
    //静音
    public void mute(boolean ismMandatory){
        if (ismMandatory){
            liveDataManager.setMaikeOn(false);
            enableAudioCapture(false);
            ivMaike.setImageDrawable(studentLiveActivity.getDrawable(R.mipmap.maike_guan));
            myselfMaike.setEnabled(false);
        }else {
            liveDataManager.setMaikeOn(false);
            enableAudioCapture(false);
            ivMaike.setImageDrawable(studentLiveActivity.getDrawable(R.mipmap.maike_guan));
        }
    }
    //取消静音
    public void cancelMute(){
        myselfMaike.setEnabled(true);
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
        liveDataManager.getTrtcViewmap().clear();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e("生命周期","VideoListFragment-onDestroy");
        super.onDestroy();
    }


}
