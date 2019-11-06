package com.example.hanh6_11.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.hanh6_11.ActivityMusic;
import com.example.hanh6_11.MediaPlaybackService;
import com.example.hanh6_11.R;
import com.example.hanh6_11.SongGetter;
import com.example.hanh6_11.SongModel;
import com.example.hanh6_11.sqlite.FavoriteSongProvider;
import com.example.hanh6_11.sqlite.SongsFavoriteTable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;


public class MediaPlaybackFragment extends Fragment implements BaseSongListFragment.LoadCallback {
    public static final String NUMBER_EXTRA = "number";
    public static final String NAME_SONG_EXTRA = "namesong";
    public static final String AUTHOR_SONG_EXTRA = "authorsong";
    public static final String IMAGE_SONG_EXTRA = "imagesong";
    public static final String TIME_SONG_EXTRA = "timesong";
    private final static String SONG = " song";
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";

    private TextView mNameSong, mAuthor, mTime;
    private ImageView mImageSong, mImageBackground;
    private ImageView mPlaySong;
    private SeekBar mSeekbar;
    private TextView mProgressTime;
    private ImageButton mNext, mPrev, mLike, mDisLike, mList, mShuffle, mRepeat;

    private MediaPlaybackService mService;
    private ActivityMusic mActi;
    private int mSave;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActi = (ActivityMusic) getActivity();
        mService = mActi.getmMediaService();
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
        if (mService != null) {
            SongModel song = mService.findSongFromId();
            String songString = encodeTobase64(song.getImageSong());
            play();
            updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
        }

    }

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
            Cursor cursor = findSongById(id);
            if (cursor != null && cursor.moveToFirst()) {
                if (cursor.getInt(0) == 1) {
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like_click);
                } else if (cursor.getInt(0) == 2) {
                    mLike.setBackgroundResource(R.drawable.ic_like_click);
                } else {
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like);
                    mLike.setBackgroundResource(R.drawable.ic_like);
                }
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
                    mService.updatePlayNotification();
                } else {
                    mSave = mService.getmSavePlay();
                    if (mSave == 0) {
                        mService.playSong();
                        mSave++;
                        mService.setmSavePlay(mSave);
                    } else {
                        mService.go();
                    }

                    mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
                    mService.getmNotifyManager().notify(mService.FOREGROUND_ID, mService.buildForegroundNotification());
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
                SongModel song = mService.findSongFromId();
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
            }
        });
    }

    public void pre() {
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.playPrev();
                SongModel song = mService.findSongFromId();
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                mPlaySong.setBackgroundResource(R.drawable.ic_pause_22);
            }
        });
    }

    public void shuffle() {
        boolean shuffle = readShuffleShare();
        if (shuffle) {
            mShuffle.setBackgroundResource(R.drawable.ic_shuffle_white);
            if (mService != null) {
                mService.shuffeSong();
            }
        } else {
            mShuffle.setBackgroundResource(R.drawable.ic_shuffle_click);
            if (mService != null) {
                mService.shuffeSong();
            }
        }
        mShuffle.setOnClickListener(new View.OnClickListener() {
            boolean i = readShuffleShare();
            @Override
            public void onClick(View v) {
                if (i) {
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_click);
                    mService.shuffeSong();
                    i = false;
                    saveStateShuffe(i);
                } else {
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_white);
                    mService.shuffeSong();
                    i = true;
                    saveStateShuffe(i);
                }
            }
        });
    }

    public void repeat() {
        int i = readRepeatShare();
        if (i == 1) {
            mRepeat.setBackgroundResource(R.drawable.ic_repeat);
        } else if (i == 2) {
            mRepeat.setBackgroundResource(R.drawable.ic_repeat_click);
        } else if (i == 3) {
            mRepeat.setBackgroundResource(R.drawable.ic_repeat_click_one_song);
            if (mService != null) {
                mService.repeatSong();
            }
        }
        mRepeat.setOnClickListener(new View.OnClickListener() {
            int i = readRepeatShare();
            @Override
            public void onClick(View v) {
                if (i == 1) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_click);
                    i = 2;
                    saveStateRepeat(i);
                } else if (i == 2) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_click_one_song);
                    mService.repeatSong();
                    i = 3;
                    saveStateRepeat(3);
                } else if (i == 3) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat);
                    i = 1;
                    saveStateRepeat(1);
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
                    likeSong();
                    i = false;

                } else {
                    dislikeSong();
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
                    dislikeSong();
                    mLike.setBackgroundResource(R.drawable.ic_like);
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like_click);
                    i = false;
                } else {
                    mDisLike.setBackgroundResource(R.drawable.ic_dis_like);
                    i = true;
                }
            }
        });
    }

    private void dislikeSong() {
        if (mService != null) {
            SongModel song = mService.findSongFromId();
            ContentValues values = new ContentValues();
            values.put(SongsFavoriteTable.ID_PROVIDER, song.getId());
            values.put(SongsFavoriteTable.IS_FAVORITE, 1);
            Cursor cursor = findSongById(song.getId());
            if (cursor != null && cursor.moveToFirst()) {
                getActivity().getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                        "id_provider = \"" + song.getId() + "\"", null);
            } else {
                getActivity().getContentResolver().insert(FavoriteSongProvider.CONTENT_URI, values);
            }
            Toast.makeText(getActivity().getBaseContext(),
                    "Đẫ xoá bài hát " + song.getNameSong() + " khỏi yêu thich", Toast.LENGTH_LONG).show();
        }
    }

    private void likeSong() {
        if (mService != null) {
            SongModel song = mService.findSongFromId();
            ContentValues values = new ContentValues();
            values.put(SongsFavoriteTable.ID_PROVIDER, song.getId());
            values.put(SongsFavoriteTable.IS_FAVORITE, 2);
            Cursor cursor = findSongById(song.getId());
            if (cursor != null && cursor.moveToFirst()) {
                getActivity().getContentResolver().update(FavoriteSongProvider.CONTENT_URI, values,
                        "id_provider = \"" + song.getId() + "\"", null);
            } else {
                getActivity().getContentResolver().insert(FavoriteSongProvider.CONTENT_URI, values);
            }
            Toast.makeText(getActivity().getBaseContext(),
                    "Đẫ thêm bài hát " + song.getNameSong() + " vào yêu thich", Toast.LENGTH_LONG).show();
        }
    }

    // tim kiem theo id cua bai hat
    public Cursor findSongById(int id) {
        return getActivity().getContentResolver().query(FavoriteSongProvider.CONTENT_URI, new String[]{SongsFavoriteTable.IS_FAVORITE},
                SongsFavoriteTable.ID_PROVIDER + "=?",
                new String[]{String.valueOf(id)}, null);
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

    //giao dien quay ngang
    @Override
    public void onLoadFinish(SongGetter songGetter) {
        SongModel song = mService.findSongFromId();
        String songString = encodeTobase64(song.getImageSong());
        updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
    }

    private void updateTimeSong() {
        if (mService != null) {
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

    //chuyen byte[] thanh bimap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    //chuyen bitmap -> byte
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (immagex != null) {
            immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            return imageEncoded;
        } else {
            return null;
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        //code thi hanh khi receiver nhan dc intent
        @Override
        public void onReceive(Context context, Intent intent) {
            //kiem tra intent
            if (intent.getAction().equals(MediaPlaybackService.ACTION)) {
                //doc du lieu tu intent
                Boolean change = intent.getBooleanExtra(MediaPlaybackService.MY_KEY, true);
                if (change) {
                    SongModel song = mService.findSongFromId();
                    String songString = encodeTobase64(song.getImageSong());
                    updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(MediaPlaybackService.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityMusic activityMusic = (ActivityMusic) getActivity();
        activityMusic.getSupportActionBar().show();
        getActivity().unregisterReceiver(receiver);
    }

    public void saveStateShuffe(boolean shuffleState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SONG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHUFFLE, shuffleState);
        editor.apply();
    }

    public void saveStateRepeat(int repeatState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SONG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(REPEAT, repeatState);
        editor.apply();
    }

    public Boolean readShuffleShare() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SONG, Context.MODE_PRIVATE);
        boolean i = true;
        if (sharedPreferences != null) {
            i = sharedPreferences.getBoolean(SHUFFLE, true);
        }
        return i;
    }

    public int readRepeatShare() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SONG, Context.MODE_PRIVATE);
        int i = 1;
        if (sharedPreferences != null) {
            i = sharedPreferences.getInt(REPEAT, 1);
        }
        return i;
    }

}
