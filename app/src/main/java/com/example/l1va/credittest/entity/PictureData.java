package com.example.l1va.credittest.entity;

import android.graphics.Bitmap;

public class PictureData {
    int resourceId;
    Bitmap thumbnail;

    public PictureData(int resourceId, Bitmap thumbnail) {
        this.resourceId = resourceId;
        this.thumbnail = thumbnail;
    }

    public int getResourceId() {
        return resourceId;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
