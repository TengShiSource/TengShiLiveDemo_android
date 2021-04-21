package com.qm.qmclass.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qm.qmclass.R;
import com.qm.qmclass.activitys.StudentLiveActivity;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.OnLineStudentAdpter;
import com.qm.qmclass.adpter.StudentStateAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.utils.LivePopupWindow;

import java.util.ArrayList;
import java.util.List;


public class OnLineStudentListFragment extends Fragment {
    private TextView state;
    private ListView studentlist;
    private StudentLiveActivity studentLiveActivity;
    private List<StudentInfor> shangkeList =new ArrayList();
    private OnLineStudentAdpter onLineStudentAdpter;
    private LiveDataManager liveDataManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onlinestudentlist, container, false);
        studentLiveActivity=(StudentLiveActivity) getActivity();
        liveDataManager=LiveDataManager.getInstance();
        state=(TextView) view.findViewById(R.id.state);
        studentlist=(ListView) view.findViewById(R.id.studentlist);
        onLineStudentAdpter=new OnLineStudentAdpter(studentLiveActivity, shangkeList);
        studentlist.setAdapter(onLineStudentAdpter);
        showShangKeList();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            showShangKeList();
        }
        super.onHiddenChanged(hidden);
    }

    public void showShangKeList(){
        List<StudentInfor> onLineStudentsList = new ArrayList(liveDataManager.getOnLineStudentsMap().values());
        if (!onLineStudentsList.isEmpty()){
            state.setText("上课中("+onLineStudentsList.size()+")");
        }else {
            state.setText("上课中(0)");
        }
        onLineStudentAdpter.refresh(onLineStudentsList);
    }
}
