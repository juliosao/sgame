package com.sao.sgame.GameObjects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.Shader;
import com.sao.sgame.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This class paints bitmaps on screen
 */
public class Sprite extends ScreenObject {
    /* Shaders */
     private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 TexCoordIn;" +
            "varying vec2 TexCoordOut;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  TexCoordOut = TexCoordIn;" +
            "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D MapContainer;" +
            "varying lowp vec2 TexCoordOut;" +
            "void main() {" +
            "  gl_FragColor = texture2D(MapContainer, TexCoordOut);" +
            "}";

    public final static float[] coords = new float[]{
        -0.5f, 0.5f, 1,
        -0.5f, -0.5f, 1,
        0.5f, 0.5f, 1,
        0.5f, -0.5f, 1};

    private static final String TAG = "Sprite";
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * Util.SIZEOF_FLOAT;
    private static final int VERTEX_COUNT = 4;
    private static final int COORDS_PER_TEXTURE = 2;
    private static final int TEXTURE_STRIDE = COORDS_PER_TEXTURE * Util.SIZEOF_FLOAT; // 4 bytes per mapContainer vertex

    /* This is the map to paint */
    private Map map;

    /* Store for vertices */
    protected static FloatBuffer vertexBuffer = null;

    protected  int mProgram = -1;
    private int vertexShader = 0;
    private int fragmentShader = 0;

    /* Size of graphic (For collisions) */
    private float w, h;

    /* Transformation matrix */
    private float[] transformMatrix = new float[16];

    public Sprite()
    {
        super();

        if(mProgram == -1)
        {
            mProgram = GLES20.glCreateProgram();

            vertexShader = Shader.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            fragmentShader = Shader.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            mProgram = GLES20.glCreateProgram();

            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            // Vertex to draw
            ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * Util.SIZEOF_FLOAT);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(coords);
            vertexBuffer.position(0);
        }

        setMap(null);
        setCoords(0,0);
        setAngle(0);
        setZoom(1);
    }

    /**
     * Returns if this Sprite collides with another Sprite
     * @param other El sprite con el que comparar
     * @return true si hay colision, false en caso contrario
     */
    public boolean collision(Sprite other)
    {
        float centerX = w/2;
        float centerY = h/2;
        float otherCenterX =  other.w/2;
        float otherCenterY =  other.w/2;

        float fx = (x-centerX > other.x-otherCenterX) ? x-centerX : other.x-otherCenterX;
        float fy = (y-centerY > other.y-otherCenterY) ? y-centerY : other.y-otherCenterY;

        float fw= x-centerX+w < other.x-otherCenterX+other.w ? x-centerX+w : other.x-otherCenterX+other.w;
        float fh= y-centerY+h < other.y-otherCenterY+other.h ? y-centerY+h : other.y-otherCenterY+other.h;


        if(fx<0)
        {
            fw+=fx;
            fx=0;
        }

        if(fy<0)
        {
            fh+=fy;
            fy=0;
        }


        if( fw > fx && fh > fy )
        {
            return true;
        }

        return false;
    }

    /**
     * Sets Zoom for object
     * @param z The new zoom (100%=1)
     */
    public void setZoom(float z)
    {
        zoom=z;
        if(map!=null) {
            w = map.getWidth() * zoom;
            h = map.getHeight() * zoom;
            updateNeeded = true;
        }
    }

    /**
     * Set map to paint
     * @param m The new map
     */
    public void setMap(Map m)
    {
        map =m;
        if(map!=null) {
            w = map.getWidth() * zoom;
            h = map.getHeight() * zoom;
            updateNeeded = true;
        }
    }

    /**
     * Updates transformation matrix
     */
    private void updateMatrix()
    {
        Matrix.setIdentityM(transformMatrix,0);
        // Translate -> Rotate -> Scale
        Matrix.translateM(transformMatrix, 0, -x, y, z);
        Matrix.rotateM(transformMatrix, 0, 180-angle,0f, 0f, 1f  );
        Matrix.scaleM(transformMatrix, 0, w,h,1);

        updateNeeded=false;
    }

    @Override
    public void render(float[] mvpMatrix)
    {
        if(map==null)
            return;

        try {
            GLES20.glUseProgram(mProgram);
        }
        catch (Exception ex)
        {
            Log.e (TAG,"Error:",ex);
        }

        //get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        if (mPositionHandle == -1) Log.e(TAG, "vPosition not found");

        //get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        if (mMVPMatrixHandle == -1) Log.e(TAG, "uMVPMatrix not found");

        //get handle to mapContainer coordinate variable
        int vsTextureCoord = GLES20.glGetAttribLocation(mProgram, "TexCoordIn");
        if (vsTextureCoord == -1) Log.e(TAG, "TexCoordIn not found");

        //get handle to shape's mapContainer reference
        int fsTexture = GLES20.glGetUniformLocation(mProgram, "MapContainer");
        if (fsTexture == -1) Log.e(TAG, "MapContainer not found");

        if(updateNeeded)
        {
            updateMatrix();
        }

        float[] scratch = new float[16];
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, transformMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);


        // Prepare the shape coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer);

        GLES20.glVertexAttribPointer(vsTextureCoord, COORDS_PER_TEXTURE,
                GLES20.GL_FLOAT, false,
                TEXTURE_STRIDE, map.getBuffer());

        // Enable a handle to the shape vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(vsTextureCoord);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, map.getHandle());
        GLES20.glUniform1i(fsTexture, 0);


        //Draw the shape
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);


        //Disable vertex array
        GLES20.glDisableVertexAttribArray(vsTextureCoord);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    public void finalize()
    {
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        GLES20.glDeleteProgram(mProgram);
        mProgram=-1;
    }

}
