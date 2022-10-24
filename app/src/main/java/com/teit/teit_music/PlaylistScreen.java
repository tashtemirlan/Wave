package com.teit.teit_music;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PlaylistScreen extends Activity {
    NavigationBarView navigationBarView;
    Button btn1;
    ScrollView scrl;
    TextView tv1;
    DataBasePlaylists dataBasePlaylists;

    //set logic to click back arrow =>
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.playlist);


        //declare intents
        final Intent intent_to_home = new Intent(com.teit.teit_music.PlaylistScreen.this,HomeScreen.class);
        intent_to_home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        final Intent intent_to_favorite = new Intent(com.teit.teit_music.PlaylistScreen.this,FavoriteScreen.class);
        intent_to_favorite.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

        //get device height and width
        DisplayMetrics metrics = PlaylistScreen.this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int width_text = (int) Math.floor(width*0.8) - 120;

        navigationBarView = findViewById(R.id.bottom_navigation_playlist);
        scrl = findViewById(R.id.scrollView_playlist);
        tv1 = findViewById(R.id.TextWelcomePlaylistScreen);
        btn1 = findViewById(R.id.floating_action_button);

        navigationBarView.setItemIconTintList(null);
        navigationBarView.setSelectedItemId(R.id.page_3);


        navigationBarView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()){

                case R.id.page_1:
                    startActivity(intent_to_home);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.page_2:
                    startActivity(intent_to_favorite);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.page_3:
                    break;
            }

            return true;
        });
        //here appear magic =>
        //get height of text1 =>
        tv1.measure(0,0);
        int height_text1 = tv1.getMeasuredHeight();
        btn1.measure(0,0);
        int height_fb = btn1.getMeasuredHeight();
        //get height of navigation bar =>
        Resources resources = PlaylistScreen.this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int navigationheight = PlaylistScreen.this.getResources().getDimensionPixelSize(resourceId);
        int height_scroll = height - height_text1 - height_fb -navigationheight -height/20;

        //set scrollview height
        ConstraintLayout.LayoutParams scroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, height_scroll);
        scroll.topToBottom = tv1.getId();
        scrl.setLayoutParams(scroll);
        //set ScrollView scrolling smooth =>
        scrl.fullScroll(View.FOCUS_DOWN);
        scrl.setSmoothScrollingEnabled(true);


        //we should work with our database =>
        dataBasePlaylists =  new DataBasePlaylists(PlaylistScreen.this);

        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> namePlaylists = new ArrayList<>();

        //Should get all records inside =>
        //so now we have all our Favorite songs inside our arraylist =>
        dataBasePlaylists.getAllRecords(namePlaylists);

        //so after we get all records from playlists we should create them =>
        final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.playlistcontainer);
        linearLayout.removeAllViews();
        for(int i=0; i< namePlaylists.size();i++){
            //here we should create all music playlists =>
            //now we should create for each song his linear layout fragment
            LinearLayout.LayoutParams maincontainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height/10);
            LinearLayout.LayoutParams musiciconparams = new LinearLayout.LayoutParams(128,128);
            LinearLayout.LayoutParams deleteiconparams = new LinearLayout.LayoutParams(96,96);
            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(width_text,LinearLayout.LayoutParams.WRAP_CONTENT);
            //we should create Linear Layout to store our data =>
            LinearLayout datalayout = new LinearLayout(PlaylistScreen.this);
            maincontainer.setMargins(0,0,0,20);
            datalayout.setLayoutParams(maincontainer);
            datalayout.setOrientation(LinearLayout.HORIZONTAL);
            //probably set gravity =>
            datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
            datalayout.setBackgroundResource(R.drawable.shape_rectangle);
            //create here music icon =>
            ImageView musicicon = new ImageView(PlaylistScreen.this);
            musicicon.setBackgroundResource(R.drawable.library);
            musiciconparams.setMargins(20,0,0,0);
            musicicon.setLayoutParams(musiciconparams);
            //create here music icon =>
            ImageView deleteicon = new ImageView(PlaylistScreen.this);
            deleteicon.setBackgroundResource(R.drawable.remove);
            deleteiconparams.setMargins(5,0,10,0);
            deleteicon.setLayoutParams(deleteiconparams);

            //create Text which will show name of song =>
            TextView textView = new TextView(PlaylistScreen.this);
            Typeface roboto = ResourcesCompat.getFont(PlaylistScreen.this, R.font.roboto_medium);
            textView.setTypeface(roboto);
            textView.setGravity(Gravity.START);
            textparams.setMargins(10,0,0,0);
            textView.setLayoutParams(textparams);
            textView.setTextSize(18);
            textView.setTextColor(Color.rgb(255,255,255));
            textView.setText(namePlaylists.get(i));
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setHorizontalFadingEdgeEnabled(true);
            textView.setMarqueeRepeatLimit(-1);
            textView.setSingleLine(true);

            deleteicon.setOnClickListener(view -> {
                DataBasePlayList dataBasePlayList = new DataBasePlayList(PlaylistScreen.this);
                //delete that from view =>
                linearLayout.removeView(datalayout);
                dataBasePlaylists.deletePlaylists(String.valueOf(textView.getText()));
                //should delete all database =>
                SQLiteDatabase db = dataBasePlayList.getWritableDatabase();
                dataBasePlayList.Destroy(db);
            });

            //add children to our main layout =>
            datalayout.addView(musicicon);
            datalayout.addView(textView);
            datalayout.addView(deleteicon);
            datalayout.setOnClickListener(v -> {
                Intent intent = new Intent(PlaylistScreen.this, Playlist.class);
                intent.putExtra("name", String.valueOf(textView.getText()));
                startActivity(intent);
                finish();
            });
            //add our linear layout to main linear layout =>
            linearLayout.addView(datalayout);
        }

        //get if our plus button was touched =>
        btn1.setOnClickListener(view -> {
            //to do when we click to create new playlist =>
            //start new intent ==>
            Intent intent = new Intent(PlaylistScreen.this,MusicPlayListDialoge.class);
            startActivity(intent);
            finish();
        });
    }
}
