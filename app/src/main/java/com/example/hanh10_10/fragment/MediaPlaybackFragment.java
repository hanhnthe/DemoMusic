package com.example.hanh10_10.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hanh10_10.ActivityMusic;
import com.example.hanh10_10.MediaPlaybackService;
import com.example.hanh10_10.MusicController;
import com.example.hanh10_10.R;
import com.example.hanh10_10.SongGetter;
import com.example.hanh10_10.SongModel;
import com.example.hanh10_10.controller.LayoutController;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;


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
    private SeekBar mSeekbar;
    private TextView mProgressTime;

    private MusicController mController;
    private MediaPlaybackService mService;
    private boolean mBoundMusic;
    private ActivityMusic mActi;
    private int mMax;
    private int mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActi = (ActivityMusic) getActivity();
        mService = mActi.mMediaService;
        mBoundMusic = mActi.mMusicBound;

    }

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
        mSeekbar = (SeekBar) view.findViewById(R.id.seekBar);
        mProgressTime = (TextView) view.findViewById(R.id.startTime);

        mSeekbar.setMax(mService.getDur());
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
                mProgressTime.setText(dinhdang.format(progress));
                updateTimeSong();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.seek(mSeekbar.getProgress());
            }
        });
        updateTimeSong();


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
                    args.getString(IMAGE_SONG_EXTRA),
                    args.getBoolean(CHECKPLAY_EXTRA));
        }
    }

    public void updateUI(int number, String name, String author, String time, String image, boolean checkplay) {
        mNameSong.setText(name);
        mAuthor.setText(author);

        if (image != null) {
            mImageSong.setImageBitmap(decodeBase64(image));
            mImageBackground.setImageBitmap(decodeBase64(image));
        }

        mTime.setText(time);
        mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);

        mPlaySong.setOnClickListener(new View.OnClickListener() {
            boolean i = true;

            @Override
            public void onClick(View view) {
                if (i) {
                    mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
                    mService.go();
                    i = false;
                } else {
                    mPlaySong.setBackgroundResource(R.drawable.ic_play_22);
                    mService.pausePlayer();
                    i = true;
                }
            }
        });
    }

    //chuyen byte[] thanh bimap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    //chuyen bitmap -> byte
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
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
            lastNumberSong = args.getInt("last_music");
        }

        songGetter.setCurrentSongNumber(lastNumberSong);

        SongModel song = songGetter.getCurrentItem();
        String songString = encodeTobase64(song.getImageSong());
        updateUI(song.getNumber(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString, args.getBoolean(CHECKPLAY_EXTRA));

    }

    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
                mProgressTime.setText(dinhdang.format(mService.getPos()));
                mSeekbar.setProgress(mService.getPos());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }
}
