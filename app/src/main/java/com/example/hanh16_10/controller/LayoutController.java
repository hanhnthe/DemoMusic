package com.example.hanh16_10.controller;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh16_10.MediaPlaybackService;
import com.example.hanh16_10.OnSongClickListener;
import com.example.hanh16_10.SongModel;
import com.example.hanh16_10.fragment.AllSongsFragment;
import com.example.hanh16_10.fragment.MediaPlaybackFragment;

import java.io.ByteArrayOutputStream;

public abstract class LayoutController implements OnSongClickListener {

    protected AppCompatActivity mActivity;
    protected AllSongsFragment mAllSongsFragment;
    protected OnSongClickListener mOnclickService;
    protected MediaPlaybackService mService;

    public LayoutController(AppCompatActivity activity, MediaPlaybackService service) {
        mActivity = activity;
        mService = service;
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

    public abstract void onCreate(Bundle saveInstate, int currentSongNumber);

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

}
