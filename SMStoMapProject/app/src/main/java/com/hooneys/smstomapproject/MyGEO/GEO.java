package com.hooneys.smstomapproject.MyGEO;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GEO {
    public LatLng getNameToLatLng(Context context, String spot){
        Geocoder geocoder = new Geocoder(context);
        List<Address> list = null;
        Address address = null;
        try{
            list = geocoder.getFromLocationName(spot, 1);
            address = list.get(0);
            if(address != null){
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
