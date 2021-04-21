package com.qm.qmdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.qmmanager.QMClassManager;
import com.qm.qmclass.qmmanager.QMClassManagerImpl;
import com.qm.qmclass.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String baseurl="http://jledu.f3322.net:9208/live";
    private Button join;
    private TextView verifycode;
    private EditText nickname;
    private EditText classname;
    private EditText phone;
    private EditText etVerifycode;
    private EditText teacherpd;
    private EditText studentpd;
    private EditText roomid;
    private RadioButton rbTeacher;
    private RadioButton rbStudent;
    private LinearLayout llRoom;
    private LinearLayout llNickname;
    private LinearLayout llClassname;
    private LinearLayout llPhone;
    private LinearLayout llTeacherpd;
    private LinearLayout llStudentpd;
    private LinearLayout llVerifycode;
    private QMClassManager qmClassManager;
    private String mobile;
    private String studentPwd;
    private String teacherPwd;
    private String role="t";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferencesUtils.getInstance(this,"qm_data");
        join=(Button) findViewById(R.id.join);
        verifycode=(TextView) findViewById(R.id.verifycode);
        nickname=(EditText) findViewById(R.id.nickname);
        classname=(EditText) findViewById(R.id.classname);
        phone=(EditText) findViewById(R.id.phone);
        etVerifycode=(EditText) findViewById(R.id.et_verifycode);
        teacherpd=(EditText) findViewById(R.id.teacherpd);
        studentpd=(EditText) findViewById(R.id.studentpd);
        roomid=(EditText) findViewById(R.id.roomid);
        rbTeacher=(RadioButton) findViewById(R.id.rb_teacher);
        rbStudent=(RadioButton) findViewById(R.id.rb_student);
        llRoom=(LinearLayout) findViewById(R.id.ll_room);
        llNickname=(LinearLayout) findViewById(R.id.ll_nickname);
        llClassname=(LinearLayout) findViewById(R.id.ll_classname);
        llPhone=(LinearLayout) findViewById(R.id.ll_phone);
        llTeacherpd=(LinearLayout) findViewById(R.id.ll_teacherpd);
        llStudentpd=(LinearLayout) findViewById(R.id.ll_studentpd);
        llVerifycode=(LinearLayout) findViewById(R.id.ll_verifycode);

        qmClassManager= QMClassManager.getInstance();
        qmClassManager.init(this);

        rbTeacher.setChecked(true);
        llRoom.setVisibility(View.GONE);
        RadioGroup rg = findViewById(R.id.rg_1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_teacher:
                        llRoom.setVisibility(View.GONE);
                        llNickname.setVisibility(View.VISIBLE);
                        llClassname.setVisibility(View.VISIBLE);
                        llPhone.setVisibility(View.VISIBLE);
                        llVerifycode.setVisibility(View.VISIBLE);
                        llTeacherpd.setVisibility(View.VISIBLE);
                        join.setText("创建直播间");
                        teacherPwd=teacherpd.getText().toString();
                        studentPwd=studentpd.getText().toString();
                        role="t";
                        break;
                    case R.id.rb_student:
                        llRoom.setVisibility(View.VISIBLE);
                        llNickname.setVisibility(View.VISIBLE);
                        llClassname.setVisibility(View.GONE);
                        llPhone.setVisibility(View.GONE);
                        llVerifycode.setVisibility(View.GONE);
                        llTeacherpd.setVisibility(View.GONE);
                        join.setText("进入直播间");
                        studentPwd=studentpd.getText().toString();
                        teacherPwd="";
                        role="s";
                        break;
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (role.equals("t")){
                    getappid();
                }else if (role.equals("s")){
                    if (roomid.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this,"请填写房间号",Toast.LENGTH_SHORT).show();
                    }else {
                        registUser(Integer.parseInt(roomid.getText().toString()));
                    }

                }
            }
        });
        verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile=phone.getText().toString();
                String vcType="live_app_register";
                if (mobile.equals("")){
                    Toast.makeText(MainActivity.this,"请填写手机号",Toast.LENGTH_SHORT).show();
                }else{
                    OkHttpUtils.getInstance().Get(baseurl+"/sms/getVerifyCode?mobile="+mobile+"&vcType="+vcType, new MyCallBack<BaseResponse>() {
                        @Override
                        public void onLoadingBefore(Request request) {

                        }

                        @Override
                        public void onSuccess(BaseResponse result) {

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
        });
    }

    private void getappid(){
        String code=etVerifycode.getText().toString();
        OkHttpUtils.getInstance().Get(baseurl+"/app/getAppId?mobile="+phone.getText().toString()+"&verifyCode="+code, new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result!=null&&result.getData()!=null){
                    createCourse(result.getData().toString());
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
    private void createCourse(String appid){
        String name=classname.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("appId", appid);
        map.put("courseName", name);
        map.put("courseOptions", null);
        map.put("endTime", "");
        map.put("startTime", "");
        map.put("studentPwd", studentPwd);
        map.put("teacherPwd", teacherPwd);
        String jsonObject=new JSONObject(map).toString();

        OkHttpUtils.getInstance().PostWithJson(baseurl+"/lvbcourse/createCourse",jsonObject,new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result!=null&&result.getData()!=null) {
                    registUser(Integer.parseInt(result.getData().toString()));
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
    private void registUser(int courseId){
        String name=nickname.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("role", role);
        if (role.equals("t")){
            map.put("password", teacherPwd);
        }else {
            map.put("password", studentPwd);
        }
        map.put("originExpValue", "4");
        map.put("nickName", name);
        map.put("deviceToken", "22");
        map.put("avatarUrl", "11111");
        String jsonObject=new JSONObject(map).toString();

        OkHttpUtils.getInstance().PostWithJson(baseurl+"/member/registUser",jsonObject,new MyCallBack<BaseResponse<String>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result!=null&&result.getData()!=null){
                    qmClassManager.createOrJoinClassroom(result.getData().toString());
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
    protected void onDestroy() {
        Log.e("生命周期","MAIN-onDestroy");
        super.onDestroy();
    }
}