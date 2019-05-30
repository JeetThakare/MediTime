package com.meditime.meditime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DrHome extends AppCompatActivity {
    private ListView listView;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dr_home);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        listView=(ListView)findViewById(R.id.listview);


    }
}
