package fiu.ssobec.Services.Threads;

import android.content.Context;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by ShadowFox on 9/21/2015.
 */
public class LocationThread extends Thread {
    Context context;
    boolean run;
    private String activity_service;
    private long beginTime;
    private long timeDiff;
    private int sleepTime;
    private final int POLL_PERIOD = 5000; //How many seconds between polls

    public LocationThread(Context context, String activity_service) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.context = context;
        this.activity_service = activity_service;
        run = true;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public synchronized void start() {
        super.start();
    }
    public void setRun(boolean run)
    {
        this.run = run;
    }

    @Override
    public void run() {
        //Anything inside this loop runs multiple times
        while(run) {
            beginTime = System.currentTimeMillis();

            timeDiff = System.currentTimeMillis() - beginTime;
            sleepTime = (int) (POLL_PERIOD - timeDiff);
            doISleepTime(sleepTime);
        }
    }

    private void doISleepTime(int sleepTime)
    {
        if (sleepTime > 0) {
            try {
                // send the thread to sleep for a short period to save battery
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

            }
        }
    }
}
