package com.audiorecorder.voicerecorderhd.editor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    public  static  final  String TEST_KEY = "TEST";
    public  void  testComit(){
        /// test commit
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
