package com.qm.qmdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmdemo.BuildConfig;
import com.qm.qmdemo.R;
import com.qm.qmdemo.okhttp.BaseResponse;
import com.qm.qmdemo.okhttp.MyCallBack;
import com.qm.qmdemo.okhttp.OkHttpUtils;
import com.qm.qmdemo.utils.RoundImageView;
import com.qm.qmdemo.utils.SharedPreferencesUtils;
import com.qm.qmdemo.utils.ToastUtil;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

public class SetActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private LinearLayout llIcon;
    private RoundImageView icon;
    private TextView userId;
    private EditText nickname;
    private EditText appId;
    private EditText key;
    private LinearLayout test;
    private LinearLayout determine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set);
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        llIcon=(LinearLayout) findViewById(R.id.ll_icon);
        llIcon.setOnClickListener(this);
        icon=(RoundImageView) findViewById(R.id.icon);
        userId=(TextView) findViewById(R.id.userId);
        nickname=(EditText) findViewById(R.id.nickname);
        appId=(EditText) findViewById(R.id.appId);
        key=(EditText) findViewById(R.id.key);
        test=(LinearLayout) findViewById(R.id.test);
        test.setOnClickListener(this);
        determine=(LinearLayout) findViewById(R.id.determine);
        determine.setOnClickListener(this);
        if (!SharedPreferencesUtils.getBoolean("isFirstLogin",true)){
            userId.setText(SharedPreferencesUtils.getString("userId",""));
            nickname.setText(SharedPreferencesUtils.getString("nickName",""));
            appId.setText(SharedPreferencesUtils.getString("appId",""));
            key.setText(SharedPreferencesUtils.getString("appSecret",""));
            back.setVisibility(View.VISIBLE);
        }else {
            userId.setText(getNonceStr());
            nickname.setText("未命名");
            appId.setText("RMGF2JD1UY");
            key.setText("8OCFTPB1F7");
            back.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedPreferencesUtils.getString("avatarUrl","").equals("")){
            Glide.with(SetActivity.this).load(SharedPreferencesUtils.getString("avatarUrl","")).skipMemoryCache(true).into(icon);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }else if (view.getId() == R.id.ll_icon){
            Intent intent=new Intent(this, IconActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.test){
            String mappId=appId.getText().toString();
            String mkey=key.getText().toString();
            if (mappId!=null&&!mappId.equals("")){
                if (mkey!=null&&!mkey.equals("")){
                    getAppInfo(mappId,mkey);
                }else {
                    ToastUtil.showToast1(this,"","密钥不能为空");
                }
            }else {
                ToastUtil.showToast1(this,"","应用ID不能为空");
            }
        }else if (view.getId() == R.id.determine){
            SharedPreferencesUtils.putData("userId",userId.getText().toString());
            if (nickname.getText().toString()!=null&&!nickname.getText().toString().equals("")){
                SharedPreferencesUtils.putData("nickName",nickname.getText().toString());
            }else {
                SharedPreferencesUtils.putData("nickName","未命名");
            }
            if (SharedPreferencesUtils.getBoolean("isHaveAPP",false)){
                if (!SharedPreferencesUtils.getBoolean("isFirstLogin",true)){
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                }else {
                    Intent intent=new Intent(this, QMClassActivity.class);
                    startActivity(intent);
                }
                SharedPreferencesUtils.putData("isFirstLogin",false);
                finish();
            }else {
                ToastUtil.showToast1(this,"","请验证APP是否存在");
            }

        }
    }
    private void getAppInfo(String mappId,String appSecret){
        HashMap<String, Object> map = new HashMap<>();
        map.put("appId", mappId);
        map.put("appSecret", appSecret);
        String jsonObject=new JSONObject(map).toString();

        OkHttpUtils.getInstance().PostWithJson(BuildConfig.SERVER_URL+"/app/getAppInfo",jsonObject,new MyCallBack<BaseResponse>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse result) {
                if (result.getCode()==200){
                    SharedPreferencesUtils.putData("appId",appId.getText().toString());
                    SharedPreferencesUtils.putData("appSecret",key.getText().toString());
                    SharedPreferencesUtils.putData("isHaveAPP",true);
                    ToastUtil.showToast1(SetActivity.this,"","测试成功");
                }else if (result.getCode()==500){
                    SharedPreferencesUtils.putData("isHaveAPP",false);
                    ToastUtil.showToast1(SetActivity.this,"",result.getMsg());
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

    /**
     * 生成8位随机数
     * @return
     */
    public String getNonceStr() {
        String SYMBOLS = "0123456789";
        Random RANDOM = new SecureRandom();
        char[] nonceChars = new char[8];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }
}