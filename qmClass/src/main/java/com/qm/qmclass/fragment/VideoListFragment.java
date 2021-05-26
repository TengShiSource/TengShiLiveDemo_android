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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.qmmanager.QMClassManagerImpl;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.qm.qmclass.tencent.TRTCView;
import com.qm.qmclass.utils.LivePopupWindow;
import com.qm.qmclass.utils.PermissionUtils;
import com.qm.qmclass.utils.RefreshJianKongListener;
import com.qm.qmclass.utils.RoundImageView;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.trtc.TRTCCloudDef.TRTCSystemVolumeTypeMedia;
import static com.tencent.trtc.TRTCCloudDef.TRTC_AUDIO_QUALITY_SPEECH;
import static com.tencent.trtc.TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE;
import static com.tencent.trtc.TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER;


public class VideoListFragment extends Fragment implements TICManager.TICEventListener, View.OnClickListener {
    private View view;
    private TICManager mTicManager;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private TeacherLiveActivity teacherLiveActivity;
    //trtc
    TRTCCloud mTrtcCloud;
    // 实时音视频视图控件
    RelativeLayout teacherVideoView;
    TXCloudVideoView teacherLocalVideoView;
//    private RecyclerView studentVideolist;
    private TextView teachername;
    private RelativeLayout rlTools;
    private LinearLayout shouqi;
    private LinearLayout llTool;
    private RoundImageView teacherIcon;
    private LinearLayout teacherShexiangtou;
    private ImageView ivShexiangtou;
    private LinearLayout teacherMaike;
    private ImageView ivMaike;
    private LinearLayout teacherQiehuan;
    private LinearLayout teacherQuanping;
    private ScrollView svStudent;
    private LinearLayout studentVideo;
    private LinearLayout addstudent;
    private String videoState;
//    private List<StudentVideoList> studentList=new ArrayList<StudentVideoList>();
    private LinkedHashMap<String, View> studentViewMap = new LinkedHashMap<>();
    private boolean isVideoToolShow=true;
    private RefreshJianKongListener refreshJianKongListener;
    boolean isFrontCamera = true;
    private TRTCView trtcView;
    private LivePopupWindow qpPopupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_videolist, container, false);
        teacherLiveActivity=(TeacherLiveActivity)getActivity();
        dataManager =DataManager.getInstance();
        trtcView=TRTCView.getInstance(teacherLiveActivity);
        liveDataManager=LiveDataManager.getInstance();
        liveDataManager.setVideoListFragment(this);
        teacherVideoView = (RelativeLayout) view.findViewById(R.id.teacher_videoview);
        teachername=(TextView) view.findViewById(R.id.teachername);
        rlTools=(RelativeLayout) view.findViewById(R.id.rl_tools);
        shouqi=(LinearLayout) view.findViewById(R.id.shouqi);
        llTool=(LinearLayout) view.findViewById(R.id.ll_tool);
        teacherShexiangtou=(LinearLayout) view.findViewById(R.id.teacher_shexiangtou);
        ivShexiangtou=(ImageView) view.findViewById(R.id.iv_shexiangtou);
        teacherIcon=(RoundImageView) view.findViewById(R.id.teacher_icon);
        teacherIcon.setVisibility(View.GONE);
        teacherMaike=(LinearLayout) view.findViewById(R.id.teacher_maike);
        ivMaike=(ImageView) view.findViewById(R.id.iv_maike);
        teacherQiehuan=(LinearLayout) view.findViewById(R.id.teacher_qiehuan);
        teacherQuanping=(LinearLayout) view.findViewById(R.id.teacher_quanping);
        svStudent=(ScrollView) view.findViewById(R.id.sv_student);
        studentVideo=(LinearLayout) view.findViewById(R.id.student_video);
        addstudent=(LinearLayout) view.findViewById(R.id.addstudent);
        shouqi.setOnClickListener(this);
        teacherShexiangtou.setOnClickListener(this);
        teacherMaike.setOnClickListener(this);
        teacherQiehuan.setOnClickListener(this);
        teacherQuanping.setOnClickListener(this);
        addstudent.setOnClickListener(this);
        mTicManager= QMSDK.getTICManager();
        mTicManager.addEventListener(VideoListFragment.this);
        CheckPermission();
        hidenAnimation();
        teachername.setText(dataManager.getUserName());
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
            if (liveDataManager.isTCameraOn()){
                //关闭摄像头
                ivShexiangtou.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.shexiangtou_guan));
                liveDataManager.setTCameraOn(false);
                startLocalVideo(false);
                teacherLiveActivity.sendGroupCustomMessage("teacherVideoClose",dataManager.getUserCode(),"");

            }else {
                //开启摄像头
                ivShexiangtou.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.shexiangtou));
                liveDataManager.setTCameraOn(true);
                startLocalVideo(true);
                teacherLiveActivity.sendGroupCustomMessage("teacherVideoOpen",dataManager.getUserCode(),"");
            }

        }else if (v.getId()==R.id.teacher_maike){
            if (liveDataManager.isTMaikeOn()){
                //关闭音频
                ivMaike.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.maike_guan));
                liveDataManager.setTMaikeOn(false);
                enableAudioCapture(false);
            }else {
                //开始音频
                ivMaike.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.maike));
                liveDataManager.setTMaikeOn(true);
                enableAudioCapture(true);
            }
        }else if (v.getId()==R.id.teacher_qiehuan){
            if (isFrontCamera){
                isFrontCamera=false;
                mTrtcCloud.switchCamera();
            }else {
                isFrontCamera=true;
                mTrtcCloud.switchCamera();
            }

        }else if (v.getId()==R.id.teacher_quanping){
           showQP(v,dataManager.getUserCode());
        }else if (v.getId()==R.id.addstudent){
            teacherLiveActivity.showAddStudent(v);
        }
    }
    //---------trtc--------------
    private void initTrtc() {
        //1、获取trtc
        mTrtcCloud = mTicManager.getTRTCClound();
        if (mTrtcCloud != null) {
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
//            mTrtcCloud.setRemoteViewFillMode(false);
//            final String usrid = dataManager.getUserCode();
            if (liveDataManager.getTrtcViewmap().get(dataManager.getUserCode())==null){
                teacherLocalVideoView = trtcView.getTRTCView();
                teacherLocalVideoView.setUserId(dataManager.getUserCode());
                teacherLocalVideoView.setVisibility(View.VISIBLE);
                teacherVideoView.addView(teacherLocalVideoView);
                liveDataManager.getTrtcViewmap().put(dataManager.getUserCode(),teacherLocalVideoView);
            }
            if (enable) {
                teacherIcon.setVisibility(View.GONE);
                teacherVideoView.setVisibility(View.VISIBLE);
                mTrtcCloud.startLocalPreview(isFrontCamera, teacherLocalVideoView);
                liveDataManager.getTrtcViewmap().put(dataManager.getUserCode(),teacherLocalVideoView);
            } else {
                teacherVideoView.setVisibility(View.GONE);
                teacherIcon.setVisibility(View.VISIBLE);
                Glide.with(teacherLiveActivity).load(dataManager.getUserIcon()).skipMemoryCache(true).into(teacherIcon);
                mTrtcCloud.stopLocalPreview();
            }
        }
    }

    private void enableAudioCapture(boolean bEnable) {
        if (mTrtcCloud != null) {
            if (bEnable) {
                mTrtcCloud.startLocalAudio(TRTC_AUDIO_QUALITY_SPEECH);
            } else {
                mTrtcCloud.stopLocalAudio();
            }
        }

    }
    //关闭和开启SDK音量
    public void enableSDKAudio(boolean bEnable) {
        if (mTrtcCloud != null) {
            if (bEnable) {
//                mTrtcCloud.setAudioCaptureVolume(0);
                mTrtcCloud.setAudioPlayoutVolume(0);
//                mTrtcCloud.setAudioRoute(TRTC_AUDIO_ROUTE_EARPIECE);

            } else {
                mTrtcCloud.muteLocalAudio(false);
            }
        }

    }
//    创建一个新学生视频View
    private View createView(final String studentCode) {
        //首先引入要添加的View
        final View studentVideoItemView=View.inflate(getActivity(), R.layout.student_video_item, null);
        //找到里面需要动态改变的控件
        RelativeLayout studentVideoview = (RelativeLayout) studentVideoItemView.findViewById(R.id.student_videoview);
        TextView studentname = (TextView) studentVideoItemView.findViewById(R.id.studentname);

        final LinearLayout sShouqi = (LinearLayout) studentVideoItemView.findViewById(R.id.s_shouqi);
        LinearLayout sVideoHuabi = (LinearLayout) studentVideoItemView.findViewById(R.id.s_video_huabi);
        ImageView ivHuabi= (ImageView) studentVideoItemView.findViewById(R.id.iv_huabi);
        LinearLayout sVideoLanmai = (LinearLayout) studentVideoItemView.findViewById(R.id.s_video_lanmai);
        LinearLayout sVideoQuanping = (LinearLayout) studentVideoItemView.findViewById(R.id.s_video_quanping);
        final RelativeLayout sRlTools = (RelativeLayout) studentVideoItemView.findViewById(R.id.s_rl_tools);
        final RelativeLayout rlItem = (RelativeLayout) studentVideoItemView.findViewById(R.id.rl_item);
        final LinearLayout sLlTool = (LinearLayout) studentVideoItemView.findViewById(R.id.s_ll_tool);

        sRlTools.setBackgroundColor(getResources().getColor(R.color.hlfTransparent));
        sShouqi.setRotation(180);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sShouqi.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sShouqi.setLayoutParams(lp);
        //给控件赋值
        if (liveDataManager.getAllStudentsMap().get(studentCode)!=null){
            studentname.setText(liveDataManager.getAllStudentsMap().get(studentCode).getNickName());
        }
        //设置每个View的点击事件
        sShouqi.setOnClickListener(new View.OnClickListener() {
            boolean isstudentVideoToolShow=false;
            @Override
            public void onClick(View v) {
                if (isstudentVideoToolShow){
                    isstudentVideoToolShow=false;
                    sRlTools.setBackgroundColor(getResources().getColor(R.color.hlfTransparent));
                    TranslateAnimation hiden = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    hiden.setDuration(500);
                    sLlTool.startAnimation(hiden);
                    sShouqi.setRotation(180);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sShouqi.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    sShouqi.setLayoutParams(lp);
                }else {
                    isstudentVideoToolShow=true;
                    sRlTools.setBackground(getResources().getDrawable(R.mipmap.videotool));
                    sShouqi.setRotation(360);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sShouqi.getLayoutParams();
                    lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    sShouqi.setLayoutParams(lp);
                    TranslateAnimation show = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    show.setDuration(500);
                    sLlTool.startAnimation(show);
                }
            }
        });
        sVideoHuabi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liveDataManager.getAllStudentsMap().get(studentCode).isHuabiOn()){
                   //老师开启学生画笔
                    ivHuabi.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.huabi_lv));
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "brushEnable");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getAllStudentsMap().get(studentCode).setHuabiOn(true);
                }else if (liveDataManager.getAllStudentsMap().get(studentCode).getLianMaiState()==1&&liveDataManager.getAllStudentsMap().get(studentCode).isHuabiOn()){
                   //老师关闭学生画笔
                    ivHuabi.setImageDrawable(teacherLiveActivity.getResources().getDrawable(R.mipmap.huabi));
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "brushDisable");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getAllStudentsMap().get(studentCode).setHuabiOn(false);
                }

            }
        });
        sVideoLanmai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAllStudentsMap().get(studentCode).getLianMaiState()==1){
//                    老师发起让学生挂麦
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "micClose");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getAllStudentsMap().get(studentCode).setLianMaiState(3);
                    liveDataManager.getAllStudentsMap().get(studentCode).setHuabiOn(false);
                }
            }
        });
        sVideoQuanping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),studentCode+"sVideoQuanping",Toast.LENGTH_SHORT).show();
                showQP(v,studentCode);
            }
        });
        studentViewMap.put(studentCode,studentVideoItemView);
        return studentVideoItemView;
    }
//    获取学生视频view
    private View getVideoView(String studentid) {
        View studentVideoItemView=studentViewMap.get(studentid);
        return studentVideoItemView;
    }

    //    对应的远端主路（即摄像头）画面的状态通知
    @Override
    public void onTICUserVideoAvailable(String userId, boolean available) {
    }
    //摄像头状态改变
    public void studentCameraState(String userCode){
        View studentVideoView=getVideoView(userCode);
        RelativeLayout rlview = (RelativeLayout) studentVideoView.findViewById(R.id.student_videoview);
        LinearLayout onlianmai = (LinearLayout) studentVideoView.findViewById(R.id.onlianmai);
        RoundImageView studentIcon=(RoundImageView) studentVideoView.findViewById(R.id.student_icon);
        if (liveDataManager.getAllStudentsMap().get(userCode).isCameraOn()){
            TXCloudVideoView studentTRTCView=liveDataManager.getTrtcViewmap().get(userCode);
            if (studentTRTCView != null) {
                // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                mTrtcCloud.setRemoteViewFillMode(userCode, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                mTrtcCloud.startRemoteView(userCode, studentTRTCView);
                studentTRTCView.setVisibility(View.VISIBLE);
            }
            if (videoState.equals("JK")){
                refreshJianKongListener.refreshSomeOne(userCode);
            }else if(videoState.equals("TRTC")){
                onlianmai.setVisibility(View.GONE);
                studentIcon.setVisibility(View.GONE);
                rlview.setVisibility(View.VISIBLE);
                //                添加TRTC视图
                ViewGroup studentTRTCViewParent=(ViewGroup) studentTRTCView.getParent();
                if (studentTRTCViewParent!=null){
                    studentTRTCViewParent.removeAllViews();
                }
                rlview.addView(studentTRTCView);
            }
        }else {
            if (videoState.equals("JK")){
                refreshJianKongListener.refreshSomeOne(userCode);
            }else if (videoState.equals("TRTC")){
                //                移除TRTC视图
                rlview.removeView(liveDataManager.getTrtcViewmap().get(userCode));
                rlview.setVisibility(View.GONE);
                onlianmai.setVisibility(View.GONE);
                studentIcon.setVisibility(View.VISIBLE);
                Glide.with(teacherLiveActivity).load(liveDataManager.getAllStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(studentIcon);
            }
        }
    }
//    添加连麦学生视图
    public void addStudentItem(String userCode){
        View studentVideoItemView=createView(userCode);
        //找到里面需要动态改变的控件
        LinearLayout onlianmai = (LinearLayout) studentVideoItemView.findViewById(R.id.onlianmai);
        RoundImageView studentIcon=(RoundImageView) studentVideoItemView.findViewById(R.id.student_icon);
        if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==2){
            onlianmai.setVisibility(View.VISIBLE);
            studentIcon.setVisibility(View.GONE);
        }
        studentVideo.addView(studentVideoItemView);
        if (videoState.equals("JK")){
            refreshJianKongListener.refreshSomeOne(userCode);
        }
    }
    // 拒绝连麦
    public void refuseLianMai(String userCode){
        if (getVideoView(userCode)!=null){
            studentVideo.removeView(getVideoView(userCode));
        }
        studentViewMap.remove(userCode);
        //            移除TRTC视图
        liveDataManager.getTrtcViewmap().remove(userCode);

        if (videoState.equals("JK")){
            refreshJianKongListener.refreshSomeOne(userCode);
        }

    }

    @Override
    public void onUserEnter(String userId) {
        TXCLog.i("加入房间", "TICManager: onUserEnter :" + userId );
        String broadId=dataManager.getAppid() + "_" + dataManager.getCourseId() + "_pusher";
        if (!userId.equals(broadId)){
            Map<String, String> map = new HashMap<>();
            map.put("studentId", userId);
            map.put("type", "1");
            String str = JSON.toJSONString(map);
            teacherLiveActivity.sendGroupCustomMessage("micStuNotify",dataManager.getUserCode(),str);
            if (liveDataManager.getAllStudentsMap().get(userId)!=null) {
                liveDataManager.getAllStudentsMap().get(userId).setLianMaiState(1);
            }
            liveDataManager.getLianMaiList().add(userId);
//            创建TRTC视图
            TXCloudVideoView studentTRTCView = trtcView.getTRTCView();
            studentTRTCView.setUserId(userId);
            if (studentTRTCView != null) {
                // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
                mTrtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                mTrtcCloud.startRemoteView(userId, studentTRTCView);
                studentTRTCView.setVisibility(View.VISIBLE);
            }
//            添加TRTC视图
            liveDataManager.getTrtcViewmap().put(userId,studentTRTCView);
            View studentVideoView=getVideoView(userId);
            if (studentVideoView==null){
                studentVideoView=createView(userId);
                studentVideo.addView(studentVideoView);
            }
            if (videoState.equals("TRTC")){
                RelativeLayout rlview = (RelativeLayout) studentVideoView.findViewById(R.id.student_videoview);
                LinearLayout onlianmai = (LinearLayout) studentVideoView.findViewById(R.id.onlianmai);
                RoundImageView studentIcon=(RoundImageView) studentVideoView.findViewById(R.id.student_icon);
                if (liveDataManager.getAllStudentsMap().get(userId).getLianMaiState()==2){
                    onlianmai.setVisibility(View.VISIBLE);
                    studentIcon.setVisibility(View.GONE);
                }else {
                    onlianmai.setVisibility(View.GONE);
                }
                if (liveDataManager.getAllStudentsMap().get(userId).isCameraOn()){
                    studentIcon.setVisibility(View.GONE);
                    //                添加列表视图
                    rlview.addView(studentTRTCView);
                }else {
                    studentIcon.setVisibility(View.VISIBLE);
                    Glide.with(teacherLiveActivity).load(liveDataManager.getAllStudentsMap().get(userId).getAvatarUrl()).skipMemoryCache(true).into(studentIcon);
                }

            }else if (videoState.equals("JK")){
                if (videoState.equals("JK")){
                    refreshJianKongListener.refreshSomeOne(userId);
                }
            }
        }
    }


    @Override
    public void onUserExit(String userId, int reason) {
        TXCLog.i("退出房间", "TICManager: onUserExit :" + userId );
        Map<String, String> map = new HashMap<>();
        map.put("studentId", userId);
        map.put("type", "2");
        String str = JSON.toJSONString(map);
        teacherLiveActivity.sendGroupCustomMessage("micStuNotify",dataManager.getUserCode(),str);

        if (liveDataManager.getAllStudentsMap().get(userId)!=null) {
            liveDataManager.getAllStudentsMap().get(userId).setLianMaiState(3);
            teacherLiveActivity.changeStudentListLMstate(userId,3);
        }
        liveDataManager.getLianMaiList().remove(userId);

        if (videoState.equals("JK")&&refreshJianKongListener!=null){
            refreshJianKongListener.refreshSomeOne(userId);
        }

        mTrtcCloud.stopRemoteView(userId);
//            去掉列表视图
        refuseLianMai(userId);
    }
    /*
     *全屏
     */
    public void showQP(View view,final String userCode) {
        if (qpPopupWindow==null){
            qpPopupWindow=new LivePopupWindow(teacherLiveActivity);
        }
        qpPopupWindow.showQPPopupWindow(view,userCode);
        teacherLiveActivity.sendGroupCustomMessage("cameraFull","",userCode);

        liveDataManager.setCameraFullStudent(userCode);
    }
    /*
     *退出全屏
     */
    public void cameraBack(String userCode){
        qpPopupWindow=null;
        liveDataManager.setCameraFullStudent("");
        if (userCode.equals(dataManager.getUserCode())){
            TXCloudVideoView teacherTrtcView=liveDataManager.getTrtcViewmap().get(userCode);
            ViewGroup teacherTrtcViewParent=(ViewGroup) teacherTrtcView.getParent();
            if (teacherTrtcViewParent!=null){
                teacherTrtcViewParent.removeAllViews();
            }
            teacherVideoView.addView(teacherTrtcView);
        }else {
            if (videoState.equals("TRTC")){
                for(String key:liveDataManager.getTrtcViewmap().keySet()){
                    if (key.equals(userCode)){
                        View studentView=studentViewMap.get(key);
                        RelativeLayout studentVideoview = (RelativeLayout) studentView.findViewById(R.id.student_videoview);
                        LinearLayout onlianmai = (LinearLayout) studentView.findViewById(R.id.onlianmai);
                        RoundImageView studentIcon=(RoundImageView) studentView.findViewById(R.id.student_icon);
                        onlianmai.setVisibility(View.GONE);
                        if (liveDataManager.getAllStudentsMap().get(key).isCameraOn()){
                            studentIcon.setVisibility(View.GONE);
                            //                添加列表视图
                            TXCloudVideoView trtcView=liveDataManager.getTrtcViewmap().get(key);
                            ViewGroup trtcViewParent=(ViewGroup) trtcView.getParent();
                            if (trtcViewParent!=null){
                                trtcViewParent.removeAllViews();
                            }
                            studentVideoview.addView(trtcView);
                        }else {
                            studentIcon.setVisibility(View.VISIBLE);
                            Glide.with(teacherLiveActivity).load(liveDataManager.getAllStudentsMap().get(key).getAvatarUrl()).skipMemoryCache(true).into(studentIcon);
                        }
                    }
                }

            }else if (videoState.equals("JK")&&refreshJianKongListener!=null){
                refreshJianKongListener.refreshSomeOne(userCode);
            }
        }

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
    public void onHiddenChanged(boolean hidden) {
        Bundle bundle = getArguments();
        videoState = bundle.getString("videoState");
        if (!hidden){
            Log.e("生命周期","VideoListFragment-onHiddenChanged"+videoState);
            if (videoState.equals("TRTC")){
                svStudent.setVisibility(View.VISIBLE);
                if (liveDataManager.getTrtcViewmap().size()>1){
                    for(String key:liveDataManager.getTrtcViewmap().keySet()){
                        if (!key.equals(dataManager.getUserCode())){
                            View studentView=studentViewMap.get(key);
                            RelativeLayout studentVideoview = (RelativeLayout) studentView.findViewById(R.id.student_videoview);
                            LinearLayout onlianmai = (LinearLayout) studentView.findViewById(R.id.onlianmai);
                            RoundImageView studentIcon=(RoundImageView) studentView.findViewById(R.id.student_icon);
                            onlianmai.setVisibility(View.GONE);
                            if (liveDataManager.getAllStudentsMap().get(key).isCameraOn()){
                                studentIcon.setVisibility(View.GONE);
                                //                添加列表视图
                                TXCloudVideoView trtcView=liveDataManager.getTrtcViewmap().get(key);
                                ViewGroup trtcViewParent=(ViewGroup) trtcView.getParent();
                                if (trtcViewParent!=null){
                                    trtcViewParent.removeAllViews();
                                }
                                studentVideoview.addView(trtcView);
                            }else {
                                studentIcon.setVisibility(View.VISIBLE);
                                Glide.with(teacherLiveActivity).load(liveDataManager.getAllStudentsMap().get(key).getAvatarUrl()).skipMemoryCache(true).into(studentIcon);
                            }

//                    ViewGroup studentViewParent=(ViewGroup) studentView.getParent();
//                    if (studentViewParent!=null){
//                        studentViewParent.removeAllViews();
//                    }
//                    studentVideo.addView(studentView);
                        }
                    }
                }
            }else if (videoState.equals("JK")){
//                if (studentVideo!=null){
//                    studentVideo.removeAllViews();
//                }
                svStudent.setVisibility(View.GONE);
            }
        }
        super.onHiddenChanged(hidden);
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
        if (mTrtcCloud != null) {
            mTrtcCloud.stopAllRemoteView();
        }
        if (studentVideo!=null){
            studentVideo.removeAllViews();
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

    public void setRefreshJianKongListener(RefreshJianKongListener refreshJianKongListener) {
        this.refreshJianKongListener = refreshJianKongListener;
    }
}
