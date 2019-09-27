package com.example.hanhcopy26_9.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanhcopy26_9.ActivityMusic;
import com.example.hanhcopy26_9.OnSongClickListener;
import com.example.hanhcopy26_9.R;
import com.example.hanhcopy26_9.SongAdapter;
import com.example.hanhcopy26_9.SongGetter;
import com.example.hanhcopy26_9.SongModel;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends Fragment implements OnSongClickListener {

    private static final String LOG_TAG =
            ActivityMusic.class.getSimpleName();
    private RecyclerView mRecyclerview;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayout;
   // private ActivityMusic mActivityMusic;
    private View.OnClickListener mListen;
    private SongGetter mSongGetter;
    private int mCurrentNumber;


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
       // view.findViewById(R.id.linearLayout3).setVisibility(View.GONE);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayout = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayout);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());

        mSongGetter = new SongGetter();
        mSongGetter.setCurrentSongNumber(mCurrentNumber);
        mSongAdapter = new SongAdapter(mSongGetter);
        mSongAdapter.setOnclickListener(this);

        mRecyclerview.setAdapter(mSongAdapter);
        Log.d(LOG_TAG, "onActivityCreated()");

    }

    public void setOnClickListener(View.OnClickListener listener){
        this.mListen = listener;
    }




    public  void onClickItem(SongModel song){
        View view = this.getView();
       // view.findViewById(R.id.linearLayout3).setVisibility(View.VISIBLE);
        String name, author;
        int image;
        name = song.getNameSong();
        author = song.getAuthorSong();
        image = song.getImageSong();

        TextView nameSong2 = (TextView) view.findViewById(R.id.nameSong2);
        nameSong2.setText(name);

        TextView authorSong = (TextView) view.findViewById(R.id.author1);
        authorSong.setText(author);

        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
        imageView.setImageResource(image);

    }
}
