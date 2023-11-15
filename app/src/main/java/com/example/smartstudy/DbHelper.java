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

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DbHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "SmartStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "timetable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_BEGIN = "starttime";
    public static final String COLUMN_END = "endtime";
    public static final String COLUMN_COLOUR = "colour";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;

    }

    public static final String SQL_CREATE =
            "create table "+ TABLE +"(" +
                    COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBJECT + " TEXT NOT NULL, " +
                    COLUMN_BEGIN + " TEXT NOT NULL, " +
                    COLUMN_END + " TEXT NOT NULL, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_ROOM + " TEXT, " +
                    COLUMN_TEACHER + " TEXT, " +
                    COLUMN_COLOUR + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    void addTimeTableObject(String sub, String beg, String end, String day, String room, String teach, String colour){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, sub);
        contentValues.put(COLUMN_BEGIN, beg);
        contentValues.put(COLUMN_END, end);
        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_ROOM, room);
        contentValues.put(COLUMN_TEACHER, teach);
        contentValues.put(COLUMN_COLOUR, colour);
        if(sub.length() != 0 && beg.length() != 0 && end.length() != 0){
            long result = db.insert(TABLE, null, contentValues);
            if(result == -1){
                Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context , "Added succesfully!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }

    }
    void updateTimetableObject(String id, String sub, String beg, String end, String day, String room, String teach, String colour){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, sub);
        contentValues.put(COLUMN_BEGIN, beg);
        contentValues.put(COLUMN_END, end);
        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_ROOM, room);
        contentValues.put(COLUMN_TEACHER, teach);
        contentValues.put(COLUMN_COLOUR, colour);

        if(sub.length() != 0 && beg.length() != 0 && end.length() != 0){
            long result = db.update(TABLE,contentValues, "id=?", new String[]{id});
            if(result == -1){
                Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context , "Saved succesfully!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }


    }
    void deleteTimetableObject(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE, "id=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context , "Removed succesfully!", Toast.LENGTH_SHORT).show();
        }
    }
    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }
}
