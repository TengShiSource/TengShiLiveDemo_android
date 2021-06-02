package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentInfor;

import java.util.List;

public class StudentStateAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<StudentInfor> mlist;
    private int mstate;
    private LiveDataManager liveDataManager;
    private DataManager dataManager;
    private StudentStateClickListener mstudentStateClickListener;
    public StudentStateAdpter(Context context, List<StudentInfor> list,int state,StudentStateClickListener studentStateClickListener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mstate=state;
        liveDataManager=LiveDataManager.getInstance();
        dataManager=DataManager.getInstance();
        mstudentStateClickListener=studentStateClickListener;
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
        WeiShangkeHolderView weiShangkeHolderView=null;
        convertView=null;
        if (mstate==0){
            shangkeHolderView = new ShangkeHolderView();
            convertView = inflater.inflate(R.layout.shangke_item, null);
            shangkeHolderView.studentname = (TextView) convertView.findViewById(R.id.studentname);
            shangkeHolderView.maike= (ImageView) convertView.findViewById(R.id.maike);
            shangkeHolderView.tichu= (ImageView) convertView.findViewById(R.id.tichu);
            shangkeHolderView.expIcon= (ImageView) convertView.findViewById(R.id.expIcon);
            convertView.setTag(shangkeHolderView);
            if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==1){
                shangkeHolderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.guamai));
            }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==3){
                shangkeHolderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.videolist));
            }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==2){
                shangkeHolderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.onlianmai));
            }
            if (mlist.get(position).getExpIcon()!=null&&!mlist.get(position).getExpIcon().equals("")){
                shangkeHolderView.expIcon.setVisibility(View.VISIBLE);
                Glide.with(mcontext).load(mlist.get(position).getExpIcon()).skipMemoryCache(true).into(shangkeHolderView.expIcon);
            }else {
                shangkeHolderView.expIcon.setVisibility(View.INVISIBLE);
            }
            shangkeHolderView.studentname.setText(mlist.get(position).getNickName());
            shangkeHolderView.maike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mstudentStateClickListener.shangkeOnClick(mlist.get(position).getUserCode(),v);
                }
            });
            shangkeHolderView.tichu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mstudentStateClickListener.shangkeOnClick(mlist.get(position).getUserCode(),v);
                }
            });
        }else if (mstate==1){
            weiShangkeHolderView = new WeiShangkeHolderView();
            convertView = inflater.inflate(R.layout.weishangke_item, null);
            weiShangkeHolderView.studentname = (TextView) convertView.findViewById(R.id.studentname);
            weiShangkeHolderView.shangketixing=(LinearLayout) convertView.findViewById(R.id.shangketixing);
            shangkeHolderView.expIcon= (ImageView) convertView.findViewById(R.id.expIcon);
            convertView.setTag(weiShangkeHolderView);
            weiShangkeHolderView.studentname.setText(mlist.get(position).getNickName());
            if (dataManager.getOpenClassReminder().equals("n")){
                weiShangkeHolderView.shangketixing.setVisibility(View.GONE);
            }else if (dataManager.getOpenClassReminder().equals("y")){
                weiShangkeHolderView.shangketixing.setVisibility(View.VISIBLE);
                if (mlist.get(position).getSKTXNUM()==0){
                    weiShangkeHolderView.tvShangketixing.setText("上课提醒");
                }else {
                    weiShangkeHolderView.tvShangketixing.setText("上课提醒("+mlist.get(position).getSKTXNUM()+")");
                }
            }
            if (mlist.get(position).getExpIcon()!=null&&!mlist.get(position).getExpIcon().equals("")){
                shangkeHolderView.expIcon.setVisibility(View.VISIBLE);
                Glide.with(mcontext).load(mlist.get(position).getExpIcon()).skipMemoryCache(true).into(shangkeHolderView.expIcon);
            }else {
                shangkeHolderView.expIcon.setVisibility(View.INVISIBLE);
            }
            weiShangkeHolderView.shangketixing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mstudentStateClickListener.weishangkeOnClick(mlist.get(position).getUserCode(),v);
                }
            });
        }

        return convertView;
    }
    public class ShangkeHolderView {
        TextView studentname;
        ImageView maike;
        ImageView tichu;
        ImageView expIcon;
    }
    public class WeiShangkeHolderView {
        TextView studentname;
        LinearLayout shangketixing;
        TextView tvShangketixing;
        ImageView expIcon;
    }
    public void refresh(List<StudentInfor> list,int state) {
        mstate=state;
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
    public static abstract class StudentStateClickListener{
        public abstract void shangkeOnClick(String userCode, View v);
        public abstract void weishangkeOnClick(String userCode, View v);
    }
}
