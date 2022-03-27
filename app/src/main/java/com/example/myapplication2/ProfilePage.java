package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {
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

    ImageView backButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        db = FirebaseFirestore.getInstance();

        backButton = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        pillarValue = findViewById(R.id.pillarValue);
        termValue = findViewById(R.id.termValue);
        module1 = findViewById(R.id.Module1);
        module2 = findViewById(R.id.Module2);
        module3 = findViewById(R.id.Module3);
        module4 = findViewById(R.id.Module4);
        module5 = findViewById(R.id.Module5);
        bioText = findViewById(R.id.bioText);
        editButton = findViewById(R.id.editButton);

        editButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getProfileData("Test", profileName, Data.NAME);
        getUserData("Test", profileEmail, Data.EMAIL);
        getProfileData("Test", pillarValue, Data.PILLAR);
        getProfileData("Test", termValue, Data.TERM);
        //FIXME find a way to extract modules from Firestore DocumentReference
        getProfileData("Test", bioText, Data.BIO);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editButton:
                startActivity(new Intent(ProfilePage.this, EditProfilePage.class));
                break;
            case R.id.backArrow:
                startActivity((new Intent(ProfilePage.this, MainPageActivity.class)));
                break;
            case R.id.logOutButton:
                startActivity((new Intent(ProfilePage.this, LoginActivity.class)));
        }
    }

    public void getProfileData(String profileId, TextView view, Data data) {
        CollectionReference profiles = db.collection("Profiles");
        DocumentReference profileRef = profiles.document(profileId);
        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: "+ document.getData());
                        switch (data) {
                            case NAME:
                                view.setText(document.getString("name"));
                                break;
                            case TERM:
                                view.setText(String.valueOf(document.getLong("term")));
                                break;
                            case PILLAR:
                                view.setText(document.getString("pillar"));
                                break;
                            case BIO:
                                view.setText(document.getString("bio"));
                                break;
                            default:
                                Toast.makeText(ProfilePage.this, "Item Does Not Exist in Document", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error in Enum detected" + data);
                                break;
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
           }
        });
    }

    public void getUserData(String userId, TextView view, Data data) {
        CollectionReference users = db.collection("Users");
        DocumentReference userRef = users.document(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: "+ document.getData());
                        switch (data) {
                            case EMAIL:
                                view.setText(document.getString("email"));
                                break;
                            default:
                                Toast.makeText(ProfilePage.this, "Item Does Not Exist in Document", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error in Enum detected"+ data);
                                break;
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}