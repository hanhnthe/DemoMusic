package com.example.hanh6_11.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hanh6_11.OnSongClickListener;
import com.example.hanh6_11.R;
import com.example.hanh6_11.SongModel;
import com.example.hanh6_11.fragment.AllSongsFragment;
import com.example.hanh6_11.fragment.FavoriteSongsFragment;
import com.example.hanh6_11.fragment.MediaPlaybackFragment;

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
        mOnclickService.onClickItem(item);
        mMediaPlayBackFragment.updateUIFromService();
        View view = mMediaPlayBackFragment.getView();
        ConstraintLayout view1 = (ConstraintLayout) view.findViewById(R.id.play);
        ImageView play = (ImageView) view1.findViewById(R.id.playSong2);
        play.setBackgroundResource(R.drawable.ic_pause_22);
        clickSongFavorite(item);
    }

    @Override
    public void setmOnclickService(OnSongClickListener click) {
        mOnclickService = click;
    }
}
