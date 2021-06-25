package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.Sprite;
import com.sao.sgame.R;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.SimpleMapLoader;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Fondo extends Brain {
    static SimpleMapLoader file;
    static Map map;
    static Sprite sprite;

    public static void init(SGameSurfaceView src)
    {
        file = new SimpleMapLoader(src, R.drawable.espacio);
        map = file.getMap(0);
    }

    public Fondo(SGameSurfaceView src)
    {
        super(src);
        sprite = new Sprite();
        sprite.setMap(map);
        sprite.setCoords(src.getEffectiveHRes()/2,src.getEffectiveVRes()/2,90);
        src.addRenderable(sprite);
    }

    @Override
    public void think() {

    }
}
