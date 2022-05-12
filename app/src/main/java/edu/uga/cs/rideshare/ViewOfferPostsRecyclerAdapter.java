/**
 * This is the recycler adapter for viewing all user's posted ride offers.
 * A delete and update button is also provided under each post.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
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

public class ViewOfferPostsRecyclerAdapter extends RecyclerView.Adapter<ViewOfferPostsRecyclerAdapter.ViewOfferPostsHolder> {
    public static final String DEBUG_TAG = "ViewOfferPostsRecyclerAdapter";

    private List<RideOffer> rideOfferList;
    private RideOffer rideOffer;

    public ViewOfferPostsRecyclerAdapter( List<RideOffer> rideOfferList)
    {
        this.rideOfferList = rideOfferList;
    }

    class ViewOfferPostsHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView cost; //point cost of ride
        Button updateButton; //used to update a post
        Button deleteButton; //used to delete a post

        public ViewOfferPostsHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.userOfferStart);
            end = (TextView) itemView.findViewById(R.id.userOfferEnd);
            date = (TextView) itemView.findViewById(R.id.userOfferDate);
            time = (TextView) itemView.findViewById(R.id.userOfferTime);
            cost = (TextView) itemView.findViewById(R.id.userOfferCost);
            updateButton = (Button) itemView.findViewById(R.id.button22);
            deleteButton = (Button) itemView.findViewById(R.id.button23);

            updateButton.setOnClickListener(new ViewOfferPostsRecyclerAdapter.updateButtonClickListener());
            deleteButton.setOnClickListener(new ViewOfferPostsRecyclerAdapter.deleteButtonClickListener());

        }
    }

    @Override
    public ViewOfferPostsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_post, parent, false);
        return new ViewOfferPostsHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewOfferPostsHolder holder, int position) {
        rideOffer = rideOfferList.get(position);

        holder.start.setText("Start Location: " + rideOffer.getStart());
        holder.end.setText("Destination: " + rideOffer.getEnd());
        holder.date.setText("Date: " + rideOffer.getDate());
        holder.time.setText("Time: " + rideOffer.getTime());
        holder.cost.setText("Points Cost: " + rideOffer.getCost());
    }

    /**
     * Used to update a ride offer. The particular RideOffer object is sent
     * to UpdateActivity
     */
    private class updateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //determines whether a post is an offer or request
            boolean isOffer = true;
            Intent intent = new Intent(view.getContext(), UpdateActivity.class);
            Bundle extras = new Bundle();

            //passes isOffer and the specific ride to the next activity
            extras.putBoolean("Type", isOffer);
            extras.putSerializable("Ride", rideOffer);
            intent.putExtras(extras);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Used to delete a ride offer. Offer will disappear from both the application
     * and database.
     */
    private class deleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //firebase authorization instance
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //database reference to all users
            DatabaseReference userRef = database.getReference("users");

            //database reference to all ride offers
            DatabaseReference rideOffersRef = database.getReference("allRideOffers");

            //delete from users ride offers
            userRef.child(rideOffer.getOfferUserKey() + "/rideOffers/" + rideOffer.getOfferKey()).removeValue();

            //delete from allRideOffers
            rideOffersRef.child(rideOffer.getOfferKey()).removeValue();
            rideOfferList.remove(rideOffer);
        }
    }

    @Override
    public int getItemCount() { return rideOfferList.size(); }

}

