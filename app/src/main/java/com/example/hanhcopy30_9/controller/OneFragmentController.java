package com.example.hanhcopy30_9.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanhcopy30_9.R;
import com.example.hanhcopy30_9.SongModel;
import com.example.hanhcopy30_9.fragment.AllSongsFragment;
import com.example.hanhcopy30_9.fragment.MediaPlaybackFragment;

public class OneFragmentController extends LayoutController implements View.OnClickListener {
    private Bundle mBundle;//khai bao luu tru gia tri hien tai

    AllSongsFragment allSongsFragment = new AllSongsFragment();

    public OneFragmentController(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(Bundle saveInstate, int currentSongNumber) {
        if (mActivity.findViewById(R.id.container_fragment) != null) {

            allSongsFragment.setOnClickListener(this);
            allSongsFragment.setOnSongClickListener(this);
            mActivity.getSupportFragmentManager().beginTransaction().
                    replace(R.id.container_fragment, allSongsFragment).commit();


        }

    }

    @Override
    public void onClickItem(final SongModel item) {
        View view = allSongsFragment.getView();

        item.setCheckPlay(true);//set bai hat dang duoc choi
        mBundle = newBundleFromSong(item);// khoi tao bien bunlde vao item click

        String name, author;
        int image;
        name = item.getNameSong();
        author = item.getAuthorSong();
        image = item.getImageSong();


        TextView nameSong2 = (TextView) view.findViewById(R.id.nameSong2);
        nameSong2.setText(name);

        TextView authorSong = (TextView) view.findViewById(R.id.author1);
        authorSong.setText(author);

        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
        imageView.setImageResource(image);

        final ImageButton button1 = (ImageButton) view.findViewById(R.id.playSong1);
        button1.setImageResource(R.drawable.ic_pause_1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (item.getCheckPlay()) {
                    button1.setImageResource(R.drawable.ic_play_1);
                    item.setCheckPlay(false);
                } else {
                    button1.setImageResource(R.drawable.ic_pause_1);
                    item.setCheckPlay(true);
                }

            }
        });


    }

    @Override
    public void onClick(View view) {
        mActivity.getSupportActionBar().hide();
        MediaPlaybackFragment mediaPlaybackFragment = new MediaPlaybackFragment();
        mediaPlaybackFragment.setArguments(mBundle);//truyen du lieu sang fragment mediaplay
        mActivity.getSupportFragmentManager().beginTransaction().
                replace(R.id.container_fragment, mediaPlaybackFragment).
                addToBackStack(null).commit();
    }
}
