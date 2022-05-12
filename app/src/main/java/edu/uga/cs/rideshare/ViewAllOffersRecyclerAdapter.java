/**
 * This is the recycler adapter for viewing all ride offers (except the current user's)
 * A button to accept rides is also provided under each post.
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

public class ViewAllOffersRecyclerAdapter extends RecyclerView.Adapter<ViewAllOffersRecyclerAdapter.ViewAllOffersHolder> {
    public static final String DEBUG_TAG = "ViewAllOffersRecyclerAdapter";

    private List<RideOffer> rideOfferList;
    private RideOffer rideOffer;

    public ViewAllOffersRecyclerAdapter( List<RideOffer> rideOfferList)
    {
        this.rideOfferList = rideOfferList;
    }

    class ViewAllOffersHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView cost; //point cost of ride
        TextView offeringUser; //user that posted the offer
        Button acceptOfferButton; //used to accept an offer

        public ViewAllOffersHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.allOfferStart);
            end = (TextView) itemView.findViewById(R.id.allOfferEnd);
            date = (TextView) itemView.findViewById(R.id.allOfferDate);
            time = (TextView) itemView.findViewById(R.id.allOfferTime);
            cost = (TextView) itemView.findViewById(R.id.allOfferCost);
            offeringUser = (TextView) itemView.findViewById(R.id.allOfferUser);
            acceptOfferButton = (Button) itemView.findViewById(R.id.button17);

            acceptOfferButton.setOnClickListener(new ViewAllOffersRecyclerAdapter.AcceptOfferClickListener());
        }
    }

    @Override
    public ViewAllOffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_offers, parent, false);
        return new ViewAllOffersHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewAllOffersHolder holder, int position) {
        rideOffer = rideOfferList.get(position);

        holder.start.setText("Start Location: " + rideOffer.getStart());
        holder.end.setText("Destination: " + rideOffer.getEnd());
        holder.date.setText("Date: " + rideOffer.getDate());
        holder.time.setText("Time: " + rideOffer.getTime());
        holder.cost.setText("Points Cost: " + rideOffer.getCost());
        holder.offeringUser.setText("Posted By: " + rideOffer.getOfferUser());
    }

    /**
     * Used to accept a ride offer. Once accepted, ride is moved to user's accepted
     * offers list.
     */
    private class AcceptOfferClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //firebase authorization instance
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //database reference to all users
            DatabaseReference userRef = database.getReference("users");

            //database references to all ride requests and offers
            DatabaseReference rideOffersRef = database.getReference("allRideOffers");
            DatabaseReference acceptedOffersRef = database.getReference("allAcceptedRideOffers");

            //Ride removed from overall offer/request list
            rideOffersRef.child(rideOffer.getOfferKey()).removeValue();
            rideOfferList.remove(rideOffer);

            //Ride removed from offering/requesting user’s list
            userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey()).removeValue();

            //Set accepting user email and id to database child
            rideOffer.setAcceptedUser(firebaseAuth.getCurrentUser().getEmail());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                         User user = postSnapshot.getValue(User.class);
                        if((user.getUserEmail()).equals(firebaseAuth.getCurrentUser().getEmail())) {
                            //set the offer's accepted user key to the current user's key
                            rideOffer.setAcceptedUserKey(user.getUserId());

                            //Added to offering user's confirmation list with accepting user’s email
                            String key = userRef.push().getKey();
                            rideOffer.setOfferKey(key);
                            userRef.child(rideOffer.getOfferUserKey() + "/rideOffersToConfirm/" + key).setValue(rideOffer);

                            //Added to accepting user’s accepted ride requests
                            userRef.child(rideOffer.getAcceptedUserKey() + "/acceptedRideOffers/" + key).setValue(rideOffer);
                            acceptedOffersRef.child(key).setValue(rideOffer);

                            //confirmation message
                            Toast.makeText(view.getContext(),
                                    "Offer accepted. View accepted offers list.",
                                    Toast.LENGTH_SHORT).show();

                            break;
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
