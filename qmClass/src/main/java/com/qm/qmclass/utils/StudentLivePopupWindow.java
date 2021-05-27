package com.qm.qmclass.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.core.widget.PopupWindowCompat;

import com.alibaba.fastjson.JSON;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.AnswerListAdpter;
import com.qm.qmclass.adpter.ChatContentAdpter;
import com.qm.qmclass.adpter.ColorAdpter;
import com.qm.qmclass.adpter.JushouAdpter;
import com.qm.qmclass.adpter.OptionsAdpter;
import com.qm.qmclass.adpter.XzAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.base.QMSDK;
import com.qm.qmclass.model.AnswerInfor;
import com.qm.qmclass.model.AnswerListInfo;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Response;

public class StudentLivePopupWindow extends PopupWindow {
    private Activity mactivity;
    private PopupWindowListener mpopupWindowListener;
    private CountDownTimer timer;
    private boolean chatChecked;
    private boolean isChatJinyan=false;
    private ChatContentAdpter chatContentAdpter;
    private ColorAdpter colorAdpter;
    private XzAdpter xzAdpter;
    private LiveDataManager liveDataManager;
    private DataManager dataManager;
    private String[] xz = {"shifang","shituo","kongfang","kongtuo"};
    private long dmtime;
    private long dttime;
    private String studentAnswer;
    private LinearLayout question;
    private LinearLayout questionresult;
    private ImageView ivResult;
    private TextView tvResult;
    private TextView rightkey;
    private LinearLayout rush;
    private LinearLayout llresult;
    private TextView rushresult;

    public StudentLivePopupWindow(Activity activity) {
        mactivity=activity;
        liveDataManager=LiveDataManager.getInstance();
        dataManager=DataManager.getInstance();
    }
   //    聊天
    public void showChatPopupWindow(View view,List<String> chatContentList){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_chat,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch danmu=(Switch)contentView.findViewById(R.id.danmu);
        final EditText messageInput=(EditText) contentView.findViewById(R.id.message_input);
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
            messageInput.setInputType(InputType.TYPE_NULL);
            messageInput.setBackground(mactivity.getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
        }else if (!liveDataManager.isJinYan()){
            messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
            messageInput.setBackground(mactivity.getResources().getDrawable(R.drawable.bg_edit));
        }
        danmu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                liveDataManager.setOpenDanmu(isChecked);
                mpopupWindowListener.showDanmu();
                if (liveDataManager.isOpenDanmu()){
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
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    public void addChatContent(String data){
        if (chatContentAdpter!=null){
            chatContentAdpter.add(data);
        }
    }
    //提问
    public void showQuestionPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_question,
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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.questionOnclick("camera");
                dismiss();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.questionOnclick("album");
                dismiss();
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
//设置
    public void showSetPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_set,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch beauty=(Switch)contentView.findViewById(R.id.beauty);
        yincang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView courseId=(TextView)contentView.findViewById(R.id.courseId);
        courseId.setText(String.valueOf(DataManager.getInstance().getCourseId()));
        beauty.setChecked(liveDataManager.isOpenBeauty());

        beauty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                liveDataManager.setOpenBeauty(isChecked);
                mpopupWindowListener.setBeauty();
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
//    退出课堂
    public void quitClass(View view){
        String msg="退出后本节课将会结束，确定要退出课堂吗？";
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.live_classover,
                null, false);
        TextView context=(TextView) contentView.findViewById(R.id.context);
        TextView cancel=(TextView) contentView.findViewById(R.id.cancel);
        TextView determine=(TextView) contentView.findViewById(R.id.determine);
        context.setText(msg);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpopupWindowListener.quitClass();
                dismiss();
            }
        });
        setHeight((int)mactivity.getResources().getDimension(R.dimen.dp_150));
        setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_220));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        setHeight((int)mactivity.getResources().getDimension(R.dimen.dp_100));
        setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_140));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = -(this.getContentView().getMeasuredWidth()-view.getWidth());
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+(int)mactivity.getResources().getDimension(R.dimen.dp_5));
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

        setHeight((int)mactivity.getResources().getDimension(R.dimen.dp_135));
        if (liveDataManager.getWitchTools().equals("2")){
            penstyle.setVisibility(View.VISIBLE);
            xian.setVisibility(View.GONE);
            xingzhuang.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            //        两列
            setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_215));

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
            setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_190));
        }else if (liveDataManager.getWitchTools().equals("3")) {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tvprogress.setText(String.valueOf(liveDataManager.getTextProgress()));
            seekBar.setProgress(liveDataManager.getTextProgress());
            setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_150));
        }else {
            penstyle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tvprogress.setText(String.valueOf(liveDataManager.getLineProgress()));
            seekBar.setProgress(liveDataManager.getLineProgress());
            setWidth((int)mactivity.getResources().getDimension(R.dimen.dp_150));
        }
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = -(this.getContentView().getMeasuredWidth()-view.getWidth());
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+(int)mactivity.getResources().getDimension(R.dimen.dp_5));
        PopupWindowCompat.showAsDropDown(this, view, offsetX, offsetY, Gravity.START);
    }
    //视频全屏
    public void showQPPopupWindow(View view,String userCode,String type){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_qp,
                null, false);
        RelativeLayout quanpingVideoview=(RelativeLayout) contentView.findViewById(R.id.quanpingview);
        if (type.equals("TRTC")){
            TXCloudVideoView trtcView=liveDataManager.getTrtcViewmap().get(userCode);
            if (trtcView!=null){
                ViewGroup trtcViewParent=(ViewGroup) trtcView.getParent();
                if (trtcViewParent!=null){
                    trtcViewParent.removeAllViews();
                }
                quanpingVideoview.addView(trtcView);
            }
        }else if (type.equals("VIDEO")){
            View videoView=liveDataManager.getVideoViewMap().get(userCode);
            if (videoView!=null){
                ViewGroup videoViewParent=(ViewGroup) videoView.getParent();
                if (videoViewParent!=null){
                    videoViewParent.removeAllViews();
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.topMargin = 0;
                layoutParams.leftMargin = 0;
                videoView.setLayoutParams(layoutParams);
                quanpingVideoview.addView(videoView);
            }
        }
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    //签到
    public void showDianMingPopupWindow(View view,int time){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_dianming,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout llSign=(LinearLayout) contentView.findViewById(R.id.ll_sign);
        TextView tvTime=(TextView) contentView.findViewById(R.id.tv_time);
        TextView sign=(TextView) contentView.findViewById(R.id.sign);
        LinearLayout llSignfinish=(LinearLayout) contentView.findViewById(R.id.ll_signfinish);
        TextView finish=(TextView) contentView.findViewById(R.id.finish);
        llSign.setVisibility(View.VISIBLE);
        llSignfinish.setVisibility(View.GONE);
        timer=new CountDownTimer(time*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                dmtime=time*1000-millisUntilFinished;
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                if (minute>9&&second>9){
                    tvTime.setText(minute+":"+second);
                }
                if (minute<10&&second<10){
                    tvTime.setText("0"+minute+":0"+second);
                }
                if (minute>9&&second<10){
                    tvTime.setText(minute+":0"+second);
                }
                if (minute<10&&second>9){
                    tvTime.setText("0"+minute+":"+second);
                }
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
            }
        };
        timer.start();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                mpopupWindowListener.signOnclick(dmtime);
                llSign.setVisibility(View.GONE);
                llSignfinish.setVisibility(View.VISIBLE);
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //答题器
    public void showAnswerPopupWindow(View view,Long code,int type,int exp,int time,String options){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_answer,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);

        question=(LinearLayout) contentView.findViewById(R.id.question);
        questionresult=(LinearLayout) contentView.findViewById(R.id.questionresult);
        ivResult=(ImageView) contentView.findViewById(R.id.iv_result);
        tvResult=(TextView) contentView.findViewById(R.id.tv_result);
        rightkey=(TextView) contentView.findViewById(R.id.rightkey);

        TextView tvQuestiontype=(TextView) contentView.findViewById(R.id.tv_questiontype);
        GridView lvOption=(GridView) contentView.findViewById(R.id.lv_option);
        TextView expValue=(TextView) contentView.findViewById(R.id.expValue);
        TextView answerTime=(TextView) contentView.findViewById(R.id.answer_time);
        TextView commit=(TextView) contentView.findViewById(R.id.commit);
        question.setVisibility(View.VISIBLE);
        questionresult.setVisibility(View.GONE);
        List<String> list = Arrays.asList(options.split(""));
        List<String> optionlist = new ArrayList<String>(list);
        optionlist.remove(0);

        if (type==1){
            tvQuestiontype.setText("单选题");
        }else if (type==2){
            tvQuestiontype.setText("多选题");
        }else if (type==3){
            tvQuestiontype.setText("判断题");
        }

        OptionsAdpter optionsAdpter=new OptionsAdpter(mactivity, optionlist,type, new OptionsAdpter.MyClickListener() {
            @Override
            public void myOnClick(String rightKey) {
                studentAnswer=rightKey;
            }

            @Override
            public void dxOnClick(Set dxRightKey) {
                studentAnswer = TextUtils.join("",dxRightKey.toArray());
            }
        });
        lvOption.setAdapter(optionsAdpter);
        expValue.setText(String.valueOf(exp));
        timer=new CountDownTimer(time*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                dttime=time*1000-millisUntilFinished;
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                if (minute>9&&second>9){
                    answerTime.setText(minute+":"+second);
                }
                if (minute<10&&second<10){
                    answerTime.setText("0"+minute+":0"+second);
                }
                if (minute>9&&second<10){
                    answerTime.setText(minute+":0"+second);
                }
                if (minute<10&&second>9){
                    answerTime.setText("0"+minute+":"+second);
                }
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
            }
        };
        timer.start();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitAnswer(code);
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    private void commitAnswer(Long questionId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseId", dataManager.getCourseId());
        map.put("nickName", dataManager.getUserName());
        map.put("questionId", questionId);
        map.put("studentAnswer", studentAnswer);
        map.put("studentId", dataManager.getUserid());
        String jsonQuestion= JSON.toJSONString(map);
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/question/answer",jsonQuestion,new MyCallBack<BaseResponse<AnswerInfor>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<AnswerInfor> result) {
                if (result.getData()!=null){
                    question.setVisibility(View.GONE);
                    questionresult.setVisibility(View.VISIBLE);
                    AnswerInfor answerInfor=result.getData();
                    if (answerInfor.getAnswerFlag()==0){
                        ivResult.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                        ivResult.setImageDrawable(mactivity.getDrawable(R.mipmap.cuo));
                        tvResult.setText("很遗憾您回答错误!-"+answerInfor.getExpValue()+"分");
                        rightkey.setText("正确答案："+answerInfor.getQuestionAnswer());
                    }else {
                        ivResult.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                        ivResult.setImageDrawable(mactivity.getDrawable(R.mipmap.dui));
                        tvResult.setText("恭喜你回答正确!+"+answerInfor.getExpValue()+"分");
                        rightkey.setText("正确答案："+answerInfor.getQuestionAnswer());
                    }
                    studentAnswer="";
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

    //答题器明细
    public void showAnswerDetailPopupWindow(View view,List<AnswerListInfo> list){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_answerdetail,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);

        ListView lvDetailed=(ListView) contentView.findViewById(R.id.lv_detailed);
        AnswerListAdpter answerListAdpter=new AnswerListAdpter(mactivity, list);
        lvDetailed.setAdapter(answerListAdpter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    //抢答题 抢
    public void showRushPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_rush,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        rush=(LinearLayout) contentView.findViewById(R.id.rush);
        llresult=(LinearLayout) contentView.findViewById(R.id.result);
        rushresult=(TextView) contentView.findViewById(R.id.rushresult);
        TextView tvRush=(TextView) contentView.findViewById(R.id.tv_rush);
        rush.setVisibility(View.VISIBLE);
        llresult.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvRush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rushQuestion();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    private void rushQuestion(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickName", dataManager.getUserName());
        map.put("questionId", liveDataManager.getQuestionId());
        map.put("studentId", dataManager.getUserid());
        String jsonQuestion= JSON.toJSONString(map);
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/question/rushQuestion",jsonQuestion,new MyCallBack<BaseResponse<AnswerInfor>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<AnswerInfor> result) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
    }
    public void refreshRush(String nickName){
        rush.setVisibility(View.GONE);
        llresult.setVisibility(View.VISIBLE);
        rushresult.setText("已被"+nickName+"同学抢答");
    }
    //抢答题 答题完成
    public void showRushFinishPopupWindow(View view,String nickName,int expValue,String questionAnswer,int result){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_rushfinish,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        TextView name=(TextView) contentView.findViewById(R.id.name);
        TextView tvexpValue=(TextView) contentView.findViewById(R.id.expValue);
        TextView rightkey=(TextView) contentView.findViewById(R.id.rightkey);
        TextView yesorno=(TextView) contentView.findViewById(R.id.yesorno);
        name.setText(nickName);
        tvexpValue.setText(expValue+"分");
        rightkey.setText(questionAnswer);
        if (result==0){
            yesorno.setText("否");
        }else if (result==1){
            yesorno.setText("是");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    //抢红包
    public void showRushRedEnvelopePopupWindow(View view,String redPackKey){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_rushredenvelope,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout llRushRedEnvelope=(LinearLayout) contentView.findViewById(R.id.ll_rushRedEnvelope);
//        llresult=(LinearLayout) contentView.findViewById(R.id.result);
//        rushresult=(TextView) contentView.findViewById(R.id.rushresult);
        TextView tvRushRedEnvelope=(TextView) contentView.findViewById(R.id.tv_rushRedEnvelope);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvRushRedEnvelope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rushRedPack(redPackKey);
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    private void rushRedPack(String redPackKey){
        HashMap<String, Object> map = new HashMap<>();
        map.put("redPackKey", redPackKey);
        String jsonQuestion= JSON.toJSONString(map);
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/member/rushRedPack",jsonQuestion,new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<String> result) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
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
    private int getPopWidth(){
        DisplayMetrics outMetrics = new DisplayMetrics();
        mactivity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        int popWidth=0;
        if(widthPixels < heightPixels) {
            popWidth=heightPixels/3;
        }else {
            popWidth=widthPixels/3;
        }
        return popWidth;
    }
    public interface PopupWindowListener {
        void chatSendOnclick(String data);
        void toolItemOnclick(String witch);
        void colorItemOnclick(int position);
        void xiangZhuangOnclick(int type);
        void seekBarOnclick(int progress);
        void xianOnclick(int type);
        void quitClass();
        void showDanmu();
        void questionOnclick(String type);
        void signOnclick(long time);
        void setBeauty();
    }
    public void setPopupWindowListener(PopupWindowListener popupWindowListener) {
        this.mpopupWindowListener = popupWindowListener;
    }
    public static abstract class ChangeStudentListener{
        public abstract void changeStudentList(int state);
    }
}
