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

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OptionsAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<String> mlist;
    private MyClickListener mListener;
    private int mtype;
    private int selectPosition=-1;
    private Set<Integer> dxSelectPosition=new TreeSet<>();
    private Set<String> dxRightKey=new TreeSet<>();
    private String rightKey;
    public OptionsAdpter(Context context, List list,int type, MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
        mtype=type;
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
            holderView.option.setVisibility(View.VISIBLE);
            holderView.optionPd.setVisibility(View.GONE);
            if (selectPosition==position){
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_green_bg));
            }else {
                holderView.option.setBackground(mcontext.getDrawable(R.drawable.circle_darkgary_bg));
            }
            holderView.option.setText(mlist.get(position));
        }else if (mtype==2){
            holderView.option.setVisibility(View.VISIBLE);
            holderView.optionPd.setVisibility(View.GONE);
            if (dxSelectPosition.contains(position)){
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
                    if (position==0){
                        rightKey="A";
                    }else if (position==1){
                        rightKey="B";
                    }else if (position==2){
                        rightKey="C";
                    }else if (position==3){
                        rightKey="D";
                    }else if (position==4){
                        rightKey="E";
                    }else if (position==5){
                        rightKey="F";
                    }else if (position==6){
                        rightKey="G";
                    }else if (position==7){
                        rightKey="H";
                    }
                    notifyDataSetChanged();
                    mListener.myOnClick(rightKey);
                }else if (mtype==2){
                    if (selectPosition==position){
                        selectPosition=-1;
                        if (position==0){
                            dxRightKey.remove("A");
                        }else if (position==1){
                            dxRightKey.remove("B");
                        }else if (position==2){
                            dxRightKey.remove("C");
                        }else if (position==3){
                            dxRightKey.remove("D");
                        }else if (position==4){
                            dxRightKey.remove("E");
                        }else if (position==5){
                            dxRightKey.remove("F");
                        }else if (position==6){
                            dxRightKey.remove("G");
                        }else if (position==7){
                            dxRightKey.remove("H");
                        }
                        dxSelectPosition.remove(position);
                    }else {
                        selectPosition=position;
                        if (position==0){
                            dxRightKey.add("A");
                        }else if (position==1){
                            dxRightKey.add("B");
                        }else if (position==2){
                            dxRightKey.add("C");
                        }else if (position==3){
                            dxRightKey.add("D");
                        }else if (position==4){
                            dxRightKey.add("E");
                        }else if (position==5){
                            dxRightKey.add("F");
                        }else if (position==6){
                            dxRightKey.add("G");
                        }else if (position==7){
                            dxRightKey.add("H");
                        }
                        dxSelectPosition.add(position);
                    }
                    notifyDataSetChanged();
                    mListener.dxOnClick(dxRightKey);
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
        public abstract void myOnClick(String RightKey);
        public abstract void dxOnClick(Set dxRightKey);
    }
}
