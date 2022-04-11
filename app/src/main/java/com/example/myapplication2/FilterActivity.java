package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Filter Activity is used instead of Filter Fragment due to bugs - yk
 */
public class FilterActivity extends AppCompatActivity {
  
    private static final String TAG = "FILTER_ACTIVITY";

    /**
     * Filter Activity returns a page that allows user to filter the type of events
     * they want to join
     */
    ArrayList<String> moduleItems = new ArrayList<String>();
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getModules(firebaseFirestore, moduleItems); // populate array list with modules


        autoCompleteTxt = findViewById(R.id.autoCompleteTxt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, moduleItems);
        autoCompleteTxt.setAdapter(adapterItems);


        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });
    }


    void getModules(FirebaseFirestore firebaseFirestore, ArrayList<String> modules) {
        final FirebaseContainer<ArrayList<String>> container = new FirebaseContainer<>();

        firebaseFirestore.collection("Modules")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getData().toString());
                                modules.add(document.getData().get("name").toString());
                            }
                        } else {
                        }
                        container.set(modules);
                        Log.d(TAG, "Container Object" + container.get().toString());
                    }

                });

    }

}

