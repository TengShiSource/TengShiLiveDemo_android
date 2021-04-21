package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qm.qmclass.R;
import com.qm.qmclass.model.ChatContent;
import com.qm.qmclass.model.StudentVideo;
import com.qm.qmclass.tencent.TICVideoRootView;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import java.util.List;

public class StudentVideoAdpter extends RecyclerView.Adapter<StudentVideoAdpter.MyHolder> {
    private Context mcontext;
    private List<StudentVideo> mlist;
    private TRTCCloud mTrtcCloud;
    boolean mEnableFrontCamera = true;
    private boolean isVideoToolShow=true;
    private StudentVideoClickListener onClickListener;
    public StudentVideoAdpter(Context context, List<StudentVideo> list, TRTCCloud trtcCloud,StudentVideoClickListener studentVideoClickListener) {
        mcontext=context;
        mlist=list;
        mTrtcCloud=trtcCloud;
        onClickListener=studentVideoClickListener;
    }

    @NonNull
    @Override
    public StudentVideoAdpter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_video_item,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentVideoAdpter.MyHolder holder, final int position) {
        holder.studentname.setText(mlist.get(position).getName());
        if (mTrtcCloud != null) {
            TXCloudVideoView localVideoView = holder.studentVideoview.getCloudVideoViewByIndex(0);
            localVideoView.setUserId(mlist.get(position).getStudentId());
            //3、开始本地视频图像
            startLocalVideo(true, mlist.get(position).getStudentId(), holder.studentVideoview);

            //4. 开始音频
            enableAudioCapture(true);
        }
        holder.shouqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoToolShow){
//                    hidenAnimation(holderView.rlItem);
                    isVideoToolShow=false;
                    holder.rlTools.setBackgroundColor(mcontext.getResources().getColor(R.color.hlfTransparent));
                    TranslateAnimation hiden = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    hiden.setDuration(500);
                    holder.llTool.startAnimation(hiden);
                    holder.shouqi.setRotation(180);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.shouqi.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    holder.shouqi.setLayoutParams(lp);
                }else {
                    isVideoToolShow=true;
//                    showAnimation(holderView.rlItem);
                    holder.rlTools.setBackground(mcontext.getResources().getDrawable(R.mipmap.videotool));
                    holder.shouqi.setRotation(360);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.shouqi.getLayoutParams();
                    lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    holder.shouqi.setLayoutParams(lp);
                    TranslateAnimation show = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    show.setDuration(500);
                    holder.llTool.startAnimation(show);
                }
            }
        });
        holder.videohuabi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.HuabiOnClick(position);
            }
        });
        holder.videomaike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.MaikeOnClick(position);
            }
        });
        holder.videoquanping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.QuanpingOnClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (!mlist.isEmpty()){
            return mlist.size();
        }
        return 0;
    }
    private void startLocalVideo(boolean enable,String studentId,TICVideoRootView videoview) {
        if (mTrtcCloud != null) {
            // 大画面的编码器参数设置
            TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
            encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
            encParam.videoFps = 15;
            encParam.videoBitrate = 550;
            // videoResolutionMode 设置为横屏
            encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE;
            mTrtcCloud.setVideoEncoderParam(encParam);
            final String usrid = studentId;
            TXCloudVideoView localVideoView = videoview.getCloudVideoViewByUseId(usrid);
            localVideoView.setUserId(usrid);
            localVideoView.setVisibility(View.VISIBLE);
            if (enable) {
                mTrtcCloud.startLocalPreview(mEnableFrontCamera, localVideoView);
            } else {
                mTrtcCloud.stopLocalPreview();
            }
        }
    }

    private void enableAudioCapture(boolean bEnable) {
        if (mTrtcCloud != null) {
            if (bEnable) {
                mTrtcCloud.startLocalAudio();
            } else {
                mTrtcCloud.stopLocalAudio();
            }
        }

    }
    private void showAnimation(View v) {
        isVideoToolShow=true;
        RelativeLayout rlTools = (RelativeLayout) v.findViewById(R.id.rl_tools);
        LinearLayout llTool = (LinearLayout) v.findViewById(R.id.ll_tool);
        LinearLayout shouqi = (LinearLayout) v.findViewById(R.id.ll_tool);

        rlTools.setBackground(mcontext.getResources().getDrawable(R.mipmap.videotool));
        shouqi.setRotation(360);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shouqi.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        shouqi.setLayoutParams(lp);
        TranslateAnimation show = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        show.setDuration(500);
        llTool.startAnimation(show);
    }

    private void hidenAnimation(View v) {
        isVideoToolShow=false;
        RelativeLayout rlTools = (RelativeLayout) v.findViewById(R.id.rl_tools);
        LinearLayout llTool = (LinearLayout) v.findViewById(R.id.ll_tool);
        LinearLayout shouqi = (LinearLayout) v.findViewById(R.id.ll_tool);

        rlTools.setBackgroundColor(mcontext.getResources().getColor(R.color.hlfTransparent));
        TranslateAnimation hiden = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        hiden.setDuration(500);
        llTool.startAnimation(hiden);
        shouqi.setRotation(180);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) shouqi.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        shouqi.setLayoutParams(lp);
    }
    /**
     * 自定义的ViewHolder
     */
    class MyHolder extends RecyclerView.ViewHolder {

        TICVideoRootView studentVideoview;
        TextView studentname;
        RelativeLayout rlTools;
        LinearLayout llTool;
        LinearLayout shouqi;
        LinearLayout videohuabi;
        LinearLayout videomaike;
        LinearLayout videoquanping;
        View mitemView;

        public MyHolder(View itemView) {
            super(itemView);
            mitemView=itemView;
            studentVideoview = (TICVideoRootView) itemView.findViewById(R.id.student_videoview);
            studentname = (TextView) itemView.findViewById(R.id.studentname);
            rlTools = (RelativeLayout) itemView.findViewById(R.id.rl_tools);
            shouqi = (LinearLayout) itemView.findViewById(R.id.shouqi);
            llTool = (LinearLayout) itemView.findViewById(R.id.ll_tool);
//            videohuabi = (LinearLayout) itemView.findViewById(R.id.video_huabi);
//            videomaike = (LinearLayout) itemView.findViewById(R.id.video_maike);
//            videoquanping = (LinearLayout) itemView.findViewById(R.id.video_quanping);
        }
    }
    public void refresh(List<StudentVideo> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
    /**
     * 用于回调的抽象类
     */
    public static abstract class StudentVideoClickListener{
        public abstract void HuabiOnClick(int position);
        public abstract void MaikeOnClick(int position);
        public abstract void QuanpingOnClick(int position);
    }
}
