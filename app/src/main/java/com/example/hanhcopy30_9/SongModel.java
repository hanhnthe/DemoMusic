package com.example.hanhcopy30_9;

import java.io.Serializable;

public class SongModel implements Serializable {
     int number,imageSong;
     String nameSong, authorSong,timeSong;
    Boolean checkPlay;

     public SongModel(){}

    public SongModel(int number, String nameSong, String authorSong, int imageSong, String timeSong, Boolean checkPlay) {
        this.authorSong = authorSong;
        this.imageSong = imageSong;
        this.timeSong= timeSong;
        this.nameSong = nameSong;
        this.number = number;
        this.checkPlay = checkPlay;
    }

    public SongModel(String nameSong, String authorSong, int imageSong, String timeSong, Boolean checkPlay) {
        this.authorSong = authorSong;
        this.imageSong = imageSong;
        this.timeSong = timeSong;
        this.nameSong = nameSong;
        this.checkPlay = checkPlay;

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

    public Boolean getCheckPlay() {
        return checkPlay;
    }

    public void setCheckPlay(Boolean checkPlay) {
        this.checkPlay = checkPlay;
    }
}
