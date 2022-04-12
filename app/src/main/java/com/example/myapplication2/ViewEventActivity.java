package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getCurrentUser;
import static com.example.myapplication2.utils.Utils.getDocumentFromPath;
import static com.example.myapplication2.utils.Utils.getProfileID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.objectmodel.UserModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.ProfileViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "ViewEvents";
    FirebaseFirestore db;

    TextView event_name;
    TextView event_desc;
    TextView event_creator;
    ShapeableImageView person;
    ShapeableImageView location_pic;
    ImageView calendar_icon;
    TextView date;
    ImageView start_time_icon;
    TextView start_time;
    ImageView end_time_icon;
    TextView end_time;
    ImageView location_icon;
    TextView location;
    ImageView information_icon;
    TextView information;
    ImageView emoji;
    TextView no_of_ppl;
    //    ShapeableImageView person1;
//    ShapeableImageView person2;
//    ShapeableImageView person3;
//    TextView name1;
//    TextView name2;
//    TextView name3;
//    ImageView search1;
//    ImageView search2;
//    ImageView search3;
    MaterialButton join_button;
    MaterialButton edit_event_button;
    String documentName = "";
    DocumentReference user; //singleton User
    DocumentReference docRef;
    ImageView backButton;


    // Recycler View
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView usersList; // providing views that represent items in a data set.

    ArrayList<DocumentReference> usersJoined = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_events_users);

        db = FirebaseFirestore.getInstance();

        event_name = findViewById(R.id.event_name); // DONE
        event_desc = findViewById(R.id.event_desc); // DONE
        event_creator = findViewById(R.id.event_creator); // Need to retrieve
        person = findViewById(R.id.person); // Need to retrieve
        location_pic = findViewById(R.id.location_pic); // DONE
        calendar_icon = findViewById(R.id.calendar_icon);
        date = findViewById(R.id.date); // DONE
        start_time_icon = findViewById(R.id.start_time_icon);
        start_time = findViewById(R.id.start_time); // DONE
        end_time_icon = findViewById(R.id.end_time_icon);
        end_time = findViewById(R.id.end_time); // DONE
        location_icon = findViewById(R.id.location_icon);
        location = findViewById(R.id.location); // DONE
        information_icon = findViewById(R.id.information_icon);
        information = findViewById(R.id.information); // Need to retrieve
        emoji = findViewById((R.id.emoji));
        no_of_ppl = findViewById(R.id.no_of_ppl); // Need to retrieve

        join_button = findViewById(R.id.join_button);
        usersList = findViewById(R.id.users_list);
        edit_event_button = findViewById(R.id.edit_event_button);
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        join_button.setOnClickListener(this);
        join_button.setEnabled(true);

        edit_event_button.setVisibility(View.GONE);
        edit_event_button.setOnClickListener(this);
        edit_event_button.setEnabled(false);


        usersJoined.add(db.document("/Users/Test"));

        user = getCurrentUser(db);

        String documentId = getIntent().getStringExtra("DOCUMENT_ID");
        documentName = getDocumentFromPath(documentId);
        Log.i(TAG, "Document Name" + documentName);

        docRef = db.collection("Events").document(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                setEventDetails(eventModel);
                DocumentReference userCreated = eventModel.getUserCreated();
                Log.d(TAG, "getUserCreated : " + userCreated + "\n currentuser : " + user);

                // logic to change join event to edit event
                if (user != null && userCreated != null) {
                    if (userCreated.getPath().equals(user.getPath())) {
                        Log.d(TAG, "works : ");
                        join_button.setEnabled(false);
                        join_button.setClickable(false); // Bug here
                        join_button.setVisibility(View.GONE);
                        join_button.invalidate();
//                        join_button.refreshDrawableState();
                        Log.i(TAG, "check for join button" + join_button.isEnabled());

                        edit_event_button.setVisibility(View.VISIBLE);
                        edit_event_button.setEnabled(true);
                        edit_event_button.setClickable(true); // Bug here
                    }
                }


                adapter.startListening();

                usersList.setHasFixedSize(true);
                usersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                usersList.setAdapter(adapter);

            }
        });


    }

    private void setEventDetails(EventModel eventModel) {
        setModuleDetails(eventModel.getModule(), information);

        event_name.setText(eventModel.getTitle());
        event_desc.setText(eventModel.getDescription());
        location.setText(eventModel.getVenue());
        start_time.setText("Start: " + eventModel.getEventStartTimeString());
        end_time.setText("End: " + eventModel.getEventEndTimeString());
        date.setText(eventModel.getEventDateString());
        Utils.loadImage(eventModel.getImagePath(), location_pic);
        setCreatorDetails(eventModel.getUserCreated(), person, event_creator);
        no_of_ppl.setText(eventModel.getRemainingCapacity());

        // get users joined
        usersJoined = eventModel.getUserJoined();
        Log.i(TAG, "" + usersJoined);
        setUserJoinedRecyclerView();
    }

    private void setModuleDetails(DocumentReference moduleReference, TextView text_name) {
        if (moduleReference != null) {
            moduleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ModuleModel model = documentSnapshot.toObject(ModuleModel.class);
                    text_name.setText(model.getModuleName());
                }
            });

        } else {
            Log.d(TAG, "No module reference");
        }

    }

    private void setCreatorDetails(DocumentReference userReference, ImageView creatorProfilePic, TextView creatorName) {
        if (userReference != null) {
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel model = documentSnapshot.toObject(UserModel.class);
                    setCreatorProfileDetails(model.getProfile(), creatorProfilePic, creatorName);
                }
            });
        } else {
            Log.d(TAG, "User Reference is Null");
        }

    }

    private void setCreatorProfileDetails(DocumentReference profileReference, ImageView image_name, TextView creatorName) {
        profileReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ProfileModel model = documentSnapshot.toObject(ProfileModel.class);
                Utils.loadImage(model.getImagePath(), image_name);
                creatorName.setText(model.getName());


            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_event_button:
                backButton.setEnabled(false);
                Intent editIntent = new Intent(ViewEventActivity.this, EditEventActivity.class);
                editIntent.putExtra("DOCUMENT_ID", getDocumentFromPath(docRef.getPath()));
                Toast.makeText(ViewEventActivity.this, "Editing Event", Toast.LENGTH_SHORT).show();
                ViewEventActivity.this.startActivity(editIntent);

            case R.id.join_button:
                if (join_button.isEnabled()) {
                    DocumentReference docRef = db.collection("Events").document(documentName);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                            usersJoined = eventModel.getUserJoined();
                            int current = usersJoined.size();
                            if (current == eventModel.getCapacity()) {
                                Toast.makeText(ViewEventActivity.this, "The event is full! So sorry!", Toast.LENGTH_SHORT).show();
                            };

                            // check if user is already in the list
                            if (usersJoined.contains(user)) {
                                Toast.makeText(ViewEventActivity.this, "You have already joined the event", Toast.LENGTH_SHORT).show();
                            } else {
                                // append the array list of usersJoined with your Profile
                                usersJoined.add(user);
                                docRef.update("userJoined", FieldValue.arrayUnion(user));

                                // update the firebase with usersJoined
                                Toast.makeText(ViewEventActivity.this, "You have successfully joined the event", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }


            case R.id.backButton:
                if (backButton.isEnabled()) {
                    // Create explicit intent to go into MainPage
                    Intent mainActivityIntent = new Intent(ViewEventActivity.this, MainPageActivity.class);
                    Toast.makeText(ViewEventActivity.this, "Home Page", Toast.LENGTH_SHORT).show();
                    startActivity(mainActivityIntent);
                }

        }
    }

    private void setUserJoinedRecyclerView() {
        // RecyclerView
        Query query = db.collection("Profiles")
                .whereIn("userId", usersJoined);


        FirestoreRecyclerOptions<ProfileModel> options = new FirestoreRecyclerOptions.Builder<ProfileModel>()
                .setQuery(query, ProfileModel.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<ProfileModel, ProfileViewHolder>(options) {
            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_events_users_item, parent, false);
                return new ProfileViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull ProfileModel model) {
                Log.d(TAG, "Query " + model);
                holder.username.setText(model.getName());
                Utils.loadImage(model.getImagePath(), holder.user_image);
                // Bring users to View Event when clicking on viewEventButton
                String profileId = getProfileID(getSnapshots().getSnapshot(position).getReference().getPath());
                holder.user_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        Toast.makeText(ViewEventActivity.this, "UserProfile Clicked : " + profileId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewEventActivity.this, ProfilePage.class);
                        intent.putExtra("PROFILE_ID", profileId);
                        ViewEventActivity.this.startActivity(intent);
                    }
                });

            }
        };


    }

    ;

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
