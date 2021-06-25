package com.sao.main;

import com.sao.sgame.Ui.SGameActivity;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Main extends SGameActivity {
    @Override
    public void onGameStarted(SGameSurfaceView src) {
        Bueno.init(src);
        Malo.init(src);
        Disparo.init(src);
        Fuego.init(src);
        Fondo.init(src);

        src.addBrain(new Bueno(src));
        src.addBrain(new CreaMalos(src));
        src.addBrain(new Fondo(src));
    }

    @Override
    public void onGameEnd(SGameSurfaceView src) {
        finish();
    }

}
