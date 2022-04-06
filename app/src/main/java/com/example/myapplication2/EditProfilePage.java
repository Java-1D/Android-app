package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Objects;

//TODO Data Persistence for EditProfilePage
public class EditProfilePage extends AppCompatActivity {
    private static final String TAG = "EditProfilePage";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    FirebaseStorage storage;
    String profileDocumentId;
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());

    //Data fields present in UI elements
    enum Data {
        PROFILE_PIC,
        NAME,
        PILLAR,
        TERM,
        MODULE,
        BIO
    }

    //UI elements
    ImageView profilePicture;
    ImageView backButton;
    EditText editName;
    EditText editPillar;
    EditText editTerm;
    EditText editModules;
    EditText editBio;
    Button confirmEdit;

    boolean[] selectedModule;
    ArrayList<Integer> moduleList = new ArrayList<>();
    String[] moduleArray = {"50.001: Shit", "50.002: Lao Sai" ,"50.003: Pang Sai", "50.004: Jiak Sai", "50.005: Bak Sai"};

    //Shared Preferences to store Objects as a String
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEditor;

    //Button interactions in Profile Page Activity
    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.editProfilePicture:
                    chooseProfilePic();
                    break;
                case R.id.confirmButton:
                    updateProfileDocument(profileDocumentId, new Intent(EditProfilePage.this, ProfilePage.class));
                    break;
                case R.id.backButton:
                    startActivity(new Intent(EditProfilePage.this, MainPageActivity.class));
                    break;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);
        Log.i(TAG, "onCreate is called");

        //initialise Firestore db
        db = FirebaseFirestore.getInstance();

        //initialise Firebase Cloud Storage
        storage = FirebaseStorage.getInstance();

        //Retrieve Object Data from SharedPrefs
        sharedPrefs = getSharedPreferences("PROFILE_PAGE", MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();
        profileDocumentId = sharedPrefs.getString("PROFILE_ID", null);
        Log.i(TAG, "Profile Document ID Retrieved: " + profileDocumentId);
        getProfileData(profileDocumentId);

        //initialise UI elements
        profilePicture = findViewById(R.id.editProfilePicture);
        backButton = findViewById(R.id.backButton);
        editName = findViewById(R.id.editName);
        editPillar = findViewById(R.id.editPillar);
        editTerm = findViewById(R.id.editTerm);
        editModules = findViewById(R.id.editModules);
        editBio = findViewById(R.id.editBio);
        confirmEdit = findViewById(R.id.confirmButton);


        selectedModule = new boolean[moduleArray.length];
        editModules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                        EditProfilePage.this
                );
                builder.setTitle("Select Modules");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(moduleArray, selectedModule, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            moduleList.add(i);
                            Collections.sort(moduleList);
                        }else {
                            moduleList.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < moduleList.size(); j ++) {
                            stringBuilder.append(moduleArray[moduleList.get(j)]);
                            if (j != moduleList.size() - 1) {
                                stringBuilder.append("\n");
                            }
                        }
                        editModules.setText(stringBuilder.toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < selectedModule.length; j ++){
                            selectedModule[j] = false;
                            moduleList.clear();
                            editModules.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        //initialise buttons
        profilePicture.setOnClickListener(new ClickListener());
        confirmEdit.setOnClickListener(new ClickListener());
        backButton.setOnClickListener(new ClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart is called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume is called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause is called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop is called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy is called");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
//        Save the user's current workout state
//        savedInstanceState.putInt(WORKOUT_STATE, currentState);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        Restore state
//        currentScore = savedInstanceState.getInt(WORKOUT_STATE);
    }


    public void updateProfileDocument(String Id, Intent intent) {
        Date date = new Date();
        Timestamp current = new Timestamp(date);
        DocumentReference docRef = db.collection("Profiles").document(Id);
        if (!editName.getText().toString().matches("")) {
            profile.get().setName(editName.getText().toString());
        }
        if (!editPillar.getText().toString().matches("")) {
            profile.get().setPillar(editPillar.getText().toString());
        }
        if (!editTerm.getText().toString().matches("")) {
            profile.get().setTerm((Integer.parseInt(editTerm.getText().toString())));
        }
        if (!editBio.getText().toString().matches("")) {
            profile.get().setBio(editBio.getText().toString());
        }
        updateFirestore(Id, profile.get(), intent);
    }

    public void updateFirestore(String Id, ProfileModel model, Intent intent) {
        db.collection("Profiles").document(Id).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                if (intent != null) {
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);

            }
        });
    }

    //Set UI Elements using data from Firebase
    public void setUIElements(ProfileModel profile) {
        //Set Image
        setImage(profile.getImagePath());
    }

    //Set Image for ImageView
    public void setImage(String imageURL) {
        Picasso.get().load(imageURL).resize(120, 120).centerCrop().transform(new Utils.CircleTransform()).into(profilePicture);
        Log.i(TAG, "Profile Picture set");
    }

    //Firebase-Specific Methods
    public DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }

    //Get Data from Profiles Collection
    public void getProfileData(String profileDocumentId) {
        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Log.i(TAG, "File Path in Firebase: " + profileRef.getPath());
                    EditProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                    Log.i(ProfileModel.TAG, "Contents of Firestore Document: "+ Objects.requireNonNull(document.toObject(ProfileModel.class)).toString());
                    EditProfilePage.this.setUIElements(EditProfilePage.this.profile.get());

                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error retrieving document from Firestore", e);
            }
        });
    }

    private void chooseProfilePic() {
        View dialogView;
        AlertDialog.Builder builder;
        AlertDialog alertDialogProfilePicture;
        LayoutInflater inflater;
        ImageView takePic;
        ImageView chooseGallery;

        builder = new AlertDialog.Builder(this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_picture_alert, null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        takePic = dialogView.findViewById(R.id.takePic);
        chooseGallery = dialogView.findViewById(R.id.chooseGallery);

        alertDialogProfilePicture = builder.create();
        alertDialogProfilePicture.show();

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermission()) {
                    takePicFromCamera();
                    alertDialogProfilePicture.cancel();
                }
            }
        });

        chooseGallery.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicFromGallery();
                alertDialogProfilePicture.cancel();
            }
        }));

    }

    private void takePicFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    private void takePicFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    Log.i(TAG, "URI "+ selectedImageUri);
                    uploadImagetoCloudStorage(selectedImageUri);
                    profilePicture.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    uploadImagetoCloudStorage(bitmapImage);
                    profilePicture.setImageBitmap(bitmapImage);
                }
        }
    }

    private void uploadImagetoCloudStorage(Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("Profiles/"+ profileDocumentId);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                profile.get().setImagePath(downloadUri.toString());
                Log.i(TAG, "Profile imagePath successfully updated: " + profile.get().getImagePath());
            } else {
                // Handle failures
                Log.i(TAG, "Unable to obtain URI from Cloud Storage");
            }
        });
    }

    private void uploadImagetoCloudStorage(Bitmap bitmap) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("Profiles/"+ profileDocumentId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                profile.get().setImagePath(downloadUri.toString());
                Log.i(TAG, "Profile imagePath successfully updated: " + profile.get().getImagePath());
            } else {
                // Handle failures
                Log.i(TAG, "Unable to obtain URI from Cloud Storage");
            }
        });
    }

    private boolean checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}