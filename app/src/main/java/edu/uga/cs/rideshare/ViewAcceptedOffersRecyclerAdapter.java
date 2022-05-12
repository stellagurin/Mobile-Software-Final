/**
 * This is the recycler adapter for the user's accepted ride offers
 */
package edu.uga.cs.rideshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAcceptedOffersRecyclerAdapter extends RecyclerView.Adapter<ViewAcceptedOffersRecyclerAdapter.ViewAcceptedOffersHolder> {
    public static final String DEBUG_TAG = "ViewAcceptedOffersRecyclerAdapter";

    private List<RideOffer> rideOfferList;

    public ViewAcceptedOffersRecyclerAdapter( List<RideOffer> rideOfferList)
    {
        this.rideOfferList = rideOfferList;
    }

    class ViewAcceptedOffersHolder extends RecyclerView.ViewHolder {

        TextView start; //ride's starting location
        TextView end; //ride's destination
        TextView date; //date of ride
        TextView time; //time of ride
        TextView cost; //point cost of ride
        TextView offerUser; //user that posted the offer

        public ViewAcceptedOffersHolder(View itemView) {
            super(itemView);

            start = (TextView) itemView.findViewById(R.id.acceptedOfferStart);
            end = (TextView) itemView.findViewById(R.id.acceptedOfferEnd);
            date = (TextView) itemView.findViewById(R.id.acceptedOfferDate);
            time = (TextView) itemView.findViewById(R.id.acceptedOfferTime);
            cost = (TextView) itemView.findViewById(R.id.acceptedOfferCost);
            offerUser = (TextView) itemView.findViewById(R.id.acceptedOfferUser);
        }
    }

    @Override
    public ViewAcceptedOffersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_offer, parent, false);
        return new ViewAcceptedOffersHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewAcceptedOffersHolder holder, int position) {
        RideOffer rideOffer = rideOfferList.get(position);

        holder.start.setText("Start Location: " + rideOffer.getStart());
        holder.end.setText("Destination: " + rideOffer.getEnd());
        holder.date.setText("Date: " + rideOffer.getDate());
        holder.time.setText("Time: " + rideOffer.getTime());
        holder.cost.setText("Points Cost: " + rideOffer.getCost());
        holder.offerUser.setText("Posted By: " + rideOffer.getOfferUser());
    }

    @Override
    public int getItemCount() { return rideOfferList.size(); }

}

