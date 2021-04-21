package com.qm.qmclass.adpter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;

import java.util.List;

public class XzAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<Integer> mlist;
    private int selectorPosition=0;
    private LiveDataManager liveDataManager;
    public XzAdpter(Context context, List list) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        liveDataManager=LiveDataManager.getInstance();
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
            convertView = inflater.inflate(R.layout.pop_xz_item, null);
            holderView.llxingzhuang = (LinearLayout) convertView.findViewById(R.id.llxingzhuang);
            holderView.xingzhuang = (ImageView) convertView.findViewById(R.id.xingzhuang);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)holderView.xingzhuang.getLayoutParams();
        if (position==0){
            holderView.xingzhuang.setBackgroundResource(R.drawable.changfangxing);
            lp.width= mcontext.getResources().getDimensionPixelSize(R.dimen.dp_20);
            lp.height=mcontext.getResources().getDimensionPixelSize(R.dimen.dp_20);
        }else if (position==1){
            holderView.xingzhuang.setBackgroundResource(R.drawable.tuoyuan_bg);
            lp.width= mcontext.getResources().getDimensionPixelSize(R.dimen.dp_23);
            lp.height=mcontext.getResources().getDimensionPixelSize(R.dimen.dp_17);
        }else if (position==2){
            holderView.xingzhuang.setBackgroundResource(R.drawable.zhengfangxing_bg);
            lp.width= mcontext.getResources().getDimensionPixelSize(R.dimen.dp_20);
            lp.height=mcontext.getResources().getDimensionPixelSize(R.dimen.dp_20);
        }else if (position==3){
            holderView.xingzhuang.setBackgroundResource(R.drawable.yuan);
            lp.width= mcontext.getResources().getDimensionPixelSize(R.dimen.dp_23);
            lp.height=mcontext.getResources().getDimensionPixelSize(R.dimen.dp_17);
        }
        holderView.xingzhuang.setLayoutParams(lp);

        if (liveDataManager.getXingzhuang()==position){
            holderView.llxingzhuang.setBackgroundResource(R.drawable.tool_bg);
        }else {
            holderView.llxingzhuang.setBackgroundResource(0);
        }
        GradientDrawable myGrad = (GradientDrawable)holderView.xingzhuang.getBackground();
        if (liveDataManager.getLineColor()==0){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_red));
        }else if (liveDataManager.getLineColor()==1){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_pink));
        }else if (liveDataManager.getLineColor()==2){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_yellow));
        }else if (liveDataManager.getLineColor()==3){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_green));
        }else if (liveDataManager.getLineColor()==4){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_skublue));
        }else if (liveDataManager.getLineColor()==5){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_blue));
        }else if (liveDataManager.getLineColor()==6){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_violet));
        }else if (liveDataManager.getLineColor()==7){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_pinkr));
        }else if (liveDataManager.getLineColor()==8){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_orange));
        }else if (liveDataManager.getLineColor()==9){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_white));
        }else if (liveDataManager.getLineColor()==10){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_gray));
        }else if (liveDataManager.getLineColor()==11){
            myGrad.setColor(mcontext.getResources().getColor(R.color.b_black));
        }

        return convertView;
    }
    public void changeState(int pos) {
        selectorPosition = pos;
        notifyDataSetChanged();
    }
    public class HolderView {
        LinearLayout llxingzhuang;
        ImageView xingzhuang;
    }


}