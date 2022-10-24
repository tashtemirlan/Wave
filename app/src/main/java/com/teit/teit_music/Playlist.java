package com.teit.teit_music;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.ArrayList;

public class Playlist extends Activity {
    DataBasePlayList dataBasePlayList;
    ScrollView scrl;

    //set logic to click back arrow =>
    @Override
    public void onBackPressed() {
        // your code.
        Intent intent = new Intent(Playlist.this, PlaylistScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.playlistmusic);


        //get device height and width
        DisplayMetrics metrics = Playlist.this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int width_text = (int) Math.floor(width*0.8) - 90;
        //get data from our intent =>
        String tablename = getIntent().getStringExtra("name");

        scrl = findViewById(R.id.scrollview_playlist_info);

        //now we should get all records =>
        //we should work with our database =>
        dataBasePlayList =  new DataBasePlayList(Playlist.this);

        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> namePlaylistSongs = new ArrayList<>();

        //Should get all records inside =>
        dataBasePlayList.getAllRecords(namePlaylistSongs , tablename);

        Log.d("Music" , tablename);
        Log.d("Music" , String.valueOf(namePlaylistSongs));

        //set ScrollView scrolling smooth =>
        scrl.fullScroll(View.FOCUS_DOWN);
        scrl.setSmoothScrollingEnabled(true);


        //now we should display only musics which is favorite =>
        //here we should get all items which stored in downloads folder =>
        String path = Environment.getExternalStorageDirectory().toString()+"/Download";
        File directory = new File(path);
        //here we get all files in download folder
        File[] files = directory.listFiles();
        //so now we should get only mp3 files and build them=>
        if(directory.canRead() && files!=null) {
            final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musiccontainer_playlist_info);
            linearLayout.removeAllViews();
            for(File file: files){
                //get only mp3 files =>
                if(file.getName().endsWith(".mp3")){
                    String nameOurSong = file.getName().substring(0,file.getName().length()-4);

                    //so now we should get if our song inside our database =>
                    for(int i=0; i<namePlaylistSongs.size();i++){
                        if(file.getName().equals(namePlaylistSongs.get(i))){
                            //now we should create for each song his linear layout fragment
                            LinearLayout.LayoutParams maincontainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height/10);
                            LinearLayout.LayoutParams musiciconparams = new LinearLayout.LayoutParams(128,128);
                            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(width_text,LinearLayout.LayoutParams.WRAP_CONTENT);

                            //we should create Linear Layout to store our data =>
                            final LinearLayout datalayout = new LinearLayout(Playlist.this);
                            maincontainer.setMargins(0,0,0,20);
                            datalayout.setLayoutParams(maincontainer);
                            datalayout.setOrientation(LinearLayout.HORIZONTAL);
                            //probably set gravity =>
                            datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
                            datalayout.setBackgroundResource(R.drawable.child_rounded);

                            //create here music icon =>
                            ImageView musicicon = new ImageView(Playlist.this);
                            musicicon.setBackgroundResource(R.drawable.music);
                            musiciconparams.setMargins(20,0,0,0);
                            musicicon.setLayoutParams(musiciconparams);


                            //create Text which will show name of song =>
                            final TextView textView = new TextView(Playlist.this);
                            Typeface roboto = ResourcesCompat.getFont(Playlist.this, R.font.roboto_medium);
                            textView.setTypeface(roboto);
                            textView.setGravity(Gravity.START);
                            textparams.setMargins(10,0,0,0);
                            textView.setLayoutParams(textparams);
                            textView.setTextSize(18);
                            textView.setTextColor(Color.parseColor("#404241"));
                            textView.setText(nameOurSong);
                            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            textView.setHorizontalFadingEdgeEnabled(true);
                            textView.setMarqueeRepeatLimit(-1);
                            textView.setSingleLine(true);

                            //add children to our main layout =>
                            datalayout.addView(musicicon);
                            datalayout.addView(textView);

                            //after all we should add onclick listener to our music layout =>
                            datalayout.setOnClickListener(v -> {
                                //start new intent ==>
                                Intent intent = new Intent(Playlist.this,PlayListMusicScreen.class);
                                //add data to our intent = >
                                intent.putExtra("file", file.getAbsolutePath());
                                intent.putExtra("playlistname" , tablename);
                                startActivity(intent);
                                finish();
                            });
                            //add our linear layout to main linear layout =>
                            linearLayout.addView(datalayout);
                        }
                    }
                }
            }
        }
    }
}
