/**
 * This activity allows a user to update any unaccepted posts that
 * they have previously made. The user can find the update button
 * under the specific post that they want to update. Users must provide
 * at least one field. Posts are updated in real-time.
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "UpdateActivity";

    private EditText start; //starting location of ride
    private EditText end; //destination of ride
    private EditText dateOfRide;
    private EditText timeOfRide;
    private RideOffer rideOffer; //a ride offer
    private RideRequest rideRequest; //a ride request


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //changes depending on is user views as a rider or driver
        TextView prompt = findViewById(R.id.updateTextView);

        start = findViewById(R.id.editTextStart);
        end = findViewById(R.id.editTextEnd);
        dateOfRide = findViewById(R.id.editTextDate1);
        timeOfRide = findViewById(R.id.editTextTime1);

        //used to update a ride
        Button postButton = findViewById(R.id.updateButton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //determines whether a ride is an offer or request
        boolean isOffer = bundle.getBoolean("Type", false);


        //driver is updating offer
        if(isOffer) {
            prompt.setText("Update Offer");
            //get ride offer to be updated
            rideOffer = (RideOffer) bundle.getSerializable("Ride");
        }

        //rider is updating request
        else {
            prompt.setText("Update Request");
            //get ride request to be updated
            rideRequest = (RideRequest) bundle.getSerializable("Ride");
        }

        postButton.setOnClickListener(new UpdateActivity.UpdateButtonClickListener());

    }

    /**
     * When the user clicks the update button, the ride is updated in all places
     * where it can be found, including in the database.
     */
    private class UpdateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //firebase database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //database reference to all users
            DatabaseReference userRef = database.getReference("users");

            //database reference of all ride offers
            DatabaseReference rideOffersRef = database.getReference("allRideOffers");

            //database reference of all ride requests
            DatabaseReference rideRequestsRef = database.getReference("allRideRequests");

            Intent intent = getIntent();
            //determines whether the user is viewing as a rider or driver
            boolean isDriver = intent.getBooleanExtra("Type", false);

            //driver is posting offer
            if(isDriver) {

                //save offer information in variables
                String startLocation = start.getText().toString();
                String destination = end.getText().toString();
                String date = dateOfRide.getText().toString();
                String time = timeOfRide.getText().toString();

                //checking which fields are being updated (not empty)
                if(!startLocation.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey() + "/start").setValue(startLocation);
                    //update all rideOffers
                    rideOffersRef.child(rideOffer.getOfferKey() + "/start").setValue(startLocation);
                }

                if(!destination.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey() + "/end").setValue(destination);
                    //update all rideOffers
                    rideOffersRef.child(rideOffer.getOfferKey() + "/end").setValue(destination);
                }

                if(!date.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey() + "/date").setValue(date);
                    //update all rideOffers
                    rideOffersRef.child(rideOffer.getOfferKey() + "/date").setValue(date);
                }

                if(!time.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey() + "/time").setValue(time);
                    //update all rideOffers
                    rideOffersRef.child(rideOffer.getOfferKey() + "/time").setValue(time);
                }

                //success message
                Toast.makeText(getApplicationContext(),
                        "Offer Updated",
                        Toast.LENGTH_SHORT).show();
                //edit text fields reset for next entry
                start.setText("");
                end.setText("");
                dateOfRide.setText("");
                timeOfRide.setText("");
            }

            //rider is posting request
            else {
                //save offer information in variables
                String startLocation = start.getText().toString();
                String destination = end.getText().toString();
                String date = dateOfRide.getText().toString();
                String time = timeOfRide.getText().toString();

                //checking which fields are being updated (not empty)
                if (!startLocation.isEmpty()) {
                    //update user's rideOffers
                    //update user's rideOffers
                    userRef.child(rideRequest.getRequestKey() + "/rideRequests/" + rideRequest.getRequestKey() + "/start").setValue(start);
                    //update all rideOffers
                    rideRequestsRef.child(rideRequest.getRequestKey() + "/start").setValue(start);
                }

                if (!destination.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideRequest.getRequestKey() + "/rideRequests/" + rideRequest.getRequestKey() + "/end").setValue(destination);
                    //update all rideOffers
                    rideRequestsRef.child(rideRequest.getRequestKey() + "/end").setValue(destination);
                }

                if (!date.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideRequest.getRequestKey() + "/rideRequests/" + rideRequest.getRequestKey() + "/date").setValue(date);
                    //update all rideOffers
                    rideRequestsRef.child(rideRequest.getRequestKey() + "/date").setValue(date);
                }

                if (!time.isEmpty()) {
                    //update user's rideOffers
                    userRef.child(rideRequest.getRequestKey() + "/rideRequests/" + rideRequest.getRequestKey() + "/time").setValue(time);
                    //update all rideOffers
                    rideRequestsRef.child(rideRequest.getRequestKey() + "/time").setValue(time);
                }

                //success message
                Toast.makeText(getApplicationContext(),
                        "Request Updated",
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
