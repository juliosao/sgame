package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.Sprite;
import com.sao.sgame.R;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.SimpleMapLoader;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Malo extends Brain {
    static SimpleMapLoader container;
    static Map map = null;
    Sprite nave;

    public static void init(SGameSurfaceView ctx)
    {
        container = new SimpleMapLoader(ctx, R.drawable.malo1 );
        map = container.getMap(0);
    }

    public Malo(SGameSurfaceView src, float x, float y)
    {
        super(src);
        nave = new Sprite();
        nave.setCoords(x,y);
        nave.setMap(map);
        src.addRenderable(nave);
    }

    @Override
    public void think() {
        nave.advance(-5);
        if(nave.getX()<-64)
            kill();

    }

    @Override
    public void onKill() {
        game.removeRenderable(nave);
    }

    public Sprite getSprite()
    {
        return nave;
    }
}
