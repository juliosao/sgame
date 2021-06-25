package com.sao.main;

import android.util.Log;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.GamePoint;
import com.sao.sgame.R;
import com.sao.sgame.GameObjects.Sprite;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.SimpleMapLoader;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Bueno extends Brain {
    static SimpleMapLoader fbuenos;
    static SimpleMapLoader fbtnDisparo;
    static Map mbuenos;
    static Map mdisparo;

    float dX=0.05f,dY=0.05f, xAngle=0;
    Sprite btnDisparo;
    Sprite nave;
    int timeout=0;

    public static void init(SGameSurfaceView src)
    {
        fbuenos = new SimpleMapLoader(src, R.drawable.bueno);
        mbuenos = fbuenos.getMap(0);
        fbtnDisparo = new SimpleMapLoader(src, R.drawable.boton1);
        mdisparo = fbtnDisparo.getMap(0);

    }

    public Bueno(SGameSurfaceView sg)
    {
        super(sg);

        nave = new Sprite();
        nave.setMap(mbuenos);
        nave.setCoords(320,200);
        sg.addRenderable(nave);

        btnDisparo = new Sprite();
        btnDisparo.setCoords(sg.getEffectiveHRes()-20,sg.getEffectiveVRes()-20);
        btnDisparo.setMap(mdisparo);
        sg.addRenderable(btnDisparo);

        dX=120;
        dY=200;
    }

    @Override
    public void think() {
        float x,y;

        if(game.getTouches().size()>0)
        {
            Log.d("Bueno","Touches:"+game.getTouches().size());
        }

        if(timeout>0)timeout--;

        for(GamePoint t: game.getTouches())
        {
            if(btnDisparo.getDistTo(t.x,t.y)<64)
            {
                if(timeout==0)
                {
                    game.addBrain(new Disparo(game,nave.getCords()));
                    timeout=10;
                }
            }
            else
            {
                dX =t.x;
                dY =t.y;
            }
        }

        if(nave.getDistTo(dX,dY)>10)
        {
            xAngle =nave.getAngleTo(dX,dY);
            nave.advance(5F, xAngle);
        }
    }

    @Override
    public void onKill() {
        game.removeRenderable(nave);
        game.removeRenderable(btnDisparo);
    }
}
