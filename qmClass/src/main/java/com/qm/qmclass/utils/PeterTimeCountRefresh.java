package com.qm.qmclass.utils;
import android.os.CountDownTimer;

/**
 * Author:boshuai.li
 * Time:2020/3/17   11:42
 * Description: 倒计时的工具类
 */
public class PeterTimeCountRefresh extends CountDownTimer {

    private OnTimerFinishListener finishListener;
    private OnTimerProgressListener progressListener;

    /**
     * @param millisInFuture    分钟转换成 毫秒
     * @param countDownInterval 计时的时间间隔
     */
    public PeterTimeCountRefresh(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔,要显示的按钮
    }


    @Override
    public void onTick(long millisUntilFinished) {//计时过程显示
        if (progressListener != null) {
            progressListener.onTimerProgress(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {//计时完毕时触发
        if (finishListener != null) {
            finishListener.onTimerFinish();
        }
    }

    /**
     * 设置timer走完的回调
     */
    public void setOnTimerFinishListener(OnTimerFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    /**
     * 设置监听进度的
     */
    public void setOnTimerProgressListener(OnTimerProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * Timer 执行完成的回调
     */
    public interface OnTimerFinishListener {

        void onTimerFinish();
    }

    /**
     * Timer 进度的监听
     */
    public interface OnTimerProgressListener {

        void onTimerProgress(long timeLong);
    }
}
