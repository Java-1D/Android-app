package com.example.myapplication2.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public abstract class FirebaseStorageReference {
    private static final String TAG = "FirebaseStorage";
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private StorageReference getStorageReference(String documentID) {
        return firebaseStorage.getReference().child(documentID);
    }

    public void callbackOnFailure(@NonNull Exception e) {
        Log.w(TAG, "Error retrieving document from Firestore", e);
    }

    public void uploadImage(Bitmap bitmap, String documentID) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = getStorageReference(documentID).putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String imageURL = task.getResult().toString();
                        uploadSuccess(imageURL);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callbackOnFailure(e);
                uploadFailed();
            }
        });
    }

    public void uploadImage(ImageView imageView, String documentID){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        uploadImage(bitmap, documentID);
    }

    public abstract void uploadSuccess(String string);

    public abstract void uploadFailed();
}
