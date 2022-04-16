package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getDocumentFromPath;

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
import com.example.myapplication2.interfaces.DialogInterfaces.URIDialogInterface;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
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

        backButton = (ImageView) findViewById(R.id.backButton);

        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

//         getting ID from intent
        String documentId = getIntent().getStringExtra("DOCUMENT_ID");
        documentName = getDocumentFromPath(documentId);
        Log.i(TAG, "Document Name" + documentName);

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

        // Get edit documentID from previous intent
        documentId = getIntent().getStringExtra("DOCUMENT_ID");

        // Checking that data exists in Firestore and can be retrieved and initializing values
        DocumentReference docRef = db.collection(EventModel.COLLECTION_ID).document(documentId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                editName.setText(eventModel.getTitle());

                Utils.loadImage(eventModel.getImagePath(), editImage);

                editDescription.setText(eventModel.getDescription());
                editVenue.setText(eventModel.getVenue());

                DocumentReference moduleReference = eventModel.getModule();
                if (moduleReference != null) {
                    moduleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ModuleModel moduleModel = documentSnapshot.toObject(ModuleModel.class);
                            editModule.setText(moduleModel.getName());
                            selectedModuleReference = eventModel.getModule();
                        }
                    });
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
        });

        editModule.setOnClickListener(this);
        editButton.setOnClickListener(this);
        setImageButton.setOnClickListener(this);
        editStart.setOnClickListener(this);
        editEnd.setOnClickListener(this);
        editName.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editEventButton:
                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(EditEventActivity.this, R.string.internet_required, Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<EditText> editTextArrayList = new ArrayList<>(
                        Arrays.asList(editName,
                                editDescription,
                                editVenue,
                                editModule,
                                editCapacity,
                                editStart,
                                editEnd));

                if (Utils.invalidData(editTextArrayList)) {
                    editButton.setEnabled(true);
                    editButton.setText(R.string.edit_event);
                    return;
                }

                String eventName = editName.getText().toString();
                String eventDescription = editDescription.getText().toString();
                String eventVenue = editVenue.getText().toString();

                DocumentReference userCreated = LoggedInUser.getInstance().getUserDocRef();

                Integer eventCapacity = Integer.parseInt(editCapacity.getText().toString());

                // https://firebase.google.com/docs/firestore/quickstart#java
                // Checking that the data does not exist in Firebase
                DocumentReference docRef = db.collection(EventModel.COLLECTION_ID).document(eventName);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // https://firebase.google.com/docs/storage/android/upload-files
                            // Uploading image into Firebase Storage
                            // Randomizing id for file name
                            StorageReference eventImageRef = firebaseStorage.getReference().child(EventModel.COLLECTION_ID + "/" + UUID.randomUUID().toString());

                            // Get the data from an ImageView as bytes
                            editImage.setDrawingCacheEnabled(true);
                            editImage.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) editImage.getDrawable()).getBitmap();
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
                                            Log.i(TAG, "editEvent: Successful. Event pushed to Firebase");

                                            // Create explicit intent to go into MainPage
                                            Intent intent = new Intent(EditEventActivity.this, MainPageActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            editButton.setEnabled(true);
                            editButton.setText(R.string.edit_event);
                        }
                    }
                });

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
                chooseModule();
                break;

            case R.id.setImageButton:
                CropDialogFragment cropDialogFragment = new CropDialogFragment(new URIDialogInterface() {
                    @Override
                    public void onResult(Uri uri) {
                        editImage.setImageURI(uri);
                    }
                });
                cropDialogFragment.show(getSupportFragmentManager(), CropDialogFragment.TAG);
                break;
            case R.id.editEventStartDateTime:
                dateTimePicker(editStart);
                break;

            case R.id.editEventEndDateTime:
                dateTimePicker(editEnd);
        }
    }

    /**
     * Module dialog picker
     */
    void chooseModule() {
        String[] moduleArray = moduleStringList.toArray(new String[moduleStringList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
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
                    editModule.setText(moduleStringList.get(i));
                }
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    /**
     * DateTimePicker
     * Adapted to take in inputs and set date/time for different texts
     * https://stackoverflow.com/questions/2055509/how-to-edit-a-date-and-time-picker-in-android
     */
    public void dateTimePicker(EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        Calendar dateTime;

        // Assign for different widgets
        if (editText == editStart) {
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTime.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        if (editText == editStart) {
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
