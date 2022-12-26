package com.teit.teit_music;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;

public class MusicPlayListDialoge extends Activity {
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    ScrollView scrl;
    Button btn1;
    DataBasePlaylists dataBasePlaylists;
    //set logic to click back arrow =>
    @Override
    public void onBackPressed() {
        // your code.
        Intent intent = new Intent(MusicPlayListDialoge.this, PlaylistScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.musicplaylistdialoge);

        //define our variables =>

        textInputLayout = findViewById(R.id.textField_namePlaylist);
        textInputEditText = findViewById(R.id.textinput_namePlaylist);
        scrl = findViewById(R.id.scrollView_musicdialog);
        btn1 = findViewById(R.id.elevatedButton_playlist);

        if(Locale.getDefault().getLanguage()=="ru"){
            textInputLayout.setHint("Наименование плейлиста");
            btn1.setText("Создать");
        }

        //get measure =>
        //get device height and width
        DisplayMetrics metrics = MusicPlayListDialoge.this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int width_text = (int) Math.floor(width*0.8) - 90;

        //set proper height to our scrollView =>
        //get height of text input layout =>
        textInputLayout.measure(0,0);
        int height_textinput = textInputLayout.getMeasuredHeight();

        btn1.measure(0,0);
        int btn_height = btn1.getMeasuredHeight();


        int scrl_height = height - height_textinput - btn_height -40;


        //set scrollview height
        ConstraintLayout.LayoutParams scroll = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, scrl_height);
        scroll.topToBottom = textInputLayout.getId();
        scrl.setLayoutParams(scroll);
        //set ScrollView scrolling smooth =>
        scrl.fullScroll(View.FOCUS_DOWN);
        scrl.setSmoothScrollingEnabled(true);


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

        //should create our databases =>
        //we should work with our database =>
        dataBasePlaylists =  new DataBasePlaylists(MusicPlayListDialoge.this);
        //create arraylist where we will get all records from our lovely database =>
        ArrayList<String> namePlaylists = new ArrayList<>();

        //Should get all records inside =>
        //so now we have all our Favorite songs inside our arraylist =>
        dataBasePlaylists.getAllRecords(namePlaylists);


        ArrayList<String> newplaylistsongs = new ArrayList<>();

        //here we get all files in download folder
        File[] files = directory.listFiles(filter);
        if(directory.canRead() && files!=null) {
            final LinearLayout linearLayout =(LinearLayout)findViewById(R.id.musicdialog_container);
            linearLayout.removeAllViews();
            for(File file: files){
                //get only mp3 files =>
                if(file.getName().endsWith(".mp3")){
                    //define that we don't have any favorite song =>
                    String nameOurSong = file.getName().substring(0,file.getName().length()-4);

                    //now we should create for each song his linear layout fragment
                    LinearLayout.LayoutParams maincontainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height/10);
                    LinearLayout.LayoutParams musiciconparams = new LinearLayout.LayoutParams(128,128);
                    LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(width_text,LinearLayout.LayoutParams.WRAP_CONTENT);

                    //we should create Linear Layout to store our data =>
                    LinearLayout datalayout = new LinearLayout(MusicPlayListDialoge.this);
                    maincontainer.setMargins(0,0,0,20);
                    datalayout.setLayoutParams(maincontainer);
                    datalayout.setOrientation(LinearLayout.HORIZONTAL);
                    //probably set gravity =>
                    datalayout.setGravity(Gravity.TOP & Gravity.CENTER);
                    datalayout.setBackgroundResource(R.drawable.child_rounded);

                    //create here music icon =>
                    ImageView musicicon = new ImageView(MusicPlayListDialoge.this);
                    musicicon.setBackgroundResource(R.drawable.music);
                    musiciconparams.setMargins(20,0,0,0);
                    musicicon.setLayoutParams(musiciconparams);

                    //create Text which will show name of song =>
                    TextView textView = new TextView(MusicPlayListDialoge.this);
                    Typeface roboto = ResourcesCompat.getFont(MusicPlayListDialoge.this, R.font.roboto_medium);
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


                    datalayout.setOnClickListener(v -> {
                        datalayout.setBackgroundResource(R.drawable.shape_rectangle);
                        textView.setTextColor(Color.rgb(255,255,255));
                        musicicon.setBackgroundResource(R.drawable.plus);
                        datalayout.setOnClickListener(null);
                        //add values to our new playlist =>
                        newplaylistsongs.add(textView.getText() + ".mp3");
                    });
                    //add our linear layout to main linear layout =>
                    linearLayout.addView(datalayout);
                }
            }
        }



        //now we should set on our button click listener =>
        btn1.setOnClickListener(view -> {
            if(textInputEditText.getText().length()>=1 && newplaylistsongs.size()>=1){
                //to do when user want to create playlist =>
                //in newplaylistsongs stored all songs which we want to create new playlist =>
                //should check if we have playlist which is already in database =>
                boolean alreadycreatedplaylist = false;
                for(int i=0; i< namePlaylists.size();i++){
                    if(namePlaylists.get(i).equals(String.valueOf(textInputEditText.getText()))){
                        alreadycreatedplaylist = true;
                    }
                }
                if(alreadycreatedplaylist==false){
                    dataBasePlaylists.createPlaylists(String.valueOf(textInputEditText.getText()));

                    //should full our database =>
                    DataBasePlayList dataBasePlayList = new DataBasePlayList(MusicPlayListDialoge.this);
                    SQLiteDatabase db = dataBasePlayList.getWritableDatabase();
                    dataBasePlayList.createTable(db ,String.valueOf(textInputEditText.getText()));

                    ArrayList<String> tryplaylist = new ArrayList<>();
                    for(int i=0; i< newplaylistsongs.size();i++){
                        dataBasePlayList.createPlaylist(newplaylistsongs.get(i) ,String.valueOf(textInputEditText.getText()));
                    }
                    dataBasePlayList.getAllRecords(tryplaylist ,String.valueOf(textInputEditText.getText()));
                    //after we all maded =>
                    Log.d("Music" , "Data inside playlist =>" + tryplaylist);
                    //after all what we did we should return to playlist screen =>
                    Intent intent = new Intent(MusicPlayListDialoge.this, PlaylistScreen.class);
                    startActivity(intent);
                    Toast.makeText(MusicPlayListDialoge.this, "Плейлист создан",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(MusicPlayListDialoge.this, "Плейлист с таким именем уже создан",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(textInputEditText.getText().length()<1 & newplaylistsongs.size()>=1){
                    Toast.makeText(MusicPlayListDialoge.this, "Введите наименование плейлиста",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MusicPlayListDialoge.this, "Выберите как минимум одну песню",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}