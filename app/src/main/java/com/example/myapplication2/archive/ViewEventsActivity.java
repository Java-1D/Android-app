//package com.example.myapplication2.archive;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.myapplication2.R;
//import com.example.myapplication2.objectmodel.EventModel;
//import com.example.myapplication2.objectmodel.LocationModel;
//import com.example.myapplication2.objectmodel.UserModel;
//import com.example.myapplication2.utils.Utils;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//
//// View Events Activity is the activity responsible for getting data from firebase and populating the screen with it
//public class ViewEventsActivity extends AppCompatActivity {
//
//    private static final String TAG = "Test";
//    private FirebaseFirestore firebaseFirestore;
//    private RecyclerView eventsList; // providing views that represent items in a data set.
//    private FirestoreRecyclerAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_events);
//
//
//        firebaseFirestore = FirebaseFirestore.getInstance();
//        eventsList = findViewById(R.id.events_list);
//
//        // Query
//        Query query = firebaseFirestore.collection("Events");
//        Log.d(TAG, query.toString());
//
//        // RecyclerOptions
//        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
//                .setQuery(query, EventModel.class)
//                .build();
//
//        // Recycler Adapter
//        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
//            @NonNull
//            @Override
//            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                // Creates a new instance of View Holder
//                // Uses layout called R.layout.event_item
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
//                return new EventViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventModel model) {
//                // binds query object  (TestModel) to recycler view TestViewHolder
//                Log.d(TAG, model.toString());
//                Log.d(TAG, String.valueOf(holder));
//                Log.d(TAG, model.getTitle());
//                holder.event_title.setText(model.getTitle());
//                holder.event_description.setText(model.getDescription());
//
//                // Get location
//                setLocationDetails(model.getVenue(),holder);
//
//
//            }
//
//            private void setLocationDetails(DocumentReference locationReference, EventViewHolder holder){
//                locationReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        LocationModel location = documentSnapshot.toObject(LocationModel.class);
//                        holder.location.setText(location.getTitle());
//                        Bitmap imageBitmap = Utils.getImageBitmap(location.getImageRef());
//                        holder.locationImage.setImageBitmap(imageBitmap);
//                    }
//                });
//            }
//
//
//
//
//        };
//
//        eventsList.setHasFixedSize(true);
//        eventsList.setLayoutManager(new LinearLayoutManager(this));
//        eventsList.setAdapter(adapter);
//
//
//    }
//    // View Holder Class
//
//    private class EventViewHolder extends RecyclerView.ViewHolder {
//
//        // Declare Text view and set data
//        private TextView capacity;
//
//        private TextView event_title;
//        private TextView event_description;
//        private TextView status;
//        private TextView location;
//        private ImageView locationImage;
//
//
//        // passed in the item from oncreate
//        public EventViewHolder(@NonNull View itemView) {
//            super(itemView);
//            event_title = (TextView) itemView.findViewById(R.id.event_title);
//            event_description = (TextView) itemView.findViewById(R.id.event_desc);
//        }
//
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//}
