package com.qm.qmclass.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qm.qmclass.R;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentInfor;

import java.util.List;

public class AddStudentAdpter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mcontext;
    private List<StudentInfor> mlist;
    private LiveDataManager liveDataManager;
    private AddStudentClickListener maddStudentClickListener;
    public AddStudentAdpter(Context context, List<StudentInfor> list, AddStudentClickListener addStudentClickListener) {
        inflater = LayoutInflater.from(context);
        mcontext=context;
        mlist=list;
        liveDataManager=LiveDataManager.getInstance();
        maddStudentClickListener=addStudentClickListener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        AddSTHolderView addSTHolderView= null;
        if (convertView == null) {
            addSTHolderView = new AddSTHolderView();
            convertView = inflater.inflate(R.layout.addstudent_item, null);
            addSTHolderView.studentname = (TextView) convertView.findViewById(R.id.studentname);
            addSTHolderView.addstudent= (ImageView) convertView.findViewById(R.id.addstudent);
            convertView.setTag(addSTHolderView);
        } else {
            addSTHolderView = (AddSTHolderView) convertView.getTag();
        }
        if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==1){
            addSTHolderView.addstudent.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.guamai));
        }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==3){
            addSTHolderView.addstudent.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.videolist));
        }else if (liveDataManager.getAllStudentsMap().get(mlist.get(position).getUserCode()).getLianMaiState()==2){
            addSTHolderView.addstudent.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.onlianmai));
        }
        addSTHolderView.studentname.setText(mlist.get(position).getNickName());
        addSTHolderView.addstudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maddStudentClickListener.addStudentOnClick(mlist.get(position).getUserCode(),v);
                }
            });

        return convertView;
    }
    public class AddSTHolderView {
        TextView studentname;
        ImageView addstudent;
    }
    public void refresh(List<StudentInfor> list,int state) {
        mlist = list;//传入list，然后调用notifyDataSetChanged方法
        notifyDataSetChanged();
    }
    public static abstract class AddStudentClickListener{
        public abstract void addStudentOnClick(String userCode, View v);
    }
}
