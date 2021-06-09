package com.qm.qmdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qm.qmdemo.adpter.PartAdpter;
import com.qm.qmdemo.R;
import com.qm.qmdemo.model.CourseVideoInfo;
import com.qm.qmdemo.utils.RoundImageView;

import java.util.List;

import cn.jzvd.JzvdStd;


public class VideoActivity extends AppCompatActivity {
    private ImageView back;
    private JzvdStd videoplayer;
    private TextView coursename;
    private RoundImageView icon;
    private TextView teacherName;
    private GridView gridView;
    private PartAdpter partAdpter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_video);
        CourseVideoInfo courseVideoInfo=(CourseVideoInfo)getIntent().getSerializableExtra("CourseVideoInfo");
        back=(ImageView) findViewById(R.id.back);
        videoplayer = (JzvdStd) findViewById(R.id.videoplayer);
        coursename=(TextView) findViewById(R.id.coursename);
        icon=(RoundImageView) findViewById(R.id.icon);
        teacherName=(TextView) findViewById(R.id.teacher_name);
        gridView=(GridView) findViewById(R.id.gridView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        videoplayer.setUp(courseVideoInfo.getVideoInfos().get(0).getVideoUrl(), "");
        videoplayer.startVideo();
        coursename.setText(courseVideoInfo.getCourseName());
        Glide.with(this).load(courseVideoInfo.getAvatarUrl()).skipMemoryCache(true).into(icon);
        teacherName.setText(courseVideoInfo.getNickName());
        List<CourseVideoInfo.VideoInfos> list= courseVideoInfo.getVideoInfos();
        partAdpter=new PartAdpter(this, list);
        gridView.setAdapter(partAdpter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                videoplayer.releaseAllVideos();
                videoplayer.setUp(courseVideoInfo.getVideoInfos().get(position).getVideoUrl(), "");
                videoplayer.startVideo();
                partAdpter.refresh(position);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (videoplayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        videoplayer.releaseAllVideos();
    }

}