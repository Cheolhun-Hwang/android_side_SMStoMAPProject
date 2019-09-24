package com.hooneys.smstomapproject.MyRooms;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.hooneys.smstomapproject.MyRooms.Dao.CatchDAO;
import com.hooneys.smstomapproject.MyRooms.Dao.SendDAO;
import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.MyRooms.Do.Send;

@Database(entities = {Catch.class, Send.class}, version = 1)
public abstract class Databases extends RoomDatabase {
    private static Databases instance;
    public abstract CatchDAO catchDAO();
    public abstract SendDAO sendDAO();

    public static synchronized Databases getInstance(Context context){
        if(instance == null){
            //Only One..
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    Databases.class,
                    "sms_catch_database")
                    .fallbackToDestructiveMigration()   //Crash Check.
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
