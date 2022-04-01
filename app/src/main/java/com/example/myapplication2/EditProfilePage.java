package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
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
import com.example.myapplication2.objectmodel.UserModel;
import com.example.myapplication2.utils.Container;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

//TODO Data Persistence for EditProfilePage
public class EditProfilePage extends AppCompatActivity {
    private static final String TAG = "EditProfilePage";

    enum Data {
        NAME,
        PILLAR,
        TERM,
        MODULE,
        BIO
    }

    ImageView profilePicture;
    ImageView backButton;
    EditText editName;
    EditText editPillar;
    EditText editTerm;
    EditText editModules;
    EditText editBio;
    Button confirmEdit;

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    String profileDocumentId;
    final Container<ProfileModel> profile = new Container<>(new ProfileModel());

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.editProfilePicture:
                    chooseProfilePic();
                    break;
                case R.id.confirmButton:
                    if (!editName.getText().toString().matches("")) {
                        updateProfileDataString(profileDocumentId, Data.NAME, editName.getText().toString());
                    }
                    if (!editPillar.getText().toString().matches("")) {
                        updateProfileDataString(profileDocumentId, Data.PILLAR, editPillar.getText().toString());
                    }
                    if (!editTerm.getText().toString().matches("")) {
                        updateProfileDataNumber(profileDocumentId, Data.TERM, Long.parseLong(editTerm.getText().toString()));
                    }
                    if (!editBio.getText().toString().matches("")) {
                        updateProfileDataString(profileDocumentId, Data.BIO, editBio.getText().toString());
                    }
                    startActivity(new Intent(EditProfilePage.this, ProfilePage.class));
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

        if (savedInstanceState != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                profileDocumentId = extras.getString("PROFILE_ID");
            }
            else {
                profileDocumentId = null;
                Log.w(TAG, "Intent is empty.");
            }
        }
        else {
            profileDocumentId = null;
            Log.w(TAG, "SavedInstanceState is empty.");
        }

        profileDocumentId = "Test";

        setContentView(R.layout.activity_edit_profile_page);

        profilePicture = findViewById(R.id.editProfilePicture);
        backButton = findViewById(R.id.backButton);
        editName = findViewById(R.id.editName);
        editPillar = findViewById(R.id.editPillar);
        editTerm = findViewById(R.id.editTerm);
        editModules = findViewById(R.id.editModules);
        editBio = findViewById(R.id.editBio);
        confirmEdit = findViewById(R.id.confirmButton);

        profilePicture.setOnClickListener(new ClickListener());
        confirmEdit.setOnClickListener(new ClickListener());
        backButton.setOnClickListener(new ClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void chooseProfilePic() {
        View dialogview;
        AlertDialog.Builder builder;
        AlertDialog alertDialogProfilePicture;
        LayoutInflater inflater;
        ImageView takePic;
        ImageView chooseGallery;

        builder = new AlertDialog.Builder(this);
        inflater = getLayoutInflater();
        dialogview = inflater.inflate(R.layout.add_picture_alert, null);
        builder.setCancelable(false);
        builder.setView(dialogview);

        takePic = dialogview.findViewById(R.id.takePic);
        chooseGallery = dialogview.findViewById(R.id.chooseGallery);

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
                    //TODO Store Image in Firestore
                    profilePicture.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    //TODO Store Image in Firestore
                    profilePicture.setImageBitmap(bitmapImage);
                }
        }
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

    public void updateProfileDataString(String Id, Data data, String value) {
        Date date = new Date();
        Timestamp current = new Timestamp(date);
        DocumentReference docRef = db.collection("Profiles").document(Id);
        switch (data) {
            case NAME:
                docRef.update("name", value);
                Log.i(TAG, "Update Profile Name");
                break;
            case PILLAR:
                docRef.update("pillar", value);
                Log.i(TAG, "Update Pillar");
                break;
            case BIO:
                docRef.update("bio", value);
                Log.i(TAG, "Update Bio");
                break;
            default:

        }
        docRef.update("profileUpdated", current);
    }

    public void updateProfileDataNumber(String Id, Data data, Long value) {
        Date date = new Date();
        Timestamp current = new Timestamp(date);
        DocumentReference docRef = db.collection("Profiles").document(Id);
        switch (data) {
            case TERM:
                docRef.update("term", value);
                Log.i(TAG, "Update Term");
                break;
            default:

        }
        docRef.update("profileUpdated", current);
    }

}