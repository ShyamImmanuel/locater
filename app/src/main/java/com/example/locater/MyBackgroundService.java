package com.example.locater;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

public class MyBackgroundService extends Service {

    private  static final String CHANNEL_ID="my_channel";
    private  static final String EXTRA_STARTED_FROM_NOTIFICATION="com.example.testlocation"+".started_from_notification";

    private final  IBinder mBinder=new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL=10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL=UPDATE_INTERVAL_IN_MIL/2;
    private static final int NOTI_ID=1223;
    private boolean mChangingConfiguration=false;
    private NotificationManager mNotificationManager;
     private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    public MyBackgroundService(){


    }
    public void onCreate(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationCallback=new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread=new HandlerThread("EDMTDev");
        handlerThread.start();
        mServiceHandler =new Handler(handlerThread.getLooper());
        mNotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel mchannel=new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mchannel);
        }

    }


    public int  onStartCommand(Intent intent,int flags,int startId)
    {
            boolean startedFromNotification=intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false);
            if(startedFromNotification)
            {
                removeLocationUpdates();
                stopSelf();
            }
            return START_NOT_STICKY;
    }
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration=true;
    }

    private void removeLocationUpdates() {
        try{
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                Commom.setRequestingLocationUpdates(this,false);
                stopSelf();

            }catch (SecurityException ex)
            {
                Commom.setRequestingLocationUpdates(this,true);
                Log.e("EDMT_DEV","Lost location permission"+ex);


            }
    }

    private void getLastLocation() {
        try{
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        public void onComplete(Task<Location> task){
                            if(task.isSuccessful()&&task.getResult()!=null)
                                mLocation=task.getResult();
                            else
                                Log.e("EDMT_DEV","Failed to get location");
                        }
                    });
            }catch (SecurityException ex)
        {
            Log.e("EDMT_DEV","Lost location "+ex);
        }
    }

    private void createLocationRequest() {
                locationRequest=new LocationRequest();
                locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
                locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MIL);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    private void onNewLocation(Location lastLocation) {

         mLocation=lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));
        if(serviceIsRunningInForeground(this))
                mNotificationManager.notify(NOTI_ID,getNotificaton());
    }

    private Notification getNotificaton() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        String text = Commom.getLocationText(mLocation);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch_black_24dp, "Launch", activityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp, "Remove", servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Commom.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }


        return builder.build();


    }
    private boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager=(ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;
        return false;
    }


    public class  LocalBinder extends Binder{
        MyBackgroundService getService(){
            return MyBackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)  {
        stopForeground(true);
        mChangingConfiguration=false;
        return mBinder;
    }
    public void onRebind(Intent intent)
    {
        stopForeground(true);
        mChangingConfiguration=false;
        super.onRebind(intent);
    }
    public boolean onUnbind(Intent intent)
    {
        if(!mChangingConfiguration && Commom.requestingLocationUpdates(this))
            startForeground(NOTI_ID,getNotificaton());
        return true;

    }
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}

