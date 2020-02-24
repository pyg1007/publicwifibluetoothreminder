package com.example.wifibluetoothreminder.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContentListDao {

    @Query("SELECT * FROM Content_table")
    LiveData<List<ContentList>> getAll();

    @Query("SELECT * FROM Content_table WHERE Mac = :Mac")
    List<ContentList> getItem(String Mac);

    @Insert
    void Insert(ContentList contentList);

    @Query("Update Content_table SET Content = :Content WHERE ID = :ID")
    void Update(int ID, String Content);

    @Query("Delete FROM Content_table WHERE ID = :ID AND Content = :Content")
    void Delete(int ID, String Content);

    @Query("Delete FROM Content_table Where Mac = :Mac")
    void Delete_All(String Mac);

}
