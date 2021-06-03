package com.qm.qmclass.qmmanager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.activitys.StudentLiveActivity;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.base.Constants;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.model.CourseInfo;
import com.qm.qmclass.model.LoginInfor;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.utils.SdkUtil;
import com.qm.qmclass.utils.CrashHandler;
import com.qm.qmclass.utils.SharedPreferencesUtils;
import com.tencent.rtmp.TXLiveBase;

import okhttp3.Request;
import okhttp3.Response;

public class QMClassManagerImpl extends QMClassManager {
    private static String baseurl= BuildConfig.SERVER_URL;
    private final static byte[] SYNC = new byte[1];
    private static volatile QMClassManager instance;
    private static TICManager mticManager;
    private static Context mContext;
//    private static String mrole;
//    private static int courseId=-1;
    private static DataManager dataManager;

    public static QMClassManager sharedInstance() {
        if (instance == null) {
            synchronized (SYNC) {
                if (instance == null) {
                    instance = new QMClassManagerImpl();
                }
            }
        }
        return instance;
    }
    private QMClassManagerImpl() {
    }

    @Override
    public void init(Context context) {
        if (context!=null){
            mContext = context;
        }else {
            mContext=context.getApplicationContext();
        }
        initSomething();
    }

    private static void initSomething() {

        if (SdkUtil.isMainProcess(mContext)) {    // 仅在主线程初始化
            // 初始化TIC
            mticManager = TICManager.getInstance();
            QMSDK.setTicManager(mticManager);
            QMSDK.setContext(mContext);
        }
//        直播推流licence
        String licenceURL = "http://license.vod2.myqcloud.com/license/v1/d7e6e335a169e562588f30880ee20988/TXLiveSDK.licence"; // 获取到的 licence url
        String licenceKey = "83412b67ef1fe4172492048db09c29bc"; // 获取到的 licence key
        TXLiveBase.getInstance().setLicence(mContext, licenceURL, licenceKey);

        SharedPreferencesUtils.getInstance(mContext,"qm_data");
        dataManager=DataManager.getInstance();
    }

    @Override
    public void createOrJoinClassroom(final String token) {
        if (SharedPreferencesUtils.putData(Constants.USER_Token,token)){
            getCourseInfo();
        }
    }

    private static void getCourseInfo(){
        OkHttpUtils.getInstance().Get(baseurl+"/lvbcourse/getCourseInfo", new MyCallBack<BaseResponse<CourseInfo>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<CourseInfo> result) {
                if (result!=null&&result.getData()!=null){
                    CourseInfo courseInfo=result.getData();
                    dataManager.setCourseId(courseInfo.getCourse().getId());
                    dataManager.setCourseName(courseInfo.getCourse().getCourseName());
                    dataManager.setAppid(String.valueOf(courseInfo.getTxAppId()));
                    dataManager.setTeacherCode(courseInfo.getTeacher().getUserCode());
                    dataManager.setTeacherName(courseInfo.getTeacher().getNickName());
                    dataManager.setTeacherIcon(courseInfo.getTeacher().getAvatarUrl());
                    dataManager.setStartTime(courseInfo.getCourse().getStartTime());
                    dataManager.setEndTime(courseInfo.getCourse().getEndTime());
                    dataManager.setRecMethod(courseInfo.getOptions().getRecMethod());
                    dataManager.setOpenClassReminder(courseInfo.getOptions().getOpenClassReminder());

                    mticManager.init(mContext, courseInfo.getTxAppId());

                    //        直播推流licence
                    TXLiveBase.getInstance().setLicence(mContext, courseInfo.getLvbPush().getLicenceURL(), courseInfo.getLvbPush().getLicenceKey());

                    getLoginInfo();
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
    }

    private static void getLoginInfo(){
        OkHttpUtils.getInstance().Get(baseurl+"/member/getLoginInfo", new MyCallBack<BaseResponse<LoginInfor>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<LoginInfor> result) {
                if (result!=null&&result.getData()!=null){
                    String userCode=result.getData().getUserCode();
                    String userSig=result.getData().getUserSig();
                    dataManager.setRole(result.getData().getUserRole());
                    dataManager.setUserid(result.getData().getUserId());
                    dataManager.setUserCode(userCode);
                    dataManager.setUserName(result.getData().getUsername());
                    dataManager.setUserIcon(result.getData().getAvatar());
                    if (result.getData().getUserRole().equals("s")){
                        dataManager.setExpValue(result.getData().getExpValue());
                        dataManager.setExpIcon(result.getData().getExpIcon());
                        dataManager.setStudyCoin(result.getData().getStudyCoin());
                    }
                    if (result.getData().getUserRole()!=null&&!result.getData().getUserRole().equals("")){
                        Login(userCode,userSig);
                    }
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
    }

    private static void Login(final String userCode, final String userSig){
        mticManager.login(userCode, userSig, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                //标志登录成功
                Log.e("Login", "登录腾讯云成功" + userCode);
                if (dataManager.getRole().equals("t")){
                    Intent intent=new Intent(mContext, TeacherLiveActivity.class);
                    mContext.startActivity(intent);
                }else if(dataManager.getRole().equals("s")){
                    Intent intent=new Intent(mContext, StudentLiveActivity.class);
//                        intent.putExtra("RoomId",dataManager.getCourseId());
                    mContext.startActivity(intent);
                }

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("Login", "登录腾讯云失败" + userCode + ",errMsg=" + errMsg);
            }
        });
    }

//    private static void createTXClassroom(){
//        mticManager.createClassroom(dataManager.getCourseId(), TICManager.TICClassScene.TIC_CLASS_SCENE_VIDEO_CALL, new TICManager.TICCallback() {
//            @Override
//            public void onSuccess(Object data) {
//                Intent intent=new Intent(mContext, TeacherLiveActivity.class);
////                intent.putExtra("RoomId",dataManager.getCourseId());
//                mContext.startActivity(intent);
//            }
//
//            @Override
//            public void onError(String module, int errCode, String errMsg) {
//                Log.e("Login", "创建房间失败" + errCode + ",errMsg=" + errMsg);
//                if (errCode==10021){
//                    Intent intent=new Intent(mContext, TeacherLiveActivity.class);
//                    mContext.startActivity(intent);
//                }
//            }
//        });
//    }



}
