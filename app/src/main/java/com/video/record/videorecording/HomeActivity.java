package com.video.record.videorecording;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFmpegExecution;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.video.record.videorecording.Helper.CallAPI;
import com.video.record.videorecording.Helper.Constant;
import com.video.record.videorecording.Helper.GetAPIkey;
import com.video.record.videorecording.Helper.SharedHelper;
import com.video.record.videorecording.Utils.FileUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static private AdvanceDrawerLayout drawer;
    Toolbar toolbar;
    CardView card_record_video,card_select_music,card_select_frame,card_send_massage,card_share_videos;
    TextView txt_select_music,txt_select_frame;
    String video_path = "",audio_path = "",image_path = "";
    String time;
    boolean frame = false;
    String date = "";
    String mix_audio_video_path = "",video_path_5_sec = "",rev_video_path = "";
    RelativeLayout rel_loader;

    CardView card_my_profile,card_subscription,card_privacy_policy,card_contact_us,card_share_app,card_logout;

    String save_file_path =  "";
    String string_api_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.white));// set status background white
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/
        Window window = HomeActivity.this.getWindow();
        Drawable background = HomeActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(HomeActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(HomeActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        checkPermission();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            save_file_path  = "/data/user/0/com.video.record.videorecording/";
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            save_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
            //below android 11=======
        }
        getApiKey();

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        card_record_video = findViewById(R.id.card_record_video);
        card_select_music = findViewById(R.id.card_select_music);
        card_select_frame = findViewById(R.id.card_select_frame);
        card_send_massage = findViewById(R.id.card_send_massage);
        card_share_videos = findViewById(R.id.card_share_videos);
        txt_select_music = findViewById(R.id.txt_select_music);
        txt_select_music.setSelected(true);
        txt_select_frame = findViewById(R.id.txt_select_frame);
        txt_select_frame.setSelected(true);
        rel_loader = findViewById(R.id.rel_loader);

        card_my_profile = findViewById(R.id.card_my_profile);
        card_subscription = findViewById(R.id.card_subscription);
        card_privacy_policy = findViewById(R.id.card_privacy_policy);
        card_contact_us = findViewById(R.id.card_contact_us);
        card_share_app = findViewById(R.id.card_share_app);
        card_logout = findViewById(R.id.card_logout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.setViewScale(Gravity.START, 0.8f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 80);

        if (!SharedHelper.getKey(HomeActivity.this,Constant.AUDIO_PATH).isEmpty()){
            File dir = new File(SharedHelper.getKey(HomeActivity.this,Constant.AUDIO_PATH));
            if(!dir.exists()){
                audio_path = "";
                txt_select_music.setText("Select Music");
            }
            else {
                audio_path = SharedHelper.getKey(HomeActivity.this,Constant.AUDIO_PATH);
                txt_select_music.setText(audio_path.substring(audio_path.lastIndexOf("/") + 1));
            }
        }

        if (!SharedHelper.getKey(HomeActivity.this,Constant.IMAGE_PATH).isEmpty()){
            File dir = new File(SharedHelper.getKey(HomeActivity.this,Constant.IMAGE_PATH));
            if(!dir.exists()){
                image_path = "";
                txt_select_frame.setText("Select Frame");
            }
            else {
                image_path = SharedHelper.getKey(HomeActivity.this,Constant.IMAGE_PATH);
                txt_select_frame.setText(image_path.substring(image_path.lastIndexOf("/") + 1));
            }
        }

        card_record_video.setOnClickListener(view -> {

            SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yy hh:mm:ss a", Locale.getDefault());
            date=sdf.format(new Date());

            if (audio_path.isEmpty()){
                Dialog dialog = new Dialog(this, R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_select_music_error);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                ImageView img_close = dialog.findViewById(R.id.img_close);

                img_close.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });

            }
            else {
                Dialog dialog = new Dialog(this, R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_record_video);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                ImageView img_close = dialog.findViewById(R.id.img_close);
                CheckBox chk_box_frame = dialog.findViewById(R.id.chk_box_frame);
                CardView card_create_video = dialog.findViewById(R.id.card_create_video);
                Spinner spin_time = dialog.findViewById(R.id.spin_time);
                ArrayList<String> time_array = new ArrayList<>();

                time_array.clear();
                time_array.add("5s");
                time_array.add("10s");
                time = "7";

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.optionitem, time_array);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_time.setAdapter(dataAdapter);

                spin_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {

                            time = time_array.get(position);
                            if (time.equals("5s")){
                                time = "7";
                            }
                            else if (time.equals("10s")){
                                time = "12";
                            }

                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                img_close.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });

                card_create_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (chk_box_frame.isChecked()){
                            if (image_path.isEmpty()){
                                Dialog dialog1 = new Dialog(HomeActivity.this, R.style.CustomDialog);
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog1.setContentView(R.layout.dialog_select_frame_error);
                                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog1.show();

                                ImageView img_close = dialog1.findViewById(R.id.img_close);

                                img_close.setOnClickListener(view1 -> {
                                    dialog1.dismiss();
                                });
                            }
                            else {
                                frame = true;
                                /*SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yy hh:mm:ss a", Locale.getDefault());
                                date=sdf.format(new Date());*/

                                if (SharedHelper.getKey(HomeActivity.this,Constant.IS_SUBSCRIBED).equals("true")){
                                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,Integer.parseInt(time));
                                    intent.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 1280);
                                    intent.putExtra(MediaStore.Video.Thumbnails.WIDTH, 720);
                                    startActivityForResult(intent,1);
                                }
                                else {
                                    //if no subscription code here :-
                                    Toast.makeText(HomeActivity.this, "First get Subscription", Toast.LENGTH_SHORT).show();
                                }
                                //add music with frame code here...
                            }
                        }
                        else {
                            frame = false;
                            /*SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yy hh:mm:ss a", Locale.getDefault());
                            date=sdf.format(new Date());*/

                            if (SharedHelper.getKey(HomeActivity.this,Constant.IS_SUBSCRIBED).equals("true")){
                                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,Integer.parseInt(time));
                                intent.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 1280);
                                intent.putExtra(MediaStore.Video.Thumbnails.WIDTH, 720);
                                startActivityForResult(intent,1);
                            }
                            else {
                                //if no subscription code here :-
                                Toast.makeText(HomeActivity.this, "First get Subscription", Toast.LENGTH_SHORT).show();
                            }
                            //add music only code here...
                        }
                    }
                });

            }
        });

        card_select_music.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            intent.setType("audio/*");
            startActivityForResult(intent, 10);
        });

        card_select_frame.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            startActivityForResult(intent1, 20);
        });

        card_send_massage.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,sendActivity.class));
            finish();
        });

        card_share_videos.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,MyVideosActivity.class));
            finish();
        });

        //*********************************************navigation menu************************************************
        card_my_profile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
            finish();
        });

        card_logout.setOnClickListener(view -> {
            SharedHelper.clearSharedPreferences(HomeActivity.this);
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            finish();
        });

        card_subscription.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,SubscriptionActivity.class));
            finish();
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handling navigation view item clicks here.
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //****************************************
        if(requestCode==10 && data != null){
            if (resultCode == RESULT_OK){
                if (requestCode == 10){
                    Uri audio_uri = data.getData();
                    audio_path = FileUtility.getPath(HomeActivity.this,audio_uri);
                    SharedHelper.putKey(HomeActivity.this, Constant.AUDIO_PATH,audio_path);
                    txt_select_music.setText(audio_path.substring(audio_path.lastIndexOf("/") + 1));
                    Log.e("show_audio_path",audio_path);
                }
            }
        }
        else if (requestCode==20 && data != null){
            if (resultCode == RESULT_OK) {
                if (requestCode == 20){
                    Uri selectedImage = data.getData();
                    String path = FileUtility.getPath(HomeActivity.this,selectedImage);
                    if (path != null){
                        image_path = path;
                        SharedHelper.putKey(HomeActivity.this, Constant.IMAGE_PATH,image_path);
                        txt_select_frame.setText(image_path.substring(image_path.lastIndexOf("/") + 1));
                    }
                }
            }
        }
        else if(requestCode==1 && data != null){
            if (resultCode == RESULT_OK) {
                if (requestCode == 1){
                    Uri video_uri = data.getData();
                    video_path = FileUtility.getPath(HomeActivity.this,video_uri);
                    //video_name = video_path.substring(video_path.lastIndexOf("/") + 1);
                    Log.e("show_video_path",video_path);
                    merge();
                }
            }
        }
        //*****************************
    }

    private void merge(){
        //String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
        String relativeLocation = save_file_path;
        File dirs = new File(relativeLocation);
        if(!dirs.exists())
        {
            dirs.mkdir();
        }

        String[] c = {"-i",video_path,
                "-filter_complex","[0:v]reverse,fifo[r];[0:v][r] concat=n=2:v=1 [v]",
                "-map","[v]",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date + ".mp4"};
        /*String[] c = {"-i",video_path,"-vf","reverse",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date + ".mp4"};*/
        video_path_5_sec = dirs.getPath()+File.separator + date + ".mp4";
        rev_video_path = dirs.getPath()+File.separator + date + ".mp4";
        mergeVideo(c);
        rel_loader.setVisibility(View.VISIBLE);
    }

    private void mergeVideo(String[] co){
        FFmpeg.executeAsync(co, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                Log.e("hello", "Merge_return : "+returnCode);
                Log.e("hello","Merge_executionID : "+executionId);
                Log.e("hello","Merge_FFMPG : "+new FFmpegExecution(executionId,co));
                File fdelete = new File(video_path);
                if (fdelete.exists()) {
                    fdelete.delete();
                    if (time.equals("7")){
                        merge1();
                        time = "";
                    }
                    else {
                        addMusic();
                    }
                }
            }
        });
    }

    private void merge1(){
        rev_video_path = "";
        //String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
        String relativeLocation = save_file_path;
        File dirs = new File(relativeLocation);
        if(!dirs.exists())
        {
            dirs.mkdir();
        }

        String[] c = {"-i",video_path_5_sec,
                "-filter_complex","[0:v]reverse,fifo[r];[0:v][r] concat=n=2:v=1 [v]",
                "-map","[v]",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date + "1" + ".mp4"};

        /*String[] c = {"-i",video_path_5_sec,"-i",video_path_5_sec,
                "-filter_complex","[0:v] [0:a] [1:v] [1:a] [2:v] [2:a] concat=n=3:v=1:a=1 [vv] [aa]",
                "-map","[vv]","-map","[aa]",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date + "1" + ".mp4"};*/
        rev_video_path = dirs.getPath()+File.separator + date + "1" + ".mp4";
        mergeVideo1(c);
    }

    private void mergeVideo1(String[] co){
        FFmpeg.executeAsync(co, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                Log.e("hello", "Merge1_return : "+returnCode);
                Log.e("hello","Merge1_executionID : "+executionId);
                Log.e("hello","Merge1_FFMPG : "+new FFmpegExecution(executionId,co));
                File fdelete = new File(video_path_5_sec);
                if (fdelete.exists()) {
                    fdelete.delete();

                    addMusic();

                }
            }

            //"receive call"
        });
    }

    private void addMusic(){
        //String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
        String relativeLocation = save_file_path;
        File dirs = new File(relativeLocation);
        if(!dirs.exists())
        {
            dirs.mkdir();
        }

        String[] c = {"-i",rev_video_path,
                "-i",audio_path,"-c:v","copy","-c:a","aac","-map","0:v:0","-map","1:a:0","-shortest",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date + "2" + ".mp4"};
        mix_audio_video_path = dirs.getPath()+File.separator + date + "2" + ".mp4";
        mergeVideo2(c);
        rel_loader.setVisibility(View.VISIBLE);
    }

    private void mergeVideo2(String[] co){
        FFmpeg.executeAsync(co, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                Log.e("hello", "Add_Music_return : "+returnCode);
                Log.e("hello","Add_Music_executionID : "+executionId);
                Log.e("hello","Add_Music_FFMPG : "+new FFmpegExecution(executionId,co));
                File fdelete = new File(rev_video_path);
                if (fdelete.exists()) {
                    fdelete.delete();
                    if (frame){
                        //code add frame
                        addLogo();
                    }
                    else {
                        rel_loader.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Saved Video!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this,sendActivity.class);
                        startActivity(intent);
                        finish();
                        //saveVideo();
                        /*moveFile(save_file_path,
                                "/"+mix_audio_video_path.substring(mix_audio_video_path.lastIndexOf("/") + 1),
                                save_file_path+File.separator+"Saved Video");*/
                    }
                }
            }
        });
    }

    private void addLogo(){
        //String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
        String relativeLocation = save_file_path;
        File dirs = new File(relativeLocation);
        if(!dirs.exists())
        {
            dirs.mkdir();
        }

        String[] c = {"-i",mix_audio_video_path,
                "-i",SharedHelper.getKey(HomeActivity.this,Constant.IMAGE_PATH),"-filter_complex",
                "[0]scale=720:1280:force_original_aspect_ratio=increase,pad=720:1280:(ow-iw)/2:(oh-ih)/2,setsar=1[backd],[1]scale=720x1280[scaled_image],[backd][scaled_image]overlay",
                "-b","10000k",
                "-maxrate","10000k",
                dirs.getPath()+File.separator + date+ "3" + ".mp4"};
        mergeVideoFrame(c);
    }

    private void mergeVideoFrame(String[] co){
        FFmpeg.executeAsync(co, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                Log.e("hello", "Add_Frame_return : "+returnCode);
                Log.e("hello","Add_Frame_executionID : "+executionId);
                Log.e("hello","Add_Frame_FFMPG : "+new FFmpegExecution(executionId,co));

                File fdelete = new File(mix_audio_video_path);
                if (fdelete.exists()) {
                    fdelete.delete();
                    rel_loader.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Saved Video!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this,sendActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveVideo(){
        //String relativeLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"MusicalVideo";
        String relativeLocation = save_file_path;
        File dirs = new File(relativeLocation);
        if(!dirs.exists())
        {
            dirs.mkdir();
        }

        String[] c = {"-i",mix_audio_video_path,
                "-c:a","libvorbis",
                dirs.getPath()+File.separator + date+ "4" + ".mp4"};
        mergeSaveVideo(c);
    }

    private void mergeSaveVideo(String[] co){
        FFmpeg.executeAsync(co, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                Log.e("hello", "Add_Frame_return : "+returnCode);
                Log.e("hello","Add_Frame_executionID : "+executionId);
                Log.e("hello","Add_Frame_FFMPG : "+new FFmpegExecution(executionId,co));

                File fdelete = new File(mix_audio_video_path);
                if (fdelete.exists()) {
                    fdelete.delete();
                    rel_loader.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Saved Video!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this,sendActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public boolean checkPermission() {
        boolean isExternalStrage = false, readExternal = false;

        try {
            int hasWriteContactsPermission = checkSelfPermission(WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                isExternalStrage = false;
            } else {
                isExternalStrage = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isExternalStrage = true;
        }

        try {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                readExternal = false;
            } else {
                readExternal = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            readExternal = true;
        }


        if (!isExternalStrage || !readExternal)
        {
            this.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 007);
        }


        if (isExternalStrage && readExternal) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void callApi(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("user_id", SharedHelper.getKey(this,Constant.USER_ID));
        }catch (Exception e) {
            e.printStackTrace();
        }
        CallAPI.callingAPI(HomeActivity.this, 1, "check_user", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length();i++){
                        JSONObject obj = array.getJSONObject(0);
                        SharedHelper.putKey(HomeActivity.this,Constant.VALID_TILL,obj.getString("valid_till"));
                    }
                    convertdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    public void getApiKey(){
        GetAPIkey.callGetAPI(HomeActivity.this, new GetAPIkey.OnGetKeyResponseListener() {
            @Override
            public void onSuccess(String api_key) {
                string_api_key = api_key;

                callApi();
                //Toast.makeText(HomeActivity.this, api_key, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveFile(String inputPath,String inputFile,String outputPath){

        InputStream in = null;
        OutputStream out = null;

        try {
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();

            Toast.makeText(HomeActivity.this, "Saved Video!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this,sendActivity.class);
            startActivity(intent);
            finish();

        }
        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public void convertdate() {
        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDatestarting = dateFormat.format(current_date);

        //String sd =SharedHelper.getKey(HomeActivity.this,ConstantVariables.START_DATE) ;
        String ed =SharedHelper.getKey(HomeActivity.this,Constant.VALID_TILL) ;


        //Date starting_date = new Date();
        Date end_date = new Date();

        Log.e("cusrrentdateisss",formattedDatestarting);
        try
        {
            //starting_date = dateFormat.parse(sd);
            end_date = dateFormat.parse(ed);
            Log.e("done","doneeeeeeee");
            current_date=dateFormat.parse(formattedDatestarting);
            if(current_date.getTime() <= end_date.getTime())
            {
                Toast.makeText(this, "active", Toast.LENGTH_SHORT).show();
                SharedHelper.putKey(HomeActivity.this,Constant.IS_SUBSCRIBED,"true");
            }
            else
            {
                Toast.makeText(this, "deactive", Toast.LENGTH_SHORT).show();
                SharedHelper.putKey(HomeActivity.this,Constant.IS_SUBSCRIBED,"false");
            }
            Log.e("last executed","ok");
        }
        catch (ParseException e) {
            Log.e("exceptionissssss",String.valueOf(e));
            e.printStackTrace();
        }
    }

}