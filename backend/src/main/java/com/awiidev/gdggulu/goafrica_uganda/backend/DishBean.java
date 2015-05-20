package com.awiidev.gdggulu.goafrica_uganda.backend;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class DishBean {

    Long Id;
    String title;
    String description;
    String ingredients;
    String steps;
//     String photo;
//    String videoUrl;


    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title;  }

    public String getDescription() { return description;  }

    public void setDescription(String description) { this.description = description;  }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

//    public String getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(String photo) {
//        this.photo = photo;
//    }
//
//    public String getVideoUrl() {
//        return videoUrl;
//    }
//
//    public void setVideoUrl(String videoUrl) {
//        this.videoUrl = videoUrl;
//    }


}
