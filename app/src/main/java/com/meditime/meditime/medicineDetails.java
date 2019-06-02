package com.meditime.meditime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class medicineDetails extends AppCompatActivity {

    Button photoBtn, saveBtn;
    ImageView medicineImage;
    EditText name, schedule, frequency, startDate, endDate;
    private FirebaseAuth auth;

    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);
        setTitle("Medicine Details");
        photoBtn = (Button) findViewById(R.id.photoBtn);
        saveBtn=findViewById(R.id.saveBtn);
        medicineImage = (ImageView) findViewById(R.id.medicineIV);

        name=findViewById(R.id.nameET);
        schedule=findViewById(R.id.scheduleET);
        frequency=findViewById(R.id.frequencyET);
        startDate=findViewById(R.id.startDateET);
        endDate=findViewById(R.id.endDateET);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        Intent intent=getIntent();
        if (intent.getStringExtra("email")==null){
            name.setText(intent.getStringExtra("name"));
            schedule.setText(intent.getStringExtra("schedule"));
            frequency.setText(intent.getStringExtra("weekfreq"));
            startDate.setText(intent.getStringExtra("startdt"));
            endDate.setText(intent.getStringExtra("enddt"));
//            set medicineImage url
        }
        if(intent.getStringExtra("role").matches("Patient")){
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

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            Bitmap bitmap= (Bitmap)extras.get("data");
            medicineImage.setImageBitmap(bitmap);
        }

    }

    private boolean validate(){
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
}
