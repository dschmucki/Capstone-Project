package com.example.catcha.sync.model;

public class Location {

    // the id of the location
    private String id;
    // type of the location, can contain: station, poi, address, refine
    private String type;
    // the location name
    private String name;
    // the accuracy of the result
    private String score;
    // the location coordinates
    private Coordinate coordinate;
    // distance to original point in meters
    private String distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return name;
    }
}
