package com.example.hanh4_10.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanh4_10.OnSongClickListener;
import com.example.hanh4_10.R;
import com.example.hanh4_10.SongAdapter;
import com.example.hanh4_10.SongGetter;

public class AllSongsFragment extends Fragment {
    public final static String LAST_SONG = "last_song";

    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
   // private ActivityMusic mActivityMusic;
    private View.OnClickListener mListen;
    private SongGetter mSongGetter;
    private OnSongClickListener mOnSongClickListener;

    private int mCurrentNumber;
    public LoadCallback mLoadCallback;

    public Boolean i = false;


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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        // mSongGetter = new SongGetter(this);
        mSongGetter = new SongGetter(getContext());

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

    public SongAdapter getSongAdapter(SongGetter songGetter) {
        return new SongAdapter(songGetter);
    }

}
