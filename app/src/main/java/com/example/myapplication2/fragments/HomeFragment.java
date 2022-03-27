
package com.example.myapplication2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication2.R;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.utils.EventDetails;
import com.example.myapplication2.viewholder.EventViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private static final String TAG = "HOMEFRAGMENT";
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView eventsList; // providing views that represent items in a data set.
    private FirestoreRecyclerAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        // Inflate the layout for this fragment
        eventsList = view.findViewById(R.id.recyclerView);
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(eventsList.getContext()));
        eventsList.setAdapter(adapter);

        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Query
        Query query = firebaseFirestore.collection("Events");
        Log.d(TAG, "Query" + query.toString());

        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                // Uses layout called R.layout.event_item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventModel model) {
                // binds query object  (TestModel) to recycler view TestViewHolder
                Log.d(TAG, model.toString());
                Log.d(TAG, String.valueOf(holder));
                Log.d(TAG, model.getTitle());
                Log.d(TAG, "DESC" + model.getDescription());
                holder.event_title.setText(model.getTitle());
                holder.event_description.setText(model.getDescription());
                EventDetails.setEventImage(model.getImagePath(),holder);
            }

        };
    }





    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}

