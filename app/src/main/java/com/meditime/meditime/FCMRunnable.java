package com.meditime.meditime;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FCMRunnable implements Runnable {
    String token;
    String title;
    String body;
    String command;
    String medicineId;

    public FCMRunnable(String token, String title, String body, String command, String medicineId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.command = command;
        this.medicineId = medicineId;
    }

    @Override
    public void run() {

        try {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Authorization", "key=" + Constants.FIREBASE_SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject notif = new JSONObject();
                notif.put("to", token);

                JSONObject info = new JSONObject();
                info.put("title", title); // Notification title
                info.put("body", body); // Notification body

                notif.put("notification", info);

                JSONObject data = new JSONObject();
                data.put("command", command);
                data.put("medicineId", medicineId);

                notif.put("data", data);


                Log.i("FCM", notif.toString());

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(notif.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
