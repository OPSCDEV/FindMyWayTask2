package com.example.findmyway;

import com.google.android.gms.maps.model.LatLng;

public class GetLatLng {
    public LatLng latlng;
    public String Name;

    public GetLatLng() {

    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
