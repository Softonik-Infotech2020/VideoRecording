package com.video.record.videorecording;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.video.record.videorecording.Helper.Constant;
import com.video.record.videorecording.Helper.SharedHelper;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalce_screen);
        Window window = SplashScreen.this.getWindow();
        Drawable background = SplashScreen.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(SplashScreen.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(SplashScreen.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {

                if (SharedHelper.getKey(SplashScreen.this, Constant.IS_LOGIN).equalsIgnoreCase("1")){
                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                    finish();
                }
                else {
                    Intent mainIntent = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, 3000);

    }
}