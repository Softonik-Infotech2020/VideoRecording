package com.video.record.videorecording;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.video.record.videorecording.Helper.CallAPI;
import com.video.record.videorecording.Helper.Constant;
import com.video.record.videorecording.Helper.GetAPIkey;
import com.video.record.videorecording.Helper.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    ImageView img_back;
    EditText edt_user_name,edt_mobile_number,edt_email_address,edt_city;
    CardView card_submit;
    String string_api_key = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Window window = RegistrationActivity.this.getWindow();
        Drawable background = RegistrationActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(RegistrationActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(RegistrationActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        img_back = findViewById(R.id.img_back);
        edt_user_name = findViewById(R.id.edt_user_name);
        edt_mobile_number = findViewById(R.id.edt_mobile_number);
        edt_mobile_number.setEnabled(false);
        edt_email_address = findViewById(R.id.edt_email_address);
        edt_city =  findViewById(R.id.edt_city);
        card_submit = findViewById(R.id.card_submit);

        getApiKey();
        Intent intent = getIntent();
        if (intent.getStringExtra("MOBILE") != null){
            edt_mobile_number.setText(intent.getStringExtra("MOBILE"));
        }

        card_submit.setOnClickListener(view -> {
            if (edt_user_name.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter your user name!!", Toast.LENGTH_SHORT).show();
            }
            else if (edt_mobile_number.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter mobile number!", Toast.LENGTH_SHORT).show();
            }
            else if (edt_mobile_number.getText().toString().length() < 10){
                Toast.makeText(this, "Please enter valid mobile number!!", Toast.LENGTH_SHORT).show();
            }
            else if (edt_email_address.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter email address!!", Toast.LENGTH_SHORT).show();
            }
            else if (edt_city.getText().toString().isEmpty()){
                Toast.makeText(this, "Please enter your city!!", Toast.LENGTH_SHORT).show();
            }
            else {
                if (SharedHelper.getKey(RegistrationActivity.this,Constant.IS_LOGIN).equals("1")){
                    updateProfile();
                }
                else {
                    callApi();
                }
            }
        });

        img_back.setOnClickListener(view -> {
            onBackPressed();
        });
        
    }

    public void getApiKey(){
        GetAPIkey.callGetAPI(RegistrationActivity.this, new GetAPIkey.OnGetKeyResponseListener() {
            @Override
            public void onSuccess(String api_key) {
                //Toast.makeText(RegistrationActivity.this, api_key, Toast.LENGTH_SHORT).show();
                string_api_key = api_key;
                getProfile();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProfile(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("user_id", SharedHelper.getKey(RegistrationActivity.this,Constant.USER_ID));
        }catch (Exception e) {
            e.printStackTrace();
        }

        CallAPI.callingAPI(RegistrationActivity.this, 1, "check_user", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length();i++){
                        JSONObject obj = array.getJSONObject(0);
                        edt_user_name.setText(obj.getString("name"));
                        edt_mobile_number.setText(obj.getString("number").substring(3));
                        edt_email_address.setText(obj.getString("email"));
                        edt_city.setText(obj.getString("city"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void callApi(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("name", edt_user_name.getText().toString());
            jsonObject.put("email", edt_email_address.getText().toString());
            jsonObject.put("number", "+91"+edt_mobile_number.getText().toString());
            jsonObject.put("valid_till", "00-00-0000");
            jsonObject.put("city", edt_city.getText().toString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        CallAPI.callingAPI(RegistrationActivity.this, 1, "add_user", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length();i++){
                        JSONObject obj = array.getJSONObject(0);
                        SharedHelper.putKey(RegistrationActivity.this,Constant.USER_ID,obj.getString("id"));
                        SharedHelper.putKey(RegistrationActivity.this,Constant.NAME,obj.getString("name"));
                        SharedHelper.putKey(RegistrationActivity.this,Constant.EMAIL,obj.getString("email"));
                        SharedHelper.putKey(RegistrationActivity.this,Constant.NUMBER,obj.getString("number"));
                        SharedHelper.putKey(RegistrationActivity.this,Constant.VALID_TILL,obj.getString("valid_till"));
                        SharedHelper.putKey(RegistrationActivity.this,Constant.CITY,obj.getString("city"));
                    }

                    SharedHelper.putKey(RegistrationActivity.this, Constant.IS_LOGIN,"1");
                    startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String message) {
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateProfile(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("user_id", SharedHelper.getKey(RegistrationActivity.this,Constant.USER_ID));
            jsonObject.put("email", edt_email_address.getText().toString());
            jsonObject.put("name", edt_user_name.getText().toString());
            jsonObject.put("city", edt_city.getText().toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
        CallAPI.callingAPI(RegistrationActivity.this, 1, "update_profile", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Toast.makeText(RegistrationActivity.this, "Profile updated!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                finish();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (SharedHelper.getKey(RegistrationActivity.this,Constant.IS_LOGIN).equals("1")){
            startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            finish();
        }
    }

}