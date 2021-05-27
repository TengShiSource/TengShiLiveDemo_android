package com.qm.qmclass.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.AnswerListAdpter;
import com.qm.qmclass.adpter.AnswerStatAdpter;
import com.qm.qmclass.adpter.DMDetailsAdpter;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.AnswerInfor;
import com.qm.qmclass.model.AnswerListInfo;
import com.qm.qmclass.model.CourseInfo;
import com.qm.qmclass.model.StudentAnswerStatInfo;
import com.qm.qmclass.model.StudentSignInfor;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.tencent.rtmp.TXLiveBase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/*
 *互动下相关功能
 */
public class HudongPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener{
    private TeacherLiveActivity mactivity;
    private HDPWListener hdpwListener;
    private LiveDataManager liveDataManager;
    private CountDownTimer timer;
    private TextView signinNum;
    private DMDetailsAdpter dmDetailsAdpter;
    private int optionNum=4;
    private TextView rightkey;
    private NoScrollListview lvStatistics;
    private TextView answerednum;
    private TextView accuracy;
    private NoScrollListview lvDetailed;
    private AnswerStatAdpter answerStatAdpter;
    private AnswerListAdpter answerListAdpter;
    private boolean answerFinsh=false;
    Handler answerStathandler=new Handler();
    Handler answerListhandler=new Handler();
    Runnable answerStatRunnable=new Runnable(){
        @Override
        public void run() {
            getStudentAnswerStat();
        }
    };
    Runnable answerListRunnable=new Runnable(){
        @Override
        public void run() {
            getStudentAnswerList();
        }
    };
    private TextView waittime;
    private int recLen = 0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            long day=recLen*1000/(1000*60*60*24);
            long hour=(recLen*1000-day*(1000*60*60*24))/(1000*60*60);
            long minute=(recLen*1000-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
            long second=(recLen*1000-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
            if (minute>9&&second>9){
                waittime.setText("已等待 "+minute+":"+second);
            }
            if (minute<10&&second<10){
                waittime.setText("已等待 0"+minute+":0"+second);
            }
            if (minute>9&&second<10){
                waittime.setText("已等待 "+minute+":0"+second);
            }
            if (minute<10&&second>9){
                waittime.setText("已等待 0"+minute+":"+second);
            }
            handler.postDelayed(this, 1000);
        }
    };
    private boolean startTime=false;
    private long minute=900000;
    private long second=30000;
    private int randomMoney=0;
    private int averageMoney=0;
    private int RedEnveLopeType=1;//1 随机红包  2  均分红包

    public HudongPopupWindow(TeacherLiveActivity activity) {
        mactivity=activity;
        liveDataManager=LiveDataManager.getInstance();
    }

    //发起（结束）点名
    public void showDianMingPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_dianming,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        TextView time30=(TextView) contentView.findViewById(R.id.time_30);
        TextView time60=(TextView) contentView.findViewById(R.id.time_60);
        TextView time180=(TextView) contentView.findViewById(R.id.time_180);
        TextView startDm=(TextView) contentView.findViewById(R.id.start_dm);
        time60.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setDMTime(60);
                dismiss();
            }
        });
        time30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setDMTime(30);
                time30.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time60.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time180.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        time60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setDMTime(60);
                time60.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time180.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        time180.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setDMTime(180);
                time180.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time60.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        startDm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hdpwListener.dianMingOnclick("start");
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
    //点名中
    public void showONDMPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_ondming,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        signinNum=(TextView) contentView.findViewById(R.id.signin_num);
        TextView details=(TextView) contentView.findViewById(R.id.details);
        signinNum.setText("0/"+liveDataManager.getOnLineStudentsMap().size());
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                liveDataManager.setDMTime(60);
                hdpwListener.dianMingOnclick("close");
                liveDataManager.setDMSurplusTime(0);
                dismiss();
            }
        });
        timer=new CountDownTimer(liveDataManager.getDMTime()*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/1000;
                details.setText(second+"s后查看详情");
                liveDataManager.setDMSurplusTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
                hdpwListener.dianMingOnclick("timeOut");
                liveDataManager.setDMSurplusTime(0);
            }
        };
        timer.start();

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    public void refreshONDM(){
        signinNum.setText(liveDataManager.getSignedList().size()+"/"+liveDataManager.getOnLineStudentsMap().size());
    }
    //点名详情
    public void showDMDetailsPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_dmdetails,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout unsign=(LinearLayout) contentView.findViewById(R.id.unsign);
        TextView tvUnsign=(TextView) contentView.findViewById(R.id.tv_unsign);
        View viewUnsign=(View) contentView.findViewById(R.id.view_unsign);
        LinearLayout signed=(LinearLayout) contentView.findViewById(R.id.signed);
        TextView tvSigned=(TextView) contentView.findViewById(R.id.tv_signed);
        View viewSigned=(View) contentView.findViewById(R.id.view_signed);
        ListView signlistView=(ListView)contentView.findViewById(R.id.signlistView);

        tvUnsign.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
        tvSigned.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
        viewSigned.setVisibility(View.INVISIBLE);
        viewUnsign.setVisibility(View.VISIBLE);

        List<String> onLineList = new ArrayList(liveDataManager.getOnLineStudentsMap().keySet());
        List<StudentSignInfor> unsingedList=new ArrayList<>();
        List<StudentSignInfor> singedList=liveDataManager.getSignedList();
        for (int i=0;i<singedList.size();i++){
            onLineList.remove(singedList.get(i).getUserCode());
        }
        for (int i=0;i<onLineList.size();i++){
            StudentSignInfor studentSignInfor=new StudentSignInfor();
            studentSignInfor.setUserCode(onLineList.get(i));
            unsingedList.add(studentSignInfor);
        }
        tvUnsign.setText("未签到（"+unsingedList.size()+"）");
        tvSigned.setText("已签到（"+singedList.size()+"）");
        dmDetailsAdpter=new DMDetailsAdpter(mactivity, unsingedList);
        signlistView.setAdapter(dmDetailsAdpter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getSignedList().clear();
                liveDataManager.setDMTime(60);
                dismiss();
            }
        });
        unsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUnsign.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                tvSigned.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewSigned.setVisibility(View.INVISIBLE);
                viewUnsign.setVisibility(View.VISIBLE);
                dmDetailsAdpter.refresh(unsingedList);
            }
        });
        signed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUnsign.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                tvSigned.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewSigned.setVisibility(View.VISIBLE);
                viewUnsign.setVisibility(View.INVISIBLE);
                dmDetailsAdpter.refresh(singedList);
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
    public void showAnswerPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_answer,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout choice=(LinearLayout) contentView.findViewById(R.id.choice);
        TextView tvChoice=(TextView) contentView.findViewById(R.id.tv_choice);
        View viewChoice=(View) contentView.findViewById(R.id.view_choice);
        LinearLayout judgment=(LinearLayout) contentView.findViewById(R.id.judgment);
        TextView tvJudgment=(TextView) contentView.findViewById(R.id.tv_judgment);
        View viewJudgment=(View) contentView.findViewById(R.id.view_judgment);
        LinearLayout option=(LinearLayout) contentView.findViewById(R.id.option);
        LinearLayout option1=(LinearLayout) contentView.findViewById(R.id.option1);
        TextView optionA=(TextView) contentView.findViewById(R.id.optionA);
        TextView optionB=(TextView) contentView.findViewById(R.id.optionB);
        TextView optionC=(TextView) contentView.findViewById(R.id.optionC);
        TextView optionD=(TextView) contentView.findViewById(R.id.optionD);
        LinearLayout option2=(LinearLayout) contentView.findViewById(R.id.option2);
        TextView optionE=(TextView) contentView.findViewById(R.id.optionE);
        TextView optionF=(TextView) contentView.findViewById(R.id.optionF);
        TextView optionG=(TextView) contentView.findViewById(R.id.optionG);
        TextView optionH=(TextView) contentView.findViewById(R.id.optionH);
        LinearLayout addorreduce=(LinearLayout) contentView.findViewById(R.id.addorreduce);
        ImageView add=(ImageView) contentView.findViewById(R.id.add);
        ImageView reduce=(ImageView) contentView.findViewById(R.id.reduce);
        LinearLayout judge=(LinearLayout) contentView.findViewById(R.id.judge);
        ImageView dui=(ImageView) contentView.findViewById(R.id.dui);
        ImageView cuo=(ImageView) contentView.findViewById(R.id.cuo);
        TextView time30=(TextView) contentView.findViewById(R.id.time_30);
        TextView time60=(TextView) contentView.findViewById(R.id.time_60);
        TextView time180=(TextView) contentView.findViewById(R.id.time_180);
        TextView fen1=(TextView) contentView.findViewById(R.id.fen1);
        TextView fen2=(TextView) contentView.findViewById(R.id.fen2);
        TextView fen3=(TextView) contentView.findViewById(R.id.fen3);
        TextView fen4=(TextView) contentView.findViewById(R.id.fen4);
        TextView fen5=(TextView) contentView.findViewById(R.id.fen5);
        TextView none=(TextView) contentView.findViewById(R.id.none);
        TextView startDt=(TextView) contentView.findViewById(R.id.start_dt);

        tvChoice.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
        tvJudgment.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
        viewJudgment.setVisibility(View.INVISIBLE);
        viewChoice.setVisibility(View.VISIBLE);
        option.setVisibility(View.VISIBLE);
        option2.setVisibility(View.GONE);
        addorreduce.setVisibility(View.VISIBLE);
        judge.setVisibility(View.GONE);
        liveDataManager.getQuestionOptions().add("A");
        liveDataManager.getQuestionOptions().add("B");
        liveDataManager.getQuestionOptions().add("C");
        liveDataManager.getQuestionOptions().add("D");

        liveDataManager.getQuestionAnswer().add("A");
        optionA.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
        liveDataManager.setAnswerType(1);

        time60.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getQuestionOptions().clear();
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.setAnswerType(1);
                liveDataManager.setQuestionMode(0);
                liveDataManager.setTimeLimit(60);
                liveDataManager.setExpValue(3);
                dismiss();
            }
        });
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvChoice.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                tvJudgment.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewJudgment.setVisibility(View.INVISIBLE);
                viewChoice.setVisibility(View.VISIBLE);
                option.setVisibility(View.VISIBLE);
                addorreduce.setVisibility(View.VISIBLE);
                judge.setVisibility(View.GONE);

                option1.setVisibility(View.VISIBLE);
                option2.setVisibility(View.GONE);
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.getQuestionAnswer().add("A");
                optionA.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                optionB.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionC.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionD.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionE.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionF.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionG.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                optionH.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                liveDataManager.setAnswerType(1);
            }
        });
        judgment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvChoice.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                tvJudgment.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewChoice.setVisibility(View.INVISIBLE);
                viewJudgment.setVisibility(View.VISIBLE);
                option.setVisibility(View.GONE);
                addorreduce.setVisibility(View.GONE);
                judge.setVisibility(View.VISIBLE);

                liveDataManager.setAnswerType(3);
                dui.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                cuo.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.getQuestionAnswer().add("A");
            }
        });

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("A")) {
                        liveDataManager.getQuestionAnswer().remove("A");
                        optionA.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("A");
                        optionA.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("B")) {
                        liveDataManager.getQuestionAnswer().remove("B");
                        optionB.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("B");
                        optionB.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("C")) {
                        liveDataManager.getQuestionAnswer().remove("C");
                        optionC.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("C");
                        optionC.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("D")) {
                        liveDataManager.getQuestionAnswer().remove("D");
                        optionD.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("D");
                        optionD.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("E")) {
                        liveDataManager.getQuestionAnswer().remove("E");
                        optionE.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("E");
                        optionE.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("F")) {
                        liveDataManager.getQuestionAnswer().remove("F");
                        optionF.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("F");
                        optionF.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("G")) {
                        liveDataManager.getQuestionAnswer().remove("G");
                        optionG.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("G");
                        optionG.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        optionH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()!=3) {
                    if (liveDataManager.getQuestionAnswer().contains("H")) {
                        liveDataManager.getQuestionAnswer().remove("H");
                        optionH.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    } else {
                        liveDataManager.getQuestionAnswer().add("H");
                        optionH.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    }
                    if (liveDataManager.getQuestionAnswer().size() > 1) {
                        liveDataManager.setAnswerType(2);
                    } else {
                        liveDataManager.setAnswerType(1);
                    }
                }
            }
        });
        dui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()==3){
                    dui.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    cuo.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    liveDataManager.getQuestionAnswer().clear();
                    liveDataManager.getQuestionAnswer().add("A");
                }
            }
        });
        cuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveDataManager.getAnswerType()==3) {
                    cuo.setBackground(mactivity.getDrawable(R.drawable.circle_green_bg));
                    dui.setBackground(mactivity.getDrawable(R.drawable.circle_darkgary_bg));
                    liveDataManager.getQuestionAnswer().clear();
                    liveDataManager.getQuestionAnswer().add("B");
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionNum<8){
                    optionNum++;
                    if (optionNum==3){
                        optionC.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("C");
                    } else if (optionNum==4){
                        optionD.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("D");
                    }else if (optionNum==5){
                        option2.setVisibility(View.VISIBLE);
                        optionE.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("E");
                    }else if (optionNum==6){
                        optionF.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("F");
                    }else if (optionNum==7){
                        optionG.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("G");
                    }else if (optionNum==8){
                        optionH.setVisibility(View.VISIBLE);
                        liveDataManager.getQuestionOptions().add("H");
                    }
                }
            }
        });
        reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionNum>2){
                    optionNum--;
                    if (optionNum==2){
                        optionC.setVisibility(View.INVISIBLE);
                        liveDataManager.getQuestionOptions().remove("C");
                    }else if (optionNum==3){
                        optionD.setVisibility(View.INVISIBLE);
                        liveDataManager.getQuestionOptions().remove("D");
                    } else if (optionNum==4){
                        optionE.setVisibility(View.INVISIBLE);
                        option2.setVisibility(View.GONE);
                        liveDataManager.getQuestionOptions().remove("E");
                    }else if (optionNum==5){
                        optionF.setVisibility(View.INVISIBLE);
                        liveDataManager.getQuestionOptions().remove("F");
                    }else if (optionNum==6){
                        optionG.setVisibility(View.INVISIBLE);
                        liveDataManager.getQuestionOptions().remove("G");
                    }else if (optionNum==7){
                        optionH.setVisibility(View.INVISIBLE);
                        liveDataManager.getQuestionOptions().remove("H");
                    }
                }

            }
        });
        time30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setTimeLimit(30);
                time30.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time60.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time180.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        time60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setTimeLimit(60);
                time60.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time180.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        time180.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setTimeLimit(180);
                time180.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                time30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                time60.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        fen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(1);
                fen1.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        fen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(2);
                fen1.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        fen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(3);
                fen1.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        fen4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(4);
                fen1.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        fen5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(5);
                fen1.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.setExpValue(0);
                fen1.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen2.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen3.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen4.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                fen5.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                none.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
            }
        });
        startDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hdpwListener.questionOnclick("start");
                liveDataManager.setOnQuestion(true);
                dismiss();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOnDismissListener(this);
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    //答题明细
    public void showAnswerDetailsPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_answerdetails,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout statistics=(LinearLayout) contentView.findViewById(R.id.statistics);
        TextView tvStatistics=(TextView) contentView.findViewById(R.id.tv_statistics);
        View viewStatistics=(View) contentView.findViewById(R.id.view_statistics);
        LinearLayout detailed=(LinearLayout) contentView.findViewById(R.id.detailed);
        TextView tvDetailed=(TextView) contentView.findViewById(R.id.tv_detailed);
        View viewDetailed=(View) contentView.findViewById(R.id.view_detailed);
        TextView answerTime=(TextView) contentView.findViewById(R.id.answer_time);
        TextView expValue=(TextView) contentView.findViewById(R.id.expValue);
        LinearLayout llStatistics=(LinearLayout) contentView.findViewById(R.id.ll_statistics);
        rightkey=(TextView) contentView.findViewById(R.id.rightkey);
        lvStatistics=(NoScrollListview) contentView.findViewById(R.id.lv_statistics);
        answerednum=(TextView) contentView.findViewById(R.id.answerednum);
        accuracy=(TextView) contentView.findViewById(R.id.accuracy);
        LinearLayout llDetailed=(LinearLayout) contentView.findViewById(R.id.ll_detailed);
        lvDetailed=(NoScrollListview) contentView.findViewById(R.id.lv_detailed);
        TextView finshDt=(TextView) contentView.findViewById(R.id.finsh_dt);

        answerFinsh=false;
        tvStatistics.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
        viewStatistics.setVisibility(View.VISIBLE);
        tvDetailed.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
        viewDetailed.setVisibility(View.INVISIBLE);

        llStatistics.setVisibility(View.VISIBLE);
        llDetailed.setVisibility(View.GONE);

        finshDt.setBackground(mactivity.getDrawable(R.drawable.green_bg));
        finshDt.setEnabled(true);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getQuestionOptions().clear();
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.setAnswerType(1);
                liveDataManager.setQuestionMode(0);

                liveDataManager.setTimeLimit(60);
                liveDataManager.setExpValue(3);
                liveDataManager.setOnQuestion(false);

                if (timer != null) {
                    timer.cancel();
                }

                answerStatAdpter=null;
                answerListAdpter=null;

                if (answerStatRunnable!=null){
                    answerStathandler.removeCallbacks(answerStatRunnable);
                }
                if (answerListRunnable!=null){
                    answerListhandler.removeCallbacks(answerListRunnable);
                }

                questionClose();

                dismiss();
            }
        });
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatistics.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewStatistics.setVisibility(View.VISIBLE);
                tvDetailed.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewDetailed.setVisibility(View.INVISIBLE);

                llStatistics.setVisibility(View.VISIBLE);
                llDetailed.setVisibility(View.GONE);
                if (answerListRunnable!=null){
                    answerListhandler.removeCallbacks(answerListRunnable);
                }
                if (!answerFinsh){
                    getStudentAnswerStat();
                }

            }
        });
        detailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatistics.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewStatistics.setVisibility(View.INVISIBLE);
                tvDetailed.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewDetailed.setVisibility(View.VISIBLE);

                llStatistics.setVisibility(View.GONE);
                llDetailed.setVisibility(View.VISIBLE);
                if (answerStatRunnable!=null){
                    answerStathandler.removeCallbacks(answerStatRunnable);
                }
                if (!answerFinsh) {
                    getStudentAnswerList();
                }

            }
        });
        timer=new CountDownTimer(liveDataManager.getTimeLimit()*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
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
                liveDataManager.setQuestionSurplusTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                answerFinsh=true;

                if (timer != null) {
                    timer.cancel();
                }

                answerStatAdpter=null;
                answerListAdpter=null;

                if (answerStatRunnable!=null){
                    answerStathandler.removeCallbacks(answerStatRunnable);
                }
                if (answerListRunnable!=null){
                    answerListhandler.removeCallbacks(answerListRunnable);
                }

                questionFinish();
                liveDataManager.setOnQuestion(false);
                finshDt.setBackground(mactivity.getDrawable(R.drawable.gray_bg));
                finshDt.setEnabled(false);
                liveDataManager.setQuestionSurplusTime(0);
            }
        };
        timer.start();

        expValue.setText(liveDataManager.getExpValue()+"分");

        getStudentAnswerStat();

        finshDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getQuestionOptions().clear();
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.setAnswerType(1);
                liveDataManager.setQuestionMode(0);
                liveDataManager.setTimeLimit(60);
                liveDataManager.setExpValue(3);

                answerFinsh=true;

                if (timer != null) {
                    timer.cancel();
                }

                answerStatAdpter=null;
                answerListAdpter=null;

                if (answerStatRunnable!=null){
                    answerStathandler.removeCallbacks(answerStatRunnable);
                }
                if (answerListRunnable!=null){
                    answerListhandler.removeCallbacks(answerListRunnable);
                }

                questionFinish();
                liveDataManager.setOnQuestion(false);
                finshDt.setBackground(mactivity.getDrawable(R.drawable.gray_bg));
                finshDt.setEnabled(false);
            }
        });
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOnDismissListener(this);
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    private void getStudentAnswerStat(){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/question/studentAnswerStat/"+liveDataManager.getQuestionId(), new MyCallBack<BaseResponse<StudentAnswerStatInfo>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<StudentAnswerStatInfo> result) {
                if (result!=null&&result.getData()!=null){
                    StudentAnswerStatInfo studentAnswerStatInfo=result.getData();
                    rightkey.setText("正确答案："+studentAnswerStatInfo.getQuestionAnswer());
                    if (studentAnswerStatInfo.getTotalCount()==null){
                        answerednum.setText(studentAnswerStatInfo.getAnswerCount()+"/0");
                    }else {
                        answerednum.setText(studentAnswerStatInfo.getAnswerCount()+"/"+studentAnswerStatInfo.getTotalCount());
                    }
                    accuracy.setText(studentAnswerStatInfo.getCorrectPercent()+"%");
                    if (answerStatAdpter==null){
                        answerStatAdpter=new AnswerStatAdpter(mactivity, studentAnswerStatInfo.getAnswerStat(),studentAnswerStatInfo.getTotalCount());
                        lvStatistics.setAdapter(answerStatAdpter);
                    }else {
                        answerStatAdpter.refresh(studentAnswerStatInfo.getAnswerStat(),studentAnswerStatInfo.getTotalCount());
                    }
                    answerStathandler.postDelayed(answerStatRunnable,1000);
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

    private void getStudentAnswerList(){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/question/studentAnswerList/"+liveDataManager.getQuestionId(), new MyCallBack<BaseResponse<List<AnswerListInfo>>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<List<AnswerListInfo>> result) {
                if (result!=null&&result.getData()!=null){
                    if (answerListAdpter==null){
                        answerListAdpter=new AnswerListAdpter(mactivity, result.getData());
                        lvDetailed.setAdapter(answerListAdpter);
                    }else {
                        answerListAdpter.refresh(result.getData());
                    }
                    answerListhandler.postDelayed(answerListRunnable,1000);
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

    //抢答中
    public void showRushQuestionPopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_rushquestion,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        waittime=(TextView) contentView.findViewById(R.id.waittime);
        TextView finishrush=(TextView) contentView.findViewById(R.id.finishrush);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                questionClose();
                dismiss();
            }
        });
        handler.postDelayed(runnable, 1000);
        finishrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                questionClose();
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
    //学生答抢答题中
    public void showRushAnswerPopupWindow(View view,String name,int timeLimit){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_onrushanswer,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        TextView rushtips=(TextView) contentView.findViewById(R.id.rushtips);
        TextView ansertime=(TextView) contentView.findViewById(R.id.ansertime);
        TextView finishrush=(TextView) contentView.findViewById(R.id.finishrush);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                questionClose();
                liveDataManager.setOnQuestion(false);



                dismiss();
            }
        });
        String tip="本题被"+name+"抢答，正在答题中";
        if(!TextUtils.isEmpty(tip)&&!TextUtils.isEmpty(name)){
            if(tip.contains(name)){
                int start=tip.indexOf(name);
                int end=start+name.length();
                SpannableStringBuilder spBuilder=new SpannableStringBuilder(tip);
                CharacterStyle charaStyle=new ForegroundColorSpan(mactivity.getResources().getColor(R.color.textGreen));
                spBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                rushtips.setText(spBuilder);
            }
        }
        timer=new CountDownTimer(timeLimit*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                if (minute>9&&second>9){
                    ansertime.setText("答题倒计时 "+minute+":"+second);
                }
                if (minute<10&&second<10){
                    ansertime.setText("答题倒计时 0"+minute+":0"+second);
                }
                if (minute>9&&second<10){
                    ansertime.setText("答题倒计时 "+minute+":0"+second);
                }
                if (minute<10&&second>9){
                    ansertime.setText("答题倒计时 0"+minute+":"+second);
                }
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                questionFinish();
                liveDataManager.setOnQuestion(false);
                dismiss();
            }
        };
        timer.start();
        finishrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                questionFinish();
                liveDataManager.setOnQuestion(false);
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

    //学生答抢答题结束
    public void showRushAnswerFinishPopupWindow(View view,String nickName,int expValue,String questionAnswer,int result){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_rushfinish,
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


    //关闭答题（共用）
    private void questionClose(){
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/question/questionClose/"+liveDataManager.getQuestionId(),"",new MyCallBack<BaseResponse<Long>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<Long> result) {
                if (result.getData()!=null){
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
    //结束答题（共用）
    private void questionFinish(){
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/question/questionFinish/"+liveDataManager.getQuestionId(),"",new MyCallBack<BaseResponse<Long>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<Long> result) {
                if (result.getData()!=null){
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

    //计时器
    public void showFixedTimePopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_fixedtime,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout fixeTime=(LinearLayout) contentView.findViewById(R.id.fixeTime);
        LinearLayout restart=(LinearLayout) contentView.findViewById(R.id.restart);
        PickerView  minute_pv = (PickerView) contentView.findViewById(R.id.minute_pv);
        PickerView  second_pv = (PickerView) contentView.findViewById(R.id.second_pv);
        TextView countDown=(TextView) contentView.findViewById(R.id.countDown);
        TextView startFixeTime=(TextView) contentView.findViewById(R.id.startFixeTime);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 0; i < 31; i++) {
            if (i < 10){
                data.add("0" + i);
            }else {
                data.add("" + i);
            }
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10){
                seconds.add("0" + i);
            }else {
                seconds.add("" + i);
            }
        }
        minute_pv.setData(data);
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                minute=Long.parseLong(text)*1000*60;
            }
        });
        second_pv.setData(seconds);
        second_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                second=Long.parseLong(text)*1000;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                hdpwListener.fixedTimeOnclick("kill",0);
                liveDataManager.setFixedSurplusTime(0);
                dismiss();
            }
        });

        startFixeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startTime){
                    startTime=true;
                    startFixeTime.setText("重新计时");
                    fixeTime.setVisibility(View.GONE);
                    restart.setVisibility(View.VISIBLE);
                    timer=new CountDownTimer(minute+second,1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long day=millisUntilFinished/(1000*60*60*24);
                            long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                            long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                            long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                            if (minute>9&&second>9){
                                countDown.setText(minute+":"+second);
                            }
                            if (minute<10&&second<10){
                                countDown.setText("0"+minute+":0"+second);
                            }
                            if (minute>9&&second<10){
                                countDown.setText(minute+":0"+second);
                            }
                            if (minute<10&&second>9){
                                countDown.setText("0"+minute+":"+second);
                            }
                            liveDataManager.setFixedSurplusTime(millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            if (timer != null) {
                                timer.cancel();
                            }
                            liveDataManager.setFixedSurplusTime(0);
                        }
                    };
                    timer.start();
                    hdpwListener.fixedTimeOnclick("start",minute+second);
                }else {
                    startTime=false;
                    startFixeTime.setText("开始计时");
                    fixeTime.setVisibility(View.VISIBLE);
                    restart.setVisibility(View.GONE);
                    if (timer != null) {
                        timer.cancel();
                    }
                    liveDataManager.setFixedSurplusTime(0);
                }
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

    //红包
    public void showRedEnveLopePopupWindow(View view){
        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.hd_redenvelope,
                null, false);
        ImageView close=(ImageView)contentView.findViewById(R.id.close);
        LinearLayout randomRed=(LinearLayout) contentView.findViewById(R.id.random_red);
        TextView tvRandom=(TextView) contentView.findViewById(R.id.tv_random);
        View viewRandom=(View) contentView.findViewById(R.id.view_random);
        LinearLayout averageRed=(LinearLayout) contentView.findViewById(R.id.average_red);
        TextView tvAverage=(TextView) contentView.findViewById(R.id.tv_average);
        View viewAverage=(View) contentView.findViewById(R.id.view_average);

        LinearLayout llRandom=(LinearLayout) contentView.findViewById(R.id.ll_random);
        TextView random50=(TextView) contentView.findViewById(R.id.random_50);
        TextView random100=(TextView) contentView.findViewById(R.id.random_100);
        TextView random150=(TextView) contentView.findViewById(R.id.random_150);
        TextView random200=(TextView) contentView.findViewById(R.id.random_200);
        EditText randomInput=(EditText) contentView.findViewById(R.id.random_input);

        LinearLayout llAverage=(LinearLayout) contentView.findViewById(R.id.ll_average);
        TextView average10=(TextView) contentView.findViewById(R.id.average_10);
        TextView average20=(TextView) contentView.findViewById(R.id.average_20);
        TextView average30=(TextView) contentView.findViewById(R.id.average_30);
        TextView average40=(TextView) contentView.findViewById(R.id.average_40);
        EditText averageInput=(EditText) contentView.findViewById(R.id.average_input);

        EditText redEnvelopnum=(EditText) contentView.findViewById(R.id.redEnvelopnum);
        TextView  onlinenum = (TextView) contentView.findViewById(R.id.onlinenum);
        TextView  commit = (TextView) contentView.findViewById(R.id.commit);
        int num=liveDataManager.getOnLineStudentsMap().size();
        String tip="当前在线学生共"+num+"人";
        if(!TextUtils.isEmpty(tip)){
            if(tip.contains(String.valueOf(num))){
                int start=tip.indexOf(String.valueOf(num));
                int end=start+String.valueOf(num).length();
                SpannableStringBuilder spBuilder=new SpannableStringBuilder(tip);
                CharacterStyle charaStyle=new ForegroundColorSpan(mactivity.getResources().getColor(R.color.textGreen));
                spBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                onlinenum.setText(spBuilder);
            }
        }
        if (RedEnveLopeType==1){
            tvRandom.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
            viewRandom.setVisibility(View.VISIBLE);
            tvAverage.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
            viewAverage.setVisibility(View.INVISIBLE);
            llRandom.setVisibility(View.VISIBLE);
            llAverage.setVisibility(View.GONE);
        }else {
            tvRandom.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
            viewRandom.setVisibility(View.INVISIBLE);
            tvAverage.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
            viewAverage.setVisibility(View.VISIBLE);
            llRandom.setVisibility(View.GONE);
            llAverage.setVisibility(View.VISIBLE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomMoney=0;
                averageMoney=0;
                RedEnveLopeType=1;
                dismiss();
            }
        });
        randomRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedEnveLopeType=1;
                tvRandom.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewRandom.setVisibility(View.VISIBLE);
                tvAverage.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewAverage.setVisibility(View.INVISIBLE);
                llRandom.setVisibility(View.VISIBLE);
                llAverage.setVisibility(View.GONE);
            }
        });
        averageRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedEnveLopeType=2;
                tvRandom.setTextColor(mactivity.getResources().getColor(R.color.text_color_sub_info));
                viewRandom.setVisibility(View.INVISIBLE);
                tvAverage.setTextColor(mactivity.getResources().getColor(R.color.colorWhite));
                viewAverage.setVisibility(View.VISIBLE);
                llRandom.setVisibility(View.GONE);
                llAverage.setVisibility(View.VISIBLE);
            }
        });
        random50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomMoney=50;
                randomInput.setText("50");
                random50.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                random100.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random150.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random200.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        random100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomMoney=100;
                randomInput.setText("100");
                random50.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random100.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                random150.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random200.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        random150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomMoney=150;
                randomInput.setText("150");
                random50.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random100.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random150.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                random200.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        random200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomMoney=200;
                randomInput.setText("200");
                random50.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random100.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random150.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random200.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
            }
        });
        randomInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (textView.getText()!=null){
                    randomMoney=Integer.parseInt(textView.getText().toString().trim());
                }else {
                    randomMoney=0;
                }
                random50.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random100.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random150.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                random200.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                return false;
            }
        });
        average10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageMoney=10;
                averageInput.setText("10");
                average10.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                average20.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average40.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        average20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageMoney=20;
                averageInput.setText("20");
                average10.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average20.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                average30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average40.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        average30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageMoney=30;
                averageInput.setText("30");
                average10.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average20.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average30.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
                average40.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
            }
        });
        average40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageMoney=40;
                averageInput.setText("40");
                average10.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average20.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average40.setBackground(mactivity.getDrawable(R.drawable.green_fillet_bg));
            }
        });
        averageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (textView.getText()!=null){
                    averageMoney=Integer.parseInt(textView.getText().toString().trim());
                }else {
                    averageMoney=0;
                }
                average10.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average20.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average30.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                average40.setBackground(mactivity.getDrawable(R.drawable.darkgray_bg));
                return false;
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redEnvelopnum.getText()!=null&&!redEnvelopnum.getText().equals("")){
                    if (RedEnveLopeType==1){
                        if (randomMoney!=0){
                            envelopRedPack(Integer.parseInt(redEnvelopnum.getText().toString()),randomMoney,"1");
                        }else {
                            ToastUtil.showToast1(mactivity,"","请输入红包金额");
                        }
                    }else if (RedEnveLopeType==2){
                        if (averageMoney!=0){
                            envelopRedPack(Integer.parseInt(redEnvelopnum.getText().toString()),Integer.parseInt(redEnvelopnum.getText().toString())*averageMoney,"2");
                        }else {
                            ToastUtil.showToast1(mactivity,"","请输入红包金额");
                        }
                    }
                    dismiss();
                }else {
                    ToastUtil.showToast1(mactivity,"","请输入红包个数");
                }

            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(getPopWidth());
        setOutsideTouchable(false);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    //发红包
    private void envelopRedPack(int num,int totalStudyCoin,String type){
        HashMap<String, Object> map = new HashMap<>();
        map.put("num", num);
        map.put("totalStudyCoin", totalStudyCoin);
        map.put("type", type);
        String json=JSON.toJSONString(map);
        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/member/envelopRedPack",json,new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<String> result) {
                if (result.getCode()==200){
                    randomMoney=0;
                    averageMoney=0;
                    RedEnveLopeType=1;
                    ToastUtil.showToast1(mactivity,"","发送红包成功");
                }else {
                    ToastUtil.showToast1(mactivity,"","发送红包失败");
                }

            }

            @Override
            public void onFailure(Request request, Exception e) {
                ToastUtil.showToast1(mactivity,"","发送红包失败");
            }

            @Override
            public void onError(Response response) {
                ToastUtil.showToast1(mactivity,"","发送红包失败");
            }
        });
    }
    @Override
    public void onDismiss() {

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

    public interface HDPWListener {
        void dianMingOnclick(String state);
        void questionOnclick(String state);
        void fixedTimeOnclick(String state,long time);
    }
    public void setHDPWListener(HDPWListener hdpwListener) {
        this.hdpwListener = hdpwListener;
    }
}
