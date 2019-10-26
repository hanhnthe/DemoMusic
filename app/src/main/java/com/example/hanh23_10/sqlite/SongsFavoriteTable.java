package com.example.hanh23_10.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SongsFavoriteTable extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "songManager";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "songFavorite";

    public static final String KEY_ID = "id";
    public static final String ID_PROVIDER = "id_provider";
    public static final String IS_FAVORITE = "is_favorite";
    public static final String COUNT_OF_PLAY = "count_of_play";


    public SongsFavoriteTable(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_songs_table = String.
                format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "%s INTEGER,%s INTEGER, %s INTEGER )",
                        TABLE_NAME, KEY_ID, ID_PROVIDER,
                        IS_FAVORITE, COUNT_OF_PLAY);
        db.execSQL(create_songs_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_songs_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_songs_table);

        onCreate(db);
    }
//   //them du lieu den bang SongsFavorite
//    public void addData(SongModel song,int is_favorite, int count_of_play){
//        ContentValues values = new ContentValues();
//        values.put(ID_PROVIDER,song.getId());
//        values.put(IS_FAVORITE,is_favorite);
//        values.put(COUNT_OF_PLAY,count_of_play);
//        mContentResolver.insert(FavoriteSongProvider.CONTENT_URI,values);
//    }
//    //tim kiem bai hat theo id_provider
//    public SongDataFavorite findSongsData(int id){
//        String[] projection = {KEY_ID,ID_PROVIDER,IS_FAVORITE,COUNT_OF_PLAY};
//        String selection = "id_provider = \"" + id + "\"";
//        Cursor cursor = mContentResolver.query(FavoriteSongProvider.CONTENT_URI,
//                projection,selection,null,null);
//        SongDataFavorite song = new SongDataFavorite();
//        if(cursor.moveToFirst()){
//            song.setId(cursor.getInt(0));
//            song.setId_provider(cursor.getInt(1));
//            song.setIs_favorite(cursor.getInt(2));
//            song.setCount_of_play(cursor.getInt(3));
//        }else {
//            song = null;
//        }
//        return song;
//    }
//    //delete data theo id_provider
//    public boolean deleteData(int id){
//        boolean result = false;
//        String selction = "id_provider = \""+ id + "\"";
//        int rowDeleted = mContentResolver.delete(
//                FavoriteSongProvider.CONTENT_URI,selction,null);
//        if(rowDeleted>0){
//            result = true;
//        }
//        return result;
//    }
//    //update SongsFavorite
//    public boolean updateData(int id_provider, int is_favorite, int count_of_play){
//        ContentValues args = new ContentValues();
//        args.put(IS_FAVORITE,is_favorite);
//        args.put(COUNT_OF_PLAY,count_of_play);
//        boolean result = false;
//        String selection = "id_provider = \""+ id_provider +"\"";
//        int rowsUpdate = mContentResolver.update(FavoriteSongProvider.CONTENT_URI,
//                args,selection,null);
//        if(rowsUpdate > 0){
//            result  = true;
//        }
//        return result;
//    }
}
