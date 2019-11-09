package com.example.hanh6_11.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh6_11.OnSongClickListener;
import com.example.hanh6_11.SongModel;
import com.example.hanh6_11.fragment.BaseSongListFragment;
import com.example.hanh6_11.fragment.MediaPlaybackFragment;
import com.example.hanh6_11.sqlite.FavoriteSongProvider;
import com.example.hanh6_11.sqlite.SongsFavoriteTable;

import java.io.ByteArrayOutputStream;

public abstract class LayoutController implements OnSongClickListener {

    protected AppCompatActivity mActivity;
    protected BaseSongListFragment mBaseSongListFragment;
    protected OnSongClickListener mOnclickService;

    public LayoutController(AppCompatActivity activity) {
        mActivity = activity;
    }

    public abstract void onCreate(int i);

    public abstract void setmOnclickService(OnSongClickListener click);

    //chuyen bitmap -> byte
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    // tim kiem theo id cua bai hat
    public Cursor findSongById(int id) {
        return mActivity.getContentResolver().query(FavoriteSongProvider.CONTENT_URI, new String[]{SongsFavoriteTable.COUNT_OF_PLAY},
                SongsFavoriteTable.ID_PROVIDER + "=?",
                new String[]{String.valueOf(id)}, null);
    }

    public void clickSongFavorite(SongModel item) {
        ContentValues values = new ContentValues();
        Cursor cursor = findSongById(item.getId());
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            System.out.println(count);
            count++;
            if (count == 3) {
                values.put(SongsFavoriteTable.IS_FAVORITE, 2);
                Toast.makeText(mActivity.getApplicationContext(), "đã thêm bài hát "
                        + item.getNameSong() + " vao yeu thich", Toast.LENGTH_SHORT).show();
            }
            values.put(SongsFavoriteTable.COUNT_OF_PLAY, count);
            mActivity.getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                    "id_provider = \"" + item.getId() + "\"", null);
            cursor.close();
        } else {
            int count_of_play = 0;
            count_of_play++;
            values.put(SongsFavoriteTable.COUNT_OF_PLAY, count_of_play);
            values.put(SongsFavoriteTable.ID_PROVIDER, item.getId());
            values.put(SongsFavoriteTable.IS_FAVORITE, 0);
            mActivity.getContentResolver().insert(FavoriteSongProvider.CONTENT_URI, values);
        }
    }


}
