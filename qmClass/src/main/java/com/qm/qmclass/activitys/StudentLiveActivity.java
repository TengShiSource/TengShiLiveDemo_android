package com.qm.qmclass.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.adpter.AnswerListAdpter;
import com.qm.qmclass.adpter.AnswerStatAdpter;
import com.qm.qmclass.adpter.DanmuContentAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.fragment.OnLineStudentListFragment;
import com.qm.qmclass.fragment.StudentVideoListFragment;
import com.qm.qmclass.fragment.TimeSDVideoListFragment;
import com.qm.qmclass.model.AnswerListInfo;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentAnswerStatInfo;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.model.YcFileInfo;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.tencent.TICClassroomOption;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.utils.DialogUtil;
import com.qm.qmclass.utils.LivePopupWindow;
import com.qm.qmclass.utils.StudentLivePopupWindow;
import com.qm.qmclass.utils.ToastUtil;
import com.tencent.imsdk.TIMMessage;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.teduboard.TEduBoardController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.teduboard.TEduBoardController.TEduBoardElementType.TEDU_BOARD_ELEMENT_IMAGE;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_ERASER;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_LINE;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_OVAL;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_OVAL_SOLID;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_PEN;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_POINT_SELECT;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_RECT;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_RECT_SOLID;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_TEXT;

public class StudentLiveActivity extends AppCompatActivity implements View.OnClickListener,
        StudentLivePopupWindow.PopupWindowListener,
        TICManager.TICMessageListener,
        TICManager.TICIMStatusListener {
    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivSet;
    private ImageView ivChat;
    private ImageView ivJushou;
    private ImageView ivQuestion;
    private ImageView ivClassover;
    private FrameLayout llBroadcast;
    private ImageView ivChehui;
    private ImageView ivColor;
    private TextView tvText;
    private ImageView ivHuabi;
    private FrameLayout flFrament;
    private ImageView ivVideolist;
    private ImageView ivStudentlist;
    private LinearLayout llDanmu;
    private RelativeLayout rlDanmu;
    private ListView danmulistView;
    private EditText danmuInput;
    private LinearLayout tools;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private StudentVideoListFragment studentVideoListFragment;
    private TimeSDVideoListFragment timeSDVideoListFragment;
    private OnLineStudentListFragment onLineStudentListFragment;
    private List colorList=null;

    private TICManager mTicManager;
    static TEduBoardController mBoard = null;
    MyBoardCallback mBoardCallback;
    boolean mHistroyDataSyncCompleted = false;
    boolean mCanRedo = false;
    boolean mCanUndo = false;

    private RelativeLayout rlVideoView;
    private TXCloudVideoView videoView;
    private static TXLivePlayer mLivePlayer;
    private int count = 0;

    private StudentLivePopupWindow chatPopupWindow;
    private StudentLivePopupWindow questionPopupWindow;
    private StudentLivePopupWindow setPopupWindow;
    private StudentLivePopupWindow classOverPopupWindow;
    private StudentLivePopupWindow toolsPopupWindow;
    private StudentLivePopupWindow colorPopupWindow;
    private StudentLivePopupWindow qpPopupWindow;
    private StudentLivePopupWindow dianMingPopupWindow;
    private StudentLivePopupWindow answerPopupWindow;
    private StudentLivePopupWindow answerDetailPopupWindow;
    private StudentLivePopupWindow rushPopupWindow;
    private StudentLivePopupWindow rushFinishPopupWindow;
    private StudentLivePopupWindow rushRedEnvelopePopupWindow;

    private static Activity mactivity;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private VideoFragmentListener mvideoFragmentListener;
    private DanmuContentAdpter danmuContentAdpter;
    private boolean isquitClass=false;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int GET_PHOTO = 3;
    private Uri headImgUri;
    private File appDir = null;
    private Uri uritempFile = null;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private String currentTime;//当前时间
    private String startTime;//开始时间
    private String endTime;//结束时间
    private Date startDate;//开始时间
    private Date endDate;//结束时间
    private int classState=-1;
    private int recLen = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (classState==1){
                recLen--;
                tvTime.setText("还有 "+timeCalculate(recLen)+" 上课");
                //获取当前时间
                date = new Date(System.currentTimeMillis());
                String currentTime=simpleDateFormat.format(date);
                try {
                    Date currentDate = simpleDateFormat.parse(currentTime);//当前时间
                    if (currentDate.getTime()>=startDate.getTime()) {
                        //已经上课00:00
                        classState=2;
                        recLen=Integer.parseInt(String.valueOf(currentDate.getTime()-startDate.getTime()))/1000;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if (classState==2){
                recLen++;
                tvTime.setText("已上课 "+timeCalculate(recLen));
                //获取当前时间
                date = new Date(System.currentTimeMillis());
                String currentTime=simpleDateFormat.format(date);
                try {
                    Date currentDate = simpleDateFormat.parse(currentTime);//当前时间
                    if (endDate.getTime()-currentDate.getTime()<=600000) {
                        //已经上课00:00
                        classState=3;
                        recLen=Integer.parseInt(String.valueOf(endDate.getTime()-currentDate.getTime()))/1000;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if (classState==3){
                recLen--;
                tvTime.setText("还有 "+timeCalculate(recLen)+" 下课");
                //获取当前时间
                date = new Date(System.currentTimeMillis());
                String currentTime=simpleDateFormat.format(date);
                try {
                    Date currentDate = simpleDateFormat.parse(currentTime);//当前时间
                    if (currentDate.getTime()>endDate.getTime()) {
                        //已经上课00:00
                        classState=4;
                        recLen=Integer.parseInt(String.valueOf(currentDate.getTime()-endDate.getTime()))/1000;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if (classState==4){
                recLen++;
                tvTime.setText("上课超时 "+timeCalculate(recLen));
            }

            handler.postDelayed(this, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_live);
        mactivity=this;

        mTicManager= QMSDK.getTICManager();

        dataManager=DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();

        initView();

        joinOpenClass(true,true);

        getOnLineStudents();

        mTicManager.addIMMessageListener(this);
        mTicManager.addIMStatusListener(this);
    }
    private void initView(){
        tvTitle=(TextView) findViewById(R.id.tv_title);
        tvTime=(TextView) findViewById(R.id.tv_time);
        ivSet=(ImageView) findViewById(R.id.iv_set);
        ivSet.setOnClickListener(this);
        ivChat=(ImageView) findViewById(R.id.iv_chat);
        ivChat.setOnClickListener(this);
        ivJushou=(ImageView) findViewById(R.id.iv_jushou);
        ivJushou.setOnClickListener(this);
        ivQuestion=(ImageView) findViewById(R.id.iv_question);
        ivQuestion.setOnClickListener(this);
        ivClassover=(ImageView) findViewById(R.id.iv_classover);
        ivClassover.setOnClickListener(this);
        llBroadcast=(FrameLayout) findViewById(R.id.ll_broadcast);
        ivChehui=(ImageView) findViewById(R.id.iv_chehui);
        ivChehui.setOnClickListener(this);
        ivColor=(ImageView) findViewById(R.id.iv_color);
        ivColor.setOnClickListener(this);
        tvText=(TextView) findViewById(R.id.tv_text);
        tvText.setOnClickListener(this);
        ivHuabi=(ImageView) findViewById(R.id.iv_huabi);
        ivHuabi.setOnClickListener(this);
//        白板
        flFrament=(FrameLayout) findViewById(R.id.fl_frament);
//        播放器
        rlVideoView = (RelativeLayout) findViewById(R.id.rl_videoView);

        ivVideolist=(ImageView) findViewById(R.id.iv_videolist);
        ivVideolist.setOnClickListener(this);
        ivStudentlist=(ImageView) findViewById(R.id.iv_studentlist);
        ivStudentlist.setOnClickListener(this);
        llDanmu=(LinearLayout) findViewById(R.id.ll_danmu);
        rlDanmu=(RelativeLayout) findViewById(R.id.rl_danmu);
        danmulistView=(ListView) findViewById(R.id.danmulistView);
        danmuInput=(EditText) findViewById(R.id.danmu_input);
        danmuInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (textView.getText().toString().equals("")||textView.getText()==null){
                    ToastUtil.showToast1(StudentLiveActivity.this,"","请输入内容");
                }else {
                    sendGroupCustomMessage("msg", dataManager.getUserName(), textView.getText().toString());
                    String chatContent = dataManager.getUserName() + ": " + textView.getText().toString();
                    danmuContentAdpter.add(chatContent);
                    danmuInput.setText("");
                }
                return true;
            }
        });
        tools=(LinearLayout) findViewById(R.id.tools);

        ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
        ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
        fragmentManager = this.getSupportFragmentManager();
        tvTitle.setText(dataManager.getCourseName());

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        date = new Date(System.currentTimeMillis());
        currentTime=simpleDateFormat.format(date);
        startTime=dataManager.getStartTime();
        endTime=dataManager.getEndTime();
        try {
            Date currentDate = simpleDateFormat.parse(currentTime);//当前时间
            startDate = simpleDateFormat.parse(startTime);//开始时间
            endDate = simpleDateFormat.parse(endTime);//结束时间
            if (currentDate.getTime() < startDate.getTime()) {
                //还有00:00上课
                classState=1;
                recLen=Integer.parseInt(String.valueOf(startDate.getTime()-currentDate.getTime()))/1000;
                handler.postDelayed(runnable, 1000);
            } else if (currentDate.getTime()>=startDate.getTime()) {
                //已经上课00:00
                classState=2;
                recLen=Integer.parseInt(String.valueOf(currentDate.getTime()-startDate.getTime()))/1000;
                handler.postDelayed(runnable, 1000);
            }  else if (endDate.getTime()-currentDate.getTime()<=600000) {
                //还有00:00下课
                classState=3;
                recLen=Integer.parseInt(String.valueOf(currentDate.getTime()-endDate.getTime()))/1000;
                handler.postDelayed(runnable, 1000);
            }else if (currentDate.getTime() > endDate.getTime()) {
                //上课超时00:00
                classState=4;
                recLen=Integer.parseInt(String.valueOf(currentDate.getTime()-endDate.getTime()))/1000;
                handler.postDelayed(runnable, 1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //学生端进入直播间是拉流观看
        rlVideoView.setVisibility(View.VISIBLE);
        llBroadcast.setVisibility(View.GONE);
        ivColor.setImageDrawable(getResources().getDrawable(R.mipmap.color));
        colorList=new ArrayList();
        colorList.add(0, R.mipmap.red);
        colorList.add(1, R.mipmap.pink);
        colorList.add(2, R.mipmap.yellow);
        colorList.add(3, R.mipmap.green);
        colorList.add(4, R.mipmap.skublue);
        colorList.add(5, R.mipmap.blue);
        colorList.add(6, R.mipmap.violet);
        colorList.add(7, R.mipmap.pinkr);
        colorList.add(8, R.mipmap.orange);
        colorList.add(9, R.mipmap.white);
        colorList.add(10, R.mipmap.gray);
        colorList.add(11, R.mipmap.black);
    }
    /**
     * 进去课堂只加入群组
     */
    private void joinOpenClass(boolean isSend,boolean isjoinGroup) {
        setFragmentSelection(0);
        if (isjoinGroup){
            mTicManager.joinGroup(dataManager.getCourseId(), new TICManager.TICCallback() {
                @Override
                public void onSuccess(Object data) {
                    Map<String, String> map = new HashMap<>();
                    map.put("avatarUrl", dataManager.getUserIcon());
                    map.put("expValue", dataManager.getExpValue());
                    map.put("nickName", dataManager.getUserName());
                    map.put("studyCoin", dataManager.getStudyCoin());
                    map.put("userCode", dataManager.getUserCode());
                    map.put("userId", String.valueOf(dataManager.getUserid()));
                    String str = JSON.toJSONString(map);
                    sendGroupCustomMessage("studentJoin",dataManager.getUserCode(),str);
                    ShowBroadcastVideo();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }else {
            ShowBroadcastVideo();
        }
    }
    /*
     *拉流显示白板
     */
    private void ShowBroadcastVideo(){
        //mPlayerView 添加的界面 view
        if (mLivePlayer==null){
            //创建 player 对象
            mLivePlayer = new TXLivePlayer(this);
        }
        if (mLivePlayer != null) {
            videoView = (TXCloudVideoView) findViewById(R.id.video_view);
            //关键 player 对象与界面 view
            mLivePlayer.setPlayerView(videoView);
            // 设置填充模式
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            String flvUrl = "http://live.jledu.com/live/board_"+dataManager.getCourseId() +".flv";
            Log.e("ShowBroadcastVideo",dataManager.getAppid()+flvUrl);
            mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int event, Bundle param) {
                    Log.e("joinOpenClass",String.valueOf(event));
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        Log.e("joinOpenClass","DISCONNECT");
                        ShowBroadcastVideo();
                        if (count==0){
                            count=1;
                            Log.e("joinOpenClass",String.valueOf(count));
                            ToastUtil.showToast1(StudentLiveActivity.this, "", "当前课堂没有教师，请耐心等待");
                        }
                    }else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                        Log.e("joinOpenClass","BEGIN");
                        ivJushou.setEnabled(true);
                        rlVideoView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        count=0;
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

    /**
     * 切换到实时音白板并加入实时音视频
     */
    public void toBroadcastClass() {
        setFragmentSelection(1);
        liveDataManager.setMyselfLianmai(true);
        llBroadcast.setVisibility(View.VISIBLE);
        rlVideoView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        //2.白板
        mBoard = mTicManager.getBoardController();
        //1、设置白板的回调
        mBoardCallback = new MyBoardCallback(this);

        //2、如果用户希望白板显示出来时，不使用系统默认的参数，就需要设置特性缺省参数，如是使用默认参数，则填null。
        TEduBoardController.TEduBoardInitParam initParam = new TEduBoardController.TEduBoardInitParam();
        initParam.drawEnable = false;
        initParam.ratio = "16:9";
        initParam.globalBackgroundColor=new TEduBoardController.TEduBoardColor(45,47,50,255);
        initParam.brushColor = new TEduBoardController.TEduBoardColor(253, 64, 64, 255);
        initParam.brushThin=50;
        initParam.smoothLevel = 0; //用于指定笔迹平滑级别，默认值0.1，取值[0, 1]
        TICClassroomOption classroomOption = new TICClassroomOption();
        classroomOption.classId = dataManager.getCourseId();
        classroomOption.boardCallback = mBoardCallback;
        classroomOption.boardInitPara = initParam;
        mTicManager.initBoardAndTRTC(classroomOption, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("TeacherLiveActivity","切换到白板成功");
                ivJushou.setEnabled(false);
                if (mLivePlayer!=null){
                    mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
                    mLivePlayer=null;
                }
               if (videoView!=null){
                   videoView.onDestroy();
               }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("TeacherLiveActivity","切换到白板失败");
            }
        });
    }
    /*
     *切换到拉流白板
     */
    public void toOpenClass() {
        //如果是老师，可以清除；
        //如查是学生一般是不要清除数据
        tools.setVisibility(View.GONE);
        liveDataManager.setMyselfLianmai(false);
        if(mTicManager!=null){
            mTicManager.uninitBoardAndTRTC(false);
        }
        removeBoardView();
        joinOpenClass(false,false);
    }
    /*
     *退出群组
     */
    private void quitOpenGroup() {
        if(mTicManager!=null) {
            mTicManager.quitGroup(dataManager.getCourseId(), new TICManager.TICCallback() {
                @Override
                public void onSuccess(Object data) {
                    if (liveDataManager.isJushou()){
                        liveDataManager.setJushou(false);
                        ivJushou.setImageDrawable(getResources().getDrawable(R.mipmap.jushou));
                        Map<String, String> map = new HashMap<>();
                        map.put("action", "handsDown");
                        String str = JSON.toJSONString(map);
                        final byte msg[] = str.getBytes();
                        sendCustomMessage(dataManager.getTeacherCode(),msg);
                    }
                    if (mBoard != null) {
                        removeBoardView();
                    }
                    if (mLivePlayer != null) {
                        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
                        videoView.onDestroy();
                    }
                    mTicManager.uninitBoardAndTRTC(false);
                    mTicManager.removeIMMessageListener(StudentLiveActivity.this);
                    mTicManager.removeIMStatusListener(StudentLiveActivity.this);
                    removeAllFragment();
                    liveDataManager.destroyInstance();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    logout();
                    //将线程销毁掉
                    handler.removeCallbacks(runnable);
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    isquitClass=false;
                    finish();
                }
            });
        }
    }
    /*
     *退出IM
     */
    private void logout(){
        mTicManager.logout(new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("生命周期","StudentLiveActivity-logout");
                isquitClass=true;
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                isquitClass=false;
                finish();
            }
        });
    }
    /*
     *切换fragment
     */
    private void setFragmentSelection(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                if (studentVideoListFragment == null) {
                    studentVideoListFragment = new StudentVideoListFragment();
                    fragmentTransaction.add(R.id.fl_frament, studentVideoListFragment);
                }
                if (timeSDVideoListFragment!=null){
                    fragmentTransaction.remove(timeSDVideoListFragment);
                    timeSDVideoListFragment=null;
                }
                hideFragment(fragmentTransaction);
                fragmentTransaction.show(studentVideoListFragment);
                break;
            case 1:
                if (timeSDVideoListFragment == null) {
                    timeSDVideoListFragment = new TimeSDVideoListFragment();
                    fragmentTransaction.add(R.id.fl_frament, timeSDVideoListFragment);
                }
                if (studentVideoListFragment!=null){
                    fragmentTransaction.remove(studentVideoListFragment);
                    studentVideoListFragment=null;
                }
                hideFragment(fragmentTransaction);
                fragmentTransaction.show(timeSDVideoListFragment);
                break;
            case 2:
                if (onLineStudentListFragment == null) {
                    onLineStudentListFragment = new OnLineStudentListFragment();
                    fragmentTransaction.add(R.id.fl_frament, onLineStudentListFragment);
                }
                hideFragment(fragmentTransaction);
                fragmentTransaction.show(onLineStudentListFragment);
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    /*
     *隐藏所有fragment
     */
    private void hideFragment(FragmentTransaction transaction){
        if(studentVideoListFragment != null){
            transaction.hide(studentVideoListFragment);
        }
        if (timeSDVideoListFragment!=null){
            transaction.hide(timeSDVideoListFragment);
        }
        if(onLineStudentListFragment != null){
            transaction.hide(onLineStudentListFragment);
        }
    }
    /*
     *移除所有fragment
     */
    private void removeAllFragment(){
        if (timeSDVideoListFragment!=null){
            fragmentTransaction.remove(timeSDVideoListFragment);
            timeSDVideoListFragment=null;
        }
        if (studentVideoListFragment!=null){
            fragmentTransaction.remove(studentVideoListFragment);
            studentVideoListFragment=null;
        }
        if(onLineStudentListFragment != null){
            fragmentTransaction.remove(onLineStudentListFragment);
            onLineStudentListFragment=null;
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_chat) {
            if (chatPopupWindow==null){
                chatPopupWindow=new StudentLivePopupWindow(mactivity);
                chatPopupWindow.setPopupWindowListener(this);
            }
            ivChat.setImageDrawable(getDrawable(R.mipmap.liaotian));
            chatPopupWindow.showChatPopupWindow(view,liveDataManager.getChatContentList());
        } else if (view.getId() == R.id.iv_jushou) {
            if (liveDataManager.isJushou()){
                liveDataManager.setJushou(false);
                ivJushou.setImageDrawable(getResources().getDrawable(R.mipmap.jushou));
                Map<String, String> map = new HashMap<>();
                map.put("action", "handsDown");
                String str = JSON.toJSONString(map);
                final byte msg[] = str.getBytes();
                sendCustomMessage(dataManager.getTeacherCode(),msg);
            }else {
                liveDataManager.setJushou(true);
                ivJushou.setImageDrawable(getResources().getDrawable(R.mipmap.jushou_lv));
                Map<String, String> map = new HashMap<>();
                map.put("action", "handsUp");
                String str = JSON.toJSONString(map);
                final byte msg[] = str.getBytes();
                sendCustomMessage(dataManager.getTeacherCode(),msg);
            }
        } else if (view.getId() == R.id.iv_question) {
            if (questionPopupWindow==null){
                questionPopupWindow=new StudentLivePopupWindow(mactivity);
                questionPopupWindow.setPopupWindowListener(this);
            }
            questionPopupWindow.showQuestionPopupWindow(view);
        } else if (view.getId() == R.id.iv_set) {
            if (setPopupWindow==null){
                setPopupWindow=new StudentLivePopupWindow(mactivity);
                setPopupWindow.setPopupWindowListener(this);
            }
            setPopupWindow.showSetPopupWindow(view);
        } else if (view.getId() == R.id.iv_classover) {
            if (classOverPopupWindow==null){
                classOverPopupWindow=new StudentLivePopupWindow(mactivity);
                classOverPopupWindow.setPopupWindowListener(this);
            }
            classOverPopupWindow.quitClass(view);
        } else if (view.getId() == R.id.iv_chehui) {
            mBoard.undo();
        }else if (view.getId() == R.id.iv_color) {
            if (colorPopupWindow==null){
                colorPopupWindow=new StudentLivePopupWindow(mactivity);
                colorPopupWindow.setPopupWindowListener(this);
            }
            colorPopupWindow.showColorPopupWindow(tools,colorList);
        }  else if (view.getId() == R.id.tv_text) {
            if (colorPopupWindow==null){
                colorPopupWindow=new StudentLivePopupWindow(mactivity);
                colorPopupWindow.setPopupWindowListener(this);
            }
            colorPopupWindow.showColorPopupWindow(tools,colorList);
        }else if (view.getId() == R.id.iv_huabi) {
            if (toolsPopupWindow==null){
                toolsPopupWindow=new StudentLivePopupWindow(mactivity);
                toolsPopupWindow.setPopupWindowListener(this);
            }
            toolsPopupWindow.showToolsPopupWindow(tools);
        }else if (view.getId() == R.id.iv_videolist) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
            if (liveDataManager.isMyselfLianmai()){
                setFragmentSelection(1);
            }else {
                setFragmentSelection(0);
            }
        } else if (view.getId() == R.id.iv_studentlist) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist_lv));
            setFragmentSelection(2);
        }
    }
    /*
     *聊天发送按钮点击
     */
    @Override
    public void chatSendOnclick(String data) {
        if (data.equals("")||data==null){
            ToastUtil.showToast1(StudentLiveActivity.this,"","请输入内容");
        }else {
            sendGroupCustomMessage("msg", dataManager.getUserName(), data);
            String chatContent = dataManager.getUserName() + ": " + data;
            chatPopupWindow.addChatContent(chatContent);
        }
    }
    /*
     *点击画笔工具
     */
    @Override
    public void toolItemOnclick(String witch) {
        if (witch.equals("1")){
//            画笔
            ivHuabi.setImageDrawable(getDrawable(R.mipmap.huabi));
//         ivColor.setImageDrawable(getDrawable(R.mipmap.color));
            liveDataManager.setWitchTools("1");
            ivColor.setVisibility(View.VISIBLE);
            ivColor.setImageDrawable(getResources().getDrawable(R.mipmap.color));
            ivColor.setBackgroundResource(0);
            tvText.setVisibility(View.GONE);
            if (mBoard!=null){
                if (liveDataManager.getXian()==1){
                    //曲线
                    mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_PEN);
                }else if (liveDataManager.getXian()==2){
                    //直线
                    mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_LINE);
                }
            }
        }else if (witch.equals("2")){
//            形状
            ivHuabi.setImageDrawable(getDrawable(R.mipmap.xingzhuang));
            liveDataManager.setWitchTools("2");
            ivColor.setVisibility(View.VISIBLE);
            ivColor.setImageDrawable(null);
            tvText.setVisibility(View.GONE);
            xiangZhuangOnclick(liveDataManager.getXingzhuang());
        }else if (witch.equals("3")){
//            文字
            ivHuabi.setImageDrawable(getDrawable(R.mipmap.wenben));
            liveDataManager.setWitchTools("3");
            ivColor.setVisibility(View.GONE);
            tvText.setVisibility(View.VISIBLE);
            tvText.setText(String.valueOf(liveDataManager.getTextProgress()));
            chooseTextColor();
            if (mBoard!=null){
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_TEXT);
            }
        }else if (witch.equals("4")){
//            鼠标
            ivHuabi.setImageDrawable(getDrawable(R.mipmap.shubiao));
            liveDataManager.setWitchTools("4");
            ivColor.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            if (mBoard!=null){
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_POINT_SELECT);
            }
        }else if (witch.equals("5")){
//            橡皮
            ivHuabi.setImageDrawable(getDrawable(R.mipmap.xiangpi));
            liveDataManager.setWitchTools("5");
            ivColor.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            if (mBoard!=null){
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_ERASER);
            }
        }else if (witch.equals("6")){
//            删除
            DialogUtil.showDialog(this, "确定要清空吗？打开文档上的涂鸦内容也会被清空。",
                    "确定",  "取消",false, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            if (mBoard!=null){
                                mBoard.clear(true);
                            }
                        }

                        @Override
                        public void clickNegative() {
                        }
                    });
        }
    }
    /*
     *选择字体颜色
     */
    private void chooseTextColor(){
        if (liveDataManager.getTextColor()==0){
            tvText.setTextColor(getResources().getColor(R.color.b_red));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(253, 64, 64, 255));
        }else if (liveDataManager.getTextColor()==1){
            tvText.setTextColor(getResources().getColor(R.color.b_pink));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(250, 173, 123, 255));
        }else if (liveDataManager.getTextColor()==2){
            tvText.setTextColor(getResources().getColor(R.color.b_yellow));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(224, 255, 164, 255));
        }else if (liveDataManager.getTextColor()==3){
            tvText.setTextColor(getResources().getColor(R.color.b_green));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(40, 178, 139, 255));
        }else if (liveDataManager.getTextColor()==4){
            tvText.setTextColor(getResources().getColor(R.color.b_skublue));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(91, 171, 230, 255));
        }else if (liveDataManager.getTextColor()==5){
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(63, 90, 202, 255));
            tvText.setTextColor(getResources().getColor(R.color.b_blue));
        }else if (liveDataManager.getTextColor()==6){
            tvText.setTextColor(getResources().getColor(R.color.b_violet));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(163, 62, 201, 255));
        }else if (liveDataManager.getTextColor()==7){
            tvText.setTextColor(getResources().getColor(R.color.b_pinkr));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(244, 91, 148, 255));
        }else if (liveDataManager.getTextColor()==8){
            tvText.setTextColor(getResources().getColor(R.color.b_orange));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(255, 162, 0, 255));
        }else if (liveDataManager.getTextColor()==9){
            tvText.setTextColor(getResources().getColor(R.color.b_white));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(255, 255, 255, 255));
        }else if (liveDataManager.getTextColor()==10){
            tvText.setTextColor(getResources().getColor(R.color.b_gray));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(80, 84, 92, 255));
        }else if (liveDataManager.getTextColor()==11){
            tvText.setTextColor(getResources().getColor(R.color.b_black));
            mBoard.setTextColor(new TEduBoardController.TEduBoardColor(0, 0, 0, 255));
        }
    }
    /*
     *显示弹幕
     */
    @Override
    public void showDanmu() {
        if (liveDataManager.isOpenDanmu()){
            llDanmu.setVisibility(View.VISIBLE);
            rlDanmu.setVisibility(View.VISIBLE);
            if (danmuContentAdpter==null){
                danmuContentAdpter=new DanmuContentAdpter(this,R.layout.danmu_content_item,liveDataManager.getChatContentList());
            }
            danmulistView.setAdapter(danmuContentAdpter);

            if(liveDataManager.isJinYan()){
//           禁言禁止手机软键盘
                danmuInput.setInputType(InputType.TYPE_NULL);
                danmuInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
            }else {
                danmuInput.setInputType(InputType.TYPE_CLASS_TEXT);
                danmuInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_edit));
            }

        }else {
            llDanmu.setVisibility(View.GONE);
            rlDanmu.setVisibility(View.GONE);
//            if (chatPopupWindow!=null){
//                chatPopupWindow.refreshChatContent(liveDataManager.getChatContentList());
//            }
        }
    }

    @Override
    public void questionOnclick(String type) {
        if (type.equals("camera")){
            //相机
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
            appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            try {
                appDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //第二个参数为 包名.fileprovider
                headImgUri = FileProvider.getUriForFile(this, "com.qm.qmclass.fileprovider", appDir);
            } else {
                headImgUri = Uri.fromFile(appDir);
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, headImgUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            this.startActivityForResult(intent, TAKE_PHOTO);
        }else if (type.equals("album")){
            //相册
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
            appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GET_PHOTO);
        }
    }

    @Override
    public void signOnclick(long time) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "rollCallResponse");
        map.put("second", String.valueOf(time));
        String str = JSON.toJSONString(map);
        final byte msg[] = str.getBytes();
        sendCustomMessage(dataManager.getTeacherCode(),msg);
    }

    @Override
    public void setBeauty() {
        if(studentVideoListFragment != null){
            studentVideoListFragment.setBeauty();
        }
        if (timeSDVideoListFragment!=null){
            timeSDVideoListFragment.setBeauty();
        }
    }

    /**
     * 裁剪
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        // 裁剪框的比例，1：1
//        intent.putExtra("aspectX", 4);// 输出是X方向的比例
//        intent.putExtra("aspectY", 3);
        // 裁剪后输出图片的尺寸大小,不能太大500程序崩溃
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("circleCrop", false);
        // 图片格式
        /* intent.putExtra("outputFormat", "JPEG"); */
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", true);// 取消人脸识别
        //intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
        //String phone = Build.MODEL;
        //System.out.println(phone);
        // 同一个地址下 裁剪的图片覆盖拍照的图片
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, appDir);
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_PHOTO:
                if (resultCode == RESULT_OK) {
                    crop(data.getData());
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    crop(headImgUri);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap headImage = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uritempFile));
                        File file = getFile(headImage);//把Bitmap转成File
                        OkHttpUtils.getInstance().PostWithFile(BuildConfig.SERVER_URL+"/upload/file",file,new MyCallBack<BaseResponse<String>>() {
                            @Override
                            public void onLoadingBefore(Request request) {

                            }

                            @Override
                            public void onSuccess(BaseResponse<String> result) {
                               if (result.getData()!=null){
                                   postPazzle(result.getData());
                               }
                            }

                            @Override
                            public void onFailure(Request request, Exception e) {

                            }

                            @Override
                            public void onError(Response response) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
    private void postPazzle(String url){
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseId", dataManager.getCourseId());
        map.put("pazzleUrl", url);
        map.put("studentId", dataManager.getUserid());
        String jsonObject=new JSONObject(map).toJSONString();
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/pazzle/pazzle",jsonObject,new MyCallBack<BaseResponse<Boolean>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<Boolean> result) {
                if (result.getData()){
                    ToastUtil.showToast1(mactivity,"","上传成功");
                }else {
                    ToastUtil.showToast1(mactivity,"","上传失败");
                }

            }

            @Override
            public void onFailure(Request request, Exception e) {
                ToastUtil.showToast1(mactivity,"","上传失败");
            }

            @Override
            public void onError(Response response) {
                ToastUtil.showToast1(mactivity,"","上传失败");
            }
        });
    }
//    把bitmap转成file
    public File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
    /*
     *选择画笔颜色
     */
    @Override
    public void colorItemOnclick(int position) {
        if (mBoard!=null){
            GradientDrawable myGrad = (GradientDrawable)ivColor.getBackground();
            if (position==0){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_red));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(253, 64, 64, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_red));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_red));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(253, 64, 64, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(253, 64, 64, 255));
                }
            }else if (position==1){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_pink));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(250, 173, 123, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_pink));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_pink));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(250, 173, 123, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(250, 173, 123, 255));
                }
            }else if (position==2){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_yellow));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(224, 255, 164, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_yellow));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_yellow));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(224, 255, 164, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(224, 255, 164, 255));
                }
            }else if (position==3){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_green));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(40, 178, 139, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_green));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_green));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(40, 178, 139, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(40, 178, 139, 255));
                }
            }else if (position==4){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_skublue));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(91, 171, 230, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_skublue));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_skublue));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(91, 171, 230, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(91, 171, 230, 255));
                }
            }else if (position==5){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_blue));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(63, 90, 202, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_blue));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_blue));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(63, 90, 202, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(63, 90, 202, 255));
                }
            }else if (position==6){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_violet));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(163, 62, 201, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_violet));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_violet));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(163, 62, 201, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(163, 62, 201, 255));
                }
            }else if (position==7){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_pinkr));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(244, 91, 148, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_pinkr));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_pinkr));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(244, 91, 148, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(244, 91, 148, 255));
                }
            }else if (position==8){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_orange));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(255, 162, 0, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_orange));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_orange));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 162, 0, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 162, 0, 255));
                }
            }else if (position==9){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_white));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(255, 255, 255, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_white));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_white));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 255, 255, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 255, 255, 255));
                }
            }else if (position==10){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_gray));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(80, 84, 92, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_gray));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_gray));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(80, 84, 92, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(80, 84, 92, 255));
                }
            }else if (position==11){
                if (liveDataManager.getWitchTools().equals("3")){
                    tvText.setTextColor(getResources().getColor(R.color.b_black));
                    mBoard.setTextColor(new TEduBoardController.TEduBoardColor(0, 0, 0, 255));
                }else if (liveDataManager.getWitchTools().equals("2")){
                    if (liveDataManager.getXingzhuang()==2||liveDataManager.getXingzhuang()==3){
                        myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_black));
                    }else {
                        myGrad.setColor(getResources().getColor(R.color.b_black));
                    }
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(0, 0, 0, 255));
                }else {
                    mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(0, 0, 0, 255));
                }
            }
        }
    }
    /*
     *选择形状
     */
    @Override
    public void xiangZhuangOnclick(int type) {
        LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)ivColor.getLayoutParams();
        if (mBoard!=null){
            if (type==0){
                //实心矩形
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_RECT_SOLID);
                ivColor.setBackgroundResource(R.drawable.changfangxing);
                lp.width= getResources().getDimensionPixelSize(R.dimen.dp_20);
                lp.height=getResources().getDimensionPixelSize(R.dimen.dp_20);
            }else if (type==1){
                //实心椭圆
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_OVAL_SOLID);
                ivColor.setBackgroundResource(R.drawable.tuoyuan_bg);
                lp.width=getResources().getDimensionPixelSize(R.dimen.dp_23);
                lp.height=getResources().getDimensionPixelSize(R.dimen.dp_17);
            }else if (type==2){
                //空心正方形
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_RECT);
                ivColor.setBackgroundResource(R.drawable.zhengfangxing_bg);
                lp.width=getResources().getDimensionPixelSize(R.dimen.dp_20);
                lp.height=getResources().getDimensionPixelSize(R.dimen.dp_20);
            }else if (type==3){
                //空心正圆形
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_OVAL);
                ivColor.setBackgroundResource(R.drawable.yuan);
                lp.width=getResources().getDimensionPixelSize(R.dimen.dp_23);
                lp.height=getResources().getDimensionPixelSize(R.dimen.dp_17);
            }
            GradientDrawable myGrad = (GradientDrawable)ivColor.getBackground();
            if (liveDataManager.getLineColor()==0){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_red));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_red));
                }
            }else if (liveDataManager.getLineColor()==1){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_pink));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_pink));
                }
            }else if (liveDataManager.getLineColor()==2){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_yellow));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_yellow));
                }
            }else if (liveDataManager.getLineColor()==3){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_green));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_green));
                }

            }else if (liveDataManager.getLineColor()==4){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_skublue));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_skublue));
                }

            }else if (liveDataManager.getLineColor()==5){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_blue));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_blue));
                }

            }else if (liveDataManager.getLineColor()==6){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_violet));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_violet));
                }

            }else if (liveDataManager.getLineColor()==7){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_pinkr));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_pinkr));
                }

            }else if (liveDataManager.getLineColor()==8){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_orange));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_orange));
                }

            }else if (liveDataManager.getLineColor()==9){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_white));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_white));
                }

            }else if (liveDataManager.getLineColor()==10){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_gray));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_gray));
                }

            }else if (liveDataManager.getLineColor()==11){
                if (type==2||type==3){
                    myGrad.setStroke(getResources().getDimensionPixelSize(R.dimen.dp_2),getResources().getColor(R.color.b_black));
                }else {
                    myGrad.setColor(getResources().getColor(R.color.b_black));
                }
            }
        }
        ivColor.setLayoutParams(lp);
    }
    /*
     *选择字体大小或线粗细
     */
    @Override
    public void seekBarOnclick(int progress) {
        if (mBoard!=null){
            if (liveDataManager.getWitchTools().equals("1")||liveDataManager.getWitchTools().equals("2")){
                mBoard.setBrushThin(progress);
            }else if (liveDataManager.getWitchTools().equals("3")){
                tvText.setText(String.valueOf(progress));
                mBoard.setTextSize(progress);
            }
        }
    }
    /*
     *选择线条
     */
    @Override
    public void xianOnclick(int type) {
        if (mBoard!=null){
            if (type==1){
                //曲线
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_PEN);
            }else if (type==2){
                //直线
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_LINE);
            }
        }
    }

    @Override
    public void quitClass() {
        quitOpenGroup();
    }


    //发送单独数据
    public void sendCustomMessage(final String usrid, final byte[] msg) {
        mTicManager.sendCustomMessage(usrid, msg, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("发送单独IM", "成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("发送单独IM", errMsg);
                if (errCode != 40005 && errCode != 9520) {
                    ToastUtil.showToast1(StudentLiveActivity.this, "", "发送单独IM" + errCode + ":" + errMsg);
                } else if (errCode == 9520) {
                    ToastUtil.showToast1(StudentLiveActivity.this, "", "本地网络受限/不通，请检查网络！");
                }

            }
        });
    }
    @Override
    public void onTICRecvTextMessage(String fromUserId, String text) {

    }
    /*
     *接收单独IM数据
     */
    @Override
    public void onTICRecvCustomMessage(String fromUserId, byte[] data) {
        String str = new String(data);
        JSONObject jo = JSON.parseObject(str);
        if (jo.getString("action").equals("micOpen")) {
//            学生处理老师的连麦请求
            DialogUtil.showDialog(this, "老师请求与你连麦，是否同意？",
                    "是",  "否",false, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            toBroadcastClass();
                            Map<String, String> map = new HashMap<>();
                            map.put("action", "micOpenRequestResult");
                            map.put("result", "1");
                            String str = JSON.toJSONString(map);
                            final byte msg[] = str.getBytes();
                            sendCustomMessage(fromUserId,msg);
                            if(liveDataManager.isJushou()){
                                liveDataManager.setJushou(false);
                                ivJushou.setImageDrawable(getResources().getDrawable(R.mipmap.jushou));
                            }
                        }

                        @Override
                        public void clickNegative() {
                            Map<String, String> map = new HashMap<>();
                            map.put("action", "micOpenRequestResult");
                            map.put("result", "2");
                            String str = JSON.toJSONString(map);
                            final byte msg[] = str.getBytes();
                            sendCustomMessage(fromUserId,msg);
                        }
                    });


        }else if (jo.getString("action").equals("micClose")){
//            老师发起让学生挂麦
            toOpenClass();
        }else if (jo.getString("action").equals("micOpenRequestResult")){
//            请求与老师连麦反馈
            if (jo.getString("result").equals("1")){
                toBroadcastClass();
            }else if (jo.getString("result").equals("2")){
                ToastUtil.showToast1(StudentLiveActivity.this, "", dataManager.getTeacherName()+"拒绝了连麦请求");
            }
        }else if (jo.getString("action").equals("kickOut")){
            //踢出直播间
            quitOpenGroup();
        }else if (jo.getString("action").equals("brushEnable")){
//            开启画笔
            tools.setVisibility(View.VISIBLE);
            if (mBoard!=null){
                mBoard.setDrawEnable(true);
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_PEN);
            }
        }else if (jo.getString("action").equals("brushDisable")){
//            禁用画笔
            tools.setVisibility(View.GONE);
            if (mBoard!=null) {
                mBoard.setDrawEnable(false);
            }

        }else if (jo.getString("action").equals("synStatus")){
//            同步状态
            JSONObject info = JSON.parseObject(jo.getString("status"));
            if (info.getBoolean("talkDisable")){
                liveDataManager.setJinYan(true);
//            禁言
                if(liveDataManager.isOpenDanmu()){
//        禁止手机软键盘
                    danmuInput.setInputType(InputType.TYPE_NULL);
                    danmuInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
                }else {
                    if (chatPopupWindow!=null&&chatPopupWindow.isShowing()) {
                        EditText messageInput = chatPopupWindow.getContentView().findViewById(R.id.message_input);
                        messageInput.setInputType(InputType.TYPE_NULL);
                        messageInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
                    }
                }
            }else if (info.getBoolean("micDisable")){
                if (timeSDVideoListFragment!=null){
                    timeSDVideoListFragment.mute(true);
                }
            }else if (info.getLong("rollCall")!=0){
                int timeLimit=info.getLong("rollCall").intValue();
                if (dianMingPopupWindow==null){
                    dianMingPopupWindow=new StudentLivePopupWindow(mactivity);
                    dianMingPopupWindow.setPopupWindowListener(this);
                }
                dianMingPopupWindow.showDianMingPopupWindow(mactivity.getWindow().getDecorView(),timeLimit);
            }else if (!info.getBoolean("teacherCameraStatus")){
                liveDataManager.setTCameraOn(false);
                if (studentVideoListFragment!=null){
                    studentVideoListFragment.teacherCameraState();
                }
                if (timeSDVideoListFragment!=null){
                    timeSDVideoListFragment.teacherCameraState();
                }
            }else if (!info.getString("cameraFull").equals("")){
                String cameraFullInfo=info.getString("cameraFull");
                if (qpPopupWindow==null){
                    qpPopupWindow=new StudentLivePopupWindow(this);
                }
                if (timeSDVideoListFragment!=null){
                    qpPopupWindow.showQPPopupWindow(this.getWindow().getDecorView(),cameraFullInfo,"TRTC");
                }else if (studentVideoListFragment!=null){
                    qpPopupWindow.showQPPopupWindow(this.getWindow().getDecorView(),cameraFullInfo,"VIDEO");
                }
            }else if (info.getString("micMembers")!=null){
                List<String> micMembersList = (List<String>) JSONArray.parseArray(info.getString("micMembers"), String.class);
                for (int i=0;i<micMembersList.size();i++){
                    if (studentVideoListFragment!=null){
                        studentVideoListFragment.classmatelianMai(micMembersList.get(i),"1");
                    }
                }
            }else if (info.getString("questionStatus")!=null){
                JSONObject questionInfo = JSON.parseObject(info.getString("questionStatus"));
                Long questionId=questionInfo.getLong("questionId");
                liveDataManager.setQuestionId(questionId);
                int questionType=questionInfo.getInteger("questionType");
                int expValue=questionInfo.getInteger("expValue");
                int timeLimit=questionInfo.getInteger("questionSurplusTime");
                String questionOptions=questionInfo.getString("questionOptions");
                if (answerPopupWindow==null){
                    answerPopupWindow=new StudentLivePopupWindow(mactivity);
                    answerPopupWindow.setPopupWindowListener(this);
                }
                answerPopupWindow.showAnswerPopupWindow(mactivity.getWindow().getDecorView(),questionId,questionType,expValue,timeLimit,questionOptions);
            }else if (info.getLong("timer")!=0){
                int timeLimit=info.getLong("timer").intValue();
            }
        }
    }
    /*
     *发送群组IM
     */
    public void sendGroupCustomMessage(String action, String sender, String info) {
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        map.put("sender", sender);
        map.put("info", info);
        String str = JSON.toJSONString(map);
        final byte msg[] = str.getBytes();
        mTicManager.sendGroupCustomMessage(msg, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("发送群组IM", errCode + ":" + errMsg);
                if (errCode != 40005 && errCode != 9520) {
                    ToastUtil.showToast1(StudentLiveActivity.this, "", "发送群组IM失败" + errCode + ":" + errMsg);
                } else if (errCode == 9520) {
                    ToastUtil.showToast1(StudentLiveActivity.this, "", "本地网络受限/不通，请检查网络！");
                }
            }
        });
    }
    /*
     *接收群组GroupTextIM数据
     */
    @Override
    public void onTICRecvGroupTextMessage(String fromUserId, String text) {
        JSONObject jo = JSON.parseObject(text);
        if (jo.getString("action").equals("studentExit")) {
            String exitStudent=jo.getString("studentIds").toString();
            List<String> list = Arrays.asList(exitStudent.split(","));
            for (int i=0;i<list.size();i++){
                liveDataManager.getOnLineStudentsMap().remove(list.get(i));
            }
            if (onLineStudentListFragment!=null){
                onLineStudentListFragment.showShangKeList();
            }
        }else if (jo.getString("action").equals("classOver")){
            //课堂结束
            quitOpenGroup();
        }else if (jo.getString("action").equals("question")) {
            //老师发起答题卡
            Long questionId=jo.getLong("questionId");
            liveDataManager.setQuestionId(questionId);
            int questionType=jo.getInteger("questionType");
            int expValue=jo.getInteger("expValue");
            int timeLimit=jo.getInteger("timeLimit");
            String questionOptions=jo.getString("questionOptions");
            if (answerPopupWindow==null){
                answerPopupWindow=new StudentLivePopupWindow(mactivity);
                answerPopupWindow.setPopupWindowListener(this);
            }
            answerPopupWindow.showAnswerPopupWindow(mactivity.getWindow().getDecorView(),questionId,questionType,expValue,timeLimit,questionOptions);
        }else if (jo.getString("action").equals("questionFinish")) {
            //老师发起结束答题
            if (answerPopupWindow!=null){
                if (answerPopupWindow.isShowing()){
                    answerPopupWindow.dismiss();
                }
            }
            OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/question/studentAnswerList/"+liveDataManager.getQuestionId(), new MyCallBack<BaseResponse<List<AnswerListInfo>>>() {
                @Override
                public void onLoadingBefore(Request request) {

                }

                @Override
                public void onSuccess(BaseResponse<List<AnswerListInfo>> result) {
                    if (result!=null&&result.getData()!=null){
                        if (answerDetailPopupWindow==null){
                            answerDetailPopupWindow=new StudentLivePopupWindow(mactivity);
                        }
                        answerDetailPopupWindow.showAnswerDetailPopupWindow(mactivity.getWindow().getDecorView(),result.getData());
                    }
                }

                @Override
                public void onFailure(Request request, Exception e) {

                }

                @Override
                public void onError(Response response) {

                }
            });
        }else if (jo.getString("action").equals("questionClose")) {
            //老师发起取消答题
            if (answerPopupWindow!=null){
                if (answerPopupWindow.isShowing()){
                    answerPopupWindow.dismiss();
                }
            }
        }else if (jo.getString("action").equals("rushQuestion")) {
            //老师发起抢答
            Long questionId=jo.getLong("questionId");
            liveDataManager.setQuestionId(questionId);
            if (rushPopupWindow==null){
                rushPopupWindow=new StudentLivePopupWindow(mactivity);
                rushPopupWindow.setPopupWindowListener(this);
            }
            rushPopupWindow.showRushPopupWindow(mactivity.getWindow().getDecorView());
        }else if (jo.getString("action").equals("rushQuestionFinish")) {
            //老师发起抢答完成
            int userId=jo.getInteger("userId");
            if (userId==dataManager.getUserid()){
                int questionType=jo.getInteger("questionType");
                int expValue=jo.getInteger("expValue");
                int timeLimit=jo.getInteger("timeLimit");
                String questionOptions=jo.getString("questionOptions");
                if (rushPopupWindow!=null&&rushPopupWindow.isShowing()){
                    rushPopupWindow.dismiss();
                }
                if (answerPopupWindow==null){
                    answerPopupWindow=new StudentLivePopupWindow(mactivity);
                    answerPopupWindow.setPopupWindowListener(this);
                }
                answerPopupWindow.showAnswerPopupWindow(mactivity.getWindow().getDecorView(),liveDataManager.getQuestionId(),questionType,expValue,timeLimit,questionOptions);
            }else {
                String nickName=jo.getString("nickName");
                if (rushPopupWindow!=null&&rushPopupWindow.isShowing()){
                    rushPopupWindow.refreshRush(nickName);
                }
            }
        }else if (jo.getString("action").equals("rushQuestionAnswerFinish")) {
            //抢答题答题完成
            int userId=jo.getInteger("userId");
            if (userId!=dataManager.getUserid()){
                int expValue=jo.getInteger("expValue");
                int result=jo.getInteger("result");
                String nickName=jo.getString("nickName");
                String questionAnswer=jo.getString("questionAnswer");
                if (rushPopupWindow!=null&&rushPopupWindow.isShowing()){
                    rushPopupWindow.dismiss();
                }
                if (rushFinishPopupWindow==null){
                    rushFinishPopupWindow=new StudentLivePopupWindow(mactivity);
                    rushFinishPopupWindow.setPopupWindowListener(this);
                }
                rushFinishPopupWindow.showRushFinishPopupWindow(mactivity.getWindow().getDecorView(),nickName,expValue,questionAnswer,result);
            }
        }else if (jo.getString("action").equals("redEnvelopeGroup")) {
            //抢红包
            String redPackKey=jo.getString("key");
            if (rushRedEnvelopePopupWindow==null){
                rushRedEnvelopePopupWindow=new StudentLivePopupWindow(mactivity);
                rushRedEnvelopePopupWindow.setPopupWindowListener(this);
            }
            rushRedEnvelopePopupWindow.showRushRedEnvelopePopupWindow(mactivity.getWindow().getDecorView(),redPackKey);
        }
    }
    /*
     *接收群组GroupCustom IM数据
     */
    @Override
    public void onTICRecvGroupCustomMessage(String fromUserId, byte[] data) {
        String str = new String(data);
        JSONObject jo = JSON.parseObject(str);
        if (jo.getString("action").equals("micStuNotify")) {
            JSONObject info = JSON.parseObject(jo.getString("info"));
            if (!info.getString("studentId").equals(dataManager.getUserCode())){
                if (info.getString("type").equals("1")){
//                    有人上台
                    if (studentVideoListFragment!=null){
                        studentVideoListFragment.classmatelianMai(info.getString("studentId"),"1");
                    }
                }else if (info.getString("type").equals("2")){
//                    有人下台
                    if (studentVideoListFragment!=null){
                        studentVideoListFragment.classmatelianMai(info.getString("studentId"),"2");
                    }
                }
            }
        } else if (jo.getString("action").equals("msg")) {
            String chatContent=jo.getString("sender")+": "+jo.getString("info");
            if (liveDataManager.isOpenDanmu()){
                danmuContentAdpter.add(chatContent);
            }else {
                if(chatPopupWindow!=null){
                    if (!chatPopupWindow.isShowing()){
                        ivChat.setImageDrawable(getDrawable(R.mipmap.chat_red));
                    }
                    chatPopupWindow.addChatContent(chatContent);
                }else {
                    liveDataManager.getChatContentList().add(chatContent);
                    ivChat.setImageDrawable(getDrawable(R.mipmap.chat_red));
                }
            }
        }else if(jo.getString("action").equals("talkDisable")){
            liveDataManager.setJinYan(true);
//            禁言
            if(liveDataManager.isOpenDanmu()){
//        禁止手机软键盘
              danmuInput.setInputType(InputType.TYPE_NULL);
              danmuInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
            }else {
                if (chatPopupWindow!=null&&chatPopupWindow.isShowing()) {
                    EditText messageInput = chatPopupWindow.getContentView().findViewById(R.id.message_input);
                    messageInput.setInputType(InputType.TYPE_NULL);
                    messageInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
                }
            }

        }else if(jo.getString("action").equals("talkEnable")){
            liveDataManager.setJinYan(false);
//            解除禁言
            if(liveDataManager.isOpenDanmu()){
                danmuInput.setInputType(InputType.TYPE_CLASS_TEXT);
                danmuInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_edit));
            }else {
                if (chatPopupWindow!=null){
                    EditText messageInput=chatPopupWindow.getContentView().findViewById(R.id.message_input);
                    messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    messageInput.setBackground(getResources().getDrawable(R.drawable.bg_edit));
                }
            }
        }else if (jo.getString("action").equals("teacherJoin")){
            //老师进入教室
            JSONObject info = JSON.parseObject(jo.getString("info"));
            dataManager.setTeacherCode(fromUserId);
            dataManager.setTeacherIcon(info.getString("avatar"));
            dataManager.setTeacherName(info.getString("username"));
            if (studentVideoListFragment!=null){
                studentVideoListFragment.ShowTeacherVideo();
            }
        }else if (jo.getString("action").equals("teacherVideoOpen")){
            //老师开启摄像头
            liveDataManager.setTCameraOn(true);
            if (studentVideoListFragment!=null){
                studentVideoListFragment.teacherCameraState();
            }
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.teacherCameraState();
            }
        }else if (jo.getString("action").equals("teacherVideoClose")){
            //老师关闭摄像头
            liveDataManager.setTCameraOn(false);
            if (studentVideoListFragment!=null){
                studentVideoListFragment.teacherCameraState();
            }
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.teacherCameraState();
            }
        }else if (jo.getString("action").equals("studentVideoClose")){
            //同学关闭摄像头
            liveDataManager.getOnLineStudentsMap().get(fromUserId).setCameraOn(false);
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.classmateCameraState(fromUserId);
            }
        }else if (jo.getString("action").equals("studentVideoOpen")){
            //同学关闭摄像头
            liveDataManager.getOnLineStudentsMap().get(fromUserId).setCameraOn(true);
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.classmateCameraState(fromUserId);
            }
        }else if (jo.getString("action").equals("studentJoin")) {
            //同学加入课堂
            String info=jo.getString("info");
            StudentInfor studentInfor = JSONObject.parseObject(info,StudentInfor.class);
            liveDataManager.getOnLineStudentsMap().put(fromUserId,studentInfor);
            if (onLineStudentListFragment!=null){
                onLineStudentListFragment.showShangKeList();
            }

        }else if (jo.getString("action").equals("cameraFull")) {
            //老师发起全屏
            String info=jo.getString("info");
            if (qpPopupWindow==null){
                qpPopupWindow=new StudentLivePopupWindow(this);
            }
            if (timeSDVideoListFragment!=null){
                qpPopupWindow.showQPPopupWindow(this.getWindow().getDecorView(),info,"TRTC");
            }else if (studentVideoListFragment!=null){
                qpPopupWindow.showQPPopupWindow(this.getWindow().getDecorView(),info,"VIDEO");
            }

        }else if (jo.getString("action").equals("cameraBack")) {
            //老师发起退出全屏
            String info=jo.getString("info");
            if (qpPopupWindow!=null){
                qpPopupWindow.dismiss();
                qpPopupWindow=null;
            }
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.showView(info);
            }else if (studentVideoListFragment!=null){
                studentVideoListFragment.showView(info);
            }
        }else if (jo.getString("action").equals("micDisable")) {
            //老师发起静音
            JSONObject info = JSON.parseObject(jo.getString("info"));
            boolean ismMandatory=info.getBoolean("ismMandatory");
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.mute(ismMandatory);
            }
        }else if (jo.getString("action").equals("micEnable")) {
            //老师发起取消静音
            if (timeSDVideoListFragment!=null){
                timeSDVideoListFragment.cancelMute();
            }
        }else if (jo.getString("action").equals("rollCall")) {
            //老师发起点名
            JSONObject info = JSON.parseObject(jo.getString("info"));
            int timeLimit=info.getInteger("timeLimit");
            if (dianMingPopupWindow==null){
                dianMingPopupWindow=new StudentLivePopupWindow(mactivity);
                dianMingPopupWindow.setPopupWindowListener(this);
            }
            dianMingPopupWindow.showDianMingPopupWindow(mactivity.getWindow().getDecorView(),timeLimit);
        }else if (jo.getString("action").equals("timer")) {
            //老师发起计时器
            JSONObject info = JSON.parseObject(jo.getString("info"));
            long time=info.getLong("time");
        }else if (jo.getString("action").equals("killTimer")) {
            //老师结束计时器
        }
    }

    @Override
    public void onTICRecvMessage(TIMMessage message) {

    }

    @Override
    public void onTICForceOffline() {

    }

    @Override
    public void onTICUserSigExpired() {

    }


    /*
     *白板回调
     */
    private static class MyBoardCallback implements TEduBoardController.TEduBoardCallback {
        WeakReference<StudentLiveActivity> mActivityRef;

        MyBoardCallback(StudentLiveActivity activityEx) {
            mActivityRef = new WeakReference<>(activityEx);
        }

        @Override
        public void onTEBError(int code, String msg) {
            ToastUtil.showToast1(mactivity, "", code + ":" + msg);
        }

        @Override
        public void onTEBWarning(int code, String msg) {
            ToastUtil.showToast1(mactivity, "", code + ":" + msg);
        }

        //        白板初始化完成回调
        @Override
        public void onTEBInit() {
            try {
                StudentLiveActivity activity = mActivityRef.get();
                if (activity != null) {
                    activity.addBoardView();
                }

            } catch (ArrayStoreException e) {
                Log.e("白板初始化完成回调", e.toString());
            }

        }

        //        白板历史数据同步完成回调
        @Override
        public void onTEBHistroyDataSyncCompleted() {
            StudentLiveActivity activityEx = mActivityRef.get();
            if (activityEx != null) {
                activityEx.onTEBHistroyDataSyncCompleted();
            }

        }

        @Override
        public void onTEBSyncData(String data) {

        }

        @Override
        public void onTEBImageStatusChanged(String boardId, String url, int status) {

        }

        @Override
        public void onTEBAddBoard(List<String> boardId, final String fileId) {
            TXLog.i("LiveBroadcastActivity", "onTEBAddBoard:" + fileId);
        }

        @Override
        public void onTEBDeleteBoard(List<String> boardId, final String fileId) {

        }

        @Override
        public void onTEBGotoBoard(String boardId, final String fileId) {

        }

        @Override
        public void onTEBGotoStep(int currentStep, int total) {

        }

        @Override
        public void onTEBRectSelected() {

        }

        @Override
        public void onTEBRefresh() {

        }

        @Override
        public void onTEBDeleteFile(String fileId) {
        }

        @Override
        public void onTEBSwitchFile(String fileId) {
        }

        @Override
        public void onTEBAddTranscodeFile(String s) {
        }

        //        白板可撤销状态改变回调
        @Override
        public void onTEBUndoStatusChanged(boolean canUndo) {
            StudentLiveActivity activityEx = mActivityRef.get();
            if (activityEx != null) {
                activityEx.mCanUndo = canUndo;
            }
        }

        //        白板可重做状态改变回调
        @Override
        public void onTEBRedoStatusChanged(boolean canredo) {
            StudentLiveActivity activityEx = mActivityRef.get();
            if (activityEx != null) {
                activityEx.mCanRedo = canredo;
            }
        }

        @Override
        public void onTEBFileUploadProgress(final String path, int currentBytes, int totalBytes, int uploadSpeed, float percent) {
            TXLog.i("LiveBroadcastActivity", "onTEBFileUploadProgress:" + path + " percent:" + percent);
        }

        @Override
        public void onTEBFileUploadStatus(final String path, int status, int code, String statusMsg) {
            TXLog.i("LiveBroadcastActivity", "onTEBFileUploadStatus:" + path + " status:" + status);
        }

        @Override
        public void onTEBFileTranscodeProgress(String s, String s1, String s2, TEduBoardController.TEduBoardTranscodeFileResult tEduBoardTranscodeFileResult) {

        }

        @Override
        public void onTEBH5FileStatusChanged(String fileId, int status) {

        }

        @Override
        public void onTEBAddImagesFile(String s) {

        }

        @Override
        public void onTEBVideoStatusChanged(String fileId, int status, float progress, float duration) {

        }

        @Override
        public void onTEBAudioStatusChanged(String s, int i, float v, float v1) {

        }

        @Override
        public void onTEBSnapshot(String s, int i, String s1) {
           Toast.makeText(mactivity,"截屏成功",Toast.LENGTH_SHORT).show();
            File file = new File(s);
            // 把file里面的图片插入到系统相册中
            String fileName = System.currentTimeMillis() + ".jpg";
            try {
                MediaStore.Images.Media.insertImage(mactivity.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 通知相册更新
            mactivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }

        @Override
        public void onTEBH5PPTStatusChanged(int i, String s, String s1) {

        }

        @Override
        public void onTEBSetBackgroundImage(final String url) {
            TXLog.i("LiveBroadcastActivity", "onTEBSetBackgroundImage:" + url);
        }

        @Override
        public void onTEBAddImageElement(final String url) {
            TXLog.i("LiveBroadcastActivity", "onTEBAddImageElement:" + url);
        }

        @Override
        public void onTEBAddElement(String s, String s1) {

        }

        @Override
        public void onTEBDeleteElement(List<String> list) {

        }

        @Override
        public void onTEBBackgroundH5StatusChanged(String boardId, String url, int status) {
            TXLog.i("LiveBroadcastActivity", "onTEBBackgroundH5StatusChanged:" + boardId + " url:" + boardId + " status:" + status);
        }
    }
    /*
     *添加白板视图
     */
    private void addBoardView() {
        View boardview = mBoard.getBoardRenderView();
        //        隐藏视频工具栏
        mBoard.showVideoControl(false);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        boardview.setBackgroundColor(getResources().getColor(R.color.livecolorBack));
        //postToast("正在使用白板：" + TEduBoardController.getVersion(), true);
        llBroadcast.addView(boardview, layoutParams);
    }
    /*
     *移除白板视图
     */
    private void removeBoardView() {
        if (mBoard != null) {
            View boardview = mBoard.getBoardRenderView();
            if (llBroadcast != null && boardview != null) {
                llBroadcast.removeView(boardview);
            }
            mBoard.uninit();
        }
    }
    /*
     *历史数据同步完成
     */
    private void onTEBHistroyDataSyncCompleted() {
        mHistroyDataSyncCompleted = true;
        Log.e("Board", "历史数据同步完成");
    }
    @Override
    protected void onStart() {
        Log.e("生命周期","StudentLiveActivity-onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e("生命周期","StudentLiveActivity-onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("生命周期","StudentLiveActivity-onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("生命周期","StudentLiveActivity-onStop");
        super.onStop();

    }
    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            DialogUtil.showDialog(this, "退出后本节课将会结束，确定要退出课堂吗？",
                    "确定",  "取消",false, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            quitOpenGroup();
                        }

                        @Override
                        public void clickNegative() {
                        }
                    });
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onDestroy() {
        Log.e("生命周期","StudentLiveActivity-onDestroy");
        if (!isquitClass) {
            quitOpenGroup();
        }
        super.onDestroy();

    }
    public void getOnLineStudents(){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/student/getOnlineStudents", new MyCallBack<BaseResponse<List<StudentInfor>>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<List<StudentInfor>> result) {
                if (result!=null&&result.getData()!=null){
                    Map<String, StudentInfor> onLineStudentsMap;
                    List<StudentInfor> list=result.getData();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        onLineStudentsMap = list.stream().collect(Collectors.toMap(StudentInfor::getUserCode, Function.identity(), (key1, key2) -> key2));
                    }else {
                        onLineStudentsMap= new HashMap<String, StudentInfor>();
                        for (StudentInfor studentInfor : list) {
                            onLineStudentsMap.put(studentInfor.getUserCode(), studentInfor);
                        }
                    }
                    liveDataManager.getOnLineStudentsMap().putAll(onLineStudentsMap);
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
    public interface VideoFragmentListener{
        void classmatelianMai(String classmatecode,String type);
    }
    public void setVideoFragmentListener(VideoFragmentListener videoFragmentListener) {
        this.mvideoFragmentListener = videoFragmentListener;
    }
    /**
     * 获取计时时间
     */
    public String timeCalculate(long time){
        long  daysuuu,hoursuuu, minutesuuu, secondsuuu;
        String restT = "";
        daysuuu = (Math.round(time) / 86400);
        hoursuuu = (Math.round(time) / 3600) - (daysuuu * 24);
        minutesuuu = (Math.round(time) / 60) - (daysuuu * 1440) - (hoursuuu * 60);
        secondsuuu = Math.round(time) % 60;
        restT = String.format("%02d:%02d:%02d", hoursuuu, minutesuuu, secondsuuu);
        return restT;
    }
}