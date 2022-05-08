package edu.cuhk.mapnotes.datatypes;

import android.graphics.Bitmap;

public class Photo {
    private Bitmap image;

    public Photo(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
