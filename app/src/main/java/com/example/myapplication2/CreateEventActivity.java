package com.example.myapplication2;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.db.EventsDb;
import com.example.myapplication2.fragments.CropDialogFragment;
import com.example.myapplication2.fragments.ModuleDialogFragment;
import com.example.myapplication2.interfaces.CustomDialogInterface;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseQuery;
import com.example.myapplication2.utils.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView createImage;
    Button setImageButton;

    EditText createName;
    EditText createDescription;
    EditText createVenue;
    EditText createModule;
    EditText createCapacity;
    EditText createStart;
    EditText createEnd;

    Button createButton;

    ImageView backButton;

    FirebaseFirestore db;
    FirebaseStorage firebaseStorage;

    // Global variable to take note of Calendar object for createDate
    // Used because it cannot be stored in EditText or any other type of texts
    Calendar startDateTime;
    Calendar endDateTime;

    ArrayList<DocumentReference> moduleReferences;
    ArrayList<String> moduleStringList;
    DocumentReference selectedModuleReference;

    static final String TAG = "CreateEvents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_events);

        // Casting to ensure that the types are correct
        createImage = findViewById(R.id.createEventImage);
        setImageButton = findViewById(R.id.setImageButton);

        createName = (EditText) findViewById(R.id.createEventName);
        createDescription = (EditText) findViewById(R.id.createEventDescription);
        createVenue = (EditText) findViewById(R.id.createEventVenue);
        createModule = (EditText) findViewById(R.id.createEventModule);
        createCapacity = (EditText) findViewById(R.id.createEventCapacity);
        createStart = (EditText) findViewById(R.id.createEventStartDateTime);
        createEnd = (EditText) findViewById(R.id.createEventEndDateTime);

        createButton = (Button) findViewById(R.id.createEventButton);

        backButton = (ImageView) findViewById(R.id.backButton);

        // https://firebase.google.com/docs/firestore/quickstart#java
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // For module dropdown initialization purposes
        moduleReferences = new ArrayList<>();
        moduleStringList = new ArrayList<>();

        // https://stackoverflow.com/questions/50035752/how-to-get-list-of-documents-from-a-collection-in-firestore-android
        FirebaseQuery firebaseQuery = new FirebaseQuery() {
            @Override
            public void callbackOnSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    moduleReferences.add(documentSnapshot.getReference());
                    moduleStringList.add(documentSnapshot.getString("name"));
                    Log.i(TAG, "Module documentReferences loaded.");
                }
            }
        };
        firebaseQuery.run(ModuleModel.getCollectionId());

        createModule.setOnClickListener(this);
        createButton.setOnClickListener(this);
        setImageButton.setOnClickListener(this);
        createStart.setOnClickListener(this);
        createEnd.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createEventButton:
                createButton.setEnabled(false);
                createButton.setText("Creating event...");

                new EventsDb(new EventsDb.OnEventModelSuccess() {
                    @Override
                    public void onResult(EventModel eventModel) {
                        if (eventModel == null) {
                            createButton.setEnabled(true);
                            createButton.setText(R.string.create_event);
                        } else {
                            new EventsDb().pushEvent(CreateEventActivity.this, eventModel);
                            Log.i(TAG, "createEvent: Successful. Event added to Firebase");

                            // Create explicit intent to go into MainPage
                            Intent intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
                            startActivity(intent);
                        }
                    }
                }).convertToEventModel(this, createImage, createName,
                        createDescription, createVenue, createModule,
                        createCapacity, createStart, createEnd,
                        selectedModuleReference, startDateTime, endDateTime);
                break;

            case R.id.backButton:
                // Create explicit intent to go into MainPage
                Intent intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
                startActivity(intent);

            case R.id.createEventModule:
                ModuleDialogFragment moduleDialogFragment = new ModuleDialogFragment(moduleStringList,
                        new ModuleDialogFragment.OnSingleSelectListener() {
                            @Override
                            public void onResult(Integer i) {
                                selectedModuleReference = moduleReferences.get(i);
                                createModule.setText(moduleStringList.get(i));
                            }
                        });
                moduleDialogFragment.show(getSupportFragmentManager(), ModuleDialogFragment.TAG);
                break;

            case R.id.setImageButton:
                CropDialogFragment cropDialogFragment = new CropDialogFragment(
                        new CropDialogFragment.OnCropListener() {
                            @Override
                            public void onResult(Uri uri) {
                                createImage.setImageURI(uri);
                            }
                        });
                cropDialogFragment.show(getSupportFragmentManager(), CropDialogFragment.TAG);
                break;

            case R.id.createEventStartDateTime:
                Utils.dateTimePicker(getSupportFragmentManager(), Calendar.getInstance(), endDateTime,
                        new CustomDialogInterface() {
                            @Override
                            public void onResult(Object o) {
                                startDateTime = (Calendar) o;
                                String stringDateTime = Utils.dateFormat.format(startDateTime.getTime());
                                createStart.setText(stringDateTime);
                            }
                        }
                );
                break;

            case R.id.createEventEndDateTime:
                if (startDateTime == null) {
                    startDateTime = Calendar.getInstance();
                }

                Utils.dateTimePicker(getSupportFragmentManager(), startDateTime, null,
                        new CustomDialogInterface() {
                            @Override
                            public void onResult(Object o) {
                                endDateTime = (Calendar) o;
                                String stringDateTime = Utils.dateFormat.format(endDateTime.getTime());
                                createEnd.setText(stringDateTime);
                            }
                        }
                );
        }
    }
}
