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

public class JushouAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List mlist;
    private MyClickListener mListener;
    public JushouAdpter(Context context, List list,MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
    }
    @Override
    public int getCount() {
        return mlist.size();
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
        holderView.name.setText(mlist.get(position).toString());
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
}
