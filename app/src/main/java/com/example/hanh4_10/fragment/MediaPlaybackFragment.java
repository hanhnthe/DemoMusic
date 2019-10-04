package com.example.hanh4_10.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hanh4_10.ActivityMusic;
import com.example.hanh4_10.R;
import com.example.hanh4_10.SongGetter;
import com.example.hanh4_10.SongModel;
import com.example.hanh4_10.controller.LayoutController;


public class MediaPlaybackFragment extends Fragment implements AllSongsFragment.LoadCallback {
    public static final String NUMBER_EXTRA = "number";
    public static final String NAME_SONG_EXTRA = "namesong";
    public static final String AUTHOR_SONG_EXTRA = "authorsong";
    public static final String IMAGE_SONG_EXTRA = "imagesong";
    public static final String TIME_SONG_EXTRA = "timesong";
    public static final String CHECKPLAY_EXTRA = "checkplay";

    private TextView mNameSong, mAuthor, mTime;
    private ImageView mImageSong, mImageBackground;
    private ImageView mPlaySong;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_song, container, false);

        mNameSong = (TextView) view.findViewById(R.id.nameSong3);
        mAuthor = (TextView) view.findViewById(R.id.author2);
        mTime = (TextView) view.findViewById(R.id.maxTime);
        mImageSong = (ImageView) view.findViewById(R.id.image2);
        mImageBackground = (ImageView) view.findViewById(R.id.manhinh);
        ConstraintLayout view1 = (ConstraintLayout) view.findViewById(R.id.play);
        mPlaySong = (ImageView) view1.findViewById(R.id.playSong2);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        update(args);
    }

    public void update(Bundle args) {
        if (args != null) {
            updateUI(args.getInt(NUMBER_EXTRA),
                    args.getString(NAME_SONG_EXTRA),
                    args.getString(AUTHOR_SONG_EXTRA),
                    args.getString(TIME_SONG_EXTRA),
                    args.getInt(IMAGE_SONG_EXTRA),
                    args.getBoolean(CHECKPLAY_EXTRA));
        }
    }

    public void updateUI(int number, String name, String author, String time, int image, boolean checkplay) {
        mNameSong.setText(name);
        mAuthor.setText(author);
        mImageSong.setImageResource(image);
        mImageBackground.setBackgroundResource(image);
        mTime.setText(time);
        mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);

        mPlaySong.setOnClickListener(new View.OnClickListener() {
            boolean i = true;

            @Override
            public void onClick(View view) {
                if (i) {
                    mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
                    i = false;
                } else {
                    mPlaySong.setBackgroundResource(R.drawable.ic_play_22);
                    i = true;
                }
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        activityMusic.getSupportActionBar().show();
    }

    //giao dien quay ngang
    @Override
    public void onLoadFinish(SongGetter songGetter) {
        Bundle args = getArguments();

        int lastNumberSong = -1;
        if (args != null) {
            lastNumberSong = args.getInt(LayoutController.LAST_NUMBER_SONG);
        }

        songGetter.setCurrentSongNumber(lastNumberSong);

        SongModel song = songGetter.getCurrentItem();
        updateUI(song.getNumber(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), song.getImageSong(), args.getBoolean(CHECKPLAY_EXTRA));


    }
}
