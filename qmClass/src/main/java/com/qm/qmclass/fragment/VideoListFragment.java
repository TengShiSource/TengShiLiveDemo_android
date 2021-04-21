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
import com.google.gson.Gson;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.qmmanager.QMClassManagerImpl;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.qm.qmclass.utils.PermissionUtils;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

import static com.tencent.trtc.TRTCCloudDef.TRTCSystemVolumeTypeMedia;


public class VideoListFragment extends Fragment implements TICManager.TICEventListener, View.OnClickListener {
    private View view;
    private TICManager mTicManager;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private TeacherLiveActivity teacherLiveActivity;
    //trtc
    TRTCCloud mTrtcCloud;
    // 实时音视频视图控件
    TICVideoRootView teacherVideoView;
//    private RecyclerView studentVideolist;
    private TextView teachername;
    private RelativeLayout rlTools;
    private LinearLayout shouqi;
    private LinearLayout llTool;
    private LinearLayout teacherShexiangtou;
    private LinearLayout teacherMaike;
    private LinearLayout teacherQiehuan;
    private LinearLayout teacherQuanping;
    private ScrollView svStudent;
    private LinearLayout studentVideo;
    private ImageView addstudent;
    private String videoState;
//    private List<StudentVideoList> studentList=new ArrayList<StudentVideoList>();
    private HashMap<String, Object> map = new HashMap<>();
    boolean mEnableFrontCamera = true;
    private boolean isVideoToolShow=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_videolist, container, false);
        teacherLiveActivity=(TeacherLiveActivity)getActivity();
        dataManager =DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
        teachername=(TextView) view.findViewById(R.id.teachername);
        rlTools=(RelativeLayout) view.findViewById(R.id.rl_tools);
        shouqi=(LinearLayout) view.findViewById(R.id.shouqi);
        llTool=(LinearLayout) view.findViewById(R.id.ll_tool);
        teacherShexiangtou=(LinearLayout) view.findViewById(R.id.teacher_shexiangtou);
        teacherMaike=(LinearLayout) view.findViewById(R.id.teacher_maike);
        teacherQiehuan=(LinearLayout) view.findViewById(R.id.teacher_qiehuan);
        teacherQuanping=(LinearLayout) view.findViewById(R.id.teacher_quanping);
        svStudent=(ScrollView) view.findViewById(R.id.sv_student);
        studentVideo=(LinearLayout) view.findViewById(R.id.student_video);
        addstudent=(ImageView) view.findViewById(R.id.addstudent);
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


        }else if (v.getId()==R.id.teacher_maike){

        }else if (v.getId()==R.id.teacher_qiehuan){

        }else if (v.getId()==R.id.teacher_quanping){

        }else if (v.getId()==R.id.addstudent){

        }
    }
//    创建一个新学生视频View
    private View createView(final String studentCode) {
        //首先引入要添加的View
        final View view=View.inflate(getActivity(), R.layout.student_video_item, null);
        map.put(studentCode,view);
        //找到里面需要动态改变的控件
        TICVideoRootView studentVideoview = (TICVideoRootView) view.findViewById(R.id.student_videoview);
        TXCloudVideoView localVideoView = studentVideoview.getCloudVideoViewByIndex(0);
        localVideoView.setUserId(studentCode);
        if (localVideoView != null) {
            // 启动远程画面的解码和显示逻辑，FillMode 可以设置是否显示黑边
            mTrtcCloud.setRemoteViewFillMode(studentCode, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            mTrtcCloud.startRemoteView(studentCode, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG, localVideoView);
            localVideoView.setVisibility(View.VISIBLE);
        }
        TextView studentname = (TextView) view.findViewById(R.id.studentname);
        final LinearLayout sShouqi = (LinearLayout) view.findViewById(R.id.s_shouqi);
        LinearLayout sVideoHuabi = (LinearLayout) view.findViewById(R.id.s_video_huabi);
        LinearLayout sVideoLanmai = (LinearLayout) view.findViewById(R.id.s_video_lanmai);
        LinearLayout sVideoQuanping = (LinearLayout) view.findViewById(R.id.s_video_quanping);
        final RelativeLayout sRlTools = (RelativeLayout) view.findViewById(R.id.s_rl_tools);
        final RelativeLayout rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
        final LinearLayout sLlTool = (LinearLayout) view.findViewById(R.id.s_ll_tool);
        sRlTools.setBackgroundColor(getResources().getColor(R.color.hlfTransparent));
        sShouqi.setRotation(180);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sShouqi.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sShouqi.setLayoutParams(lp);
        //给控件赋值
        studentname.setText(liveDataManager.getAllStudentsMap().get(studentCode).getNickName());

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
                if (!liveDataManager.getOnLineStudentsMap().get(studentCode).isHuabiOn()){
//老师开启学生画笔
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "brushEnable");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getOnLineStudentsMap().get(studentCode).setHuabiOn(true);
                }else if (liveDataManager.getOnLineStudentsMap().get(studentCode).isLianMai()){
//老师关闭学生画笔
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "brushDisable");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getOnLineStudentsMap().get(studentCode).setHuabiOn(false);
                }

            }
        });
        sVideoLanmai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getOnLineStudentsMap().get(studentCode).isLianMai()){
//                    老师发起让学生挂麦
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "micClose");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(studentCode,msg);
                    liveDataManager.getOnLineStudentsMap().get(studentCode).setLianMai(false);
                }
            }
        });
        sVideoQuanping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),studentCode+"sVideoQuanping",Toast.LENGTH_SHORT).show();

//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlItem.getLayoutParams();
//                lp.height=RelativeLayout.LayoutParams.MATCH_PARENT;
//                lp.width=RelativeLayout.LayoutParams.MATCH_PARENT;
//                rlItem.setLayoutParams(lp);
            }
        });
        return view;
    }
//    获取学生视频view
    private View getVideoView(String studentid) {
        view=(View) map.get(studentid);
        return view;
    }

    //---------trtc--------------
    private void initTrtc() {
        //1、获取trtc
        mTrtcCloud = mTicManager.getTRTCClound();
//        设置使用媒体音量
        mTrtcCloud.setSystemVolumeType(TRTCSystemVolumeTypeMedia);

        if (mTrtcCloud != null) {
            teacherVideoView = (TICVideoRootView) view.findViewById(R.id.teacher_videoview);
            TXCloudVideoView localVideoView = teacherVideoView.getCloudVideoViewByIndex(0);
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
//            mTrtcCloud.setRemoteViewFillMode(false);
            final String usrid = dataManager.getUserCode();
            TXCloudVideoView localVideoView = teacherVideoView.getCloudVideoViewByUseId(usrid);
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
        if (available) {
            String broadId=dataManager.getAppid() + "_" + dataManager.getCourseId() + "_pusher";
            if (!userId.equals(broadId)&&!userId.equals(dataManager.getUserCode())){
                studentVideo.addView(createView(userId));
            }
        } else {
            if (!map.isEmpty()){
                TICVideoRootView ticVideoRootView=(TICVideoRootView)getVideoView(userId).findViewById(R.id.student_videoview);
                if (ticVideoRootView!=null){
                    ticVideoRootView.getCloudVideoViewByUseId(userId).setVisibility(View.GONE);
                    mTrtcCloud.stopRemoteView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                    studentVideo.removeView(getVideoView(userId));
                }
            }

        }
    }

    @Override
    public void onUserEnter(String userId) {
        TXCLog.i("退出加入房间", "TICManager: onUserEnter :" + userId );
        Map<String, String> map = new HashMap<>();
        map.put("studentId", userId);
        map.put("type", "1");
        String str = JSON.toJSONString(map);
        teacherLiveActivity.sendGroupCustomMessage("micStuNotify",dataManager.getUserCode(),str);
//        teacherLiveActivity.changeStudentListLMstate(userId,true);
    }


    @Override
    public void onUserExit(String userId, int reason) {
        TXCLog.i("退出加入房间", "TICManager: onUserExit :" + userId );
        Map<String, String> map = new HashMap<>();
        map.put("studentId", userId);
        map.put("type", "2");
        String str = JSON.toJSONString(map);
        teacherLiveActivity.sendGroupCustomMessage("micStuNotify",dataManager.getUserCode(),str);
        teacherLiveActivity.changeStudentListLMstate(userId,false);
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
            }else if (videoState.equals("JK")){
                if (mTrtcCloud!=null){
                    mTrtcCloud.stopAllRemoteView();
                }
                if (studentVideo!=null){
                    studentVideo.removeAllViews();
                }
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
}
