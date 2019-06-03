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

public class PrescriptionActivity extends AppCompatActivity {
    private String name;
    private String age;
    private String gender;
    private String email;
    private FirebaseAuth mAuth;
    TextView nameTV, ageTV, genderTV;
    ListView medicineLV;
    ArrayAdapter adapter;
    Button addmMdicines;
    ArrayList<Medicine> medicineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        setTitle("Prescription");
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        gender = intent.getStringExtra("gender");
        age = intent.getStringExtra("age");

        if (email != null || !email.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.getString("Name");
                            age = document.getString("Age");
                            gender = document.getString("Gender");
                        } else {
                            Log.d("Login role check", "No such document");
                        }
                    } else {
                        Log.d("Login role check", "get failed with ", task.getException());
                    }
                }
            });
        }
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        nameTV = findViewById(R.id.patientNameTV);
        ageTV = findViewById(R.id.ageTV);
        genderTV = findViewById(R.id.genderTV);
        medicineLV = findViewById(R.id.medicinelv);

        nameTV.setText(name);
        ageTV.setText(age);
        genderTV.setText(gender);

        addmMdicines = findViewById(R.id.addMediBtn);
        addmMdicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionActivity.this, medicineDetails.class);
                intent.putExtra("email", email);
                intent.putExtra("role", "Doctor");
                startActivity(intent);
            }
        });

        showMedicines(user);

        medicineLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(PrescriptionActivity.this, medicineDetails.class);
                intent1.putExtra("name", medicineList.get(position).getName());
                intent1.putExtra("schedule", medicineList.get(position).getDayFreq());
                intent1.putExtra("weekfreq", medicineList.get(position).getWeekFreq());
                intent1.putExtra("startdt", medicineList.get(position).getStartDate());
                intent1.putExtra("enddt", medicineList.get(position).getEndDate());
                intent1.putExtra("photourl", medicineList.get(position).getPhotoUrl());
                intent1.putExtra("medicineID", medicineList.get(position).getMedicineID());
                intent1.putExtra("role", "Doctor");
                startActivity(intent1);
            }
        });
    }

    public void showMedicines(FirebaseUser user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicines")
                .whereEqualTo("PatientID", email)
                .whereEqualTo("DoctorID", user.getEmail())
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
                            adapter = new ArrayAdapter(PrescriptionActivity.this, android.R.layout.simple_list_item_1, medicineList);
                            medicineLV.setAdapter(adapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return;
    }

}
