package com.hooneys.smstomapproject.MyMonitoring;

import com.google.android.gms.maps.model.LatLng;

public class MMSDO {
    private String depart;
    private LatLng latLng;
    private String location;

    public MMSDO() {
        this.depart = null;
        this.latLng = null;
        this.location = null;
    }

    public MMSDO(String depart, LatLng latLng, String location) {
        this.depart = depart;
        this.latLng = latLng;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
