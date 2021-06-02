package com.qm.qmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qm.qmclass.okhttp.BaseResponse;
import com.qm.qmclass.okhttp.MyCallBack;
import com.qm.qmclass.okhttp.OkHttpUtils;
import com.qm.qmclass.qmmanager.QMClassManager;
import com.qm.qmclass.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;

public class QMClassActivity extends AppCompatActivity implements View.OnClickListener {
    private String baseurl="http://jledu.f3322.net:9208/live";
    private ImageView share;
    private ImageView set;
    private RadioGroup radioGroup;
    private EditText roomid;
    private LinearLayout classBegins;
    private EditText password;
    private LinearLayout teacherBn;
    private TextView teacherEstablish;
    private TextView teacherJoin;
    private LinearLayout studentJoin;
    private LinearLayout historyCourse;
    private String role="t";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_qmclass);
        share= (ImageView)findViewById(R.id.share);
        share.setOnClickListener(this);
        set= (ImageView)findViewById(R.id.set);
        set.setOnClickListener(this);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        roomid= (EditText)findViewById(R.id.roomid);
        classBegins= (LinearLayout)findViewById(R.id.classBegins);
        teacherEstablish=(TextView) findViewById(R.id.teacher_establish);
        teacherEstablish.setOnClickListener(this);
        teacherJoin=(TextView) findViewById(R.id.teacher_join);
        teacherJoin.setOnClickListener(this);
        teacherBn= (LinearLayout)findViewById(R.id.teacher_bn);
        password= (EditText)findViewById(R.id.password);
        studentJoin= (LinearLayout)findViewById(R.id.student_join);
        historyCourse= (LinearLayout)findViewById(R.id.historyCourse);

        classBegins.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        teacherBn.setVisibility(View.VISIBLE);
        studentJoin.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.tradio:
                        classBegins.setVisibility(View.VISIBLE);
                        password.setVisibility(View.VISIBLE);
                        teacherBn.setVisibility(View.VISIBLE);
                        studentJoin.setVisibility(View.GONE);
                        role="t";
                        break;
                    case R.id.sradio:
                        classBegins.setVisibility(View.GONE);
                        password.setVisibility(View.GONE);
                        teacherBn.setVisibility(View.GONE);
                        studentJoin.setVisibility(View.VISIBLE);
                        role="s";
                        break;
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.teacher_establish) {

            Intent intent=new Intent(this,CreateCourseActivity.class);
            startActivityForResult(intent,1);

        } else if (view.getId() ==R.id.teacher_join) {

        }else if (view.getId() ==R.id.share){

        }else if (view.getId() ==R.id.set){
            Intent intent=new Intent(this,SetActivity.class);
            startActivityForResult(intent,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Log.e("生命周期","MAIN-onDestroy");
        super.onDestroy();
    }


}