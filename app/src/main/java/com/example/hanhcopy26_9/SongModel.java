package com.example.hanhcopy26_9;

import java.io.Serializable;

public class SongModel implements Serializable {
     int number,imageSong;
     String nameSong, authorSong,timeSong;

     public SongModel(){}

     public SongModel(int number, String nameSong, String authorSong, int imageSong, String timeSong){
         this.authorSong = authorSong;
         this.imageSong = imageSong;
         this.timeSong= timeSong;
         this.nameSong = nameSong;
         this.number = number;
     }

    public int getNumber() {
        return number;
    }

    public String getAuthorSong() {
        return authorSong;
    }

    public int getImageSong() {
        return imageSong;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getTimeSong() {
        return timeSong;
    }

    public void setAuthorSong(String authorSong) {
        this.authorSong = authorSong;
    }

    public void setImageSong(int imageSong) {
        this.imageSong = imageSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTimeSong(String timeSong) {
        this.timeSong = timeSong;
    }

}
