package com.teit.teit_music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBasePlayList extends SQLiteOpenHelper {
    //name of our database
    private static String DataBase_Name="Playlist";

    //database version
    private static final int DataBase_Version=1;
    //table name
    private String Table_Name="PlayList_TABLE";

    //city name column:
    private static final String Playlist_Column = "mewplaylistname";

    public DataBasePlayList(Context context){
        super(context,DataBase_Name,null,DataBase_Version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+Table_Name+" ("+Playlist_Column + " TEXT)";
        db.execSQL(query);
    }
    public void createTable(SQLiteDatabase db , String table_Name){
        String query = "CREATE TABLE "+table_Name+" ("+Playlist_Column + " TEXT)";
        db.execSQL(query);
    }
    public void createPlaylist(String playlistname , String table_Name){
        //we should add name of song to our database =>
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Playlist_Column,playlistname);
        db.insert(table_Name,null,values);
        Log.d("Music" , "NewRecord" + playlistname);
        //show records:
        db.close();
    }
    public void deletePlaylist(String playlistname , String table_Name){
        //we should remove our song by it's name from database =>
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table_Name+ " WHERE "+Playlist_Column+"='"+playlistname+"'");
        //show records:
        db.close();
    }
    public void getAllRecords(ArrayList<String> list , String table_Name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(table_Name, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameCity = c.getColumnIndex(Playlist_Column);
            do {
                //add all records to list
                list.add(c.getString(nameCity));
            } while (c.moveToNext());
        }
        else{

        }
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void Destroy(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+DataBase_Name);
    }
}
