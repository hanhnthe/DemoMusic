package com.example.hanh5_11.fragment;


import com.example.hanh5_11.SongAdapter;
import com.example.hanh5_11.SongGetter;

public class AllSongsFragment extends BaseSongListFragment {
    @Override
    public SongGetter songGetter() {
        return (mActi.getSongGetter(1));
    }

    @Override
    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter, 1);
    }
}
