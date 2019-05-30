package com.meditime.meditime;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    TextView doctorLbl;
    Spinner genderSpinner, roleSpinner, doctorSpinner;
    EditText name, age, email, password, confirmPwd;
    private FirebaseAuth mAuth;
    private DocumentReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.nameTxt);
        age = findViewById(R.id.ageTxt);
        email = findViewById(R.id.emailTxt);
        password = findViewById(R.id.passwordTxt);
        confirmPwd = findViewById(R.id.confirmPwdText);

        genderSpinner = findViewById(R.id.genderSpinner);
        roleSpinner = findViewById(R.id.spinner2);
        doctorSpinner = findViewById(R.id.doctorSpinner);
        doctorLbl = findViewById(R.id.doctorLabel);

        final ArrayAdapter<User> docsAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getDoctors());
        doctorSpinner.setAdapter(docsAdapter);

//        String compareValue="doctor";
//        if (compareValue != null) {
//            int spinnerPosition = docsAdapter.getPosition(compareValue);
//            doctorSpinner.setSelection(spinnerPosition);
//        }


        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signupButton = (Button) findViewById(R.id.signupButton);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_dropdown, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this, R.array.role_dropdown, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        doctorLbl = findViewById(R.id.doctorLabel);

        final int currentChoice = roleSpinner.getSelectedItemPosition();
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                if (currentChoice == i) {
                    doctorLbl.setVisibility(View.VISIBLE);
                    doctorSpinner.setVisibility(View.VISIBLE);
                } else {
                    doctorLbl.setVisibility(View.GONE);
                    doctorSpinner.setVisibility(View.GONE);
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
                                    if (user == null) {
                                        Toast.makeText(SignupActivity.this, "Authentication details failed.",
                                                Toast.LENGTH_SHORT).show();
                                        user.delete();
                                        return;
                                    }
                                    mDocRef = FirebaseFirestore.getInstance().document("users/" + user.getEmail());
                                    HashMap<String, String> userDetails = new HashMap<>();
                                    userDetails.put("Name", name.getText().toString());
                                    userDetails.put("Age", age.getText().toString());
                                    userDetails.put("Gender", genderSpinner.getSelectedItem().toString());
                                    userDetails.put("Role", roleSpinner.getSelectedItem().toString());
                                    if (roleSpinner.getSelectedItem().toString().equals("Patient")) {
                                        userDetails.put("Doctor", doctorSpinner.getSelectedItem().toString());
                                    }

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
        if (email.getText().toString().isEmpty()) {
            email.setError("Email is required!");
            return false;
        } else {
            String regExpn =
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

            Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email.getText().toString());

            if (!matcher.matches()) {
                email.setError("Enter valid email!");
                return false;
            }
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Password is required!");
            return false;
        }
        if (confirmPwd.getText().toString().isEmpty()) {
            confirmPwd.setError("This is required!");
            return false;
        }
        if (!password.getText().toString().equals(confirmPwd.getText().toString())) {
            confirmPwd.setError("Password mismatch!");
            return false;
        }
        return true;
    }

    private ArrayList<User> getDoctors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<User> docs = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("Role", "Doctor")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docs.add(new User(document.getId(), document.getString("Name"), document.getString("Role"), document.getString("Gender")));
                                //Log.d("DB", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return docs;
    }

}
