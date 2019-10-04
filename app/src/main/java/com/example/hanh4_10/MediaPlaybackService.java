package com.example.hanh4_10;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.List;

public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //them doi tuong
    //media player
    private MediaPlayer mPlayer;
    //song list
    private List<SongModel> songs;
    //current position
    private int mCurrentSong;
    private final IBinder mMusicBind = new MusicBinder();

    //oncreat cho service


    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentSong = 1;//khoi tao vi tri =0
        mPlayer = new MediaPlayer();
        initMusicPlayer();

    }

    //phuong thuc khoi tao lop mediaplayer
    public void initMusicPlayer() {
        //cau hinh phat nhac bang cach thiet lap thuoc tinh
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // thiet lap onprepare khi doi tuong mediaplayre duoc chuan bi
        mPlayer.setOnPreparedListener(this);
        //thiet lap khi bai hat da phat xong
        mPlayer.setOnCompletionListener(this);
        //thiet lap khi say ra loi
        mPlayer.setOnErrorListener(this);
    }

    //methos truyen danh sach cac bai hat tu activity
    public void setList(List<SongModel> songs) {
        this.songs = songs;
    }

    //them binder de tuong tac voi activity
    public class MusicBinder extends Binder {
        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    public IBinder onBind(Intent arg0) {
        return mMusicBind;
    }

    //giai phong tai nguyen khi doi tuong service khong duoc lien ket
    //thu thi khi nguoi dung thoat khoi ung dung,-> ngung service
   /* @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mPlayer.release();
        return false;
    }*/

    //thiet lap de play 1 song
    public void playSong() {
        mPlayer.reset();
        SongModel playSong = songs.get(mCurrentSong);//get song
        long idSong = playSong.getId();//get id
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idSong);
        //thiet lap uri nay lam nguoi du lieu doi tuong mediaplayer
        try {
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //start playback
        mediaPlayer.start();

    }

    //thiet lap bai hat hien tai duoc chon
    public void setSong(int songIndex) {
        mCurrentSong = songIndex;//su dung khi nguoi dung chon 1 bai hat tu danh sach
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
