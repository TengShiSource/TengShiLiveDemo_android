package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentSignInfor;

import java.util.List;

public class DMDetailsAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<StudentSignInfor> mlist;
    private LiveDataManager liveDataManager;
    public DMDetailsAdpter(Context context, List<StudentSignInfor> list) {
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
            convertView = inflater.inflate(R.layout.dmdetails_item, null);
            holderView.icon = (ImageView) convertView.findViewById(R.id.icon);
            holderView.name = (TextView) convertView.findViewById(R.id.tv_name);
            holderView.signtime = (TextView) convertView.findViewById(R.id.signtime);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if (mlist.get(position).getTime()!=null){
            long minute=Long.valueOf(mlist.get(position).getTime())/(1000*60);
            long second=(Long.valueOf(mlist.get(position).getTime())-minute*(1000*60))/1000;
            if (minute>9&&second>9){
                holderView.signtime.setText(minute+":"+second);
            }
            if (minute<10&&second<10){
                holderView.signtime.setText("0"+minute+":0"+second);
            }
            if (minute>9&&second<10){
                holderView.signtime.setText(minute+":0"+second);
            }
            if (minute<10&&second>9){
                holderView.signtime.setText("0"+minute+":"+second);
            }
        }else {
            holderView.signtime.setText("");
        }
        Glide.with(mcontext).load(liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getAvatarUrl()).skipMemoryCache(true).into(holderView.icon);
        holderView.name.setText(liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getNickName());
        return convertView;
    }

    public class HolderView {
        TextView name;
        ImageView icon;
        TextView signtime;
    }
    public void refresh(List<StudentSignInfor> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
}
