package com.example.hanhcopy26_9.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hanhcopy26_9.ActivityMusic;
import com.example.hanhcopy26_9.R;


public class DetailSongFragment extends Fragment  {
    private static final String LOG_TAG =
            ActivityMusic.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_song,container,false);

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        ActivityMusic activityMusic= (ActivityMusic) getActivity();
        activityMusic.getSupportActionBar().show();

    }


}
