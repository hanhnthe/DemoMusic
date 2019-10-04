package com.example.hanh4_10.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh4_10.OnSongClickListener;
import com.example.hanh4_10.SongModel;
import com.example.hanh4_10.fragment.AllSongsFragment;
import com.example.hanh4_10.fragment.MediaPlaybackFragment;

public abstract class LayoutController implements OnSongClickListener {
    public static final String LAST_NUMBER_SONG = "last_number_song";

    protected AppCompatActivity mActivity;
    protected AllSongsFragment mAllSongsFragment;
    protected OnSongClickListener mOnclickService;

    public LayoutController(AppCompatActivity activity) {
        mActivity = activity;
    }

    //lưu trữ để truyeemf dữ liệu tu fragmnet nay sang fragment khác
    protected Bundle newBundleFromSong(SongModel song) {
        Bundle args = new Bundle();
        args.putInt(MediaPlaybackFragment.NUMBER_EXTRA, song.getNumber());
        args.putString(MediaPlaybackFragment.NAME_SONG_EXTRA, song.getNameSong());
        args.putString(MediaPlaybackFragment.AUTHOR_SONG_EXTRA, song.getAuthorSong());
        args.putString(MediaPlaybackFragment.TIME_SONG_EXTRA, song.getTimeSong());
        args.putInt(MediaPlaybackFragment.IMAGE_SONG_EXTRA, song.getImageSong());
        return args;
    }

    //lưu lại trạng thái của số bài hát hiện tại
    public void onSaveBundleLastSong(Bundle outState) {
        outState.putInt(LAST_NUMBER_SONG, mAllSongsFragment.getmCurrentNumber());
    }

    public abstract void onCreate(Bundle saveInstate, int currentSongNumber);

    public abstract void setmOnclickService(OnSongClickListener click);
}
