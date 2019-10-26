package com.example.hanh23_10.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh23_10.OnSongClickListener;
import com.example.hanh23_10.SongModel;
import com.example.hanh23_10.fragment.AllSongsFragment;
import com.example.hanh23_10.fragment.BaseSongListFragment;
import com.example.hanh23_10.fragment.MediaPlaybackFragment;
import com.example.hanh23_10.sqlite.FavoriteSongProvider;
import com.example.hanh23_10.sqlite.SongsFavoriteTable;

import java.io.ByteArrayOutputStream;

public abstract class LayoutController implements OnSongClickListener {

    protected AppCompatActivity mActivity;
    protected BaseSongListFragment mAllSongsFragment;
    protected OnSongClickListener mOnclickService;
    protected SongsFavoriteTable mSongFavorite;

    public LayoutController(AppCompatActivity activity) {
        mActivity = activity;
    }

    //lưu trữ để truyeemf dữ liệu tu fragmnet nay sang fragment khác
    protected Bundle newBundleFromSong(SongModel song) {
        Bundle args = new Bundle();
        String songString = encodeTobase64(song.getImageSong());
        args.putInt(MediaPlaybackFragment.NUMBER_EXTRA, song.getNumber());
        args.putString(MediaPlaybackFragment.NAME_SONG_EXTRA, song.getNameSong());
        args.putString(MediaPlaybackFragment.AUTHOR_SONG_EXTRA, song.getAuthorSong());
        args.putString(MediaPlaybackFragment.TIME_SONG_EXTRA, song.getTimeSong());
        args.putString(MediaPlaybackFragment.IMAGE_SONG_EXTRA, songString);
        return args;
    }

    public abstract void onCreate();

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
            findSongById(item.getId()).close();
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
