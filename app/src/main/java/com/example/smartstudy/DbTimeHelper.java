package com.example.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.Time;

public class DbTimeHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DbTimeHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "SmartStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "time";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TIME = "time";

    public DbTimeHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;
    }

    public static final String SQL_CREATE =
            "create table " + TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_TIME + "TEXT NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    void addTimeTableObject(String day, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_TIME, time);


        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();
        }


    }

    void updateTimetableObject(String id, String day, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_TIME, time);


        long result = db.update(TABLE, contentValues, "id=?", new String[]{id});
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Saved succesfully!", Toast.LENGTH_SHORT).show();
        }


    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }
}
