package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getDocumentFromPath;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.ImageHandler;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.google.android.datatransport.runtime.dagger.Module;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.LogDescriptor;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener{
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
                if (moduleReference != null){
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
        switch(view.getId()) {
            case R.id.editEventButton:
                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(EditEventActivity.this, R.string.internet_required, Toast.LENGTH_SHORT).show();
                    return;
                }

                editButton.setEnabled(false);
                editButton.setText("Editing event...");

                // Check if data are all filled and valid
                if (invalidData(editName) |
                        invalidData(editDescription) |
                        invalidData(editVenue) |
                        invalidData(editModule) |
                        invalidData(editCapacity) |
                        invalidData(editStart) |
                        invalidData(editEnd)) {
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
                                                Log.i(TAG, "createEvent: Successful. Event added to Firebase");

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
                            editButton.setText(R.string.create_event);
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
                chooseImage();
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
     * Entry validation
     * https://www.c-sharpcorner.com/UploadFile/1e5156/validation/
     */
    boolean invalidData(TextView editText) {
        if (editText.getText().toString().length() == 0) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
            return true;
        } else {
            return false;
        }
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

    /**
     * CropImage helper functions
     * Call function: chooseImage()
     */
    //<editor-fold desc="CropImage helper functions">
    // Creating AlertDialog for user action
    // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
    // Edited the part where user can still click and request individually
    void chooseImage() {
        // Creating AlertDialog for user action
        // Adapted from https://medium.com/analytics-vidhya/how-to-take-photos-from-the-camera-and-gallery-on-android-87afe11dfe41
        // Edited the part where user can still click and request individually
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // edit a menuOption Array
        Log.i(TAG, "chooseImage: Dialog launched");
        // edit a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    cameraLaunch();
                    Log.i(TAG, "chooseImage: Camera chosen");
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    galleryLaunch();
                    Log.i(TAG, "chooseImage: Gallery chosen");
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                    Log.i(TAG, "chooseImage: Dialog dismissed");
                }
            }
        });
        builder.show();
    }

    void cameraLaunch() {
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(EditEventActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Start new CropActivity provided by library
            // https://github.com/CanHub/Android-Image-Cropper
            CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
            options.setAspectRatio(1,1);
            options.setImageSource(false, true);
            cropImage.launch(options);
            Log.i(TAG, "cameraLaunch: Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            Log.i(TAG, "cameraLaunch: Permission for camera requested");
        }
    }

    void galleryLaunch() {
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(EditEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Start new CropActivity provided by library
            // https://github.com/CanHub/Android-Image-Cropper
            CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions());
            options.setAspectRatio(1,1);
            options.setImageSource(true, false);
            cropImage.launch(options);
            Log.i(TAG, "galleryLaunch: Permission allowed, camera launched");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestGalleryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.i(TAG, "galleryLaunch: Permission for camera requested");
        }
    }

    // Used for receiving activity result from CropImage
    // Read on Android contract options
    // https://developer.android.com/training/basics/intents/result
    // https://www.youtube.com/watch?v=DfDj9EadOLk
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(
            new CropImageContract(),
            new ActivityResultCallback<CropImageView.CropResult>() {
                @Override
                public void onActivityResult(CropImageView.CropResult result) {
                    if (result!=null ) {
                        if (result.isSuccessful() && result.getUriContent() != null) {
                            Uri selectedImageUri = result.getUriContent();
                            editImage.setImageURI(selectedImageUri);
                            Log.i(TAG, "onActivityResult: Cropped image set");
                        } else {
                            Log.d(TAG, "onActivityResult: Cropping returned null");
                        }
                    }
                }
            });

    // Using launchers to request for permission
    // https://developer.android.com/training/permissions/requesting
    ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) {
                        // Permission is granted. Continue the action or workflow in your app.
                        cameraLaunch();
                    } else {
                        Toast.makeText(EditEventActivity.this, R.string.camera_access, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "PermissionRequest: Camera access denied");
                    }
                }
            });

    ActivityResultLauncher<String> requestGalleryPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) {
                        // Permission is granted. Continue the action or workflow in your app.
                        galleryLaunch();
                    } else {
                        Toast.makeText(EditEventActivity.this, R.string.storage_access, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "PermissionRequest: Gallery access denied");
                    }

                }
            });
    //</editor-fold>

}
