package com.teit.teit_music;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;

public class FindMusicScreen extends Activity {
    ImageView arrowback;
    TextInputLayout textinplay;
    TextInputEditText textInputLayout;
    ScrollView scrl;
    LinearLayout ln1;
    DataBaseHelp dataBaseHelp;
    ArrayList<String> nameSearchedSongs = new ArrayList<>();
    //set logic to click back arrow =>
    @Override
    public void onBackPressed() {
        // your code.
        Intent intent = new Intent(FindMusicScreen.this, HomeScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.findmusic);

        //define our variables =>
        arrowback = findViewById(R.id.BackArrowMusicSearch);
        textinplay = findViewById(R.id.textField_search);
        textInputLayout = findViewById(R.id.textinput_search);
        scrl = findViewById(R.id.scrollView_search);
        ln1 = findViewById(R.id.TopLinearSearch);

        //get measure =>
        //get device height and width
        DisplayMetrics metrics = FindMusicScreen.this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int width_text = (int) Math.floor(width*0.8) - 112;

        if(Locale.getDefault().getLanguage()=="ru"){
            textinplay.setHint("Поиск музыки");
        }

        //set proper height to our scrollView =>
        //get height of text input layout =>
        textinplay.measure(0,0);
        int height_textinput = textinplay.getMeasuredHeight();

        int scrl_height = height - height_textinput -40;


        //set scrollview height
        ConstraintLayout.LayoutParams scroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, scrl_height);
        scroll.topToBottom = ln1.getId();
        scrl.setLayoutParams(scroll);
        //set ScrollView scrolling smooth =>
        scrl.fullScroll(View.FOCUS_DOWN);
        scrl.setSmoothScrollingEnabled(true);

        //should get our favorite database ==>
        //we should work with our database =>
        dataBaseHelp =  new DataBaseHelp(FindMusicScreen.this);

        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> nameFavoriteSongs = new ArrayList<>();

        //Should get all records inside =>
        //so now we have all our Favorite songs inside our arraylist =>
        dataBaseHelp.getAllRecords(nameFavoriteSongs);

        //set that when click on arrow we should go to home screen =>
        arrowback.setOnClickListener(view -> {
            //we should clear our media player
            Intent intent = new Intent(FindMusicScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });

        //we should get all files
        String path = Environment.getExternalStorageDirectory().toString() + "/Download";
        File directory = new File(path);
        //create filter to get only mp3 files =>
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".mp3");
            }
        };


        //here we get all files in download folder
        File[] files = directory.listFiles(filter);


        //now we should get point when user stop writing data inside text view =>

        final long[] last_text_edit = {0};
        Handler handler = new Handler();
        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit[0])) {
                    //To DO when user finish typing =>
                    final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musicseacrh);
                    linearLayout.removeAllViews();
                    for(File f : files){
                        if(f.getName().toLowerCase(Locale.ROOT).contains(String.valueOf(textInputLayout.getText()).toLowerCase(Locale.ROOT))){
                            //define that we don't have any favorite song =>
                            boolean isFavorite = false;

                            String nameOurSong = f.getName().substring(0,f.getName().length()-4);

                            //so now we should get if our song inside our database =>
                            for(int i=0; i<nameFavoriteSongs.size();i++){
                                if(f.getName().equals(nameFavoriteSongs.get(i))){
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
                            final LinearLayout datalayout = new LinearLayout(FindMusicScreen.this);
                            maincontainer.setMargins(0,0,0,20);
                            datalayout.setLayoutParams(maincontainer);
                            datalayout.setOrientation(LinearLayout.HORIZONTAL);
                            //probably set gravity =>
                            datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
                            datalayout.setBackgroundResource(R.drawable.child_rounded);

                            //create here music icon =>
                            ImageView musicicon = new ImageView(FindMusicScreen.this);
                            musicicon.setBackgroundResource(R.drawable.music);
                            musiciconparams.setMargins(20,0,0,0);
                            musicicon.setLayoutParams(musiciconparams);

                            //create here music icon =>
                            ImageView favoriteicon = new ImageView(FindMusicScreen.this);
                            if(isFavorite){
                                favoriteicon.setBackgroundResource(R.drawable.favoritemusic_filled);
                            }
                            else{
                                favoriteicon.setBackgroundResource(R.drawable.favoritemusic);
                            }
                            favoriteiconparams.setMargins(5,0,10,0);
                            favoriteicon.setLayoutParams(favoriteiconparams);

                            //create Text which will show name of song =>
                            final TextView textView = new TextView(FindMusicScreen.this);
                            Typeface roboto = ResourcesCompat.getFont(FindMusicScreen.this, R.font.roboto_medium);
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
                            datalayout.addView(favoriteicon);

                            //after all we should add onclick listener to our music layout =>
                            //recreate our logical variable =>
                            boolean finalIsFavorite = isFavorite;
                            datalayout.setOnClickListener(v -> {
                                ShowSameSongs(files, String.valueOf(textInputLayout.getText()).toLowerCase(Locale.ROOT));
                                //start new intent ==>
                                Intent intent = new Intent(FindMusicScreen.this,SearchMusicScreen.class);
                                //add data to our intent = >
                                intent.putExtra("file", f.getAbsolutePath());
                                intent.putExtra("isFav", finalIsFavorite);
                                intent.putExtra("ArrayNameSearchSongs", nameSearchedSongs);
                                startActivity(intent);
                                finish();
                            });
                            //add our linear layout to main linear layout =>
                            linearLayout.addView(datalayout);
                        }
                    }
                }
            }
        };

        textInputLayout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //avoid triggering event when text is empty
                if (editable.length() > 0) {
                    last_text_edit[0] = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, 0);
                } else {

                }
            }
        });
    }
<<<<<<< HEAD
    public void ShowSameSongs(File[] files , String givenText ){
        for (File f : files){
            if(f.getName().toLowerCase(Locale.ROOT).contains(givenText)) {
                //todo: add files to arraylist =>
                nameSearchedSongs.add(f.getAbsolutePath());
            }
        }
    }
}
=======
}
>>>>>>> origin/main
