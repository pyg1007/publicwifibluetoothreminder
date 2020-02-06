package com.example.wifibluetoothreminder.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContentListRepository {

    private ContentListDao contentListDao;

    private LiveData<List<ContentList>> Contentlist;

    public ContentListRepository(Application application) {
        ListDatabase listDatabase = ListDatabase.getDatabase(application);

        contentListDao = listDatabase.contentListDao();

        Contentlist = contentListDao.getAll();
    }

    public LiveData<List<ContentList>> getAllData() {
        return Contentlist;
    }

    public void Insert(final ContentList contentList) {
        Runnable insert = new Runnable() {
            @Override
            public void run() {
                contentListDao.Insert(contentList);
            }
        };
        Executor executor = Executors.newSingleThreadScheduledExecutor();
        executor.execute(insert);
    }

    public void Update(final int ID, final String Content) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                contentListDao.Update(ID, Content);
            }
        };
        Executor executor = Executors.newSingleThreadScheduledExecutor();
        executor.execute(update);
    }

    public void Delete(final int ID, final String Content) {
        Runnable delete = new Runnable() {
            @Override
            public void run() {
                contentListDao.Delete(ID, Content);
            }
        };
        Executor executor = Executors.newSingleThreadScheduledExecutor();
        executor.execute(delete);
    }
}
