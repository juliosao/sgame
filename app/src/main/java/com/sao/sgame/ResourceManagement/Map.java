package com.sao.sgame.ResourceManagement;

import com.sao.sgame.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Map {
    FloatBuffer textureCordsBuffer;
    int handle;
    float w;
    float h;

    public Map(int hnd, float x0, float y0, float x1, float y1, float fW, float fH)
    {
        this.w = fW;
        this.h = fH;
        handle = hnd;

        float[] texCords = new float[]{
                x0, y0,
                x0, y1,
                x1, y0,
                x1, y1
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(texCords.length * Util.SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        textureCordsBuffer = bb.asFloatBuffer();
        textureCordsBuffer.put(texCords);
        textureCordsBuffer.position(0);

    }

    public float getWidth()
    {
        return w;
    }

    public float getHeight()
    {
        return h;
    }

    public int getHandle()
    {
        return handle;
    }

    public FloatBuffer getBuffer()
    {
        return textureCordsBuffer;
    }
}
