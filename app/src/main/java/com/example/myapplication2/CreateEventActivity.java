package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    ImageView cropImageView;
    Button setImageButton;
    EditText eventTitleCreate;
    EditText eventDescriptionCreate;
    EditText venueCreate;
    EditText topicCreate;

    Intent intent;

    Uri selectedImageUri;

    static final String TAG = "CreateEvents";

    // TODO: Check out glide and picasso for handling photos (?)
    //       Might need for profile page if wanting a round photo
    // http://bumptech.github.io/glide/doc/download-setup.html
    // https://square.github.io/picasso/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events);

        // Casting to ensure that the types are correct
        createButton = (Button) findViewById(R.id.createButton);
        cropImageView = findViewById(R.id.cropImageView);
        setImageButton = findViewById(R.id.setImageButton);
        eventTitleCreate = (EditText) findViewById(R.id.eventTitleCreate);
        eventDescriptionCreate = (EditText) findViewById(R.id.eventDescriptionCreate);
        venueCreate = (EditText) findViewById(R.id.venueCreate);
        topicCreate = (EditText) findViewById(R.id.topicCreate);

        // Set default image for the location
        cropImageView.setImageResource(R.drawable.sch_picture);

        // TODO: Should we do a network check here?
        createButton.setOnClickListener(this);
        setImageButton.setOnClickListener(this);
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

            case R.id.setImageButton:
                // Creating AlertDialog for user action
                // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
                // Edited the part where user can still click and request individually
                final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
                // create a dialog for showing the optionsMenu
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
                // set the items in builder
                builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(optionsMenu[i].equals("Take Photo")){
                            cameraLaunch();
                        }
                        else if(optionsMenu[i].equals("Choose from Gallery")){
                            galleryLaunch();
                        }
                        else if (optionsMenu[i].equals("Exit")) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
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
                            selectedImageUri = result.getUriContent();
                            cropImageView.setImageURI(selectedImageUri);
                            Log.i(TAG, "onActivityResult: cropped image set");
                        } else {
                            Log.d(TAG, "onActivityResult: cropping returned null");
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
                        Log.i(TAG, "Camera access denied");
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
                        Log.i(TAG, "Gallery access denied");
                    }

                }
            });

    void cameraLaunch() {
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Start new CropActivity provided by library
            // https://github.com/CanHub/Android-Image-Cropper
            CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
            options.setAspectRatio(1,1);
            options.setImageSource(false, true);
            cropImage.launch(options);
            Log.i(TAG, "Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            Log.i(TAG, "Permission for camera requested");
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
            Log.i(TAG, "Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestGalleryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.i(TAG, "Permission for camera requested");
        }
    }




}
