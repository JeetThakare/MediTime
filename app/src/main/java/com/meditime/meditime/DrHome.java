package com.meditime.meditime;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        listView=(ListView)findViewById(R.id.listview);
        getPaitents(user);
        setTitle("Doctor Home");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });




    }

    private void getPaitents(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("Role", "Patient")
                .whereEqualTo("Doctor",user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user=new User(document.getId(), document.getString("Name"), document.getString("Role"), document.getString("Gender"));
                                patients.add(user);
                                System.out.println("user added to patient list");

                                //Log.d("DB", document.getId() + " => " + document.getData());
                            }
                            adapter=new ArrayAdapter(DrHome.this,android.R.layout.simple_list_item_1, patients);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("DB", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return;
    }
}
