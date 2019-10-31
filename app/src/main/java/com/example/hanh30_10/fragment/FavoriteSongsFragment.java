package com.example.hanh30_10.fragment;

import com.example.hanh30_10.SongGetter;

public class FavoriteSongsFragment extends BaseSongListFragment {
    @Override
    public SongGetter songGetter() {
        return mActi.getSongGetter(2);
    }
}
