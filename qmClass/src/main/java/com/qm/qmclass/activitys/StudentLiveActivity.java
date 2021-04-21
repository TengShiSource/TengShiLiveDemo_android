package com.qm.qmclass.activitys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.adpter.DanmuAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.fragment.OnLineStudentListFragment;
import com.qm.qmclass.fragment.StudentListFragment;
import com.qm.qmclass.fragment.StudentVideoListFragment;
import com.qm.qmclass.fragment.TimeSDVideoListFragment;
import com.qm.qmclass.fragment.VideoListFragment;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.Hudong;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.qmmanager.QMClassManagerImpl;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.rtmp.TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_PEN;

public class StudentLiveActivity extends AppCompatActivity implements View.OnClickListener,
        StudentLivePopupWindow.PopupWindowListener,
        TICManager.TICMessageListener,
        TICManager.TICIMStatusListener {
    private ImageView ivSet;
    private ImageView ivChat;
    private ImageView ivJushou;
    private ImageView ivQuestion;
    private ImageView ivClassover;
    private FrameLayout llBroadcast;
    private ImageView ivChehui;
    private ImageView ivColor;
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
    private StudentLivePopupWindow danmuPopupWindow;
    private StudentLivePopupWindow setPopupWindow;
    private StudentLivePopupWindow classOverPopupWindow;
    private StudentLivePopupWindow colorPopupWindow;

    private static Activity mactivity;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    private VideoFragmentListener mvideoFragmentListener;
    private DanmuAdpter danmuAdpter;

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
                sendGroupCustomMessage("msg",dataManager.getUserName(),textView.getText().toString());
                ChatContent chatContent=new ChatContent();
                chatContent.setChatName(dataManager.getUserName());
                chatContent.setChatContent(textView.getText().toString());
                liveDataManager.getChatContentList().add(chatContent);
                danmuAdpter.refresh(liveDataManager.getChatContentList());
                danmuInput.setText("");
                return true;
            }
        });
        tools=(LinearLayout) findViewById(R.id.tools);

        ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
        ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
        fragmentManager = this.getSupportFragmentManager();

        //学生端进入直播间是拉流观看
        rlVideoView.setVisibility(View.VISIBLE);
        llBroadcast.setVisibility(View.GONE);
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
        initParam.brushColor = new TEduBoardController.TEduBoardColor(0, 255, 0, 255);
        initParam.smoothLevel = 0; //用于指定笔迹平滑级别，默认值0.1，取值[0, 1]
        TICClassroomOption classroomOption = new TICClassroomOption();
        classroomOption.classId = dataManager.getCourseId();
        classroomOption.boardCallback = mBoardCallback;
        classroomOption.boardInitPara = initParam;
        mTicManager.initBoardAndTRTC(classroomOption, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("TeacherLiveActivity","切换到白板成功");
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
        mTicManager.uninitBoardAndTRTC(false);
        removeBoardView();
        joinOpenClass(false,false);
    }
    /*
     *退出群组
     */
    private void quitOpenGroup() {
        mTicManager.quitGroup(dataManager.getCourseId(), new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                if (mBoard!=null){
                    removeBoardView();
                }
                if (mLivePlayer!=null){
                    mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
                    videoView.onDestroy();
                }
                mTicManager.removeIMMessageListener(StudentLiveActivity.this);
                mTicManager.removeIMStatusListener(StudentLiveActivity.this);
                logout();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                finish();
            }
        });
    }
    /*
     *退出IM
     */
    private void logout(){
        mTicManager.logout(new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Log.e("生命周期","StudentLiveActivity-logout");
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                finish();
            }
        });
    }
    /*
     *切换fragment
     */
    private void setFragmentSelection(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
        if(onLineStudentListFragment != null){
            transaction.hide(onLineStudentListFragment);
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_chat) {
            if (chatPopupWindow==null){
                chatPopupWindow=new StudentLivePopupWindow(mactivity);
                chatPopupWindow.setPopupWindowListener(this);
            }
            chatPopupWindow.showChatPopupWindow(view,liveDataManager.getChatContentList());
        } else if (view.getId() == R.id.iv_jushou) {

        } else if (view.getId() == R.id.iv_question) {

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
            colorList=new ArrayList();
            colorList.add(0, "红");
            colorList.add(1, "白");
            colorList.add(2, "黄");
            colorList.add(3, "蓝");
            colorList.add(4, "绿");
            colorList.add(5, "紫");
            if (colorPopupWindow==null){
                colorPopupWindow=new StudentLivePopupWindow(mactivity);
                colorPopupWindow.setPopupWindowListener(this);
            }
            colorPopupWindow.showColorPopupWindow(ivColor,colorList);
        } else if (view.getId() == R.id.iv_huabi) {

        }else if (view.getId() == R.id.iv_videolist) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
            setFragmentSelection(0);
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
        Toast.makeText(this,data,Toast.LENGTH_SHORT).show();
        sendGroupCustomMessage("msg",dataManager.getUserName(),data);
        ChatContent chatContent=new ChatContent();
        chatContent.setChatName(dataManager.getUserName());
        chatContent.setChatContent(data);
        liveDataManager.getChatContentList().add(chatContent);
        chatPopupWindow.refreshChatContent( liveDataManager.getChatContentList());
    }

    /*
     *显示弹幕
     */
    @Override
    public void showDanmu() {
        if (liveDataManager.isOpenDanmu()){
            llDanmu.setVisibility(View.VISIBLE);
            rlDanmu.setVisibility(View.VISIBLE);
            danmuAdpter=new DanmuAdpter(this,liveDataManager.getChatContentList());
            danmulistView.setAdapter(danmuAdpter);

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
            if (chatPopupWindow!=null){
                chatPopupWindow.refreshChatContent(liveDataManager.getChatContentList());
            }
        }
    }
    /*
     *选择画笔颜色
     */
    @Override
    public void colorItemOnclick(int position) {
        if (colorList.get(position).equals("红")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 0, 0, 255));
        }else if (colorList.get(position).equals("白")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 255, 255, 255));
        }else if (colorList.get(position).equals("黄")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(255, 165, 0, 255));
        }else if (colorList.get(position).equals("蓝")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(0, 0, 255, 255));
        }else if (colorList.get(position).equals("绿")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(0, 255, 0, 255));
        }else if (colorList.get(position).equals("紫")){
            mBoard.setBrushColor(new TEduBoardController.TEduBoardColor(138, 43, 226, 255));
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
//                mvideoFragmentListener.lianMai(true);
            }else if (jo.getString("result").equals("2")){
//                mvideoFragmentListener.lianMai(false);
                ToastUtil.showToast1(StudentLiveActivity.this, "", "XXX拒绝了连麦请求");
            }
        }else if (jo.getString("action").equals("kickOut")){
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
                mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_PEN);
            }
        }
    }
    /*
     *发送群组IM
     */
    private void sendGroupCustomMessage(String action, String sender, String info) {
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
        if (jo.getString("action").equals("studentJoin")) {
            List<StudentInfor> list = JSON.parseArray(jo.getString("students"),StudentInfor.class);
            Map<String, StudentInfor> joinStudentsMap;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                joinStudentsMap = list.stream().collect(Collectors.toMap(StudentInfor::getUserCode, Function.identity(), (key1, key2) -> key2));
            }else {
                joinStudentsMap= new HashMap<String, StudentInfor>();
                for (StudentInfor studentInfor : list) {
                    joinStudentsMap.put(studentInfor.getUserCode(), studentInfor);
                }
            }
            joinStudentsMap.remove(dataManager.getUserCode());
            liveDataManager.getOnLineStudentsMap().putAll(joinStudentsMap);
            if (onLineStudentListFragment!=null){
                onLineStudentListFragment.showShangKeList();
            }

        } else if (jo.getString("action").equals("studentExit")) {
            String exitStudent=jo.getString("studentIds").toString();
            List<String> list = Arrays.asList(exitStudent.split(","));
            for (int i=0;i<list.size();i++){
                liveDataManager.getOnLineStudentsMap().remove(list.get(i));
            }
            if (onLineStudentListFragment!=null){
                onLineStudentListFragment.showShangKeList();
            }
        }else if (jo.getString("action").equals("classOver")){
            quitOpenGroup();
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
            ChatContent chatContent=new ChatContent();
            chatContent.setChatName(jo.getString("sender"));
            chatContent.setChatContent(jo.getString("info"));
            liveDataManager.getChatContentList().add(chatContent);
            if (liveDataManager.isOpenDanmu()){
                if (danmuPopupWindow!=null){
                    danmuPopupWindow.refreshChatContent(liveDataManager.getChatContentList());
                }
            }else {
                if(chatPopupWindow!=null){
                    chatPopupWindow.refreshChatContent(liveDataManager.getChatContentList());
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
                if (chatPopupWindow!=null) {
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
                    messageInput.setBackground(getResources().getDrawable(R.drawable.bg_danmu_edit));
                }
            }
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
    @Override
    protected void onDestroy() {
        Log.e("生命周期","StudentLiveActivity-onDestroy");
        if (studentVideoListFragment!=null){
            studentVideoListFragment=null;
        }
        if (onLineStudentListFragment!=null){
            onLineStudentListFragment=null;
        }
        liveDataManager.destroyInstance();
        quitOpenGroup();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
}