package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;

import java.util.List;

public class ColorAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<Integer> mlist;
    private int selectorPosition=0;
    private LiveDataManager liveDataManager;
    public ColorAdpter(Context context,List list) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        liveDataManager=LiveDataManager.getInstance();
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
            convertView = inflater.inflate(R.layout.pop_color_item, null);
            holderView.llColor = (LinearLayout) convertView.findViewById(R.id.ll_color);
            holderView.ivColor = (ImageView) convertView.findViewById(R.id.iv_color);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.ivColor.setImageDrawable(mcontext.getDrawable(mlist.get(position)));
        if (liveDataManager.getWitchTools().equals("3")){
            if (liveDataManager.getTextColor()==position){
                holderView.llColor.setBackgroundResource(R.drawable.circle_bg_gray);
            }else {
                holderView.llColor.setBackgroundResource(0);
            }
        }else if (liveDataManager.getWitchTools().equals("2")||liveDataManager.getWitchTools().equals("1")){
            if (liveDataManager.getLineColor()==position){
                holderView.llColor.setBackgroundResource(R.drawable.circle_bg_gray);
            }else {
                holderView.llColor.setBackgroundResource(0);
            }
        }else {
            if (selectorPosition==position){
                holderView.llColor.setBackgroundResource(R.drawable.circle_bg_gray);
            }else {
                holderView.llColor.setBackgroundResource(0);
            }
        }

//        holderView.tvColor.setOnClickListener(new MyOnClickListener(position));
        return convertView;
    }
    public void changeState(int pos) {
        selectorPosition = pos;
        notifyDataSetChanged();
    }
    public class HolderView {
        LinearLayout llColor;
        ImageView ivColor;
    }


}
