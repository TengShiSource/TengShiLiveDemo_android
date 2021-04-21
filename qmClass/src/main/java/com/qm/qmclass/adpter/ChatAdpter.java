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

public class ChatAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<ChatContent> mlist;
    public ChatAdpter(Context context, List<ChatContent> list) {
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
            convertView = inflater.inflate(R.layout.chat_content_item, null);
            holderView.chatname = (TextView) convertView.findViewById(R.id.chatname);
            holderView.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if (mlist.get(position).getChatName().equals("系统")){
            holderView.chatname.setText("系统:");
        }else {
            holderView.chatname.setText(mlist.get(position).getChatName()+":");
        }
        holderView.tvContent.setText(mlist.get(position).getChatContent());
        return convertView;
    }

    public class HolderView {
        TextView chatname;
        TextView tvContent;
    }
    public void refresh(List<ChatContent> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }

}
