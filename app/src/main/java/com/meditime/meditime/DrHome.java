package com.meditime.meditime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class DrHome extends AppCompatActivity {
    private ListView listView;
    private FirebaseAuth auth;
    private ArrayAdapter adapter;
    ArrayList<User> patients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dr_home);
        setTitle("My Patients");
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        listView = findViewById(R.id.listview);
        getPaitents(user);
        setTitle("Doctor Home");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DrHome.this, PrescriptionActivity.class);
                intent.putExtra("name", patients.get(position).getName());
                intent.putExtra("age", patients.get(position).getAge());
                intent.putExtra("email", patients.get(position).getEmail());
                intent.putExtra("gender", patients.get(position).getGender());

                startActivity(intent);
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
            startActivity(new Intent(DrHome.this,SplashActivity.class));
            finish();
        }
        return true;
    }

    private void getPaitents(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("Role", "Patient")
                .whereEqualTo("Doctor", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = new User(document.getId(), document.getString("Name"), document.getString("Role"), document.getString("Gender"), document.getString("Age"));
                                patients.add(user);
                                System.out.println("user added to patient list");

                                //Log.d("DB", document.getId() + " => " + document.getData());
                            }
                            adapter = new ArrayAdapter(DrHome.this, android.R.layout.simple_list_item_1, patients);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
