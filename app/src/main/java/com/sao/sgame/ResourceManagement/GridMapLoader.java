package com.sao.sgame.ResourceManagement;

import com.sao.sgame.Ui.SGameSurfaceView;

/**
 * Loads a bunch of maps distributed in a grid from a bitmap
 */
public class GridMapLoader extends MapLoader{


    /**
     * Class constructor
     * @param sg Sgame where to load the resulting maps
     * @param resourceId Resource with the bitmap
     * @param rows Rows nomber of the resource
     * @param cols Cols number of the resource
     */
    public GridMapLoader(SGameSurfaceView sg, int resourceId, int rows, int cols)
    {
        super(sg,resourceId);
        if(rows<=0 || cols<=0)
            throw new IndexOutOfBoundsException();

        m = new Map[rows*cols];
        int idx=0;
        float w = 1F/(float)cols;
        float h = 1F/(float)rows;

        for(float i=0; i<rows; i++)
        {
            for(float j=0; j<cols; j++)
            {
                m[idx] = new Map(handle, j*w, i*h, (j+1F)*w, (i+1F)*h, bitmapWidth/(float)cols, bitmapHeight/(float)rows);
                idx++;
            }
        }
    }
}
