package com.example.myapplication2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.fragments.CropDialogFragment;
import com.example.myapplication2.interfaces.CropDialogInterface;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.Container;
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

        /**
         * @see #chooseModule()
         */
        // For module dropdown initalization purposes
        moduleReferences = new ArrayList<>();
        moduleStringList = new ArrayList<>();

        // https://stackoverflow.com/questions/50035752/how-to-get-list-of-documents-from-a-collection-in-firestore-android
        db.collection(ModuleModel.COLLECTION_ID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        moduleReferences.add(documentSnapshot.getReference());
                        moduleStringList.add(documentSnapshot.getString("name"));
                        Log.i(TAG, "Module documentReferences loaded.");
                    }
                } else {
                    Log.d(TAG, "Error getting module documents: ", task.getException());
                }
            }
        });

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
                DocumentReference docRef = db.collection(EventModel.COLLECTION_ID).document(eventName);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
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
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            createButton.setEnabled(true);
                            createButton.setText(R.string.create_event);
                        }
                    }
                });

                break;

            case R.id.backButton:
                // Create explicit intent to go into MainPage
                Intent intent = new Intent(CreateEventActivity.this, MainPageActivity.class);
                startActivity(intent);

            case R.id.createEventModule:
                final Container<Integer> modulesContainer = new Container<>();
                Utils.chooseSingleModule(moduleStringList, modulesContainer, CreateEventActivity.this);

                selectedModuleReference = moduleReferences.get(modulesContainer.get());
                createModule.setText(moduleStringList.get(modulesContainer.get()));
                break;

            case R.id.setImageButton:
                CropDialogFragment cropDialogFragment = new CropDialogFragment(new CropDialogInterface() {
                    @Override
                    public void onDialogResult(Uri uri) {
                        createImage.setImageURI(uri);
                    }
                });
                cropDialogFragment.show(getSupportFragmentManager(), CropDialogFragment.TAG);
                break;

            case R.id.createEventStartDateTime:
                dateTimePicker(createStart);
                break;

            case R.id.createEventEndDateTime:
                dateTimePicker(createEnd);
        }
    }

    void chooseModule() {
        String[] moduleArray = moduleStringList.toArray(new String[moduleStringList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateEventActivity.this
        );
        builder.setTitle("Select Module");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(moduleArray, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, moduleStringList.get(i) + " selected.");
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    i = 0;
                    selectedModuleReference = moduleReferences.get(i);
                    createModule.setText(moduleStringList.get(i));
                }
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    /**
     * DateTimePicker
     * Adapted to take in inputs and set date/time for different texts
     * https://stackoverflow.com/questions/2055509/how-to-create-a-date-and-time-picker-in-android
     */
    public void dateTimePicker(EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        Calendar dateTime;

        // Assign for different widgets
        if (editText == createStart) {
            if (startDateTime == null) {
                startDateTime = Calendar.getInstance();
            }
            dateTime = startDateTime;
        } else {
            if (endDateTime == null) {
                endDateTime = Calendar.getInstance();
            }
            dateTime = endDateTime;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);

                        // Adapted and allowed setting of text
                        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM @ hh:mm aa");
                        String stringDateTime = dateFormat.format(dateTime.getTime());

                        editText.setText(stringDateTime);
                        Log.i(TAG, "dateTimePicker: Success for " + getResources().getResourceEntryName(editText.getId()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        // Limiting input to valid time frame
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        if (editText == createStart) {
            if (endDateTime != null) {
                datePickerDialog.getDatePicker().setMaxDate(endDateTime.getTimeInMillis());
            }
        } else {
            if (startDateTime != null) {
                datePickerDialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis());
            }
        }
        datePickerDialog.show();
        Log.i(TAG, "dateTimePicker: Dialog launched");
    }
}
