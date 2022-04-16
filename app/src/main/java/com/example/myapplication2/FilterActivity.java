package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication2.utils.FirebaseQuery;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Filter Activity returns a page that allows user to filter the type of events
 * they want to join
 */

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "FilterActivity";
    public static final String CAPACITY_SELECTION = "CAPACITY_SELECTION";
    public static final String MODULE_SELECTION = "MODULE_SELECTION";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    //Store Hashmap of name, DocumentReference to update ArrayList of Modules
    Map<String, DocumentReference> modulesMap = new HashMap<>();
    ArrayList<String> moduleItems = new ArrayList<>();

    //UI elements
    ImageView backArrow;
    Button filterButton;

    AutoCompleteTextView autoCompleteTxtCapacity;
    String[] items = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};
    ArrayAdapter<String> adapterItemsCapacity;

    AutoCompleteTextView autoCompleteTxtModules;
    ArrayAdapter<String> adapterItemsModules;

    //User-selected values
    String moduleSelection;
    int capacitySelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        autoCompleteTxtModules = findViewById(R.id.autoCompleteTxt);
        autoCompleteTxtCapacity = findViewById((R.id.autoCompleteTxtCapacity));

        db = FirebaseFirestore.getInstance();
        getModulesFromFirestore(); // populate array list with modules

        backArrow = findViewById(R.id.BackArrow);
        backArrow.setOnClickListener(new ClickListener());
        filterButton = findViewById(R.id.FilterButton);
        filterButton.setOnClickListener(new ClickListener());
    }

    //Get Modules from Firestore for Auto Complete Text
    protected void getModulesFromFirestore() {
        String collectionId = "Modules";
        FirebaseQuery firebaseQuery = new FirebaseQuery() {

            @Override
            public void callbackOnSuccess(QuerySnapshot queryDocumentSnapshots) {
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
        };
        firebaseQuery.run(collectionId);
    }

    //After querying for modules from Firestore, set up the AutoCompleteText Adapter
    protected void setAutoCompleteTxt() {
        adapterItemsModules = new ArrayAdapter<>(this, R.layout.list_item, moduleItems);
        autoCompleteTxtModules.setAdapter(adapterItemsModules);

        adapterItemsCapacity = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTxtCapacity.setAdapter(adapterItemsCapacity);

        autoCompleteTxtModules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String module = adapterView.getItemAtPosition(i).toString();
                moduleSelection = Objects.requireNonNull(modulesMap.get(module)).getPath();
                Log.i(TAG, moduleSelection);
            }
        });

        autoCompleteTxtCapacity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                capacitySelection = Integer.parseInt(parent.getItemAtPosition(position).toString());
                Log.i(TAG, String.valueOf(capacitySelection));
            }
        });
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.BackArrow) {
                startActivity(new Intent(FilterActivity.this, MainPageActivity.class));
            } else if (view.getId() == R.id.FilterButton) {

                //shared preferences for previous selection
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FilterActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(MODULE_SELECTION, moduleSelection);
                editor.putInt(CAPACITY_SELECTION, capacitySelection);
                editor.apply();

                Log.i(TAG, "Filter Selection applied to shared Preferences");

                Toast.makeText(FilterActivity.this, "Filters Applied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}

