package com.meditime.meditime;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class Utils {
    private static FirebaseAuth mAuth;
    private static DocumentReference mDocRef;

    public static void setToken(String token) {
        if(token == null || token.isEmpty()){
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user ==  null){
            return;
        }
        mDocRef = FirebaseFirestore.getInstance().document("users/" + user.getEmail());

        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);

        mDocRef.set(token, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("FCM", "Token saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FCM", "Token not saved");
            }
        });
    }
}
