package com.teit.teit_music;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.Locale;

public class PermissionGranted extends Activity {
    MaterialButton btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.permission_get);

        final Intent intent = new Intent(com.teit.teit_music.PermissionGranted.this,HomeScreen.class);

        //define =>
        btn = (MaterialButton)findViewById(R.id.elevatedButton_granted);
        TextView textvw = (TextView)findViewById(R.id.Text1);
        if(Locale.getDefault().getLanguage()=="ru"){
            textvw.setText("Спасибо!\n\nНаслаждайтесь нашим приложением для проигрывания музыки :)");
            btn.setText("Продолжить");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we just simply should navigate user to home screen =>
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
