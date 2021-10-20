package com.qm.qmdemo.adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qm.qmdemo.R;
import com.qm.qmdemo.utils.SharedPreferencesUtils;

import java.util.List;

public class IconAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity mactivity;
    private List<String> mlist;
    public IconAdpter(Activity activity, List<String> list) {
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
            convertView = inflater.inflate(R.layout.icon_item, null);
            holderView.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        Glide.with(mactivity).load(mlist.get(position)).skipMemoryCache(true).into(holderView.ivIcon);
        holderView.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.putData("avatarUrl",mlist.get(position));
                mactivity.finish();
            }
        });
        return convertView;
    }

    public class HolderView {
        ImageView ivIcon;
    }
}
