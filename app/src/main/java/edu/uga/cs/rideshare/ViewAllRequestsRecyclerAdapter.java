/**
 * This is the recycler adapter for viewing all ride requests (except the current user's)
 * A button to accept rides is also provided under each post.
 */
package edu.uga.cs.rideshare;

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

public class ViewAllRequestsRecyclerAdapter extends RecyclerView.Adapter<ViewAllRequestsRecyclerAdapter.ViewAllRequestsHolder> {
    public static final String DEBUG_TAG = "ViewAllRequestsRecyclerAdapter";

    private List<RideRequest> rideRequestList;
    private RideRequest rideRequest;

    public ViewAllRequestsRecyclerAdapter( List<RideRequest> rideRequestList)
    {
        this.rideRequestList = rideRequestList;
    }

    class ViewAllRequestsHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView points; //point cost of ride
        TextView requestingUser; //user that posted the request
        Button acceptRequestButton; //used to accept an request

        public ViewAllRequestsHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.allRequestStart);
            end = (TextView) itemView.findViewById(R.id.allRequestEnd);
            date = (TextView) itemView.findViewById(R.id.allRequestDate);
            time = (TextView) itemView.findViewById(R.id.allRequestTime);
            points = (TextView) itemView.findViewById(R.id.allRequestPoints);
            requestingUser = (TextView) itemView.findViewById(R.id.allRequestUser);
            acceptRequestButton = (Button) itemView.findViewById(R.id.button18);

            acceptRequestButton.setOnClickListener(new ViewAllRequestsRecyclerAdapter.AcceptRequestClickListener());
        }
    }

    @Override
    public ViewAllRequestsRecyclerAdapter.ViewAllRequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_requests, parent, false);
        return new ViewAllRequestsRecyclerAdapter.ViewAllRequestsHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewAllRequestsRecyclerAdapter.ViewAllRequestsHolder holder, int position) {
        rideRequest = rideRequestList.get(position);

        holder.start.setText("Start Location: " + rideRequest.getStart());
        holder.end.setText("Destination: " + rideRequest.getEnd());
        holder.date.setText("Date: " + rideRequest.getDate());
        holder.time.setText("Time: " + rideRequest.getTime());
        holder.points.setText("Points Cost: " + rideRequest.getPoints());
        holder.requestingUser.setText("Posted By: " + rideRequest.getRequestUser());
    }

    /**
     * Used to accept a ride request. Once accepted, ride is moved to user's accepted
     * requests list.
     */
    private class AcceptRequestClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //firebase authorization instance
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //database reference to all users
            DatabaseReference userRef = database.getReference("users");

            //database references to all ride requests and offers
            DatabaseReference rideRequestsRef = database.getReference("allRideRequests");
            DatabaseReference acceptedRequestsRef = database.getReference("allAcceptedRideRequests");

            //Ride removed from overall offer/request list
            rideRequestsRef.child(rideRequest.getRequestKey()).removeValue();
            rideRequestList.remove(rideRequest);

            //Ride removed from offering/requesting user’s list
            userRef.child(rideRequest.getRequestUserKey() + "/rideRequests/" + rideRequest.getRequestKey()).removeValue();

            //Set accepting user email and id to database child
            rideRequest.setAcceptedUser(firebaseAuth.getCurrentUser().getEmail());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for( DataSnapshot postSnapshot: snapshot.getChildren() ) {
                        User user = postSnapshot.getValue(User.class);
                        if((user.getUserEmail()).equals(firebaseAuth.getCurrentUser().getEmail())) {
                            //set the request's accepted user key to the current user's key
                            rideRequest.setAcceptedUserKey(user.getUserId());

                            //Added to offering user's confirmation list with accepting user’s email
                            String key = userRef.push().getKey();
                            rideRequest.setRequestKey(key);
                            userRef.child(rideRequest.getRequestUserKey() + "/rideRequestsToConfirm/" + key).setValue(rideRequest);

                            //Added to accepting user’s accepted ride requests
                            userRef.child(rideRequest.getAcceptedUserKey() + "/acceptedRideRequests/" + key).setValue(rideRequest);
                            acceptedRequestsRef.child(key).setValue(rideRequest);

                            //confirmation message
                            Toast.makeText(view.getContext(),
                                    "Request accepted. View accepted requests list.",
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
    public int getItemCount() { return rideRequestList.size(); }

}
