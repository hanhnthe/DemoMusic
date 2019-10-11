package com.example.hanh10_10;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //them doi tuong
    //media player
    private MediaPlayer mPlayer;
    //song list
    public List<SongModel> songs;
    //current position
    private int mCurrentSong;
    private final IBinder mMusicBind = new MusicBinder();

    private NotificationManager mNotifyManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int FOREGROUND_ID = 2;

    //oncreat cho service
    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentSong = 0;//khoi tao vi tri =0
        mPlayer = new MediaPlayer();
        initMusicPlayer();
        createNotificationChannel();
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
        startForeground(FOREGROUND_ID, buildForegroundNotification());
    }

    public Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(
                getApplicationContext(), ActivityMusic.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        RemoteViews notificationLayout = new RemoteViews(
                getPackageName(), R.layout.notification_small);
        RemoteViews notificationLayoutBig = new RemoteViews(
                getPackageName(), R.layout.notification_big);


        SongModel song = songs.get(mCurrentSong);
        notificationLayout.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setImageViewBitmap(R.id.notify_image, song.getImageSong());
        notificationLayoutBig.setTextViewText(R.id.notify_name, song.getNameSong());
        notificationLayoutBig.setTextViewText(R.id.notify_author, song.getAuthorSong());

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setCustomContentView(notificationLayout)
                        .setCustomBigContentView(notificationLayoutBig);
        return (notification.build());
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(PRIMARY_CHANNEL_ID, "Music Notification",
                            NotificationManager.IMPORTANCE_HIGH);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }

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

    public int getmCurrentSong() {
        return mCurrentSong;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    //tac dong den musiccontroller
    public int getPos() {
        return mPlayer.getCurrentPosition();
    }

    public int getDur() { //tra ve tong thoi gian bai hat
        return mPlayer.getDuration();
    }

    public Boolean isPng() {
        return mPlayer.isPlaying();
    }

    public void pausePlayer() {
        mPlayer.pause();
    }

    public void seek(int pos) {
        mPlayer.seekTo(pos);
    }

    public void go() {
        mPlayer.start();
    }

    //chuyeen tiep den bai hat tiep theo
    public void playPrev() {
        mCurrentSong--;
        playSong();
    }
}
