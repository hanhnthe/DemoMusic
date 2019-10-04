package com.example.hanh4_10;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.hanh4_10.controller.LayoutController;
import com.example.hanh4_10.controller.OneFragmentController;
import com.example.hanh4_10.controller.TowFragmentController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityMusic extends AppCompatActivity implements OnSongClickListener {
    ActionBar actionBar ;
    LayoutController mLayoutController;
    SongGetter songGetter;// = new SongGetter();

    //khai bao cac doi tuong service
    private MediaPlaybackService mMediaService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;

    private List<SongModel> list = new ArrayList<>();//songGetter.getMp3FilesFromMemory() ;//nhaps

    @Override
    protected void onStart() { // khoi dong doi tuong service khi activity khoi dong
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MediaPlaybackService.class);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        int currentNumberSong = -1;
        if (savedInstanceState != null) {
            currentNumberSong = savedInstanceState.getInt(LayoutController.LAST_NUMBER_SONG);
        }

        //actionBar.hide();
        // boolean isPortrait = getResources().getBoolean(R.bool.isPortrait);

       if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
           // if(isPortrait){
           mLayoutController = new OneFragmentController(this);
           mLayoutController.onCreate(savedInstanceState, currentNumberSong);
        } else{
           mLayoutController = new TowFragmentController(this);
           mLayoutController.onCreate(savedInstanceState, currentNumberSong);

       }
        mLayoutController.setmOnclickService(this);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mLayoutController.onSaveBundleLastSong(outState);
    }

    //ket noi voi service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder) service;
            //get service
            mMediaService = binder.getService();
            //chuyen list
            mMediaService.setList(list);
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicBound = false;

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickItem(SongModel item) {
        Toast.makeText(this, "hnhanh" + item.getNumber(), Toast.LENGTH_SHORT).show();
//        mMediaService.setSong(item.getNumber());
//        mMediaService.playSong();

    }
}
