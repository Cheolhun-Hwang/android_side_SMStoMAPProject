package com.hooneys.smstomapproject.MyRooms.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.hooneys.smstomapproject.MyRooms.Dao.CatchDAO;
import com.hooneys.smstomapproject.MyRooms.Databases;
import com.hooneys.smstomapproject.MyRooms.Do.Catch;

import java.util.List;

public class CatchRepository {
    private CatchDAO catchDAO;
    private LiveData<List<Catch>> catchList;

    public CatchRepository(Application application){
        //Use Application Context. SingleTone Just One.
        Databases database = Databases.getInstance(application);
        this.catchDAO = database.catchDAO();
        this.catchList = this.catchDAO.catchList();
    }

    public void insert(Catch item){
        new InsertItemAsyncTask(this.catchDAO).execute(item);      //AsyncTask 실행
    }

    private static class InsertItemAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDAO catchDAO;
        private InsertItemAsyncTask(CatchDAO catchDAO){
            this.catchDAO = catchDAO;
        }
        @Override
        protected Void doInBackground(Catch... catches) {  //여러개의 배열이긴 하지만
            this.catchDAO.insert(catches[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public void update(Catch item){
        new updateItemAsyncTask(this.catchDAO).execute(item);
    }

    private static class updateItemAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDAO catchDAO;
        private updateItemAsyncTask(CatchDAO catchDAO){
            this.catchDAO = catchDAO;
        }
        @Override
        protected Void doInBackground(Catch... catches) {  //여러개의 배열이긴 하지만
            this.catchDAO.update(catches[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public void remove(Catch item){
        new removeItemAsyncTask(this.catchDAO).execute(item);
    }

    private static class removeItemAsyncTask extends AsyncTask<Catch, Void, Void> {
        private CatchDAO catchDAO;
        private removeItemAsyncTask(CatchDAO catchDAO){
            this.catchDAO = catchDAO;
        }
        @Override
        protected Void doInBackground(Catch... catches) {  //여러개의 배열이긴 하지만
            this.catchDAO.delete(catches[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public LiveData<List<Catch>> getAllCatches(){
        return this.catchList;
    }
}
