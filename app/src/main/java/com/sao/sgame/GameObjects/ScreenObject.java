package com.sao.sgame.GameObjects;

import android.opengl.GLES20;
import android.util.Log;

import com.sao.sgame.GameObjects.GamePoint;
import com.sao.sgame.Util;

public abstract class ScreenObject implements Comparable<ScreenObject>{

    /* Screen corrdinates */
    protected float x, y, z=50;

    /* Fpr rotations */
    protected float angle;

    /* Para el tamaño del grafico */
    protected float zoom;

    /* Is needed a update? */
    protected boolean updateNeeded = true;

    /**
     * Paints the object
     * @param MVPMatrix The Transformation matrix for display this object
     */
    public abstract void render(float[] MVPMatrix);

    /**
     * Devuelve las coordenadas del sprite
     * @return Las coordenadas del centro del sprite
     */
    public GamePoint getCords()
    {
        return new GamePoint(x,y);
    }

    /**
     * Devuelve la coordenada X del sprite
     * @return La coordenada X
     */
    public float getX()
    {
        return x;
    }

    /**
     * Devuelve la coordenada Y del sprite
     * @return la coordenada Y
     */
    public float getY()
    {
        return y;
    }

    /**
     * Returns the z-order of the sprite
     * @return The z-order of the sprite
     */
    public float getZ() { return z; }

    /**
     * Set the screen coordinates of the object
     * @param x x-Coordinate
     * @param y y-Coordinate
     * @param z z-Order
     */
    public void setCoords(float x, float y, float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
        updateNeeded=true;
    }

    /**
     * Set the screen coordinates of the object
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    public void setCoords(float x, float y)
    {
        this.x=x;
        this.y=y;
        updateNeeded=true;
    }

    public void setCoords(GamePoint g)
    {
        x=g.x;
        y=g.y;
    }


    /**
     * Obtiene la distancia a un punto
     * @param x Coordenada X a la que calcular la distancia
     * @param y Coordenada Y a la que calcular la distancia
     * @return La distancia obtenida
     */
    public float getDistTo(float x, float y)
    {
        float tmpX = x-this.x;
        float tmpY = y-this.y;
        return (float)Math.sqrt( tmpX*tmpX + tmpY*tmpY  );
    }

    /**
     * Obtiene la distancia al sprite indicado
     * @param s El sprite al que calcular la distancia
     * @return La distancia obtenida
     */
    public float getDistTo(Sprite s)
    {
        float tmpX = s.x-this.x;
        float tmpY = s.y-this.y;
        return (float)Math.sqrt( tmpX*tmpX + tmpY*tmpY  );
    }

    @Override
    public int compareTo(ScreenObject o) {
        return (int)(o.z-z);
    }

    /**
     * Establece en angulo del objeto
     * @param angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
        updateNeeded=true;
    }

    /**
     * Devuelve el angulo del objeto
     */
    public float getAngle()
    {
        return  angle;
    }

    /**
     * Funcion de utilidad que devuelve el angulo a unas coordenadas
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return Angulo a las coordenadas indicadas (En grados)
     */
    public float getAngleTo(float x, float y)
    {
        float tmp = (float) Math.toDegrees(Math.atan2(y-this.y,x-this.x));
        return  tmp;
    }

    /**
     * Obtiene el angulo al sprite indicado
     * @param s El sprite al que calcular el angulo
     * @return El angulo obtenido (En grados)
     */
    public float getAngleTo(Sprite s)
    {
        return  (float) Math.toDegrees(Math.atan2(s.y-this.y,s.x-this.x));
    }

    /**
     * Avanza el objeto en la direccion de su angulo
     * @param q Cantidad de puntos a avanzar
     */
    public void advance(float q)
    {
        double a = Math.toRadians(angle);
        x += q * Math.cos(a);
        y += q * Math.sin(a);
        updateNeeded=true;
    }

    /**
     * Avanza el objeto en la direccion de su angulo
     * @param q Cantidad de puntos a avanzar
     * @param angle El angulo en el que avanzar (En grados)
     */
    public void advance(float q, float angle)
    {
        double a = Math.toRadians(angle);
        x += q * Math.cos(a);
        y += q * Math.sin(a);
        updateNeeded=true;
    }
}
