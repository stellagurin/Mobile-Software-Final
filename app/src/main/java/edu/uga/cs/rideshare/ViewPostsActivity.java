/**
 * This activity allows the user to view all offers and requests
 * posted by them. Users will also be able to update or delete their
 * posts.
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

public class ViewPostsActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "ViewPostsActivity";

    //used to display user's posts
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    private List<RideOffer> postsOfferList;
    private List<RideRequest> postsRequestList;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d( DEBUG_TAG, "ViewPostsActivity.onCreate()" );

        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_view_user_posts);

        //determines whether a user is a rider or driver
        Intent intent = getIntent();
        boolean isDriver = intent.getBooleanExtra("Type", false);

        recyclerView = (RecyclerView) findViewById( R.id.recyclerView );

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );

        //firebase authorization reference
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //database references to all ride offers and all ride requests
        DatabaseReference offersRef = database.getReference("allRideOffers");
        DatabaseReference requestsRef = database.getReference("allRideRequests");

        postsOfferList = new ArrayList<RideOffer>();
        postsRequestList = new ArrayList<RideRequest>();

        //reviewing all offer posts from user (driver)
        if(isDriver) {
            offersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postsOfferList.clear();
                   for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                       RideOffer offer = postSnapshot.getValue(RideOffer.class);

                       //only add user's offers
                       if(offer.getOfferUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                           postsOfferList.add(offer);
                       }
                   }

                    recyclerAdapter = new ViewOfferPostsRecyclerAdapter(postsOfferList);
                    recyclerView.setAdapter(recyclerAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }

        //reviewing all request posts from user (rider)
        else {
            requestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postsRequestList.clear();
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        RideRequest request = postSnapshot.getValue(RideRequest.class);

                        //only add user's requests
                        if(request.getRequestUser().equals(firebaseAuth.getCurrentUser().getEmail())) {
                            postsRequestList.add(request);
                        }
                    }

                    recyclerAdapter = new ViewRequestPostsRecyclerAdapter(postsRequestList);
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
