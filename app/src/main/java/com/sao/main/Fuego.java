package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.Sprite;
import com.sao.sgame.R;
import com.sao.sgame.ResourceManagement.GridMapLoader;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.SimpleMapLoader;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Fuego extends Brain {
    static GridMapLoader container;
    static Map map = null;
    Sprite sprite;
    int counter;
    int graph;

    public static void init(SGameSurfaceView ctx)
    {
        container = new GridMapLoader(ctx,R.drawable.explosion,2,3);
        map = container.getMap(0);

    }

    public Fuego(SGameSurfaceView src, float x, float y)
    {
        super(src);
        sprite = new Sprite();
        sprite.setCoords(x,y);
        sprite.setMap(map);
        src.addRenderable(sprite);
        counter = 20;
        graph = 0;
    }

    @Override
    public void think() {
        counter--;
        if(counter % 4 == 0) {
            graph++;
            sprite.setMap(container.getMap(graph));
        }
        if(counter==0)
            kill();
    }

    @Override
    public void onKill() {
        game.removeRenderable(sprite);
    }
}
