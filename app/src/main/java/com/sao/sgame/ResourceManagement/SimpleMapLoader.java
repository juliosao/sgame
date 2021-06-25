package com.sao.sgame.ResourceManagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.sao.sgame.Ui.SGameSurfaceView;

public class SimpleMapLoader extends MapLoader {

    public SimpleMapLoader(SGameSurfaceView sg, int resourceId)
    {
        super(sg,resourceId);
        m = new Map[1];
        m[0] = new Map(handle, 0, 0, 1, 1, bitmapWidth, bitmapHeight);
    }
}
