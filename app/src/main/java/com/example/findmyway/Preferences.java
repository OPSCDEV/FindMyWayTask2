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

    public void setPrefLandmark(String prefLandmark) {
        this.prefLandmark = prefLandmark;
    }

    public String getPrefdistance() {
        return prefdistance;
    }

    public void setPrefdistance(String prefdistance) {
        this.prefdistance = prefdistance;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
