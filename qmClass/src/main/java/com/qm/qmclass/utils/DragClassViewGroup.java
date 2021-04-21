package com.qm.qmclass.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class DragClassViewGroup extends LinearLayout {
    private int lastX,lastY,left,top,right,bottom,topwid,rightwid,screenWidth,screenHeight;
    private int width;
    private int height;
    private String mscreenType;
    public DragClassViewGroup(Context context) {
        this(context, null);
    }

    public DragClassViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragClassViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setxx(int mtopwid,int mrightwid){
        topwid=mtopwid;
        rightwid=mrightwid;
    }
//    public void setwh(int width,int height){
//        topwid=mtopwid;
//        rightwid=mrightwid;
//    }
    public void setScreenType(String screenType){
        mscreenType=screenType;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (mscreenType.equals("LANDSCAPE")){
            //横屏
            if (dm.widthPixels<dm.heightPixels){
                screenWidth = dm.heightPixels;
                screenHeight = dm.widthPixels;
            }else {
                screenWidth = dm.widthPixels;
                screenHeight = dm.heightPixels;//减去下边的高度
            }
        }
    }
    public int getStatusBarHeight(){
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=getMeasuredWidth();
        height=getMeasuredHeight();
    }
    //定位
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //可以在这里确定这个viewGroup的：宽 = r-l.高 = b - t
    }
    //拦截touch事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        LogTool.e("onInterceptTouchEvent");
        int action = ev.getAction();
        if(mscreenType.equals("LANDSCAPE")){
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) ev.getRawX();//设定移动的初始位置相对位置
                    lastY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE://移动
                    //event.getRawX()事件点距离屏幕左上角的距离
                    int dx = (int) ev.getRawX() - lastX;
                    int dy = (int) ev.getRawY() - lastY;

                    left = this.getLeft() + dx;
                    top = this.getTop() + dy;
                    right = this.getRight() + dx;
                    bottom = this.getBottom() + dy;
                    if (left < 0+rightwid) { //最左边
                        left = 0+rightwid;
                        right = left + width;
                    }
                    if (right > screenWidth-rightwid) { //最右边
                        right = screenWidth-rightwid;
                        left = right - width;
                    }
                    if (top < 0+topwid) {  //最上边
                        top = 0+topwid;
                        bottom = top + height;
                    }
                    if (bottom > screenHeight-topwid) {//最下边
                        bottom = screenHeight-topwid;
                        top = bottom - height;
                    }
                    this.layout(left, top, right, bottom);//设置控件的新位置
                    Log.e("yidong","position:" + left + ", " + top + ", " + right + ", " + bottom+", " + screenHeight+","+height+","+width);
                    lastX = (int) ev.getRawX();//再次将滑动其实位置定位
                    lastY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    //将最后拖拽的位置定下来，否则页面刷新渲染后按钮会自动回到初始位置
                    //注意父容器

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
                    lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    lp.setMargins(left, top,0,0);
                    this.setLayoutParams(lp);
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
