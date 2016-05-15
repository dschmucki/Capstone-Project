package com.example.catcha.sync.model;

public class Coordinate {

    // type of the given coordinate
    private String type;
    private String x;
    private String y;

    public double getLat() {
        return Double.parseDouble(x);
    }

    public double getLon() {
        return Double.parseDouble(y);
    }

}
