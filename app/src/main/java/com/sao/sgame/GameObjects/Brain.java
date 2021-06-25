package com.sao.sgame.GameObjects;

import com.sao.sgame.Ui.SGameSurfaceView;

/**
 * Clase que representa el "cerebro" de los objetos de los juegos
 */
public abstract class Brain {

    public static final int DEAD = 0; /** Matar el objeto */
    public static final int SLEEPING = 1; /** Matar el objeto */
    public static final int AWAKE = 2; /** Matar el objeto */

    protected int status;
    protected SGameSurfaceView game;

    /**
     * Constructor de clase
     * @param game El juego "padre"
     */
    public Brain(SGameSurfaceView game)
    {
        status = DEAD;
        this.game = game;
    }

    /**
     * Occurs when the game is building another frame and tells to this brain to "think" about what to do
     */
    public abstract void think();

    /**
     * Kills this brain.
     * When a brain receives this signal, an onKill event is called
     */
    public void kill()
    {
        onKill();
        status= DEAD;
    }

    /**
     * Occurs when a brain is about to be killed
     */
    public void onKill()
    {}

    /**
     * Tells the brain to "go to sleep".
     * A sleeping brain do not think about anything (think is not called)
     */
    public void sleep()
    {
        onSleep();
        status=SLEEPING;
    }

    /**
     * Occurs when a brain is about to go to sleep
     */
    protected void onSleep()
    {}

    /**
     * Tells te brain to wakeup
     */
    public void wakeUp()
    {
        onWakeUp();
        status= AWAKE;
    }

    /**
     * Occurs when a brain is about to wakeUp.
     * Also, when a brain is created and added to a game, is in sleeping state.
     * Game tells the brain to wake up automatically in the next frame.
     * Its possible to know if is the first time the process is waking up because status on this time
     * is "DEAD"
     */
    protected void onWakeUp()
    {}

    /**
     * Return the current status of the brain
     * @return
     */
    public int getStatus()
    {
        return status;
    }

}
