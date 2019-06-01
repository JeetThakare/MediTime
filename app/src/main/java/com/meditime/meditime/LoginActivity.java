package com.meditime.meditime;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText pass;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private String role="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        username = findViewById(R.id.usernameTV);
        pass = findViewById(R.id.passwordTV);

        loginBtn = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(username.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("LogIn", "signInWithEmail:success");
                                    System.out.println("signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, user.getEmail()+"logged in successfully", Toast.LENGTH_LONG).show();

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference docRef = db.collection("users").document(user.getEmail()); // user is current user here
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
//                                                    Log.d(TAG, "DocumentSnapshot data: " + document.getString("Role"));
                                                    role = document.getString("Role");
                                                    System.out.println("role:"+ role);
                                                    if(role.equals("Doctor")){
                                                        startActivity(new Intent(LoginActivity.this, DrHome.class));
                                                    }
//                                                    else{
//                                                        startActivity(new Intent(LoginActivity.this, PatientHome.class))
//                                                    }
                                                } else {
                                                    Log.d("Login role check", "No such document");
                                                }
                                            } else {
                                                Log.d("Login role check", "get failed with ", task.getException());
                                            }

                                        }
                                    });


                                    // Open relevant activity
                                    // Code here ...
                                    // startActivity(new Intent(LoginActivity.this, SplashActivity.class));

                                    // Open relevant activity
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("LogIn", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }
}
