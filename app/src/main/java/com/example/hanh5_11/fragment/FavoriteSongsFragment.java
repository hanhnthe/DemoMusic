package com.example.hanh5_11.fragment;

import com.example.hanh5_11.SongAdapter;
import com.example.hanh5_11.SongGetter;

public class FavoriteSongsFragment extends BaseSongListFragment {
    @Override
    public SongGetter songGetter() {
        return mActi.getSongGetter(2);
    }

    @Override
    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter, 2);
    }
}
