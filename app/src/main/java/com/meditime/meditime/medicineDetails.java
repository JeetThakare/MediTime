package com.meditime.meditime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class medicineDetails extends AppCompatActivity {

    Button photoBtn, saveBtn;
    ImageView medicineImage;
    EditText name, schedule, frequency, startDate, endDate;
    private FirebaseAuth auth;
    private Calendar calendar1;
    private Calendar calendar2;
    private DatePickerDialog datePickerDialog;

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
        startDate.setInputType(InputType.TYPE_NULL);
        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();
        endDate = findViewById(R.id.endDateET);
        endDate.setInputType(InputType.TYPE_NULL);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        Intent intent = getIntent();
        if (intent.getStringExtra("email") == null) {
            name.setText(intent.getStringExtra("name"));
            schedule.setText(intent.getStringExtra("schedule"));
            frequency.setText(intent.getStringExtra("weekfreq"));
            startDate.setText(intent.getStringExtra("startdt"));
            endDate.setText(intent.getStringExtra("enddt"));
//            set medicineImage url
        }
        if (intent.getStringExtra("role").matches("Patient")) {
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
                if (validate()) {
                    System.out.println("Data Validated");
                }

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calendar=Calendar.getInstance();
                int day = calendar1.get(Calendar.DAY_OF_MONTH);
                int month = calendar1.get(Calendar.MONTH);
                int year = calendar1.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(medicineDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int myear, int mmonth, int mday) {
                        startDate.setText(mmonth + 1 + "/" + mday + "/" + myear);
                    }
                }, month, day, year);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calendar=Calendar.getInstance();
                int day = calendar2.get(Calendar.DAY_OF_MONTH);
                int month = calendar2.get(Calendar.MONTH);
                int year = calendar2.get(Calendar.YEAR);


                datePickerDialog = new DatePickerDialog(medicineDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int myear, int mmonth, int mday) {
                        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
                        endDate.setText(mmonth + 1 + "/" + mday + "/" + myear);

                    }
                }, month, day, year);
                datePickerDialog.show();
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
            startDate.setError("Start date is required!");
            return false;
        }
        if (endDate.getText().toString().isEmpty()) {
            endDate.setError("End date is required!");
            return false;
        }
        if (!(validateEndDate())) {
            endDate.setError("End date should be after start date");
            return false;
        }
//

        return true;

    }

    private boolean validateEndDate() {
        try {
            Date strdate = new SimpleDateFormat("mm/dd/yyyy").parse(startDate.getText().toString());
            Date enddate = new SimpleDateFormat("mm/dd/yyyy").parse(endDate.getText().toString());
            if (strdate.before(enddate)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;


    }
}
