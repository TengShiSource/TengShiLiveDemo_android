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
    private static CountDownTimer timer;

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
    public interface AlertDialogBtnClickListener {
        void clickPositive();

        void clickNegative();
    }
}
