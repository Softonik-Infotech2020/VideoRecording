package com.video.record.videorecording;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class sendActivity extends AppCompatActivity {

    ImageView img_back;
    EditText edt_mobile;
    CardView card_send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Window window = sendActivity.this.getWindow();
        Drawable background = sendActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(sendActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(sendActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        img_back = findViewById(R.id.img_back);
        edt_mobile = findViewById(R.id.edt_mobile);
        card_send_btn = findViewById(R.id.card_send_btn);

        card_send_btn.setOnClickListener(view -> {

            if (edt_mobile.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter mobile number!!", Toast.LENGTH_SHORT).show();
            }
            else if (edt_mobile.getText().toString().length() < 10){
                Toast.makeText(this, "Enter valid mobile number!!", Toast.LENGTH_SHORT).show();
            }
            else {
                String contact = "91"+edt_mobile.getText().toString();

                Intent sendMsg = new Intent(Intent.ACTION_VIEW);
                String url = "https://api.whatsapp.com/send?phone=" + contact + "&text=" + "Musical Video";
                sendMsg.setPackage("com.whatsapp");
                sendMsg.setData(Uri.parse(url));
                startActivity(sendMsg);
                /*try {
                    Intent sendMsg = new Intent(Intent.ACTION_VIEW);
                    String url = "https://api.whatsapp.com/send?phone=" + contact + "&text=" + "Musical Video";
                    sendMsg.setPackage("com.whatsapp");
                    sendMsg.setData(Uri.parse(url));
                    if (sendMsg.resolveActivity(getPackageManager()) != null) {
                        startActivity(sendMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        });

        img_back.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(sendActivity.this,HomeActivity.class));
        finish();
    }

}