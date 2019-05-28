package com.meditime.meditime;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    EditText name, age, gender, email, password, confirmPwd, role, speciality;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=(EditText)findViewById(R.id.nameTxt);
        age=(EditText)findViewById(R.id.ageTxt);
        gender=(EditText)findViewById(R.id.genderTxt);
        email=(EditText)findViewById(R.id.emailTxt);
        password=(EditText)findViewById(R.id.passwordTxt);
        confirmPwd=(EditText)findViewById(R.id.confirmPwdText);
        role=(EditText)findViewById(R.id.roleTxt);
        speciality=(EditText)findViewById(R.id.splTxt);

        signupButton=(Button)findViewById(R.id.signupButton);

        mAuth = FirebaseAuth.getInstance();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Sign Up", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Sign Up", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
//                if( name.getText().toString().isEmpty()){
//                    name.setError("Name is required!");
//                }if( age.getText().toString().isEmpty()){
//                    age.setError("Age is required!");
//                }if( gender.getText().toString().isEmpty()){
//                    gender.setError("Gender is required!");
//                }if( email.getText().toString().isEmpty()){
//                    email.setError("Email is required!");
//                }if( password.getText().toString().isEmpty()){
//                    password.setError("Password is required!");
//                }if( confirmPwd.getText().toString().isEmpty()){
//                    confirmPwd.setError("This is required!");
//                }if( role.getText().toString().isEmpty()){
//                    role.setError("Role is required!");
//                }else{
//                    Toast.makeText(SignupActivity.this, "You have successfully signed up!",
//                            Toast.LENGTH_SHORT).show();
//
//                    Intent intent= new Intent(SignupActivity.this,LoginActivity.class);
//                    startActivity(intent);
//                }
            }
        });
    }
}
