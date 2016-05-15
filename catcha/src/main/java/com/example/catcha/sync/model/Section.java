package com.example.catcha.sync.model;

public class Section {

    private Journey journey;
    private Walk walk;
    private Checkpoint departure;
    private Checkpoint arrival;

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Walk getWalk() {
        return walk;
    }

    public void setWalk(Walk walk) {
        this.walk = walk;
    }

    public Checkpoint getDeparture() {
        return departure;
    }

    public void setDeparture(Checkpoint departure) {
        this.departure = departure;
    }

    public Checkpoint getArrival() {
        return arrival;
    }

    public void setArrival(Checkpoint arrival) {
        this.arrival = arrival;
    }

    @Override
    public String toString() {
        return "Section{" +
                "journey=" + journey +
                ", walk=" + walk +
                ", departure=" + departure +
                ", arrival=" + arrival +
                '}';
    }
}
