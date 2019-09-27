package com.example.hanhcopy26_9;

import java.util.ArrayList;

public class SongGetter {
    private ArrayList<SongModel> mSong;
    private int mCurrentItemIndex;

    //set du lieu
    public SongGetter(){
        mSong = new ArrayList<SongModel>();
        mCurrentItemIndex=0;
        // cap nhat du lieu
        SongModel textData = new SongModel(1,"Anh ơi ở lại","Chi Pu",R.drawable.anh_oi_o_lai,"4:11");
        mSong.add(textData);
        textData = new SongModel(2,"Ai là người em thương","Quan Ap",R.drawable.ai_la_nguoi_e_thuong, "4:51");
        mSong.add(textData);
        textData = new SongModel(3,"Anh nhà ở đâu thế","AMEE",R.drawable.anh_nha_o_dau_the, "4:31");
        mSong.add(textData);
        textData = new SongModel(4,"Bạc phận","jack",R.drawable.bac_phan, "4:51");
        mSong.add(textData);
        textData = new SongModel(5,"Cảm giác lúc ấy sẽ ra sao","nh cs",R.drawable.cam_giac_luc_ay_se_ra_sao, "4:51");
        mSong.add(textData);
        textData = new SongModel(6,"Đau để trưởng thành","Only C",R.drawable.dau_de_truong_thanh, "4:51");
        mSong.add(textData);
        textData = new SongModel(7,"Đếm cừu","hana",R.drawable.dem_cuu, "4:51");
        mSong.add(textData);
        textData = new SongModel(8,"Đời hư ảo","Bình",R.drawable.doi_hu_ao, "4:51");
        mSong.add(textData);
        textData = new SongModel(9,"Đúng người đúng thời điểm","Thanh Hưng",R.drawable.dung_nguoi_dung_thoi_diem, "4:51");
        mSong.add(textData);
        textData = new SongModel(10,"Đừng yêu nữa em mệt rồi","MIN",R.drawable.dung_yeu_nua_em_met_r, "4:51");
        mSong.add(textData);
        textData = new SongModel(11,"ghen","erik",R.drawable.ghen, "4:51");
        mSong.add(textData);
        textData = new SongModel(12,"Hai triệu năm","Đen",R.drawable.hai_trieu_nam, "4:51");
        mSong.add(textData);
        textData = new SongModel(13,"Hồng Nhan","jack",R.drawable.hong_nhan, "4:51");
        mSong.add(textData);
        textData = new SongModel(14,"Một đêm say","CS",R.drawable.mot_dem_say, "4:51");
        mSong.add(textData);
        textData = new SongModel(15,"Sai người sai thời điểm","Thanh Hưng",R.drawable.sai_nguoi_sai_thoi_diem, "4:51");
        mSong.add(textData);
        textData = new SongModel(16,"Sao em vô tình","Liam",R.drawable.sao_e_vo_tinh, "4:51");
        mSong.add(textData);
        textData = new SongModel(17,"Sống chết có nhau","Phú lê",R.drawable.song_chet_co_nhau, "4:51");
        mSong.add(textData);
        textData = new SongModel(18,"Sóng gió","Jack",R.drawable.song_gio, "4:51");
        mSong.add(textData);
        textData = new SongModel(19,"Trách ai vô tình","nh cs",R.drawable.trach_ai_vo_tinh, "4:51");
        mSong.add(textData);

    }

    public SongModel getNextSong(){
        mCurrentItemIndex++;
        if(mCurrentItemIndex>=mSong.size()){
            mCurrentItemIndex=mSong.size()-1;
        }
        return mSong.get(mCurrentItemIndex);
    }
    public int getCurrentItemIndex(){
        return mCurrentItemIndex;
    }
    public SongModel getCurrentItem(){
        return mSong.get(mCurrentItemIndex);
    }
    public boolean hasNext(){
        return mCurrentItemIndex<mSong.size()-1;
    }
    public boolean hasPrevious(){
        return mCurrentItemIndex>0;
    }
    public int getCount(){
        return mSong.size();
    }
    public SongModel getSongAt(int pos){
        return mSong.get(pos);
    }
    public void setCurrentItemIndex(int pos){
        if(pos<0) pos=0;
        if(pos>=getCount()) pos=getCount()-1;
        mCurrentItemIndex=pos;

    }
    public void setCurrentSongNumber(int number){
        for(int i=0; i<mSong.size(); i++){
            SongModel song = new SongModel();
            if(number == song.getNumber()){
                mCurrentItemIndex=i;
                return;
            }
        }
    }

}
