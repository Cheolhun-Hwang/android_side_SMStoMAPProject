package com.hooneys.smstomapproject.MyRooms.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hooneys.smstomapproject.MyRooms.Do.Catch;

import java.util.List;

@Dao
public interface CatchDAO {
    @Query("SELECT * FROM catch")
    LiveData<List<Catch>> catchList();

    @Insert
    void insert(Catch item);

    @Update
    void update(Catch item);

    @Delete
    void delete(Catch item);
}
