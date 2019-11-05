package com.example.hanh5_11.sqlite;

public class SongDataFavorite {
    private int id_provider;
    private int id;
    private int is_favorite;
    private int count_of_play;

    public int getId() {
        return id;
    }

    public int getCount_of_play() {
        return count_of_play;
    }

    public int getId_provider() {
        return id_provider;
    }

    public int getIs_favorite() {
        return is_favorite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCount_of_play(int count_of_play) {
        this.count_of_play = count_of_play;
    }

    public void setId_provider(int id_provider) {
        this.id_provider = id_provider;
    }

    public void setIs_favorite(int is_favorite) {
        this.is_favorite = is_favorite;
    }
}
