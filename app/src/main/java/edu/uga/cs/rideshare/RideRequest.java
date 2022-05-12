/**
 * This is a class for a RideRequest object.
 */
package edu.uga.cs.rideshare;

import java.io.Serializable;

public class RideRequest implements Serializable {
    private String requestKey;  //unique key that is generated when added to the database
    private String start; //starting location of ride
    private String end; //ride destination
    private String date; //date of ride
    private String time; //time of ride
    private int points; //point cost of ride
    private String requestUser; //person making the request
    private String requestUserKey; //unique key associated with user
    private String acceptedUser; //person accepting the request
    private String acceptedUserKey; //unique key associated with user
    private boolean confirmedByUser; //states whether user has confirmed the accepted request

    //default constructor
    public RideRequest()
    {
        this.requestKey = null;
        this.start = null;
        this.end = null;
        this.date = null;
        this.time = null;
        this.points = 0;
        this.requestUser = null;
        this.requestUserKey = null;
        this.acceptedUser = null;
        this.acceptedUserKey = null;
        this.confirmedByUser = false;
    }

    //constructor
    public RideRequest( String requestKey, String start, String end, String date, String time, int points, String requestUser, String requestUserKey, String acceptedUser,String acceptedUserKey, boolean confirmedByUser) {
        this.requestKey = requestKey;
        this.start = start;
        this.end = end;
        this.date = date;
        this.time = time;
        this.points = points;
        this.requestUser = requestUser;
        this.requestUserKey = requestUserKey;
        this.acceptedUser = acceptedUser;
        this.acceptedUserKey = acceptedUserKey;
        this.confirmedByUser = confirmedByUser;
    }

    //get and set methods for all variables
    public String getRequestKey() { return requestKey; }

    public void setRequestKey(String requestKey) { this.requestKey = requestKey; }

    public String getStart() { return start; }

    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }

    public void setEnd(String end) { this.end = end; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }

    public String getRequestUser() { return requestUser; }

    public void setRequestUser(String requestUser) { this.requestUser = requestUser; }

    public String getRequestUserKey() { return requestUserKey; }

    public void setRequestUserKey(String requestUserKey) { this.requestUserKey = requestUserKey; }

    public String getAcceptedUser() { return acceptedUser; }

    public void setAcceptedUser(String acceptedUser) { this.acceptedUser = acceptedUser; }

    public String getAcceptedUserKey() { return acceptedUserKey; }

    public void setAcceptedUserKey(String acceptedUserKey) { this.acceptedUserKey = acceptedUserKey; }

    public boolean getConfirmedByUser() { return confirmedByUser; }

    public void setConfirmedByUser(boolean confirmedByUser) { this.confirmedByUser = confirmedByUser; }
}