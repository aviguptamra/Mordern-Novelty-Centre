package com.esri.natmoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.esri.natmoapp.util.CommonFunctions;

public class DatabaseController extends SQLiteOpenHelper {

    public Context contxt;
    private static final int DATABASE_VERSION = 1;
    static String DBPATH = "/" + CommonFunctions.parentFolderName + "/"
            + CommonFunctions.dbFolderName + "/ScanBar.sqlite";
    DatabaseInsert databaseInsert;

    public DatabaseController(Context applicationcontext) {
        super(applicationcontext, applicationcontext.getExternalFilesDir(null).getAbsolutePath() + DBPATH, null, DATABASE_VERSION);
        contxt = applicationcontext;
        databaseInsert = new DatabaseInsert();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tbl_state = "CREATE TABLE State(StateId NUMERIC,StateName TEXT,IsActive NUMERIC)";
        String tbl_district = "CREATE TABLE District(DistrictId NUMERIC,DistrictName TEXT,StateId NUMERIC ,IsActive NUMERIC)";

        try {
            dropTable(db, "State");
            dropTable(db, "District");

            db.execSQL(tbl_state);
            db.execSQL(tbl_district);
            databaseInsert.DeleteMasterDatabase(db, contxt);
            databaseInsert.InsertMasterDatabase(db, contxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropTable(SQLiteDatabase database, String tableName) {
        String query;
        query = "DROP TABLE IF EXISTS " + tableName;
        database.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
