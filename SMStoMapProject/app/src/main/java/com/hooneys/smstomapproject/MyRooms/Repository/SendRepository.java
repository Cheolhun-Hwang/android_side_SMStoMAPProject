package com.hooneys.smstomapproject.MyRooms.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.hooneys.smstomapproject.MyRooms.Dao.CatchDAO;
import com.hooneys.smstomapproject.MyRooms.Dao.SendDAO;
import com.hooneys.smstomapproject.MyRooms.Databases;
import com.hooneys.smstomapproject.MyRooms.Do.Catch;
import com.hooneys.smstomapproject.MyRooms.Do.Send;

import java.util.List;

public class SendRepository {
    private SendDAO sendDAO;
    private LiveData<List<Send>> sendList;

    public SendRepository(Application application){
        //Use Application Context. SingleTone Just One.
        Databases database = Databases.getInstance(application);
        this.sendDAO = database.sendDAO();
        this.sendList = this.sendDAO.sendList();
    }

    public void insert(Send item){
        new InsertItemAsyncTask(this.sendDAO).execute(item);      //AsyncTask 실행
    }

    private static class InsertItemAsyncTask extends AsyncTask<Send, Void, Void> {
        private SendDAO sendDAO;
        private InsertItemAsyncTask(SendDAO sendDAO){
            this.sendDAO = sendDAO;
        }
        @Override
        protected Void doInBackground(Send... sends) {  //여러개의 배열이긴 하지만
            this.sendDAO.insert(sends[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public void update(Send item){
        new updateItemAsyncTask(this.sendDAO).execute(item);
    }

    private static class updateItemAsyncTask extends AsyncTask<Send, Void, Void> {
        private SendDAO sendDAO;
        private updateItemAsyncTask(SendDAO snedDAO){
            this.sendDAO = sendDAO;
        }
        @Override
        protected Void doInBackground(Send... sends) {  //여러개의 배열이긴 하지만
            this.sendDAO.update(sends[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public void remove(Send item){
        new removeItemAsyncTask(this.sendDAO).execute(item);
    }

    private static class removeItemAsyncTask extends AsyncTask<Send, Void, Void> {
        private SendDAO sendDAO;
        private removeItemAsyncTask(SendDAO sendDAO){
            this.sendDAO = sendDAO;
        }
        @Override
        protected Void doInBackground(Send... sends) {  //여러개의 배열이긴 하지만
            this.sendDAO.delete(sends[0]);                   //이번에는 하나만 받기 때문에.
            return null;
        }
    }

    public LiveData<List<Send>> getAllSends(){
        return this.sendList;
    }
}
