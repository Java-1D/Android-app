package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.FirebaseDocument;
import com.example.myapplication2.utils.FirebaseQuery;
import com.example.myapplication2.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class EditProfilePage extends AppCompatActivity {
    private static final String TAG = "EditProfilePage";
    private static final String PROFILE_ID = "PROFILE_ID";
    //Objects to handle data from Firebase
    FirebaseFirestore db;
    FirebaseStorage storage;

    //String to retrieve Profile Document from Firestore
    String profileDocumentId;
    //Store Profile Document to set the Image and relevant information of the User
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());

    //Store Hashmap of name, DocumentReference to update ArrayList of Modules
    Map<String, DocumentReference> modulesMap = new HashMap<>();
    //Update ProfileModel modules with this arraylist if arraylist != null
    final FirebaseContainer<ArrayList<DocumentReference>> modules = new FirebaseContainer<>(new ArrayList<>());

    //Initialise elements for Modules DropDown
    boolean[] selectedModule;
    ArrayList<Integer> moduleList = new ArrayList<>();
    String[] moduleArray;

    //UI elements
    ImageView profilePicture;
    ImageView backButton;
    EditText editName;
    EditText editPillar;
    EditText editTerm;
    TextView editModules;
    EditText editBio;
    Button confirmEdit;


    //Button interactions in Profile Page Activity
    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.editProfilePicture) {
                Intent intent = new Intent(EditProfilePage.this, ImageHandlerActivity.class);
                startActivityForResult(intent, ImageHandlerActivity.IMAGECROP);
            } else if (id == R.id.confirmButton) {
                Intent confirmIntent = new Intent(EditProfilePage.this, ProfilePage.class);
                confirmIntent.putExtra(PROFILE_ID, profileDocumentId);
                updateProfileDocument(profileDocumentId, confirmIntent);
            } else if (id == R.id.backButton) {
                Intent backIntent = new Intent(EditProfilePage.this, ProfilePage.class);
                backIntent.putExtra(PROFILE_ID, profileDocumentId);
                startActivity(backIntent);
            } else {
                Log.w(TAG, "Button not Found");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageHandlerActivity.IMAGECROP) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getParcelableExtra("croppedImage");
                uploadImageToCloudStorage(selectedImageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "PermissionRequest: Result cancelled");
            } else if (resultCode == ImageHandlerActivity.CAMERADENIED) {
                Log.i(TAG, "PermissionRequest: Camera access denied");
            } else if (resultCode == ImageHandlerActivity.GALLERYDENIED) {
                Log.i(TAG, "PermissionRequest: Gallery access denied");
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

        //Retrieve profileDocumentId from Intent
        Intent intent = getIntent();
        profileDocumentId = intent.getStringExtra(PROFILE_ID);
        if (profileDocumentId == null) {
            Log.w(TAG, "Profile Document ID is null");
            finish();
        } else {
            Log.i(TAG, "Profile Document ID Retrieved: " + profileDocumentId);
        }

        //Get Profile Data from Firestore
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

        //initialise buttons
        profilePicture.setOnClickListener(new ClickListener());
        confirmEdit.setOnClickListener(new ClickListener());
        backButton.setOnClickListener(new ClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart is called");

        getAllModulesFromFirebase();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");

        getAllModulesFromFirebase();
    }

    //Get Data from Profiles Collection
    protected void getProfileData(String profileDocumentId) {
        FirebaseDocument firebaseDocument = new FirebaseDocument() {
            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                Log.i(ProfileModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ProfileModel.class)));
                EditProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                Log.i(ProfileModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ProfileModel.class)));
                EditProfilePage.this.setUIElements(EditProfilePage.this.profile.get());
            }
        };

        firebaseDocument.run(ProfileModel.getCollectionId(), profileDocumentId);
    }

    //Set UI Elements using data from Firebase
    protected void setUIElements(ProfileModel profile) {
        //Set Text
        editName.setText(profile.getName());
        editPillar.setText(profile.getPillar());
        editTerm.setText(String.valueOf(profile.getTerm()));
        editBio.setText(profile.getBio());

        //Set Image
        setImage(profile.getImagePath());
    }

    //Set Image for ImageView
    protected void setImage(String imageURL) {
        Picasso.get().load(imageURL).resize(120, 120).centerCrop().transform(new Utils.CircleTransform()).into(profilePicture);
        Log.i(TAG, "Profile Picture set");
    }

    protected void getAllModulesFromFirebase() {
        String collectionId = "Modules";
        FirebaseQuery firebaseQuery = new FirebaseQuery() {

            @Override
            public void callbackOnSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Log.i(TAG, document.getId() + " => " + document.getData() + "\n" + document.getReference());
                    modulesMap.put(document.getString("name"), document.getReference());
                }

                Log.i(TAG, String.valueOf(modulesMap.keySet()));
                Set<String> keys = modulesMap.keySet();
                moduleArray = keys.toArray(new String[keys.size()]);

                //Build String to set Text to editModules
                Arrays.sort(moduleArray);
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < moduleArray.length; j++) {
                    String key = moduleArray[j];
                    stringBuilder.append(key);
                    if (j != moduleList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }

                //Set Dropdown Text
                editModules.setText(stringBuilder.toString());

                //Build Dropdown list
                buildModuleDropDown(moduleArray);
            }
        };
        firebaseQuery.run(collectionId);
    }

    protected void buildModuleDropDown(String[] moduleArray) {
        selectedModule = new boolean[moduleArray.length];

        editModules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditProfilePage.this);
                builder.setTitle("Select Modules").setCancelable(false)
                        .setMultiChoiceItems(moduleArray, selectedModule,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if (b) {
                                    moduleList.add(i);
                                    Collections.sort(moduleList);
                                } else {
                                    moduleList.remove(Integer.valueOf(i));
                                }
                            }
                        }).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int j = 0; j < moduleList.size(); j++) {
                                    String key = moduleArray[moduleList.get(j)];
                                    stringBuilder.append(key);
                                    if (j != moduleList.size() - 1) {
                                        stringBuilder.append(", ");
                                    }
                                    Log.i(TAG, "Modules Key: " + modulesMap.get(key));
                                    modules.get().add(modulesMap.get(key));
                                }
                                Log.i(TAG, "Modules Array: " + modules.get().toString());
                                editModules.setText(stringBuilder.toString());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Arrays.fill(selectedModule, false);
                                moduleList.clear();
                                editModules.setText("");
                                modules.get().clear();
                                Log.i(TAG, "Modules Array: " + modules.get().toString());
                            }
                        }).show();
            }
        });
    }

    //Upload image from Gallery/Camera to Firebase Cloud Storage
    private void uploadImageToCloudStorage(Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("Profiles" + "/" + profileDocumentId);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                profile.get().setImagePath(downloadUri.toString());
                setImage(profile.get().getImagePath());
                Log.i(TAG, "Profile imagePath successfully updated: " + profile.get().getImagePath());
            } else {
                // Handle failures
                Log.w(TAG, "Unable to obtain URI from Cloud Storage");
            }
        });
    }

    protected void updateProfileDocument(String profileDocumentId, Intent intent) {
        Date date = new Date();
        if (!editName.getText().toString().matches("")) {
            profile.get().setName(editName.getText().toString());
        }
        if (!editPillar.getText().toString().matches("")) {
            profile.get().setPillar(editPillar.getText().toString());
        }
        if (!editTerm.getText().toString().matches("")) {
            profile.get().setTerm(Integer.parseInt(editTerm.getText().toString()));
        }
        if (!modules.get().isEmpty()) {
            profile.get().setModules(modules.get());
        }
        if (!editBio.getText().toString().matches("")) {
            profile.get().setBio(editBio.getText().toString());
        }
        profile.get().setProfileUpdated(date);
        updateFirestore(profileDocumentId, profile.get(), intent);
    }

    protected void updateFirestore(String profileDocumentId, ProfileModel model, Intent intent) {
        db.collection("Profiles").document(profileDocumentId).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}