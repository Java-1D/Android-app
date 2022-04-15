package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.disableButton;

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

import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.FirebaseDocument;
import com.example.myapplication2.utils.LoggedInUser;
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
    private static final String PROFILE_ID = "PROFILE_ID";

    //Objects to handle data from Firebase
    String profileDocumentId;
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());
    final FirebaseContainer<ModuleModel> module = new FirebaseContainer<>(new ModuleModel());

    //User Singleton
    LoggedInUser user;

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

    //Button interactions in Profile Page Activity
    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.backArrow) {
                startActivity((new Intent(ProfilePage.this, MainPageActivity.class)));
            } else if (id == R.id.logOutButton) {
                Intent logOutIntent = new Intent(ProfilePage.this, LoginActivity.class);
                logOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logOutIntent);
                finish();
            } else if (id == R.id.editButton) {
                Intent editIntent = new Intent(ProfilePage.this, EditProfilePage.class);
                editIntent.putExtra(PROFILE_ID, profileDocumentId);
                Log.i(TAG, "PROFILE_ID has been added to Intent");
                startActivity(editIntent);
            } else {
                Log.w(TAG, "Button not Found");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Log.i(TAG, "onCreate is called");

        //Get value from Singleton
        user = LoggedInUser.getInstance();

        //initialise UI elements
        backArrow = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        pillarValue = findViewById(R.id.pillarValue);
        termValue = findViewById(R.id.termValue);
        bioText = findViewById(R.id.bioText);
        editButton = findViewById(R.id.editButton);

        Intent intent = getIntent();
        profileDocumentId = intent.getStringExtra(PROFILE_ID);
        if (profileDocumentId == null) {
            Log.w(TAG, "Profile Document ID is null. Using value from User Singleton");
            profileDocumentId = user.getUserString();
        } else {
            Log.i(TAG, "Profile Document ID Retrieved: " + profileDocumentId);
        }

        //Check whether user is not checking his own profile
        if (!profileDocumentId.equals(user.getUserString())) {
            disableButton(editButton);
            disableButton(logOutButton);
        }

        //Get Profile Data from Firestore
        getProfileData(profileDocumentId);

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


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");

        arrModules.clear();
        getProfileData(profileDocumentId);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");

        startActivity((new Intent(ProfilePage.this, MainPageActivity.class)));
    }

    //Get Data from Profiles Collection
    private void getProfileData(String profileDocumentId) {
        FirebaseDocument firebaseDocument = new FirebaseDocument() {
            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                Log.i(ProfileModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ProfileModel.class)));
                ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                ProfilePage.this.setUIElements(ProfilePage.this.profile.get());
                if (ProfilePage.this.profile.get().getModules() != null) {
                    ProfilePage.this.addModuleToRecyclerView();
                }
            }
        };

        firebaseDocument.run(ProfileModel.getCollectionId(), profileDocumentId);
    }

    //Add Module Data Retrieved
    private void addModuleToRecyclerView() {
        for (DocumentReference moduleRef : profile.get().getModules()) {
            Log.i(TAG, "File Path in Firebase: " + moduleRef.getPath());
            FirebaseDocument firebaseDocument = new FirebaseDocument() {
                @Override
                public void callbackOnSuccess(DocumentSnapshot document) {
                    Log.i(ModuleModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ModuleModel.class)));
                    ProfilePage.this.module.set(document.toObject(ModuleModel.class));

                    //Add modules from Firestore DocumentReference to Recycler View
                    arrModules.add(new ProfileViewModel(ProfilePage.this.module.get()));
                    ProfilePage.this.adapter.notifyItemInserted(arrModules.size());
                }
            };

            firebaseDocument.run(moduleRef);
        }
    }

    //Set UI Elements for ProfilePage Activity
    private void setUIElements(ProfileModel profile) {
        //Set Text
        profileName.setText(profile.getName());
        pillarValue.setText(profile.getPillar());
        termValue.setText(String.valueOf(profile.getTerm()));
        bioText.setText(profile.getBio());

        //Set Image
        setImage(profile.getImagePath());
    }

    //Set Image for ImageView
    private void setImage(String imageURL) {
        Picasso.get().load(imageURL).resize(120, 120).centerCrop().transform(new Utils.CircleTransform()).into(profilePicture);
        Log.i(TAG, "Profile Picture set");
    }
}
