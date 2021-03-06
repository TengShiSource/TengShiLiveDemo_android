package com.qm.qmdemo.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.qm.qmdemo.R;


public class DialogUtil {
    private static AlertDialog dialog;

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
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);
        ImageView mIcon = view.findViewById(R.id.icon);
        TextView mTitle = view.findViewById(R.id.title);
        TextView mMessage = view.findViewById(R.id.message);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        TextView viewe=view.findViewById(R.id.view);
        if (iconRes==0){
            mIcon.setVisibility(View.GONE);
        }else {
            mIcon.setImageResource(iconRes);
        }
        if(negativeText.equals("")){
            viewe.setVisibility(View.GONE);
            negativeButton.setVisibility(View.GONE);
        }else {
            negativeButton.setVisibility(View.VISIBLE);
            viewe.setVisibility(View.VISIBLE);
            negativeButton.setText(negativeText);
        }
        mTitle.setText(title);
        positiveButton.setText(positiveText);
        mMessage.setText(msg);
        positiveButton.setOnClickListener(v -> {
            alertDialogBtnClickListener.clickPositive();
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(v -> {
            alertDialogBtnClickListener.clickNegative();
            dialog.dismiss();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        builder.setCancelable(true);   //返回键dismiss
        //创建对话框
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialog.setCanceledOnTouchOutside(cancelableTouchOut);   //失去焦点dismiss
        dialog.show();
    }

    public interface AlertDialogBtnClickListener {
        void clickPositive();

        void clickNegative();
    }
}
