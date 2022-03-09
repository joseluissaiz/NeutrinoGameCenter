package com.overshade.neutrinogamecenter.DatabaseUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.overshade.neutrinogamecenter.R;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    private final String username;
    private final int iconRes;
    private final int level;
    private final int score;

    public UserInfo(String username, int iconRes, int level, int score) {
        this.username = username;
        this.iconRes = iconRes;
        this.level = level;
        this.score = score;
    }


    public static List<UserInfo> getLevelRanking(DatabaseManager dbManager) {
        List<UserInfo> userList = new ArrayList<>();
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + ", "
                              + UsersTable.KEY_ICON + ", "
                              + UsersTable.KEY_LEVEL + ", "
                              + "MAX(" + StatsTable.KEY_POINTS + ")" +
                        " FROM "  + UsersTable.TABLE_NAME + ", " + StatsTable.TABLE_NAME  +
                        " WHERE " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + " = "+ StatsTable.TABLE_NAME+"."+StatsTable.KEY_USERNAME +
                        " GROUP BY " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME +
                        " ORDER BY " + UsersTable.KEY_LEVEL + " DESC" +
                        " LIMIT 10", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    userList.add(new UserInfo(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2) / 5000, //level is divided (5000xp/level)
                            cursor.getInt(3)
                    ));
                    cursor.moveToNext();
                }
            }
        }
        System.out.println(userList.size());
        return userList;
    }

    public static List<UserInfo> getScoreUserRanking(DatabaseManager dbManager) {
        List<UserInfo> userList = new ArrayList<>();
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + ", "
                        + UsersTable.KEY_ICON + ", "
                        + UsersTable.KEY_LEVEL + ", "
                        + "MAX(" + StatsTable.KEY_POINTS + ")" +
                        " FROM "  + UsersTable.TABLE_NAME + ", " + StatsTable.TABLE_NAME  +
                        " WHERE " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + " = "+ StatsTable.TABLE_NAME+"."+StatsTable.KEY_USERNAME +
                        " GROUP BY " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME +
                        " ORDER BY " + StatsTable.KEY_POINTS + " DESC" +
                        " LIMIT 10", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    userList.add(new UserInfo(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2) / 5000, //level is divided (5000xp/level)
                            cursor.getInt(3)
                    ));
                    cursor.moveToNext();
                }
            }
        }
        System.out.println(userList.size());
        return userList;
    }

    public static List<UserInfo> getScoreRanking(DatabaseManager dbManager) {
        List<UserInfo> userList = new ArrayList<>();
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + ", "
                        + UsersTable.KEY_ICON + ", "
                        + UsersTable.KEY_LEVEL + ", "
                        + StatsTable.KEY_POINTS +
                        " FROM "  + UsersTable.TABLE_NAME + ", " + StatsTable.TABLE_NAME  +
                        " WHERE " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + " = "+ StatsTable.TABLE_NAME+"."+StatsTable.KEY_USERNAME +
                        " ORDER BY " + StatsTable.KEY_POINTS + " DESC" +
                        " LIMIT 10", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    userList.add(new UserInfo(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2) / 5000, //level is divided (5000xp/level)
                            cursor.getInt(3)
                    ));
                    cursor.moveToNext();
                }
            }
        }
        System.out.println(userList.size());
        return userList;
    }

    public static List<UserInfo> getScoreGameRanking(String gameName, DatabaseManager dbManager) {
        List<UserInfo> userList = new ArrayList<>();
        SQLiteDatabase database = dbManager.getWritableDatabase();
        try (Cursor cursor = database.rawQuery(
                "SELECT " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + ", "
                        + UsersTable.KEY_ICON + ", "
                        + UsersTable.KEY_LEVEL + ", "
                        + StatsTable.KEY_POINTS +
                        " FROM "  + UsersTable.TABLE_NAME + ", " + StatsTable.TABLE_NAME  +
                        " WHERE " + UsersTable.TABLE_NAME+"."+UsersTable.KEY_USERNAME + " = "+ StatsTable.TABLE_NAME+"."+StatsTable.KEY_USERNAME + " AND "
                                  + StatsTable.KEY_GAME + " = '"+ gameName + "'" +
                        " ORDER BY " + StatsTable.KEY_POINTS + " DESC" +
                        " LIMIT 10", null
        )) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    userList.add(new UserInfo(
                            cursor.getString(0),
                            cursor.getInt(1),
                            cursor.getInt(2) / 5000, //level is divided (5000xp/level)
                            cursor.getInt(3)
                    ));
                    cursor.moveToNext();
                }
            }
        }
        System.out.println(userList.size());
        return userList;
    }

    public static int getRankPoints(String username, DatabaseManager dbManager) {
        //Get all ranking scores
        List<UserInfo> rankLevel = getLevelRanking(dbManager);
        List<UserInfo> rankScoreUser = getScoreUserRanking(dbManager);
        List<UserInfo> rankScore = getScoreRanking(dbManager);
        List<UserInfo> rankScore2048 = getScoreGameRanking("2048", dbManager);
        List<UserInfo> rankScorePeg = getScoreGameRanking("peg", dbManager);
        //See if the user is top in any one and sum points
        int points = 0;
        try {
            if (rankLevel.get(0).username.equals(username)) {
                points++;
            }
            if (rankScoreUser.get(0).username.equals(username)) {
                points++;
            }
            if (rankScore.get(0).username.equals(username)) {
                points++;
            }
            if (rankScore2048.get(0).username.equals(username)) {
                points++;
            }
            if (rankScorePeg.get(0).username.equals(username)) {
                points++;
            }
        } catch (IndexOutOfBoundsException noScores) {
            return 0;
        }

        return points;
    }

    public String getUsername() {
        return username;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }
}
