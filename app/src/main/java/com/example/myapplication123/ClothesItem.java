package com.example.myapplication123;

public class ClothesItem {
    private String type;
    private int preference;
    private String imageUrl;

    public ClothesItem(String type, int preference, String imageUrl) {
        this.type = type;
        this.preference = preference;
        this.imageUrl = imageUrl;
    }

    public String getType() { return type; }
    public int getPreference() { return preference; }
    public String getImageUrl() { return imageUrl; }
}
