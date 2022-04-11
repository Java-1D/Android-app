package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getDocumentFromPath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.ProfileRecyclerAdapter;
import com.example.myapplication2.viewholder.ProfileViewModel;

import com.example.myapplication2.objectmodel.ProfileModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    String profileDocumentId;
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());
    final FirebaseContainer<ModuleModel> module = new FirebaseContainer<>(new ModuleModel());

    //View UI elements
    ImageView profilePicture;
    TextView profileName;
    TextView pillarValue;
    TextView termValue;
    TextView bioText;

    //Interactive UI elements
    ImageView backArrow;
    Button logOutButton;
    Button editButton;
    RecyclerView recyclerView;

    //RecyclerView components
    ProfileRecyclerAdapter adapter;
    ArrayList<ProfileViewModel> arrModules = new ArrayList<>();

    //Shared Preferences to store Objects as a String
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEditor;
    DocumentReference profileRef;


    //Button interactions in Profile Page Activity
    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backArrow:
                    startActivity((new Intent(ProfilePage.this, MainPageActivity.class)));
                    break;
                case R.id.logOutButton:
                    startActivity((new Intent(ProfilePage.this, LoginActivity.class)));
                    break;
                case R.id.editButton:
                    Intent intent = new Intent(ProfilePage.this, EditProfilePage.class);
                    startActivity(intent);
                    break;
                default:
                    Log.w(TAG, "Button not Found");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Log.i(TAG, "onCreate is called");

        //initialise Firestore db
        db = FirebaseFirestore.getInstance();

        sharedPrefs = getSharedPreferences("PROFILE_PAGE", MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();

        // getting Profile Id from viewEvents TODO : let issac know about this
        profileRef = getProfileRef();


//        //Fetch Data from Profile Collection
//        //TODO Wire up Profile Document ID from preceding activity
//        profileDocumentId = "Test";
//        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
//        getProfileData(profileRef);

        //initialise UI elements
        backArrow = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        pillarValue = findViewById(R.id.pillarValue);
        termValue = findViewById(R.id.termValue);
        bioText = findViewById(R.id.bioText);
        editButton = findViewById(R.id.editButton);

        //initialise RecyclerView elements for Modules Section
        recyclerView = findViewById(R.id.recyclerProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProfileRecyclerAdapter(this, arrModules);
        recyclerView.setAdapter(adapter);

        //initialise buttons
        backArrow.setOnClickListener(new ClickListener());
        logOutButton.setOnClickListener(new ClickListener());
        editButton.setOnClickListener(new ClickListener());
    }

    private DocumentReference getProfileRef() {
        String documentId = getIntent().getStringExtra("profileId");
        if (documentId != null) {
            profileRef = getDocumentReference(ProfileModel.getCollectionId(), getDocumentFromPath(documentId));
        } else {
            profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        }
        Log.i(TAG, "Document Name" + profileRef);
        return profileRef;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart is called");
        prefsEditor.putString("PROFILE_ID", profileDocumentId);
        prefsEditor.apply();
        Log.i(TAG, "PROFILE_ID has been added to prefsEditor");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");

        arrModules.clear();

        //TODO Store ProfileDocumentId and carry across activities
        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        getProfileData(profileRef);
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
//        prefsEditor.putString("PROFILE_ID", profileDocumentId);
//        prefsEditor.apply();
//        Log.i(TAG, "PROFILE_ID has been added to prefsEditor");

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

    //To store contents in Bundle when user leaves Activity (occur before onStop)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        Save the user's current workout state
//        savedInstanceState.putInt(WORKOUT_STATE, currentState);

        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "SaveInstanceState Saved");
    }

    //To retrieve contents from Bundle when user returns to Activity (occur after onStart)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        Restore state
//        currentScore = savedInstanceState.getInt(WORKOUT_STATE);
        Log.i(TAG, "SaveInstanceState Restored");
    }


    //Set UI Elements using data from Firebase
    public void setUIElements(ProfileModel profile) {
        //Set Text
        profileName.setText(profile.getName());
        pillarValue.setText(profile.getPillar());
        termValue.setText(String.valueOf(profile.getTerm()));
        bioText.setText(profile.getBio());

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
    public void getProfileData(DocumentReference profileRef) {
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Log.i(TAG, "File Path in Firebase: " + profileRef.getPath());
                    Log.i(ProfileModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ProfileModel.class)));
                    ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                    ProfilePage.this.setUIElements(ProfilePage.this.profile.get());
                    addModuleToRecyclerView();
                } else {
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

    //Add Module Data Retrieved
    public void addModuleToRecyclerView() {
        for (DocumentReference moduleRef : profile.get().getModules()) {
            moduleRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        Log.i(TAG, "File Path in Firebase: " + moduleRef.getPath());
                        Log.i(ModuleModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ModuleModel.class)));
                        ProfilePage.this.module.set(document.toObject(ModuleModel.class));

                        //Add modules from Firestore DocumentReference to Recycler View
                        arrModules.add(new ProfileViewModel(ProfilePage.this.module.get()));
                        ProfilePage.this.adapter.notifyItemInserted(arrModules.size());
                    } else {
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
    }
}