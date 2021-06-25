package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.Ui.SGameSurfaceView;

public class CreaMalos extends Brain {
    int timeout=30;

    public CreaMalos(SGameSurfaceView game) {
        super(game);
    }

    @Override
    public void think() {
        if(timeout==0)
        {
            timeout=120;
            game.addBrain(new Malo( game,660,(float)Math.random()*400));
        }
        timeout--;
    }

}
