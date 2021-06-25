package com.sao.sgame.Ui;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.sao.sgame.GameObjects.Brain;
import com.sao.sgame.GameObjects.GamePoint;
import com.sao.sgame.GameObjects.ScreenObject;
import com.sao.sgame.GameObjects.Sprite;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Esta clase implementa una superficie donde pintar los graficos, incuye so propio renderer
 */
public class SGameSurfaceView
        extends GLSurfaceView
        implements GLSurfaceView.Renderer{

    Sprite s = null;

    // Parametros para la visualización
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private boolean needCameraUpdate=true;
    float cameraX=0, cameraY=0, cameraZ=-5;

    private float viewWidth, viewHeight; // Tamaño real de la vista
    private float desiredHRes=640, desiredVRes=400; // Resolucion deseada
    private float effectiveHRes,effectiveVRes; // Resolucion obtenida debido al factor de forma del dispositivo
    private float hScale,vScale; // Cuantos pixeles reales corresponden a cada uno virtual

    private LinkedList<ScreenObject> screenObjects;
    private LinkedList<Brain> brains;
    private LinkedList<Brain> toStart;

    private LinkedList<GamePoint> preTouches = new LinkedList<>();
    private LinkedList<GamePoint> touches = new LinkedList<>();
    private boolean firstFrame = true;
    private boolean running = false;
    private SGameViewListener listener;

    public SGameSurfaceView(Context context, SGameViewListener lst){
        super(context);

        brains = new LinkedList<>();
        toStart = new LinkedList<>();
        screenObjects = new LinkedList<>();
        listener=lst;

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        //setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDepthMask( true );
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewWidth=width;
        viewHeight=height;
        setRes(desiredHRes,desiredVRes);
    }

    public void setRes(float w, float h)
    {
        effectiveHRes = w;
        effectiveVRes = h;

        // Si la anchura es mayor que la altura intentamos conservar la altura
        if(viewWidth>viewHeight)
        {
            float ratio = viewWidth/viewHeight;
            effectiveVRes=effectiveHRes/ratio;
        }
        else
        {
            float ratio = viewHeight/viewWidth;
            effectiveHRes=effectiveVRes/ratio;
        }
        hScale = viewWidth / effectiveHRes;
        vScale = viewHeight / effectiveVRes;

        Matrix.orthoM(projectionMatrix, 0,  0, effectiveHRes, effectiveVRes, 0, 0f, 100f);
        //Matrix.frustumM(projectionMatrix,0,0,effectiveHRes,0,effectiveVRes,0.1f,100);
        //Matrix.scaleM(projectionMatrix,0, w,h,1);

        needCameraUpdate=true;
        // Definimos cono de visualizacion

    }

    /**
     * Devuelve la resolucion efectiva horizontal de la vista
     * @return La resolucion efectiva horizontal
     */
    public float getEffectiveHRes()
    {
        return effectiveHRes;
    }

    /**
     * Devuelve la resolucion efectiva vertical de la vista
     * @return La resolucion efectiva vertical
     */
    public float getEffectiveVRes()
    {
        return effectiveVRes;
    }

    public void setCameraDistance(float d)
    {
        cameraZ=-d;
        needCameraUpdate=true;
    }

    public void moveCamera(float x, float y)
    {
        cameraX=x;
        cameraY=y;
        needCameraUpdate=true;
    }

    public float getCameraX()
    {
        return cameraX;
    }

    public float getCameraY()
    {
        return cameraY;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(firstFrame && listener!=null)
        {
            listener.onGameStarted(this);
            firstFrame=false;
            running=true;
        }


        if(needCameraUpdate) {
            // Definimos donde miramos

            Matrix.setLookAtM(viewMatrix, 0,
                    cameraX, cameraY, cameraZ, //Posicion de la cámara
                    cameraX, cameraY, 0f, // Posición a la que mira la camara
                    0f, 1.0f, 0.0f ); // Rotación de la cámara
            Matrix.multiplyMM(vPMatrix,0, projectionMatrix, 0, viewMatrix, 0);
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        touches = preTouches;
        preTouches = new LinkedList<>();

        if(running)
        {
            Iterator<Brain> iter = toStart.iterator();
            while (iter.hasNext())
            {
                Brain g = iter.next();
                g.wakeUp();
                brains.add(g);
                iter.remove();
            }

            iter= brains.iterator();
            while(iter.hasNext())
            {
                Brain g = iter.next();
                if(g.getStatus() == Brain.AWAKE)
                    g.think();
            }

            iter= brains.iterator();
            while(iter.hasNext())
            {
                Brain g = iter.next();
                if(g.getStatus() == Brain.DEAD)
                    iter.remove();
            }
        }

        Collections.sort(screenObjects);
        Iterator<ScreenObject> rndi= screenObjects.iterator();
        while(rndi.hasNext())
        {
            rndi.next().render(vPMatrix);
        }

        if(brains.size()==0 )
        {
            if( listener!=null )
                listener.onGameEnd(this);
            running=false;
        }
    }

    /**
     * Añade un objeto al juego
     * @param o El objeto a añadir
     */
    public void addBrain(Brain o)
    {
        toStart.push(o);
    }

    /**
     * Devuelve los objetos en el juego
     * @return Los objetos presentes en el juego
     */
    public LinkedList<Brain> getBrains()
    {
        return new LinkedList<>(brains);
    }

    /**
     * Añade un objeto al juego
     * @param o El objeto a añadir
     */
    public void addRenderable(ScreenObject o)
    {
        screenObjects.push(o);
    }

    public void removeRenderable(ScreenObject o)
    {
        screenObjects.remove(o);
    }

    /**
     * Devuelve los renderables en el juego
     * @param c La clase de los objetos que queremos
     * @return Los objetos presentes en el juego
     */
    public LinkedList<ScreenObject> getRenderables(Class c)
    {
        return new LinkedList<>(screenObjects);
    }

    /**
     * Devuelve los cerebros en el juego
     * @param c La clase de los objetos que queremos
     * @return Los objetos presentes en el juego
     */
    public LinkedList<Brain> getBrains(Class c)
    {
        if(c==null)
            return new LinkedList<>(brains);

        LinkedList<Brain> res = new LinkedList<>();
        Iterator<Brain> rndi=brains.iterator();
        while(rndi.hasNext())
        {
            Brain o = rndi.next();
            if(c.isInstance(o))
                res.add(o);
        }
        return res;
    }


    public interface SGameViewListener {
        void onGameStarted(SGameSurfaceView src);
        void onGameEnd(SGameSurfaceView src);
    }

    public LinkedList<GamePoint> getTouches() {
        return touches;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if(action!=MotionEvent.ACTION_DOWN && action!=MotionEvent.ACTION_POINTER_DOWN && action!=MotionEvent.ACTION_MOVE)
            return false;

        for(int i=0; i<event.getPointerCount(); i++)
        {
            GamePoint p = new GamePoint(event.getX(i)/hScale,event.getY(i)/vScale);
            preTouches.add(p);
        }
        return true;
    }


}

