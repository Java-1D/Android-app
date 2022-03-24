package com.example.myapplication2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

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


    // TODO: Check out glide and picasso for handling photos
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
                // Start new CropActivity provided by library
                // https://github.com/CanHub/Android-Image-Cropper
                CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
                options.setAspectRatio(1,1);

                activityResultLauncher.launch(options);

        }
    }

    // https://github.com/CanHub/Android-Image-Cropper/issues/199
    private final ActivityResultLauncher<CropImageContractOptions> activityResultLauncher = registerForActivityResult(new CropImageContract(),
            result -> {

                if (result.isSuccessful()) {
                    selectedImageUri = result.getUriContent();
                    cropImageView.setImageURI(selectedImageUri);
                } else {
                    Log.d(TAG, ": error");
                }

            });
}
