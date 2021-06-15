package com.qm.qmdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.qm.qmdemo.BuildConfig;
import com.qm.qmdemo.adpter.IconAdpter;
import com.qm.qmdemo.R;
import com.qm.qmdemo.okhttp.BaseResponse;
import com.qm.qmdemo.okhttp.MyCallBack;
import com.qm.qmdemo.okhttp.OkHttpUtils;

import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
/**
 * 头像
 */
public class IconActivity extends AppCompatActivity {

    private ImageView back;
    private GridView gridView;
    private IconAdpter iconAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_icon);
        back=(ImageView) findViewById(R.id.back);
        gridView=(GridView) findViewById(R.id.gridView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getAvatarUrlList();
    }
    private void getAvatarUrlList(){
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/member/getAvatarUrlList", new MyCallBack<BaseResponse<List<String>>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<List<String>> result) {
                if (result.getCode()==200){
                    iconAdpter=new IconAdpter(IconActivity.this, result.getData());
                    gridView.setAdapter(iconAdpter);
                }else {
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