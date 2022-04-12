package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.EventViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
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
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        autoCompleteTxt = findViewById(R.id.autoCompleteTxt);

        db = FirebaseFirestore.getInstance();
        getModulesFromFirestore(); // populate array list with modules
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
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, moduleItems);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Log.i(TAG, item);
            }
        });
    }

    //
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

    protected Query filterEvents(int capacitySelection) {
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereGreaterThanOrEqualTo("capacity", capacitySelection);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected Query filterEvents(ArrayList<String> moduleSelection, int capacitySelection) {
        //Only accepts up to 10 comparisons, i.e. modulesDocRef must have at most 10 elements
        ArrayList<DocumentReference> modulesDocRef = new ArrayList<>();
        for (String key: moduleSelection) {
            modulesDocRef.add(modulesMap.get(key));
        }

        //TODO: Need to check whether compound queries such as the one below works
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereIn("modules", modulesDocRef).whereGreaterThanOrEqualTo("capacity", capacitySelection);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected void buildFirestoreRecyclerView(Query query) {
        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                // Uses layout called R.layout.event_row
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventModel model) {
                Log.d(TAG, "Query " + model);

                holder.event_title.setText(model.getTitle());
                holder.event_description.setText(model.getDescription());
                Utils.loadImage(model.getImagePath(), holder.event_image);
                holder.status.setText(model.getStatus());
                holder.location.setText(model.getVenue());
                holder.capacity.setText(model.getCapacityString());
                holder.event_start.setText(model.getEventStartTimeString());
                holder.event_end.setText(model.getEventEndTimeString());
                holder.event_date.setText(model.getEventStartDate());
//                String documentId = getSnapshots().getSnapshot(position).getId();
                String documentId = getSnapshots().getSnapshot(position).getReference().getPath();

                // Bring users to View Event when clicking on viewEventButton
                holder.viewEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Intent intent = new Intent(FilterActivity.this, ViewEventActivity.class);
                        intent.putExtra("DOCUMENT_ID",documentId);
                        startActivity(intent);
                    }
                });
            }
        };
    }

}

