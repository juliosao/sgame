package com.sao.sgame.GameObjects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import com.sao.sgame.ResourceManagement.Map;
import com.sao.sgame.ResourceManagement.MapLoader;
import com.sao.sgame.ResourceManagement.Shader;
import com.sao.sgame.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Esta clase sirve para pintar mapas de bit flotantes en la pantalla
 */
public class TileGrid extends ScreenObject {
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

    private static final String TAG = "Sprite";
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * Util.SIZEOF_FLOAT;
    private static final int COORDS_PER_TEXTURE = 2;
    private static final int TEXTURE_STRIDE = COORDS_PER_TEXTURE * Util.SIZEOF_FLOAT; // 4 bytes per mapContainer vertex

    /* Para almacenar las texturas */
    private Map[][] maps;
    private float[] texCords;
    private FloatBuffer textureCordsBuffer;

    /* Para almacenar los vertices */
    protected FloatBuffer vertexBuffer = null;

    protected  int mProgram = -1;
    private int vertexShader = 0;
    private int fragmentShader = 0;

    /* Para las rotaciones */
    private float colW,rowH;
    private float w, h;
    private int colN, rowN;
    private int vertexCount;
    protected MapLoader mapLoader;

    /* Para el tamaño del grafico */
    float zoom;

    /* Matriz de trasformaciones */
    private boolean updateNeeded=true;
    private float[] transformMatrix = new float[16];

    public TileGrid(int rows, int cols, int colWidth, int rowHeight, MapLoader m)
    {
        super();

        rowN = rows;
        colN = cols;
        mapLoader = m;
        maps = new Map[rows][cols];
        colW = colWidth;
        rowH = rowHeight;

        if(mProgram == -1)
        {
            mProgram = GLES20.glCreateProgram();

            vertexShader = Shader.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            fragmentShader = Shader.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            mProgram = GLES20.glCreateProgram();

            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
        }

        vertexCount = rows*cols*18;
        float[] coords = new float[vertexCount];

        // Rows
        for(int i=0; i<rows; i++)
        {
            // Cols
            for(int j=0; j<cols; j++)
            {
                //First triangle
                coords[(i*cols+j)*18]=i; //x
                coords[(i*cols+j)*18+1]=j; //y
                coords[(i*cols+j)*18+2]=0; //z

                coords[(i*cols+j)*18+3]=i+1;
                coords[(i*cols+j)*18+4]=j;
                coords[(i*cols+j)*6+5]=0;

                coords[(i*cols+j)*18+6]=i;
                coords[(i*cols+j)*18+7]=j+1;
                coords[(i*cols+j)*6+8]=0;

                //Second triangle
                coords[(i*cols+j)*18+9]=i;
                coords[(i*cols+j)*6+10]=j+1;
                coords[(i*cols+j)*6+11]=0;

                coords[(i*cols+j)*18+12]=i+1;
                coords[(i*cols+j)*18+13]=j+1;
                coords[(i*cols+j)*18+14]=0;

                coords[(i*cols+j)*18+15]=i;
                coords[(i*cols+j)*18+16]=j+1;
                coords[(i*cols+j)*18+17]=0;
            }
        }

        //Vertices to draw
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * Util.SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

        updateMapBuffer();

        setCoords(0,0);
        setAngle(0);
        setZoom(1);
    }

    protected void updateMapBuffer()
    {
        texCords = new float[rowN * colN * 6 * COORDS_PER_TEXTURE];
        // Rows
        for(int i=0; i<rowN; i++)
        {
            // Cols
            for(int j=0; j<colN; j++)
            {
                //First triangle
                texCords[(i*colN+j)*18]=i; //x
                texCords[(i*colN+j)*18+1]=j; //y

                texCords[(i*colN+j)*18+3]=i+1;
                texCords[(i*colN+j)*18+4]=j;

                texCords[(i*colN+j)*18+6]=i;
                texCords[(i*colN+j)*18+7]=j+1;

                //Second triangle
                texCords[(i*colN+j)*18+9]=i;
                texCords[(i*colN+j)*6+10]=j+1;

                texCords[(i*colN+j)*18+12]=i+1;
                texCords[(i*colN+j)*18+13]=j+1;

                texCords[(i*colN+j)*18+15]=i;
                texCords[(i*colN+j)*18+16]=j+1;

            }
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(texCords.length * Util.SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        textureCordsBuffer = bb.asFloatBuffer();
        textureCordsBuffer.put(texCords);
        textureCordsBuffer.position(0);
    }

    /**
     * Establece el zoom del objeto
     * @param z El nuevo zoom (100%=1)
     */
    public void setZoom(float z)
    {
        zoom=z;
        w =colW * colN * zoom;
        h = rowH * rowN * zoom;
        updateNeeded = true;

    }

    /**
     * Establece el mapa a pintar
     * @param m El map a pintar
     */
    public void setMap(int row, int col, Map m)
    {
        if(row<0 || col<0 || row>rowN || col>colN)
            throw new IndexOutOfBoundsException();

        maps[row][col]=m;
    }

    /**
     * Actualiza la matriz de transformacion
     */
    private void updateMatrix()
    {
        Matrix.setIdentityM(transformMatrix,0);
        // Translate -> Rotate -> Scale
        Matrix.translateM(transformMatrix, 0, -x, y, z);
        Matrix.rotateM(transformMatrix, 0, 180-angle,0f, 0f, 1f  );
        Matrix.scaleM(transformMatrix, 0, colW,rowH,1);

        updateNeeded=false;
    }

    @Override
    public void render(float[] mvpMatrix)
    {
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
                TEXTURE_STRIDE, textureCordsBuffer);

        // Enable a handle to the shape vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(vsTextureCoord);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mapLoader.getHandle() );
        GLES20.glUniform1i(fsTexture, 0);


        //Draw the shape
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);


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
