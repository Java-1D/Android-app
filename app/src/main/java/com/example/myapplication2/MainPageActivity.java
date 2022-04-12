package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication2.fragments.HomeFragment;
import com.example.myapplication2.fragments.ExploreFragment;
import com.example.myapplication2.fragments.ProfileFragment;
import com.example.myapplication2.utils.LoggedInUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ArrayList<Integer> imageArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        // SharedPreferences sharedPrefs = getSharedPreferences("PROFILE_PAGE",
        // MODE_PRIVATE);
        // SharedPreferences.Editor prefsEditor = sharedPrefs.edit();

    }

    ExploreFragment exploreFragment = new ExploreFragment();
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.explore:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, exploreFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;

            case R.id.profile:
                Intent profileIntent = new Intent(this, ProfilePage.class);
                profileIntent.putExtra("PROFILE_ID", LoggedInUser.getInstance().getUserId()); //TODO : bugs here
                startActivity(profileIntent);
                return true;
        }

        return false;
    }

}