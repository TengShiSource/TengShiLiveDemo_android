package com.qm.qmclass.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.adpter.DanmuContentAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.fragment.StudentListFragment;
import com.qm.qmclass.fragment.VideoListFragment;
import com.qm.qmclass.model.ClassOver;
import com.qm.qmclass.model.Hudong;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.tencent.TICClassroomOption;
import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.utils.DialogUtil;
import com.qm.qmclass.utils.LivePopupWindow;
import com.qm.qmclass.utils.ToastUtil;
import com.tencent.imsdk.TIMMessage;
import com.tencent.rtmp.TXLog;
import com.tencent.teduboard.TEduBoardController;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;

import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_ERASER;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_LINE;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_OVAL;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_OVAL_SOLID;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_PEN;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_POINT_SELECT;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_RECT;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_RECT_SOLID;
import static com.tencent.teduboard.TEduBoardController.TEduBoardToolType.TEDU_BOARD_TOOL_TYPE_TEXT;

/*
*教师直播间
*/
public class TeacherLiveActivity extends AppCompatActivity implements View.OnClickListener,
        LivePopupWindow.PopupWindowListener,
        TICManager.TICMessageListener,
        TICManager.TICIMStatusListener {
    private ImageView ivSet;
    private ImageView ivChat;
    private ImageView ivJushou;
    private ImageView ivJinyan;
    private ImageView ivQuestion;
    private ImageView ivHudong;
    private ImageView ivClassover;
    private FrameLayout llBroadcast;
    private ImageView ivChehui;
    private ImageView ivJieping;
    private ImageView ivWenjian;
    private ImageView ivColor;
    private TextView tvText;
    private ImageView ivHuabi;
    private FrameLayout flFrament;
    private ImageView ivVideolist;
    private ImageView ivStudentlist;
    private ImageView jiankong;
    private RelativeLayout rlDanmu;
    private ListView danmulistView;
    private LinearLayout llDanmu;
    private EditText danmuInput;
    private ImageView ivFanye;
    private TextView tvYeshu;
    private ImageView ivJiaye;
    private LinearLayout tools;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private VideoListFragment videoListFragment;
    private StudentListFragment studentListFragment;
    private List<Integer> colorList=null;
    private List jushouList=null;
    private List<Hudong> huDongList;

    private TICManager mTicManager;
    static TEduBoardController mBoard = null;
    MyBoardCallback mBoardCallback;
    boolean mHistroyDataSyncCompleted = false;
    boolean mCanRedo = false;
    boolean mCanUndo = false;

    private DataManager dataManager;
    private LiveDataManager liveDataManager;

    private LivePopupWindow chatPopupWindow;
    private LivePopupWindow showJuShouPopupWindow;
    private LivePopupWindow mutePopupWindow;
    private LivePopupWindow setPopupWindow;
    private LivePopupWindow classOverPopupWindow;
    private LivePopupWindow toolsPopupWindow;
    private LivePopupWindow colorPopupWindow;
    private LivePopupWindow classOverAtOncePopupWindow;
    private LivePopupWindow huDongPopupWindow;
    private LivePopupWindow jiankongPopupWindow;
    private LivePopupWindow addSTPopupWindow;

    private static TeacherLiveActivity mactivity;
    private boolean isquitClass=false;
    private boolean jiankongisshow=false;
//    private VideoFragmentListener mvideoFragmentListener;
    private StudentlistFragmentListener mstudentlistFragmentListener;
    private DanmuContentAdpter danmuContentAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teacher_live);
        mactivity=this;

        mTicManager= QMSDK.getTICManager();
        //2.白板
        mBoard = mTicManager.getBoardController();

        dataManager= DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();

        initView();

        joinClass();

        getStudents();

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
        ivJinyan=(ImageView) findViewById(R.id.iv_jinyan);
        ivJinyan.setOnClickListener(this);
        ivQuestion=(ImageView) findViewById(R.id.iv_question);
        ivQuestion.setOnClickListener(this);
        ivHudong=(ImageView) findViewById(R.id.iv_hudong);
        ivHudong.setOnClickListener(this);
        ivClassover=(ImageView) findViewById(R.id.iv_classover);
        ivClassover.setOnClickListener(this);
        llBroadcast=(FrameLayout) findViewById(R.id.ll_broadcast);
        ivChehui=(ImageView) findViewById(R.id.iv_chehui);
        ivChehui.setOnClickListener(this);
        ivJieping=(ImageView) findViewById(R.id.iv_jieping);
        ivJieping.setOnClickListener(this);
        ivWenjian=(ImageView) findViewById(R.id.iv_wenjian);
        ivWenjian.setOnClickListener(this);
        ivColor=(ImageView) findViewById(R.id.iv_color);
        ivColor.setOnClickListener(this);
        tvText=(TextView) findViewById(R.id.tv_text);
        tvText.setOnClickListener(this);
        ivHuabi=(ImageView) findViewById(R.id.iv_huabi);
        ivHuabi.setOnClickListener(this);
        flFrament=(FrameLayout) findViewById(R.id.fl_frament);
        ivVideolist=(ImageView) findViewById(R.id.iv_videolist);
        ivVideolist.setOnClickListener(this);
        ivStudentlist=(ImageView) findViewById(R.id.iv_studentlist);
        ivStudentlist.setOnClickListener(this);
        jiankong=(ImageView) findViewById(R.id.jiankong);
        jiankong.setOnClickListener(this);
        llDanmu=(LinearLayout) findViewById(R.id.ll_danmu);
        rlDanmu=(RelativeLayout) findViewById(R.id.rl_danmu);
        danmulistView=(ListView)findViewById(R.id.danmulistView);
        danmuInput=(EditText) findViewById(R.id.danmu_input);
        danmuInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (textView.getText().toString().equals("")||textView.getText()==null){
                    ToastUtil.showToast1(TeacherLiveActivity.this,"","请输入内容");
                }else {
                    sendGroupCustomMessage("msg", dataManager.getUserName(), textView.getText().toString());
                    String chatContent = dataManager.getUserName() + ": " + textView.getText().toString();
                    danmuContentAdpter.add(chatContent);
                    danmuInput.setText("");
                }
                return true;
            }
        });
        ivFanye=(ImageView) findViewById(R.id.iv_fanye);
        ivFanye.setOnClickListener(this);
        tvYeshu=(TextView) findViewById(R.id.tv_yeshu);
        ivJiaye=(ImageView) findViewById(R.id.iv_jiaye);
        ivJiaye.setOnClickListener(this);
        tools=(LinearLayout) findViewById(R.id.tools);

        ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
        ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
        jiankong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        fragmentManager = this.getSupportFragmentManager();
        setFragmentSelection(0,"TRTC");
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
     * 进入课堂
     */
    private void joinClass() {
        llBroadcast.setVisibility(View.VISIBLE);

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
        mTicManager.joinClassroom(classroomOption, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                Map<String, String> map = new HashMap<>();
                map.put("username", dataManager.getUserName());
                map.put("avatar", dataManager.getUserIcon());
                String str = JSON.toJSONString(map);
                sendGroupCustomMessage("teacherJoin",dataManager.getUserCode(),str);
                Log.e("TeacherLiveActivity","加入课堂成功");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("TeacherLiveActivity","加入课堂失败"+ errMsg);
                ToastUtil.showToast1(mactivity, "", "加入课堂失败" + errCode + ":" + errMsg);
            }
        });

    }
    /*
     *退出课堂
     */
    private void quitClass() {
        //如果是老师，可以清除；
        //如查是学生一般是不要清除数据
        mTicManager.quitClassroom(true, new TICManager.TICCallback() {
            @Override
            public void onSuccess(Object data) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                liveDataManager.destroyInstance();
                removeAllFragment();
                if (mTicManager!=null){
                    mTicManager.removeIMMessageListener(TeacherLiveActivity.this);
                    mTicManager.removeIMStatusListener(TeacherLiveActivity.this);
                }
//                销毁白板
                removeBoardView();

                File file = new File(mactivity.getExternalFilesDir(null).getPath()+"jieping/");
                deleteAllFiles(file);
//                退出IM
                logout();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                isquitClass=false;
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
                isquitClass=true;
                Log.e("生命周期","TeacherLiveActivity-quitClass");
                classOver();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                isquitClass=false;
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_chat) {
            if (chatPopupWindow==null){
                chatPopupWindow=new LivePopupWindow(mactivity);
                chatPopupWindow.setPopupWindowListener(this);
            }
            ivChat.setImageDrawable(getDrawable(R.mipmap.liaotian));
            chatPopupWindow.showChatPopupWindow(view,liveDataManager.getChatContentList());
        } else if (view.getId() == R.id.iv_jushou) {
            jushouList=new ArrayList();
            jushouList.add(0, "张佳佳");
            jushouList.add(1, "张佳佳1");
            jushouList.add(2, "张佳佳2");
            jushouList.add(3, "张佳佳3");
            jushouList.add(4, "张佳佳4");
            jushouList.add(5, "张佳佳5");
            if (showJuShouPopupWindow==null){
                showJuShouPopupWindow=new LivePopupWindow(mactivity);
                showJuShouPopupWindow.setPopupWindowListener(this);
            }
            showJuShouPopupWindow.showJuShouPopupWindow(view,jushouList);
        } else if (view.getId() == R.id.iv_jinyan) {
            if (mutePopupWindow==null){
                mutePopupWindow=new LivePopupWindow(mactivity);
                mutePopupWindow.setPopupWindowListener(this);
            }
            mutePopupWindow.showMutePopupWindow(view);
        } else if (view.getId() == R.id.iv_question) {

        } else if (view.getId() == R.id.iv_hudong) {
            huDongList=new ArrayList<Hudong>();
            Hudong hudong0=new Hudong();
            hudong0.setName("点名");
            hudong0.setIconid(R.mipmap.dianming);
            Hudong hudong1=new Hudong();
            hudong1.setName("答题器");
            hudong1.setIconid(R.mipmap.dati);
            Hudong hudong2=new Hudong();
            hudong2.setName("抢答器");
            hudong2.setIconid(R.mipmap.qiangda);
            Hudong hudong3=new Hudong();
            hudong3.setName("积分器");
            hudong3.setIconid(R.mipmap.jifen);
            Hudong hudong4=new Hudong();
            hudong4.setName("定时器");
            hudong4.setIconid(R.mipmap.dingshi);
            Hudong hudong5=new Hudong();
            hudong5.setName("抽奖");
            hudong5.setIconid(R.mipmap.dianming);
            Hudong hudong6=new Hudong();
            hudong6.setName("红包");
            hudong6.setIconid(R.mipmap.hongbao);
            huDongList.add(hudong0);
            huDongList.add(hudong1);
            huDongList.add(hudong2);
            huDongList.add(hudong3);
            huDongList.add(hudong4);
            huDongList.add(hudong5);
            huDongList.add(hudong6);
            if (huDongPopupWindow==null){
                huDongPopupWindow=new LivePopupWindow(mactivity);
                huDongPopupWindow.setPopupWindowListener(this);
            }
            huDongPopupWindow.showhudongPopupWindow(view,huDongList);
        } else if (view.getId() == R.id.iv_set) {
            if (setPopupWindow==null){
                setPopupWindow=new LivePopupWindow(mactivity);
                setPopupWindow.setPopupWindowListener(this);
            }
            setPopupWindow.showSetPopupWindow(view);
        } else if (view.getId() == R.id.iv_classover) {
            if (classOverPopupWindow==null){
                classOverPopupWindow=new LivePopupWindow(mactivity);
                classOverPopupWindow.setPopupWindowListener(this);
            }
            classOverPopupWindow.showClassOverPW(view);
        } else if (view.getId() == R.id.iv_chehui) {
            mBoard.undo();
        } else if (view.getId() == R.id.iv_jieping) {
            TEduBoardController.TEduBoardSnapshotInfo tEduBoardSnapshotInfo=new TEduBoardController.TEduBoardSnapshotInfo();
            // 新建目录appDir，并把图片存到其下
            File appDir = new File(mactivity.getExternalFilesDir(null).getPath()+"jieping/");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            tEduBoardSnapshotInfo.path= mactivity.getExternalFilesDir(null).getPath()+"jieping/"+fileName;
            mBoard.snapshot(tEduBoardSnapshotInfo);

        } else if (view.getId() == R.id.iv_wenjian) {

        } else if (view.getId() == R.id.iv_color) {
            if (colorPopupWindow==null){
                colorPopupWindow=new LivePopupWindow(mactivity);
                colorPopupWindow.setPopupWindowListener(this);
            }
            colorPopupWindow.showColorPopupWindow(tools,colorList);
        } else if (view.getId() == R.id.tv_text) {
            if (colorPopupWindow==null){
                colorPopupWindow=new LivePopupWindow(mactivity);
                colorPopupWindow.setPopupWindowListener(this);
            }
            colorPopupWindow.showColorPopupWindow(tools,colorList);
        } else if (view.getId() == R.id.iv_huabi) {
            if (toolsPopupWindow==null){
                toolsPopupWindow=new LivePopupWindow(mactivity);
                toolsPopupWindow.setPopupWindowListener(this);
            }
            toolsPopupWindow.showToolsPopupWindow(tools);
        }  else if (view.getId() == R.id.iv_fanye) {
            if (mBoard!=null) {
                if (liveDataManager.getBoardNum() > 1) {
                    mBoard.prevBoard();
                    liveDataManager.setBoardNum(liveDataManager.getBoardNum() - 1);
                    tvYeshu.setText(liveDataManager.getBoardNum() + "/" + mBoard.getBoardList().size());
                    ivJiaye.setImageDrawable(getResources().getDrawable(R.mipmap.fanyeright));
                }
            }
        } else if (view.getId() == R.id.iv_jiaye) {
            if (mBoard!=null){
                if (liveDataManager.getBoardNum()==mBoard.getBoardList().size()){
                    mBoard.addBoard(null);
                    liveDataManager.setBoardNum(liveDataManager.getBoardNum()+1);
                    ivJiaye.setImageDrawable(getResources().getDrawable(R.mipmap.jiaye));
                    tvYeshu.setText(liveDataManager.getBoardNum()+"/"+mBoard.getBoardList().size());
                }else if (liveDataManager.getBoardNum()<mBoard.getBoardList().size()){
                    mBoard.nextBoard();
                    liveDataManager.setBoardNum(liveDataManager.getBoardNum()+1);
                    if (liveDataManager.getBoardNum()==mBoard.getBoardList().size()){
                        ivJiaye.setImageDrawable(getResources().getDrawable(R.mipmap.jiaye));
                    }else {
                        ivJiaye.setImageDrawable(getResources().getDrawable(R.mipmap.fanyeright));
                    }
                    tvYeshu.setText(liveDataManager.getBoardNum()+"/"+mBoard.getBoardList().size());
                }
            }
        }else if (view.getId() == R.id.iv_videolist) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist_lv));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
            jiankong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
            setFragmentSelection(0,"TRTC");
            if (jiankongPopupWindow!=null){
                if (jiankongisshow){
                    jiankongisshow=false;
                    jiankongPopupWindow.dismiss();
                }
            }
        } else if (view.getId() == R.id.iv_studentlist) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist_lv));
            jiankong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
            setFragmentSelection(1,"");
            if (jiankongPopupWindow!=null){
                if (jiankongisshow){
                    jiankongisshow=false;
                    jiankongPopupWindow.dismiss();
                }
            }

        } else if (view.getId() == R.id.jiankong) {
            ivVideolist.setImageDrawable(getResources().getDrawable(R.mipmap.videolist));
            ivStudentlist.setImageDrawable(getResources().getDrawable(R.mipmap.studentlist));
            jiankong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong_lv));
            setFragmentSelection(0,"JK");
            if (!jiankongisshow){
                jiankongisshow=true;
                if (jiankongPopupWindow==null){
                    jiankongPopupWindow=new LivePopupWindow(mactivity);
                    jiankongPopupWindow.setPopupWindowListener(this);
                }
                jiankongPopupWindow.showJianKongPopupWindow(view);
            }
        }
    }
    /*
     *切换fragment
     */
    private void setFragmentSelection(int index,String type) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                if (videoListFragment == null) {
                    videoListFragment = new VideoListFragment();
                    fragmentTransaction.add(R.id.fl_frament, videoListFragment);
                }
                //Activity传值，通过Bundle
                Bundle bundle = new Bundle();
                bundle.putString("videoState", type);
                //首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
                videoListFragment.setArguments(bundle);
                hideFragment(fragmentTransaction);
                fragmentTransaction.show(videoListFragment);
//                fragmentTransaction.replace(R.id.fl_frament, videoListFragment);
                break;
            case 1:
                if (studentListFragment == null) {
                    studentListFragment = new StudentListFragment();
                    fragmentTransaction.add(R.id.fl_frament, studentListFragment);
                }

                hideFragment(fragmentTransaction);
                fragmentTransaction.show(studentListFragment);
//                fragmentTransaction.replace(R.id.fl_frament, studentListFragment);
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
    /*
     *隐藏所有的fragment
     */
    private void hideFragment(FragmentTransaction transaction){
        if(videoListFragment != null){
            transaction.hide(videoListFragment);
        }
        if(studentListFragment != null){
            transaction.hide(studentListFragment);
        }
    }
    /*
     *移除所有的fragment
     */
    private void removeAllFragment(){
        if (videoListFragment!=null){
            fragmentTransaction.remove(videoListFragment);
            videoListFragment=null;
        }
        if (studentListFragment!=null){
            fragmentTransaction.remove(studentListFragment);
            studentListFragment=null;
        }
    }
    /*
     *点击聊天发送按钮
     */
    @Override
    public void chatSendOnclick(String data) {
        if (data.equals("")||data==null){
            ToastUtil.showToast1(this,"","请输入内容");
        }else {
            sendGroupCustomMessage("msg",dataManager.getUserName(),data);
            String chatContent=dataManager.getUserName()+": "+data;
            chatPopupWindow.addChatContent(chatContent);
        }
    }
    /*
     *点击聊天静音按钮
     */
    @Override
    public void chatJinyanOnclick() {
        if (liveDataManager.isJinYan()){
            sendGroupCustomMessage("talkDisable",dataManager.getUserCode(),"");
            Toast.makeText(this,"禁言",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"解除禁言",Toast.LENGTH_SHORT).show();
            sendGroupCustomMessage("talkEnable",dataManager.getUserCode(),"");
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
         LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)ivColor.getLayoutParams();
         lp.width= getResources().getDimensionPixelSize(R.dimen.dp_20);
         lp.height=getResources().getDimensionPixelSize(R.dimen.dp_20);
         ivColor.setLayoutParams(lp);
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
     *选择颜色
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
    /*
     *开启弹幕
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
        }else {
            llDanmu.setVisibility(View.GONE);
            rlDanmu.setVisibility(View.GONE);
//            if (chatPopupWindow!=null){
//                chatPopupWindow.refreshChatContent();
//            }
        }
    }
    /*
     *退出课堂提示
     */
    @Override
    public void classOverAfter(final String time) {
        if (classOverAtOncePopupWindow==null){
            classOverAtOncePopupWindow=new LivePopupWindow(mactivity);
            classOverAtOncePopupWindow.setPopupWindowListener(this);
        }
        classOverAtOncePopupWindow.showClassOverAtOnce(mactivity.getWindow().getDecorView(),time);
    }

    @Override
    public void teacherBack() {

    }
    /*
     *退出课堂倒计时
     */
    @Override
    public void timeCountDown(final String time) {
        if (time.equals("0")){
            classOverPopupWindow.dismiss();
            quitClass();
        }else {
            DialogUtil.showClassoverDialog(this, time,
                "回来",  false, new DialogUtil.AlertDialogBtnClickListener() {
                    @Override
                    public void clickPositive() {
                        cancelClassOver(time);
                        Toast.makeText(TeacherLiveActivity.this,"回来",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void clickNegative() {
                    }
                });
        }
    }
    /*
     *取消下课
     */
    @Override
    public void cancelClassOver(String time) {
        if (classOverPopupWindow!=null){
            if (time.equals("0")){
                Switch sClassover=(Switch)classOverPopupWindow.getContentView().findViewById(R.id.s_classover);
                sClassover.setChecked(false);
            }else if (time.equals("5")){
                Switch s5=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_5);
                s5.setChecked(false);
            }else if (time.equals("10")){
                Switch s10=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_10);
                s10.setChecked(false);
            }else if (time.equals("15")){
                Switch s15=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_15);
                s15.setChecked(false);
            }else if (time.equals("20")){
                Switch s20=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_20);
                s20.setChecked(false);
            }else if (time.equals("25")){
                Switch s25=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_25);
                s25.setChecked(false);
            }else if (time.equals("30")){
                Switch s30=(Switch) classOverPopupWindow.getContentView().findViewById(R.id.s_30);
                s30.setChecked(false);
            }

        }
    }
    /*
     *点击静音
     */
    @Override
    public void mute(boolean isforce, boolean isChecked) {
        if (!isforce){
            if (isChecked){
                Toast.makeText(TeacherLiveActivity.this,"开启静音",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(TeacherLiveActivity.this,"关闭静音",Toast.LENGTH_SHORT).show();
            }
        }else {
            if (isChecked){
                Toast.makeText(TeacherLiveActivity.this,"开启强制静音",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(TeacherLiveActivity.this,"关闭强制静音",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
     *点击举手列表
     */
    @Override
    public void juShouOnclick(int position, String action) {
        if (action.equals("huabi")){
            Toast.makeText(TeacherLiveActivity.this,"huabi"+position,Toast.LENGTH_SHORT).show();
        }else if (action.equals("maike")){
            Toast.makeText(TeacherLiveActivity.this,"maike"+position,Toast.LENGTH_SHORT).show();
        }else if (action.equals("tichu")){
            Toast.makeText(TeacherLiveActivity.this,"tichu"+position,Toast.LENGTH_SHORT).show();
        }
    }
    /*
     *点击互动列表
     */
    @Override
    public void huDongOnclick(int position) {
        Toast.makeText(TeacherLiveActivity.this,huDongList.get(position).getName(),Toast.LENGTH_SHORT).show();
    }

    /*
     *连麦学生
     */
    @Override
    public void addStudentOnclick(String userCode) {
        if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==3){
            //老师向学生发起连麦
            Map<String, String> map = new HashMap<>();
            map.put("action", "micOpen");
            String str = JSON.toJSONString(map);
            final byte msg[] = str.getBytes();
            sendCustomMessage(userCode,msg);
            liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(2);
            changeStudentVideoLMstate(userCode);
            changeStudentListLMstate(userCode,2);
        }
    }
    /*
     *显示连麦学生列表
     */
    public void showAddStudent(View view){
        List<StudentInfor> onLineStudentsList = new ArrayList(liveDataManager.getOnLineStudentsMap().values());
        for (int i=0;i<onLineStudentsList.size();i++){
            if (onLineStudentsList.get(i).getLianMaiState()!=3){
                onLineStudentsList.remove(i);
            }
        }
        if (addSTPopupWindow==null){
            addSTPopupWindow=new LivePopupWindow(this);
            addSTPopupWindow.setPopupWindowListener(this);
        }
        addSTPopupWindow.showAddSTPopupWindow(view,onLineStudentsList);
    }
    /*
     *更改学生列表连麦状态
     */
    public void changeStudentListLMstate(String userCode,int lianMaistate){
        if(mstudentlistFragmentListener!=null){
            mstudentlistFragmentListener.lianMai(userCode,lianMaistate);
        }
    }
    /*
     *发送单独数据
     */
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
                    ToastUtil.showToast1(TeacherLiveActivity.this, "", "发送单独IM" + errCode + ":" + errMsg);
                } else if (errCode == 9520) {
                    ToastUtil.showToast1(TeacherLiveActivity.this, "", "本地网络受限/不通，请检查网络！");
                }

            }
        });
    }
    @Override
    public void onTICRecvTextMessage(String fromUserId, String text) {
        Log.e("接收IM", "接收单独Text--IM"+text);
    }
    /*
     *接收单独IM数据
     */
    @Override
    public void onTICRecvCustomMessage(String fromUserId, byte[] data) {
        String str = new String(data);
        JSONObject jo = JSON.parseObject(str);
        if (jo.getString("action").equals("micOpenRequestResult")) {
//         老师请求与你连麦反馈
            if (jo.getString("result").equals("2")){
                ToastUtil.showToast1(this,"",liveDataManager.getOnLineStudentsMap().get(fromUserId).getNickName()+"拒绝了连麦请求");
                if (liveDataManager.getAllStudentsMap().get(fromUserId)!=null) {
                    liveDataManager.getAllStudentsMap().get(fromUserId).setLianMaiState(3);
                    //刷新学生列表
                    changeStudentListLMstate(fromUserId, 3);
                    //删除连麦学生
                    refuseLianMai(fromUserId);
                }
            }else if (jo.getString("result").equals("1")){
                changeStudentListLMstate(fromUserId, 1);
            }

        }else if (jo.getString("action").equals("micOpenRequest")){
//         老师处理学生的连麦请求
            DialogUtil.showDialog(this, jo.getString("nickName")+"请求连麦，是否同意？",
                    "同意",  "拒绝",false, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            Map<String, String> map = new HashMap<>();
                            map.put("action", "micOpenRequestResult");
                            map.put("result", "1");
                            String str = JSON.toJSONString(map);
                            final byte msg[] = str.getBytes();
                            sendCustomMessage(fromUserId,msg);
//                            刷新学生列表
                            changeStudentListLMstate(fromUserId, 1);
                        }

                        @Override
                        public void clickNegative() {
                            Map<String, String> map = new HashMap<>();
                            map.put("action", "micOpenRequestResult");
                            map.put("result", "2");
                            String str = JSON.toJSONString(map);
                            final byte msg[] = str.getBytes();
                            sendCustomMessage(fromUserId,msg);
                            if (liveDataManager.getAllStudentsMap().get(fromUserId)!=null){
                                liveDataManager.getAllStudentsMap().get(fromUserId).setLianMaiState(3);
                                changeStudentListLMstate(fromUserId,3);
                            }
                        }
                    });
        }else if (jo.getString("action").equals("studentPushOpen")){
         //学生开启推流
            if (liveDataManager.getAllStudentsMap().get(fromUserId)!=null){
                liveDataManager.getAllStudentsMap().get(fromUserId).setCameraOn(true);
                if (jiankongPopupWindow!=null){
                    jiankongPopupWindow.refreshSomeOne(fromUserId);
                }
            }
        }else if (jo.getString("action").equals("studentPushClose")){
            //学生关闭推流
            if (liveDataManager.getAllStudentsMap().get(fromUserId)!=null){
                liveDataManager.getAllStudentsMap().get(fromUserId).setCameraOn(false);
                if (jiankongPopupWindow!=null){
                    jiankongPopupWindow.refreshSomeOne(fromUserId);
                }
            }
        }
    }
    /*
     *发送群组IM数据
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
                    ToastUtil.showToast1(TeacherLiveActivity.this, "", "发送群组IM失败" + errCode + ":" + errMsg);
                } else if (errCode == 9520) {
                    ToastUtil.showToast1(TeacherLiveActivity.this, "", "本地网络受限/不通，请检查网络！");
                }
            }
        });
        if (action.equals("cameraBack")){
            if (videoListFragment!=null){
                videoListFragment.cameraBack(info);
            }
        }
    }


    /*
     *接收群组GroupTextIM数据
     */
    @Override
    public void onTICRecvGroupTextMessage(String fromUserId, String text) {
        Log.e("接收IM", "接收群组GroupText--IM"+text);
//        String str = new String(data);
        JSONObject jo = JSON.parseObject(text);
      if (jo.getString("action").equals("studentExit")) {
            String exitStudent=jo.getString("studentIds").toString();
            List<String> list = Arrays.asList(exitStudent.split(","));
            HashMap<String, StudentInfor> exitStudentsMap = new HashMap<>();
            for (int i=0;i<list.size();i++){
                StudentInfor exitStudentInfor=liveDataManager.getOnLineStudentsMap().get(list.get(i));
                exitStudentsMap.put(list.get(i),exitStudentInfor);
                liveDataManager.getOnLineStudentsMap().remove(list.get(i));
                // 更新监控
                if (jiankongPopupWindow!=null){
                    jiankongPopupWindow.showJianKong("Exit",list.get(i));
                }
            }
            liveDataManager.getOffLineStudentsMap().putAll(exitStudentsMap);
            if (studentListFragment!=null){
                if (liveDataManager.getTeacher_StudentListState()==0){
                    studentListFragment.showShangKeList();
                }else if (liveDataManager.getTeacher_StudentListState()==1){
                    studentListFragment.showWeiShangKeList();
                }
            }

        }
    }
    /*
     *接收群组IM数据
     */
    @Override
    public void onTICRecvGroupCustomMessage(String fromUserId, byte[] data) {
        Log.e("接收IM", "接收群组GroupCustom--IM");
        String str = new String(data);
        JSONObject jo = JSON.parseObject(str);
        if (jo.getString("action").equals("msg")) {
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
        }else if (jo.getString("action").equals("studentVideoClose")){
            //学生关闭摄像头
            liveDataManager.getAllStudentsMap().get(fromUserId).setCameraOn(false);
            if (videoListFragment!=null) {
                videoListFragment.studentCameraState(fromUserId);
            }
        }else if (jo.getString("action").equals("studentVideoOpen")){
           //学生开启摄像头
            liveDataManager.getAllStudentsMap().get(fromUserId).setCameraOn(true);
            if (videoListFragment!=null) {
                videoListFragment.studentCameraState(fromUserId);
            }

        }else if (jo.getString("action").equals("studentJoin")) {
            String info=jo.getString("info");
            StudentInfor studentInfor = JSONObject.parseObject(info,StudentInfor.class);
            liveDataManager.getOnLineStudentsMap().put(fromUserId,studentInfor);
            liveDataManager.getAllStudentsMap().put(fromUserId,studentInfor);
            liveDataManager.getOffLineStudentsMap().remove(fromUserId);
            // 更新监控
            if (jiankongPopupWindow!=null){
                jiankongPopupWindow.showJianKong("Join",fromUserId);
            }
            if (studentListFragment!=null){
                if (liveDataManager.getTeacher_StudentListState()==0){
                    studentListFragment.showShangKeList();
                }else if (liveDataManager.getTeacher_StudentListState()==1){
                    studentListFragment.showWeiShangKeList();
                }
            }


        }
    }

    @Override
    public void onTICRecvMessage(TIMMessage message) {
        Log.e("接收IM", "接收单独message--IM"+message);
    }
    /*
     *被挤下线
     */
    @Override
    public void onTICForceOffline() {
        ToastUtil.showToast1(TeacherLiveActivity.this, "", "被踢下线");
    }

    @Override
    public void onTICUserSigExpired() {

    }
    //添加连麦学生
    public void changeStudentVideoLMstate(String userCode){
        if (videoListFragment!=null){
            videoListFragment.addStudentItem(userCode);
        }
    }
    //学生或老师拒绝连麦
    public void refuseLianMai(String userCode){
        if (videoListFragment!=null){
            videoListFragment.refuseLianMai(userCode);
        }
    }
    /*
     *白板回调
     */
    private static class MyBoardCallback implements TEduBoardController.TEduBoardCallback {
        WeakReference<TeacherLiveActivity> mActivityRef;

        MyBoardCallback(TeacherLiveActivity activityEx) {
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
                TeacherLiveActivity activity = mActivityRef.get();
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
            TeacherLiveActivity activityEx = mActivityRef.get();
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
            TeacherLiveActivity activityEx = mActivityRef.get();
            if (activityEx != null) {
                activityEx.mCanUndo = canUndo;
            }
        }

        //        白板可重做状态改变回调
        @Override
        public void onTEBRedoStatusChanged(boolean canredo) {
            TeacherLiveActivity activityEx = mActivityRef.get();
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
            ToastUtil.showToast1(mactivity,"","截屏成功");
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
        mBoard.setDrawEnable(true);
        mBoard.setToolType(TEDU_BOARD_TOOL_TYPE_PEN);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        boardview.setBackgroundColor(getResources().getColor(R.color.livecolorBack));
        //postToast("正在使用白板：" + TEduBoardController.getVersion(), true);
        llBroadcast.addView(boardview, layoutParams);
    }
    /*
     *销毁白板视图和白板
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
     *白板历史数据同步
     */
    private void onTEBHistroyDataSyncCompleted() {
        mHistroyDataSyncCompleted = true;
        tvYeshu.setText("1/"+mBoard.getBoardList().size());
        startPushBoardStream();
        Log.e("Board", "历史数据同步完成");
    }
    /*
     *启动白板推流
     */
    private void startPushBoardStream(){
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/lvbcourse/startPushBoardStream","",new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result.getCode()==200) {
                    Log.e("Board", "启动白板推流成功");
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
    /*
     *关闭课堂
     */
    private void classOver(){
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/lvbcourse/classOver","",new MyCallBack<BaseResponse<ClassOver>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<ClassOver> result) {
                finish();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                finish();
            }

            @Override
            public void onError(Response response) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        Log.e("生命周期","TeacherLiveActivity-onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e("生命周期","TeacherLiveActivity-onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("生命周期","TeacherLiveActivity-onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("生命周期","TeacherLiveActivity-onStop");
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
            DialogUtil.showDialog(this, "退出后本节课将结束，确定要退出课堂吗？",
                    "确定",  "取消",false, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            quitClass();
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
        Log.e("生命周期","TeacherLiveActivity-onDestroy");
        if (!isquitClass){
           quitClass();
        }
        super.onDestroy();

    }
    public void getStudents(){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/member/getStudents", new MyCallBack<BaseResponse<List<StudentInfor>>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<List<StudentInfor>> result) {
                if (result!=null&&result.getData()!=null){
                    Map<String, StudentInfor> allStudentsMap;
                    List<StudentInfor> list=result.getData();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        allStudentsMap = list.stream().collect(Collectors.toMap(StudentInfor::getUserCode, Function.identity(), (key1, key2) -> key2));
                    }else {
                        allStudentsMap= new HashMap<String, StudentInfor>();
                        for (StudentInfor studentInfor : list) {
                            allStudentsMap.put(studentInfor.getUserCode(), studentInfor);
                        }
                    }
                    liveDataManager.getAllStudentsMap().putAll(allStudentsMap);
                }
                getOnLineStudents();
            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
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
                        List<StudentInfor> onLineStudentsList = new ArrayList(liveDataManager.getOnLineStudentsMap().values());
                        List<StudentInfor> allStudentsList = new ArrayList(liveDataManager.getAllStudentsMap().values());
                        List<StudentInfor> offLineStudentsList= getOffLineStudentsList(allStudentsList,onLineStudentsList);
                        Map<String, StudentInfor> offLineStudentsMap;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            offLineStudentsMap = offLineStudentsList.stream().collect(Collectors.toMap(StudentInfor::getUserCode, Function.identity(), (key1, key2) -> key2));
                        }else {
                            offLineStudentsMap= new HashMap<String, StudentInfor>();
                            for (StudentInfor studentInfor : list) {
                                offLineStudentsMap.put(studentInfor.getUserCode(), studentInfor);
                            }
                        }
                        liveDataManager.getOffLineStudentsMap().putAll(offLineStudentsMap);
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
    /**
     *　　这是目前我了解到速度最快的一种
     */
    @SuppressWarnings("unchecked")
    public static List<StudentInfor> getOffLineStudentsList(List<StudentInfor> allStudentsList,List<StudentInfor> onLineStudentsList){
        HashSet allStudents = new HashSet(allStudentsList);
        HashSet onLineStudents = new HashSet(onLineStudentsList);
        allStudents.removeAll(onLineStudents);
        List<StudentInfor> offLineStudentsList=new ArrayList<>();
        offLineStudentsList.addAll(allStudents);
        return offLineStudentsList;
    }

//    public interface VideoFragmentListener{
//      void lianMai(String userCode,boolean isLianmai);
//    }
//    public void setVideoFragmentListener(VideoFragmentListener videoFragmentListener) {
//        this.mvideoFragmentListener = videoFragmentListener;
//    }
    public interface StudentlistFragmentListener{
        void lianMai(String userCode,int lianMaistate);
    }
    public void setStudentlistFragmentListener(StudentlistFragmentListener studentlistFragmentListener) {
        this.mstudentlistFragmentListener = studentlistFragmentListener;
    }
    /**
     * 清除目录下文件
     *
     */
    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}