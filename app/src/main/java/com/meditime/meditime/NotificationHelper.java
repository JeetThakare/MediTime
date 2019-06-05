package com.meditime.meditime;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class NotificationHelper {

    private static DocumentReference mDocRef;

    public void displayNotification(Context context, String title, String body, String bigText, String type) {

        Log.i("AlramService", "Inside notif");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setSmallIcon(R.drawable.heart_16)
                .setVibrate(new long[]{1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (type.equals("reminder")) {
            Intent intent = new Intent(context, EntryActivity.class);
            Log.i("AlramService", "Adding sound");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            mBuilder.setSound(alarmSound);
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setContentIntent(pendingIntent);

        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder.build());
    }

    public void sendNotification(String email, final String title, final String body, final String medicineId, final String command) {

        mDocRef = FirebaseFirestore.getInstance().document("users/" + email);

        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("NOTIF", "DocumentSnapshot data: " + document.getData());
                        String token = document.getString("token");
                        callFcm(token, title, body, command, medicineId);
                    } else {
                        Log.d("NOTIF", "No such document");
                    }
                } else {
                    Log.d("NOTIF", "get failed with ", task.getException());
                }
            }
        });
    }

    private void callFcm(String token, String title, String body, String command, String medicineId) {
        FCMRunnable r = new FCMRunnable(token, title, body, command, medicineId);
        new Thread(r).start();
    }

}