package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getDocumentFromPath;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.db.EventsDb;
import com.example.myapplication2.fragments.CropDialogFragment;
import com.example.myapplication2.fragments.ModuleDialogFragment;
import com.example.myapplication2.fragments.YesNoDialogFragment;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView editImage;
    Button setImageButton;

    EditText editName;
    EditText editDescription;
    EditText editVenue;
    EditText editModule;
    EditText editCapacity;
    EditText editStart;
    EditText editEnd;

    Button deleteButton;
    Button editButton;

    ImageView backButton;

    // Global variable to take note of Calendar object for editDate
    // Used because it cannot be stored in EditText or any other type of texts
    Calendar startDateTime;
    Calendar endDateTime;

    FirebaseFirestore db;
    FirebaseStorage firebaseStorage;

    ArrayList<DocumentReference> moduleReferences;
    ArrayList<String> moduleStringList;
    DocumentReference selectedModuleReference;
    String documentName;

    static final String TAG = "EditEvents";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        // Casting to ensure that the types are correct
        editImage = findViewById(R.id.editEventImage);
        setImageButton = findViewById(R.id.setImageButton);

        editName = (EditText) findViewById(R.id.editEventName);
        editDescription = (EditText) findViewById(R.id.editEventDescription);
        editVenue = (EditText) findViewById(R.id.editEventVenue);
        editModule = (EditText) findViewById(R.id.editEventModule);
        editCapacity = (EditText) findViewById(R.id.editEventCapacity);
        editStart = (EditText) findViewById(R.id.editEventStartDateTime);
        editEnd = (EditText) findViewById(R.id.editEventEndDateTime);

        editButton = (Button) findViewById(R.id.editEventButton);
        deleteButton = (Button) findViewById(R.id.deleteEventButton);

        backButton = (ImageView) findViewById(R.id.backButton);

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

        // Getting ID from intent
        String documentId = getIntent().getStringExtra("DOCUMENT_ID");
        documentName = getDocumentFromPath(documentId);
        Log.i(TAG, "Document Name" + documentName);

        FirebaseDocument firebaseDocument = new FirebaseDocument() {
            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    EventModel eventModel = document.toObject(EventModel.class);
                    editName.setText(eventModel.getTitle());

                    Utils.loadImage(eventModel.getImagePath(), editImage);

                    editDescription.setText(eventModel.getDescription());
                    editVenue.setText(eventModel.getVenue());

                    DocumentReference moduleReference = eventModel.getModule();
                    if (moduleReference != null) {
                        FirebaseDocument moduleDocument = new FirebaseDocument() {
                            @Override
                            public void callbackOnSuccess(DocumentSnapshot document) {
                                if (document.exists()) {
                                    ModuleModel moduleModel = document.toObject(ModuleModel.class);
                                    editModule.setText(moduleModel.getName());
                                    selectedModuleReference = eventModel.getModule();
                                }
                                else {
                                    Log.w(TAG, "Document does not exist");
                                }
                            }
                        };
                        moduleDocument.run(moduleReference);
                    }

                    editCapacity.setText(String.valueOf(eventModel.getCapacity()));

                    // Setting date and syncing global calendar variable
                    startDateTime = Calendar.getInstance();
                    startDateTime.setTime(eventModel.getEventStart());

                    endDateTime = Calendar.getInstance();
                    endDateTime.setTime(eventModel.getEventEnd());

                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM @ hh:mm aa");
                    editStart.setText(dateFormat.format(startDateTime.getTime()));
                    editEnd.setText(dateFormat.format(endDateTime.getTime()));
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        };
        firebaseDocument.run(EventModel.getCollectionId(), documentId);

        editModule.setOnClickListener(this);
        editButton.setOnClickListener(this);
        setImageButton.setOnClickListener(this);
        editStart.setOnClickListener(this);
        editEnd.setOnClickListener(this);
        editName.setOnClickListener(this);
        backButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editEventButton:
                editButton.setEnabled(false);
                editButton.setText("Editing event...");

                new EventsDb(new EventsDb.OnEventModelSuccess() {
                    @Override
                    public void onResult(EventModel eventModel) {
                        if (eventModel == null) {
                            editButton.setEnabled(true);
                            editButton.setText(R.string.edit_event);
                        } else {
                            new EventsDb().pushEvent(EditEventActivity.this, eventModel);
                            Log.i(TAG, "createEvent: Successful. Event added to Firebase");

                            // Create explicit intent to go into MainPage
                            Intent intent = new Intent(EditEventActivity.this, MainPageActivity.class);
                            startActivity(intent);
                        }
                    }
                }).convertToEventModel(this, editImage, editName,
                        editDescription, editVenue, editModule,
                        editCapacity, editStart, editEnd,
                        selectedModuleReference, startDateTime, endDateTime);
                break;

            case R.id.backButton:
                // Create explicit intent to go into MainPage
                Intent intent = new Intent(EditEventActivity.this, MainPageActivity.class);
                startActivity(intent);

            case R.id.editEventName:
                editName.setError("Event name cannot be edited.");
                Toast.makeText(EditEventActivity.this, "Event name cannot be edited.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.editEventModule:
                ModuleDialogFragment moduleDialogFragment = new ModuleDialogFragment(moduleStringList,
                        new ModuleDialogFragment.OnSingleSelectListener() {
                            @Override
                            public void onResult(Integer i) {
                                selectedModuleReference = moduleReferences.get(i);
                                editModule.setText(moduleStringList.get(i));
                            }
                        });
                moduleDialogFragment.show(getSupportFragmentManager(), ModuleDialogFragment.TAG);
                break;

            case R.id.setImageButton:
                CropDialogFragment cropDialogFragment = new CropDialogFragment(new CropDialogFragment.OnCropListener() {
                    @Override
                    public void onResult(Uri uri) {
                        editImage.setImageURI(uri);
                    }
                });
                cropDialogFragment.show(getSupportFragmentManager(), CropDialogFragment.TAG);
                break;

            case R.id.editEventStartDateTime:
                Utils.dateTimePicker(getSupportFragmentManager(), Calendar.getInstance(), endDateTime,
                        new CustomDialogInterface() {
                            @Override
                            public void onResult(Object o) {
                                startDateTime = (Calendar) o;
                                String stringDateTime = Utils.dateFormat.format(startDateTime.getTime());
                                editStart.setText(stringDateTime);
                            }
                        }
                );
                break;

            case R.id.editEventEndDateTime:
                if (startDateTime == null) {
                    startDateTime = Calendar.getInstance();
                }

                Utils.dateTimePicker(getSupportFragmentManager(), startDateTime, null,
                        new CustomDialogInterface() {
                            @Override
                            public void onResult(Object o) {
                                endDateTime = (Calendar) o;
                                String stringDateTime = Utils.dateFormat.format(startDateTime.getTime());
                                editEnd.setText(stringDateTime);
                            }
                        }
                );
                break;
                
            case R.id.deleteEventButton:
                String message = "Are you sure you want to delete this event? \n" +
                        "THIS IS NON-REVERSIBLE.";
                YesNoDialogFragment yesNoDialogFragment = new YesNoDialogFragment(message,
                        new YesNoDialogFragment.OnClickListener() {
                            @Override
                            public void onResult(boolean bool) {
                                if (bool == true) {
                                    String documentId = getIntent().getStringExtra("DOCUMENT_ID");
                                    db.collection(EventModel.COLLECTION_ID).document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Event successfully deleted!");
                                                    // Create explicit intent to go into MainPage
                                                    Intent intent = new Intent(EditEventActivity.this, MainPageActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting event", e);
                                                }
                                            });
                                }
                            }
                        });
                yesNoDialogFragment.show(getSupportFragmentManager(), YesNoDialogFragment.TAG);
                break;
        }
    }
}
