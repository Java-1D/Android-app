
package com.example.myapplication2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication2.CreateEventActivity;
import com.example.myapplication2.FilterActivity;
import com.example.myapplication2.LoginActivity;
import com.example.myapplication2.MainPageActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.ViewEventActivity;
import com.example.myapplication2.objectmodel.EventModel;
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

public class ExploreFragment extends Fragment {
    private static final String TAG = "ExploreFragment";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    Query query;

    //RecyclerView
    private RecyclerView eventsList; // providing views that represent items in a data set.
    private FirestoreRecyclerAdapter adapter;

    //Store Hashmap of name, DocumentReference to update ArrayList of Modules
    Map<String, DocumentReference> modulesMap = new HashMap<>();

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_events, container, false);
        eventsList = view.findViewById(R.id.recyclerViewEvents);
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(eventsList.getContext()));
        eventsList.setAdapter(adapter);

        // Button to create new event
        view.findViewById(R.id.create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                ((MainPageActivity) getActivity()).startActivity(intent);
            }
        });

        // Button to start FilterActivity
        view.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                ((MainPageActivity) getActivity()).startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Query
        //TODO: Replace Query object from Filter Activity if there is no null values passed via intent
        query = db.collection("Events");
        Log.d(TAG, "Query" + query.toString());
        buildFirestoreRecyclerView(query);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "OnStart is called");
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume is called");

        Intent intent = getActivity().getIntent();
        String modulePath = intent.getStringExtra("MODULE_SELECTION");
        int capacity = intent.getIntExtra("CAPACITY_SELECTION", 0);

        if (modulePath != null && capacity != 0) {
             query = filterEvents(modulePath, capacity);
        } else if (modulePath != null) {
            query = filterEvents(modulePath);
        } else if (capacity != 0) {
            query = filterEvents(capacity);
        } else {
            query = db.collection("Events");
            Log.d(TAG, "Query" + query.toString());
        }
        buildFirestoreRecyclerView(query);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop is called");
        adapter.stopListening();
    }

    //FIXME: Move Methods to ExploreFragment to perform query based on values passed in Filter Button via intent
    protected Query filterEvents(String moduleSelection) {
        DocumentReference moduleDocRef = db.document(moduleSelection);

        Query query = db.collection("Events").whereEqualTo("modules", moduleDocRef);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected Query filterEvents(int capacitySelection) {
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereGreaterThanOrEqualTo("capacity", capacitySelection);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    protected Query filterEvents(String moduleSelection, int capacitySelection) {
        DocumentReference moduleDocRef = db.document(moduleSelection);

        //TODO: Need to check whether compound queries such as the one below works
        //Filter based on Capacity Chosen -> Checks whether an events have at least x slots available for user and his friends to join
        Query query = db.collection("Events").whereEqualTo("modules", moduleDocRef).whereGreaterThanOrEqualTo("capacity", capacitySelection);
        Log.d(TAG, "Query" + query.toString());
        return query;
    }

    void buildFirestoreRecyclerView(Query query) {
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

                holder.event_title.setText(model.getTitle());
                holder.event_description.setText(model.getDescription());
                Utils.loadImage(model.getImagePath(), holder.event_image);
                holder.status.setText(model.getStatus());
                holder.location.setText(model.getVenue());
                holder.capacity.setText(model.getCapacityString());
                holder.event_start.setText(model.getEventStartTimeString());
                holder.event_end.setText(model.getEventEndTimeString());
                holder.event_date.setText(model.getEventStartDate());

                String documentId = getSnapshots().getSnapshot(position).getReference().getPath();

                // Bring users to View Event when clicking on viewEventButton
                holder.viewEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                        intent.putExtra("DOCUMENT_ID",documentId);
                        ((MainPageActivity) getActivity()).startActivity(intent);
                    }
                });

            }

        };
    }
}
