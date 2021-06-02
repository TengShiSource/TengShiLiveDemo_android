package com.qm.qmdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set);
    }
}