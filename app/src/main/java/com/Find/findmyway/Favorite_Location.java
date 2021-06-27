package com.Find.findmyway;

public class Favorite_Location {
    String email;
    String locationName;

    public Favorite_Location(String email, String locationName) {
        this.email = email;
        this.locationName = locationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
