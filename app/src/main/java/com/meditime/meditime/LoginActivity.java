package com.meditime.meditime;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText pass;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private String role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        username = findViewById(R.id.usernameTV);
        pass = findViewById(R.id.passwordTV);
        loginBtn = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

    }

    public void login(View view) {
        if (!isValidate()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(username.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setToken();
                            Log.d("LogIn", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("users").document(user.getEmail());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            role = document.getString("Role");
                                            System.out.println("role:" + role);
                                            if (role.equals("Doctor")) {
                                                startActivity(new Intent(LoginActivity.this, DrHome.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(LoginActivity.this, PatientActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Log.d("Login role check", "No such document");
                                        }
                                    } else {
                                        Log.d("Login role check", "get failed with ", task.getException());
                                    }
                                }
                            });
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LogIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean isValidate() {
        if (username.getText().toString().isEmpty()) {
            username.setError("Username is required");
            return false;
        }
        if (pass.getText().toString().isEmpty()) {
            pass.setError("Password is required");
            return false;
        }
        return true;
    }

    private void setToken() {
        SharedPreferences pref = getSharedPreferences("MediPrefs", MODE_PRIVATE);
        Utils.setToken(pref.getString("token", null));
    }
}
