package com.qm.qmclass.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.AddStudentAdpter;
import com.qm.qmclass.adpter.ChatContentAdpter;
import com.qm.qmclass.adpter.ColorAdpter;
import com.qm.qmclass.adpter.JushouAdpter;
import com.qm.qmclass.adpter.XzAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentInfor;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LivePopupWindow extends PopupWindow implements PopupWindow.OnDismissListener , GestureDetector.OnGestureListener, RefreshJianKongListener {
    private TeacherLiveActivity mactivity;
    private PopupWindowListener mpopupWindowListener;
    private ChangeStudentListener mchangeStudentListener;
    private CountDownTimer timer;
    private boolean muteChecked;
    private boolean forcemuteChecked;
    private ChatContentAdpter chatContentAdpter;
    private ColorAdpter colorAdpter;
    private XzAdpter xzAdpter;
    private JushouAdpter jushouAdpter;
    private LiveDataManager liveDataManager;
    private String[] xz = {"shifang","shituo","kongfang","kongtuo"};
    private LinearLayout jiankongpage;
    private RelativeLayout jk1;
    private RelativeLayout jk2;
    private RelativeLayout jk3;
    private RelativeLayout jk4;
    private JianaKongUtils jianaKongUtils;
    private GestureDetector gestureDetector;
    private int pageNum=1;
    private List<StudentInfor> pageList=null;

    public LivePopupWindow(TeacherLiveActivity activity) {
        mactivity=activity;
        liveDataManager=LiveDataManager.getInstance();
    }
   //    聊天
    public void showChatPopupWindow(View view,List<String> chatContentList){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_chat,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch danmu=(Switch)contentView.findViewById(R.id.danmu);
        final EditText messageInput=(EditText) contentView.findViewById(R.id.message_input);
        final ImageView jinyan=(ImageView)contentView.findViewById(R.id.jinyan);
        final LinearLayout chatTip=(LinearLayout)contentView.findViewById(R.id.chat_tip);
        final LinearLayout llChatinput=(LinearLayout)contentView.findViewById(R.id.ll_chatinput);
        final ListView chatlistView=(ListView)contentView.findViewById(R.id.chatlistView);
        // 使用ArrayAdapter适配器
        if (chatContentAdpter==null){
            chatContentAdpter = new ChatContentAdpter(mactivity, R.layout.chat_content_item, chatContentList);
        }
        chatlistView.setAdapter(chatContentAdpter);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        danmu.setChecked(liveDataManager.isOpenDanmu());
        if (liveDataManager.isOpenDanmu()){
            chatlistView.setVisibility(View.GONE);
            llChatinput.setVisibility(View.GONE);
            chatTip.setVisibility(View.VISIBLE);
        }else {
            chatlistView.setVisibility(View.VISIBLE);
            llChatinput.setVisibility(View.VISIBLE);
            chatTip.setVisibility(View.GONE);
        }
        if (liveDataManager.isJinYan()){
            jinyan.setImageDrawable(mactivity.getDrawable(R.mipmap.chatjinyan_red));
        }else {
            jinyan.setImageDrawable(mactivity.getDrawable(R.mipmap.chatjinyan));
        }
        danmu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                liveDataManager.setOpenDanmu(isChecked);
                mpopupWindowListener.showDanmu();
                if (isChecked){
                    chatlistView.setVisibility(View.GONE);
                    llChatinput.setVisibility(View.GONE);
                    chatTip.setVisibility(View.VISIBLE);
                }else {
                    chatlistView.setVisibility(View.VISIBLE);
                    llChatinput.setVisibility(View.VISIBLE);
                    chatTip.setVisibility(View.GONE);
                }
            }
        });
        messageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                mpopupWindowListener.chatSendOnclick(textView.getText().toString());
                messageInput.setText("");
                return true;
            }
        });
        jinyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liveDataManager.isJinYan()){
                    liveDataManager.setJinYan(true);
                    jinyan.setImageDrawable(mactivity.getDrawable(R.mipmap.chatjinyan_red));
                    mpopupWindowListener.chatJinyanOnclick();
                }else {
                    liveDataManager.setJinYan(false);
                    jinyan.setImageDrawable(mactivity.getDrawable(R.mipmap.chatjinyan));
                    mpopupWindowListener.chatJinyanOnclick();
                }
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    public void refreshChatContent(){
        if (chatContentAdpter!=null){
            chatContentAdpter.refresh();
        }
    }
    public void addChatContent(String data){
        if (chatContentAdpter!=null){
            chatContentAdpter.add(data);
        }
    }
    //弹幕
//    @SuppressLint("WrongConstant")
//    public void showDanmuPopupWindow(View view, List<ChatContent> chatContentList){
//        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_danmu,
//                null, false);
//        ListView danmulistView=(ListView)contentView.findViewById(R.id.danmulistView);
//        danmuAdpter=new DanmuAdpter(mactivity,chatContentList);
//        danmulistView.setAdapter(danmuAdpter);
//        setHeight(DensityUtil.dp2px(mactivity, 150));
//        setWidth(DensityUtil.dp2px(mactivity, 235));
//        setOutsideTouchable(false);
//        setFocusable(false);
//        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setContentView(contentView);
//        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
//                makeDropDownMeasureSpec(this.getHeight()));
//        PopupWindowCompat.showAsDropDown(this, view, 0, 0, Gravity.START);
//    }
    //设置
    public void showSetPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_set,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView courseId=(TextView)contentView.findViewById(R.id.courseId);
        courseId.setText(String.valueOf(DataManager.getInstance().getCourseId()));
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //打开文件
    public void showWenJianPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_wenjian,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        LinearLayout camera=(LinearLayout) contentView.findViewById(R.id.camera);
        LinearLayout album=(LinearLayout) contentView.findViewById(R.id.album);
        LinearLayout remote=(LinearLayout) contentView.findViewById(R.id.remote);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.wenJianOnclick("camera");
                dismiss();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.wenJianOnclick("album");
                dismiss();
            }
        });
        remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //举手学生列表
    public void showJuShouPopupWindow(View view,List list){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_jushou,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView jushounum=(TextView)contentView.findViewById(R.id.jushounum);
        jushounum.setText("("+list.size()+")");
        ListView jushoulistView=(ListView)contentView.findViewById(R.id.jushoulistView);
        jushouAdpter=new JushouAdpter(mactivity, list, new JushouAdpter.MyClickListener() {
            @Override
            public void myOnClick(int position, View v) {
                 if (v.getId()==R.id.maike){
                    mpopupWindowListener.juShouOnclick(position,"maike");
                }else if (v.getId()==R.id.tichu){
                    mpopupWindowListener.juShouOnclick(position,"tichu");
                }
            }
        });
        jushoulistView.setAdapter(jushouAdpter);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    public void refreshJuShou(List<String> list){
        jushouAdpter.refresh(list);
    }
//    静音
    public void showMutePopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_mute,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch mute=(Switch)contentView.findViewById(R.id.mute);
        Switch forcemute=(Switch)contentView.findViewById(R.id.forcemute);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mute.setChecked(muteChecked);
        forcemute.setChecked(forcemuteChecked);
        mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    muteChecked=isChecked;
                    mpopupWindowListener.mute(false,isChecked);
            }
        });
        forcemute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                forcemuteChecked=isChecked;
                    mpopupWindowListener.mute(true,isChecked);
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //课堂答疑
    public void showQuestionPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_question,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        LinearLayout answered=(LinearLayout) contentView.findViewById(R.id.answered);
        TextView tvAnswered=(TextView) contentView.findViewById(R.id.tv_answered);
        View viewAnswered=(View) contentView.findViewById(R.id.view_answered);
        LinearLayout unanswered=(LinearLayout) contentView.findViewById(R.id.unanswered);
        TextView tvUnanswered=(TextView) contentView.findViewById(R.id.tv_unanswered);
        View viewUnanswered=(View) contentView.findViewById(R.id.view_unanswered);
        tvAnswered.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
        tvUnanswered.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
        viewAnswered.setVisibility(View.VISIBLE);
        viewUnanswered.setVisibility(View.INVISIBLE);
        answered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAnswered.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                tvUnanswered.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewAnswered.setVisibility(View.VISIBLE);
                viewUnanswered.setVisibility(View.INVISIBLE);
            }
        });
        unanswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAnswered.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                tvUnanswered.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewAnswered.setVisibility(View.INVISIBLE);
                viewUnanswered.setVisibility(View.VISIBLE);
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //互动列表
    public void showhudongPopupWindow(View view){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_hudong,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        LinearLayout dianming=(LinearLayout)contentView.findViewById(R.id.dianming);
        LinearLayout dati=(LinearLayout)contentView.findViewById(R.id.dati);
        LinearLayout qiangda=(LinearLayout)contentView.findViewById(R.id.qiangda);
        LinearLayout jifen=(LinearLayout)contentView.findViewById(R.id.jifen);
        LinearLayout dingshi=(LinearLayout)contentView.findViewById(R.id.dingshi);
        LinearLayout choujiang=(LinearLayout)contentView.findViewById(R.id.choujiang);
        LinearLayout hongbao=(LinearLayout)contentView.findViewById(R.id.hongbao);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dianming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(1);
            }
        });
        dati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(2);
            }
        });
        qiangda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(3);
            }
        });
        jifen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(4);
            }
        });
        dingshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(5);
            }
        });
        choujiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(6);
            }
        });
        hongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.huDongOnclick(7);
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //下课
    public void showClassOverPW(final View view){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_classovertime,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch sClassover=(Switch)contentView.findViewById(R.id.s_classover);
        Switch s5=(Switch) contentView.findViewById(R.id.s_5);
        Switch s10=(Switch) contentView.findViewById(R.id.s_10);
        Switch s15=(Switch) contentView.findViewById(R.id.s_15);
        Switch s20=(Switch) contentView.findViewById(R.id.s_20);
        Switch s25=(Switch) contentView.findViewById(R.id.s_25);
        Switch s30=(Switch) contentView.findViewById(R.id.s_30);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sClassover.setChecked(false);
        s5.setChecked(false);
        s10.setChecked(false);
        s15.setChecked(false);
        s20.setChecked(false);
        s25.setChecked(false);
        s30.setChecked(false);
        sClassover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("0");
                }
            }
        });
        s5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("5");
                }
            }
        });
        s10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("10");
                }
            }
        });
        s15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("15");
                }
            }
        });
        s20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("20");
                }
            }
        });
        s25.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("25");
                }
            }
        });
        s30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mpopupWindowListener.classOverAfter("30");
                }
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
//    退出课堂
    public void showClassOverAtOnce(View view, final String time){
        String msg="";
        if (time.equals("0")){
            msg="退出后本节课将结束，确定要退出课堂吗？";
        }else{
            msg="确定要退出课堂吗，请记得"+time+"分钟后回来，否则本节课将会结束？";
        }
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.live_classover,
                null, false);
        TextView context=(TextView) contentView.findViewById(R.id.context);
        TextView cancel=(TextView) contentView.findViewById(R.id.cancel);
        TextView determine=(TextView) contentView.findViewById(R.id.determine);
        context.setText(msg);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.cancelClassOver(time);
                dismiss();
            }
        });
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.timeCountDown(time);
                dismiss();
            }
        });
        setHeight(DensityUtil.dp2px(mactivity, 150));
        setWidth(DensityUtil.dp2px(mactivity, 220));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
//倒计时
    public void showTimeCountDown(View view, String time){
        this.dismiss();
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.time_down,
                null, false);
        final TextView context=(TextView) contentView.findViewById(R.id.context);
        TextView back=(TextView) contentView.findViewById(R.id.back);
        timer=new CountDownTimer(Integer.parseInt(time)*60000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                context.setText(minute+":"+second);
            }

            @Override
            public void onFinish() {
                mpopupWindowListener.timeCountDown("true");
            }
        };
        timer.start();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                mpopupWindowListener.teacherBack();
                dismiss();
            }
        });
        setHeight(DensityUtil.dp2px(mactivity, 300));
        setWidth(DensityUtil.dp2px(mactivity, 300));
        setOutsideTouchable(false);
        setFocusable(false);
//        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    //选择白板工具
    public void showToolsPopupWindow(View view){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_tools,
                null, false);
        LinearLayout llbi=(LinearLayout) contentView.findViewById(R.id.ll_bi);
        LinearLayout llxingzhaung=(LinearLayout) contentView.findViewById(R.id.ll_xingzhaung);
        LinearLayout llwenzi=(LinearLayout) contentView.findViewById(R.id.ll_wenzi);
        LinearLayout llshubiao=(LinearLayout) contentView.findViewById(R.id.ll_shubiao);
        LinearLayout llxiangpi=(LinearLayout) contentView.findViewById(R.id.ll_xiangpi);
        LinearLayout llqingkong=(LinearLayout) contentView.findViewById(R.id.ll_qiangkong);
        ImageView bi=(ImageView) contentView.findViewById(R.id.bi);
        ImageView xingzhaung=(ImageView) contentView.findViewById(R.id.xingzhaung);
        ImageView wenzi=(ImageView) contentView.findViewById(R.id.wenzi);
        ImageView shubiao=(ImageView) contentView.findViewById(R.id.shubiao);
        ImageView xiangpi=(ImageView) contentView.findViewById(R.id.xiangpi);
        ImageView qingkong=(ImageView) contentView.findViewById(R.id.qingkong);
        if (liveDataManager.getWitchTools().equals("1")){
            bi.setBackgroundResource(R.drawable.tool_bg);
        }else if (liveDataManager.getWitchTools().equals("2")){
            xingzhaung.setBackgroundResource(R.drawable.tool_bg);
        }else if (liveDataManager.getWitchTools().equals("3")){
            wenzi.setBackgroundResource(R.drawable.tool_bg);
        }else if (liveDataManager.getWitchTools().equals("4")){
            shubiao.setBackgroundResource(R.drawable.tool_bg);
        }else if (liveDataManager.getWitchTools().equals("5")){
            xiangpi.setBackgroundResource(R.drawable.tool_bg);
        }
        llbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.setBackgroundResource(R.drawable.tool_bg);
                xingzhaung.setBackgroundResource(0);
                wenzi.setBackgroundResource(0);
                shubiao.setBackgroundResource(0);
                xiangpi.setBackgroundResource(0);
                qingkong.setBackgroundResource(0);
                mpopupWindowListener.toolItemOnclick("1");
                dismiss();
            }
        });
        llxingzhaung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.setBackgroundResource(0);
                xingzhaung.setBackgroundResource(R.drawable.tool_bg);
                wenzi.setBackgroundResource(0);
                shubiao.setBackgroundResource(0);
                xiangpi.setBackgroundResource(0);
                qingkong.setBackgroundResource(0);
                mpopupWindowListener.toolItemOnclick("2");
                dismiss();
            }
        });
        llwenzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.setBackgroundResource(0);
                xingzhaung.setBackgroundResource(0);
                wenzi.setBackgroundResource(R.drawable.tool_bg);
                shubiao.setBackgroundResource(0);
                xiangpi.setBackgroundResource(0);
                qingkong.setBackgroundResource(0);
                mpopupWindowListener.toolItemOnclick("3");
                dismiss();
            }
        });
        llshubiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.setBackgroundResource(0);
                xingzhaung.setBackgroundResource(0);
                wenzi.setBackgroundResource(0);
                shubiao.setBackgroundResource(R.drawable.tool_bg);
                xiangpi.setBackgroundResource(0);
                qingkong.setBackgroundResource(0);
                mpopupWindowListener.toolItemOnclick("4");
                dismiss();
            }
        });
        llxiangpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bi.setBackgroundResource(0);
                xingzhaung.setBackgroundResource(0);
                wenzi.setBackgroundResource(0);
                shubiao.setBackgroundResource(0);
                xiangpi.setBackgroundResource(R.drawable.tool_bg);
                qingkong.setBackgroundResource(0);
                mpopupWindowListener.toolItemOnclick("5");
                dismiss();
            }
        });
        llqingkong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.toolItemOnclick("6");
                dismiss();
            }
        });

        setHeight(DensityUtil.dp2px(mactivity, 100));
        setWidth(DensityUtil.dp2px(mactivity, 140));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = -(this.getContentView().getMeasuredWidth()-view.getWidth());
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+DensityUtil.dp2px(mactivity, 5));
        PopupWindowCompat.showAsDropDown(this, view, offsetX, offsetY, Gravity.START);
    }
    //选择画笔颜色
    public void showColorPopupWindow(View view,List<Integer> list){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_color,
                null, false);
        RelativeLayout penstyle=(RelativeLayout) contentView.findViewById(R.id.penstyle);
        //线的形状布局
        LinearLayout xian=(LinearLayout) contentView.findViewById(R.id.xian);
        ImageView quxian=(ImageView) contentView.findViewById(R.id.quxian);
        ImageView zhixian=(ImageView) contentView.findViewById(R.id.zhixian);
        //形状布局
        LinearLayout xingzhuang=(LinearLayout) contentView.findViewById(R.id.xingzhuang);
        //分割线
        View line=(View) contentView.findViewById(R.id.line);
        SeekBar seekBar=(SeekBar) contentView.findViewById(R.id.seekBar);
        TextView tvprogress=(TextView) contentView.findViewById(R.id.progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (liveDataManager.getWitchTools().equals("3")){
                    liveDataManager.setTextProgress(progress);
                }else if (liveDataManager.getWitchTools().equals("2")||liveDataManager.getWitchTools().equals("1")){
                    liveDataManager.setLineProgress(progress);
                }
                tvprogress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (liveDataManager.getWitchTools().equals("3")){
                    mpopupWindowListener.seekBarOnclick(liveDataManager.getTextProgress());
                }else if (liveDataManager.getWitchTools().equals("2")||liveDataManager.getWitchTools().equals("1")){
                    mpopupWindowListener.seekBarOnclick(liveDataManager.getLineProgress());
                }

            }
        });
        //颜色
        GridView gridView=(GridView) contentView.findViewById(R.id.gridView);
        colorAdpter = new ColorAdpter(mactivity, list);
        gridView.setAdapter(colorAdpter);
        //gridView的点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (liveDataManager.getWitchTools().equals("3")){
                    liveDataManager.setTextColor(position);
                }else if (liveDataManager.getWitchTools().equals("2")||liveDataManager.getWitchTools().equals("1")){
                    liveDataManager.setLineColor(position);
                }
                //把点击的position传递到adapter里面去
                colorAdpter.changeState(position);
                mpopupWindowListener.colorItemOnclick(position);
            }
        });
        //形状
        List<String> xzList = Arrays.asList(xz);
        GridView xzgridView=(GridView) contentView.findViewById(R.id.xzgridView);
        xzAdpter = new XzAdpter(mactivity, xzList);
        xzgridView.setAdapter(xzAdpter);
        //gridView的点击事件
        xzgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                liveDataManager.setXingzhuang(position);
                xzAdpter.changeState(position);
                mpopupWindowListener.xiangZhuangOnclick(position);
            }
        });
        quxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quxian.setBackgroundResource(R.drawable.tool_bg);
                zhixian.setBackgroundResource(0);
                liveDataManager.setXian(1);
                mpopupWindowListener.xianOnclick(1);
            }
        });
        zhixian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quxian.setBackgroundResource(0);
                zhixian.setBackgroundResource(R.drawable.tool_bg);
                liveDataManager.setXian(2);
                mpopupWindowListener.xianOnclick(2);
            }
        });

        setHeight(DensityUtil.dp2px(mactivity, 125));
        if (liveDataManager.getWitchTools().equals("2")){
            penstyle.setVisibility(View.VISIBLE);
            xian.setVisibility(View.GONE);
            xingzhuang.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            //        两列
            setWidth(DensityUtil.dp2px(mactivity, 200));

        }else if (liveDataManager.getWitchTools().equals("1")){
            penstyle.setVisibility(View.VISIBLE);
            xian.setVisibility(View.VISIBLE);
            xingzhuang.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);
            if (liveDataManager.getXian()==1){
                quxian.setBackgroundResource(R.drawable.tool_bg);
                zhixian.setBackgroundResource(0);
            }else if (liveDataManager.getXian()==2){
                quxian.setBackgroundResource(0);
                zhixian.setBackgroundResource(R.drawable.tool_bg);
            }
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            //        一列
            setWidth(DensityUtil.dp2px(mactivity, 175));
        }else if (liveDataManager.getWitchTools().equals("3")) {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tvprogress.setText(String.valueOf(liveDataManager.getTextProgress()));
            seekBar.setProgress(liveDataManager.getTextProgress());
            setWidth(DensityUtil.dp2px(mactivity, 130));
        }else {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            setWidth(DensityUtil.dp2px(mactivity, 130));
        }
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = -(this.getContentView().getMeasuredWidth()-view.getWidth());
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+DensityUtil.dp2px(mactivity, 5));
        PopupWindowCompat.showAsDropDown(this, view, offsetX, offsetY, Gravity.START);
    }
    //学生列表切换
    public void showStudentStatePopupWindow(View view,ChangeStudentListener changeStudentListener){
        mchangeStudentListener=changeStudentListener;
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_studentstate,
                null, false);
        TextView shnagke=(TextView) contentView.findViewById(R.id.shnagke);
        TextView weishangke=(TextView) contentView.findViewById(R.id.weishangke);
        shnagke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mchangeStudentListener.changeStudentList(0);
                dismiss();
            }
        });
        weishangke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mchangeStudentListener.changeStudentList(1);
                dismiss();
            }
        });
        setHeight(DensityUtil.dp2px(mactivity, 80));
        setWidth(DensityUtil.dp2px(mactivity, 140));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = Math.abs(this.getContentView().getMeasuredWidth()-view.getWidth()) / 2;
        PopupWindowCompat.showAsDropDown(this,view, offsetX, 0, Gravity.START);
    }

    //视频监控
    public void showJianKongPopupWindow(View view){
        View jiankongView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_jiankong,
                null, false);
        gestureDetector = new GestureDetector(this);
        jianaKongUtils=JianaKongUtils.getInstance(mactivity);
        jiankongpage=(LinearLayout) jiankongView.findViewById(R.id.jiankongpage);
        jk1=(RelativeLayout) jiankongView.findViewById(R.id.jk1);
        jk2=(RelativeLayout) jiankongView.findViewById(R.id.jk2);
        jk3=(RelativeLayout) jiankongView.findViewById(R.id.jk3);
        jk4=(RelativeLayout) jiankongView.findViewById(R.id.jk4);
        liveDataManager.getVideoListFragment().setRefreshJianKongListener(this);
        jianaKongUtils.setRefreshJianKongListener(this);
        showJianKong("show","");

        WindowManager windowManager = mactivity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(width-DensityUtil.dp2px(mactivity, 160));
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(jiankongView);
        this.setOnDismissListener(this);
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        this.showAtLocation(view, Gravity.LEFT, 0, 0);
    }

    @Override
    public void onDismiss() {
        pageNum=1;
        jianaKongUtils.destroyInstance();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX()-e2.getX() > 50 && Math.abs(velocityX) > 0) {
            if (pageNum==liveDataManager.getPageCount()){
                Toast.makeText(mactivity, "当前为最后一页", Toast.LENGTH_SHORT).show();
            }else {
                if (liveDataManager.getPageCount()>pageNum){
                    showAnimation();
                    pageNum=pageNum+1;
                    showJianKong("show","");
                }
            }
        } else if (e2.getX()-e1.getX() > 50 && Math.abs(velocityX) > 0) {
            if (pageNum>1){
                pageNum=pageNum-1;
                hidenAnimation();
                showJianKong("show","");
            }else {
                Toast.makeText(mactivity, "当前为第一页", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public void showJianKong(String state,String userCode){
        List<StudentInfor> allStudentsList = new ArrayList(liveDataManager.getAllStudentsMap().values());
        pageList=PageUtils.getPage(allStudentsList,pageNum,4);
        if (pageList!=null){
            for (int i=0;i<pageList.size();i++){
                if (state.equals("show")){
                    showJKItem(i);
                }else if (state.equals("Join")){
                    if (pageList.get(i).getUserCode().equals(userCode)){
                        showJKItem(i);
                    }
                }else if (state.equals("Exit")){
                    if (pageList.get(i).getUserCode().equals(userCode)){
                        showJKItem(i);
                    }
                }

            }
        }
    }

    @Override
    public void refreshSomeOne(String userCode) {
        if (pageList!=null){
            for (int i=0;i<pageList.size();i++){
                if (pageList.get(i).getUserCode().equals(userCode)){
                    showJKItem(i);
                }
            }
        }
    }

    private void showJKItem(int position){
        String userCode=pageList.get(position).getUserCode();
        View jkItemView=jianaKongUtils.createJKItemView(userCode);
        //找到里面需要动态改变的控件
        RelativeLayout jiankongItem= (RelativeLayout) jkItemView.findViewById(R.id.jiankong_item);
        RoundImageView jiankongIcon=(RoundImageView) jkItemView.findViewById(R.id.jiankong_icon);

        LinearLayout onlianmai = (LinearLayout) jkItemView.findViewById(R.id.onlianmai);

        TextView lmtips=(TextView) jkItemView.findViewById(R.id.lmtips);

        LinearLayout lianmaiTool = (LinearLayout) jkItemView.findViewById(R.id.lianmai_tools);

        LinearLayout laliuTool = (LinearLayout) jkItemView.findViewById(R.id.laliu_tool);
        LinearLayout shangketixing = (LinearLayout) jkItemView.findViewById(R.id.shangketixing);
        LinearLayout quxiaolm = (LinearLayout) jkItemView.findViewById(R.id.quxiaolm);

        if (position==0){
            jk1.removeAllViews();
            jk1.addView(jkItemView);
        }else if (position==1){
            jk2.removeAllViews();
            jk2.addView(jkItemView);
        }else if (position==2){
            jk3.removeAllViews();
            jk3.addView(jkItemView);
        }else if (position==3){
            jk4.removeAllViews();
            jk4.addView(jkItemView);
        }


        if (liveDataManager.getOnLineStudentsMap().get(userCode)==null){
            shangketixing.setVisibility(View.VISIBLE);
            lianmaiTool.setVisibility(View.GONE);
            laliuTool.setVisibility(View.GONE);
            lmtips.setVisibility(View.GONE);
            onlianmai.setVisibility(View.GONE);
            quxiaolm.setVisibility(View.GONE);
            jiankongIcon.setVisibility(View.VISIBLE);
            Glide.with(mactivity).load(liveDataManager.getAllStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(jiankongIcon);
        }else {
            shangketixing.setVisibility(View.GONE);
            if (!liveDataManager.getAllStudentsMap().isEmpty()&&liveDataManager.getAllStudentsMap().get(userCode)!=null){
                if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==1){
                    lmtips.setVisibility(View.VISIBLE);
                    lianmaiTool.setVisibility(View.VISIBLE);
                    laliuTool.setVisibility(View.GONE);
                    onlianmai.setVisibility(View.GONE);
                    quxiaolm.setVisibility(View.GONE);
                    if (liveDataManager.getAllStudentsMap().get(userCode).isCameraOn()){
                        jiankongItem.setVisibility(View.VISIBLE);
                        jiankongIcon.setVisibility(View.GONE);
                        TXCloudVideoView trtcView=liveDataManager.getTrtcViewmap().get(userCode);
                        if (trtcView!=null){
                            ViewGroup parent=(ViewGroup) trtcView.getParent();
                            if (parent!=null){
                                parent.removeAllViews();
                            }
                            jiankongItem.addView(trtcView);
                        }
                    }else {
                        jiankongItem.setVisibility(View.GONE);
                        jiankongIcon.setVisibility(View.VISIBLE);
                        Glide.with(mactivity).load(liveDataManager.getAllStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(jiankongIcon);
                    }

                }else if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==3){
                    lmtips.setVisibility(View.GONE);
                    lianmaiTool.setVisibility(View.GONE);
                    laliuTool.setVisibility(View.VISIBLE);
                    onlianmai.setVisibility(View.GONE);
                    quxiaolm.setVisibility(View.GONE);
                    if (liveDataManager.getAllStudentsMap().get(userCode).isCameraOn()){
                        jiankongItem.setVisibility(View.VISIBLE);
                        jiankongIcon.setVisibility(View.GONE);
                        jiankongItem.addView(jianaKongUtils.createVideoView(userCode));
                    }else {
                        jiankongItem.setVisibility(View.GONE);
                        jiankongIcon.setVisibility(View.VISIBLE);
                        Glide.with(mactivity).load(liveDataManager.getAllStudentsMap().get(userCode).getAvatarUrl()).skipMemoryCache(true).into(jiankongIcon);
                    }

                }else if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==2){
                    onlianmai.setVisibility(View.VISIBLE);
                    quxiaolm.setVisibility(View.VISIBLE);
                    shangketixing.setVisibility(View.GONE);
                    lianmaiTool.setVisibility(View.GONE);
                    laliuTool.setVisibility(View.GONE);
                    lmtips.setVisibility(View.GONE);
                    jiankongIcon.setVisibility(View.GONE);
                }
            }
        }

    }
    //连麦学生列表
    public void showAddSTPopupWindow(View view,List list){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_addstudent,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ListView addstlistView=(ListView)contentView.findViewById(R.id.addstlistView);
        AddStudentAdpter addStudentAdpter=new AddStudentAdpter(mactivity,list, new AddStudentAdpter.AddStudentClickListener() {
            @Override
            public void addStudentOnClick(String userCode, View v) {
                mpopupWindowListener.addStudentOnclick(userCode);
                dismiss();
            }
        });
        addstlistView.setAdapter(addStudentAdpter);

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //视频全屏
    public void showQPPopupWindow(View view,String userCode){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_qp,
                null, false);
        RelativeLayout quanpingVideoview=(RelativeLayout) contentView.findViewById(R.id.quanpingview);
        LinearLayout videoQuanping=(LinearLayout) contentView.findViewById(R.id.video_quanping);
        TXCloudVideoView trtcView=liveDataManager.getTrtcViewmap().get(userCode);
        if (trtcView!=null){
            ViewGroup trtcViewParent=(ViewGroup) trtcView.getParent();
            if (trtcViewParent!=null){
                trtcViewParent.removeAllViews();
            }
            quanpingVideoview.addView(trtcView);
        }
        videoQuanping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mactivity.sendGroupCustomMessage("cameraBack","",userCode);
                quanpingVideoview.removeAllViews();
                dismiss();
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.LEFT, 0, 0);
    }
    private void showAnimation() {
        TranslateAnimation show = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        show.setDuration(500);
        jiankongpage.startAnimation(show);
    }

    private void hidenAnimation() {
        TranslateAnimation hiden = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        hiden.setDuration(500);
        jiankongpage.startAnimation(hiden);
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }



    public interface PopupWindowListener {
        void chatSendOnclick(String data);
        void chatJinyanOnclick();
        void toolItemOnclick(String witch);
        void colorItemOnclick(int position);
        void xiangZhuangOnclick(int type);
        void seekBarOnclick(int progress);
        void xianOnclick(int type);
        void classOverAfter(String time);
        void teacherBack();
        void timeCountDown(String time);
        void cancelClassOver(String time);
        void mute(boolean isforce,boolean isChecked);
        void showDanmu();
        void juShouOnclick(int position,String action);
        void huDongOnclick(int position);
        void addStudentOnclick(String userCode);
        void wenJianOnclick(String type);

    }
    public void setPopupWindowListener(PopupWindowListener popupWindowListener) {
        this.mpopupWindowListener = popupWindowListener;
    }
    public static abstract class ChangeStudentListener{
        public abstract void changeStudentList(int state);
    }
}
