package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {

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
}