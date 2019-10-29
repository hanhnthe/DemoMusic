package com.example.hanh29_10.fragment;


import com.example.hanh29_10.SongGetter;

public class AllSongsFragment extends BaseSongListFragment {
    @Override
    public SongGetter songGetter() {
        return (mActi.getSongGetter(1));
    }
}
