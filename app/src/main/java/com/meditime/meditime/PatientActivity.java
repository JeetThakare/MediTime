package com.meditime.meditime;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PatientActivity extends AppCompatActivity {

    ListView lv;
    private FirebaseAuth mAuth;
    ArrayList<Medicine> medicineList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        setTitle("Medicine Schedule");

        lv = (ListView) findViewById(R.id.listview2);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        getMedicine(user);
    }

    private  void getMedicine(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicines")
                .whereEqualTo("PatientID",user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Medicine newMedicine = new Medicine(document.getString("DoctorID"),
                                        document.getString("Name"),
                                        document.getString("PatientID"),
                                        document.getString("dayFreq"),
                                        document.getString("enddt"),
                                        document.getString("photourl"),
                                        document.getString("startdt"),
                                        document.getString("weekFreq"),
                                        document.getId());
                                medicineList.add(newMedicine);
                                System.out.println("New medicine has been added to list"+document.getString("name"));
                            }
                            MedicineListAdapter adaptor = new MedicineListAdapter(PatientActivity.this,R.layout.custom_medicine_list,medicineList);
                            lv.setAdapter(adaptor);
                        }else{
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
