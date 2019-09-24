package com.hooneys.smstomapproject.MyRooms.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.MyRooms.Repository.CatchRepository;

import java.util.List;

public class CatchViewModel extends AndroidViewModel {
    private CatchRepository repository;
    private LiveData<List<Catch>> allCatches;

    public CatchViewModel(@NonNull Application application) {
        super(application);
        this.repository = new CatchRepository(application);
        this.allCatches = this.repository.getAllCatches();
    }

    public void insert(Catch item){
        this.repository.insert(item);
    }

    public void update(Catch item){
        this.repository.update(item);
    }

    public void delete(Catch item){
        this.repository.remove(item);
    }

    public LiveData<List<Catch>> getAllCatehs(){
        return this.allCatches;
    }
}
