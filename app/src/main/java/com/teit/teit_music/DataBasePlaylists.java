package com.teit.teit_music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBasePlaylists extends SQLiteOpenHelper {
    //name of our database
    private static final String DataBase_Name="PlaylistsDataBase";
    //database version
    private static final int DataBase_Version=1;
    //table name
    private static final String Table_Name = "playliststable";

    //city name column:
    private static final String Playlist_Column = "playlistname";


    public DataBasePlaylists(Context context){
        super(context,DataBase_Name,null,DataBase_Version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+Table_Name+" ("+Playlist_Column + " TEXT)";
        db.execSQL(query);
    }
    public void createPlaylists(String playlistname){
        //we should add name of song to our database =>
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Playlist_Column,playlistname);
        db.insert(Table_Name,null,values);
        //show records:
        db.close();
    }
    public void deletePlaylists(String playlistname){
        //we should remove our song by it's name from database =>
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Table_Name+ " WHERE "+Playlist_Column+"='"+playlistname+"'");
        //show records:
        db.close();
    }
    public void getAllRecords(ArrayList<String> list){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(Table_Name, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameCity = c.getColumnIndex(Playlist_Column);
            do {
                //add all records to list
                list.add(c.getString(nameCity));
            } while (c.moveToNext());
        }
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
