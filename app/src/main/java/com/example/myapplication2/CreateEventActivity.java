package com.example.myapplication2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.myapplication2.objectmodel.EventModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView createImage;
    Button setImageButton;

    EditText createName;
    EditText createDescription;
    EditText createVenue;
    EditText createModule;
    EditText createCapacity;
    EditText createStart;
    EditText createEnd;

    Button createButton;

    // Global variable to take note of Calendar object for createDate
    // Used because it cannot be stored in EditText or any other type of texts
    Calendar startDateTime;
    Calendar endDateTime;

    static final String TAG = "CreateEvents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events);

        // Casting to ensure that the types are correct
        createImage = findViewById(R.id.createEventImage);
        setImageButton = findViewById(R.id.setImageButton);

        createName = (EditText) findViewById(R.id.createEventName);
        createDescription = (EditText) findViewById(R.id.createEventDescription);
        createVenue = (EditText) findViewById(R.id.createEventVenue);
        createModule = (EditText) findViewById(R.id.createEventModule);
        createCapacity = (EditText) findViewById(R.id.createEventCapacity);
        createStart = (EditText) findViewById(R.id.createEventStartDateTime);
        createEnd = (EditText) findViewById(R.id.createEventEndDateTime);

        createButton = (Button) findViewById(R.id.createEventButton);

        // TODO: Should we do a network check here?
        createButton.setOnClickListener(this);
        setImageButton.setOnClickListener(this);
        createStart.setOnClickListener(this);
        createEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.createEventButton:

                // Check if data are all filled and valid
                if (invalidData(createName) |
                        invalidData(createDescription) |
                        invalidData(createVenue) |
                        invalidData(createModule) |
                        invalidData(createCapacity) |
                        invalidData(createStart) |
                        invalidData(createEnd)) {
                    return;
                }

                // TODO: Check that event name does not repeat?

                String eventName = createName.getText().toString();
                String eventDescription = createDescription.getText().toString();
                String eventVenue = createVenue.getText().toString();

                // TODO: Module should be a DocumentReference but idk how to get
                // String eventModule = createVenue.getText().toString();
                DocumentReference eventModule = null;

                Integer eventCapacity = Integer.parseInt(createCapacity.getText().toString());

                // TODO: This should upload online and then get a DocumentReference
                // Bitmap eventImage = createImage.getDrawingCache();
                DocumentReference eventImage = null;

                // TODO: Get DocumentReference for current user
                DocumentReference userCreated = null;

                EventModel eventModel = new EventModel(
                        eventName,
                        eventDescription,
                        eventVenue,
                        eventModule,
                        eventCapacity,
                        startDateTime.getTime(),
                        endDateTime.getTime(),
                        eventImage,
                        userCreated
                        );

                // https://firebase.google.com/docs/firestore/quickstart#java
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Events").document(eventName).set(eventModel);
                Log.i(TAG, "createEvent: Success");


//                // Create explicit event to go into MainPage
//                Intent intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
//                startActivity(intent);
                break;

            case R.id.setImageButton:
                chooseImage();
                break;

            case R.id.createEventStartDateTime:
                dateTimePicker(createStart);
                break;

            case R.id.createEventEndDateTime:
                dateTimePicker(createEnd);
        }
    }

    /**
     * Entry validation
     */
    // https://www.c-sharpcorner.com/UploadFile/1e5156/validation/
    boolean invalidData(EditText editText) {
        if (editText.getText().toString().length() == 0) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
            return true;
        } else {
            return false;
        }
    }

    /**
     * DateTimePicker
     */
    // Adapted to take in inputs and set date/time for different texts
    // https://stackoverflow.com/questions/2055509/how-to-create-a-date-and-time-picker-in-android
    public void dateTimePicker(EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        Calendar dateTime;

        // Assign for different widgets
        if (editText == createStart) {
            if (startDateTime == null) {
                startDateTime = Calendar.getInstance();
            }
            dateTime = startDateTime;
        } else {
            if (endDateTime == null) {
                endDateTime = Calendar.getInstance();
            }
            dateTime = endDateTime;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);

                        // Adapted and allowed setting of text
                        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM @ hh:mm aa");
                        String stringDateTime = dateFormat.format(dateTime.getTime());

                        editText.setText(stringDateTime);
                        Log.i(TAG, "dateTimePicker: Success for " + getResources().getResourceEntryName(editText.getId()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        // Limiting input to valid time frame
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        if (editText == createStart) {
            if (endDateTime != null) {
                datePickerDialog.getDatePicker().setMaxDate(endDateTime.getTimeInMillis());
            }
        } else {
            if (startDateTime != null) {
                datePickerDialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis());
            }
        }
        datePickerDialog.show();
        Log.i(TAG, "dateTimePicker: Dialog launched");
    }

    /**
     * CropImage helper functions
     * Call function: chooseImage()
     */
    //<editor-fold desc="CropImage helper functions">
    // Creating AlertDialog for user action
    // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
    // Edited the part where user can still click and request individually
    void chooseImage() {
        // Creating AlertDialog for user action
        // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
        // Edited the part where user can still click and request individually
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        Log.i(TAG, "chooseImage: Dialog launched");
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    cameraLaunch();
                    Log.i(TAG, "chooseImage: Camera chosen");
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    galleryLaunch();
                    Log.i(TAG, "chooseImage: Gallery chosen");
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                    Log.i(TAG, "chooseImage: Dialog dismissed");
                }
            }
        });
        builder.show();
    }

    void cameraLaunch() {
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Start new CropActivity provided by library
            // https://github.com/CanHub/Android-Image-Cropper
            CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
            options.setAspectRatio(1,1);
            options.setImageSource(false, true);
            cropImage.launch(options);
            Log.i(TAG, "cameraLaunch: Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            Log.i(TAG, "cameraLaunch: Permission for camera requested");
        }
    }

    void galleryLaunch() {
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Start new CropActivity provided by library
            // https://github.com/CanHub/Android-Image-Cropper
            CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
            options.setAspectRatio(1,1);
            options.setImageSource(true, false);
            cropImage.launch(options);
            Log.i(TAG, "galleryLaunch: Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestGalleryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.i(TAG, "galleryLaunch: Permission for camera requested");
        }
    }

    // Used for receiving activity result from CropImage
    // Read on Android contract options
    // https://developer.android.com/training/basics/intents/result
    // https://www.youtube.com/watch?v=DfDj9EadOLk
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(
            new CropImageContract(),
            new ActivityResultCallback<CropImageView.CropResult>() {
                @Override
                public void onActivityResult(CropImageView.CropResult result) {
                    if (result!=null ) {
                        if (result.isSuccessful() && result.getUriContent() != null) {
                            Uri selectedImageUri = result.getUriContent();
                            createImage.setImageURI(selectedImageUri);
                            Log.i(TAG, "onActivityResult: Cropped image set");
                        } else {
                            Log.d(TAG, "onActivityResult: Cropping returned null");
                        }
                    }
                }
            });

    // Using launchers to request for permission
    // https://developer.android.com/training/permissions/requesting
    ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) {
                        // Permission is granted. Continue the action or workflow in your app.
                        cameraLaunch();
                    } else {
                        Toast.makeText(CreateEventActivity.this, R.string.camera_access, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "PermissionRequest: Camera access denied");
                    }
                }
            });

    ActivityResultLauncher<String> requestGalleryPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) {
                        // Permission is granted. Continue the action or workflow in your app.
                        galleryLaunch();
                    } else {
                        Toast.makeText(CreateEventActivity.this, R.string.storage_access, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "PermissionRequest: Gallery access denied");
                    }

                }
            });
    //</editor-fold>

}
