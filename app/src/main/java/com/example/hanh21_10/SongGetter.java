package com.example.hanh21_10;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SongGetter {
    private List<SongModel> litSong = new ArrayList<>();
    private int mCurrentItemIndex;
    private Context mContext;

    //set du lieu
    public SongGetter(Context context) {
        mContext = context;
        mCurrentItemIndex = -1;
        getMp3FilesFromMemory();
    }

    public List<SongModel> getLitSong() {
        return litSong;
    }

    public List<SongModel> getMp3FilesFromMemory() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projetion = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID,};
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projetion,
                selection,
                null,
                null);
        SongModel song;
        int i = 0;
        while (cursor.moveToNext()) {
            song = new SongModel();
            i++;
            song.setId((int) cursor.getLong(4));
            song.setNumber(i);
            song.setNameSong(cursor.getString(0));
            song.setAuthorSong(cursor.getString(1));
            long image = cursor.getLong(2);
            if (getAlbumart(image) != null) {
                song.setImageSong(getAlbumart(image));
            } else {
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.mac_dinh);
                song.setImageSong(icon);
            }

            long duration = cursor.getLong(3);
            SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
            song.setTimeSong(dinhdang.format(duration));
            litSong.add(song);
        }
        cursor.close();
        return litSong;
    }

    public Bitmap getAlbumart(long album_id) {
        Bitmap bm = null;
        try {
            final Uri artWorkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(artWorkUri, album_id);
            ParcelFileDescriptor pfd = mContext.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    public int getCurrentItemIndex(){
        return mCurrentItemIndex;
    }

    public void setmCurrentItemIndex(int mCurrentItemIndex) {
        this.mCurrentItemIndex = mCurrentItemIndex;
    }

    public SongModel getCurrentItem(){
        return litSong.get(mCurrentItemIndex);
    }
    public int getCount(){
        return litSong.size();
    }
    public SongModel getSongAt(int pos){
        return litSong.get(pos);
    }
    public void setCurrentItemIndex(int pos){
        if (pos < 0) {
            pos = 0;
        }
        if (pos >= getCount()) {
            pos = getCount() - 1;
        }
        mCurrentItemIndex = pos;
    }
    public void setCurrentSongNumber(int number){
        for (int i = 0; i < litSong.size(); i++) {
            SongModel song = litSong.get(i);
            if(number == song.getNumber()){
                mCurrentItemIndex=i;
                String name = song.getNameSong();
                return;
            }
        }
    }

}
