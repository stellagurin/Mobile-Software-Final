/**
 * This is the recycler adapter for viewing all user's posted ride requests.
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

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ViewRequestPostsRecyclerAdapter extends RecyclerView.Adapter<ViewRequestPostsRecyclerAdapter.ViewRequestPostsHolder> {
    public static final String DEBUG_TAG = "ViewRequestPostsRecyclerAdapter";

    private List<RideRequest> rideRequestList;
    private RideRequest rideRequest;

    public ViewRequestPostsRecyclerAdapter( List<RideRequest> rideRequestList)
    {
        this.rideRequestList = rideRequestList;
    }

    class ViewRequestPostsHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView points; //point cost of ride
        Button updateButton; //used to update a post
        Button deleteButton; //used to delete a post

        public ViewRequestPostsHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.userRequestStart);
            end = (TextView) itemView.findViewById(R.id.userRequestEnd);
            date = (TextView) itemView.findViewById(R.id.userRequestDate);
            time = (TextView) itemView.findViewById(R.id.userRequestTime);
            points = (TextView) itemView.findViewById(R.id.userRequestPoints);
            updateButton = (Button) itemView.findViewById(R.id.button24);
            deleteButton = (Button) itemView.findViewById(R.id.button25);

            updateButton.setOnClickListener(new ViewRequestPostsRecyclerAdapter.updateButtonClickListener());
            deleteButton.setOnClickListener(new ViewRequestPostsRecyclerAdapter.deleteButtonClickListener());
        }
    }

    @Override
    public ViewRequestPostsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_post, parent, false);
        return new ViewRequestPostsHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewRequestPostsHolder holder, int position) {
        rideRequest = rideRequestList.get(position);

        holder.start.setText("Start Location: " + rideRequest.getStart());
        holder.end.setText("Destination: " + rideRequest.getEnd());
        holder.date.setText("Date: " + rideRequest.getDate());
        holder.time.setText("Time: " + rideRequest.getTime());
        holder.points.setText("Points Cost: " + rideRequest.getPoints());
    }

    /**
     * Used to update a ride request. The particular RideRequest object is sent
     * to UpdateActivity
     */
    private class updateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //determines whether a post is an offer or request
            boolean isOffer = false;
            Intent intent = new Intent(view.getContext(), UpdateActivity.class);
            Bundle extras = new Bundle();

            //passes isOffer and the specific ride to the next activity
            extras.putBoolean("Type", isOffer);
            extras.putSerializable("Ride", rideRequest);
            intent.putExtras(extras);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * Used to delete a ride request. Request will disappear from both the application
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

            //database reference to all ride requests
            DatabaseReference rideRequestsRef = database.getReference("allRideRequests");

            //delete from users ride requests
            userRef.child(rideRequest.getRequestUserKey() + "/rideRequests/" + rideRequest.getRequestKey()).removeValue();

            //delete from allRideRequests
            rideRequestsRef.child(rideRequest.getRequestKey()).removeValue();
            rideRequestList.remove(rideRequest);
        }
    }

    @Override
    public int getItemCount() { return rideRequestList.size(); }

}

