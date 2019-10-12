package com.example.hanh10_10.fragment;


import android.content.Context;
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

import com.example.hanh10_10.ActivityMusic;
import com.example.hanh10_10.MediaPlaybackService;
import com.example.hanh10_10.OnSongClickListener;
import com.example.hanh10_10.R;
import com.example.hanh10_10.SongAdapter;
import com.example.hanh10_10.SongGetter;
import com.example.hanh10_10.SongModel;

import java.util.List;

public class AllSongsFragment extends Fragment {

    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
    private SongGetter mSongGetter;
    public List<SongModel> mList;

    private View.OnClickListener mListen;
    private OnSongClickListener mOnSongClickListener;
    private DataCallBack mDataBack;

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
        mService = mActi.mMediaService;
    }

    public void setmLoadCallback(LoadCallback mLoadCallback) {
        this.mLoadCallback = mLoadCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.list_music,container,false);
       mRecyclerview = view.findViewById(R.id.myrecyclerview);
       mRecyclerview.setHasFixedSize(true);
        View view1 = view.findViewById(R.id.linearLayout3);
        name = (TextView) view1.findViewById(R.id.nameSong2);
        au = (TextView) view1.findViewById(R.id.author1);
        ima = (ImageView) view1.findViewById(R.id.image1);
        mPlay = (ImageButton) view1.findViewById(R.id.playSong1);
        if (mService != null) {
            if (mService.isPng()) {
                mPlay.setImageResource(R.drawable.ic_play_1);
            } else mPlay.setImageResource(R.drawable.ic_pause_1);
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
        } else mPlay.setImageResource(R.drawable.ic_play_1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListen!= null)
                mListen.onClick(view);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        int last_music = -1;
        if (args != null) {
            last_music = args.getInt("last_music");
            mSongGetter.setCurrentSongNumber(last_music);
            SongModel song = mSongGetter.getCurrentItem();
            name.setText(song.getNameSong());
            au.setText(song.getAuthorSong());
            ima.setImageBitmap(song.getImageSong());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DataCallBack) {
            mDataBack = (DataCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + "must implement DataCallBack");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        // mSongGetter = new SongGetter(this);
        mSongGetter = new SongGetter(getContext());
        mList = mSongGetter.getLitSong();
        mDataBack.dataCallBack(mList);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mSongGetter.setCurrentSongNumber(mCurrentNumber);

                mSongAdapter = getSongAdapter(mSongGetter);
                mSongAdapter.setOnSongclickListener(mOnSongClickListener);

                mRecyclerview.setAdapter(mSongAdapter);

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


    public void setOnClickListener(View.OnClickListener listener){
        this.mListen = listener;
    }

    public int getmCurrentNumber() {
        return mSongGetter.getCurrentItem().getNumber();
    }

    public interface LoadCallback {
        public void onLoadFinish(SongGetter songGetter);
    }

    public interface DataCallBack {
        public void dataCallBack(List<SongModel> list);
    }

    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter);
    }

}
