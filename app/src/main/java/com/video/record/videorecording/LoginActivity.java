package com.video.record.videorecording;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.video.record.videorecording.Helper.CallAPI;
import com.video.record.videorecording.Helper.Constant;
import com.video.record.videorecording.Helper.GetAPIkey;
import com.video.record.videorecording.Helper.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ImageView img_back;
    EditText edt_mobile,edt_otp;
    LinearLayout ll_otp;
    TextView txt_resend;
    CardView card_otp,card_login;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    String phoneVerificationId;
    FirebaseAuth fbAuth;
    String finalnumberis="";
    String firebase_user_id = "";
    String string_api_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = LoginActivity.this.getWindow();
        Drawable background = LoginActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(LoginActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(LoginActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        img_back = findViewById(R.id.img_back);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_otp = findViewById(R.id.edt_otp);
        ll_otp = findViewById(R.id.ll_otp);
        txt_resend = findViewById(R.id.txt_resend);
        card_otp = findViewById(R.id.card_otp);
        card_login = findViewById(R.id.card_login);
        fbAuth = FirebaseAuth.getInstance();

        getApiKey();

        card_otp.setOnClickListener(v -> {
            if (edt_mobile.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this, "Please enter mobile number.", Toast.LENGTH_SHORT).show();
            }
            else if (edt_mobile.getText().toString().length()<10){
                Toast.makeText(LoginActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
            }
            else {
                edt_mobile.setEnabled(false);
                card_otp.setVisibility(View.GONE);
                ll_otp.setVisibility(View.VISIBLE);
                card_login.setVisibility(View.VISIBLE);
                verify_number(edt_mobile.getText().toString());
            }
        });

        card_login.setOnClickListener(v -> {
            if (edt_otp.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
            else if (edt_otp.getText().toString().length()<6){
                Toast.makeText(LoginActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,edt_otp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

        txt_resend.setOnClickListener(v -> {
            resendCode();
        });

        img_back.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void verify_number(final String number) {
        Auth_for_firebase(number);
    }

    private void Auth_for_firebase(String number) {
        setUpVerificatonCallbacks();
        String countrycode="+91";
        finalnumberis=countrycode+number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(countrycode+number,120, TimeUnit.SECONDS,LoginActivity.this,verificationCallbacks);
    }

    public void resendCode() {
        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                finalnumberis,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    private void setUpVerificatonCallbacks() {
        Log.e("called_phoneauth","calllde");
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                        signInWithPhoneAuthCredential(credential);

                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Log.e("responcejdfskf",e.toString());
                        Toast.makeText(LoginActivity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                        edt_mobile.setEnabled(true);
                        ll_otp.setVisibility(View.GONE);
                        card_login.setVisibility(View.GONE);
                        card_otp.setVisibility(View.VISIBLE);

                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                        {

                        }
                        else if (e instanceof FirebaseTooManyRequestsException)
                        {

                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        phoneVerificationId = verificationId;
                        resendToken = token;
                    }
                };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            //success code here********************************************************
                            firebase_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            callApi();
                            //*************************************************************************
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //card_otp.setVisibility(View.VISIBLE);
                                Log.e("exceptionis", String.valueOf(task.getException()));
                                Toast.makeText(LoginActivity.this, "Enter valid OTP!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void getApiKey(){
        GetAPIkey.callGetAPI(LoginActivity.this, new GetAPIkey.OnGetKeyResponseListener() {
            @Override
            public void onSuccess(String api_key) {
                //Toast.makeText(RegistrationActivity.this, api_key, Toast.LENGTH_SHORT).show();
                string_api_key = api_key;
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callApi(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("number", "+91"+edt_mobile.getText().toString());
        }catch (Exception e) {
            e.printStackTrace();
        }

        CallAPI.callingAPI(LoginActivity.this, 1, "get_login", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length();i++){
                        JSONObject obj = array.getJSONObject(0);
                        SharedHelper.putKey(LoginActivity.this,Constant.USER_ID,obj.getString("id"));
                        SharedHelper.putKey(LoginActivity.this,Constant.NAME,obj.getString("name"));
                        SharedHelper.putKey(LoginActivity.this,Constant.EMAIL,obj.getString("email"));
                        SharedHelper.putKey(LoginActivity.this,Constant.NUMBER,obj.getString("number"));
                        SharedHelper.putKey(LoginActivity.this,Constant.VALID_TILL,obj.getString("valid_till"));
                        SharedHelper.putKey(LoginActivity.this,Constant.CITY,obj.getString("city"));
                    }

                    SharedHelper.putKey(LoginActivity.this, Constant.IS_LOGIN,"1");
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String message) {

                SharedHelper.putKey(LoginActivity.this, Constant.IS_LOGIN,"0");
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class)
                        .putExtra("MOBILE",edt_mobile.getText().toString()));
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}