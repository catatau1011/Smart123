package com.example.smartufopa.Urbanismo;

public class Model {
    private String ImageUrl;

    public Model() {
    }

    public Model(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
        return imageUrl;
    }
}
