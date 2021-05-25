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
import com.qm.qmclass.model.AnswerListInfo;
import com.qm.qmclass.model.StudentSignInfor;

import java.util.List;

public class AnswerListAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<AnswerListInfo> mlist;
    private LiveDataManager liveDataManager;
    public AnswerListAdpter(Context context, List<AnswerListInfo> list) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        liveDataManager=LiveDataManager.getInstance();
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
            convertView = inflater.inflate(R.layout.answerlist_item, null);
            holderView.icon = (ImageView) convertView.findViewById(R.id.icon);
            holderView.name = (TextView) convertView.findViewById(R.id.tv_name);
            holderView.answertime = (TextView) convertView.findViewById(R.id.answertime);
            holderView.option = (TextView) convertView.findViewById(R.id.option);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        long time=Long.valueOf(mlist.get(position).getAnswerDuration())*1000;
        long minute=time/(1000*60);
        long second=(time-minute*(1000*60))/1000;
        if (minute>9&&second>9){
            holderView.answertime.setText(minute+":"+second);
        }
        if (minute<10&&second<10){
            holderView.answertime.setText("0"+minute+":0"+second);
        }
        if (minute>9&&second<10){
            holderView.answertime.setText(minute+":0"+second);
        }
        if (minute<10&&second>9){
            holderView.answertime.setText("0"+minute+":"+second);
            }

        Glide.with(mcontext).load(mlist.get(position).getAvatarUrl()).skipMemoryCache(true).into(holderView.icon);
        holderView.name.setText(mlist.get(position).getNickName());
        holderView.option.setText(mlist.get(position).getStudentAnswer());

        return convertView;
    }

    public class HolderView {
        TextView name;
        ImageView icon;
        TextView answertime;
        TextView option;
    }
    public void refresh(List<AnswerListInfo> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
}
