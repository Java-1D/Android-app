package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication2.objectmodel.RecyclerContactAdapter;
import com.example.myapplication2.objectmodel.RecyclerViewModel;


import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener {
ArrayList<RecyclerViewModel> arrModules  = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button edit = (Button) findViewById(R.id.editButton);
        edit.setOnClickListener(this);
        ImageView back = (ImageView) findViewById(R.id.backArrow);
        back.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrModules.add(new RecyclerViewModel(R.drawable.iot, "CSD", "IoT and all other stuff"));
        arrModules.add(new RecyclerViewModel(R.drawable.data_analytics, "CSD", "Data and all dat shit"));
        arrModules.add(new RecyclerViewModel(R.drawable.fin, "CSD", "Financial tech and all shit"));

        RecyclerContactAdapter adapter = new RecyclerContactAdapter(this, arrModules);
        recyclerView.setAdapter(adapter);

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