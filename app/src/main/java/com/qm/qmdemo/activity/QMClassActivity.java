package com.qm.qmdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

import com.qm.qmclass.qmmanager.QMClassManager;
import com.qm.qmdemo.BaseApp;
import com.qm.qmdemo.BuildConfig;
import com.qm.qmdemo.R;
import com.qm.qmdemo.okhttp.BaseResponse;
import com.qm.qmdemo.okhttp.MyCallBack;
import com.qm.qmdemo.okhttp.OkHttpUtils;
import com.qm.qmdemo.utils.SharedPreferencesUtils;
import com.qm.qmdemo.utils.ToastUtil;

import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

public class QMClassActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView userId;
    private TextView name;
    private ImageView share;
    private ImageView set;
    private RadioGroup radioGroup;
    private EditText roomid;
    private LinearLayout classBegins;
    private TextView courseName;
    private TextView courseStatus;
    private EditText teacherPwd;
    private EditText studentPwd;
    private LinearLayout teacherBn;
    private TextView teacherEstablish;
    private TextView teacherJoin;
    private LinearLayout studentJoin;
    private LinearLayout historyCourse;
    private boolean isHaveSPwd=false;
    private boolean isHaveTPwd=false;
    private QMClassManager qmClassManager;
    private int courseSta=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_qmclass);

        qmClassManager= BaseApp.getQMClassManager();

        userId= (TextView)findViewById(R.id.userId);
        name= (TextView)findViewById(R.id.name);
        share= (ImageView)findViewById(R.id.share);
        share.setOnClickListener(this);
        set= (ImageView)findViewById(R.id.set);
        set.setOnClickListener(this);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        roomid= (EditText)findViewById(R.id.roomid);
        classBegins= (LinearLayout)findViewById(R.id.classBegins);
        courseName=(TextView) findViewById(R.id.courseName);
        courseStatus=(TextView) findViewById(R.id.courseStatus);
        teacherEstablish=(TextView) findViewById(R.id.teacher_establish);
        teacherEstablish.setOnClickListener(this);
        teacherJoin=(TextView) findViewById(R.id.teacher_join);
        teacherJoin.setOnClickListener(this);
        teacherBn= (LinearLayout)findViewById(R.id.teacher_bn);
        teacherPwd= (EditText)findViewById(R.id.teacherPwd);
        studentPwd= (EditText)findViewById(R.id.studentPwd);
        studentJoin= (LinearLayout)findViewById(R.id.student_join);
        historyCourse= (LinearLayout)findViewById(R.id.historyCourse);
        historyCourse.setOnClickListener(this);

        classBegins.setVisibility(View.GONE);
        teacherPwd.setVisibility(View.GONE);
        teacherBn.setVisibility(View.VISIBLE);
        studentJoin.setVisibility(View.GONE);
        studentPwd.setVisibility(View.GONE);
        roomid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length()==8){
                    getCourseParam(Integer.valueOf(editable.toString().trim()));
                }else {
                    courseSta=-1;
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.tradio:
                        teacherBn.setVisibility(View.VISIBLE);
                        studentJoin.setVisibility(View.GONE);
                        studentPwd.setVisibility(View.GONE);
                        if (isHaveTPwd){
                            teacherPwd.setVisibility(View.VISIBLE);
                        }else {
                            teacherPwd.setVisibility(View.GONE);
                        }
                        SharedPreferencesUtils.putData("userRole","t");
                        break;
                    case R.id.sradio:
                        teacherBn.setVisibility(View.GONE);
                        studentJoin.setVisibility(View.VISIBLE);
                        teacherPwd.setVisibility(View.GONE);
                        if (isHaveSPwd){
                            studentPwd.setVisibility(View.VISIBLE);
                        }else {
                            studentPwd.setVisibility(View.GONE);
                        }
                        SharedPreferencesUtils.putData("userRole","s");
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.teacher_establish) {

            Intent intent=new Intent(this, CreateCourseActivity.class);
            startActivityForResult(intent,1);

        } else if (view.getId() ==R.id.teacher_join) {
            joinCourse();
        }else if (view.getId() ==R.id.student_join){
            joinCourse();
        }else if (view.getId() ==R.id.share){
            Intent intent=new Intent(this, ShareActivity.class);
            startActivityForResult(intent,2);
        }else if (view.getId() ==R.id.set){
            Intent intent=new Intent(this, SetActivity.class);
            startActivityForResult(intent,3);
        }else if (view.getId() ==R.id.historyCourse){
            Intent intent=new Intent(this, HistoryCourseActivity.class);
            startActivity(intent);
        }
    }

    private void getCourseParam(int courseId){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/lvbcourse/getCourseParam?courseId="+courseId, new MyCallBack<BaseResponse<JSONObject>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<JSONObject> result) {
                if (result.getCode()==200){
//                    JSONObject courseParam = JSON.parseObject(result.getData());
                    JSONObject courseParam = result.getData();
                    SharedPreferencesUtils.putData("courseId",courseParam.getInteger("id"));
                    classBegins.setVisibility(View.VISIBLE);
                    courseName.setText(courseParam.get("courseName").toString());
                    courseStatus.setVisibility(View.VISIBLE);
                    if (courseParam.getInteger("status")==0){
                        courseSta=0;
                        courseStatus.setText("未开始");
                    }else if (courseParam.getInteger("status")==1){
                        courseSta=1;
                        courseStatus.setText("上课中");
                    }else {
                        courseSta=courseParam.getInteger("status");
                        courseStatus.setText("已结束");
                    }
                    if(courseParam.get("teacherPwd").toString().equals("")||courseParam.get("teacherPwd").toString()==null){
                        isHaveTPwd=false;
                        teacherPwd.setVisibility(View.GONE);
                    }else {
                        isHaveTPwd=true;
                        teacherPwd.setVisibility(View.VISIBLE);
                    }
                    if(courseParam.get("studentPwd").toString().equals("")||courseParam.get("studentPwd").toString()==null){
                        isHaveSPwd=false;
                    }else {
                        isHaveSPwd=true;
                    }

                }else {
                    courseSta=-1;
                    classBegins.setVisibility(View.VISIBLE);
                    courseName.setText(result.getMsg());
                    courseStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
    }

    private void joinCourse(){
        int courseId=SharedPreferencesUtils.getInt("courseId",-1);
        if (courseSta==1||courseSta==0){
            registUser(courseId);
        }else if (courseSta==-1){
            ToastUtil.showToast1(QMClassActivity.this,"","此课程不存在");
        }else {
            ToastUtil.showToast1(QMClassActivity.this,"","此课程已结束");
        }
    }

    private void registUser(int courseId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("role", SharedPreferencesUtils.getData("userRole","t"));
        if (SharedPreferencesUtils.getData("userRole","t").equals("t")){
            map.put("password", teacherPwd.getText().toString());
        }else {
            map.put("password", studentPwd.getText().toString());
        }
        map.put("originExpValue", "4");
        map.put("nickName", SharedPreferencesUtils.getString("nickName",""));
        map.put("deviceToken", SharedPreferencesUtils.getString("deviceToken",""));
        map.put("avatarUrl",SharedPreferencesUtils.getString("avatarUrl",""));
        map.put("studyCoin", "0");
        map.put("userId", Integer.valueOf(SharedPreferencesUtils.getString("userId","")));
        String jsonObject=new JSONObject(map).toString();

        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/member/registUser",jsonObject,new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result.getCode()==200){
                    qmClassManager.createOrJoinClassroom(result.getData().toString());
                }else {
                    ToastUtil.showToast1(QMClassActivity.this,"",result.getMsg());
                }

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onError(Response response) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferencesUtils.getString("userId","").equals("")){
            userId.setVisibility(View.INVISIBLE);
        }else {
            userId.setVisibility(View.VISIBLE);
            userId.setText("用户ID："+SharedPreferencesUtils.getString("userId",""));
        }
        if (SharedPreferencesUtils.getString("nickName","").equals("")){
            name.setVisibility(View.INVISIBLE);
        }else {
            name.setVisibility(View.VISIBLE);
            name.setText("(昵称："+SharedPreferencesUtils.getString("nickName","")+")");
        }
        if (SharedPreferencesUtils.getInt("courseId",-1)!=-1){
            roomid.setText(String.valueOf(SharedPreferencesUtils.getInt("courseId",-1)));
        }else {
            classBegins.setVisibility(View.GONE);
            roomid.setText("");
        }

    }

    @Override
    protected void onDestroy() {
        Log.e("生命周期","onDestroy");
        super.onDestroy();
    }


}