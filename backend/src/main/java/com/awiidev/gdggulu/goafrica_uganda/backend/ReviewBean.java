package com.awiidev.gdggulu.goafrica_uganda.backend;

/**
 * The object model for the data we are sending through endpoints
 */
public class ReviewBean {
    Long id;
    Long dishId;
    String username;
    String comment;
    Long rating;

    public ReviewBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDishId() { return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
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
