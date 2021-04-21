package com.qm.qmclass.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
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

import com.qm.qmclass.R;
import com.qm.qmclass.adpter.ChatAdpter;
import com.qm.qmclass.adpter.ColorAdpter;
import com.qm.qmclass.adpter.DanmuAdpter;
import com.qm.qmclass.adpter.HudongAdpter;
import com.qm.qmclass.adpter.JushouAdpter;
import com.qm.qmclass.adpter.XzAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.ChatContent;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LivePopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {
    private Activity mactivity;
    private PopupWindowListener mpopupWindowListener;
    private ChangeStudentListener mchangeStudentListener;
    private CountDownTimer timer;
    private boolean muteChecked;
    private boolean forcemuteChecked;
    private ChatAdpter chatAdpter;
    private ColorAdpter colorAdpter;
    private XzAdpter xzAdpter;
    private LiveDataManager liveDataManager;
    private String[] xz = {"shifang","shituo","kongfang","kongtuo"};

    public LivePopupWindow(Activity activity) {
        mactivity=activity;
        liveDataManager=LiveDataManager.getInstance();
    }
   //    聊天
    public void showChatPopupWindow(View view,List<ChatContent> chatContentList){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_chat,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch danmu=(Switch)contentView.findViewById(R.id.danmu);
        final EditText messageInput=(EditText) contentView.findViewById(R.id.message_input);
        final ImageView jinyan=(ImageView)contentView.findViewById(R.id.jinyan);
        final LinearLayout chatTip=(LinearLayout)contentView.findViewById(R.id.chat_tip);
        final LinearLayout llChatinput=(LinearLayout)contentView.findViewById(R.id.ll_chatinput);
        final ListView chatlistView=(ListView)contentView.findViewById(R.id.chatlistView);
        chatAdpter=new ChatAdpter(mactivity,chatContentList);
        chatlistView.setAdapter(chatAdpter);
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
                    chatAdpter.refresh(liveDataManager.getChatContentList());
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
    public void refreshChatContent(List<ChatContent> list){
        if (chatAdpter!=null){
            chatAdpter.refresh(list);
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
        JushouAdpter jushouAdpter=new JushouAdpter(mactivity, list, new JushouAdpter.MyClickListener() {
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
    //互动列表
    public void showhudongPopupWindow(View view,List list){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_hudong,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        RecyclerView hudonglistView=(RecyclerView)contentView.findViewById(R.id.hudonglistView);
        GridLayoutManager manager = new GridLayoutManager(mactivity, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        hudonglistView.setLayoutManager(manager);
        HudongAdpter hudongAdpter=new HudongAdpter(mactivity, list, new HudongAdpter.MyClickListener() {
            @Override
            public void myOnClick(int position) {
                mpopupWindowListener.huDongOnclick(position);
            }
        });
        hudonglistView.setAdapter(hudongAdpter);
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
        int offsetX = -Math.abs(this.getContentView().getMeasuredWidth()-view.getWidth());
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+DensityUtil.dp2px(mactivity, 15));
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
                if (xzAdpter!=null){
                    xzAdpter.changeState(liveDataManager.getXingzhuang());
                }
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
            seekBar.setMax(120);
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
            seekBar.setMax(120);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            //        一列
            setWidth(DensityUtil.dp2px(mactivity, 175));
        }else if (liveDataManager.getWitchTools().equals("3")) {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekBar.setMin(20);
            }
            seekBar.setMax(500);
            tvprogress.setText(String.valueOf(liveDataManager.getTextProgress()));
            seekBar.setProgress(liveDataManager.getTextProgress());
            setWidth(DensityUtil.dp2px(mactivity, 130));
        }else {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setMax(120);
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
    private TXLivePlayer mLivePlayer1;
    private TXLivePlayer mLivePlayer2;
    private TXLivePlayer mLivePlayer3;
    private TXLivePlayer mLivePlayer4;
    private TXCloudVideoView video1;
    private TXCloudVideoView video2;
    private TXCloudVideoView video3;
    private TXCloudVideoView video4;
    //视频监控
    public void showJianKongPopupWindow(View view,List list){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_jiankong,
                null, false);

        LinearLayout line1=(LinearLayout) contentView.findViewById(R.id.line1);
        video1=(TXCloudVideoView) contentView.findViewById(R.id.video1);
        TextView name1=(TextView) contentView.findViewById(R.id.name1);
        LinearLayout maike1=(LinearLayout) contentView.findViewById(R.id.maike1);
        ImageView ivmaike1=(ImageView)contentView.findViewById(R.id.ivmaike1);
        video2=(TXCloudVideoView) contentView.findViewById(R.id.video2);
        TextView name2=(TextView) contentView.findViewById(R.id.name2);
        LinearLayout maike2=(LinearLayout) contentView.findViewById(R.id.maike2);
        ImageView ivmaike2=(ImageView)contentView.findViewById(R.id.ivmaike2);
        LinearLayout line2=(LinearLayout) contentView.findViewById(R.id.line2);
        video3=(TXCloudVideoView) contentView.findViewById(R.id.video3);
        TextView name3=(TextView) contentView.findViewById(R.id.name3);
        LinearLayout maike3=(LinearLayout) contentView.findViewById(R.id.maike3);
        ImageView ivmaike3=(ImageView)contentView.findViewById(R.id.ivmaike3);
        video4=(TXCloudVideoView) contentView.findViewById(R.id.video4);
        TextView name4=(TextView) contentView.findViewById(R.id.name4);
        LinearLayout maike4=(LinearLayout) contentView.findViewById(R.id.maike4);
        ImageView ivmaike4=(ImageView)contentView.findViewById(R.id.ivmaike4);

        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        //极速模式
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(1);

        if (mLivePlayer1==null){
            //创建 player 对象
            mLivePlayer1 = new TXLivePlayer(mactivity);
        }
        mLivePlayer1.setConfig(mPlayConfig);
        //关键 player 对象与界面 view
        mLivePlayer1.setPlayerView(video1);
        // 设置填充模式
        mLivePlayer1.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        mLivePlayer1.startPlay(list.get(0).toString(), TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV

        if (mLivePlayer2==null) {
            //创建 player 对象
            mLivePlayer2 = new TXLivePlayer(mactivity);
        }
        TXLivePlayConfig mPlayConfig2 = new TXLivePlayConfig();
        //极速模式
        mPlayConfig2.setAutoAdjustCacheTime(true);
        mPlayConfig2.setMinAutoAdjustCacheTime(1);
        mPlayConfig2.setMaxAutoAdjustCacheTime(1);
        mLivePlayer2.setConfig(mPlayConfig2);
        //关键 player 对象与界面 view
        mLivePlayer2.setPlayerView(video2);
        // 设置填充模式
        mLivePlayer2.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // 设置画面渲染方向
//        mLivePlayer2.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        mLivePlayer2.startPlay(list.get(1).toString(), TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV

        if (mLivePlayer3==null) {
            //创建 player 对象
            mLivePlayer3 = new TXLivePlayer(mactivity);
        }
        TXLivePlayConfig mPlayConfig3 = new TXLivePlayConfig();
        //极速模式
        mPlayConfig3.setAutoAdjustCacheTime(true);
        mPlayConfig3.setMinAutoAdjustCacheTime(1);
        mPlayConfig3.setMaxAutoAdjustCacheTime(1);
        mLivePlayer3.setConfig(mPlayConfig3);
        //关键 player 对象与界面 view
        mLivePlayer3.setPlayerView(video3);
        // 设置填充模式
        mLivePlayer3.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // 设置画面渲染方向
//        mLivePlayer3.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        mLivePlayer3.startPlay(list.get(2).toString(), TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV

        if (mLivePlayer4==null) {
            //创建 player 对象
            mLivePlayer4 = new TXLivePlayer(mactivity);
        }
        TXLivePlayConfig mPlayConfig4 = new TXLivePlayConfig();
        //极速模式
        mPlayConfig4.setAutoAdjustCacheTime(true);
        mPlayConfig4.setMinAutoAdjustCacheTime(1);
        mPlayConfig4.setMaxAutoAdjustCacheTime(1);
        mLivePlayer4.setConfig(mPlayConfig4);
        //关键 player 对象与界面 view
        mLivePlayer4.setPlayerView(video4);
        // 设置填充模式
        mLivePlayer4.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // 设置画面渲染方向
//        mLivePlayer4.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        mLivePlayer4.startPlay(list.get(3).toString(), TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV

        WindowManager windowManager = mactivity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(width-DensityUtil.dp2px(mactivity, 160));
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.setOnDismissListener(this);
        this.showAtLocation(view, Gravity.LEFT, 0, 0);
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

    @Override
    public void onDismiss() {
        if (mLivePlayer1!=null){
            mLivePlayer1.stopPlay(true); // true 代表清除最后一帧画面
            video1.onDestroy();
            mLivePlayer1=null;
        }
        if (mLivePlayer2!=null){
            mLivePlayer2.stopPlay(true); // true 代表清除最后一帧画面
            video2.onDestroy();
            mLivePlayer2=null;
        }
        if (mLivePlayer3!=null){
            mLivePlayer3.stopPlay(true); // true 代表清除最后一帧画面
            video3.onDestroy();
            mLivePlayer3=null;
        }
        if (mLivePlayer4!=null){
            mLivePlayer4.stopPlay(true); // true 代表清除最后一帧画面
            video4.onDestroy();
            mLivePlayer4=null;
        }

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
        void jianKongOnclick(int position);

    }
    public void setPopupWindowListener(PopupWindowListener popupWindowListener) {
        this.mpopupWindowListener = popupWindowListener;
    }
    public static abstract class ChangeStudentListener{
        public abstract void changeStudentList(int state);
    }

}
