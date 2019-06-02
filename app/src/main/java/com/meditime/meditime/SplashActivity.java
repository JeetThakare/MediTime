package com.meditime.meditime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private TextView appName;
    private Button login;
    private Button signUp;
    private FirebaseAuth auth;
    private DocumentReference mDocRef;
    private ProgressBar progressBar;
    String role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appName = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signup);

        progressBar.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        signUp.setVisibility(View.GONE);

        Typeface type = Typeface.createFromAsset(getAssets(), "ComicSansMS3.ttf");
        appName.setTypeface(type);

        createNotificationChannel();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Check user and open relevant activity
            mDocRef = FirebaseFirestore.getInstance().document("users/" + user.getEmail());

            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Splash", "DocumentSnapshot data: " + document.getData());
                            role = document.getString("Role");
                        } else {
                            Log.d("Splash", "No such document");
                        }
                        if (role.equals("Doctor")) {
                            startActivity(new Intent(SplashActivity.this, DrHome.class));
                            finish();
                        } else {
//                            startActivity(new Intent(SplashActivity.this, DrHome.class));
//                            finish();
                            Toast.makeText(SplashActivity.this, "Should go to student activity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Splash", "get failed with ", task.getException());
                    }
                }
            });
        }
        progressBar.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.VISIBLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    private void createNotificationChannel() {
        String name = Constants.CHANNEL_NAME;
        String description = Constants.CHANNEL_DESC;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
