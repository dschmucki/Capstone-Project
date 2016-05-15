package com.example.catcha.sync.model;

public class Walk {

    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Walk{" +
                "duration='" + duration + '\'' +
                '}';
    }
}
