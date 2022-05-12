/**
 * This activity allows the user to view all offers and requests
 * posted by other users. The user will also be able to accept any
 * offers/requests on this list.
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

public class ViewAllActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "ViewAllActivity";

    //used to display all offers/requests
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RideOffer> allOffersList;
    private List<RideRequest> allRequestsList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d(DEBUG_TAG, "ViewAllActivity.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_posts);

        //determines whether a user is a rider or driver
        Intent intent = getIntent();
        boolean isDriver = intent.getBooleanExtra("Type", false);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //firebase authorization instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //database references to all ride offers and all ride requests
        DatabaseReference offersRef = database.getReference("allRideOffers");
        DatabaseReference requestsRef = database.getReference("allRideRequests");

        allOffersList = new ArrayList<RideOffer>();
        allRequestsList = new ArrayList<RideRequest>();

        //reviewing all offer posts (rider)
        if(!isDriver) {
            offersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    allOffersList.clear();
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideOffer offer = postSnapshot.getValue(RideOffer.class);

                        //add all offer's except for the user's
                        if(!offer.getOfferUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            allOffersList.add(offer);
                        }
                    }

                    recyclerAdapter = new ViewAllOffersRecyclerAdapter(allOffersList);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }

        //reviewing all offer requests (driver)
        else {
            requestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    allRequestsList.clear();
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideRequest request = postSnapshot.getValue(RideRequest.class);

                        //add all requests except for user's
                        if(!request.getRequestUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            allRequestsList.add(request);
                        }
                    }

                    recyclerAdapter = new ViewAllRequestsRecyclerAdapter(allRequestsList);
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
