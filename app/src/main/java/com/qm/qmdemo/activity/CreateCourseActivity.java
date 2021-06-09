package com.qm.qmdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.qm.qmdemo.BuildConfig;
import com.qm.qmdemo.R;
import com.qm.qmdemo.okhttp.BaseResponse;
import com.qm.qmdemo.okhttp.MyCallBack;
import com.qm.qmdemo.okhttp.OkHttpUtils;
import com.qm.qmdemo.utils.SharedPreferencesUtils;
import com.qm.qmdemo.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

public class CreateCourseActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private EditText roomName;
    private EditText teacherPassWord;
    private EditText studentPassWord;
    private EditText classTime;
    private RadioGroup radioGroup;
    private LinearLayout createCourse;
    private int recordState=1;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_course);
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        roomName=(EditText) findViewById(R.id.roomName);
        teacherPassWord=(EditText) findViewById(R.id.teacherPassWord);
        studentPassWord=(EditText) findViewById(R.id.studentPassWord);
        classTime=(EditText) findViewById(R.id.classTime);
        createCourse=(LinearLayout) findViewById(R.id.createCourse);
        createCourse.setOnClickListener(this);
        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.recording:
                        recordState=1;
                        break;
                    case R.id.manualRecording:
                        recordState=2;
                        break;
                    case R.id.noRecording:
                        recordState=3;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        }else if (view.getId() == R.id.createCourse){
            String courseName=roomName.getText().toString();
            if (courseName==null||courseName.equals("")){
                ToastUtil.showToast1(CreateCourseActivity.this,"","课程名称不能为空");
                return;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("recMethod", recordState);
            String courseOptions=JSON.toJSONString(map);
            String teacherPwd=teacherPassWord.getText().toString();
            String studentPwd=studentPassWord.getText().toString();
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
            //获取当前时间
            Long currentTime=System.currentTimeMillis();
            String startTime=simpleDateFormat.format(new Date(currentTime));
            String endTime="";
            if (!classTime.getText().toString().equals("")&&classTime.getText().toString()!=null){
                endTime=simpleDateFormat.format(new Date(currentTime+Long.parseLong(classTime.getText().toString())*60000));
            }
            createCourse(courseName,courseOptions,startTime,endTime,studentPwd,teacherPwd);
        }
    }
    private void createCourse(String courseName,String courseOptions,String startTime,String endTime,String studentPwd,String teacherPwd){
        HashMap<String, Object> map = new HashMap<>();
        map.put("appId", SharedPreferencesUtils.getString("appId",""));
        map.put("courseName", courseName);
        map.put("courseOptions", JSONObject.parse(courseOptions));
        map.put("endTime",endTime);
        map.put("startTime", startTime);
        map.put("studentPwd", studentPwd);
        map.put("teacherPwd", teacherPwd);
        String jsonObject= JSON.toJSONString(map);

        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/lvbcourse/createCourse",jsonObject,new MyCallBack<BaseResponse<Integer>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<Integer> result) {
                if (result.getCode()==200) {
                    SharedPreferencesUtils.putData("courseId",result.getData());
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    ToastUtil.showToast1(CreateCourseActivity.this,"",result.getMsg());
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
}