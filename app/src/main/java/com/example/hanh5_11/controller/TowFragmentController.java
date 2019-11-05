package com.example.hanh5_11.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh5_11.OnSongClickListener;
import com.example.hanh5_11.R;
import com.example.hanh5_11.SongModel;
import com.example.hanh5_11.fragment.AllSongsFragment;
import com.example.hanh5_11.fragment.FavoriteSongsFragment;
import com.example.hanh5_11.fragment.MediaPlaybackFragment;

public class TowFragmentController extends LayoutController {
    private MediaPlaybackFragment mMediaPlayBackFragment;

    public TowFragmentController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(int i) {
        if (mActivity.findViewById(R.id.container_fragment_land) != null) {
            mMediaPlayBackFragment = new MediaPlaybackFragment();
            if (i == 1) {
                mBaseSongListFragment = new AllSongsFragment();
            } else if (i == 2) {
                mBaseSongListFragment = new FavoriteSongsFragment();
            }
            mBaseSongListFragment.setOnSongClickListener(this);
            mActivity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_1, mBaseSongListFragment).
                    replace(R.id.fagment_2, mMediaPlayBackFragment).
                    commit();
        }
    }

    @Override
    public void onClickItem(SongModel item) {
        Bundle args = newBundleFromSong(item);
        mOnclickService.onClickItem(item);
        mMediaPlayBackFragment.updateUIFromService();

        clickSongFavorite(item);
    }

    @Override
    public void setmOnclickService(OnSongClickListener click) {
        mOnclickService = click;
    }
}
