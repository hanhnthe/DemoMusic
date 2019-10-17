package com.example.hanh17_10.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanh17_10.ActivityMusic;
import com.example.hanh17_10.MediaPlaybackService;
import com.example.hanh17_10.OnSongClickListener;
import com.example.hanh17_10.R;
import com.example.hanh17_10.SongAdapter;
import com.example.hanh17_10.SongGetter;
import com.example.hanh17_10.SongModel;

import java.util.List;

public class AllSongsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
    private SongGetter mSongGetter;

    private View.OnClickListener mListen;
    private OnSongClickListener mOnSongClickListener;

    private int mCurrentNumber;
    public LoadCallback mLoadCallback;

    private TextView name, au;
    private ImageView ima;
    private ImageButton mPlay;

    private MediaPlaybackService mService;
    private ActivityMusic mActi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActi = (ActivityMusic) getActivity();

    }

    public void setmLoadCallback(LoadCallback mLoadCallback) {
        this.mLoadCallback = mLoadCallback;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_music, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.myrecyclerview);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mSongGetter = mActi.getSongGetter();
        mSongGetter.setCurrentSongNumber(mCurrentNumber);
        mSongAdapter = getSongAdapter(mSongGetter);
        mSongAdapter.setOnSongclickListener(mOnSongClickListener);
        mRecyclerview.setAdapter(mSongAdapter);
        mSongAdapter.notifyDataSetChanged();
        if (mLoadCallback != null) {
            mLoadCallback.onLoadFinish(mSongGetter);
        }
        mRecyclerview.setHasFixedSize(true);

        anhxaViewSmallDetail(view);
        mService = mActi.mMediaService;
        bundlerSongSmallDetail();
        play();

        return view;
    }

    public void anhxaViewSmallDetail(View view) {
        View view1 = view.findViewById(R.id.linearLayout3);
        name = (TextView) view1.findViewById(R.id.nameSong2);
        au = (TextView) view1.findViewById(R.id.author1);
        ima = (ImageView) view1.findViewById(R.id.image1);
        mPlay = (ImageButton) view1.findViewById(R.id.playSong1);
        //set click view small detail de chuyen fragment
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListen != null)
                    mListen.onClick(view);
            }
        });
    }

    public void play() {
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.isPng()) {
                    mPlay.setImageResource(R.drawable.ic_play_1);
                    mService.pausePlayer();
                } else {
                    mPlay.setImageResource(R.drawable.ic_pause_1);
                    mService.go();
                }
            }
        });
    }

    public void bundlerSongSmallDetail() {
        if (mService != null) {
            SongModel item1 = mService.getSongCurrent();
            String name1, author;
            Bitmap image;
            name1 = item1.getNameSong();
            author = item1.getAuthorSong();
            image = item1.getImageSong();
            name.setText(name1);
            au.setText(author);
            ima.setImageBitmap(image);
            if (mService.isPng()) {
                mPlay.setImageResource(R.drawable.ic_pause_1);
            } else {
                mPlay.setImageResource(R.drawable.ic_play_1);
            }

        }
    }

    public void setOnSongClickListener(OnSongClickListener onSongClickListener) {
        mOnSongClickListener = onSongClickListener;
        if (mSongAdapter != null) {
            mSongAdapter.setOnSongclickListener(onSongClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.mListen = listener;
    }

    public interface LoadCallback {
        public void onLoadFinish(SongGetter songGetter);
    }

    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter);
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
                    bundlerSongSmallDetail();
                    mPlay.setImageResource(R.drawable.ic_pause_1);
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
        getActivity().unregisterReceiver(receiver);
    }
}
