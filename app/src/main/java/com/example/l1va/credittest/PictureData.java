package com.example.l1va.credittest;

import android.graphics.Bitmap;

public class PictureData {
    int resourceId;
    String description;
    Bitmap thumbnail;

    public PictureData(int resourceId, String description, Bitmap thumbnail) {
        this.resourceId = resourceId;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
