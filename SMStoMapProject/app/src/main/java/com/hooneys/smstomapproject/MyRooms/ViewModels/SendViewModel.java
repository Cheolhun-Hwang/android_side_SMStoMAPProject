package com.hooneys.smstomapproject.MyRooms.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.MyRooms.Do.Send;
import com.hooneys.smstomapproject.MyRooms.Repository.CatchRepository;
import com.hooneys.smstomapproject.MyRooms.Repository.SendRepository;

import java.util.List;

public class SendViewModel extends AndroidViewModel {
    private SendRepository repository;
    private LiveData<List<Send>> allSends;

    public SendViewModel(@NonNull Application application) {
        super(application);
        this.repository = new SendRepository(application);
        this.allSends = this.repository.getAllSends();
    }

    public void insert(Send item){
        this.repository.insert(item);
    }

    public void update(Send item){
        this.repository.update(item);
    }

    public void delete(Send item){
        this.repository.remove(item);
    }

    public LiveData<List<Send>> getAllSends(){
        return this.allSends;
    }
}
