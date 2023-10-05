package com.example.convertor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DataBase extends SQLiteOpenHelper {

    private static final String DB_RES = "converter";
    private static int version = 1;
    private static final String TABLE = "results";
    private static final String COLUM = "value";


    public DataBase(@Nullable Context context) {
        super(context, DB_RES, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUM + " TEXT NOT NULL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DELETE TABLE IF EXISTS " + db + ";";
        db.execSQL(query);
        onCreate(db);
    }

    public void insertData (String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUM, result);
        db.insertWithOnConflict(TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void deleteData (String result) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, COLUM + " =?", new String[] {result});
        db.close();
    }

    public ArrayList<String> getData () {
        ArrayList<String> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,new String[]{COLUM}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(COLUM);
            dataList.add(cursor.getString(index));
        }
        db.close();
        cursor.close();
        return dataList;
    }
}
