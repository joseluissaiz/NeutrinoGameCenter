package com.overshade.neutrinogamecenter.DatabaseUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.overshade.neutrinogamecenter.R;

public class StatsTable {

    public static final String TABLE_NAME = "stats";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_GAME = "game";
    public static final String KEY_POINTS = "points";

    public static String CREATE_TABLE_SQL = (
            "CREATE TABLE "+TABLE_NAME+"("+
                    KEY_USERNAME+" TEXT    ," +
                    KEY_GAME    +" TEXT    ," +
                    KEY_POINTS  +" INTEGER  " +
                    ")"
    );

    public static void insertResult
            (String username, String game, int points, DatabaseManager dbManager)
    {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_GAME, game);
        contentValues.put(KEY_POINTS, points);
        database.insert(TABLE_NAME, null, contentValues);
        Log.d("SQL", "New result added :"+username);
        database.close();
    }

    public static int getHighScore(String username, String game, DatabaseManager dbManager) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT MAX(" + KEY_POINTS + ")" +
                        " FROM "  + TABLE_NAME   +
                        " WHERE " + KEY_USERNAME + " = '"+username+"' AND " +
                                    KEY_GAME     + " = '"+game    +"'", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                return 0;
            }
        }
    }

}
