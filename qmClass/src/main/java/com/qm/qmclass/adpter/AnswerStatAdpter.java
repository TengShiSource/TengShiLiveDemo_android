package com.qm.qmclass.adpter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentAnswerStatInfo;
import com.qm.qmclass.model.StudentSignInfor;

import java.util.List;

public class AnswerStatAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<StudentAnswerStatInfo.AnswerStat> mlist;
    private String mtotalCount="5";
    private LiveDataManager liveDataManager;
    public AnswerStatAdpter(Context context, List<StudentAnswerStatInfo.AnswerStat> list,String totalCount) {
        inflater = LayoutInflater.from(context);
        if (totalCount==null){
            mtotalCount="0";
        }else {
            mtotalCount=totalCount;
        }
        mcontext=context;
        mlist=list;
        liveDataManager=LiveDataManager.getInstance();
    }
    @Override
    public int getCount() {
        if (mlist!=null&&!mlist.isEmpty()){
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
            convertView = inflater.inflate(R.layout.answerstatistic_item, null);
            holderView.key = (TextView) convertView.findViewById(R.id.key);
            holderView.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
            holderView.personnum = (TextView) convertView.findViewById(R.id.personnum);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        holderView.key.setText(mlist.get(position).getOption());
        holderView.personnum.setText(mlist.get(position).getStudentNum()+"人");
        holderView.seekBar.setMax(Integer.parseInt(mtotalCount));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holderView.seekBar.setMin(0);
        }
        holderView.seekBar.setProgress(mlist.get(position).getStudentNum());
        return convertView;
    }

    public class HolderView {
        TextView key;
        SeekBar seekBar;
        TextView personnum;
    }
    public void refresh(List<StudentAnswerStatInfo.AnswerStat> list,String totalCount) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        if (totalCount==null){
            mtotalCount="0";
        }else {
            mtotalCount=totalCount;
        }
        notifyDataSetChanged();
    }
}
