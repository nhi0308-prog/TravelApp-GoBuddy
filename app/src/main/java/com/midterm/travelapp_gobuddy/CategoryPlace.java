package com.midterm.travelapp_gobuddy;

public class CategoryPlace {
    private final int image;
    private final String name;
    private final float rating;

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