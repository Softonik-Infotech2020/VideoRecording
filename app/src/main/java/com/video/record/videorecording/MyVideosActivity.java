package com.video.record.videorecording;

import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.video.record.videorecording.Adapters.MyVideoAdapter;
import com.video.record.videorecording.Model.VideoPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MyVideosActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager mlayoutManager;
    RecyclerView rec_video_list;
    ArrayList<VideoPath> videoPathArrayList = new ArrayList<>();
    MyVideoAdapter myVideoAdapter;

    ImageView img_back;
    LinearLayout ll_no_data_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_videos);
        Window window = MyVideosActivity.this.getWindow();
        Drawable background = MyVideosActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(MyVideosActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(MyVideosActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        rec_video_list = findViewById(R.id.rec_video_list);
        img_back = findViewById(R.id.img_back);
        ll_no_data_found = findViewById(R.id.ll_no_data_found);

        if (SDK_INT >= Build.VERSION_CODES.R){
            String save_file_path  = "/data/user/0/com.video.record.videorecording/";
            videofromfile(save_file_path);
        }
        else {
            String save_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
            videofromfile(save_file_path);
        }

        img_back.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void videofromfile(String path){

        File sdcardPath = new File(path);

        if (!sdcardPath.exists()) {
            boolean md = sdcardPath.mkdirs();
            if (!md) {

            }
        }

        int videoCount = sdcardPath.listFiles().length;

        if (videoCount > 0) {
            for (int count = 0; count < videoCount; count++) {
                VideoPath vp = new VideoPath();
                vp.setPath(sdcardPath.listFiles()[count].getAbsolutePath());
                if (sdcardPath.listFiles()[count].getAbsolutePath().contains(".mp4")) {
                    videoPathArrayList.add(vp);
                }
            }

            if (videoPathArrayList.size() > 0){
                Collections.reverse(videoPathArrayList);
                ll_no_data_found.setVisibility(View.GONE);
                rec_video_list.setVisibility(View.VISIBLE);

                myVideoAdapter = new MyVideoAdapter(MyVideosActivity.this, videoPathArrayList);
                mlayoutManager = new LinearLayoutManager(MyVideosActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_video_list.setLayoutManager(mlayoutManager);
                rec_video_list.setAdapter(myVideoAdapter);
            }
            else {
                rec_video_list.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }

        }
        else {
            rec_video_list.setVisibility(View.GONE);
            ll_no_data_found.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyVideosActivity.this,HomeActivity.class));
        finish();
    }

}