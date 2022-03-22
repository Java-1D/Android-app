package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    ImageView locationImage;
    EditText eventTitleCreate;
    EditText eventDescriptionCreate;
    EditText venueCreate;
    EditText topicCreate;

    Intent intent;

    static final String TAG = "Create Events";
    static final int REQUEST_IMAGE_GET = 1;

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
                // Check for permission to camera and gallery
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 112);
                    }
                }

                // Create implicit event to go into the person's gallery
                selectImage();
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            // Bitmap thumbnail = data.getParcelable("data");
            Uri fullPhotoUri = data.getData();
            // Do work with photo saved at fullPhotoUri
        }
    }
}
