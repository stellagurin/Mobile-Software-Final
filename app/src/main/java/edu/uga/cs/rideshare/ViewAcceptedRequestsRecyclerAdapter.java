/**
 * This is the recycler adapter for the user's accepted ride requests
 */
package edu.uga.cs.rideshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAcceptedRequestsRecyclerAdapter extends RecyclerView.Adapter<ViewAcceptedRequestsRecyclerAdapter.ViewAcceptedRequestsHolder> {
    public static final String DEBUG_TAG = "ViewAcceptedRequestsRecyclerAdapter";

    private List<RideRequest> rideRequestList;

    public ViewAcceptedRequestsRecyclerAdapter( List<RideRequest> rideRequestList)
    {
        this.rideRequestList = rideRequestList;
    }

    class ViewAcceptedRequestsHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView points; //point cost of ride
        TextView requestUser; //user that posted the request

        public ViewAcceptedRequestsHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.acceptedRequestStart);
            end = (TextView) itemView.findViewById(R.id.acceptedRequestEnd);
            date = (TextView) itemView.findViewById(R.id.acceptedRequestDate);
            time = (TextView) itemView.findViewById(R.id.acceptedRequestTime);
            points = (TextView) itemView.findViewById(R.id.acceptedRequestPoints);
            requestUser = (TextView) itemView.findViewById(R.id.acceptedRequestUser);
        }
    }

    @Override
    public ViewAcceptedRequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_request, parent, false);
        return new ViewAcceptedRequestsHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewAcceptedRequestsHolder holder, int position) {
        RideRequest rideRequest = rideRequestList.get(position);

        holder.start.setText("Start Location: " + rideRequest.getStart());
        holder.end.setText("Destination: " + rideRequest.getEnd());
        holder.date.setText("Date: " + rideRequest.getDate());
        holder.time.setText("Time: " + rideRequest.getTime());
        holder.points.setText("Points Cost: " + rideRequest.getPoints());
        holder.requestUser.setText("Posted By: " + rideRequest.getRequestUser());
    }

    @Override
    public int getItemCount() { return rideRequestList.size(); }

}

