
package com.example.myapplication2.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.EditProfilePage;
import com.example.myapplication2.LoginActivity;
import com.example.myapplication2.MainPageActivity;
import com.example.myapplication2.ProfilePage;
import com.example.myapplication2.R;
import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.objectmodel.ProfileModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.ProfileViewModel;
import com.example.myapplication2.viewholder.RecyclerContactAdapter;
import com.example.myapplication2.viewholder.RecyclerViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    String profileDocumentId;
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());
    final FirebaseContainer<ModuleModel> module = new FirebaseContainer<>(new ModuleModel());

    // Data fields present in UI elements
    enum Data {
        PROFILE_PIC,
        NAME,
        PILLAR,
        TERM,
        MODULE,
        BIO
    }

    // View UI elements
    ImageView profilePicture;
    TextView profileName;
    TextView pillarValue;
    TextView termValue;
    TextView bioText;

    // Interactive UI elements
    ImageView backArrow;
    Button logOutButton;
    Button editButton;
    RecyclerView recyclerView;

    // RecyclerView components
    RecyclerContactAdapter adapter;
    ArrayList<RecyclerViewModel> arrModules = new ArrayList<>();

    // Shared Preferences to store Objects as a String
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEditor;

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backArrow:
                    startActivity((new Intent(getActivity(), MainPageActivity.class)));
                    break;
                case R.id.logOutButton:
                    startActivity((new Intent(getActivity(), LoginActivity.class)));
                    break;
                case R.id.editButton:
                    Intent intent = new Intent(getActivity(), EditProfilePage.class);
                    startActivity(intent);
                    break;
                default:
                    Log.w(TAG, "Button not Found");
            }
        }
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        profileDocumentId = "Test";
        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        getProfileData(profileRef);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // FIXME find a way to extract modules from Firestore DocumentReference
        arrModules.add(new RecyclerViewModel(R.drawable.iot, "CSD", "IoT and all other stuff"));
        arrModules.add(new RecyclerViewModel(R.drawable.data_analytics, "CSD", "Data and all dat shit"));
        arrModules.add(new RecyclerViewModel(R.drawable.fin, "CSD", "Financial tech and all shit"));

        backArrow = view.findViewById(R.id.backArrow);
        logOutButton = view.findViewById(R.id.logOutButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        profileName = view.findViewById(R.id.profileName);
        pillarValue = view.findViewById(R.id.pillarValue);
        termValue = view.findViewById(R.id.termValue);
        bioText = view.findViewById(R.id.bioText);
        editButton = view.findViewById(R.id.editButton);

        // initialise RecyclerView elements for Modules Section
        recyclerView = view.findViewById(R.id.recyclerProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerContactAdapter(getContext(), arrModules);
        recyclerView.setAdapter(adapter);

        // initialise buttons
        backArrow.setOnClickListener(new ClickListener());
        logOutButton.setOnClickListener(new ClickListener());
        editButton.setOnClickListener(new ClickListener());

        return view;
    }

    // Set UI Elements using data from Firebase
    public void setUIElements(ProfileModel profile) {
        // Set Text
        profileName.setText(profile.getName());
        pillarValue.setText(profile.getPillar());
        termValue.setText(String.valueOf(profile.getTerm()));
        bioText.setText(profile.getBio());

        // Set Image
        setImage(profile.getImagePath());
    }

    // Set Image for ImageView
    public void setImage(String imageURL) {
        Picasso.get().load(imageURL).resize(120, 120).centerCrop().transform(new Utils.CircleTransform())
                .into(profilePicture);
        Log.i(TAG, "Profile Picture set");
    }

    // Firebase-Specific Methods
    public DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }

    // Get Data from Profiles Collection
    public void getProfileData(DocumentReference profileRef) {
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Log.i(TAG, "File Path in Firebase: " + profileRef.getPath());
                    Log.i(ProfileModel.TAG, "Contents of Firestore Document: "+ Objects.requireNonNull(document.toObject(ProfileModel.class)));
                    ProfileFragment.this.profile.set(document.toObject(ProfileModel.class));
                    ProfileFragment.this.setUIElements(ProfileFragment.this.profile.get());
                    addModuleToRecyclerView();
                }
                else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error retrieving document from Firestore", e);
            }
        });
    }

    //Add Module Data Retrieved
    public void addModuleToRecyclerView() {
        for (DocumentReference moduleRef: profile.get().getModules()) {
            moduleRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if (document.exists()) {
                        Log.i(TAG, "File Path in Firebase: " + moduleRef.getPath());
                        Log.i(ModuleModel.TAG, "Contents of Firestore Document: "+ Objects.requireNonNull(document.toObject(ModuleModel.class)));
                        ProfileFragment.this.module.set(document.toObject(ModuleModel.class));

                        //Add modules from Firestore DocumentReference to Recycler View
                        arrModules.add(new ProfileViewModel(ProfileFragment.this.module.get()));
                        ProfileFragment.this.adapter.notifyItemInserted(arrModules.size());
                    }
                    else {
                        Log.w(TAG, "Document does not exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error retrieving document from Firestore", e);
                }
            });
        }
    }
}

