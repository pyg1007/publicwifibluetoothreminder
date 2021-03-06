package com.yonggeun.wifibluetoothreminder.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yonggeun.wifibluetoothreminder.Room.ContentList;
import com.yonggeun.wifibluetoothreminder.Room.ContentListRepository;

import java.util.List;

public class ContentListViewModel extends AndroidViewModel {

    private ContentListRepository contentListRepository;

    private LiveData<List<ContentList>> Contentlist;


    public ContentListViewModel(Application application) {
        super(application);

        contentListRepository = new ContentListRepository(application);
        Contentlist = contentListRepository.getAllData();
    }

    public LiveData<List<ContentList>> getAll() {
        return Contentlist;
    }

    public void Insert(ContentList contentList) {
        contentListRepository.Insert(contentList);
    }

    public void Update(int ID, String Content) {
        contentListRepository.Update(ID, Content);
    }

    public void Delete(int ID, String Content) {
        contentListRepository.Delete(ID, Content);
    }

    public void DeleteAll(String Content_SSID) {
        contentListRepository.DeleteAll(Content_SSID);
    }
}
