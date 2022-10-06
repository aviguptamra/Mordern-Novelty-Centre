package com.esri.natmoapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonDatabaseFunctions extends DatabaseController{

    public CommonDatabaseFunctions(Context applicationcontext) {
        super(applicationcontext);
    }
    public HashMap<List<Integer>,List<String>> getNamesandIdsOfMasterTable(String tableName,String whereClause)
    {
        HashMap<List<Integer>,List<String>> MastertableData=new HashMap<List<Integer>,List<String>>();
        List<Integer> MastertableId = new ArrayList<Integer>();
        List<String> MastertableName = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " +tableName+ " where "+whereClause;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MastertableId.add(cursor.getInt(0));
                MastertableName.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        MastertableData.put(MastertableId,MastertableName);
        cursor.close();
        db.close();
        return MastertableData;
    }
}
