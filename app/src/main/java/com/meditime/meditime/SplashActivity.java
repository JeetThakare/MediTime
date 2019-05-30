package com.meditime.meditime;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private TextView appName;
    private Button login;
    private Button signUp;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appName=(TextView) findViewById(R.id.textView);
        Typeface type=Typeface.createFromAsset(getAssets(),"ComicSansMS3.ttf");
        appName.setTypeface(type);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Check user and open relevant activity
            /*
            if(user.role = "Patient"){
                startActivity(new Intent(SplashActivity.this, PatientHome.class));
                finish();
            }
            else {
                startActivity(new Intent(SplashActivity.this, DoctorHome.class));
                finish();
            }
            */
        }

        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
            }});
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SplashActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
