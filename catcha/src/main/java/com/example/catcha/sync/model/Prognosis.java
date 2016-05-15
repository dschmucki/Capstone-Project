package com.example.catcha.sync.model;

/**
 * Created by domi on 29.03.16.
 */
public class Prognosis {

    private String platform;
    private String departure;
    private String arrival;
    private String capacity1st;
    private String capacity2nd;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getCapacity1st() {
        return capacity1st;
    }

    public void setCapacity1st(String capacity1st) {
        this.capacity1st = capacity1st;
    }

    public String getCapacity2nd() {
        return capacity2nd;
    }

    public void setCapacity2nd(String capacity2nd) {
        this.capacity2nd = capacity2nd;
    }

    @Override
    public String toString() {
        return "Prognosis{" +
                "platform='" + platform + '\'' +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", capacity1st='" + capacity1st + '\'' +
                ", capacity2nd='" + capacity2nd + '\'' +
                '}';
    }
}
