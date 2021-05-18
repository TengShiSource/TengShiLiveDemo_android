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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OptionsAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<String> mlist;
    private MyClickListener mListener;
    private LiveDataManager liveDataManager;
    private int mtype;
    private int selectPosition=-1;
    private Set<Integer> dxPosition=new TreeSet<>();
    public OptionsAdpter(Context context, List list,int type, MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
        mtype=type;
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
            convertView = inflater.inflate(R.layout.option_item, null);
            holderView.optionItem = (LinearLayout) convertView.findViewById(R.id.option_item);
            holderView.option = (TextView) convertView.findViewById(R.id.option);
            holderView.optionPd = (ImageView) convertView.findViewById(R.id.option_pd);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        if (mtype==3){
            holderView.option.setVisibility(View.GONE);
            holderView.optionPd.setVisibility(View.VISIBLE);
            if (mlist.get(position).equals("A")){
                if (selectPosition==position){
                    holderView.optionPd.setBackground(mcontext.getDrawable(R.drawable.circle_green_bg));
                }else {
                    holderView.optionPd.setBackground(mcontext.getDrawable(R.drawable.circle_darkgary_bg));
                }
                holderView.optionPd.setImageDrawable(mcontext.getDrawable(R.mipmap.dui));
            }else if (mlist.get(position).equals("B")){
                if (selectPosition==position){
                    holderView.optionPd.setBackground(mcontext.getDrawable(R.drawable.circle_green_bg));
                }else {
                    holderView.optionPd.setBackground(mcontext.getDrawable(R.drawable.circle_darkgary_bg));
                }
                holderView.optionPd.setImageDrawable(mcontext.getDrawable(R.mipmap.cuo));
            }
        }else if (mtype==1){
            if (selectPosition==position){
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_green_bg));
            }else {
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_darkgary_bg));
            }
            holderView.option.setText(mlist.get(position));
        }else if (mtype==2){
            if (dxPosition.contains(position)){
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_green_bg));
            }else {
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_darkgary_bg));
            }
            holderView.option.setText(mlist.get(position));
        }
        holderView.optionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mtype==1||mtype==3){
                    selectPosition=position;
                    notifyDataSetChanged();
                    mListener.myOnClick(position);
                }else if (mtype==2){
                    if (selectPosition==position){
                        dxPosition.remove(position);
                    }else {
                        dxPosition.add(position);
                    }
                    selectPosition=position;
                }

            }
        });
        holderView.option.setTag(position);
        return convertView;
    }

    public class HolderView {
        LinearLayout optionItem;
        TextView option;
        ImageView optionPd;
    }
    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener {
        public abstract void myOnClick(int position);
    }
}
