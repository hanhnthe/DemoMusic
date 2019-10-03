package com.example.hanhcopy30_9;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.hanhcopy30_9.controller.LayoutController;
import com.example.hanhcopy30_9.controller.OneFragmentController;
import com.example.hanhcopy30_9.controller.TowFragmentController;
import com.example.hanhcopy30_9.fragment.AllSongsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMusic extends AppCompatActivity {
    ActionBar actionBar ;
    LayoutController mLayoutController;
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

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mLayoutController.onSaveBundleLastSong(outState);
    }

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



}
