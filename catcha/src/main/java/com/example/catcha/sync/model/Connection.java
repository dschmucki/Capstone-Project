package com.example.catcha.sync.model;

import java.util.Arrays;
import java.util.List;

public class Connection {

    private Checkpoint from;
    private Checkpoint to;
    private String duration;
    private Service service;
    private String[] products;
    private String capacity1st;
    private String capacity2nd;
    private List<Section> sections;

    public Checkpoint getFrom() {
        return from;
    }

    public void setFrom(Checkpoint from) {
        this.from = from;
    }

    public Checkpoint getTo() {
        return to;
    }

    public void setTo(Checkpoint to) {
        this.to = to;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String[] getProducts() {
        return products;
    }

    public void setProducts(String[] products) {
        this.products = products;
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

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "from=" + from +
                ", to=" + to +
                ", duration='" + duration + '\'' +
                ", service=" + service +
                ", products=" + Arrays.toString(products) +
                ", capacity1st='" + capacity1st + '\'' +
                ", capacity2nd='" + capacity2nd + '\'' +
                ", sections=" + sections +
                '}';
    }
}
