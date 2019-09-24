package com.hooneys.smstomapproject.MyRooms.Do;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Catch {
    //Basic Info
    @PrimaryKey(autoGenerate = true)
    private int index;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "company")
    private String company;

    @ColumnInfo(name = "sendnum")
    private String sendNum;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "date")
    private String date;

    //지도
    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lon")
    private double lon;

    public Catch() {
        this.phone = null;
        this.name = null;
        this.company = null;
        this.sendNum = null;
        this.location = null;
        this.date = null;
        this.lat = 0.0;
        this.lon = 0.0;
    }

    public Catch(String phone, String name, String company, String sendNum,
                 String location, String date, double lat, double lon) {
        this.phone = phone;
        this.name = name;
        this.company = company;
        this.sendNum = sendNum;
        this.location = location;
        this.date = date;
        this.lat = lat;
        this.lon = lon;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSendNum() {
        return sendNum;
    }

    public void setSendNum(String sendNum) {
        this.sendNum = sendNum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
