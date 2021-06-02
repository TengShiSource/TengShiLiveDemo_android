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
import com.qm.qmclass.model.StudentInfor;

import java.util.List;

public class OnLineStudentAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<StudentInfor> mlist;
    public OnLineStudentAdpter(Context context, List<StudentInfor> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ShangkeHolderView shangkeHolderView = null;
            shangkeHolderView = new ShangkeHolderView();
            convertView = inflater.inflate(R.layout.online_item, null);
            shangkeHolderView.studentname = (TextView) convertView.findViewById(R.id.studentname);
            shangkeHolderView.expIcon = (ImageView) convertView.findViewById(R.id.expIcon);
            convertView.setTag(shangkeHolderView);
            shangkeHolderView.studentname.setText(mlist.get(position).getNickName());
            if (mlist.get(position).getExpIcon()!=null&&!mlist.get(position).getExpIcon().equals("")){
                shangkeHolderView.expIcon.setVisibility(View.VISIBLE);
                Glide.with(mcontext).load(mlist.get(position).getExpIcon()).skipMemoryCache(true).into(shangkeHolderView.expIcon);
            }else {
                shangkeHolderView.expIcon.setVisibility(View.INVISIBLE);
            }

        return convertView;
    }

    public class ShangkeHolderView {
        TextView studentname;
        ImageView expIcon;
    }
    public void refresh(List<StudentInfor> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
}
