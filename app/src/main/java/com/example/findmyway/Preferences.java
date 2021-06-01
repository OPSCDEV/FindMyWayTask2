package com.example.findmyway;

public class Preferences {
    String prefLandmark,prefdistance,Email;
    public Preferences(){}

    public Preferences(String prefLandmark, String prefdistance, String email) {
        this.prefLandmark = prefLandmark;
        this.prefdistance = prefdistance;
        Email = email;
    }

    public String getPrefLandmark() {
        return prefLandmark;
    }

    public String getPrefDistance() {
        return prefdistance;
    }

    public String getEmail() {
        return Email;
    }

}
