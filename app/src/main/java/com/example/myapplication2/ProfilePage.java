package com.example.myapplication2;

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

import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.RecyclerContactAdapter;
import com.example.myapplication2.viewholder.RecyclerViewModel;

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

//    Data fields present in UI elements
    enum Data {
        PROFILE_PIC,
        NAME,
        PILLAR,
        TERM,
        MODULE,
        BIO
    }

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
    RecyclerContactAdapter adapter;
    ArrayList<RecyclerViewModel> arrModules  = new ArrayList<>();

    //Shared Preferences to store Objects as a String
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEditor;

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

        //Fetch Data from Profile Collection
        profileDocumentId = "123456";
        getProfileData(profileDocumentId);
        //FIXME find a way to extract modules from Firestore DocumentReference
        arrModules.add(new RecyclerViewModel(R.drawable.iot, "CSD", "IoT and all other stuff"));
        arrModules.add(new RecyclerViewModel(R.drawable.data_analytics, "CSD", "Data and all dat shit"));
        arrModules.add(new RecyclerViewModel(R.drawable.fin, "CSD", "Financial tech and all shit"));

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
        adapter = new RecyclerContactAdapter(this, arrModules);
        recyclerView.setAdapter(adapter);

        //initialise buttons
        backArrow.setOnClickListener(new ClickListener());
        logOutButton.setOnClickListener(new ClickListener());
        editButton.setOnClickListener(new ClickListener());
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

        //TODO Store ProfileDocumentId and carry across activities
        getProfileData(profileDocumentId);
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
        prefsEditor.putString("PROFILE_ID", profileDocumentId);
        prefsEditor.apply();
        Log.i(TAG, "PROFILE_ID has been added to prefsEditor");

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

    ////To store contents in Bundle when user leaves Activity (occur before onStop)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        Save the user's current workout state
//        savedInstanceState.putInt(WORKOUT_STATE, currentState);

        super.onSaveInstanceState(savedInstanceState);
    }

    //To retrieve contents from Bundle when user returns to Activity (occur after onStart)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        Restore state
//        currentScore = savedInstanceState.getInt(WORKOUT_STATE);
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
    public void getProfileData(String profileDocumentId) {
        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Log.i(TAG, "File Path in Firebase: " + profileRef.getPath());
                    ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                    Log.i(ProfileModel.TAG, "Contents of Firestore Document: "+ Objects.requireNonNull(document.toObject(ProfileModel.class)).toString());
                    ProfilePage.this.setUIElements(ProfilePage.this.profile.get());

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
}