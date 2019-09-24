package com.hooneys.smstomapproject.MyRooms.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hooneys.smstomapproject.MyRooms.Do.Send;

import java.util.List;

@Dao
public interface SendDAO {
    @Query("SELECT * FROM send")
    LiveData<List<Send>> sendList();

    @Insert
    void insert(Send item);

    @Update
    void update(Send item);

    @Delete
    void delete(Send item);
}
