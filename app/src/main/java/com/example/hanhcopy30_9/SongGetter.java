package com.example.hanhcopy30_9;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.hanhcopy30_9.fragment.AllSongsFragment;

import java.util.ArrayList;
import java.util.List;

public class SongGetter {
    private List<SongModel> litSong = new ArrayList<>();
    private int mCurrentItemIndex;
    private Context mContext;
    // ActivityMusic activityMusic= (ActivityMusic) context;

    //set du lieu
    public SongGetter(Context context) {
        //mSong = allSongsFragment.getMp3FilesFromMemory();
        mContext = context;
        mCurrentItemIndex=0;
        // cap nhat du lieu
//        SongModel textData = new SongModel(1,"Anh ơi ở lại","Chi Pu",R.drawable.anh_oi_o_lai,"4:11",false);
//        mSong.add(textData);
//        textData = new SongModel(2,"Ai là người em thương","Quan Ap",R.drawable.ai_la_nguoi_e_thuong, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(3,"Anh nhà ở đâu thế","AMEE",R.drawable.anh_nha_o_dau_the, "4:31",false);
//        mSong.add(textData);
//        textData = new SongModel(4,"Bạc phận","jack",R.drawable.bac_phan, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(5,"Cảm giác lúc ấy sẽ ra sao","nh cs",R.drawable.cam_giac_luc_ay_se_ra_sao, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(6,"Đau để trưởng thành","Only C",R.drawable.dau_de_truong_thanh, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(7,"Đếm cừu","hana",R.drawable.dem_cuu, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(8,"Đời hư ảo","Bình",R.drawable.doi_hu_ao, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(9,"Đúng người đúng thời điểm","Thanh Hưng",R.drawable.dung_nguoi_dung_thoi_diem, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(10,"Đừng yêu nữa em mệt rồi","MIN",R.drawable.dung_yeu_nua_em_met_r, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(11,"ghen","erik",R.drawable.ghen, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(12,"Hai triệu năm","Đen",R.drawable.hai_trieu_nam, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(13,"Hồng Nhan","jack",R.drawable.hong_nhan, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(14,"Một đêm say","CS",R.drawable.mot_dem_say, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(15,"Sai người sai thời điểm","Thanh Hưng",R.drawable.sai_nguoi_sai_thoi_diem, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(16,"Sao em vô tình","Liam",R.drawable.sao_e_vo_tinh, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(17,"Sống chết có nhau","Phú lê",R.drawable.song_chet_co_nhau, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(18,"Sóng gió","Jack",R.drawable.song_gio, "4:51",false);
//        mSong.add(textData);
//        textData = new SongModel(19,"Trách ai vô tình","nh cs",R.drawable.trach_ai_vo_tinh, "4:51",false);
//        mSong.add(textData);
        getMp3FilesFromMemory();

    }


    public List<SongModel> getMp3FilesFromMemory() {
        //List<SongModel> result = new ArrayList<SongModel>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        String[] projetion = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projetion,
                selection,
                null,
                null);

//        Cursor cursorAlbums = context.getContentResolver().query(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[] {MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM},
//                MediaStore.Audio.Media.ALBUM_ID + "=?",
//                new String[]{String.valueOf(albumId)},null);
//        long albumId = cursor.getLong(
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        SongModel song;
        int i = 0;
        while (cursor.moveToNext()) {
            song = new SongModel();
            i++;
            song.setNumber(i);
            song.setNameSong(cursor.getString(0));
            song.setAuthorSong(cursor.getString(1));
            // song.setImageSong(cursor.getString(2));
            long duration = cursor.getLong(3);

            song.setTimeSong(convertDuration(duration));
            litSong.add(song);
        }
        return litSong;


    }

    public String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }

    public SongModel getNextSong(){
        mCurrentItemIndex++;
        if (mCurrentItemIndex >= litSong.size()) {
            mCurrentItemIndex = litSong.size() - 1;
        }
        return litSong.get(mCurrentItemIndex);
    }
    public int getCurrentItemIndex(){
        return mCurrentItemIndex;
    }
    public SongModel getCurrentItem(){
        return litSong.get(mCurrentItemIndex);
    }
    public boolean hasNext(){
        return mCurrentItemIndex < litSong.size() - 1;
    }
    public boolean hasPrevious(){
        return mCurrentItemIndex>0;
    }
    public int getCount(){
        return litSong.size();
    }
    public SongModel getSongAt(int pos){
        return litSong.get(pos);
    }
    public void setCurrentItemIndex(int pos){
        if(pos<0) pos=0;
        if(pos>=getCount()) pos=getCount()-1;
        mCurrentItemIndex=pos;

    }
    public void setCurrentSongNumber(int number){
        for (int i = 0; i < litSong.size(); i++) {
            SongModel song = new SongModel();
            if(number == song.getNumber()){
                mCurrentItemIndex=i;
                return;
            }
        }
    }

}
