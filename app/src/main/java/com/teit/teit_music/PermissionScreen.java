package com.teit.teit_music;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class PermissionScreen extends Activity {
    MaterialButton btn;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    final Intent intent = new Intent(com.teit.teit_music.PermissionScreen.this,PermissionGranted.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.permission);

        //define =>
        btn = (MaterialButton)findViewById(R.id.elevatedButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //now we should initialize permission handler to get permission from user =>
                ActivityCompat.requestPermissions(PermissionScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            }
        });


    }
}
