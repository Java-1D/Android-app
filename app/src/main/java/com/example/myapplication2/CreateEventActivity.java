package com.example.myapplication2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.fragments.CropDialogFragment;
import com.example.myapplication2.fragments.ModuleDialogFragment;
import com.example.myapplication2.interfaces.CustomDialogInterface;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseDocument;
import com.example.myapplication2.utils.FirebaseQuery;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

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
                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(CreateEventActivity.this, R.string.internet_required, Toast.LENGTH_SHORT).show();
                    return;
                }

                createButton.setEnabled(false);
                createButton.setText("Creating event...");

                ArrayList<EditText> editTextArrayList = new ArrayList<>(
                        Arrays.asList(createName,
                                createDescription,
                                createVenue,
                                createModule,
                                createCapacity,
                                createStart,
                                createEnd));

                if (Utils.invalidData(editTextArrayList)) {
                    createButton.setEnabled(true);
                    createButton.setText(R.string.create_event);
                    return;
                }

                String eventName = createName.getText().toString();
                String eventDescription = createDescription.getText().toString();
                String eventVenue = createVenue.getText().toString();
                DocumentReference userCreated = LoggedInUser.getInstance().getUserDocRef();
                Integer eventCapacity = Integer.parseInt(createCapacity.getText().toString());

                // Checking that the data does not exist in Firebase
                FirebaseDocument firebaseDocument = new FirebaseDocument() {
                    @Override
                    public void callbackOnSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            createName.requestFocus();
                            createName.setError("Please use a different event name.");
                            createButton.setEnabled(true);
                            createButton.setText(R.string.create_event);
                        } else {
                            // Happens when eventName is not taken
                            // https://firebase.google.com/docs/storage/android/upload-files
                            // Uploading image into Firebase Storage
                            // Randomizing id for file name
                            StorageReference eventImageRef = firebaseStorage.getReference().child(EventModel.COLLECTION_ID + "/" + UUID.randomUUID().toString());

                            // Get the data from an ImageView as bytes
                            createImage.setDrawingCacheEnabled(true);
                            createImage.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) createImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = eventImageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.i(TAG, "onFailure: Storage upload unsuccessful");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.i(TAG, "uploadTask: Image successfully uploaded");
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String eventImage = task.getResult().toString();

                                            EventModel eventModel = new EventModel(
                                                    eventName,
                                                    eventDescription,
                                                    eventVenue,
                                                    selectedModuleReference,
                                                    eventCapacity,
                                                    startDateTime.getTime(),
                                                    endDateTime.getTime(),
                                                    eventImage,
                                                    userCreated
                                            );

                                            db.collection(EventModel.COLLECTION_ID).document(eventName).set(eventModel);
                                            Log.i(TAG, "createEvent: Successful. Event added to Firebase");

                                            // Create explicit intent to go into MainPage
                                            Intent intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void callbackOnFailure(Exception e) {
                        Log.d(TAG, "get failed with ", e);
                        createButton.setEnabled(true);
                        createButton.setText(R.string.create_event);
                    }
                };
                firebaseDocument.run(EventModel.getCollectionId(), eventName);
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
                CropDialogFragment cropDialogFragment = new CropDialogFragment(new CropDialogFragment.OnCropListener() {
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
                Utils.dateTimePicker(getSupportFragmentManager(), startDateTime, null,
                        new CustomDialogInterface() {
                            @Override
                            public void onResult(Object o) {
                                endDateTime = (Calendar) o;
                                String stringDateTime = Utils.dateFormat.format(startDateTime.getTime());
                                createEnd.setText(stringDateTime);
                            }
                        }
                );
        }
    }
}
