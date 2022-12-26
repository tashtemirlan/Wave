package com.teit.teit_music;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FavoriteMusicScreen extends Activity {
    //declare our widgets =>
    ImageView arrowback, musicheart, shuffle, play, nextbutton, previousbutton;
    TextView musicname_screen, musicauthor_screen, musicend_screen, musicstart_screen;
    CircleLineVisualizer circleVisual;
    SeekBar musicbar;
    LinearLayout ln1, ln2;
    ArrayList<Integer> datashuffle = new ArrayList();
    ArrayList<String> musicnameArray = new ArrayList<>();
    ArrayList<Integer> dataplay = new ArrayList();
    DataBaseHelp dataBaseHelp;
    MediaPlayer player;
    Timer timer;

    //Release our player =>
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }

    //set logic to click back arrow =>
    @Override
    public void onBackPressed() {
        // your code.
        timer.cancel();
        player.release();
        circleVisual.release();
        Intent intent = new Intent(FavoriteMusicScreen.this, FavoriteScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.musicscreen);

        //define our widgets
        arrowback = findViewById(R.id.BackArrowMusic);
        musicheart = findViewById(R.id.MusicHeart);
        shuffle = findViewById(R.id.Shuffle);
        play = findViewById(R.id.MusicPlay);
        nextbutton = findViewById(R.id.MusicForward);
        previousbutton = findViewById(R.id.MusicBack);
        musicname_screen = findViewById(R.id.MusicName);
        musicauthor_screen = findViewById(R.id.MusicAuthor);
        musicstart_screen = findViewById(R.id.MusicStartPos);
        musicend_screen = findViewById(R.id.MusicEndPos);
        ln1 = findViewById(R.id.MusicLinearLayout);
        ln2 = findViewById(R.id.MusicSwitchLayout);
        circleVisual = findViewById(R.id.MusicShow);
        musicbar = findViewById(R.id.MusicBar);

        //get data from our intent =>
        String musicplay_path = getIntent().getStringExtra("file");
        boolean ismusicFav = getIntent().getBooleanExtra("isFav", false);

        //get our dataBase =>
        dataBaseHelp = new DataBaseHelp(FavoriteMusicScreen.this);

        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> nameFavoriteSongs = new ArrayList<>();

        //Should get all records inside =>
        //so now we have all our Favorite songs inside our arraylist =>
        dataBaseHelp.getAllRecords(nameFavoriteSongs);

        //height of user screen =>
        DisplayMetrics metrics = FavoriteMusicScreen.this.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        // Creating our animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_animation);

        //create our music file =>
        File music = new File(musicplay_path);

        //Set data to our screen =>
        musicname_screen.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        musicname_screen.setHorizontalFadingEdgeEnabled(true);
        musicname_screen.setMarqueeRepeatLimit(-1);
        musicname_screen.setSingleLine(true);
        musicauthor_screen.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        musicauthor_screen.setHorizontalFadingEdgeEnabled(true);
        musicauthor_screen.setMarqueeRepeatLimit(-1);
        musicauthor_screen.setSingleLine(true);

        //set our hearth button =>
        musicheart.setImageResource(R.drawable.heart_filled);

        //Create media player =>
        player = new MediaPlayer();
        player.getAudioSessionId();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(musicplay_path);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //now we should set as screen launched properly data inside =>
        MusicSet(music);
        int audiovisualID = player.getAudioSessionId();
        if (audiovisualID != -1) {
            //set data to visual =>
            circleVisual.setAudioSessionId(audiovisualID);
        }

        //set right height to our circle visual =>

        arrowback.measure(0, 0);
        int height_arrowback = arrowback.getMeasuredHeight();

        musicname_screen.measure(0, 0);
        int height_musicname_screen = musicname_screen.getMeasuredHeight();

        musicauthor_screen.measure(0, 0);
        int height_musicauthor_screen = musicauthor_screen.getMeasuredHeight();

        ln1.measure(0, 0);
        int height_musicscroll_linearlayout = ln1.getMeasuredHeight();

        ln2.measure(0, 0);
        int height_musicbuttons_linearlayout = ln2.getMeasuredHeight();

        int circleviewheight_absolute = height - height_arrowback - height_musicname_screen - height_musicauthor_screen - height_musicscroll_linearlayout - height_musicbuttons_linearlayout;
        int circleviewheight = (int) (circleviewheight_absolute * 0.95);

        ConstraintLayout.LayoutParams circlevisualLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, circleviewheight);
        circlevisualLayoutParams.topToBottom = arrowback.getId();
        circleVisual.setLayoutParams(circlevisualLayoutParams);


        musicnameArray.add(music.getName());

        //we should get all files
        String path = Environment.getExternalStorageDirectory().toString() + "/Download/";


        //just write when we click on shuffle => repeat => repeat one
        datashuffle.add(0);

        //create our timer =>
        timer = new Timer();

        shuffle.setOnClickListener(view -> {
            if (datashuffle.get(0) == 0) {
                shuffle.setImageResource(R.drawable.repeat);
                Toast.makeText(FavoriteMusicScreen.this, "Повтор альбома",
                        Toast.LENGTH_SHORT).show();
                shuffle.startAnimation(animation);
                datashuffle.set(0, 1);
            } else {
                if (datashuffle.get(0) == 1) {
                    shuffle.setImageResource(R.drawable.repeat_one);
                    Toast.makeText(FavoriteMusicScreen.this, "Повтор песни",
                            Toast.LENGTH_SHORT).show();
                    shuffle.startAnimation(animation);
                    datashuffle.set(0, 2);
                } else {
                    shuffle.setImageResource(R.drawable.shuffle);
                    Toast.makeText(FavoriteMusicScreen.this, "Случайная песня",
                            Toast.LENGTH_SHORT).show();
                    shuffle.startAnimation(animation);
                    datashuffle.set(0, 0);
                }
            }
        });

        // set that music is non playing when we click =>
        dataplay.add(0);
        play.setOnClickListener(view -> {
            if (dataplay.get(0) == 0) {
                play.setImageResource(R.drawable.pause);
                play.startAnimation(animation);
                //play music =>
                player.start();
                dataplay.set(0, 1);
            } else {
                play.setImageResource(R.drawable.play);
                play.startAnimation(animation);
                //stop music =>
                player.pause();
                dataplay.set(0, 0);
            }
        });
        //set on click listener to move back screen when click on arrow =>
        arrowback.setOnClickListener(view -> {
            //we should clear our media player
            timer.cancel();
            player.release();
            circleVisual.release();
            Intent intent = new Intent(FavoriteMusicScreen.this, FavoriteScreen.class);
            startActivity(intent);
            finish();
        });

        ArrayList<Boolean> dataheart = new ArrayList();
        dataheart.add(ismusicFav);
        musicheart.setOnClickListener(view -> {
            if (dataheart.get(0) == true) {
                musicheart.setImageResource(R.drawable.heart);
                musicheart.startAnimation(animation);
                dataheart.set(0, false);
                dataBaseHelp.makeUnFavorite(musicnameArray.get(0));
            } else {
                musicheart.setImageResource(R.drawable.heart_filled);
                musicheart.startAnimation(animation);
                dataheart.set(0, true);
                dataBaseHelp.makeFavorite(musicnameArray.get(0));
            }
        });

        //here we should write logic for next button and previous button =>
        nextbutton.setOnClickListener(view -> {
            //hide visualization
            circleVisual.hide();

            //set pause to player =>
            player.pause();
            nextbutton.startAnimation(animation);
            int music_logic = datashuffle.get(0);
            int position = 0;
            for (int starttick = 0; starttick < nameFavoriteSongs.size(); starttick++) {
                if (musicnameArray.get(0).equals(nameFavoriteSongs.get(starttick))) {
                    position = starttick;
                    break;
                }
            }
            //set to view that music is playing
            play.setImageResource(R.drawable.pause);
            dataplay.set(0, 1);

            if (music_logic == 0) {
                int rand = new Random().nextInt(nameFavoriteSongs.size() - 1);
                File newmusic = new File(path + nameFavoriteSongs.get(rand));

                //we should pause our media player =>
                player.reset();
                //set new music =>
                MusicSet(newmusic);
                //create new player =>
                try {
                    player.setDataSource(newmusic.getAbsolutePath());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                musicnameArray.set(0, newmusic.getName());

                //set data to visualizer =>
                int neweraudiovisualID = player.getAudioSessionId();
                if (neweraudiovisualID != -1) {
                    //set data to visual =>
                    circleVisual.setAudioSessionId(neweraudiovisualID);
                    circleVisual.setAudioSessionId(neweraudiovisualID);
                }
                circleVisual.show();

                //start music =>
                player.start();

            } else {
                if (position == nameFavoriteSongs.size() - 1) {
                    File newmusic = new File(path + nameFavoriteSongs.get(0));

                    //we should pause our media player =>
                    player.reset();
                    //set new music =>
                    MusicSet(newmusic);
                    //create new player =>
                    try {
                        player.setDataSource(newmusic.getAbsolutePath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicnameArray.set(0, newmusic.getName());

                    //set data to visualizer =>
                    int neweraudiovisualID = player.getAudioSessionId();
                    if (neweraudiovisualID != -1) {
                        //set data to visual =>
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                    }
                    circleVisual.show();

                    //start music =>
                    player.start();
                } else {
                    File newmusic = new File(path + nameFavoriteSongs.get(position + 1));

                    //we should pause our media player =>
                    player.reset();
                    //set new music =>
                    MusicSet(newmusic);
                    //create new player =>
                    try {
                        player.setDataSource(newmusic.getAbsolutePath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicnameArray.set(0, newmusic.getName());

                    //set data to visualizer =>
                    int neweraudiovisualID = player.getAudioSessionId();
                    if (neweraudiovisualID != -1) {
                        //set data to visual =>
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                    }
                    circleVisual.show();

                    //start music =>
                    player.start();
                }
            }
        });

        previousbutton.setOnClickListener(view -> {
            //clear data to circle visual
            circleVisual.hide();
            previousbutton.startAnimation(animation);
            //set pause to player=>
            player.pause();
            int music_logic = datashuffle.get(0);
            //we should get position from list of our music
            int position = 0;
            for (int starttick = 0; starttick < nameFavoriteSongs.size(); starttick++) {
                if (musicnameArray.get(0).equals(nameFavoriteSongs.get(starttick))) {
                    position = starttick;
                    break;
                }
            }
            //set to view that music is playing
            play.setImageResource(R.drawable.pause);
            dataplay.set(0, 1);

            if (music_logic == 0) {
                int rand = new Random().nextInt(nameFavoriteSongs.size() - 1);
                File newmusic = new File(path +  nameFavoriteSongs.get(rand));
                //we should pause our media player =>
                player.reset();
                //set new music =>
                MusicSet(newmusic);
                //create new player =>
                try {
                    player.setDataSource(newmusic.getAbsolutePath());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                musicnameArray.set(0, newmusic.getName());

                //set data to visualizer =>
                int neweraudiovisualID = player.getAudioSessionId();
                if (neweraudiovisualID != -1) {
                    //set data to visual =>
                    circleVisual.setAudioSessionId(neweraudiovisualID);
                    circleVisual.setAudioSessionId(neweraudiovisualID);
                }
                circleVisual.show();
                //start music =>
                player.start();

            } else {
                if (position == 0) {
                    File newmusic = new File(path + nameFavoriteSongs.get(nameFavoriteSongs.size() - 1));

                    //we should pause our media player =>
                    player.reset();
                    //set new music =>
                    MusicSet(newmusic);
                    //create new player =>
                    try {
                        player.setDataSource(newmusic.getAbsolutePath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicnameArray.set(0, newmusic.getName());

                    //set data to visualizer =>
                    int neweraudiovisualID = player.getAudioSessionId();
                    if (neweraudiovisualID != -1) {
                        //set data to visual =>
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                    }
                    circleVisual.show();
                    //start music =>
                    player.start();
                } else {
                    File newmusic = new File(path + nameFavoriteSongs.get(position - 1));
                    //we should pause our media player =>
                    player.reset();
                    //set new music =>
                    MusicSet(newmusic);
                    //create new player =>
                    try {
                        player.setDataSource(newmusic.getAbsolutePath());
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicnameArray.set(0, newmusic.getName());

                    //set data to visualizer =>
                    int neweraudiovisualID = player.getAudioSessionId();
                    if (neweraudiovisualID != -1) {
                        //set data to visual =>
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                        circleVisual.setAudioSessionId(neweraudiovisualID);
                    }
                    circleVisual.show();
                    //start music =>
                    player.start();
                }
            }
        });

        //here we should write logic when player finish play our certain music =>

        musicbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //define here out textview =>
            final TextView musicstart = (TextView) findViewById(R.id.MusicStartPos);

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //set right data to textview and give to media player right value =>
                int sec_pos = seekBar.getProgress() % 60;
                int min_pos = (seekBar.getProgress() - sec_pos) / 60;
                if (String.valueOf(min_pos).startsWith("0")) {
                    if (String.valueOf(sec_pos).length() == 1) {
                        musicstart.setText(min_pos + ":0" + sec_pos);
                    } else {
                        musicstart.setText(min_pos + ":" + sec_pos);
                    }
                } else {
                    if (String.valueOf(sec_pos).length() == 1) {
                        musicstart.setText(min_pos + ":0" + sec_pos);
                    } else {
                        musicstart.setText(min_pos + ":" + sec_pos);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //we should stop playing music if played =>
                if (dataplay.get(0) == 1) {
                    player.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //stop changing value=>

                //we should set that music is playing in view =>
                //set to view that music is playing
                play.setImageResource(R.drawable.pause);
                dataplay.set(0, 1);

                //give to player right data
                player.seekTo(seekBar.getProgress() * 1000);

                //we should start playing music =>
                player.start();
            }
        });

        // set right time each second to player =>

        timer.scheduleAtFixedRate(new TimerTask() {
            //we should define our widgets here to avoid problems =>
            final TextView musicstart = (TextView) findViewById(R.id.MusicStartPos);
            final SeekBar musicDanceBar = (SeekBar) findViewById(R.id.MusicBar);

            @Override
            public void run() {
                FavoriteMusicScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //check if player is playing =>
                        if (player.isPlaying()) {
                            int secall = player.getCurrentPosition() / 1000;
                            int min = secall / 60;
                            int sec = secall % 60;

                            //set data to music bar
                            musicDanceBar.setProgress(secall);
                            //set data to start_text
                            if (sec > 9) {
                                musicstart.setText(min + ":" + sec);
                            } else {
                                musicstart.setText(min + ":0" + sec);
                            }
                            //if we reached end we should get to next music =>
                            if (secall >= (musicDanceBar.getMax() - 2)) {

                                //hide our visualization
                                circleVisual.hide();

                                int music_logic = datashuffle.get(0);
                                int position = 0;
                                for (int starttick = 0; starttick < nameFavoriteSongs.size(); starttick++) {
                                    if (musicnameArray.get(0).equals(nameFavoriteSongs.get(starttick))) {
                                        position = starttick;
                                        break;
                                    }
                                }
                                //set to view that music is playing
                                play.setImageResource(R.drawable.pause);
                                dataplay.set(0, 1);

                                if (music_logic == 0) {
                                    int rand = new Random().nextInt(nameFavoriteSongs.size() - 1);
                                    File newmusic = new File(path + nameFavoriteSongs.get(rand));
                                    //we should pause our media player =>
                                    player.reset();

                                    //set new music =>
                                    MusicSet(newmusic);
                                    //create new player =>
                                    try {
                                        player.setDataSource(newmusic.getAbsolutePath());
                                        player.prepare();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    musicnameArray.set(0, newmusic.getName());

                                    //set data to visualizer =>
                                    int neweraudiovisualID = player.getAudioSessionId();
                                    if (neweraudiovisualID != -1) {
                                        //set data to visual =>
                                        circleVisual.setAudioSessionId(neweraudiovisualID);
                                        circleVisual.setAudioSessionId(neweraudiovisualID);
                                    }
                                    circleVisual.show();

                                    //start music =>
                                    player.start();

                                } else {
                                    if (music_logic == 1) {
                                        if (position == nameFavoriteSongs.size() - 1) {
                                            File newmusic = new File(path + nameFavoriteSongs.get(0));
                                            //we should pause our media player =>
                                            player.reset();
                                            //set new music =>
                                            MusicSet(newmusic);
                                            //create new player =>
                                            try {
                                                player.setDataSource(newmusic.getAbsolutePath());
                                                player.prepare();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            musicnameArray.set(0, newmusic.getName());

                                            //set data to visualizer =>
                                            int neweraudiovisualID = player.getAudioSessionId();
                                            if (neweraudiovisualID != -1) {
                                                //set data to visual =>
                                                circleVisual.setAudioSessionId(neweraudiovisualID);
                                                circleVisual.setAudioSessionId(neweraudiovisualID);
                                            }
                                            circleVisual.show();

                                            //start music =>
                                            player.start();
                                        } else {
                                            File newmusic = new File(path + nameFavoriteSongs.get(position + 1));
                                            //we should pause our media player =>
                                            player.reset();
                                            //set new music =>
                                            MusicSet(newmusic);
                                            //create new player =>
                                            try {
                                                player.setDataSource(newmusic.getAbsolutePath());
                                                player.prepare();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            musicnameArray.set(0, newmusic.getName());

                                            //set data to visualizer =>
                                            int neweraudiovisualID = player.getAudioSessionId();
                                            if (neweraudiovisualID != -1) {
                                                //set data to visual =>
                                                circleVisual.setAudioSessionId(neweraudiovisualID);
                                                circleVisual.setAudioSessionId(neweraudiovisualID);
                                            }
                                            circleVisual.show();

                                            //start music =>
                                            player.start();
                                        }
                                    } else {
                                        File newmusic = new File(path + nameFavoriteSongs.get(position));
                                        //we should pause our media player =>
                                        player.reset();
                                        //set new music =>
                                        MusicSet(newmusic);
                                        //create new player =>
                                        try {
                                            player.setDataSource(newmusic.getAbsolutePath());
                                            player.prepare();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        musicnameArray.set(0, newmusic.getName());

                                        //set data to visualizer =>
                                        int neweraudiovisualID = player.getAudioSessionId();
                                        if (neweraudiovisualID != -1) {
                                            //set data to visual =>
                                            circleVisual.setAudioSessionId(neweraudiovisualID);
                                            circleVisual.setAudioSessionId(neweraudiovisualID);
                                        }
                                        circleVisual.show();
                                        //start music =>
                                        player.start();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void MusicSet(File musicnew) {

        SeekBar MusicSetmusicbar = (SeekBar) findViewById(R.id.MusicBar);
        TextView MusicSetmusicstart_screen = (TextView) findViewById(R.id.MusicStartPos);
        TextView MusicSetmusicend_screen = (TextView) findViewById(R.id.MusicEndPos);
        TextView MusicSetmusicname_screen = (TextView) findViewById(R.id.MusicName);
        TextView MusicSetmusicauthor_screen = (TextView) findViewById(R.id.MusicAuthor);


        //Work with music =>
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uriMusic = Uri.fromFile(musicnew);
        mediaMetadataRetriever.setDataSource(FavoriteMusicScreen.this, uriMusic);

        String songName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        String seconds = String.valueOf((dur % 60000) / 1000);
        String minutes = String.valueOf(dur / 60000);

        int allseconds = Integer.valueOf(minutes) * 60 + Integer.valueOf(seconds);
        MusicSetmusicbar.setProgress(0);
        MusicSetmusicstart_screen.setText("0:00");


        if (minutes.startsWith("0")) {
            if (seconds.length() == 1) {
                MusicSetmusicend_screen.setText("0" + minutes + ":0" + seconds);
            } else {
                MusicSetmusicend_screen.setText("0" + minutes + ":" + seconds);
            }
        } else {
            if (seconds.length() == 1) {
                MusicSetmusicend_screen.setText(minutes + ":0" + seconds);
            } else {
                MusicSetmusicend_screen.setText(minutes + ":" + seconds);
            }
        }
        //set properly data to musicbar =>
        if (songName != null) {
            MusicSetmusicname_screen.setText(songName);
        } else {
            MusicSetmusicname_screen.setText(musicnew.getName().substring(0, musicnew.getName().length() - 4));
        }
        if (artist != null) {
            MusicSetmusicauthor_screen.setText(artist);
        } else {
            if(Locale.getDefault().getLanguage()=="ru"){
                MusicSetmusicauthor_screen.setText("Неизестно");
            }
            else{
                MusicSetmusicauthor_screen.setText("Mystic");
            }
        }
        MusicSetmusicbar.setMax(allseconds);
    }
}