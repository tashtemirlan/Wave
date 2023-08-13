package com.teit.teit_music;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import java.util.Locale;

public class FavoriteScreen extends Activity {
    NavigationBarView navigationBarView;
    DataBaseHelp dataBaseHelp;
    ScrollView scrl;
    TextView welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.favorites);

        String previousMusic = getIntent().getStringExtra("SongName");

        TextView textMainFavoritetoRussian = findViewById(R.id.TextWelcomeFavoriteScreen);
        if(Locale.getDefault().getLanguage()=="ru"){
            textMainFavoritetoRussian.setText("Ваша любимая музыка");
        }

        //declare intents
        final Intent intent_to_home = new Intent(com.teit.teit_music.FavoriteScreen.this,HomeScreen.class);
        intent_to_home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        final Intent intent_to_playlist = new Intent(com.teit.teit_music.FavoriteScreen.this,PlaylistScreen.class);
        intent_to_playlist.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

        //get device height and width
        DisplayMetrics metrics = FavoriteScreen.this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        navigationBarView = findViewById(R.id.bottom_navigation_favorites);
        welcome = findViewById(R.id.TextWelcomeFavoriteScreen);
        scrl = findViewById(R.id.scrollView_favorite);


        navigationBarView.setItemIconTintList(null);
        navigationBarView.setSelectedItemId(R.id.page_2);


        navigationBarView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()){

                case R.id.page_1:
                    startActivity(intent_to_home);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.page_2:
                    break;
                case R.id.page_3:
                    startActivity(intent_to_playlist);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
            }

            return true;
        });

        //now we should get all records =>
        //we should work with our database =>
        dataBaseHelp =  new DataBaseHelp(FavoriteScreen.this);

        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> nameFavoriteSongs = new ArrayList<>();

        //Should get all records inside =>
        dataBaseHelp.getAllRecords(nameFavoriteSongs);

        //so now we have all our Favorite songs inside our arraylist =>
        //get height of welcome text
        welcome.measure(0,0);
        int height_welcome = welcome.getMeasuredHeight();
        //get height of navigation bar =>
        Resources resources = FavoriteScreen.this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int navigationheight = FavoriteScreen.this.getResources().getDimensionPixelSize(resourceId);
        int scrl_height = height - height_welcome -navigationheight-40;

        int width_text = (int) Math.floor(width*0.8) - 90;

        ConstraintLayout.LayoutParams scroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, scrl_height);
        scroll.topToBottom = welcome.getId();
        scroll.setMargins(0,10,0,0);
        scrl.setLayoutParams(scroll);
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
            final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musiccontainerFavoriteScreen);
            linearLayout.removeAllViews();
            for(File file: files){
                //get only mp3 files =>
                if(file.getName().endsWith(".mp3")){
                    String nameOurSong = file.getName().substring(0,file.getName().length()-4);

                    //so now we should get if our song inside our database =>
                    for(int i=0; i<nameFavoriteSongs.size();i++){
                        if(file.getName().equals(nameFavoriteSongs.get(i))){
                            //should give that this song is our favorite =>
                            //now we should create for each song his linear layout fragment
                            LinearLayout.LayoutParams maincontainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height/10);
                            LinearLayout.LayoutParams musiciconparams = new LinearLayout.LayoutParams(128,128);
                            LinearLayout.LayoutParams favoriteiconparams = new LinearLayout.LayoutParams(96,96);
                            LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(width_text,LinearLayout.LayoutParams.WRAP_CONTENT);

                            //we should create Linear Layout to store our data =>
                            final LinearLayout datalayout = new LinearLayout(FavoriteScreen.this);
                            maincontainer.setMargins(0,0,0,20);
                            datalayout.setLayoutParams(maincontainer);
                            datalayout.setOrientation(LinearLayout.HORIZONTAL);
                            //probably set gravity =>
                            datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
                            datalayout.setBackgroundResource(R.drawable.child_rounded);

                            //create here music icon =>
                            ImageView musicicon = new ImageView(FavoriteScreen.this);
                            musicicon.setBackgroundResource(R.drawable.music);
                            musiciconparams.setMargins(20,0,0,0);
                            musicicon.setLayoutParams(musiciconparams);


                            //create Text which will show name of song =>
                            final TextView textView = new TextView(FavoriteScreen.this);
                            Typeface roboto = ResourcesCompat.getFont(FavoriteScreen.this, R.font.roboto_medium);
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
                                Intent intent = new Intent(FavoriteScreen.this,FavoriteMusicScreen.class);
                                //add data to our intent = >
                                intent.putExtra("file", file.getAbsolutePath());
                                intent.putExtra("isFav", true);
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

        //scroll to =>
        if(previousMusic!=null){
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musiccontainerFavoriteScreen);
                    int ScrollingTo =0;
                    for(int i=0; i <linearLayout.getChildCount();i++){
                        View viewChild = linearLayout.getChildAt(i);
                        if(viewChild instanceof LinearLayout){
                            LinearLayout childLinear = (LinearLayout) viewChild;
                            for(int j=0; j<childLinear.getChildCount();j++){
                                View childOfChildView = childLinear.getChildAt(j);
                                if(childOfChildView instanceof TextView){
                                    TextView tv = (TextView) childOfChildView;
                                    String tvText = tv.getText().toString();
                                    if(tvText.equals(previousMusic)){
                                        ScrollingTo = childLinear.getTop();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    scrl.smoothScrollTo(0,ScrollingTo);
                }
            },1000);
        }
    }
}
