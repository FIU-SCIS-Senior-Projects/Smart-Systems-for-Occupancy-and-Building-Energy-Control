package fiu.ssobec.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;

import fiu.ssobec.R;
import fiu.ssobec.Services.Threads.LocationThread;

/**
 *  This background service class will be in charge of keeping track of the user's current indoor location when started
 */
public class IndoorLocationService extends Service {

    private final IBinder myBinder = new LocalBinder();
    private LocationThread location;
    static boolean isRunning = false;
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public class LocalBinder extends Binder {
        public IndoorLocationService getService() {
            return IndoorLocationService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform your long running operations here.
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        try {
            location = new LocationThread(this,ACTIVITY_SERVICE);
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smartbuildingicon);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(getText(R.string.notificaiontitle))
                    .setContentText(getText(R.string.notificationmsg))
                    .setSmallIcon(R.drawable.smartbuildingicon)
                    .setLargeIcon(icon)
                    .setWhen(System.currentTimeMillis())
                    .build();
            startForeground(5, notification);
            isRunning = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        location.start();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        location.setRun(false);
        // Officially Declare this Service as "Not Running"
        isRunning = false;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

}
