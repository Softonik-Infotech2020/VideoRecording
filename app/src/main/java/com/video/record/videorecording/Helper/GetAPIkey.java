package com.video.record.videorecording.Helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetAPIkey {

    public static void callGetAPI(Context context, OnGetKeyResponseListener listener){

        FirebaseDatabase.getInstance().getReference()
                .child("Softink Infotech").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String api_key = snapshot.child("api_key").getValue().toString();
                listener.onSuccess(api_key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFail("Failed to get children list");
            }
        });

    }

    public interface OnGetKeyResponseListener {
        void onSuccess(String api_key);

        void onFail(String message);
    }

}
