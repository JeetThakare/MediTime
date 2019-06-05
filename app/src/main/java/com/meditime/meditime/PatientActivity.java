package com.meditime.meditime;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PatientActivity extends AppCompatActivity {

    ListView lv;
    Button viewMediBtn;
    private FirebaseAuth mAuth;
    ArrayList<Medicine> medicineList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        setTitle("Medicine Schedule");

        lv = (ListView) findViewById(R.id.listview2);
        viewMediBtn=findViewById(R.id.viewMediButton);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        getMedicine(user);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PatientActivity.this, medicineDetails.class);
                intent.putExtra("name", medicineList.get(position).getName());
                intent.putExtra("schedule", medicineList.get(position).getDayFreq());
                intent.putExtra("weekfreq", medicineList.get(position).getWeekFreq());
                intent.putExtra("startdt", medicineList.get(position).getStartDate());
                intent.putExtra("enddt", medicineList.get(position).getEndDate());
                intent.putExtra("photoUrl", medicineList.get(position).getPhotoUrl());
                intent.putExtra("medicineID", medicineList.get(position).getMedicineID());
                intent.putExtra("role", "Patient");
                intent.putExtra("action", "PatientUpdate");
                intent.putExtra("email", user.getEmail());
                startActivity(intent);
            }
        });

        viewMediBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientActivity.this, MyPrescriptionActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_my_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logoutMenuId){
            Toast.makeText(this,"You are logged out",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(PatientActivity.this,SplashActivity.class));
            finish();
        }
        return true;
    }

    private  void getMedicine(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Date currentDate = Calendar.getInstance().getTime();
        Calendar cal = Calendar.getInstance();
        final int dayofweek = cal.get(Calendar.DAY_OF_WEEK)-2;
        final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedcurrentDate = df.format(currentDate);
        ArrayList<Medicine> todayMedicine=new ArrayList<>();

        db.collection("medicines")
                .whereEqualTo("PatientID",user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("AlramService", "Query sucess doc");
                                char[] weekfreq = document.getString("weekFreq").toCharArray();
                                int dayfreq = Integer.parseInt(document.getString("dayFreq"));

                                try{
                                    Date sdate = df.parse(document.getString("startdt"));
                                    Date edate = df.parse(document.getString("enddt"));
                                    if (currentDate.after(sdate) && currentDate.before(edate) && weekfreq[dayofweek] == '1' ) {
                                        System.out.println("between st date and end date ");
                                        Medicine newMedicine = new Medicine(document.getString("DoctorID"),
                                                document.getString("Name"),
                                                document.getString("PatientID"),
                                                document.getString("dayFreq"),
                                                document.getString("enddt"),
                                                document.getString("photoUrl"),
                                                document.getString("startdt"),
                                                document.getString("weekFreq"),
                                                document.getId());
                                        medicineList.add(newMedicine);

                                    }
                                }catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            Collections.sort(medicineList, new Comparator<Medicine>() {
                                @Override
                                public int compare(Medicine m1, Medicine m2) {
                                    return m2.getDayFreq().compareTo(m1.getDayFreq());
                                }
                            });

                            MedicineListAdapter adaptor = new MedicineListAdapter(PatientActivity.this,R.layout.custom_medicine_list,medicineList);
                            lv.setAdapter(adaptor);
                        }else{
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
