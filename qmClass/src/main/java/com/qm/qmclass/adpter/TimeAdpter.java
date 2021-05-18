package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qm.qmclass.R;

import java.util.List;

public class TimeAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private int selected=-1;
    private List mlist;
    public TimeAdpter(Context context, List list) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
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
            convertView = inflater.inflate(R.layout.pop_time_item, null);
            holderView.tvLikai=(TextView) convertView.findViewById(R.id.likai);
            holderView.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holderView.checked = (ImageView) convertView.findViewById(R.id.checked);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.tvTime.setText(mlist.get(position).toString()+"分钟");
        if (position==selected){
            holderView.tvLikai.setTextColor(mcontext.getResources().getColor(R.color.textGreen));
            holderView.tvTime.setTextColor(mcontext.getResources().getColor(R.color.textGreen));
            holderView.checked.setVisibility(View.VISIBLE);
        }else {
            holderView.tvLikai.setTextColor(mcontext.getResources().getColor(R.color.colorWhite));
            holderView.tvTime.setTextColor(mcontext.getResources().getColor(R.color.colorWhite));
            holderView.checked.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public class HolderView {
        TextView tvLikai;
        TextView tvTime;
        ImageView checked;
    }
public void selected(int postion){
    selected = postion;
    notifyDataSetChanged();
}
}
