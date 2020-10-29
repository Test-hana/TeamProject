package com.example.test1;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem, MyItem2 {
    private final LatLng mPosition;
    private final String title;
    private final String snippet;
    private final Bitmap bitmap;

    public MyItem(double lat, double lng, String title, String snippet,Bitmap bitmap) {
        mPosition = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.bitmap = bitmap;
    }
    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    @Nullable
    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

}
