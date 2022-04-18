
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
import com.example.myapplication2.MainPageActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.ViewEventActivity;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.EventViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/*
Home Fragment contains the Fragment for the Homepage. I.e View All Events
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirestoreRecyclerAdapter<EventModel, EventViewHolder> adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // providing views that represent items in a data set.
        RecyclerView eventsList = view.findViewById(R.id.recyclerViewEvents);
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

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference user = LoggedInUser.getInstance().getUserDocRef();

        Query query = firebaseFirestore.collection("Events")
                .whereArrayContains("userJoined", user);

        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
