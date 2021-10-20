package com.qm.qmdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import com.qm.qmdemo.BuildConfig;
import com.qm.qmdemo.R;
import com.qm.qmdemo.adpter.VideoAdpter;
import com.qm.qmdemo.model.CourseVideoInfo;
import com.qm.qmdemo.okhttp.BaseResponse;
import com.qm.qmdemo.okhttp.MyCallBack;
import com.qm.qmdemo.okhttp.OkHttpUtils;
import com.qm.qmdemo.utils.SharedPreferencesUtils;

import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
/**
 * 历史课程
 */
public class HistoryCourseActivity extends AppCompatActivity {
    private ImageView back;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_history_course);
        back=(ImageView) findViewById(R.id.back);
        listView=(ListView) findViewById(R.id.listView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getCourseVideoList();
    }
    private void getCourseVideoList(){
        String userId= SharedPreferencesUtils.getString("userId","");
        String userRole=SharedPreferencesUtils.getString("userRole","t");
        OkHttpUtils.getInstance().Get(BuildConfig.SERVER_URL+"/member/getCourseVideoList?userId="+Integer.valueOf(userId)+"&userRole="+userRole, new MyCallBack<BaseResponse<List<CourseVideoInfo>>>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(BaseResponse<List<CourseVideoInfo>> result) {
                if (result.getCode()==200){
                    List<CourseVideoInfo>  videoInfoList=result.getData();
                    VideoAdpter videoAdpter =new VideoAdpter(HistoryCourseActivity.this, videoInfoList, new VideoAdpter.MyClickListener() {
                        @Override
                        public void myOnClick(int position, View v) {
                            Intent intent=new Intent(HistoryCourseActivity.this, VideoActivity.class);
                            intent.putExtra("CourseVideoInfo",videoInfoList.get(position));
                            startActivity(intent);
                        }
                    });
                    listView.setAdapter(videoAdpter);
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