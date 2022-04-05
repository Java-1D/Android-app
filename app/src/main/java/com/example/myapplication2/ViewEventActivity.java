package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.objectmodel.EventModel;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.objectmodel.UserModel;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Document;

import java.sql.Array;
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
    ShapeableImageView person1;
    ShapeableImageView person2;
    ShapeableImageView person3;
    TextView name1;
    TextView name2;
    TextView name3;
    ImageView search1;
    ImageView search2;
    ImageView search3;
    MaterialButton join_button;

    // Recycler View
    private RecyclerView eventsList; // providing views that represent items in a data set.
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

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
        person1 = findViewById(R.id.person1); // Recycler View?
        person2 = findViewById(R.id.person2); // Recycler View?
        person3 = findViewById(R.id.person3); // Recycler View?
        name1 = findViewById(R.id.name1); // Recycler View?
        name2 = findViewById(R.id.name2); // Recycler View?
        name3 = findViewById(R.id.name3); // Recycler View?
        search1 = findViewById(R.id.search1);
        search2 = findViewById(R.id.search2);
        search3 = findViewById(R.id.search3);
        join_button = findViewById(R.id.join_button);

        join_button.setOnClickListener(this);
        search1.setOnClickListener(this);
        search2.setOnClickListener(this);
        search3.setOnClickListener(this);

        // TODO how to reflect document path based on the event that users click on the app -> documentPath -> SharedPreferences from HomePage
        DocumentReference docRef = db.collection("Events").document("Test Event");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                setModuleDetails(eventModel.getModule(),information);
                ModuleModel moduleModel = documentSnapshot.toObject(ModuleModel.class);
                event_name.setText(eventModel.getTitle());
                event_desc.setText(eventModel.getDescription());
                location.setText(eventModel.getVenue());
                start_time.setText("Start: " + eventModel.getEventStartTimeString());
                end_time.setText("End: " + eventModel.getEventEndTimeString());
                date.setText(eventModel.getEventStartDate());
                Utils.loadImage(eventModel.getImagePath(),location_pic);
                setCreatorDetails(eventModel.getUserCreated(),person,event_creator);

                // get users joined
                ArrayList<DocumentReference> usersJoined = eventModel.getUserJoined();
                information.setText(moduleModel.getName());


                // TODO Retrieve person who created the event -> decipher document reference
                // event_creator.setText(eventModel.getUserCreated()); -> Need to retrieve the person who created
                // TODO Retrieve (Document References) from firebase, using bitmap (Images: person, person1,2,3 & location_pic; Text: Information)
                // TODO Retrieve (Date) start and end time from firebase, display in textview (start_time, end time)

            }
        });
    }

            private void setModuleDetails(DocumentReference moduleReference, TextView text_name){
            moduleReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ModuleModel model = documentSnapshot.toObject(ModuleModel.class);
                    text_name.setText(model.getName());
                }
            });
        }

        private void setCreatorDetails(DocumentReference userReference, ImageView creatorProfilePic, TextView creatorName){
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserModel model = documentSnapshot.toObject(UserModel.class);
                    setCreatorProfileDetails(model.getProfile(),creatorProfilePic,creatorName);
                }
            });
        }

            private void setCreatorProfileDetails(DocumentReference profileReference, ImageView image_name,TextView creatorName){
                profileReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ProfileModel model = documentSnapshot.toObject(ProfileModel.class);
                    Log.d(TAG, "" + model);
                    Utils.loadImage(model.getImagePath(),image_name);
                    creatorName.setText(model.getName());


                }
            });
        }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_button:
                // TODO Check if event is full, if full reject join request.
                // Assume that we have the document id which was passed in from MainActivity
                DocumentReference docRef = db.collection("Events").document("Test Event");
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                        ArrayList<DocumentReference> usersJoined = eventModel.getUserJoined();
                        int current = usersJoined.size();
                        if (current == eventModel.getCapacity()){
                            Toast.makeText(ViewEventActivity.this, "The event is full! So sorry!", Toast.LENGTH_SHORT).show();

                        } ;
                        // TODO in else statement, add the user who clicked the join button into the UserJoined ArrayList

                        // Recognise my profile name

                        // Put my profile name into UserJoined array when I click Join
//                        DocumentReference user = LoggedInUser.getInstance().getUserDocRef(); // singleton
                        DocumentReference user = db.document("/Users/Test4");
                        // check if user is already in the list
                        if (usersJoined.contains(user)){
                            Toast.makeText(ViewEventActivity.this, "You have already joined the event", Toast.LENGTH_SHORT).show();
                        } else{
                            // append the array list of usersJoined with your Profile
                            usersJoined.add(user);
                            docRef.update("userJoined", FieldValue.arrayUnion(user));

                            // update the firebase with usersJoined
                            Toast.makeText(ViewEventActivity.this, "You have successfully joined the event", Toast.LENGTH_SHORT).show();


                        }


                    }
                });

                // TODO Check with Yongkang how to use recycler view to show the profiles of the users
            case R.id.search1:
                DocumentReference docRef1 = db.collection("Events").document("Test Event");
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                        // TODO Retrieve profile page of person1
                    }
                });
            case R.id.search2:
                DocumentReference docRef2 = db.collection("Events").document("Test Event");
                docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                        // TODO Retrieve profile page of person2
                    }
                });
            case R.id.search3:
                DocumentReference docRef3 = db.collection("Events").document("Test Event");
                docRef3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                        // TODO Retrieve profile page of person3
                    }
                });
        }
    }
}
