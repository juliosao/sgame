package com.sao.sgame.Ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Esta clase crea una actividad automáticamente acoplada a un SGameSurfaceView
 * Por defecto fuerza la orientación a horizontal
 */
public abstract class SGameActivity extends Activity implements SGameSurfaceView.SGameViewListener {

    private GLSurfaceView gLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = new SGameSurfaceView(this,this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(gLView);
    }


}
