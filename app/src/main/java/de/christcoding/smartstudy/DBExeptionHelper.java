package de.christcoding.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBExeptionHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DBExeptionHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "ExeptionStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "exeption";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME= "time";


    public DBExeptionHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;

    }

    public static final String SQL_CREATE =
            "create table "+ TABLE +"(" +
                    COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_TIME + " INTEGER NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    void addExeptionObject(String date, int minutes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TIME, minutes);

        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            System.out.println("Hier läuft was falsch");
        } else {
            //Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();
        }





    }
    void updateExeptionObject(String id, String date, int minutes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TIME, minutes);

        long result = db.update(TABLE,contentValues, "id=?", new String[]{id});
        if(result == -1){
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context , "Saved succesfully!", Toast.LENGTH_SHORT).show();
        }



    }
    void deleteExeptionObject(String id){
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
