package com.example.hanh21_10.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanh21_10.OnSongClickListener;
import com.example.hanh21_10.R;
import com.example.hanh21_10.SongModel;
import com.example.hanh21_10.fragment.AllSongsFragment;
import com.example.hanh21_10.fragment.MediaPlaybackFragment;

public class TowFragmentController extends LayoutController {
    private MediaPlaybackFragment mMediaPlayBackFragment;

    public TowFragmentController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate() {
        if (mActivity.findViewById(R.id.container_fragment_land) != null) {
            mMediaPlayBackFragment = new MediaPlaybackFragment();

            mAllSongsFragment = new AllSongsFragment();
            mAllSongsFragment.setOnSongClickListener(this);
            mActivity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_1, mAllSongsFragment).
                    replace(R.id.fagment_2, mMediaPlayBackFragment).
                    commit();
        }
    }

    @Override
    public void onClickItem(SongModel item) {
        Bundle args = newBundleFromSong(item);
        mOnclickService.onClickItem(item);
        mMediaPlayBackFragment.updateUIFromService();
    }

    @Override
    public void setmOnclickService(OnSongClickListener click) {
        mOnclickService = click;
    }
}
