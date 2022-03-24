package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    Button createButton;
    ImageView locationImage;
    EditText eventTitleCreate;
    EditText eventDescriptionCreate;
    EditText venueCreate;
    EditText topicCreate;

    Intent intent;

    Uri selectedImageUri;

    String currentPhotoPath;

    static final String TAG = "CreateEvents";

    static final int REQUEST_ID_CAMERA = 1;
    static final int REQUEST_ID_STORAGE = 2;
    static final int REQUEST_CROP_PIC = 3;

    // TODO: Check out glide and picasso for handling photos
    // http://bumptech.github.io/glide/doc/download-setup.html
    // https://square.github.io/picasso/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events);

        // Casting to ensure that the types are correct
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
        // create a menuOption Array
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" };

        // Create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    if (checkAndRequestPermission(CreateEventActivity.this, Manifest.permission.CAMERA, REQUEST_ID_CAMERA)) {
                        // Code adapted from:
                        // https://developer.android.com/training/camera/photobasics#TaskGallery
                        // Implicit event access camera
                        // Create file to save the photo to so that we are able to obtain the full-sized image in the end
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.d(TAG, "File creation error");
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(CreateEventActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_ID_CAMERA);
                        }
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


    // Handle implicit intent calls
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_ID_CAMERA:
                    // Don't need to check for data != null as we never used it, but only to decode path
                    if (resultCode == RESULT_OK) {
                        // Decoding the bitmap into a URI so that we can use it in performCrop();
                        Bitmap selectedImageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), selectedImageBitmap, "Title", null);
                        selectedImageUri = Uri.parse(path);

                        performCrop();
                    }
                    break;
                case REQUEST_ID_STORAGE:
                    if (resultCode == RESULT_OK && data != null) {
                        // Getting URI from the intent
                        // TODO: Currently having bugs when running on emulator, possibly due to getting it from file explorer rather than gallery?
                        //       possible fix might include saving the photo, but not ideal because it will keep creating in memory
                        selectedImageUri = data.getData();
                        performCrop();

                    }
                    break;
                case REQUEST_CROP_PIC:
                    if (resultCode == RESULT_OK && data != null) {
                        // get the returned data
                        Bundle extras = data.getExtras();
                        Uri uri = data.getData();
                        Log.d(TAG, "onActivityResult: "+ uri.toString());


                        // get the cropped bitmap
                        // TODO: for emulator, find out why extras is always null
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            locationImage.setImageBitmap(photo);
                        } else {
                            Log.d(TAG, "onCropActivityResult: failed");
                        }
                    }
            }
        }
    }

    // Crop images
    private void performCrop() {
        // take care of exceptions when the device does not have a crop action
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(selectedImageUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            // TODO: might have bugs here for emulator
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
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

    // Handle permission results after ActivityCompat.requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // TODO: try and modularize this code as it is repeated from the top & possibly in other activities
        //       also to fix the possibility of changing a code and being able to change it everywhere also
        // NOTE: possible issue with trying to modularize it with other activities as this is an Overriden function
        //       possible fix might be to create another class to do this??
        switch (requestCode) {
            case REQUEST_ID_CAMERA:
                // If camera access is still denied after requesting
                if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.camera_access, Toast.LENGTH_SHORT).show();
                } else {
                    // Implicit event access camera
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d(TAG, "File creation error");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(CreateEventActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_ID_CAMERA);
                    }
                }
                break;
            case REQUEST_ID_STORAGE:
                if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.storage_access, Toast.LENGTH_SHORT).show();
                } else {
                    // Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, requestCode);
                }
                break;
        }
    }

    // Function to create an empty image file so that we are able to save the full photo to later
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void cropPicture() {
    }

}
