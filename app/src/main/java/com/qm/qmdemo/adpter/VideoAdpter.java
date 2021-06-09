package com.qm.qmdemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmdemo.R;
import com.qm.qmdemo.activity.SetActivity;
import com.qm.qmdemo.model.CourseVideoInfo;
import com.qm.qmdemo.utils.RoundImageView;
import com.qm.qmdemo.utils.SharedPreferencesUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VideoAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<CourseVideoInfo> mlist;
    private MyClickListener mListener;
    public VideoAdpter(Context context, List<CourseVideoInfo> list, MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
    }
    @Override
    public int getCount() {
        if (!mlist.isEmpty()){
            return mlist.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        if (convertView == null) {
            holderView = new HolderView();
            convertView = inflater.inflate(R.layout.video_item, null);
            holderView.coursename = (TextView) convertView.findViewById(R.id.coursename);
            holderView.date = (TextView) convertView.findViewById(R.id.date);
            holderView.time = (TextView) convertView.findViewById(R.id.time);
            holderView.icon = (RoundImageView) convertView.findViewById(R.id.icon);
            holderView.teacherName = (TextView) convertView.findViewById(R.id.teacher_name);
            holderView.playback = (TextView) convertView.findViewById(R.id.playback);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        holderView.coursename.setText(mlist.get(position).getCourseName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date stareDate = null;
        Date endDate = null;
        try {
            stareDate = format.parse(mlist.get(position).getStartTime());
            endDate = format.parse(mlist.get(position).getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();
        calendar.setTime(stareDate);
        calendar2.setTime(endDate);

        holderView.date.setText((calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日");
        String staretime="";
        String endtime="";
        if (calendar.get(Calendar.HOUR)<10&&calendar.get(Calendar.MINUTE)<10){
            staretime="0"+calendar.get(Calendar.HOUR)+":0"+calendar.get(Calendar.MINUTE);
        }else if (calendar.get(Calendar.HOUR)<10&&calendar.get(Calendar.MINUTE)>9){
            staretime="0"+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        }else if (calendar.get(Calendar.HOUR)>9&&calendar.get(Calendar.MINUTE)<10){
            staretime=calendar.get(Calendar.HOUR)+":0"+calendar.get(Calendar.MINUTE);
        }else if (calendar.get(Calendar.HOUR)>9&&calendar.get(Calendar.MINUTE)>9){
            staretime=calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        }
        if (calendar2.get(Calendar.HOUR)<10&&calendar2.get(Calendar.MINUTE)<10){
            endtime="0"+calendar2.get(Calendar.HOUR)+":0"+calendar2.get(Calendar.MINUTE);
        }else if (calendar2.get(Calendar.HOUR)<10&&calendar2.get(Calendar.MINUTE)>9){
            endtime="0"+calendar2.get(Calendar.HOUR)+":"+calendar2.get(Calendar.MINUTE);
        }else if (calendar2.get(Calendar.HOUR)>9&&calendar2.get(Calendar.MINUTE)<10){
            endtime=calendar2.get(Calendar.HOUR)+":0"+calendar2.get(Calendar.MINUTE);
        }else if (calendar2.get(Calendar.HOUR)>9&&calendar2.get(Calendar.MINUTE)>9){
            endtime=calendar2.get(Calendar.HOUR)+":"+calendar2.get(Calendar.MINUTE);
        }
        holderView.time.setText(staretime+"-"+endtime);
        Glide.with(mcontext).load(mlist.get(position).getAvatarUrl()).skipMemoryCache(true).into(holderView.icon);
        holderView.teacherName.setText(mlist.get(position).getNickName());
        if (mlist.get(position).getVideoInfos().size()>0){
            holderView.playback.setBackground(mcontext.getDrawable(R.drawable.green_bg));
            holderView.playback.setEnabled(true);
        }else {
            holderView.playback.setEnabled(false);
            holderView.playback.setBackground(mcontext.getDrawable(R.drawable.gray_track));
        }
        holderView.playback.setOnClickListener(mListener);
        holderView.playback.setTag(position);
        return convertView;
    }

    public class HolderView {
        TextView coursename;
        TextView date;
        TextView time;
        RoundImageView icon;
        TextView teacherName;
        TextView playback;
    }
    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener implements View.OnClickListener {
        /**
         * 基类的onClick方法
         */
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }
        public abstract void myOnClick(int position, View v);
    }
}
