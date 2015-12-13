package com.jikexueyuan.maptest;

/**
 * Created by Leons on 2015/11/21.
 */
public class Location {
    private float radius;
    private double latitude;
    private double longitude;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }
}
