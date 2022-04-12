package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication2.fragments.ExploreFragment;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.EventViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Filter Activity is used instead of Filter Fragment due to bugs - yk
 * Filter Activity returns a page that allows user to filter the type of events
 * they want to join
 */

public class FilterActivity extends AppCompatActivity {



    private static final String TAG = "FilterActivity";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    //Store Hashmap of name, DocumentReference to update ArrayList of Modules
    Map<String, DocumentReference> modulesMap = new HashMap<>();
    ArrayList<String> moduleItems = new ArrayList<>();

    //UI elements
    ImageView backArrow;
    Button filterButton;

    AutoCompleteTextView autoCompleteTxtCapacity;
    String[] items = {"1", "2", "3", "4", "5", "6", "7", "8"};
    ArrayAdapter<String> adapterItemsCapacity;

    AutoCompleteTextView autoCompleteTxtModules;
    ArrayAdapter<String> adapterItemsModules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        autoCompleteTxtModules = findViewById(R.id.autoCompleteTxt);
        autoCompleteTxtCapacity = findViewById((R.id.autoCompleteTxtCapacity));

        db = FirebaseFirestore.getInstance();
        getModulesFromFirestore(); // populate array list with modules

        backArrow = (ImageView) findViewById(R.id.BackArrow);
        backArrow.setOnClickListener(new ClickListener());
        filterButton = findViewById(R.id.FilterButton);
        filterButton.setOnClickListener(new ClickListener());
    }

    //Get Modules from Firestore for Auto Complete Text
    protected void getModulesFromFirestore() {
        db.collection("Modules")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.i(TAG, document.getId() + " => " + document.getData() + "\n" + document.getReference());
                            modulesMap.put(document.getString("name"), document.getReference());
                        }
                        Log.i(TAG, "HashMap Keys: " + modulesMap.keySet());
                        Set<String> keys = modulesMap.keySet();
                        moduleItems = new ArrayList<>(keys);
                        Log.i(TAG, "ArrayList: moduleItems: " + moduleItems);
                        setAutoCompleteTxt();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting documents: ", e);
                    }
                });

    }

    //After querying for modules from Firestore, set up the AutoCompleteText Adapter
    protected void setAutoCompleteTxt() {
        adapterItemsModules = new ArrayAdapter<String>(this, R.layout.list_item, moduleItems);
        autoCompleteTxtModules.setAdapter(adapterItemsModules);

        adapterItemsCapacity = new ArrayAdapter<String>(this, R.layout.list_item, items);
        autoCompleteTxtCapacity.setAdapter(adapterItemsCapacity);

        autoCompleteTxtModules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Log.i(TAG, item);
            }
        });

        autoCompleteTxtCapacity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item2 = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: " + item2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.BackArrow) {
                startActivity(new Intent(FilterActivity.this, MainPageActivity.class));
            } else if (view.getId() == R.id.FilterButton) {
                //FIXME: Pass values such as module selection and capacity selection as an intent for processing in ExploreFragment
                Intent intent = new Intent(FilterActivity.this, ExploreFragment.class);
                ArrayList<String> moduleSelection = new ArrayList<>();
                String capacitySelection = "";
                intent.putStringArrayListExtra("MODULES_SELECTION", moduleSelection);
                intent.putExtra("CAPACITY_SELECTION", capacitySelection);
                startActivity(intent);
            }
        }
    }

    //FIXME: Move Methods to ExploreFragment to perform query based on values passed in Filter Button via intent
    protected Query filterEvents(ArrayList<String> moduleSelection) {
        //Only accepts up to 10 comparisons, i.e. modulesDocRef must have at most 10 elements
        ArrayList<DocumentReference> modulesDocRef = new ArrayList<>();
        for (String key: moduleSelection) {
            modulesDocRef.add(modulesMap.get(key));
        }

        Query query = db.collection("Events").whereIn("modules", modulesDocRef);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected Query filterEvents(String capacitySelection) {
        int capacity = Integer.parseInt(capacitySelection);
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereGreaterThanOrEqualTo("capacity", capacity);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected Query filterEvents(ArrayList<String> moduleSelection, String capacitySelection) {
        int capacity = Integer.parseInt(capacitySelection);
        //Only accepts up to 10 comparisons, i.e. modulesDocRef must have at most 10 elements
        ArrayList<DocumentReference> modulesDocRef = new ArrayList<>();
        for (String key: moduleSelection) {
            modulesDocRef.add(modulesMap.get(key));
        }

        //TODO: Need to check whether compound queries such as the one below works
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereIn("modules", modulesDocRef).whereGreaterThanOrEqualTo("capacity", capacity);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }
}

