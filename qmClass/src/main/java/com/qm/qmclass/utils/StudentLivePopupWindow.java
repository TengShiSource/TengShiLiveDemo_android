package com.qm.qmclass.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qm.qmclass.R;
import com.qm.qmclass.adpter.ChatAdpter;
import com.qm.qmclass.adpter.ColorAdpter;
import com.qm.qmclass.adpter.DanmuAdpter;
import com.qm.qmclass.adpter.HudongAdpter;
import com.qm.qmclass.adpter.JushouAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.ChatContent;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.List;

public class StudentLivePopupWindow extends PopupWindow{
    private Activity mactivity;
    private PopupWindowListener mpopupWindowListener;
    private boolean chatChecked;
    private boolean isChatJinyan=false;
    private ChatAdpter chatAdpter;
    private DanmuAdpter danmuAdpter;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;

    public StudentLivePopupWindow(Activity activity) {
        mactivity=activity;
        dataManager=DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
    }
   //    聊天
    public void showChatPopupWindow(View view,List<ChatContent> chatContentList){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.livestudent_chat,
                null, false);
        ImageView yincang=(ImageView)contentView.findViewById(R.id.yincang);
        Switch danmu=(Switch)contentView.findViewById(R.id.danmu);
        final EditText messageInput=(EditText) contentView.findViewById(R.id.message_input);
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
            messageInput.setInputType(InputType.TYPE_NULL);
            messageInput.setBackground(mactivity.getResources().getDrawable(R.drawable.bg_danmu_jinyan_edit));
        }else if (!liveDataManager.isJinYan()){
            messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
            messageInput.setBackground(mactivity.getResources().getDrawable(R.drawable.bg_danmu_edit));
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
        setHeight(DensityUtil.dp2px(mactivity, 150));
        setWidth(DensityUtil.dp2px(mactivity, 220));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        this.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    //选择画笔颜色
    public void showColorPopupWindow(View view,List list){

        View contentView = LayoutInflater.from(mactivity).inflate(R.layout.liveteacher_color,
                null, false);
//        ListView listView=(ListView)contentView.findViewById(R.id.listView);
//        ColorAdpter colorAdpter=new ColorAdpter(mactivity,list);
//        listView.setAdapter(colorAdpter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mpopupWindowListener.colorItemOnclick(position);
//                dismiss();
//            }
//        });
        setHeight(DensityUtil.dp2px(mactivity, 90));
        setWidth(DensityUtil.dp2px(mactivity, 125));
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(contentView);
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(this.getWidth()),
                makeDropDownMeasureSpec(this.getHeight()));
        int offsetX = -Math.abs(this.getContentView().getMeasuredWidth()-view.getWidth()) / 2;
        int offsetY = -(this.getContentView().getMeasuredHeight()+view.getHeight()+DensityUtil.dp2px(mactivity, 15));
        PopupWindowCompat.showAsDropDown(this, view, offsetX, offsetY, Gravity.START);
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
        void colorItemOnclick(int position);
        void quitClass();
        void showDanmu();
    }
    public void setPopupWindowListener(PopupWindowListener popupWindowListener) {
        this.mpopupWindowListener = popupWindowListener;
    }
    public static abstract class ChangeStudentListener{
        public abstract void changeStudentList(int state);
    }
}
