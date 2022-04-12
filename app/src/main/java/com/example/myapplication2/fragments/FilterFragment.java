
package com.example.myapplication2.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.myapplication2.MainPageActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.utils.Container;
import com.example.myapplication2.utils.FirebaseContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Fragment Class for the FilterActivity
 */
public class FilterFragment extends Fragment {


    private static final String TAG = "FilterFragment";
    ArrayList<String> moduleItems = new ArrayList<String>();
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    FirebaseFirestore firebaseFirestore;
    Button filterButton;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter_page, container, false);
        autoCompleteTxt = view.findViewById(R.id.autoCompleteTxt);

        // Filter Button to go to View All Events after filtering
        filterButton = view.findViewById(R.id.FilterButton);

        autoCompleteTxt.setAdapter(adapterItems); // TODO : Debug null pointer exception here upon going back to the activity a second time

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String module = adapterView.getItemAtPosition(i).toString();
            }
        });

        // Bring users to View Event when clicking on viewEventButton
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Intent intent = new Intent(getActivity(), HomeFragment.class);
                startActivity(intent);
            }
        });


        return inflater.inflate(R.layout.activity_filter_page, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseContainer container = getModules(firebaseFirestore, moduleItems); // populate array list with modules
        Log.d(TAG, "" + container.get());
        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_item, moduleItems); //TODO : Adapter Item becomes null after revisiting it for the second time
        Log.d(TAG, moduleItems + "");

    }


    FirebaseContainer getModules(FirebaseFirestore firebaseFirestore, ArrayList<String> modules) {
        final FirebaseContainer<ArrayList<String>> container = new FirebaseContainer<>();

        firebaseFirestore.collection("Modules")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getData().toString());
                                modules.add(document.getData().get("name").toString());
                            }
                        } else {
                        }
                        container.set(modules);
                        Log.d(TAG, "Container Object" + container.get().toString());
                    }

                });
        return container;
    }
}