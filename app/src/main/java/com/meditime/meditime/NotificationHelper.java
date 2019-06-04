package com.meditime.meditime;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class NotificationHelper {

    private static DocumentReference mDocRef;

    public void displayNotification(Context context, String title, String body, String bigText) {

        Log.i("AlramService", "Inside notif");

        Intent intent = new Intent(context, EntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setSmallIcon(R.drawable.heart_16)
                .setVibrate(new long[]{1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

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
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Authorization", "key=" + Constants.FIREBASE_SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject data = new JSONObject();
            data.put("to", token);
            JSONObject info = new JSONObject();
            info.put("title", title); // Notification title
            info.put("body", body); // Notification body
            data.put("command", command);
            data.put("medicineId", medicineId);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data.toString());
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            Log.i("FCM", "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            Log.i("FCM", "Response " + response.toString());
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


