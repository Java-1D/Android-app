package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {


//    public static Uri convertBitmapToUri(Intent data) {
//        Bitmap selectedImages = (Bitmap) data.getExtras().get("data");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        selectedImages.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(getContentResolver(), selectedImages, "Title", null);
//        return Uri.parse(path);
//    }
}
