package com.teit.teit_music;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class HomeScreen extends Activity{
        NavigationBarView navigationBarView;
        TextView tx1 , tx2;
        TextInputEditText textInputLayout;
        TextInputLayout textinplay;
        ScrollView scrl;
        ConstraintLayout constraintLayout;

        //set logic to click back arrow =>
        @Override
        public void onBackPressed() {
            // your code.
            //do nothing
        }

        //For future store data and get data to set favorites =>
        DataBaseHelp dataBaseHelp;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.home);

            String previousMusic = getIntent().getStringExtra("SongName");


            //declare intents
            final Intent intent_to_favorite = new Intent(HomeScreen.this,FavoriteScreen.class);
            intent_to_favorite.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            final Intent intent_to_playlist = new Intent(HomeScreen.this,PlaylistScreen.class);
            intent_to_playlist.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            final Intent intent_to_search = new Intent(HomeScreen.this,FindMusicScreen.class);
            intent_to_playlist.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

            //get device height and width
            DisplayMetrics metrics = HomeScreen.this.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            int width_text = (int) Math.floor(width*0.8) - 112;


            //define our variables
            navigationBarView = findViewById(R.id.bottom_navigation_home);
            tx1 = findViewById(R.id.Text1);
            tx2 = findViewById(R.id.Text2);
            textInputLayout = findViewById(R.id.textinput_home);
            textinplay = findViewById(R.id.textField_home);
            scrl = findViewById(R.id.scrollView_home);
            constraintLayout = findViewById(R.id.constraint_home);


            if(Locale.getDefault().getLanguage()=="ru"){
                tx1.setText("Приветствую!");
                tx2.setText("Наслаждайтесь вашей любимой музыкой");
                textinplay.setHint("Поиск музыки");
                navigationBarView.getMenu().clear();
                navigationBarView.inflateMenu(R.menu.bottom_menu_rus);
            }

            //we should work with our database =>
            dataBaseHelp =  new DataBaseHelp(HomeScreen.this);

            //create arraylist where we will get all records from our lovely database =>
            ArrayList<String> nameFavoriteSongs = new ArrayList<>();

            //Should get all records inside =>
            dataBaseHelp.getAllRecords(nameFavoriteSongs);

            //so now we have all our Favorite songs inside our arraylist =>


            //we should open another activity when user click on find text edit layout
            textInputLayout.setOnClickListener(view -> {
                startActivity(intent_to_search);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });

            navigationBarView.setItemIconTintList(null);
            navigationBarView.setSelectedItemId(R.id.page_1);

            navigationBarView.setOnItemSelectedListener(item ->{

                switch (item.getItemId()){
                    case R.id.page_1:
                        break;
                    case R.id.page_2:
                        startActivity(intent_to_favorite);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        break;
                    case R.id.page_3:
                        startActivity(intent_to_playlist);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        break;
                }
                return true;
            });


            //set ScrollView scrolling smooth =>
            scrl.fullScroll(View.FOCUS_DOWN);
            scrl.setSmoothScrollingEnabled(true);


            //here we should get all items which stored in downloads folder =>
            String path = Environment.getExternalStorageDirectory().toString()+"/Download";
            File directory = new File(path);
            //here we get all files in download folder
            File[] files = directory.listFiles();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(files, Comparator.comparing(File::getName).reversed());
            }
            //so now we should get only mp3 files and build them=>
            if(directory.canRead() && files!=null) {
                final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musiccontainer);
                linearLayout.removeAllViews();
                for(File file: files){
                    //get only mp3 files =>
                    if(file.getName().endsWith(".mp3")){
                        //define that we don't have any favorite song =>
                        boolean isFavorite = false;

                        String nameOurSong = file.getName().substring(0,file.getName().length()-4);

                        //so now we should get if our song inside our database =>
                        for(int i=0; i<nameFavoriteSongs.size();i++){
                            if(file.getName().equals(nameFavoriteSongs.get(i))){
                                //should give that this song is our favorite =>
                                isFavorite = true;
                            }
                        }
                        //now we should create for each song his linear layout fragment
                        LinearLayout.LayoutParams maincontainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height/10);
                        LinearLayout.LayoutParams musiciconparams = new LinearLayout.LayoutParams(128,128);
                        LinearLayout.LayoutParams favoriteiconparams = new LinearLayout.LayoutParams(96,96);
                        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(width_text,LinearLayout.LayoutParams.WRAP_CONTENT);

                        //we should create Linear Layout to store our data =>
                        final LinearLayout datalayout = new LinearLayout(HomeScreen.this);
                        maincontainer.setMargins(0,0,0,20);
                        datalayout.setLayoutParams(maincontainer);
                        datalayout.setOrientation(LinearLayout.HORIZONTAL);
                        //probably set gravity =>
                        datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
                        datalayout.setBackgroundResource(R.drawable.child_rounded);

                        //create here music icon =>
                        ImageView musicicon = new ImageView(HomeScreen.this);
                        musicicon.setBackgroundResource(R.drawable.music);
                        musiciconparams.setMargins(20,0,0,0);
                        musicicon.setLayoutParams(musiciconparams);

                        //create here music icon =>
                        ImageView favoriteicon = new ImageView(HomeScreen.this);
                        if(isFavorite){
                            favoriteicon.setBackgroundResource(R.drawable.favoritemusic_filled);
                        }
                        else{
                            favoriteicon.setBackgroundResource(R.drawable.favoritemusic);
                        }
                        favoriteiconparams.setMargins(5,0,10,0);
                        favoriteicon.setLayoutParams(favoriteiconparams);

                        //create Text which will show name of song =>
                        final TextView textView = new TextView(HomeScreen.this);
                        Typeface roboto = ResourcesCompat.getFont(HomeScreen.this, R.font.roboto_medium);
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
                        textView.setSelected(true);

                        //add children to our main layout =>
                        datalayout.addView(musicicon);
                        datalayout.addView(textView);
                        datalayout.addView(favoriteicon);

                        //after all we should add onclick listener to our music layout =>
                        //recreate our logical variable =>
                        boolean finalIsFavorite = isFavorite;
                        datalayout.setOnClickListener(v -> {
                            //start new intent ==>
                            Intent intent = new Intent(HomeScreen.this,MusicScreen.class);
                            //add data to our intent = >
                            intent.putExtra("file", file.getAbsolutePath());
                            intent.putExtra("isFav", finalIsFavorite);
                            startActivity(intent);
                            finish();
                        });
                        //add our linear layout to main linear layout =>
                        linearLayout.addView(datalayout);
                    }
                }
            }

            //scroll to =>
            if(previousMusic!=null){
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musiccontainer);
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