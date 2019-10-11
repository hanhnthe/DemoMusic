package com.example.hanh10_10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.example.hanh10_10.controller.LayoutController;
import com.example.hanh10_10.controller.OneFragmentController;
import com.example.hanh10_10.controller.TowFragmentController;
import com.example.hanh10_10.fragment.AllSongsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.view.View;
import android.widget.MediaController;

import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener, AllSongsFragment.DataCallBack {
    ActionBar actionBar ;
    LayoutController mLayoutController;
    SongGetter songGetter;

    //khai bao cac doi tuong service
    public MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    public boolean mMusicBound = false;

    private List<SongModel> mList;

    private static final String LAST_MUSIC = "last_music";

    @Override
    protected void onStart() { // khoi dong doi tuong service khi activity khoi dong
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MediaPlaybackService.class);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
        int currentNumberSong = -1;
        if (savedInstanceState != null) {
            currentNumberSong = savedInstanceState.getInt(LAST_MUSIC);
        }
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            // if(isPortrait){
            mLayoutController = new OneFragmentController(this, mMediaService);
            mLayoutController.onCreate(savedInstanceState, currentNumberSong);
        } else{
            mLayoutController = new TowFragmentController(this, mMediaService);
            mLayoutController.onCreate(savedInstanceState, currentNumberSong);
        }
        mLayoutController.setmOnclickService(this);
        //setController();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(musicConnection);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_MUSIC, mMediaService.songs.get(mMediaService.getmCurrentSong()).getNumber());
    }

    @Override
    public void dataCallBack(List<SongModel> list) {
        mList = list;
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
    //chuyen bitmap -> byte
//    public static String encodeTobase64(Bitmap image) {
//        Bitmap immagex = image;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
//        byte[] b = baos.toByteArray();
//        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
//        return imageEncoded;
//    }

}
