/**
 * This is the recycler adapter for accepted ride offers
 * that require confirmation from the user.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ConfirmAcceptedOffersRecyclerAdapter extends RecyclerView.Adapter<ConfirmAcceptedOffersRecyclerAdapter.ConfirmAcceptedOffersHolder> {
    public static final String DEBUG_TAG = "ConfirmAcceptedOffersRecyclerAdapter";

    private List<RideOffer> rideOfferList;
    private RideOffer rideOffer;

    public ConfirmAcceptedOffersRecyclerAdapter( List<RideOffer> rideOfferList)
    {
        this.rideOfferList = rideOfferList;
    }

    class ConfirmAcceptedOffersHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView cost; //point cost of ride
        TextView acceptingUser; //user that accepted the ride
        Button confirmOfferButton; //button to confirm offer

        public ConfirmAcceptedOffersHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.confirmOfferStart);
            end = (TextView) itemView.findViewById(R.id.confirmOfferEnd);
            date = (TextView) itemView.findViewById(R.id.confirmOfferDate);
            time = (TextView) itemView.findViewById(R.id.confirmOfferTime);
            cost = (TextView) itemView.findViewById(R.id.confirmOfferCost);
            acceptingUser = (TextView) itemView.findViewById(R.id.confirmOfferAccepter);
            confirmOfferButton = (Button) itemView.findViewById(R.id.button18);

            confirmOfferButton.setOnClickListener(new ConfirmAcceptedOffersRecyclerAdapter.ConfirmOfferClickListener());
        }
    }

    @Override
    public ConfirmAcceptedOffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_offer, parent, false);
        return new ConfirmAcceptedOffersHolder(view);
    }

    @Override
    public void onBindViewHolder( ConfirmAcceptedOffersHolder holder, int position) {
        rideOffer = rideOfferList.get(position);

        holder.start.setText("Start Location: " + rideOffer.getStart());
        holder.end.setText("Destination: " + rideOffer.getEnd());
        holder.date.setText("Date: " + rideOffer.getDate());
        holder.time.setText("Time: " + rideOffer.getTime());
        holder.cost.setText("Points Cost: " + rideOffer.getCost());
        holder.acceptingUser.setText("Accepted By: " + rideOffer.getAcceptedUser());
    }

    private class ConfirmOfferClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //Firebase Authorization reference
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //Firebase database reference
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //references to all users and all accepted ride offers (by all users)
            DatabaseReference userRef = database.getReference("users");
            DatabaseReference acceptedOffersRef = database.getReference("allAcceptedRideOffers");


            //remove ride from allAcceptedRideOffers
            acceptedOffersRef.child(rideOffer.getOfferKey()).removeValue();
            rideOfferList.remove(rideOffer);

            //remove ride from rideOffersToConfirm from user that posted
            userRef.child(rideOffer.getOfferUserKey() + "/rideOffersToConfirm/" + rideOffer.getOfferKey()).removeValue();

            //remove ride from acceptedRideOffers from user that accepted
            userRef.child(rideOffer.getAcceptedUserKey() + "/acceptedRideOffers/" + rideOffer.getOfferKey()).removeValue();


            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        User user = postSnapshot.getValue(User.class);
                        //user that offered
                        if((user.getUserEmail()).equals(rideOffer.getOfferUser())) {
                            //add points to user that offered
                            int currentPoints = user.getPoints();
                            int ridePoints = rideOffer.getCost();
                            int newPointBalance = currentPoints + ridePoints;
                            userRef.child(user.getUserId() + "/points").setValue(newPointBalance);
                        }

                        //user that accepted
                        else if(user.getUserEmail().equals(rideOffer.getAcceptedUser())) {
                            //deduct points from user that accepted
                            int currentPoints = user.getPoints();
                            int ridePoints = rideOffer.getCost();;
                            int newPointBalance = currentPoints - ridePoints;
                            userRef.child(user.getUserId() + "/points").setValue(newPointBalance);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() { return rideOfferList.size(); }

}
