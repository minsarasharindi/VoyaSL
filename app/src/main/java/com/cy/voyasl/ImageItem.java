package com.cy.voyasl;

public class ImageItem {
    private String imageUrl;
    private String description;
    private int id;

    public ImageItem(String imageUrl, String description, int id) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
