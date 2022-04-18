package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.objectmodel.UserModel;
import com.example.myapplication2.utils.FirebaseQuery;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Toast.makeText(getApplicationContext(), "Network not Available", Toast.LENGTH_LONG).show();
        }

        switch (view.getId()) {
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.loginbtn:
                TextView email = (TextView) findViewById(R.id.username);
                String emailString = email.getText().toString();
                TextView password = (TextView) findViewById(R.id.password);
                String passwordString = password.getText().toString();

                validAccount(emailString, passwordString);
        }
    }


    public void validAccount(String email, String password) {
        FirebaseQuery firebaseQuery = new FirebaseQuery() {
            @Override
            public void callbackOnSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.getData().get("password").toString().equals(password)) {
                            String username = document.getData().get("username").toString();
                            DocumentReference profileRef = getDb().collection(UserModel.getCollectionId()).document(document.getData().get("username").toString());
                            LoggedInUser currentUser = LoggedInUser.getInstance();
                            currentUser.setUser(profileRef, username);
                            startActivity(new Intent(LoginActivity.this, MainPageActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public Query getQuery(String collectionId) {
                return getDb().collection(collectionId).whereEqualTo("email", email);
            }
        };
        firebaseQuery.run(UserModel.getCollectionId());
    }
}