package com.example.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.smartstudy.models.Todo;

public class DBTodoHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DBTodoHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "TodoStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "todos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_TODO = "todo";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CHECKED = "checked";



    public DBTodoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;
    }


    public static final String SQL_CREATE =
            "create table if not exists "+ TABLE +"(" +
                    COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TODO + " TEXT NOT NULL, " +
                    COLUMN_TIME + " INTEGER NOT NULL, " +
                    COLUMN_COLLECTION + " TEXT NOT NULL, " +
                    COLUMN_CHECKED + " INTEGER NOT NULL);";




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

    void addTodoObject(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TODO, todo.getTodo());
        contentValues.put(COLUMN_TIME, todo.getTime());
        contentValues.put(COLUMN_COLLECTION, todo.getCollection());
        contentValues.put(COLUMN_CHECKED, todo.getChecked());

        try{
            long result = db.insertOrThrow(TABLE, null, contentValues);
            if (result == -1) {
                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                System.out.println("Hier läuft was falsch");
            } else {
                //Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    void updateTodoObject(Todo todo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLLECTION, todo.getCollection());
        contentValues.put(COLUMN_TODO, todo.getTodo());
        contentValues.put(COLUMN_TIME, todo.getTime());
        contentValues.put(COLUMN_CHECKED, todo.getChecked());





        long result = db.update(TABLE,contentValues, "id=?", new String[]{todo.getId()});
            if(result == -1){
                Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
            }



    }

    void deleteTodoObject(Todo todo){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE, "id=?", new String[]{todo.getId()});
        if(result == -1){
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context , "Removed succesfully!", Toast.LENGTH_SHORT).show();
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

