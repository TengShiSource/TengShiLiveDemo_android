package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.YcFileInfo;

import java.text.SimpleDateFormat;
import java.util.List;

public class YCAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<YcFileInfo> mlist;
    private MyClickListener mListener;
    public YCAdpter(Context context, List<YcFileInfo> list, MyClickListener listener) {
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
            convertView = inflater.inflate(R.layout.yc_item, null);
            holderView.fileItem = (LinearLayout) convertView.findViewById(R.id.fileItem);
            holderView.filename = (TextView) convertView.findViewById(R.id.filename);
            holderView.filetime = (TextView) convertView.findViewById(R.id.filetime);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        holderView.filename.setText(mlist.get(position).getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time=simpleDateFormat.format(mlist.get(position).getCreateTime());
        holderView.filetime.setText(time);
        holderView.fileItem.setOnClickListener(mListener);
        holderView.fileItem.setTag(position);
        return convertView;
    }

    public class HolderView {
        LinearLayout fileItem;
        TextView filename;
        TextView filetime;
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
