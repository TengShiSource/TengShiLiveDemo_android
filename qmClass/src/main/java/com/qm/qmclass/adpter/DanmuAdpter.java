package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.model.ChatContent;

import java.util.List;

public class DanmuAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<ChatContent> mlist;
    public DanmuAdpter(Context context, List<ChatContent> list) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
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
            convertView = inflater.inflate(R.layout.danmu_content_item, null);
            holderView.danmuname = (TextView) convertView.findViewById(R.id.danmuname);
            holderView.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.danmuname.setText(mlist.get(position).getChatName()+":");
        holderView.tvContent.setText(mlist.get(position).getChatContent());
        return convertView;
    }

    public class HolderView {
        TextView danmuname;
        TextView tvContent;
    }
    public void refresh(List<ChatContent> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }

}
