package com.example.smartstudy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Time;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "SmartStudy.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "timetable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_BEGIN = "begin";
    public static final String COLUMN_END = "end";

    public DbHelper(@Nullable @org.jetbrains.annotations.Nullable Context context, @Nullable @org.jetbrains.annotations.Nullable String name, @Nullable @org.jetbrains.annotations.Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public static final String SQL_CREATE =
            "create table "+ TABLE +"(" + COLUMN_ID +" integer primary key autoincrement, " +
                    COLUMN_SUBJECT + " text not null, " +
                    COLUMN_BEGIN + "time, " +
                    COLUMN_END + "time, " +
                    COLUMN_DAY + " text not null, " +
                    COLUMN_ROOM + " text, " +
                    COLUMN_TEACHER + " text);";
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
