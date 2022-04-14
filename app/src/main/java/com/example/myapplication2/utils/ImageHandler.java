package com.example.myapplication2.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.myapplication2.CreateEventActivity;
import com.example.myapplication2.R;

public class ImageHandler extends AppCompatActivity {
    private String TAG = "ImageHandler";

    public ImageView image;


    /**
     * CropImage helper functions
     * Call function: chooseImage()
     */
    //<editor-fold desc="CropImage helper functions">
    // Creating AlertDialog for user action
    // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
    // Edited the part where user can still click and request individually
    public void chooseImage() {
        // Creating AlertDialog for user action
        // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
        // Edited the part where user can still click and request individually
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        Log.i(TAG, "chooseImage: Dialog launched");
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageHandler.this);
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
        if (ContextCompat.checkSelfPermission(ImageHandler.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
        if (ContextCompat.checkSelfPermission(ImageHandler.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                            image.setImageURI(selectedImageUri);
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
                        Toast.makeText(ImageHandler.this, R.string.camera_access, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ImageHandler.this, R.string.storage_access, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "PermissionRequest: Gallery access denied");
                    }

                }
            });
    //</editor-fold>


}
