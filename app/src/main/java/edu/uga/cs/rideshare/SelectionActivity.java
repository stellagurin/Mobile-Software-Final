/**
 * This activity provides the user with many options on the rider/driver screen.
 * Options include: Post request/offer, View posted requests/offers, View all requests/offers,
 * View requests/offers accepted by the user, Confirm an accepted ride has happened, and switch
 * between rider and driver view. This screen will also display the user's email and total number
 * of points.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectionActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "SelectionActivity";

    //firebase authorization instance
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private TextView welcomeText; //changes depending on if user is driver or rider
    private TextView userText; //displays user's email
    private TextView points; //display user's total points

    private Button postButton; //post a request/offer
    private Button viewPostsButton; //view requests/offers posted by user
    private Button viewAllButton; //view all requests/offers
    private Button viewAcceptedButton; //view user's accepted requests/offers
    private Button confirmRidesButton; //confirm accepted rides (rides that already happened)
    private Button switchButton; //switch between rider and driver view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        welcomeText = findViewById(R.id.textView7);
        userText = findViewById(R.id.textView6);
        points = findViewById(R.id.textViewPoints);

        postButton = findViewById(R.id.button10);
        viewPostsButton = findViewById(R.id.button11);
        viewAllButton = findViewById(R.id.button12);
        viewAcceptedButton = findViewById(R.id.button13);
        confirmRidesButton = findViewById(R.id.button14);
        switchButton = findViewById(R.id.button15);

        //determines whether a user is viewing as a driver or rider
        Intent intent = getIntent();
        boolean isDriver = intent.getBooleanExtra("Type", false);

        //driver's view
        if(isDriver) {
            welcomeText.setText("Welcome Driver!");
            userText.setText(firebaseAuth.getCurrentUser().getEmail());
            postButton.setText("Post Offer");
            viewPostsButton.setText("View Posted Offers");
            viewAllButton.setText("View All Requests");
            viewAcceptedButton.setText("View Accepted Requests");
            confirmRidesButton.setText("Confirm Accepted Offers"); //confirm driver's offers that have been accepted by riders
            switchButton.setText("Switch to Rider");
        }

        //rider's view
        else {
            welcomeText.setText("Welcome Rider!");
            userText.setText(firebaseAuth.getCurrentUser().getEmail());
            postButton.setText("Post Request");
            viewPostsButton.setText("View Posted Requests");
            viewAllButton.setText("View All Offers");
            viewAcceptedButton.setText("View Accepted Offers");
            confirmRidesButton.setText("Confirm Accepted Requests"); //confirm rider's requests that have been accepted by drivers
            switchButton.setText("Switch to Driver");
        }

        //firebase database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //database reference to all users
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                    User user = postSnapshot.getValue(User.class);
                    if(user.getUserEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                        //displaying user points
                        points.setText("Ride Points: " + user.getPoints());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }
        });

        postButton.setOnClickListener(new SelectionActivity.PostClickListener());
        switchButton.setOnClickListener(new SelectionActivity.SwitchClickListener());
        viewPostsButton.setOnClickListener(new SelectionActivity.ViewPostsClickListener());
        viewAllButton.setOnClickListener(new SelectionActivity.ViewAllClickListener());
        viewAcceptedButton.setOnClickListener(new SelectionActivity.ViewAcceptedClickListener());
        confirmRidesButton.setOnClickListener(new SelectionActivity.ConfirmAcceptedClickListener());

    }

    /**
     * Allows the user to post a ride request or offer depending on view
     */
    private class PostClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            final boolean isDriver = intent.getBooleanExtra("Type", false);

            intent = new Intent(view.getContext(), PostActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Allows the user to switch between driver and rider view
     */
    private class SwitchClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            boolean isDriver = intent.getBooleanExtra("Type", false);

            //driver is switching to rider
            if(isDriver) {
                isDriver = false;
                intent.putExtra("Type", isDriver);
                welcomeText.setText("Welcome Rider!");
                userText.setText(firebaseAuth.getCurrentUser().getEmail());
                postButton.setText("Post Request");
                viewPostsButton.setText("View Posted Requests");
                viewAllButton.setText("View All Offers");
                viewAcceptedButton.setText("View Accepted Offers");
                confirmRidesButton.setText("Confirm Accepted Requests"); //confirm rider's requests that have been accepted by drivers
                switchButton.setText("Switch to Driver");
            }

            //rider is switching to driver
            else {
                isDriver = true;
                intent.putExtra("Type", isDriver);
                welcomeText.setText("Welcome Driver!");
                userText.setText(firebaseAuth.getCurrentUser().getEmail());
                postButton.setText("Post Offer");
                viewPostsButton.setText("View Posted Offers");
                viewAllButton.setText("View All Requests");
                viewAcceptedButton.setText("View Accepted Requests");
                confirmRidesButton.setText("Confirm Accepted Offers"); //confirm driver's offers that have been accepted by riders
                switchButton.setText("Switch to Rider");
            }
        }
    }

    /**
     * Allows the user to view their posts as a rider or driver
     */
    private class ViewPostsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            final boolean isDriver = intent.getBooleanExtra("Type", false);

            intent = new Intent(view.getContext(), ViewPostsActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Allows user to view all posts as a rider or driver
     */
    private class ViewAllClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            final boolean isDriver = intent.getBooleanExtra("Type", false);

            intent = new Intent(view.getContext(), ViewAllActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Allows user to view rides that they have accepted as a driver or rider
     */
    private class ViewAcceptedClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            final boolean isDriver = intent.getBooleanExtra("Type", false);

            intent = new Intent(view.getContext(), ViewAcceptedActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Allows user to confirm accepted rides that have already happened as a rider
     * or driver.
     */
    private class ConfirmAcceptedClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            final boolean isDriver = intent.getBooleanExtra("Type", false);

            intent = new Intent(view.getContext(), ConfirmAcceptedActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }
}
