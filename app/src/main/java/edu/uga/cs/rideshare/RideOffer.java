/**
 * This is a class for a RideOffer object.
 */
package edu.uga.cs.rideshare;

import java.io.Serializable;

public class RideOffer implements Serializable {
    private String offerKey; //unique key that is generated when added to the database
    private String start; //starting location of ride
    private String end; //ride destination
    private String date; //date of ride
    private String time; //time of ride
    private int cost; //point cost of ride
    private String offerUser; //person making the offer
    private String offerUserKey; //unique key associated with user
    private String acceptedUser; //person accepting the offer
    private String acceptedUserKey; //unique key associated with user
    private boolean confirmedByUser; //states whether user has confirmed the accepted offer

    //default constructor
    public RideOffer()
    {
        this.offerKey = null;
        this.start = null;
        this.end = null;
        this.date = null;
        this.time = null;
        this.cost = 0;
        this.offerUser = null;
        this.offerUserKey = null;
        this.acceptedUser = null;
        this.acceptedUserKey = null;
        this.confirmedByUser = false;
    }

    //constructor
    public RideOffer( String offerKey, String start, String end, String date, String time, int cost, String offerUser, String offerUserKey, String acceptedUser, String acceptedUserKey, boolean confirmedByUser) {
        this.offerKey = offerKey;
        this.start = start;
        this.end = end;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.offerUser = offerUser;
        this.offerUserKey = offerUserKey;
        this.acceptedUser = acceptedUser;
        this.acceptedUserKey = acceptedUserKey;
        this.confirmedByUser = confirmedByUser;
    }

    //get and set methods for all variables
    public String getOfferKey() { return offerKey; }

    public void setOfferKey(String offerKey) { this.offerKey = offerKey; }

    public String getStart() { return start; }

    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }

    public void setEnd(String end) { this.end = end; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public int getCost() { return cost; }

    public void setCost(int cost) { this.cost = cost; }

    public String getOfferUser() { return offerUser; }

    public void setOfferUser(String offerUser) { this.offerUser = offerUser; }

    public String getOfferUserKey() { return offerUserKey; }

    public void setOfferUserKey(String offerUserKey) { this.offerUserKey = offerUserKey; }

    public String getAcceptedUser() { return acceptedUser; }

    public void setAcceptedUser(String acceptedUser) { this.acceptedUser = acceptedUser; }

    public String getAcceptedUserKey() { return acceptedUserKey; }

    public void setAcceptedUserKey(String acceptedUserKey) { this.acceptedUserKey = acceptedUserKey; }

    public boolean getConfirmedByUser() { return confirmedByUser; }

    public void setConfirmedByUser(boolean confirmedByUser) { this.confirmedByUser = confirmedByUser; }
}