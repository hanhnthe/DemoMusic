package com.example.hanh21_10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.example.hanh21_10.controller.LayoutController;
import com.example.hanh21_10.controller.OneFragmentController;
import com.example.hanh21_10.controller.TowFragmentController;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;

import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener {
    private ActionBar actionBar;
    private LayoutController mLayoutController;
    private SongGetter songGetter;

    //khai bao cac doi tuong service
    public MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    public boolean mMusicBound = false;
    public AppCompatActivity compatActivity;
    public int a, b;

    private List<SongModel> mList;


    public SongGetter getSongGetter() {
        return songGetter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        songGetter = new SongGetter(this);
        mList = songGetter.getLitSong();
        compatActivity = this;
        a = getResources().getConfiguration().orientation;
        b = Configuration.ORIENTATION_PORTRAIT;
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MediaPlaybackService.class);
            startService(mPlayIntent);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(musicConnection);
    }

    @Override
    protected void onDestroy() {
        stopService(mPlayIntent);
        super.onDestroy();

    }

    //ket noi voi service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
            //get service
            mMediaService = binder.getService();
            //chuyen list
            mMediaService.setList(mList);
            mMusicBound = true;
            if (a == b) {
                // if(isPortrait){
                mLayoutController = new OneFragmentController(compatActivity);
            } else {
                mLayoutController = new TowFragmentController(compatActivity);
            }
            mLayoutController.onCreate();
            mLayoutController.setmOnclickService((OnSongClickListener) compatActivity);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClickItem(SongModel item) {
        mMediaService.setSong(item.getNumber() - 1);
        mMediaService.playSong();
    }

}
