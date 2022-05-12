/**
 * This activity allows the user to view all rides and offers
 * accepted by them.
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

public class ViewAcceptedActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "ViewAcceptedActivity";

    //used to display all accepted rides
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RideOffer> acceptedOffersList;
    private List<RideRequest> acceptedRequestsList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d( DEBUG_TAG, "ViewAcceptedActivity.onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_view_accepted);

        //determines if a user is viewing as a rider or driver
        Intent intent = getIntent();
        boolean isDriver = intent.getBooleanExtra("Type", false);

        recyclerView = (RecyclerView) findViewById( R.id.recyclerView3 );

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );

        //firebase authentication instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //database reference of all offers that have been accepted
        DatabaseReference acceptedOffersRef = database.getReference("allAcceptedRideOffers");

        //database reference of all requests that have been accepted
        DatabaseReference acceptedRequestsRef = database.getReference("allAcceptedRideRequests");

        acceptedOffersList = new ArrayList<RideOffer>();
        acceptedRequestsList = new ArrayList<RideRequest>();

        //reviewing accepted offer posts (rider)
        if(!isDriver) {
            acceptedOffersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideOffer acceptedOffer = postSnapshot.getValue(RideOffer.class);
                        if(acceptedOffer.getAcceptedUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            //creates a list of user's accepted offers
                            acceptedOffersList.add(acceptedOffer);
                        }
                    }

                    recyclerAdapter = new ViewAcceptedOffersRecyclerAdapter(acceptedOffersList);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }

        //reviewing all accepted requests from user (driver)
        else {
            acceptedRequestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        RideRequest acceptedRequest = postSnapshot.getValue(RideRequest.class);
                        if (acceptedRequest.getAcceptedUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            acceptedRequestsList.add(acceptedRequest);
                        }
                    }

                    //creates a list of user's accepted requests
                    recyclerAdapter = new ViewAcceptedRequestsRecyclerAdapter(acceptedRequestsList);
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
