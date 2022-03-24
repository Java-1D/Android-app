package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button edit = (Button) findViewById(R.id.editButton);
        edit.setOnClickListener(this);
        ImageView back = (ImageView) findViewById(R.id.backArrow);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editButton:
                startActivity(new Intent(ProfilePage.this, EditProfilePage.class));
                break;
            case R.id.backArrow:
                startActivity((new Intent(ProfilePage.this, LoginActivity.class)));
                break;

        }
    }
}