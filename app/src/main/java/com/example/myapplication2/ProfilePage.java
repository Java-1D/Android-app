package com.example.myapplication2;

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

import com.example.myapplication2.viewholder.RecyclerContactAdapter;
import com.example.myapplication2.viewholder.RecyclerViewModel;

import com.example.myapplication2.utils.Container;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.objectmodel.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


//TODO Need Logic to hide button when visiting other profile pages (Check DocumentReference and/or ID)
public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
  
    FirebaseFirestore db;

    enum Data {
        NAME,
        EMAIL,
        PILLAR,
        TERM,
        MODULE,
        BIO
    }

    ImageView backArrow;
    Button logOutButton;
    ImageView profilePicture;
    TextView profileName;
    TextView profileEmail;
    TextView pillarValue;
    TextView termValue;
    TextView module1;
    TextView module2;
    TextView module3;
    TextView module4;
    TextView module5;
    TextView bioText;
    Button editButton;

    //Test Variables for Firestore
    String value;
    final Container<UserModel> user = new Container(new UserModel());
    final Container<ProfileModel> profile = new Container(new ProfileModel());
    ArrayList<RecyclerViewModel> arrModules  = new ArrayList<>();

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
      
        db = FirebaseFirestore.getInstance();

        backArrow = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        pillarValue = findViewById(R.id.pillarValue);
//        termValue = findViewById(R.id.termValue);
//        module1 = findViewById(R.id.Module1);
//        module2 = findViewById(R.id.Module2);
//        module3 = findViewById(R.id.Module3);
//        module4 = findViewById(R.id.Module4);
//        module5 = findViewById(R.id.Module5);
        bioText = findViewById(R.id.bioText);
        editButton = findViewById(R.id.editButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrModules.add(new RecyclerViewModel(R.drawable.iot, "CSD", "IoT and all other stuff"));
        arrModules.add(new RecyclerViewModel(R.drawable.data_analytics, "CSD", "Data and all dat shit"));
        arrModules.add(new RecyclerViewModel(R.drawable.fin, "CSD", "Financial tech and all shit"));

        RecyclerContactAdapter adapter = new RecyclerContactAdapter(this, arrModules);
        recyclerView.setAdapter(adapter);

        backArrow.setOnClickListener(new ClickListener());
        logOutButton.setOnClickListener(new ClickListener());
        editButton.setOnClickListener(new ClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart is called");
        DocumentReference userId = getDocumentReference("Users", "Test");
        DocumentReference profileId = getDocumentReference("Profiles", "Test");

//        //FIXME refactor code and use Executor and Handler classes to update changes
//        getProfileData(profileId, profileName, Data.NAME);
//        getUserData(userId, profileEmail, Data.EMAIL);
//        //FIXME find a way to store data from Firestore in an Object for referencing
//        getProfileData(profileId, pillarValue, Data.PILLAR);
//        getProfileData(profileId, termValue, Data.TERM);
//        //FIXME find a way to extract modules from Firestore DocumentReference
//        getProfileData(profileId, bioText, Data.BIO);
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


    public DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }

    public void getProfileData(DocumentReference profileRef) {
        //FIXME Using onSuccessListener
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                Log.i(TAG, "ProfileModel Class: " + document.toObject(ProfileModel.class).toString());
            }
        });
    }

    public void getUserData(DocumentReference userRef) {
        //FIXME Using onSuccessListener
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                ProfilePage.this.user.set(document.toObject(UserModel.class));
                Log.i(TAG, "UserModel Class: " + document.toObject(UserModel.class).toString());
            }
        });
    }
}