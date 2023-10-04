package com.example.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.smartstudy.models.Event;

public class DBEventHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DBEventHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "EventStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "event";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_BEGIN = "starttime";
    public static final String COLUMN_END = "endtime";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_PROGRESS = "progress";
    public static final String COLOMN_DESCRIPTION = "description";
    public static final String COLUMN_REMAININGDAYS = "remainingdays";
    public static final String COLUMN_REMAININGMINUTES = "remainingminutes";
    public static final String COLUMN_ABSOLUTMINUTES = "absolutminutes";






    public DBEventHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;

    }

    public static final String SQL_CREATE =
            "create table if not exists "+ TABLE +"(" +
                    COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBJECT + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_VOLUME + " INTEGER NOT NULL, " +
                    COLUMN_BEGIN + " TEXT NOT NULL, " +
                    COLUMN_END + " TEXT NOT NULL, " +
                    COLUMN_COLOR + " TEXT NOT NULL, " +
                    COLUMN_PROGRESS + " INTEGER, " +
                    COLOMN_DESCRIPTION + " TEXT, " +
                    COLUMN_REMAININGDAYS + " INTEGER, " +
                    COLUMN_REMAININGMINUTES + " INTEGER, " +
                    COLUMN_ABSOLUTMINUTES + " INTEGER);";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
        System.out.println("table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    void addEventObject(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, event.getSubject());
        contentValues.put(COLUMN_TYPE, event.getType());
        contentValues.put(COLUMN_VOLUME, event.getVolume());
        contentValues.put(COLUMN_BEGIN, event.getStartDate());
        contentValues.put(COLUMN_END, event.getEndDate());
        contentValues.put(COLUMN_COLOR, event.getColor());
        contentValues.put(COLUMN_PROGRESS, event.getProgress());
        contentValues.put(COLOMN_DESCRIPTION, event.getDescription());
        contentValues.put(COLUMN_REMAININGDAYS, event.getRemainingDays());
        contentValues.put(COLUMN_REMAININGMINUTES, event.getRemainingMinutes());
        contentValues.put(COLUMN_ABSOLUTMINUTES, event.getAbsolutMinutes());

        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            System.out.println("Hier läuft was falsch");
        } else {
            //Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();
        }





    }
    void updateEventObject(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, event.getSubject());
        contentValues.put(COLUMN_TYPE, event.getType());
        contentValues.put(COLUMN_VOLUME, event.getVolume());
        contentValues.put(COLUMN_BEGIN, event.getStartDate());
        contentValues.put(COLUMN_END, event.getEndDate());
        contentValues.put(COLUMN_COLOR, event.getColor());
        contentValues.put(COLUMN_PROGRESS, event.getProgress());
        contentValues.put(COLOMN_DESCRIPTION, event.getDescription());
        contentValues.put(COLUMN_REMAININGDAYS, event.getRemainingDays());
        contentValues.put(COLUMN_REMAININGMINUTES, event.getRemainingMinutes());
        contentValues.put(COLUMN_ABSOLUTMINUTES, event.getAbsolutMinutes());


            long result = db.update(TABLE,contentValues, "id=?", new String[]{event.getId()});
            if(result == -1){
                Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context , "Saved succesfully!", Toast.LENGTH_SHORT).show();
            }



    }
    void deleteEventObject(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE, "id=?", new String[]{event.getId()});
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
