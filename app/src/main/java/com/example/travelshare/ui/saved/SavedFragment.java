package com.example.travelshare.ui.saved;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelshare.ItineraryActivity;
import com.example.travelshare.adapter.recycler.ItineraryAdapter;
import com.example.travelshare.R;
import com.example.travelshare.library.Constant;
import com.example.travelshare.library.SingletonMap;
import com.example.travelshare.data.model.Itinerary;
import com.example.travelshare.ui.login.LoginActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class SavedFragment extends Fragment {

    View root;
    String userId;
    FirebaseFirestore db;
    RecyclerView recyclerViewItinerary;
    ItineraryAdapter mItineraryAdapter;
    private FirebaseUser signInAccount;
    TabLayout tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerViewItinerary = root.findViewById(R.id.savedItineraries);
        recyclerViewItinerary.setLayoutManager(new LinearLayoutManager(root.getContext()));

        db=FirebaseFirestore.getInstance();
        //signInAccount = FirebaseAuth.getInstance().getCurrentUser();
        signInAccount = (FirebaseUser) SingletonMap.getInstance().get(Constant.CURRENT_USER);
        if(signInAccount!= null){
            userId = ((DocumentSnapshot) SingletonMap.getInstance().get(Constant.CURRENT_USER_ID)).getId();
            this.initializeItinerary();
        }else{
            Intent intent = new Intent(root.getContext(), LoginActivity.class);
            startActivity(intent);
        }
        tabs = root.findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: this.onClickMine();
                        break;
                    case 1: this.onClickOther();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
            public void onClickMine(){
                Query query= db.collection("users").document(userId).collection("itineraries").whereEqualTo("published", false);
                this.initializeItinerary(query);
            }

            public void onClickOther(){
                Query query= db.collection("users").document(userId).collection("saved");
                this.initializeItinerary(query);
            }
            private void initializeItinerary(Query query) {
                FirestoreRecyclerOptions<Itinerary> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Itinerary>()
                        .setQuery(query, Itinerary.class).build();
                mItineraryAdapter.updateOptions(firestoreRecyclerOptions);
            }

        });

        return root;
    }

    private void initializeItinerary() {
        Query query= db.collection("users").document(userId).collection("itineraries").whereEqualTo("published", false);
        FirestoreRecyclerOptions<Itinerary> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Itinerary>()
                .setQuery(query, Itinerary.class).build();
        mItineraryAdapter = new ItineraryAdapter(firestoreRecyclerOptions, root.getContext());
        mItineraryAdapter.notifyDataSetChanged();
        recyclerViewItinerary.setAdapter(mItineraryAdapter);
        mItineraryAdapter.startListening();
        mItineraryAdapter.setOnItemClickListener(new ItineraryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                SingletonMap.getInstance().put("ItineraryDocument", documentSnapshot);
                SingletonMap.getInstance().put("Saved", false);
                Intent intent= new Intent(root.getContext(), ItineraryActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }






}