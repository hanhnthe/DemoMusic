package com.example.hanh21_10.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hanh21_10.ActivityMusic;
import com.example.hanh21_10.MediaPlaybackService;
import com.example.hanh21_10.R;
import com.example.hanh21_10.SongGetter;
import com.example.hanh21_10.SongModel;
import com.example.hanh21_10.sqlite.FavoriteSongProvider;
import com.example.hanh21_10.sqlite.SongsFavoriteTable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;


public class MediaPlaybackFragment extends Fragment implements AllSongsFragment.LoadCallback {
    public static final String NUMBER_EXTRA = "number";
    public static final String NAME_SONG_EXTRA = "namesong";
    public static final String AUTHOR_SONG_EXTRA = "authorsong";
    public static final String IMAGE_SONG_EXTRA = "imagesong";
    public static final String TIME_SONG_EXTRA = "timesong";

    private TextView mNameSong, mAuthor, mTime;
    private ImageView mImageSong, mImageBackground;
    private ImageView mPlaySong;
    private SeekBar mSeekbar;
    private TextView mProgressTime;
    private ImageButton mNext, mPrev, mLike, mDisLike, mList, mShuffle, mRepeat;

    private MediaPlaybackService mService;
    private boolean mBoundMusic;
    private ActivityMusic mActi;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActi = (ActivityMusic) getActivity();
        mService = mActi.mMediaService;
        mBoundMusic = mActi.mMusicBound;
        setRetainInstance(true);

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
        mNext = (ImageButton) view.findViewById(R.id.rightButton);
        mPrev = (ImageButton) view.findViewById(R.id.leftButton);
        mLike = (ImageButton) view.findViewById(R.id.likeButton);
        mDisLike = (ImageButton) view.findViewById(R.id.disLikeButton);
        mList = (ImageButton) view.findViewById(R.id.listSong);
        mShuffle = (ImageButton) view.findViewById(R.id.shufflebutton);
        mRepeat = (ImageButton) view.findViewById(R.id.repeatbutton);

        updateUIFromService();
        seekbarChange();
        next();
        pre();
        shuffle();
        repeat();
        likeAndDis();
        listComeBack();
        updateTimeSong();
        return view;
    }

    public void updateUIFromService() {
        SongModel song = mService.songs.get(mService.getmCurrentSong());
        String songString = encodeTobase64(song.getImageSong());
        updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
        mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
    }


    @SuppressLint("Recycle")
    public void updateUI(int id, String name, String author, String time, String image) {
        if (mSeekbar != null && mNameSong != null && mAuthor != null && mImageBackground != null
                && mImageBackground != null
                && mTime != null && mPlaySong != null && mLike != null) {
            mSeekbar.setMax(mService.getDur());
            mNameSong.setText(name);
            mAuthor.setText(author);
            if (image != null) {
                mImageSong.setImageBitmap(decodeBase64(image));
                mImageBackground.setImageBitmap(decodeBase64(image));
            }
            mTime.setText(time);
            play();
            mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
            String[] projetion = {SongsFavoriteTable.ID_PROVIDER};
            if (getActivity().getContentResolver().query(Uri.parse(SongsFavoriteTable.TABLE_NAME), projetion,
                    SongsFavoriteTable.ID_PROVIDER + " = ?",
                    new String[]{String.valueOf(id)}, null) != null) {
                mLike.setBackgroundResource(R.drawable.ic_like_click);
            } else {
                mLike.setBackgroundResource(R.drawable.ic_like);
            }
        }

    }

    public void play() {
        if (mService.isPng()) {
            mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
        } else {
            mPlaySong.setBackgroundResource(R.drawable.ic_play_22);
        }
        mPlaySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService.isPng()) {
                    mPlaySong.setBackgroundResource(R.drawable.ic_play_22);
                    mService.pausePlayer();
                } else {
                    mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
                    mService.go();
                }
            }
        });
    }

    public void seekbarChange() {
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.seek(mSeekbar.getProgress());
            }
        });
    }

    public void next() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.playNext();
                SongModel song = mService.songs.get(mService.getmCurrentSong());
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);

            }
        });
    }

    public void pre() {
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.playPrev();
                SongModel song = mService.songs.get(mService.getmCurrentSong());
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
            }
        });
    }

    public void shuffle() {
        mShuffle.setOnClickListener(new View.OnClickListener() {
            boolean i = true;

            @Override
            public void onClick(View v) {
                if (i) {
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_click);
                    mService.shuffeSong();
                    i = false;
                } else {
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_white);
                    mService.shuffeSong();
                    i = true;
                }
            }
        });
    }

    public void repeat() {
        mRepeat.setOnClickListener(new View.OnClickListener() {
            int i = 1;

            @Override
            public void onClick(View v) {
                if (i == 1) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_click);
                    i = 2;
                } else if (i == 2) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_click_one_song);
                    mService.repeatSong();
                    i = 3;
                } else if (i == 3) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat);
                    i = 1;
                }
            }
        });
    }

    public void likeAndDis() {
        mLike.setOnClickListener(new View.OnClickListener() {
            boolean i = true;

            @Override
            public void onClick(View v) {
                if (i) {
                    mLike.setBackgroundResource(R.drawable.ic_like_click);
                    insertSong();
                    i = false;

                } else {
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like);
                    mLike.setBackgroundResource(R.drawable.ic_like);
                    i = true;
                }
            }
        });
        mDisLike.setOnClickListener(new View.OnClickListener() {
            boolean i = true;

            @Override
            public void onClick(View v) {
                if (i) {
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like_click);
                    i = false;
                } else {
                    mLike.setBackgroundResource(R.drawable.ic_like);
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like);
                    i = true;
                }
            }
        });
    }

    public void listComeBack() {
        if (mList != null) {
            mList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActi.getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    //insert vao slqite
    public void insertSong() {
        Toast.makeText(getActivity().getBaseContext(),
                "jashdjsa", Toast.LENGTH_LONG).show();
        if (mService != null) {
            SongModel song = mService.songs.get(mService.getmCurrentSong());
            ContentValues values = new ContentValues();
            values.put(SongsFavoriteTable.KEY_NAME, song.getNameSong());
            values.put(SongsFavoriteTable.KEY_AUTHOR, song.getAuthorSong());
            values.put(SongsFavoriteTable.IS_FAVORITE, 2);
            values.put(SongsFavoriteTable.ID_PROVIDER, song.getId());
            Uri uri = getActivity().getContentResolver().insert(
                    FavoriteSongProvider.CONTENT_URI, values);
            Toast.makeText(getActivity().getBaseContext(),
                    "Đẫ thêm bài hát " + song.getNameSong() + " vào yêu thich" + uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //giao dien quay ngang
    @Override
    public void onLoadFinish(SongGetter songGetter) {
        SongModel song = mService.songs.get(mService.getmCurrentSong());
        String songString = encodeTobase64(song.getImageSong());
        updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
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

    public String ACTION = "my_action";
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        //code thi hanh khi receiver nhan dc intent
        @Override
        public void onReceive(Context context, Intent intent) {
            //kiem tra intent
            if (intent.getAction().equals(ACTION)) {
                //doc du lieu tu intent
                Boolean change = intent.getBooleanExtra("my_key", true);
                if (change) {
                    SongModel song = mService.songs.get(mService.getmCurrentSong());
                    String songString = encodeTobase64(song.getImageSong());
                    updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        activityMusic.getSupportActionBar().show();
        getActivity().unregisterReceiver(receiver);
    }

}

