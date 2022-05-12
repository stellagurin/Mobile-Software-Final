/**
 * This is the recycler adapter for accepted ride requests
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

public class ConfirmAcceptedRequestsRecyclerAdapter extends RecyclerView.Adapter<ConfirmAcceptedRequestsRecyclerAdapter.ConfirmAcceptedRequestsHolder> {
    public static final String DEBUG_TAG = "ConfirmAcceptedRequestsRecyclerAdapter";

    private List<RideRequest> rideRequestList;
    private RideRequest rideRequest;

    public ConfirmAcceptedRequestsRecyclerAdapter( List<RideRequest> rideRequestList)
    {
        this.rideRequestList = rideRequestList;
    }

    class ConfirmAcceptedRequestsHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView cost; //point cost of ride
        TextView acceptingUser; //user that accepted the ride
        Button confirmRequestButton; //button to confirm request

        public ConfirmAcceptedRequestsHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.confirmRequestStart);
            end = (TextView) itemView.findViewById(R.id.confirmRequestEnd);
            date = (TextView) itemView.findViewById(R.id.confirmRequestDate);
            time = (TextView) itemView.findViewById(R.id.confirmRequestTime);
            cost = (TextView) itemView.findViewById(R.id.confirmRequestCost);
            acceptingUser = (TextView) itemView.findViewById(R.id.confirmRequestAccepter);
            confirmRequestButton = (Button) itemView.findViewById(R.id.button19);

            confirmRequestButton.setOnClickListener(new ConfirmAcceptedRequestsRecyclerAdapter.ConfirmRequestClickListener());
        }
    }

    @Override
    public ConfirmAcceptedRequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_request, parent, false);
        return new ConfirmAcceptedRequestsHolder(view);
    }

    @Override
    public void onBindViewHolder( ConfirmAcceptedRequestsHolder holder, int position) {
        rideRequest = rideRequestList.get(position);

        holder.start.setText("Start Location: " + rideRequest.getStart());
        holder.end.setText("Destination: " + rideRequest.getEnd());
        holder.date.setText("Date: " + rideRequest.getDate());
        holder.time.setText("Time: " + rideRequest.getTime());
        holder.cost.setText("Points Cost: " + rideRequest.getPoints());
        holder.acceptingUser.setText("Accepted By: " + rideRequest.getAcceptedUser());
    }

    private class ConfirmRequestClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //Firebase Authorization reference
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //Firebase database reference
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //references to all users and all accepted ride requests (by all users)
            DatabaseReference userRef = database.getReference("users");
            DatabaseReference acceptedRequestsRef = database.getReference("allAcceptedRideRequests");

            //remove ride from allAcceptedRideRequests
            acceptedRequestsRef.child(rideRequest.getRequestKey()).removeValue();
            rideRequestList.remove(rideRequest);

            //remove ride from rideRequestToConfirm from user that posted
            userRef.child(rideRequest.getRequestUserKey() + "/rideRequestsToConfirm/" + rideRequest.getRequestKey()).removeValue();

            //remove ride from acceptedRideRequest from user that accepted
            userRef.child(rideRequest.getAcceptedUserKey() + "/acceptedRideRequests/" + rideRequest.getRequestKey()).removeValue();


            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        User user = postSnapshot.getValue(User.class);
                        //user that offered
                        if((user.getUserEmail()).equals(rideRequest.getRequestUser())) {
                            //subtract points from user that requested
                            int currentPoints = user.getPoints();
                            int ridePoints = rideRequest.getPoints();
                            int newPointBalance = currentPoints - ridePoints;
                            userRef.child(user.getUserId() + "/points").setValue(newPointBalance);
                        }

                        //user that accepted
                        else if(user.getUserEmail().equals(rideRequest.getAcceptedUser())) {
                            //add points to user that accepted
                            int currentPoints = user.getPoints();
                            int ridePoints = rideRequest.getPoints();
                            int newPointBalance = currentPoints + ridePoints;
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
    public int getItemCount() { return rideRequestList.size(); }

}
