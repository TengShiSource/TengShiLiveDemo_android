package com.qm.qmdemo.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qm.qmdemo.R;
import com.qm.qmdemo.model.CourseVideoInfo;

import java.util.List;

public class PartAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity mactivity;
    private List<CourseVideoInfo.VideoInfos> mlist;
    private int lastPosition=0;
    public PartAdpter(Activity activity, List<CourseVideoInfo.VideoInfos> list) {
        inflater = LayoutInflater.from(activity);
        mactivity=activity;
        mlist=list;
    }
    @Override
    public int getCount() {
        if (mlist!=null&&!mlist.isEmpty()){
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
            convertView = inflater.inflate(R.layout.part_item, null);
            holderView.part = (TextView) convertView.findViewById(R.id.part);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.part.setText("第"+(position+1)+"段");
        if (lastPosition==position){
            holderView.part.setTextColor(mactivity.getResources().getColor(R.color.b_green));
            holderView.part.setBackground(mactivity.getDrawable(R.drawable.greenqian_bg));
        }else {
            holderView.part.setTextColor(mactivity.getResources().getColor(R.color.colorDarkGray));
            holderView.part.setBackground(mactivity.getDrawable(R.drawable.gray_track));
        }
        return convertView;
    }

    public class HolderView {
        TextView part;
    }
    public void refresh(int pos) {
        lastPosition = pos;
        notifyDataSetChanged();
    }
}
