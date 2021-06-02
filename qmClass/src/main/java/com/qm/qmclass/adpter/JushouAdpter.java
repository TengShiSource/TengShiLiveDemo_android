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
import com.qm.qmclass.model.StudentInfor;

import java.util.List;

public class JushouAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List mlist;
    private MyClickListener mListener;
    private LiveDataManager liveDataManager;
    public JushouAdpter(Context context, List list,MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
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
            convertView = inflater.inflate(R.layout.jushou_item, null);
            holderView.cup = (ImageView) convertView.findViewById(R.id.cup);
            holderView.name = (TextView) convertView.findViewById(R.id.tv_name);
            holderView.maike = (ImageView) convertView.findViewById(R.id.maike);
            holderView.tichu = (ImageView) convertView.findViewById(R.id.tichu);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        holderView.name.setText(liveDataManager.getAllStudentsMap().get(mlist.get(position)).getNickName());
        if (liveDataManager.getAllStudentsMap().get(mlist.get(position)).getLianMaiState()==1){
            holderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.guamai));
        }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position)).getLianMaiState()==3){
            holderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.videolist));
        }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position)).getLianMaiState()==2){
            holderView.maike.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.onlianmai));
        }
        if (liveDataManager.getAllStudentsMap().get(mlist.get(position)).getExpIcon()!=null&&!liveDataManager.getAllStudentsMap().get(mlist.get(position)).getExpIcon().equals("")){
            holderView.cup.setVisibility(View.VISIBLE);
            Glide.with(mcontext).load(liveDataManager.getAllStudentsMap().get(mlist.get(position)).getExpIcon()).skipMemoryCache(true).into(holderView.cup);
        }else {
            holderView.cup.setVisibility(View.INVISIBLE);
        }
        holderView.maike.setOnClickListener(mListener);
        holderView.tichu.setOnClickListener(mListener);
        holderView.maike.setTag(position);
        holderView.tichu.setTag(position);
        return convertView;
    }

    public class HolderView {
        TextView name;
        ImageView cup;
        ImageView maike;
        ImageView tichu;
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
    public void refresh(List<String> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
}
