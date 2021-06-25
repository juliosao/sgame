package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.GamePoint;
import com.sao.sgame.R;
import com.sao.sgame.GameObjects.Sprite;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.SimpleMapLoader;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Disparo extends Brain {
    public static SimpleMapLoader container;
    public static Map map;
    private Sprite sprite;

    public static void init(SGameSurfaceView ctx)
    {
        container = new SimpleMapLoader(ctx, R.drawable.disparo2 );
        map = container.getMap(0);
    }

    public Disparo(SGameSurfaceView sg, GamePoint p)
    {
        super(sg);
        sprite = new Sprite();
        sprite.setMap(map);
        sprite.setCoords(p);
        sg.addRenderable(sprite);
    }

    @Override
    public void think() {
        GamePoint coords = sprite.getCords();

        if( coords.x <0 || coords.x > game.getEffectiveHRes() || coords.y > game.getEffectiveVRes() )
        {
            kill();
            return;
        }

        for(Brain go: game.getBrains(Malo.class))
        {
            if(sprite.collision(((Malo)go).getSprite()))
            {
                kill();
                go.kill();
                game.addBrain(new Fuego(game,sprite.getX(),sprite.getY()));
            }
        }


        sprite.advance(20);
    }

    @Override
    public void onKill() {
        game.removeRenderable(sprite);
    }
}
