package com.example.proglam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proglam.database.utility.DAO;
import com.example.proglam.database.utility.misurazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class database {
    private static final String DB_NAME = "tracker";
    private static final int DB_VERSION = 1;

    private ExecutorService executorService;
    private DatabaseHelper dbHelper;
    private DAO measureDao;

    private static database instance;

    private database(Context context) {
        dbHelper = new DatabaseHelper(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized database getInstance(Context context) {
        if (instance == null) {
            instance = new database(context);
        }
        return instance;
    }

    public void Insert(misurazioni misurazione) {
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TYPE, misurazione.getCategoria());
            values.put(DatabaseHelper.COLUMN_VALUE, misurazione.getRilevazione());
            values.put(DatabaseHelper.COLUMN_MGRS_COORDINATES, misurazione.getCoordinate());
            values.put(DatabaseHelper.COLUMN_CREATION_DATE, misurazione.getData().getTime());
            db.insert(DatabaseHelper.TABLE_NAME, null, values);
            db.close();
        });
    }

    public LiveData<List<misurazioni>> getAllMeasurements(String mgrsArea) {
        MutableLiveData<List<misurazioni>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_MGRS_COORDINATES + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{mgrsArea});
            List<misurazioni> misurazioniList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    int codice = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEASURE_COD));
                    String categoria = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                    int rilevazione = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_VALUE));
                    String coordinate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MGRS_COORDINATES));
                    long data = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATION_DATE));

                    misurazioni misurazione = new misurazioni(codice, categoria, rilevazione, coordinate, data);
                    misurazioniList.add(misurazione);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            liveData.postValue(misurazioniList);
        });
        return liveData;
    }

    public LiveData<List<misurazioni>> getFromAreaAndType(String mgrsArea, String type) {
        MutableLiveData<List<misurazioni>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_MGRS_COORDINATES + " = ? AND " + DatabaseHelper.COLUMN_TYPE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{mgrsArea, type});
            List<misurazioni> misurazioniList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    int codice = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEASURE_COD));
                    String categoria = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                    int rilevazione = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_VALUE));
                    String coordinate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MGRS_COORDINATES));
                    long data = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATION_DATE));

                    misurazioni misurazione = new misurazioni(codice, categoria, rilevazione, coordinate, data);
                    misurazioniList.add(misurazione);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            liveData.postValue(misurazioniList);
        });
        return liveData;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TABLE_NAME = "misurazioni";
        private static final String COLUMN_MEASURE_COD = "Codice";
        private static final String COLUMN_TYPE = "Categoria";
        private static final String COLUMN_VALUE = "Rilevazione";
        private static final String COLUMN_MGRS_COORDINATES = "Coordinate";
        private static final String COLUMN_CREATION_DATE = "Data";

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_MEASURE_COD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TYPE + " STRING, " +
                    COLUMN_VALUE + " INTEGER, " +
                    COLUMN_MGRS_COORDINATES + " STRING, " +
                    COLUMN_CREATION_DATE + " INTEGER )";
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        }
    }
}
