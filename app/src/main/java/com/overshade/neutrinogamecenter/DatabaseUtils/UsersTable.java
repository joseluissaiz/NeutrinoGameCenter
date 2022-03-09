package com.overshade.neutrinogamecenter.DatabaseUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.overshade.neutrinogamecenter.R;

public class UsersTable {

    public static final String TABLE_NAME = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";
    public static final String KEY_ICON = "icon";
    public static final String KEY_LEVEL = "level";

    public static String CREATE_TABLE_SQL = (
        "CREATE TABLE "+TABLE_NAME+"("+
            KEY_USERNAME+" TEXT    ," +
            KEY_PASSWORD+" TEXT    ," +
            KEY_NAME    +" TEXT    ," +
            KEY_SURNAME +" TEXT    ," +
            KEY_ICON    +" INTEGER ," +
            KEY_LEVEL   +" INTEGER  " +
        ")"
    );

    public static void insertUser
            (String username, String password, String name,
             String surname, int icon, int level, DatabaseManager dbManager)
    {
        if (password.endsWith("\n")) {
            password = password.substring(0, password.length()-1);
        }
        SQLiteDatabase database = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USERNAME, username);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_SURNAME, surname);
        contentValues.put(KEY_ICON, icon);
        contentValues.put(KEY_LEVEL, level);
        database.insert(TABLE_NAME, null, contentValues);
        Log.d("SQL", "New user added :"+username);
        database.close();
    }

    public static boolean userExists(String username, DatabaseManager dbManager) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + KEY_USERNAME +
                    " FROM "  + TABLE_NAME +
                    " WHERE " + KEY_USERNAME + " = '"+username+"'", null
        )) {
            return cursor.getCount() > 0;
        }
    }

    public static boolean checkCredentials(
            String username, String password, DatabaseManager dbManager
    )
    {
        if (password.endsWith("\n")) {
            password = password.substring(0, password.length()-1);
        }
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                    "SELECT " + KEY_PASSWORD +
                        " FROM "  + TABLE_NAME   +
                        " WHERE " + KEY_USERNAME + " = '"+username+"' AND "
                                  + KEY_PASSWORD + " = '"+password+"'", null
        )) {
            cursor.moveToFirst();
            return cursor.getCount() > 0;
        }
    }

    public static int getIcon(String username, DatabaseManager dbManager) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + KEY_ICON +
                    " FROM "  + TABLE_NAME   +
                    " WHERE " + KEY_USERNAME + " = '"+username+"'", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                return R.drawable.robot_icon;
            }
        }
    }

    public static void setIcon(String username, int icon, DatabaseManager dbManager) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ICON, icon);
        String where = KEY_USERNAME + " = '"+username+"'";
        database.update(TABLE_NAME, contentValues, where, null);
    }

    public static void addXP(String username, int xp, DatabaseManager dbManager) {
        int currentXP = getLevel(username, dbManager);
        int totalXP = currentXP+xp;
        //update value
        SQLiteDatabase database = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LEVEL, totalXP);
        String where = KEY_USERNAME + " = '"+username+"'";
        database.update(TABLE_NAME, contentValues, where, null);
    }

    public static int getLevel(String username, DatabaseManager dbManager) {
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + KEY_LEVEL +
                    " FROM "  + TABLE_NAME   +
                    " WHERE " + KEY_USERNAME + " = '"+username+"'", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                return 0;
            }
        }
    }

    public static void setPassword(String username, String password, DatabaseManager dbManager) {
        if (password.endsWith("\n")) {
            password = password.substring(0, password.length()-1);
        }
        SQLiteDatabase database = dbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_PASSWORD, password);
        String where = KEY_USERNAME + " = '"+username+"'";
        database.update(TABLE_NAME, contentValues, where, null);
    }
}
