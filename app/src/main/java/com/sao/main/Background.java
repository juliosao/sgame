package com.sao.main;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.TileGrid;
import com.sao.sgame.R;
import com.sao.sgame.ResourceManagement.GridMapLoader;
import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.Ui.SGameSurfaceView;

public class Background extends Brain {
    static GridMapLoader container;
    static Map map = null;
    TileGrid tg;

    /**
     * Constructor de clase
     *
     * @param game El juego "padre"
     */
    public Background(SGameSurfaceView game) {
        super(game);
        tg = new TileGrid(2,4,64,64, container);
        tg.setData(new int[][]{
                {0,1,0,0},
                {1,2,3,1}
        } );
        tg.setCoords(320,200);
        game.addRenderable(tg);
    }

    public static void init(SGameSurfaceView ctx)
    {
        container = new GridMapLoader(ctx, R.drawable.background,2,4);
        map = container.getMap(0);
    }

    @Override
    public void think() {

    }
}
