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
import com.qm.qmclass.model.RedPackInfo;
import com.qm.qmclass.utils.RoundImageView;

import java.util.List;

public class RedPackAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<RedPackInfo> mlist;
    private LiveDataManager liveDataManager;
    public RedPackAdpter(Context context, List<RedPackInfo> list) {
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
            convertView = inflater.inflate(R.layout.redpack_item, null);
            holderView.icon = (RoundImageView) convertView.findViewById(R.id.icon);
            holderView.name = (TextView) convertView.findViewById(R.id.name);
            holderView.time = (TextView) convertView.findViewById(R.id.time);
            holderView.redpack = (TextView) convertView.findViewById(R.id.redpack);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.name.setText("红包-来自"+"");
        holderView.time.setText(mlist.get(position).getCreateTime());
        holderView.redpack.setText("+"+mlist.get(position).getAmount()+"币");
//        Glide.with(mcontext).load(mlist.get(position)).skipMemoryCache(true).into(holderView.icon);
        return convertView;
    }

    public class HolderView {
        RoundImageView icon;
        TextView name;
        TextView time;
        TextView redpack;
    }
//    public void refresh(List<String> list) {
//        mlist = list;//传入list，然后调用notifyDataSetChanged方法
//        notifyDataSetChanged();
//    }
}
