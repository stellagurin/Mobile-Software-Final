/**
 * This class allows the user to post an offer as a driver or
 * a request as a rider. Users must provide all fields for a ride
 * including starting location, destination, date, and time. All rides will
 * cost 50 points for riders and will give 50 points to drivers once a ride
 * is complete.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "PostActivity";

    private EditText start; //starting location of ride
    private EditText end; //ride destination
    private EditText dateOfRide;
    private EditText timeOfRide;
    private int cost; //point cost of ride
    private String offerUser; //user posting an offer
    private String requestUser; //user posting a request
    private String acceptedUser;
    private boolean confirmedByUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //prompts the user to enter an offer or request
        TextView prompt = findViewById(R.id.textView8);

        start = findViewById(R.id.editText);
        end = findViewById(R.id.editText2);
        dateOfRide = findViewById(R.id.editTextDate);
        timeOfRide = findViewById(R.id.editTextTime);

        //used to post a request
        Button postButton = findViewById(R.id.button16);

        //determines whether a user is viewing as a driver or rider
        Intent intent = getIntent();
        boolean isDriver = intent.getBooleanExtra("Type", false);

        //driver is posting offer
        if(isDriver) {
            prompt.setText("Enter Offer");
        }

        //rider is posting request
        else {
            prompt.setText("Enter Request");
        }

        postButton.setOnClickListener(new PostActivity.PostButtonClickListener());

    }

    /**
     * Used to post a ride request or offer depending on the user's view
     */
    private class PostButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //firebase authorization instance
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //firebase database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //reference to all users in database
            DatabaseReference userRef = database.getReference("users");

            //reference to all ride offers (by all users)
            DatabaseReference rideOffersRef = database.getReference("allRideOffers");

            //reference to all ride requests (by all users)
            DatabaseReference rideRequestsRef = database.getReference("allRideRequests");

            //determines whether a user is a driver or rider
            Intent intent = getIntent();
            boolean isDriver = intent.getBooleanExtra("Type", false);

            //driver is posting offer
            if(isDriver) {

                //save offer information in variables
                String startLocation = start.getText().toString();
                String destination = end.getText().toString();
                String date = dateOfRide.getText().toString();
                String time = timeOfRide.getText().toString();

                //checking if any fields are empty
                if(startLocation.isEmpty()) {
                    start.setError("Starting location is required");
                    return;
                }

                if(destination.isEmpty()) {
                    end.setError("Destination is required");
                    return;
                }

                if(date.isEmpty()) {
                    dateOfRide.setError("Date of ride is required");
                    return;
                }

                if(time.isEmpty()) {
                    timeOfRide.setError("Starting location is required");
                    return;
                }

                cost = 50; //default for now
                offerUser = firebaseAuth.getCurrentUser().getEmail(); //user who is posting offer
                acceptedUser = null; //user who accepts the offer
                confirmedByUser = false; //determines whether user has confirmed ride has happened

                //create ride offer object for all ride offers
                String key = userRef.push().getKey();
                RideOffer offer = new RideOffer(key, startLocation, destination, date, time, cost, offerUser, null, null, null, false);

                //add everything to database
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            if(user.getUserEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                                offer.setOfferUserKey(user.getUserId());
                                userRef.child(user.getUserId() + "/rideOffers/" + key).setValue(offer);
                                rideOffersRef.child(key).setValue(offer);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(DEBUG_TAG, error.getMessage());
                    }
                });

                //success message
                Toast.makeText(getApplicationContext(),
                        "Offer Posted",
                        Toast.LENGTH_SHORT).show();
                //edit text fields reset for next entry
                start.setText("");
                end.setText("");
                dateOfRide.setText("");
                timeOfRide.setText("");
            }

            //rider is posting request
            else {
                //save request information in variables
                String startLocation = start.getText().toString();
                String destination = end.getText().toString();
                String date = dateOfRide.getText().toString();
                String time = timeOfRide.getText().toString();

                //checking if any fields are empty
                if(startLocation.isEmpty()) {
                    start.setError("Starting location is required");
                    return;
                }

                if(destination.isEmpty()) {
                    end.setError("Destination is required");
                    return;
                }

                if(date.isEmpty()) {
                    dateOfRide.setError("Date of ride is required");
                    return;
                }

                if(time.isEmpty()) {
                    timeOfRide.setError("Starting location is required");
                    return;
                }

                cost = 50; //default for now
                requestUser = firebaseAuth.getCurrentUser().getEmail();
                acceptedUser = null; //user who accepts the request
                confirmedByUser = false; //determines whether user has confirmed ride has happened

                //create ride request object for all ride requests
                String key = userRef.push().getKey();
                RideRequest request = new RideRequest(key, startLocation, destination, date, time, cost, requestUser, null, null, null, false);

                //add everything to database
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            if(user.getUserEmail().equals(firebaseAuth.getCurrentUser().getEmail())) {
                                request.setRequestUserKey(user.getUserId());
                                userRef.child(user.getUserId() + "/rideRequests/" + key).setValue(request);
                                rideRequestsRef.child(key).setValue(request);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(DEBUG_TAG, error.getMessage());
                    }
                });

                //success message
                Toast.makeText(getApplicationContext(),
                        "Request Posted",
                        Toast.LENGTH_SHORT).show();
                //edit text fields reset for next entry
                start.setText("");
                end.setText("");
                dateOfRide.setText("");
                timeOfRide.setText("");

            }

        }
    }

}
