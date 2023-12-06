package de.christcoding.smartstudy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBLanguageHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = DBLanguageHelper.class.getSimpleName();
    private Context context;
    public static final String DB_NAME = "LanguageStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "language";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_NATIVE = "native";
    public static final String COLUMN_FOREIGN = "foreigner";
    public static final String COLUMN_CORRECT = "correct";


    public DBLanguageHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
        this.context = context;
    }


    public static final String SQL_CREATE =
            "create table " + TABLE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COLLECTION + " TEXT NOT NULL, " +
                    COLUMN_NATIVE + " TEXT NOT NULL, " +
                    COLUMN_FOREIGN +  " TEXT NOT NULL, " +
                    COLUMN_CORRECT + " INTEGER);";



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

    void addLanguageObject(Translation translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLLECTION, translation.getCollection());
        contentValues.put(COLUMN_NATIVE, translation.getNative_word());
        contentValues.put(COLUMN_FOREIGN, translation.getForeign_word());
        contentValues.put(COLUMN_CORRECT, translation.getCorrect());



        long result = db.insert(TABLE, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            System.out.println("Hier läuft was falsch");
        } else {
            //Toast.makeText(context, "Added succesfully!", Toast.LENGTH_SHORT).show();
        }


    }

    void updateLanguageObject(Translation translation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLLECTION, translation.getCollection());
        contentValues.put(COLUMN_NATIVE, translation.getNative_word());
        contentValues.put(COLUMN_FOREIGN, translation.getForeign_word());
        contentValues.put(COLUMN_CORRECT, translation.getCorrect());


        if(translation.getCollection().length() != 0 && translation.getNative_word().length() != 0 && translation.getForeign_word().length() != 0){
            long result = db.update(TABLE,contentValues, "id=?", new String[]{translation.getId()});
            if(result == -1){
                Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context , "Failed!", Toast.LENGTH_SHORT).show();
        }


    }

    void deleteLanguageObject(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE, "id=?", new String[]{id});
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
