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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class SplashActivity extends AppCompatActivity {
    private TextView appName;
    private Button login;
    private Button signUp;
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

}
