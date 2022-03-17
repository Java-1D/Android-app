package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication2.objectmodel.EventModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewEventsActivity extends AppCompatActivity {

    private static final String TAG = "Test";
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView eventsList; // providing views that represent items in a data set.
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);


        firebaseFirestore = FirebaseFirestore.getInstance();
        eventsList = findViewById(R.id.events_list);

        // Query
        Query query = firebaseFirestore.collection("Event");
        Log.d(TAG, query.toString());

        // RecyclerOptions
        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();

        // Recyler Adapter
        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                // Uses layout called R.layout.event_item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventModel model) {
                // binds query object  (TestModel) to recycler view TestViewHolder
                Log.d(TAG, model.toString());
                Log.d(TAG, String.valueOf(holder));
                holder.event_title.setText(model.getTitle());
                holder.user_created.setText(model.getUserCreated());

            }


        };

        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.setAdapter(adapter);


    }
    // View Holder Class

    private class EventViewHolder extends RecyclerView.ViewHolder {

        // Declare Text view and set data

        private TextView event_title;
        private TextView user_created;

        // passed in the item from oncreate
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_title = (TextView) itemView.findViewById(R.id.event_title);
            user_created = (TextView) itemView.findViewById(R.id.user_created);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}