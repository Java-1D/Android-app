
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.R;
import com.example.myapplication2.archive.ViewEventsActivity;
import com.example.myapplication2.objectmodel.EventModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private static final String TAG = "Test";
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView eventsList; // providing views that represent items in a data set.
    private FirestoreRecyclerAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Query
        Query query = firebaseFirestore.collection("Events");
        Log.d(TAG, query.toString());

        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EventModel, HomeFragment.EventViewHolder>(options) {
            @NonNull
            @Override
            public HomeFragment.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                // Uses layout called R.layout.event_item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.EventViewHolder holder, int position, @NonNull EventModel model) {
                // binds query object  (TestModel) to recycler view TestViewHolder
                Log.d(TAG, model.toString());
                Log.d(TAG, String.valueOf(holder));
                Log.d(TAG, model.getTitle());
                holder.event_title.setText(model.getTitle());
                holder.event_description.setText(model.getDescription());

                // Get location
                //setLocationDetails(model.getLocationReference(),holder);


            }
        };
    }

    // View Holder Class

    private class EventViewHolder extends RecyclerView.ViewHolder {

        // Declare Text view and set data
        private TextView capacity;

        private TextView event_title;
        private TextView event_description;
        private TextView status;
        private TextView location;
        private ImageView locationImage;


        // passed in the item from oncreate
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_title = (TextView) itemView.findViewById(R.id.event_title);
            event_description = (TextView) itemView.findViewById(R.id.event_desc);
        }


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

