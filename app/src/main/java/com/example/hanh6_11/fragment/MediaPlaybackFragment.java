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
import com.example.hanh6_11.SongModel;
import com.example.hanh6_11.favoritedatabase.FavoriteSongProvider;
import com.example.hanh6_11.favoritedatabase.SongsFavoriteTable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;


public class MediaPlaybackFragment extends Fragment {
    public static final String SHUFFLE = "shuffle";
    public static final String REPEAT = "repeat";
    public static final String CALLBACKALL = "callbackall";
    public static final String CHECKCALLBACK = "checkcallback";

    private TextView mNameSongTxt, mAuthorTxt, mTimeTxt;
    private ImageView mImageSongImgV, mImageBackgroundImgV;
    private ImageView mPlaySongImgV;
    private SeekBar mSeekbar;
    private TextView mProgressTimeTxt;
    private ImageButton mNextImg, mPrevImg, mLikeImg, mDisLikeImg, mListImg, mShuffleImg, mRepeatImg;

    private MediaPlaybackService mService;
    private ActivityMusic mActi;
    private int mSave;
    private boolean mChangeCallBackAll= false;


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

        mNameSongTxt = (TextView) view.findViewById(R.id.nameSong3);
        mAuthorTxt = (TextView) view.findViewById(R.id.author2);
        mTimeTxt = (TextView) view.findViewById(R.id.maxTime);
        mImageSongImgV = (ImageView) view.findViewById(R.id.image2);
        mImageBackgroundImgV = (ImageView) view.findViewById(R.id.manhinh);
        ConstraintLayout view1 = (ConstraintLayout) view.findViewById(R.id.play);
        mPlaySongImgV = (ImageView) view1.findViewById(R.id.playSong2);
        mSeekbar = (SeekBar) view.findViewById(R.id.seekBar);
        mProgressTimeTxt = (TextView) view.findViewById(R.id.startTime);
        mNextImg = (ImageButton) view.findViewById(R.id.rightButton);
        mPrevImg = (ImageButton) view.findViewById(R.id.leftButton);
        mLikeImg = (ImageButton) view.findViewById(R.id.likeButton);
        mDisLikeImg = (ImageButton) view.findViewById(R.id.disLikeButton);
        mListImg = (ImageButton) view.findViewById(R.id.listSong);
        mShuffleImg = (ImageButton) view.findViewById(R.id.shufflebutton);
        mRepeatImg = (ImageButton) view.findViewById(R.id.repeatbutton);

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
        if (mSeekbar != null && mNameSongTxt != null && mAuthorTxt != null && mImageBackgroundImgV != null
                && mImageBackgroundImgV != null
                && mTimeTxt != null && mPlaySongImgV != null && mLikeImg != null) {
            mSeekbar.setMax(mService.getDur());
            mNameSongTxt.setText(name);
            mAuthorTxt.setText(author);
            if (image != null) {
                mImageSongImgV.setImageBitmap(decodeBase64(image));
                mImageBackgroundImgV.setImageBitmap(decodeBase64(image));
            }
            mTimeTxt.setText(time);
            play();
            Cursor cursor = findSongById(id);
            if (cursor != null && cursor.moveToFirst()) {
                if (cursor.getInt(0) == 1) {
                    mDisLikeImg.setBackgroundResource(R.drawable.ic_dis_like_click);
                } else if (cursor.getInt(0) == 2) {
                    mLikeImg.setBackgroundResource(R.drawable.ic_like_click);
                } else {
                    mDisLikeImg.setBackgroundResource(R.drawable.ic_dis_like);
                    mLikeImg.setBackgroundResource(R.drawable.ic_like);
                }
            }
        }
    }

    public void play() {
        if (mService.isPng()) {
            mPlaySongImgV.setBackgroundResource(R.drawable.ic_pause_22);
        } else {
            mPlaySongImgV.setBackgroundResource(R.drawable.ic_play_22);
        }

        mPlaySongImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService.isPng()) {
                    mPlaySongImgV.setBackgroundResource(R.drawable.ic_play_22);
                    mService.pausePlayer();
                    mService.updatePlayNotification();
                } else {
                    mSave = mService.getmSavePlay();
                    if (mSave == 0) {
                        mService.playSong();
                        mSave++;
                        mChangeCallBackAll = true;
                        sendCallBackAll();
                        mChangeCallBackAll=false;
                        mService.setmSavePlay(mSave);
                    } else {
                        mService.go();
                    }
                    mPlaySongImgV.setBackgroundResource(R.drawable.ic_pause_22);
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
        mNextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.playNext();
                mChangeCallBackAll = true;
                sendCallBackAll();
                mChangeCallBackAll=false;
                SongModel song = mService.findSongFromId();
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                mPlaySongImgV.setBackgroundResource(R.drawable.ic_pause_22);
            }
        });
    }

    public void pre() {
        mPrevImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.playPrev();
                mChangeCallBackAll = true;
                sendCallBackAll();
                mChangeCallBackAll=false;
                SongModel song = mService.findSongFromId();
                String songString = encodeTobase64(song.getImageSong());
                updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                mPlaySongImgV.setBackgroundResource(R.drawable.ic_pause_22);
            }
        });
    }

    public void shuffle() {
        boolean shuffle = readShuffleShare();
        if (!shuffle) {
            mShuffleImg.setBackgroundResource(R.drawable.ic_shuffle_white);
            if (mService != null) {
                mService.shuffle();
            }
        } else {
            mShuffleImg.setBackgroundResource(R.drawable.ic_shuffle_click);
            if (mService != null) {
                mService.shuffle();
            }
        }
        mShuffleImg.setOnClickListener(new View.OnClickListener() {
            boolean i = readShuffleShare();
            @Override
            public void onClick(View v) {
                if (!i) {
                    mShuffleImg.setBackgroundResource(R.drawable.ic_shuffle_click);
                    i = true;
                    saveStateShuffe(i);
                    mService.shuffle();
                } else {
                    mShuffleImg.setBackgroundResource(R.drawable.ic_shuffle_white);
                    i = false;
                    saveStateShuffe(i);
                    mService.shuffle();
                }
            }
        });
    }

    public void repeat() {
        int i = readRepeatShare();
        if (i == 1) {
            mRepeatImg.setBackgroundResource(R.drawable.ic_repeat);
            if (mService != null) {
                mService.repeat();
            }
        } else if (i == 2) {
            mRepeatImg.setBackgroundResource(R.drawable.ic_repeat_click);
            if (mService != null) {
                mService.repeat();
            }
        } else if (i == 3) {
            mRepeatImg.setBackgroundResource(R.drawable.ic_repeat_click_one_song);
            if (mService != null) {
                mService.repeat();
            }
        }
        mRepeatImg.setOnClickListener(new View.OnClickListener() {
            int i = readRepeatShare();
            @Override
            public void onClick(View v) {
                if (i == 1) {
                    mRepeatImg.setBackgroundResource(R.drawable.ic_repeat_click);
                    i = 2;
                    saveStateRepeat(i);
                    mService.repeat();
                } else if (i == 2) {
                    mRepeatImg.setBackgroundResource(R.drawable.ic_repeat_click_one_song);
                    mService.repeat();
                    i = 3;
                    saveStateRepeat(3);
                    mService.repeat();
                } else if (i == 3) {
                    mRepeatImg.setBackgroundResource(R.drawable.ic_repeat);
                    mService.repeat();
                    i = 1;
                    saveStateRepeat(1);
                    mService.repeat();
                }
            }
        });
    }

    public void likeAndDis() {
        mLikeImg.setOnClickListener(new View.OnClickListener() {
            boolean i = true;
            @Override
            public void onClick(View v) {
                if (i) {
                    mLikeImg.setBackgroundResource(R.drawable.ic_like_click);
                    likeSong();
                    i = false;

                } else {
                    dislikeSong();
                    mDisLikeImg.setBackgroundResource(R.drawable.ic_dis_like);
                    mLikeImg.setBackgroundResource(R.drawable.ic_like);
                    i = true;
                }
            }
        });
        mDisLikeImg.setOnClickListener(new View.OnClickListener() {
            boolean i = true;
            @Override
            public void onClick(View v) {
                if (i) {
                    dislikeSong();
                    mLikeImg.setBackgroundResource(R.drawable.ic_like);
                    mDisLikeImg.setBackgroundResource(R.drawable.ic_dis_like_click);
                    i = false;
                } else {
                    mDisLikeImg.setBackgroundResource(R.drawable.ic_dis_like);
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

    public void listComeBack() {
        if (mListImg != null) {
            mListImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActi.getSupportFragmentManager().popBackStack();
                }
            });
        }
    }

    // tim kiem theo id cua bai hat
    public Cursor findSongById(int id) {
        return getActivity().getContentResolver().query(FavoriteSongProvider.CONTENT_URI, new String[]{SongsFavoriteTable.IS_FAVORITE},
                SongsFavoriteTable.ID_PROVIDER + "=?",
                new String[]{String.valueOf(id)}, null);
    }


    public void sendCallBackAll(){
        Intent intent = new Intent();
        intent.setAction(CALLBACKALL);
        intent.putExtra(CHECKCALLBACK,mChangeCallBackAll);
        mService.sendBroadcast(intent);
    }

    private void updateTimeSong() {
        if (mService != null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
                    mProgressTimeTxt.setText(dinhdang.format(mService.getPos()));
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
                int isplaying = intent.getIntExtra(MediaPlaybackService.ISPLAYING,0);
                if (change && isplaying==0 && mService!=null) {
                    SongModel song = mService.findSongFromId();
                    String songString = encodeTobase64(song.getImageSong());
                    updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                }else if(change && isplaying==1 && mService != null){
                    SongModel song = mService.findSongFromId();
                    String songString = encodeTobase64(song.getImageSong());
                    updateUI(song.getId(), song.getNameSong(), song.getAuthorSong(), song.getTimeSong(), songString);
                    mPlaySongImgV.setBackgroundResource(R.drawable.ic_pause_22);
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MediaPlaybackService.SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHUFFLE, shuffleState);
        editor.apply();
    }

    public void saveStateRepeat(int repeatState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MediaPlaybackService.SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(REPEAT, repeatState);
        editor.apply();
    }

    public Boolean readShuffleShare() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MediaPlaybackService.SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        boolean i = true;
        if (sharedPreferences != null) {
            i = sharedPreferences.getBoolean(SHUFFLE, true);
        }
        return i;
    }

    public int readRepeatShare() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MediaPlaybackService.SONGSHAREPREFERENCE, Context.MODE_PRIVATE);
        int i = 1;
        if (sharedPreferences != null) {
            i = sharedPreferences.getInt(REPEAT, 1);
        }
        return i;
    }
}

