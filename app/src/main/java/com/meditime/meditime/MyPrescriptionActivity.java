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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyPrescriptionActivity extends AppCompatActivity {
    private String name;
    private String age;
    private String gender;
    private FirebaseAuth mAuth;
    TextView nameTV, ageTV, genderTV;
    ListView medicineLV;
    ArrayAdapter adapter;
    ArrayList<Medicine> medicineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prescription);

        setTitle("My Prescription");

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MyPrescriptionActivity.this, SplashActivity.class));
            finish();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name = document.getString("Name");
                        age = document.getString("Age");
                        gender = document.getString("Gender");
                        nameTV.setText(name);
                        ageTV.setText(age);
                        genderTV.setText(gender);
                    } else {
                        Log.d("Login role check", "No such document");
                    }
                } else {
                    Log.d("Login role check", "get failed with ", task.getException());
                }
            }
        });


        medicineLV = findViewById(R.id.medicinelv);

        showMedicines(user);

        medicineLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(MyPrescriptionActivity.this, medicineDetails.class);
                intent1.putExtra("name", medicineList.get(position).getName());
                intent1.putExtra("schedule", medicineList.get(position).getDayFreq());
                intent1.putExtra("weekfreq", medicineList.get(position).getWeekFreq());
                intent1.putExtra("startdt", medicineList.get(position).getStartDate());
                intent1.putExtra("enddt", medicineList.get(position).getEndDate());
                intent1.putExtra("photourl", medicineList.get(position).getPhotoUrl());
                intent1.putExtra("medicineID", medicineList.get(position).getMedicineID());
                intent1.putExtra("role", "Doctor");
                intent1.putExtra("email", user.getEmail());
                intent1.putExtra("action", "PatientUpdate");
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        showMedicines(user);
    }

    public void showMedicines(FirebaseUser user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicines")
                .whereEqualTo("PatientID", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Medicine newMedicine = new Medicine(document.getString("DoctorID"), document.getString("Name"), document.getString("PatientID"), document.getString("dayFreq"), document.getString("enddt"), document.getString("photourl"), document.getString("startdt"), document.getString("weekFreq"), document.getId());
                                medicineList.add(newMedicine);
                                System.out.println("New medicine has been added to list");

                                //Log.d("DB", document.getId() + " => " + document.getData());
                            }
                            adapter = new ArrayAdapter(MyPrescriptionActivity.this, android.R.layout.simple_list_item_1, medicineList);
                            medicineLV.setAdapter(adapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
