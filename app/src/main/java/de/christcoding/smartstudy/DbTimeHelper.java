package de.christcoding.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbTimeHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DbTimeHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "TimeStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "time";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_DURATION = "duration";

    public DbTimeHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;
    }

    public static final String SQL_CREATE =
            "create table " + TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DAY + " TEXT NOT NULL, " +
                    COLUMN_DURATION + " INTEGER NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
        System.out.println("Führt den Code hier aus");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    long addTimeObject(String day, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_DURATION, duration);


        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            System.out.println("Hier läuft was falsch");
        } else {
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    void updateTimeObject(String id, String day, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DAY, day);
        contentValues.put(COLUMN_DURATION, duration);


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
        }else{
            System.out.println("Wrong");
        }

        return cursor;
    }
}
