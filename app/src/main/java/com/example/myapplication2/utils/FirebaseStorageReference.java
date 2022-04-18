package com.example.myapplication2.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication2.objectmodel.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

public abstract class FirebaseStorageReference {
    private static final String TAG = "FirebaseStorage";
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private StorageReference getStorageReference(String documentID) {
        return firebaseStorage.getReference().child(documentID);
    }

    private void uploadData(StorageReference storageReference) {

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
                        Log.i(TAG, "onFailure: Storage upload successful");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadFailed();
                Log.i(TAG, "onFailure: Storage upload unsuccessful");
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
