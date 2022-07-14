package com.video.record.videorecording;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.video.record.videorecording.Adapters.PlanListAdapter;
import com.video.record.videorecording.Helper.CallAPI;
import com.video.record.videorecording.Helper.Constant;
import com.video.record.videorecording.Helper.GetAPIkey;
import com.video.record.videorecording.Helper.OnSelectedItemListener;
import com.video.record.videorecording.Helper.SharedHelper;
import com.video.record.videorecording.Model.PlanModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscriptionActivity extends AppCompatActivity implements PaymentResultListener {

    ImageView img_back;
    private RecyclerView.LayoutManager mlayoutManager;
    RecyclerView rec_plan_list;
    ArrayList<PlanModel> planModelArrayList = new ArrayList<>();
    PlanListAdapter planListAdapter;
    String string_api_key = "";
    String plan_name,plan_id,transaction_id,order_id,starting_date,end_date,duration,amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        Window window = SubscriptionActivity.this.getWindow();
        Drawable background = SubscriptionActivity.this.getResources().getDrawable(R.drawable.bg_card_gradient_1);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(SubscriptionActivity.this.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(SubscriptionActivity.this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        img_back = findViewById(R.id.img_back);
        rec_plan_list = findViewById(R.id.rec_plan_list);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getApiKey();

    }

    public void getApiKey(){
        GetAPIkey.callGetAPI(SubscriptionActivity.this, new GetAPIkey.OnGetKeyResponseListener() {
            @Override
            public void onSuccess(String api_key) {
                string_api_key = api_key;
                callApi();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(SubscriptionActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callApi(){
        planModelArrayList.clear();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
        }catch (Exception e) {
            e.printStackTrace();
        }

        CallAPI.callingAPI(SubscriptionActivity.this, 1, "plan_list", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length();i++){
                        JSONObject obj = array.getJSONObject(i);

                        PlanModel pm = new PlanModel();
                        pm.setId(obj.getString("id"));
                        pm.setName(obj.getString("name"));
                        pm.setDuration(obj.getString("duration"));
                        pm.setAmount(obj.getString("amount"));
                        pm.setType(obj.getString("type"));
                        pm.setDiscount(obj.getString("discount"));
                        pm.setDiscounted_price(obj.getString("discounted_price"));
                        pm.setStatus(obj.getString("status"));

                        planModelArrayList.add(pm);

                    }

                    planListAdapter = new PlanListAdapter(SubscriptionActivity.this, planModelArrayList,onSelectedItemListener);
                    mlayoutManager = new LinearLayoutManager(SubscriptionActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    rec_plan_list.setLayoutManager(mlayoutManager);
                    rec_plan_list.setAdapter(planListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    private OnSelectedItemListener onSelectedItemListener = new OnSelectedItemListener() {
        @Override
        public void setOnClick(String selectionString, int position) {
            plan_name = planModelArrayList.get(position).getName();
            plan_id = planModelArrayList.get(position).getId();
            duration = planModelArrayList.get(position).getDuration()+planModelArrayList.get(position).getType();
            amount = planModelArrayList.get(position).getDiscounted_price();
            Payment(planModelArrayList.get(position).getDiscounted_price());
        }
    };

    public void Payment(String amount){

        Checkout checkout = new Checkout();
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Musical Video Maker");
            options.put("currency", "INR");

            double total = Double.parseDouble(amount);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", SharedHelper.getKey(this, Constant.EMAIL));
            preFill.put("contact", SharedHelper.getKey(this, Constant.NUMBER));

            options.put("prefill", preFill);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        transaction_id = razorpayPaymentID;
        //getSubscriptionAPI();
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }

    public void getSubscriptionAPI(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("key", string_api_key);
            jsonObject.put("user_id", SharedHelper.getKey(this,Constant.USER_ID));
            jsonObject.put("name", plan_name);
            jsonObject.put("transaction_id", transaction_id);
            jsonObject.put("order_id", "#1234fd");
            jsonObject.put("plan_id", plan_id);
            jsonObject.put("starting_date", starting_date);
            jsonObject.put("end_date", end_date);
            jsonObject.put("duration", duration);
            jsonObject.put("amount", amount);
            Log.e("jsonobjectissss", String.valueOf(jsonObject));
        }catch (Exception e) {
            e.printStackTrace();
        }

        CallAPI.callingAPI(SubscriptionActivity.this, 1, "get_subscription", jsonObject, new CallAPI.OnResponseListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.e("Done_MSG",jsonObject.toString());
            }

            @Override
            public void onFail(String message) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SubscriptionActivity.this,HomeActivity.class));
        finish();
    }

}