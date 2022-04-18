package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.disableButton;
import static com.example.myapplication2.utils.Utils.enableButton;
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

import com.example.myapplication2.db.EventsDb;
import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.objectmodel.UserModel;
import com.example.myapplication2.utils.FirebaseDocument;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.ProfileViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity implements View.OnClickListener {
    /*
        ViewEventActivity allows user to view a single event from the MainPageActivity
     */
    private static final String TAG = "ViewEvents";

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

    MaterialButton join_button;
    MaterialButton edit_event_button;
    MaterialButton leave_button;

    String documentName = "";
    DocumentReference user; //singleton User
    DocumentReference docRef;
    ImageView backButton;
    EventsDb db;

    // Recycler View
    private FirestoreRecyclerAdapter<ProfileModel, ProfileViewHolder> adapter;
    private RecyclerView usersList; // providing views that represent items in a data set.

    ArrayList<DocumentReference> usersJoined = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        db = new EventsDb();

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
        usersList = findViewById(R.id.users_list);

        join_button = findViewById(R.id.join_button);
        edit_event_button = findViewById(R.id.edit_event_button);
        leave_button = findViewById(R.id.leave_button);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        join_button.setOnClickListener(this);
        join_button.setEnabled(true);

        edit_event_button.setOnClickListener(this);
        disableButton(edit_event_button);

        leave_button.setOnClickListener(this);
        disableButton(leave_button);

        usersJoined.add(db.getDb().document("/Users/Test"));

        user = getCurrentUser(db.getDb());
        String documentId = getIntent().getStringExtra("DOCUMENT_ID");
        documentName = getDocumentFromPath(documentId);

        // Getting information for the current event
        docRef = db.getDocument(documentName);
        FirebaseDocument firebaseDocument = new FirebaseDocument() {

            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    EventModel eventModel = document.toObject(EventModel.class);
                    setEventDetails(eventModel);
                    DocumentReference userCreated = eventModel.getUserCreated();
                    Log.d(TAG, "getUserCreated : " + userCreated + "\n current user : " + user);

                    if (user != null && userCreated != null) {
                        Log.i(TAG, "User Created : " + userCreated + "user : " + user);
                        usersJoined = eventModel.getUserJoined();

                        // logic to change join event to edit event
                        if (userCreated.toString().equals(user.toString())) {
                            disableButton(join_button);
                            Log.i(TAG, "Enabling EditButton. Join Button is: " + join_button.isEnabled());
                            enableButton(edit_event_button);
                        } else if (usersJoined.contains(user)) {
                            disableButton(join_button);
                            enableButton(leave_button);
                        }
                    }

                    adapter.startListening();
                    usersList.setHasFixedSize(true);
                    usersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    usersList.setAdapter(adapter);
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        };
        firebaseDocument.run(docRef);
    }

    private void setEventDetails(EventModel eventModel) {
        setModuleDetails(eventModel.getModule(), information);

        event_name.setText(eventModel.getTitle());
        event_desc.setText(eventModel.getDescription());
        location.setText(eventModel.getVenue());
        start_time.setText(String.format("Start: %s", eventModel.getEventStartTimeString()));
        end_time.setText(String.format("End: %s", eventModel.getEventEndTimeString()));
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
            FirebaseDocument firebaseDocument = new FirebaseDocument() {
                @Override
                public void callbackOnSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        ModuleModel model = document.toObject(ModuleModel.class);
                        text_name.setText(model.getModuleName());
                    }
                    else {
                        Log.w(TAG, "Document does not exist");
                    }
                }
            };
            firebaseDocument.run(moduleReference);

        } else {
            Log.d(TAG, "No module reference");
        }

    }

    private void setCreatorDetails(DocumentReference userReference, ImageView creatorProfilePic, TextView creatorName) {
        if (userReference != null) {
            FirebaseDocument firebaseDocument = new FirebaseDocument() {
                @Override
                public void callbackOnSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        UserModel model = document.toObject(UserModel.class);
                        setCreatorProfileDetails(model.getProfile(), creatorProfilePic, creatorName);
                    }
                    else {
                        Log.w(TAG, "Document does not exist");
                    }
                }
            };
            firebaseDocument.run(userReference);

        } else {
            Log.d(TAG, "User Reference is Null");
        }

    }

    private void setCreatorProfileDetails(DocumentReference profileReference, ImageView image_name, TextView creatorName) {
        FirebaseDocument firebaseDocument = new FirebaseDocument() {
            @Override
            public void callbackOnSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    ProfileModel model = document.toObject(ProfileModel.class);
                    Utils.loadImage(model.getImagePath(), image_name);
                    creatorName.setText(model.getName());
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        };
        firebaseDocument.run(profileReference);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_event_button:
                if (edit_event_button.isEnabled()) {
                    backButton.setEnabled(false);
                    Intent editIntent = new Intent(ViewEventActivity.this, EditEventActivity.class);
                    editIntent.putExtra("DOCUMENT_ID", getDocumentFromPath(docRef.getPath()));
                    Toast.makeText(ViewEventActivity.this, "Editing Event", Toast.LENGTH_SHORT).show();
                    ViewEventActivity.this.startActivity(editIntent);
                }
                break;

            case R.id.join_button:
                if (join_button.isEnabled()) {
                    db.updateUserList(ViewEventActivity.this,documentName,user,true);
                }
                break;

                // case of leave button
            case R.id.leave_button:
                if (leave_button.isEnabled()) {
                    db.updateUserList(ViewEventActivity.this,documentName,user,false);
                }
                break;

            case R.id.backButton:
                if (backButton.isEnabled()) {
                    // Create explicit intent to go into MainPage
//                    Intent mainActivityIntent = new Intent(ViewEventActivity.this, MainPageActivity.class);
//                    startActivity(mainActivityIntent);
                    finish();
                }
                break;
        }
    }

    private void setUserJoinedRecyclerView() {
        /*
         * This functions set users joined in the recycler view layout using Firebase Recycler
         */

        Query query = db.getCollection("Profiles")
                .whereIn("userId", usersJoined);

        FirestoreRecyclerOptions<ProfileModel> options = new FirestoreRecyclerOptions.Builder<ProfileModel>()
                .setQuery(query, ProfileModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ProfileModel, ProfileViewHolder>(options) {
            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Creates a new instance of View Holder
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_events_user, parent, false);
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
                        Intent intent = new Intent(ViewEventActivity.this, ProfilePage.class);
                        intent.putExtra("PROFILE_ID", profileId);
                        ViewEventActivity.this.startActivity(intent);
                    }
                });
            }
        };
    }

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

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        startActivity(new Intent(ViewEventActivity.this, MainPageActivity.class));
    }
}