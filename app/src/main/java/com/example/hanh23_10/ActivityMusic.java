package com.example.hanh23_10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.example.hanh23_10.controller.LayoutController;
import com.example.hanh23_10.controller.OneFragmentController;
import com.example.hanh23_10.controller.TowFragmentController;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;

import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener {
    private ActionBar mActionBar;
    private LayoutController mLayoutController;
    private SongGetter mSongGetter;

    //khai bao cac doi tuong service
    private MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;
    private AppCompatActivity compatActivity;
    public int mOrientation;

    private List<SongModel> mList;


    public SongGetter getSongGetter() {
        return mSongGetter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mSongGetter = new SongGetter(this);
        mList = mSongGetter.getLitSong();
        compatActivity = this;
        mOrientation = getResources().getConfiguration().orientation;

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
            setmMediaService(((MediaPlaybackService.MusicBinder) service).getService());
            //chuyen list
            mMediaService.setList(mList);
            mMusicBound = true;
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
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

    public MediaPlaybackService getmMediaService() {
        return mMediaService;
    }

    public void setmMediaService(MediaPlaybackService mMediaService) {
        this.mMediaService = mMediaService;
    }

}
