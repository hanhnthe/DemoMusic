package com.example.hanh16_10.fragment;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.hanh16_10.ActivityMusic;
import com.example.hanh16_10.MediaPlaybackService;
import com.example.hanh16_10.OnSongClickListener;
import com.example.hanh16_10.R;
import com.example.hanh16_10.SongAdapter;
import com.example.hanh16_10.SongGetter;
import com.example.hanh16_10.SongModel;

import java.util.List;

public class AllSongsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
    private SongGetter mSongGetter;
    public List<SongModel> mList;

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
        //setRetainInstance(true);
    }

    public void setmLoadCallback(LoadCallback mLoadCallback) {
        this.mLoadCallback = mLoadCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_music, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.myrecyclerview);
        mRecyclerview.setHasFixedSize(true);

        View view1 = view.findViewById(R.id.linearLayout3);
        name = (TextView) view1.findViewById(R.id.nameSong2);
        au = (TextView) view1.findViewById(R.id.author1);
        ima = (ImageView) view1.findViewById(R.id.image1);
        mPlay = (ImageButton) view1.findViewById(R.id.playSong1);

        mService = mActi.mMediaService;
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
            mPlay.setImageResource(R.drawable.ic_pause_1);
        }
        play();
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListen != null)
                    mListen.onClick(view);
            }
        });
        return view;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mSongGetter = mActi.getSongGetter();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mSongGetter.setCurrentSongNumber(mCurrentNumber);

                mSongAdapter = getSongAdapter(mSongGetter);
                mSongAdapter.setOnSongclickListener(mOnSongClickListener);

                mRecyclerview.setAdapter(mSongAdapter);
                mSongAdapter.notifyDataSetChanged();

                if (mLoadCallback != null) {
                    mLoadCallback.onLoadFinish(mSongGetter);
                }
            }
        }.execute();

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

    public int getmCurrentNumber() {
        return mSongGetter.getCurrentItem().getNumber();
    }

    public interface LoadCallback {
        public void onLoadFinish(SongGetter songGetter);
    }

    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter);
    }

}
