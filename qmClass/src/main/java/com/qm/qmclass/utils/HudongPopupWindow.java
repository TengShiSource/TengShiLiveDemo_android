package com.qm.qmclass.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.DMDetailsAdpter;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentSignInfor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;

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
        setWidth(DensityUtil.dp2px(mactivity, 255));
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
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                dismiss();
                hdpwListener.dianMingOnclick("timeOut");
            }
        };
        timer.start();

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
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
        setWidth(DensityUtil.dp2px(mactivity, 255));
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
                dismiss();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
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
        TextView rightkey=(TextView) contentView.findViewById(R.id.rightkey);
        ListView lvStatistics=(ListView) contentView.findViewById(R.id.lv_statistics);
        TextView answerednum=(TextView) contentView.findViewById(R.id.answerednum);
        TextView accuracy=(TextView) contentView.findViewById(R.id.accuracy);
        LinearLayout llDetailed=(LinearLayout) contentView.findViewById(R.id.ll_detailed);
        ListView lvDetailed=(ListView) contentView.findViewById(R.id.lv_detailed);
        TextView finshDt=(TextView) contentView.findViewById(R.id.finsh_dt);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getQuestionOptions().clear();
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.setAnswerType(1);
                liveDataManager.setQuestionMode(0);
                liveDataManager.setTimeLimit(60);
                liveDataManager.setExpValue(3);
                hdpwListener.questionOnclick("close");
                dismiss();
            }
        });
        finshDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveDataManager.getQuestionOptions().clear();
                liveDataManager.getQuestionAnswer().clear();
                liveDataManager.setAnswerType(1);
                liveDataManager.setQuestionMode(0);
                liveDataManager.setTimeLimit(60);
                liveDataManager.setExpValue(3);
                hdpwListener.questionOnclick("finish");
                dismiss();
            }
        });

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(DensityUtil.dp2px(mactivity, 255));
        setOnDismissListener(this);
        setOutsideTouchable(false);
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }
    @Override
    public void onDismiss() {

    }


    public interface HDPWListener {
        void dianMingOnclick(String state);
        void questionOnclick(String state);
    }
    public void setHDPWListener(HDPWListener hdpwListener) {
        this.hdpwListener = hdpwListener;
    }
}
