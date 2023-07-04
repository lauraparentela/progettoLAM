package com.example.proglam.database.utility;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface DAO {
    void Insert(misurazioni misurazioni);
    LiveData<List<misurazioni>> getAll(String mgrsArea);
    LiveData<List<misurazioni>> getFromAreaAndType(String mgrsArea, String type);
}
