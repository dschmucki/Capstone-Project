package com.example.catcha.sync.model;

public class Service {

    private String regular;
    private String irregular;

    public String getIrregular() {
        return irregular;
    }

    public void setIrregular(String irregular) {
        this.irregular = irregular;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    @Override
    public String toString() {
        return "Service{" +
                "regular='" + regular + '\'' +
                ", irregular='" + irregular + '\'' +
                '}';
    }
}
