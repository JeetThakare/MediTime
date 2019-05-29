package com.meditime.meditime;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    Spinner genderSpinner,roleSpinner;
    TextView specialityLbl;
    EditText name, age, email, password, confirmPwd, speciality;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupButton=(Button)findViewById(R.id.signupButton);

        specialityLbl=(TextView)findViewById(R.id.specialityLabel);

        name=(EditText)findViewById(R.id.nameTxt);
        age=(EditText)findViewById(R.id.ageTxt);
        email=(EditText)findViewById(R.id.emailTxt);
        password=(EditText)findViewById(R.id.passwordTxt);
        confirmPwd=(EditText)findViewById(R.id.confirmPwdText);
        speciality=(EditText)findViewById(R.id.splTxt);

        genderSpinner=(Spinner)findViewById(R.id.genderSpinner);
        roleSpinner=(Spinner)findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_dropdown, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_dropdown, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        final int currentChoice=roleSpinner.getSelectedItemPosition();

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView,View view,int i,long id) {
                if(currentChoice==i){
                    specialityLbl.setVisibility(View.GONE);
                    speciality.setVisibility(View.GONE);
                }else{
                    specialityLbl.setVisibility(View.VISIBLE);
                    speciality.setVisibility(View.VISIBLE);
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

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
