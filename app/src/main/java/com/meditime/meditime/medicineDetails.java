package com.meditime.meditime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class medicineDetails extends AppCompatActivity {

    Button photoBtn, saveBtn;
    ImageView medicineImage;
    EditText name, schedule, frequency, startDate, endDate;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String action, medicineId, photoUrl = "", patientEmail;
    private static DocumentReference mDocRef;

    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);
        setTitle("Medicine Details");
        photoBtn = (Button) findViewById(R.id.photoBtn);
        saveBtn = findViewById(R.id.saveBtn);
        medicineImage = (ImageView) findViewById(R.id.medicineIV);

        name = findViewById(R.id.nameET);
        schedule = findViewById(R.id.scheduleET);
        frequency = findViewById(R.id.frequencyET);
        startDate = findViewById(R.id.startDateET);
        endDate = findViewById(R.id.endDateET);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, SplashActivity.class));
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("email") == null) {
            if (intent.getStringExtra("role").equals("Patient")) {
                action = "PatientUpdate";
            } else {
                action = "DoctorUpdate";
            }
        } else {
            patientEmail = intent.getStringExtra("email");
            action = "DoctorAdd";
        }

        if (action.contains("Update")) {
            medicineId = intent.getStringExtra("medicineID");
            name.setText(intent.getStringExtra("name"));
            schedule.setText(intent.getStringExtra("schedule"));
            frequency.setText(intent.getStringExtra("weekfreq"));
            startDate.setText(intent.getStringExtra("startdt"));
            endDate.setText(intent.getStringExtra("enddt"));
            photoUrl = intent.getStringExtra("photourl");
            //medicineImage.setImageURI(Uri.parse(photoUrl));
        }

        if (action.contains("Patient")) {
            name.setFocusable(false);
            schedule.setFocusable(false);
            frequency.setFocusable(false);
            startDate.setFocusable(false);
            endDate.setFocusable(false);
        }

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                }
                if (action.contains("Update")) {
                    mDocRef = FirebaseFirestore.getInstance().document("medicines/" + medicineId);
                    mDocRef.update(
                            "Name", name.getText().toString(),
                            "dayFreq", schedule.getText().toString(),
                            "weekFreq", frequency.getText().toString(),
                            "startdt", startDate.getText().toString(),
                            "enddt", endDate.getText().toString(),
                            "photoUrl", photoUrl
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(medicineDetails.this, "Medicine details Updated!", Toast.LENGTH_SHORT).show();
                            sendNotification(medicineId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(medicineDetails.class.getSimpleName(), "medicine update failed " + e.toString());
                        }
                    });
                } else {
                    mDocRef = FirebaseFirestore.getInstance().collection("medicines").document();
                    HashMap<String, String> data = new HashMap<>();
                    data.put("Name", name.getText().toString());
                    data.put("dayFreq", schedule.getText().toString());
                    data.put("weekFreq", frequency.getText().toString());
                    data.put("startdt", startDate.getText().toString());
                    data.put("enddt", endDate.getText().toString());
                    data.put("photoUrl", photoUrl);
                    data.put("DoctorID", user.getEmail());
                    data.put("PatientID", patientEmail);

                    mDocRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(medicineDetails.this, "Medicine details added!", Toast.LENGTH_SHORT).show();
                            sendNotification(medicineId);
                            startActivity(new Intent(medicineDetails.this, PrescriptionActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(medicineDetails.class.getSimpleName(), "medicine adding failed " + e.toString());
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            medicineImage.setImageBitmap(bitmap);
        }

    }

    private boolean validate() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Name is required!");
            return false;
        }
        if (schedule.getText().toString().isEmpty()) {
            schedule.setError("Schedule is required!");
            return false;
        }
        if (frequency.getText().toString().isEmpty()) {
            frequency.setError("Weekly frequency is required!");
            return false;
        }
        if (startDate.getText().toString().isEmpty()) {
            name.setError("Name is required!");
            return false;
        }
        return true;
    }

    private void sendNotification(String medicineId) {
        NotificationHelper.sendNotification(patientEmail, "Medicine Update",
                "Doctor has updated you medicine details, Please Checkout!",
                medicineId, Constants.SCHEDULE_UPDATE_COMMAND);
    }
}
