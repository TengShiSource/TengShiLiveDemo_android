package com.qm.qmclass.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.TimeUtils;

import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.tencent.rtmp.ui.TXCloudVideoView;


public class DialogUtil {
    private static AlertDialog dialog;
    private static AlertDialog jkdialog;
    private static AlertDialog ysdialog;
    private static CountDownTimer timer;
    private static int delayedTime=0;

    /**
     * @param activity                    Context
     * @param iconRes                     提示图标
     * @param title                       提示标题
     * @param msg                         提示内容
     * @param positiveText                确认
     * @param negativeText                取消
     * @param cancelableTouchOut          点击外部是否隐藏提示框
     * @param alertDialogBtnClickListener 点击监听
     */
    public static void showAlertDialog(Activity activity, int iconRes, String title, String msg,
                                       String positiveText, String negativeText, boolean
                                               cancelableTouchOut, final AlertDialogBtnClickListener
                                               alertDialogBtnClickListener) {
//        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);
//        ImageView mIcon = view.findViewById(R.id.icon);
//        TextView mTitle = view.findViewById(R.id.title);
//        TextView mMessage = view.findViewById(R.id.message);
//        TextView positiveButton = view.findViewById(R.id.positiveButton);
//        TextView negativeButton = view.findViewById(R.id.negativeButton);
//        TextView viewe=view.findViewById(R.id.view);
//        if (iconRes==0){
//            mIcon.setVisibility(View.GONE);
//        }else {
//            mIcon.setImageResource(iconRes);
//        }
//        if(negativeText.equals("")){
//            viewe.setVisibility(View.GONE);
//            negativeButton.setVisibility(View.GONE);
//        }else {
//            negativeButton.setVisibility(View.VISIBLE);
//            viewe.setVisibility(View.VISIBLE);
//            negativeButton.setText(negativeText);
//        }
//        mTitle.setText(title);
//        positiveButton.setText(positiveText);
//        mMessage.setText(msg);
//        positiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialogBtnClickListener.clickPositive();
//                dialog.dismiss();
//            }
//        });
//        negativeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialogBtnClickListener.clickNegative();
//                dialog.dismiss();
//            }
//        });
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//
//        builder.setCancelable(true);   //返回键dismiss
//        //创建对话框
//        dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
//        dialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
//        dialog.show();
    }
    /**
     * @param activity                    Context
     * @param positiveText                确认
     * @param negativeText                取消
     * @param cancelableTouchOut          点击外部是否隐藏提示框
     * @param alertDialogBtnClickListener 点击监听
     */
    public static void showDialog(Activity activity, String msg,
                                           String positiveText, String negativeText, boolean cancelableTouchOut, final AlertDialogBtnClickListener
                                                   alertDialogBtnClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);
        final TextView mMessage = view.findViewById(R.id.context);
        TextView positiveButton = view.findViewById(R.id.determine);
        TextView negativeButton = view.findViewById(R.id.cancel);
        mMessage.setText(msg);
        positiveButton.setText(positiveText);
        negativeButton.setText(negativeText);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBtnClickListener.clickPositive();
                dialog.dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBtnClickListener.clickNegative();
                dialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        builder.setCancelable(false);   //返回键dismiss
        //创建对话框
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        dialog.show();
    }
    /**
     * @param activity                    Context
     * @param time                         提示内容
     * @param positiveText                确认
     * @param cancelableTouchOut          点击外部是否隐藏提示框
     * @param alertDialogBtnClickListener 点击监听
     */
    public static void showClassoverDialog(Activity activity, final String time,
                                           String positiveText,  boolean cancelableTouchOut, final AlertDialogBtnClickListener
                                               alertDialogBtnClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.liveteacher_classover, null);
        final TextView mMessage = view.findViewById(R.id.context);
        TextView positiveButton = view.findViewById(R.id.determine);
        positiveButton.setText(positiveText);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                if (timer != null) {
                    timer.cancel();
                }
                alertDialogBtnClickListener.clickPositive();
            }
        });
        timer=new CountDownTimer(Integer.parseInt(time)*60000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                mMessage.setText(minute+":"+second);
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                alertDialogBtnClickListener.clickNegative();
            }
        };
        timer.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        builder.setCancelable(false);   //返回键dismiss
        //创建对话框
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        dialog.show();
    }

    public static void showJianKongQPDialog(Activity activity, final String userCode,
                                             boolean cancelableTouchOut, final AlertDialogBtnClickListener
                                                   alertDialogBtnClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.liveteacher_qp, null);
        RelativeLayout quanpingVideoview=(RelativeLayout) view.findViewById(R.id.quanpingview);
        LinearLayout videoQuanping=(LinearLayout) view.findViewById(R.id.video_quanping);
        TXCloudVideoView trtcView= LiveDataManager.getInstance().getTrtcViewmap().get(userCode);
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
                quanpingVideoview.removeAllViews();
                alertDialogBtnClickListener.clickNegative();
                jkdialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.Dialog_Fullscreen);
        builder.setView(view);
        builder.setCancelable(false);   //返回键dismiss
        //创建对话框
        jkdialog = builder.create();
        jkdialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        jkdialog.show();
    }
    private static ImageView close;
    private static TextView time;
    private static DelayedDialogClickListener mdelayedDialogClickListener;
    /**
     * 延时课堂
     */
    public static void showDelayedDialog(Activity activity, int state,boolean cancelableTouchOut, final DelayedDialogClickListener
                                                   delayedDialogClickListener) {
        mdelayedDialogClickListener=delayedDialogClickListener;
        View view = LayoutInflater.from(activity).inflate(R.layout.liveteacher_delayed, null);
        close= view.findViewById(R.id.close);
        time = view.findViewById(R.id.time);
        final TextView delayed5 = view.findViewById(R.id.delayed5);
        final TextView delayed10 = view.findViewById(R.id.delayed10);
        final TextView delayed20 = view.findViewById(R.id.delayed20);
        final TextView classover = view.findViewById(R.id.classover);
        TextView determine = view.findViewById(R.id.determine);
        if (state==1){
//            倒计时显示
            reloadYsDialog();
        }else if (state==0){
//            点击显示
            close.setVisibility(View.VISIBLE);
            time.setVisibility(View.GONE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                delayedTime=0;
                ysdialog.dismiss();
            }
        });
        delayed5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedTime=5;
                delayed5.setBackground(activity.getDrawable(R.drawable.green_biankuang));
                delayed5.setTextColor(activity.getResources().getColor(R.color.textGreen));
                delayed10.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed10.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed20.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed20.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                classover.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                classover.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }
        });
        delayed10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedTime=10;
                delayed5.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed5.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed10.setBackground(activity.getDrawable(R.drawable.green_biankuang));
                delayed10.setTextColor(activity.getResources().getColor(R.color.textGreen));
                delayed20.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed20.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                classover.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                classover.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }
        });
        delayed20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedTime=20;
                delayed5.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed5.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed10.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed10.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed20.setBackground(activity.getDrawable(R.drawable.green_biankuang));
                delayed20.setTextColor(activity.getResources().getColor(R.color.textGreen));
                classover.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                classover.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }
        });
        classover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedTime=0;
                delayed5.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed5.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed10.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed10.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                delayed20.setBackground(activity.getDrawable(R.drawable.darkgray_bg));
                delayed20.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                classover.setBackground(activity.getDrawable(R.drawable.green_biankuang));
                classover.setTextColor(activity.getResources().getColor(R.color.textGreen));
            }
        });
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedDialogClickListener.determine(delayedTime);
                delayedTime=0;
                ysdialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        builder.setCancelable(false);   //返回键dismiss
        //创建对话框
        ysdialog = builder.create();
        ysdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        ysdialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        ysdialog.show();
    }
    public static void reloadYsDialog(){
        close.setVisibility(View.GONE);
        time.setVisibility(View.VISIBLE);
        timer=new CountDownTimer(10000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long day=millisUntilFinished/(1000*60*60*24);
                long hour=(millisUntilFinished-day*(1000*60*60*24))/(1000*60*60);
                long minute=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60))/(1000*60);
                long second=(millisUntilFinished-day*(1000*60*60*24)-hour*(1000*60*60)-minute*(1000*60))/1000;
                time.setText(String.valueOf(second));
            }

            @Override
            public void onFinish() {
                if (timer != null) {
                    timer.cancel();
                }
                mdelayedDialogClickListener.determine(0);
            }
        };
        timer.start();
    }
    public static boolean ysIsShowing(){
        boolean isShowing=false;
        if (ysdialog!=null){
            isShowing=ysdialog.isShowing();
        }
        return isShowing;
    }

    public interface AlertDialogBtnClickListener {
        void clickPositive();

        void clickNegative();
    }
    public interface DelayedDialogClickListener {
        void determine(int time);
    }
}
