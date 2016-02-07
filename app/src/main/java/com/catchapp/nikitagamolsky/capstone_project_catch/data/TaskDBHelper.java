package com.catchapp.nikitagamolsky.capstone_project_catch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract.TaskEntry;

public class TaskDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "tasks.db";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_CATEGORY + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_URGENCY + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_IMPORTANCE + " TEXT NOT NULL," ;

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}