package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.QuestionInfo;

import java.util.List;

public class QuestionAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<QuestionInfo> mlist;
    private MyClickListener mListener;
    private LiveDataManager liveDataManager;
    public QuestionAdpter(Context context, List<QuestionInfo> list, MyClickListener listener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        mListener = listener;
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
            convertView = inflater.inflate(R.layout.question_item, null);
            holderView.rlQuestion = (RelativeLayout) convertView.findViewById(R.id.rl_question);
            holderView.ivQuestion = (ImageView) convertView.findViewById(R.id.iv_question);
            holderView.studentName = (TextView) convertView.findViewById(R.id.student_name);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        Glide.with(mcontext).load(mlist.get(position).getPazzleUrl()).skipMemoryCache(true).into(holderView.ivQuestion);
        holderView.studentName.setText(mlist.get(position).getStudentNickName());
        holderView.rlQuestion.setOnClickListener(mListener);
        holderView.rlQuestion.setTag(position);
        return convertView;
    }

    public class HolderView {
        TextView studentName;
        ImageView ivQuestion;
        RelativeLayout rlQuestion;
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
    public void refresh(List<QuestionInfo> list) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
}
