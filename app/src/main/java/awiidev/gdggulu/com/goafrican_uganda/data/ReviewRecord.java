package awiidev.gdggulu.com.goafrican_uganda.data;

import java.io.Serializable;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class ReviewRecord implements Serializable {
    Long dishID;
    String username;
    String comment;
    Long rating;


    public ReviewRecord(Long dishID, String username, String comment, Long rating) {
        this.dishID = dishID;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    public Long getDishID() {
        return dishID;
    }

    public void setDishID(Long dishID) {
        this.dishID = dishID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }
}
