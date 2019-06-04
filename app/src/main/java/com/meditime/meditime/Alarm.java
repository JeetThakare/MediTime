package com.meditime.meditime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class Alarm extends BroadcastReceiver {
    String body = "";
    private FirebaseAuth auth;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final int time = Integer.parseInt(intent.getStringExtra("time"));
        final NotificationHelper notif = new NotificationHelper();

        Log.i("AlramService", "On create of alram service");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            return;
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        final Date now = new Date();

        final int dayOfWeek;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicines")
                .whereEqualTo("PatientID", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("AlramService", "Query sucess");
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.i("AlramService", "Query sucess doc");
                                char[] dayFreq = document.getString("dayFreq").toCharArray();
                                char[] weekFreq = document.getString("weekFreq").toCharArray();

                                if (dayFreq[time] == '1' && weekFreq[dayOfWeek] == '1') {
                                    try {
                                        Date startdt = formatter.parse(document.getString("startdt"));
                                        Date enddt = formatter.parse(document.getString("enddt"));
                                        if (startdt.before(now) && enddt.after(now)) {
                                            body += "\n";
                                            body += document.getString("Name");
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (!body.isEmpty()) {
                                notif.displayNotification(context, "Time for your medicine", "", "Please take the below medicines " + body, "reminder");
                            }
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
