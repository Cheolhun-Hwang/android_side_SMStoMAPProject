package com.hooneys.smstomapproject.MyRooms.Do;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Send {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "phone")
    private String phone;

    public Send() {
        this.phone = null;
    }

    public Send(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
