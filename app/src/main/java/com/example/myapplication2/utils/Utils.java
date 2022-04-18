package com.example.myapplication2.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication2.CreateEventActivity;
import com.example.myapplication2.MainPageActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.fragments.DatePickerDialogFragment;
import com.example.myapplication2.fragments.TimePickerDialogFragment;
import com.example.myapplication2.interfaces.CustomDialogInterface;
import com.example.myapplication2.objectmodel.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

public class Utils {

    private static final String TAG = "UTILS";

    // Global app dateFormat style
    public static final DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM @ hh:mm aa");


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean haveNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        Log.i(TAG, "Active Network: " + haveNetwork);
        return haveNetwork;
    }

    public static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    public static void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url).into(imageView);
    }


    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public static String getDocumentFromPath(String documentId){
        return documentId.substring(documentId.lastIndexOf("/") + 1);
    }

    public static DocumentReference getCurrentUser(FirebaseFirestore db){
        DocumentReference user = LoggedInUser.getInstance().getUserDocRef(); // singleton
        if (user != null) {
            return user;
        }
        user = db.document("/Users/Test4");
        return user;
    }

    public static String getProfileID(String path){
        // get the element before the spacing
        Log.i(TAG, "STRING" + path.substring(path.lastIndexOf('/') + 1));
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Entry validation using varargs (found online)
     * https://www.c-sharpcorner.com/UploadFile/1e5156/validation/
     */
    public static boolean invalidData(EditText... editTexts) {
        boolean invalid = false;
        for (EditText editText : editTexts) {
            invalid = invalid | invalidData(editText);
        }
        return invalid;
    }

    public static boolean invalidData(EditText editText) {
        if (editText.getText().toString().length() == 0) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
            return true;
        } else {
            return false;
        }
    }

    public static boolean invalidData(ArrayList<EditText> editTextArrayList){
        boolean invalid = false;
        for (EditText editText : editTextArrayList) {
            invalid = invalid | invalidData(editText);
        }
        return invalid;
    }



    public static void disableButton(MaterialButton button){
        button.setEnabled(false);
        button.setClickable(false);
        button.setVisibility(View.GONE);
        Log.d(TAG, "button : " + button + " disabled");
    }

    public static void disableButton(Button button){
        button.setEnabled(false);
        button.setClickable(false);
        button.setVisibility(View.GONE);
        Log.d(TAG, "button : " + button + " disabled");
    }

    public static void enableButton(MaterialButton button){
        button.setEnabled(true);
        button.setClickable(true);
        button.setVisibility(View.VISIBLE);
    }

    public static void dateTimePicker(FragmentManager manager, Calendar minDate, Calendar maxDate, CustomDialogInterface customDialogInterface) {
        DatePickerDialogFragment dateTimePickerDialogFragment = new DatePickerDialogFragment(
                new DatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onResult(Calendar date) {
                        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment(date,
                                new TimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onResult(Calendar time) {
                                Log.i(TAG, "dateTimePicker: " + ((Calendar) time).getTime());
                                customDialogInterface.onResult((Calendar) time);
                            }
                        });
                        timePickerDialogFragment.show(manager, TimePickerDialogFragment.TAG);
                    }
                });

        dateTimePickerDialogFragment.setMinDate(minDate);
        dateTimePickerDialogFragment.setMaxDate(maxDate);
        dateTimePickerDialogFragment.show(manager, DatePickerDialogFragment.TAG);
    }

    public static void pushEvent(Context context,
                                 Button confirmButton,
                                 String buttonText,
                                 ImageView eventImage,
                                 EditText eventName,
                                 EditText eventDescription,
                                 EditText eventVenue,
                                 EditText eventModule,
                                 EditText eventCapacity,
                                 EditText eventStart,
                                 EditText eventEnd,
                                 DocumentReference selectedModuleReference,
                                 Calendar startDateTime,
                                 Calendar endDateTime){

        if (!Utils.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.internet_required, Toast.LENGTH_SHORT).show();
            return;
        }

        confirmButton.setEnabled(false);
        confirmButton.setText("Uploading event...");

        ArrayList<EditText> editTextArrayList = new ArrayList<>(
                Arrays.asList(eventName,
                        eventDescription,
                        eventVenue,
                        eventModule,
                        eventCapacity,
                        eventStart,
                        eventEnd));

        if (Utils.invalidData(editTextArrayList)) {
            confirmButton.setEnabled(true);
            confirmButton.setText(buttonText);
            return;
        }

        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String venue = eventVenue.getText().toString();
        DocumentReference userCreated = LoggedInUser.getInstance().getUserDocRef();
        Integer capacity = Integer.parseInt(eventCapacity.getText().toString());

        // Checking that the data does not exist in Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(EventModel.COLLECTION_ID).document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        eventName.requestFocus();
                        eventName.setError("Please use a different event name.");
                        confirmButton.setEnabled(true);
                        confirmButton.setText(buttonText);
                    } else {
                        // Happens when eventName is not taken
                        // https://firebase.google.com/docs/storage/android/upload-files
                        // Uploading image into Firebase Storage
                        // Randomizing id for file name
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference eventImageRef = firebaseStorage.getReference().child(EventModel.COLLECTION_ID + "/" + UUID.randomUUID().toString());

                        // Get the data from an ImageView as bytes
                        eventImage.setDrawingCacheEnabled(true);
                        eventImage.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) eventImage.getDrawable()).getBitmap();
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
                                        String image = task.getResult().toString();

                                        EventModel eventModel = new EventModel(
                                                name,
                                                description,
                                                venue,
                                                selectedModuleReference,
                                                capacity,
                                                startDateTime.getTime(),
                                                endDateTime.getTime(),
                                                image,
                                                userCreated
                                        );

                                        db.collection(EventModel.COLLECTION_ID).document(name).set(eventModel);
                                        Log.i(TAG, "createEvent: Successful. Event added to Firebase");

                                        // Create explicit intent to go into MainPage
                                        Intent intent = new Intent(context, MainPageActivity.class);
                                        context.startActivity(intent);
                                    }
                                });

                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    confirmButton.setEnabled(true);
                    confirmButton.setText(R.string.create_event);
                }
            }
        });
    }
}
