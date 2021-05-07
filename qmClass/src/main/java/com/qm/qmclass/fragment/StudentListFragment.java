package com.qm.qmclass.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.qm.qmclass.BuildConfig;
import com.qm.qmclass.R;
import com.qm.qmclass.activitys.TeacherLiveActivity;
import com.qm.qmclass.adpter.StudentStateAdpter;
import com.qm.qmclass.base.DataManager;
import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.CourseInfo;
import com.qm.qmclass.model.StudentInfor;
import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.utils.LivePopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;


public class StudentListFragment extends Fragment implements TeacherLiveActivity.StudentlistFragmentListener {
    private TextView state;
    private ListView studentlist;
    private LinearLayout changestate;
    private LivePopupWindow statePopupWindow;
    private TeacherLiveActivity teacherLiveActivity;
    private List<StudentInfor> shangkeList =new ArrayList();
    private StudentStateAdpter studentStateAdpter;
    private DataManager dataManager;
    private LiveDataManager liveDataManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studentlist, container, false);
        dataManager=DataManager.getInstance();
        liveDataManager=LiveDataManager.getInstance();
        teacherLiveActivity=(TeacherLiveActivity)getActivity();
        teacherLiveActivity.setStudentlistFragmentListener(this);
        state=(TextView) view.findViewById(R.id.state);
        studentlist=(ListView) view.findViewById(R.id.studentlist);
        changestate=(LinearLayout) view.findViewById(R.id.changestate);
        studentStateAdpter=new StudentStateAdpter(teacherLiveActivity, shangkeList, 0, new StudentStateAdpter.StudentStateClickListener() {
            @Override
            public void shangkeOnClick(String userCode, View v) {
                if (v.getId()==R.id.maike){
                    if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==1){
//                    老师发起让学生挂麦
                        Map<String, String> map = new HashMap<>();
                        map.put("action", "micClose");
                        String str = JSON.toJSONString(map);
                        final byte msg[] = str.getBytes();
                        teacherLiveActivity.sendCustomMessage(userCode,msg);
                        liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(3);
                        teacherLiveActivity.refuseLianMai(userCode);
                        lianMai(userCode,3);
                        liveDataManager.getAllStudentsMap().get(userCode).setHuabiOn(false);
                    }else if (liveDataManager.getAllStudentsMap().get(userCode).getLianMaiState()==3){
                        //老师向学生发起连麦
                        Map<String, String> map = new HashMap<>();
                        map.put("action", "micOpen");
                        String str = JSON.toJSONString(map);
                        final byte msg[] = str.getBytes();
                        teacherLiveActivity.sendCustomMessage(userCode,msg);
                        liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(2);
                        teacherLiveActivity.changeStudentVideoLMstate(userCode);
                        lianMai(userCode,2);
                    }

                }else if(v.getId()==R.id.tichu){
                    Toast.makeText(teacherLiveActivity,userCode+"踢出",Toast.LENGTH_SHORT).show();
                    //老师向学生发起连麦
                    Map<String, String> map = new HashMap<>();
                    map.put("action", "kickOut");
                    String str = JSON.toJSONString(map);
                    final byte msg[] = str.getBytes();
                    teacherLiveActivity.sendCustomMessage(userCode,msg);
                    liveDataManager.getJushouList().remove(userCode);
                }
            }

            @Override
            public void weishangkeOnClick(String userCode, View v) {
                if (v.getId()==R.id.hujiao){
                    Toast.makeText(teacherLiveActivity,userCode+"呼叫",Toast.LENGTH_SHORT).show();
                }
            }
        });
        studentlist.setAdapter(studentStateAdpter);
        showShangKeList();
        changestate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePopupWindow=new LivePopupWindow(teacherLiveActivity);
                statePopupWindow.showStudentStatePopupWindow(v, new LivePopupWindow.ChangeStudentListener() {
                    @Override
                    public void changeStudentList(int state) {
                        if (state==0){
                            liveDataManager.setTeacher_StudentListState(0);
                            showShangKeList();
                        }else if (state==1){
                            liveDataManager.setTeacher_StudentListState(1);
                            showWeiShangKeList();
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            if (liveDataManager.getTeacher_StudentListState()==0){
                showShangKeList();
            }else if (liveDataManager.getTeacher_StudentListState()==1){
                showWeiShangKeList();
            }
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
        studentStateAdpter.refresh(onLineStudentsList,0);
    }

    public void showWeiShangKeList(){
        List<StudentInfor> offLineStudentsList = new ArrayList(liveDataManager.getOffLineStudentsMap().values());
        if (!offLineStudentsList.isEmpty()){
            state.setText("未上课("+offLineStudentsList.size()+")");
        }else {
            state.setText("未上课(0)");
        }
        studentStateAdpter.refresh(offLineStudentsList,1);
    }

    @Override
    public void lianMai(String userCode, int lianMaistate) {
        if (liveDataManager.getAllStudentsMap().get(userCode)!=null){
            liveDataManager.getAllStudentsMap().get(userCode).setLianMaiState(lianMaistate);
        }
        if (liveDataManager.getTeacher_StudentListState()==0){
            showShangKeList();
        }
    }
}
