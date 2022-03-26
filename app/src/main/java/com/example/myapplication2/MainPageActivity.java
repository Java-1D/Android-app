package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication2.fragments.HomeFragment;
import com.example.myapplication2.fragments.SearchFragment;
import com.example.myapplication2.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainPageActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

    }
    com.example.myapplication2.SearchFragment searchFragment = new com.example.myapplication2.SearchFragment();
    com.example.myapplication2.HomeFragment homeFragment = new com.example.myapplication2.HomeFragment();
    com.example.myapplication2.SettingsFragment settingsFragment = new com.example.myapplication2.SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, searchFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, settingsFragment).commit();
                return true;

        }

        return false;
    }
}