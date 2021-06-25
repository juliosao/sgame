package com.sao.sgame.ResourceManagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.sao.sgame.Ui.SGameSurfaceView;

public abstract class MapLoader {
    protected int handle;
    protected float bitmapWidth;
    protected float bitmapHeight;
    Map[] m;

    /**
     * Carga un mapa de bits en la grafica.
     * @param bitmap Bitmap a cargar
     * @warning No llamar si no es en el constructor
     */
    protected void loadBitmap(Bitmap bitmap)
    {
        // Inicializamos la textura
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Cannot initialize texture");
        }

        // Indicamos a OpenGL que vamos a usar una textura
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Filtros
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Cargamos el mapa de bit en la textura
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmapWidth = (float)bitmap.getWidth();
        bitmapHeight = (float)bitmap.getHeight();
        handle=textureHandle[0];
    }

    public MapLoader(Bitmap bitmap)
    {
        loadBitmap(bitmap);
    }

    public MapLoader(SGameSurfaceView sg, int resourceId)
    {
        // Cargamos el mapa de bits
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final android.graphics.Bitmap bitmap = BitmapFactory.decodeResource(sg.getContext().getResources(), resourceId, options);
        loadBitmap(bitmap);
    }

    /**
     * Obtains a map from Map loader
     * @param i Map index
     * @return
     */
    public Map getMap(int i) {
        if(i<0 || i>m.length)
            throw new IndexOutOfBoundsException();

        return m[i];
    }

    /**
     * Returns the number of maps in loader
     * @return
     */
    public int getMapCount()
    {
        return m.length;
    }

    /**
     * Returns the handle for texturing (Its Useful for internal ops)
     * @return The handle for texturing
     */
    public int getHandle()
    {
        return handle;
    }
}
