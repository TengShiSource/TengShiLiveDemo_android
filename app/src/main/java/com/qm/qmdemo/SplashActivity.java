package com.qm.qmdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qm.qmclass.utils.SharedPreferencesUtils;

public class SplashActivity extends AppCompatActivity {
private ImageView ivQidong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        ivQidong=(ImageView) findViewById(R.id.iv_qidong);
        Glide.with(this).load(R.mipmap.qidong).skipMemoryCache(true).into(ivQidong);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedPreferencesUtils.getBoolean("isFirstLogin",true)){
                    Intent intent=new Intent(SplashActivity.this,SetActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent=new Intent(SplashActivity.this,QMClassActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}