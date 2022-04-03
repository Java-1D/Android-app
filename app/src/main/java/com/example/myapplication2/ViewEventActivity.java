package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class ViewEventActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "ViewEvents";
    FirebaseFirestore db;

    TextView event_name;
    TextView event_desc;
    ShapeableImageView person;
    ShapeableImageView location_pic;
    ImageView calendar_icon;
    TextView date;
    ImageView start_time_icon;
    TextView start_time;
    ImageView end_time_icon;
    TextView end_time;
    ImageView location_icon;
    TextView location;
    ImageView information_icon;
    TextView information;
    ImageView emoji;
    TextView no_of_ppl;
    ShapeableImageView person1;
    ShapeableImageView person2;
    ShapeableImageView person3;
    TextView name1;
    TextView name2;
    TextView name3;
    ImageView search1;
    ImageView search2;
    ImageView search3;
    MaterialButton join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        event_name  = findViewById(R.id.event_name); // Need to retrieve
        event_desc = findViewById(R.id.event_desc); // Need to retrieve
        person = findViewById(R.id.person); // Need to retrieve
        location_pic = findViewById(R.id.location_pic); // Need to retrieve
        calendar_icon = findViewById(R.id.calendar_icon);
        date = findViewById(R.id.date); // Need to retrieve
        start_time_icon = findViewById(R.id.start_time_icon);
        start_time = findViewById(R.id.start_time); // Need to retrieve
        end_time_icon = findViewById(R.id.end_time_icon);
        end_time = findViewById(R.id.end_time); // Need to retrieve
        location_icon = findViewById(R.id.location_icon);
        location = findViewById(R.id.location); // Need to retrieve
        information_icon = findViewById(R.id.information_icon);
        information = findViewById(R.id.information); // Need to retrieve
        emoji = findViewById((R.id.emoji));
        no_of_ppl = findViewById(R.id.no_of_ppl); // Need to retrieve
        person1 = findViewById(R.id.person1); // Need to retrieve
        person2 = findViewById(R.id.person2); // Need to retrieve
        person3 = findViewById(R.id.person3); // Need to retrieve
        name1 = findViewById(R.id.name1); // Need to retrieve
        name2 = findViewById(R.id.name2); // Need to retrieve
        name3 = findViewById(R.id.name3); // Need to retrieve
        search1 = findViewById(R.id.search1);
        search2 = findViewById(R.id.search2);
        search3 = findViewById(R.id.search3);
        join_button = findViewById(R.id.join_button);

        join_button.setOnClickListener(this);
        search1.setOnClickListener(this);
        search2.setOnClickListener(this);
        search3.setOnClickListener(this);

        DocumentReference docRef = db.collection("Events").document("Test Event");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        document.get()
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_button:
            // TODO Check if event is full, if full reject join request.
            // TODO Update database +1 member to the event

            case R.id.search1:
            // TODO Retrieve profile page of person1

            case R.id.search2:
            // TODO Retrieve profile page of person2

            case R.id.search3:
            // TODO Retrieve profile page of person3

        }
    }
}
