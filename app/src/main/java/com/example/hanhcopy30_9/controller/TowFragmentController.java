package com.example.hanhcopy30_9.controller;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanhcopy30_9.R;
import com.example.hanhcopy30_9.SongModel;
import com.example.hanhcopy30_9.fragment.AllSongsFragment;
import com.example.hanhcopy30_9.fragment.MediaPlaybackFragment;

public class TowFragmentController extends LayoutController {
    private MediaPlaybackFragment mMediaPlayBackFragment;

    public TowFragmentController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(Bundle saveInstate, int currentSongNumber) {
        if (mActivity.findViewById(R.id.container_fragment_land) != null) {
            mMediaPlayBackFragment = new MediaPlaybackFragment();
            Bundle args = new Bundle();
            args.putInt(LAST_NUMBER_SONG, currentSongNumber);
            mMediaPlayBackFragment.setArguments(args);

            mAllSongsFragment = new AllSongsFragment();
            mAllSongsFragment.setOnSongClickListener(this);
            mAllSongsFragment.setmLoadCallback(mMediaPlayBackFragment);


            mActivity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_1, mAllSongsFragment).
                    replace(R.id.fagment_2, mMediaPlayBackFragment).
                    commit();
//            mActivity.findViewById(R.id.linearLayout3).setVisibility(View.GONE);

        }

    }

    @Override
    public void onClickItem(SongModel item) {
        Bundle args = newBundleFromSong(item);
        mMediaPlayBackFragment.update(args);
    }
}
