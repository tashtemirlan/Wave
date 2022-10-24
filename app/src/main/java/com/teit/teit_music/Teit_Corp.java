package com.teit.teit_music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Teit_Corp extends AppCompatActivity {

    Animation teitanim;
    ImageView logo;
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.teit_corp);

        //here we should define our animation
        teitanim = AnimationUtils.loadAnimation(this , R.anim.teitcorp_animationscreen);

        //define our elements from screen
        logo = findViewById(R.id.Teitcorp_image);
        text=findViewById(R.id.Teitcorp_text);

        logo.setAnimation(teitanim);
        text.setAnimation(teitanim);

        final Intent intent = new Intent(com.teit.teit_music.Teit_Corp.this,HomeScreen.class);
        final Intent intent_permission = new Intent(com.teit.teit_music.Teit_Corp.this,PermissionScreen.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //here we should get information about if storage permission is granted or not =>
                if(ContextCompat.checkSelfPermission(Teit_Corp.this , Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(Teit_Corp.this , Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                ){
                    startActivity(intent);
                }
                else {
                    //if we non get permission to work with external storage =>
                    startActivity(intent_permission);
                }
            }
        },2100);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },2100);

    }
}