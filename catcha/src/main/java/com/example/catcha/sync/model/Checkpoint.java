package com.example.catcha.sync.model;

import java.util.Date;

public class Checkpoint {

    private Location station;
    private String arrival;
    private Date departure;
    private String platform;
    private Prognosis prognosis;

    public Location getStation() {
        return station;
    }

    public void setStation(Location station) {
        this.station = station;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Prognosis getPrognosis() {
        return prognosis;
    }

    public void setPrognosis(Prognosis prognosis) {
        this.prognosis = prognosis;
    }

    @Override
    public String toString() {
        return "Checkpoint{" +
                "station=" + station +
                ", arrival='" + arrival + '\'' +
                ", departure='" + departure + '\'' +
                ", platform='" + platform + '\'' +
                ", prognosis=" + prognosis +
                '}';
    }
}
