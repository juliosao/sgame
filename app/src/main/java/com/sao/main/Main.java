package com.sao.main;

import com.sao.sgame.Ui.SGameActivity;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Main extends SGameActivity {
    @Override
    public void onGameStarted(SGameSurfaceView src) {
        Background.init(src);
        Background bg = new Background(src);
        src.addBrain(bg);
    }

    @Override
    public void onGameEnd(SGameSurfaceView src) {
        finish();
    }

}
