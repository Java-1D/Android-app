package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    ImageView locationImage;
    EditText eventTitleCreate;
    EditText eventDescriptionCreate;
    EditText venueCreate;
    EditText topicCreate;

    Intent intent;

    Uri picUri;

    static final String TAG = "Create Events";

    static final int REQUEST_ID_CAMERA = 1;
    static final int REQUEST_ID_STORAGE = 2;
    static final int REQUEST_CROP_PIC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events);


        createButton = (Button) findViewById(R.id.createButton);
        locationImage = (ImageView) findViewById(R.id.locationImageCreate);
        eventTitleCreate = (EditText) findViewById(R.id.eventTitleCreate);
        eventDescriptionCreate = (EditText) findViewById(R.id.eventDescriptionCreate);
        venueCreate = (EditText) findViewById(R.id.venueCreate);
        topicCreate = (EditText) findViewById(R.id.topicCreate);

        // Set default image for the location
        locationImage.setImageResource(R.drawable.sch_picture);

        // TODO: Should we do a network check here?
        createButton.setOnClickListener(this);
        locationImage.setOnClickListener(this);

        // TODO: Add this into main activity to ask when they first open the app (?)
//        // Check for permission to camera and gallery
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_DENIED){
//                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permission, 112);
//            }
//        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.createButton:
                // TODO: Push data onto firebase

                // Create explicit event to go into MainPage
                intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;

            case R.id.locationImageCreate:
                // Create implicit event to go into the person's gallery
                chooseImage(CreateEventActivity.this);
        }
    }

    // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
    // Edited the part where user can still click and request individually
    // Function to let the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // Create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    if (checkAndRequestPermission(CreateEventActivity.this, Manifest.permission.CAMERA, REQUEST_ID_CAMERA)) {
                        // Implicit event access camera
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, REQUEST_ID_CAMERA);
                    }
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    if (checkAndRequestPermission(CreateEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_ID_STORAGE)) {
                        // Implicit event to access storage
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , REQUEST_ID_STORAGE);
                    }
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    // Implicit event return and to obtain data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_ID_CAMERA:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        locationImage.setImageBitmap(selectedImage);
                        performCrop();
                    }
                    break;
                case REQUEST_ID_STORAGE:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        locationImage.setImageURI(selectedImage);
                        performCrop();
                    }
                    break;
            }
        }
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Check and request for permission to use camera and external storage function
    // A static method that can be used everywhere
    public static boolean checkAndRequestPermission(final Activity context, String permission, int requestCode) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context, permission);

        List<String> listPermissionsNeeded = new ArrayList<>();

        // Check if permission have already been allowed
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission);
        }

        // Check if permission is needed
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    requestCode);

            return false;
        }
        return true;
    }

    // Handled permission results
    // Adapted to go right into the implicit intent if it is allowed
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_CAMERA:
                // if permission is not granted
                if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.camera_access, Toast.LENGTH_SHORT).show();
                } else {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, requestCode);
                }
                break;
            case REQUEST_ID_STORAGE:
                if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.storage_access, Toast.LENGTH_SHORT).show();
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , requestCode);
                }
                break;
        }
    }
}
