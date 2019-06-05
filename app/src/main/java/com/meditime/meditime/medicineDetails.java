package com.meditime.meditime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.icu.util.Calendar;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.text.InputType;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class medicineDetails extends AppCompatActivity {

    Button photoBtn, saveBtn;
    ImageView medicineImage;
    EditText name, schedule, frequency, startDate, endDate;
    private FirebaseAuth auth;

    private FirebaseUser user;
    private String action, medicineId, photoUrl = "", patientEmail;
    private static DocumentReference mDocRef;
    FirebaseFirestore db;

    private Calendar calendar1;
    private Calendar calendar2;
    private DatePickerDialog datePickerDialog;
    private Bitmap image;
    private StorageReference mstorageReference;
    private FirebaseStorage storage;
    private String currentPhotoPath;
    private String imageName;
    private byte[] byteData;

    private final int REQUEST_TAKE_PHOTO = 1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);
        setTitle("Medicine Details");
        db = FirebaseFirestore.getInstance();

        photoBtn = findViewById(R.id.photoBtn);
        saveBtn = findViewById(R.id.saveBtn);
        medicineImage = findViewById(R.id.medicineIV);
        name = findViewById(R.id.nameET);
        schedule = findViewById(R.id.scheduleET);
        frequency = findViewById(R.id.frequencyET);
        startDate = findViewById(R.id.startDateET);
        endDate = findViewById(R.id.endDateET);

        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();
        startDate.setInputType(InputType.TYPE_NULL);
        endDate.setInputType(InputType.TYPE_NULL);
        auth = FirebaseAuth.getInstance();


        FirebaseUser user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        mstorageReference = storage.getReference();


        if (user == null) {
            startActivity(new Intent(this, SplashActivity.class));
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("action") != null) {
            action = intent.getStringExtra("action");
            patientEmail = intent.getStringExtra("email");
        }

        if (action.contains("Update")) {
            medicineId = intent.getStringExtra("medicineID");
            endDate = findViewById(R.id.endDateET);

            DocumentReference docRef = db.collection("medicines").document(medicineId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name.setText(document.getString("Name"));
                            schedule.setText(document.getString("dayFreq"));
                            endDate.setText(document.getString("enddt"));
                            if (document.getString("photoUrl") != null && !document.getString("photoUrl").isEmpty()) {
                                try {
                                    medicineImage.setImageBitmap(GetImageBitmapFromUrl(document.getString("photoUrl")));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //medicineImage.setImageURI(null);
                                //medicineImage.setImageURI(Uri.parse(document.getString("photoUrl")));
                            }
                            startDate.setText(document.getString("startdt"));
                            frequency.setText(document.getString("weekFreq"));
                        } else {
                            Log.d(medicineDetails.class.getSimpleName(), "No such document");
                        }
                    } else {
                        Log.d(medicineDetails.class.getSimpleName(), "get failed with ", task.getException());
                    }
                }
            });
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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(medicineDetails.class.getSimpleName(), "Error creating image");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.contains("Patient")) {
                    return;
                }
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


    private Bitmap GetImageBitmapFromUrl(String url) throws IOException {
        Bitmap imageBitmap = null;
        URL imgUrl = new URL(url);
        try {
            imageBitmap = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
        } catch (Exception e) {

        }
        return imageBitmap;
    }

    public void saveMedicine(View view) {
        if (!validate()) {
            return;
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (action.contains("Update")) {
            mDocRef = FirebaseFirestore.getInstance().document("medicines/" + medicineId);
            mDocRef.update(
                    "Name", name.getText().toString(),
                    "dayFreq", schedule.getText().toString(),
                    "weekFreq", frequency.getText().toString(),
                    "startdt", startDate.getText().toString(),
                    "enddt", endDate.getText().toString()
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(medicineDetails.this, "Medicine details Updated!", Toast.LENGTH_SHORT).show();
                    sendNotification(medicineId);
                    if (action.contains("Patient")) {
                        startActivity(new Intent(medicineDetails.this, PatientActivity.class));
                    } else {
                        Log.i("Pranay", "Patient email " + patientEmail);
                        startActivity(new Intent(medicineDetails.this, PrescriptionActivity.class).putExtra("email", patientEmail));
                    }
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
                    startActivity(new Intent(medicineDetails.this, PrescriptionActivity.class).putExtra("email", patientEmail));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(medicineDetails.class.getSimpleName(), "medicine adding failed " + e.toString());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            medicineImage.setImageBitmap(image);
            medicineImage.setDrawingCacheEnabled(true);
            medicineImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) medicineImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byteData = baos.toByteArray();
            uploadPhoto();
        }

    }

    private boolean validate() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Name is required!");
            name.requestFocus();
            return false;
        }
        if (schedule.getText().toString().isEmpty()) {
            schedule.setError("Schedule is required!");
            schedule.requestFocus();
            return false;
        }
        for (char c : schedule.getText().toString().toCharArray()) {
            if (c != '1' && c != '0') {
                schedule.setError("Schedule has incorrect format!");
                schedule.requestFocus();
                return false;
            }
        }

        if (schedule.getText().toString().length() != 3) {
            schedule.setError("Schedule has incorrect length format!");
            schedule.requestFocus();
            return false;
        }
        if (frequency.getText().toString().isEmpty()) {
            frequency.setError("Weekly frequency is required!");
            frequency.requestFocus();
            return false;
        }
        for (char c : frequency.getText().toString().toCharArray()) {
            if (c != '1' && c != '0') {
                frequency.setError("Schedule has incorrect format!");
                frequency.requestFocus();
                return false;
            }
        }
        if (frequency.getText().toString().length() != 7) {
            frequency.setError("Schedule has incorrect format!");
            frequency.requestFocus();
            return false;
        }
        if (startDate.getText().toString().isEmpty()) {
            name.setError("Name is required!");
            startDate.requestFocus();
            return false;
        }
        if (endDate.getText().toString().isEmpty()) {
            endDate.setError("End date is required!");
            endDate.requestFocus();
            return false;
        }
        if (!(validateEndDate())) {
            endDate.setError("End date should be after start date");
            endDate.requestFocus();
            return false;
        }
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

    private void sendNotification(String medicineId) {
        NotificationHelper notif = new NotificationHelper();
        if (action.contains("Patient")) {
            return;
        }
        notif.sendNotification(patientEmail, "Medicine Update",
                "Doctor has updated you medicine details, Please Checkout!",
                medicineId, Constants.SCHEDULE_UPDATE_COMMAND);
    }

    private void uploadPhoto() {
        Uri file = Uri.fromFile(new File("/Android/data/com.meditime.meditime/files/Pictures/" + imageName));
        final StorageReference fileRef = mstorageReference.child(imageName);
        StorageReference reference = mstorageReference.child("uploads/" + imageName);
        fileRef.getName().equals(reference.getName());
        fileRef.getPath().equals(reference.getPath());
        UploadTask uploadTask = fileRef.putBytes(byteData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("not sent");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("image sent");
//                photoUrl=fileRef.getDownloadUrl().getResult().toString();
//                System.out.println("URL " + photoUrl);
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUri = task.getResult();
                        Log.i(medicineDetails.class.getSimpleName(), downloadUri.toString());
                        photoUrl = downloadUri.toString();
                        mDocRef = FirebaseFirestore.getInstance().document("medicines/" + medicineId);
                        mDocRef.update(
                                "photoUrl", photoUrl
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }


                });
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println(currentPhotoPath);
        imageName = currentPhotoPath.substring(70);
        System.out.println(imageName);

        return image;
    }
}