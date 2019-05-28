package com.meditime.meditime;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    EditText name, age, gender, email, password, confirmPwd, role, speciality;
    private FirebaseAuth mAuth;
    private DocumentReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name =  findViewById(R.id.nameTxt);
        age = findViewById(R.id.ageTxt);
        gender = findViewById(R.id.genderTxt);
        email = findViewById(R.id.emailTxt);
        password = findViewById(R.id.passwordTxt);
        confirmPwd = findViewById(R.id.confirmPwdText);
        role = findViewById(R.id.roleTxt);
        speciality = findViewById(R.id.splTxt);

        signupButton = findViewById(R.id.signupButton);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateform()) {
                    Toast.makeText(SignupActivity.this, "Error in the input.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Sign Up", "createUserWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    if(user == null){
                                        Toast.makeText(SignupActivity.this, "Authentication details failed.",
                                                Toast.LENGTH_SHORT).show();
                                        user.delete();
                                        return;
                                    }
                                    mDocRef = FirebaseFirestore.getInstance().document("users/"+user.getEmail());
                                    HashMap<String, String> userDetails = new HashMap<>();
                                    userDetails.put("Name", name.getText().toString());
                                    userDetails.put("Gender", gender.getText().toString());
                                    userDetails.put("Role", role.getText().toString());
                                    userDetails.put("Speciality", speciality.getText().toString());

                                    mDocRef.set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignupActivity.this, "You have successfully signed up!",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            user.delete();
                                        }
                                    });


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Sign Up", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean validateform() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Name is required!");
            return false;
        }
        if (age.getText().toString().isEmpty()) {
            age.setError("Age is required!");
            return false;
        }
        if (gender.getText().toString().isEmpty()) {
            gender.setError("Gender is required!");
            return false;
        }
        if (email.getText().toString().isEmpty()) {
            email.setError("Email is required!");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Password is required!");
            return false;
        }
        if (confirmPwd.getText().toString().isEmpty()) {
            confirmPwd.setError("This is required!");
            return false;
        }
        if (role.getText().toString().isEmpty()) {
            role.setError("Role is required!");
            return false;
        }
        return true;
    }
}
