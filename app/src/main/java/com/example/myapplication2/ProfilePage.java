package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.RecyclerContactAdapter;
import com.example.myapplication2.viewholder.RecyclerViewModel;

import com.example.myapplication2.utils.Container;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.objectmodel.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    String userDocumentId;
    String profileDocumentId;
    final Container<UserModel> user = new Container<>(new UserModel());
    final Container<ProfileModel> profile = new Container<>(new ProfileModel());

//    //Data fields present in UI elements
//    enum Data {
//        NAME,
//        EMAIL,
//        PILLAR,
//        TERM,
//        MODULE,
//        BIO
//    }

    //View UI elements
    ImageView profilePicture;
    TextView profileName;
    TextView profileEmail;
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
                    startActivity(new Intent(ProfilePage.this, EditProfilePage.class));
                    Log.i(TAG, "UserModel Container: " + user.get().toString());
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

        //initialise UI elements
        backArrow = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
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

        userDocumentId = "Test";
        profileDocumentId = "Test";

        setProfileDataOnUI(profileDocumentId);
        setUserDataOnUI(userDocumentId);
        //FIXME find a way to extract modules from Firestore DocumentReference
        arrModules.add(new RecyclerViewModel(R.drawable.iot, "CSD", "IoT and all other stuff"));
        arrModules.add(new RecyclerViewModel(R.drawable.data_analytics, "CSD", "Data and all dat shit"));
        arrModules.add(new RecyclerViewModel(R.drawable.fin, "CSD", "Financial tech and all shit"));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");

        //TODO Store UserDocumentId and ProfileDocumentId and carry across activities
        userDocumentId = "Test";
        profileDocumentId = "Test";

        setProfileDataOnUI(profileDocumentId);
        setUserDataOnUI(userDocumentId);
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

    //Set Profile Picture
    public void setProfilePicture(String imageURL) {
        int resolution = 4;
        Picasso.get().load(imageURL).resize(120*resolution, 120*resolution)
                .transform(new Utils.CircleTransform()).into(profilePicture);
    }

    //Set UI Elements using data from Firebase
    public void setUIElements(ProfileModel profile) {
        //Set Text
        profileName.setText(profile.getName());
        pillarValue.setText(profile.getPillar());
        termValue.setText(String.valueOf(profile.getTerm()));
        bioText.setText(profile.getBio());

        //Set Image
        setProfilePicture(profile.getImagePath());
    }

    public void setUIElements(UserModel user) {
        profileEmail.setText(user.getEmail());
    }

    //Firebase-Specific Methods
    public DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }

    public void setProfileDataOnUI(String profileDocumentId) {
        DocumentReference profileRef = getDocumentReference(ProfileModel.collectionId, profileDocumentId);
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                    Log.i(ProfileModel.TAG, document.toObject(ProfileModel.class).toString());
                    ProfilePage.this.setUIElements(ProfilePage.this.profile.get());
                    Log.i(TAG, profile.toString());
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error retrieving document", e);
            }
        });
    }

    public void setUserDataOnUI(String userDocumentId) {
        DocumentReference userRef = getDocumentReference(UserModel.collectionId, userDocumentId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    ProfilePage.this.user.set(document.toObject(UserModel.class));
                    Log.i(UserModel.TAG, document.toObject(UserModel.class).toString());
                    ProfilePage.this.setUIElements(ProfilePage.this.user.get());
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error retrieving document", e);
            }
        });
    }
}