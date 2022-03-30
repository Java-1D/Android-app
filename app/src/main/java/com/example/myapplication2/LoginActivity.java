package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        TextView mainPage = (TextView) findViewById(R.id.textView);
        mainPage.setOnClickListener(this);

        Button login = (Button) findViewById(R.id.loginbtn);
        login.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (!Utils.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Network not Avaliable", Toast.LENGTH_LONG).show()
        }
        switch (view.getId()) {
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.textView:
                startActivity(new Intent(LoginActivity.this, ProfilePage.class));
                break;

            //Commented out filterpage to test mainpage
            /*
            case R.id.loginbtn:
                startActivity(new Intent(LoginActivity.this, FilterPage.class));

             */

            case R.id.loginbtn:
                startActivity(new Intent(LoginActivity.this, MainPageActivity.class));

        }
    }
}