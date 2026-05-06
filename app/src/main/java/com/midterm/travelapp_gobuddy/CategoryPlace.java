package com.midterm.travelapp_gobuddy;

public class CategoryPlace {
    private int image;
    private String name;
    private float rating;

    public CategoryPlace(int image, String name, float rating) {
        this.image = image;
        this.name = name;
        this.rating = rating;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }
}