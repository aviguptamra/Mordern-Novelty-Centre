package com.esri.natmoapp.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import com.esri.natmoapp.util.FileCommonFunctions;

import java.io.IOException;

public class DatabaseInsert {

    public void InsertMasterDatabase(SQLiteDatabase db, Context context) {
        String[] list;
        AssetManager am = context.getAssets();
        try {
            list = am.list("");
            if (list.length > 0) {
                for (String file : list) {
                    if (file.endsWith(".txt")) {
                        InsertMaster(db, context, file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DeleteMasterDatabase(SQLiteDatabase db, Context context) {
        String[] list;
        AssetManager am = context.getAssets();
        try {
            list = am.list("");
            if (list.length > 0) {
                for (String file : list) {
                    if (file.endsWith(".txt")) {
                        String[] table_name = file.split("[.]", 0);
                        DeleteMaster(db, context, table_name[0]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DeleteMaster(SQLiteDatabase db, Context context, String TableName) {
        db.beginTransaction();
        try {
            String drop_query = "DELETE FROM " + TableName;
            SQLiteStatement st1 = db.compileStatement(drop_query);
            st1.execute();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Toast.makeText(context, "Error encountered while deleting master database.", Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    public void InsertMaster(SQLiteDatabase db, Context context, String TableName) {
        db.beginTransaction();
        try {
            String insertQuery = new FileCommonFunctions().getQueryInsert(TableName, context);
            SQLiteStatement st = db.compileStatement(insertQuery);
            st.execute();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Toast.makeText(context, "Error encountered while building master database.", Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }
}
