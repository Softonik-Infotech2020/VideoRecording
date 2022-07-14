package com.video.record.videorecording.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CallAPI {

    public static void callingAPI(Context context, int method, String url_last_point, JSONObject jsonObject,OnResponseListener listener){

        int pass_method;
        if (method == 1){
            pass_method = Request.Method.POST;
        }
        else {
            pass_method = Request.Method.GET;
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(pass_method,
                "https://rv365enterprice.softonikinfotech.com/api/"+url_last_point, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject json_response) {
                        Log.e("API_Response" ,url_last_point+"==> "+json_response.toString());

                        try {
                            String status = json_response.getString("status");

                            if (status.equals("1")){
                                listener.onSuccess(json_response);
                            }
                            else {
                                String message = json_response.getString("message");
                                listener.onFail(message);
                            }

                        } catch (JSONException e) {
                            Log.e("API_Catch", String.valueOf(e));
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_error", String.valueOf(error));
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            /**
             *
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                /*headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");*/
                return headers;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjReq);

    }

    public interface OnResponseListener {
        void onSuccess(JSONObject jsonObject);

        void onFail(String message);
    }

}
