package com.example.hanh6_11.fragment;


import com.example.hanh6_11.SongAdapter;
import com.example.hanh6_11.SongGetter;
import com.example.hanh6_11.SongModel;

public class AllSongsFragment extends BaseSongListFragment {
    @Override
    public SongGetter songGetter() {
        return (mActi.getSongGetter(1));
    }

    @Override
    public SongAdapter getSongAdapter(SongGetter songGetter) {
        SongAdapter songAdapter = new SongAdapter(songGetter, 1);
        songAdapter.setmCallback(new SongAdapter.callBackActivity() {
            @Override
            public void add(SongModel song) {
                mActi.addSongFavorite(song);
            }

            @Override
            public void remove(SongModel song) {
                mActi.removeSongFavorite(song);
            }
        });
        return songAdapter;
    }
}
