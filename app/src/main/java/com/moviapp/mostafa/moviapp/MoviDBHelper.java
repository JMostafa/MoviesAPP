package com.moviapp.mostafa.moviapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mostafa on 8/28/2016.
 */
public class MoviDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase moviDB;
    static final String DATABASE_NAME = "movi.db";

    public MoviDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        moviDB = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIDATA_TABLE = "CREATE TABLE moviData ( " +
                " id INTEGER PRIMARY KEY,title TEXT NOT NULL,date TEXT NOT NULL ," +
                " overView TEXT NOT NULL ,language TEXT NOT NULL ,posterPath TEXT NOT NULL," +
                " rate DOUBLE NOT NULL , poster BLOB NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIDATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS moviData");
        onCreate(sqLiteDatabase);
    }


    public Boolean delete(String table, String whereClause, String[] whereArgs) {
        Boolean resu = false;
        moviDB.beginTransaction();
        try {
            moviDB.delete(table, whereClause, whereArgs);
            moviDB.setTransactionSuccessful();
            resu = true;
        } catch (SQLException e) {
            Log.e("DELETE", e.getMessage());
            resu = false;
        } finally {
            moviDB.endTransaction();
        }

        return resu;
    }

    /** To insert row in table. */

    public long insertRow(String table, String whereClause,
                          ContentValues contentArgs) {
        long resu = 0;
        moviDB.beginTransaction();
        try {
            resu = moviDB.insert(table, whereClause, contentArgs);
            moviDB.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("INSERT", e.getMessage());
        } finally {
            moviDB.endTransaction();
        }

        return resu;
    }

    /** To update row in table. */
    public int updateRow(String table, ContentValues values,
                         String whereClause, String[] whereArgs) {
        Boolean resu = false;
        int noOfRows = 0;
        moviDB.beginTransaction();
        try {
            noOfRows = moviDB.update(table, values, whereClause, whereArgs);
            if (noOfRows > 0)
                resu = true;
            moviDB.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("UPDATE", e.getMessage());
            if (noOfRows < 0)
                resu = false;
        } finally {
            moviDB.endTransaction();
        }

        return noOfRows;
    }

    /** To select all rows in table. */
    public Cursor selectAllRaw(String tableName) {

        Cursor cursor = null; String sql = "SELECT * FROM " + tableName;
        cursor = moviDB.rawQuery(sql, new String[]{});
        return cursor;
    }

    /** To select specific row in table. */
    public Cursor selectRaw(String tableName, String wherecol) {

        Cursor cursor = null; String sql = "SELECT * FROM " + tableName+" where "+ wherecol;
        cursor = moviDB.rawQuery(sql, new String[]{});
        return cursor;
    }

    public boolean ifFound(int id)
    {
        Cursor cursor = selectRaw("moviData"," id = "+id);
        if(cursor.getCount()<1)
            return false;
        else
            return true;
    }
}
