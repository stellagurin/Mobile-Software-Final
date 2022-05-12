/**
 * This activity is responsible for allowing the user to confirm any offers
 * or requests that have been accepted by other users. The user should confirm
 * the rides after they have happened. Once the rides are confirmed, both users'
 * points will be updated accordingly. A RecyclerView is used to display all rides
 * that require confirmation to the user.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConfirmAcceptedActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "ConfirmAcceptedActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RideOffer> acceptedOffersList;
    private List<RideRequest> acceptedRequestsList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d(DEBUG_TAG, "ConfirmAcceptedActivity.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_accepted);


        Intent intent = getIntent();

        /*
         * Tells us whether the user is viewing as a driver or rider.
         * isDriver = true (Driver), isDriver = false (Rider)
         */
        boolean isDriver = intent.getBooleanExtra("Type", false);

        //used to display rides needing confirmation
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView4);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //firebase authorization instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //firebase database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //references to all accepted rides (accepted by users)
        DatabaseReference offersRef = database.getReference("allAcceptedRideOffers");
        DatabaseReference requestsRef = database.getReference("allAcceptedRideRequests");

        acceptedOffersList = new ArrayList<RideOffer>();
        acceptedRequestsList = new ArrayList<RideRequest>();

        /*
         * If the user is viewing as driver, then screen will display all
         * accepted ride offers that require confirmation from the user
         */
        if(isDriver) {
            offersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    acceptedOffersList.clear();
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideOffer offer = postSnapshot.getValue(RideOffer.class);
                        if(offer.getOfferUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            acceptedOffersList.add(offer);
                        }
                    }

                    recyclerAdapter = new ConfirmAcceptedOffersRecyclerAdapter(acceptedOffersList);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }

        /*
         * If the user is viewing as rider, then screen will display all
         * accepted ride requests that require confirmation from the user
         */
        else {
            requestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    acceptedRequestsList.clear();
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideRequest request = postSnapshot.getValue(RideRequest.class);
                        if(request.getRequestUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            acceptedRequestsList.add(request);
                        }
                    }

                    recyclerAdapter = new ConfirmAcceptedRequestsRecyclerAdapter(acceptedRequestsList);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }
    }
}