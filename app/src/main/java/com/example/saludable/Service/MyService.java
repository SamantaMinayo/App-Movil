package com.example.saludable.Service;

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
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.saludable.MapsActivity;
import com.example.saludable.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MyService extends Service {

    private static final String PACKAGE_NAME =
            "com.example.saludable.Service";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String TAG = MyService.class.getSimpleName ();
    private static final String CHANNEL_ID = "channel_01";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int NOTIFICATION_ID = 12345678;
    private final IBinder mBinder = new LocalBinder ();
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocationant;
    private Location mLocation;
    private float distancia = 0;

    public MyService() {
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient ( this );

        mLocationCallback = new LocationCallback () {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult ( locationResult );
                onNewLocation ( locationResult.getLastLocation () );
            }
        };
        createLocationRequest ();
        getLocation ();
        HandlerThread handlerThread = new HandlerThread ( TAG );
        handlerThread.start ();
        mServiceHandler = new Handler ( handlerThread.getLooper () );
        mNotificationManager = (NotificationManager) getSystemService ( NOTIFICATION_SERVICE );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString ( R.string.app_name );
            NotificationChannel mChannel =
                    new NotificationChannel ( CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT );
            mNotificationManager.createNotificationChannel ( mChannel );
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i ( TAG, "Service started" );
        boolean startedFromNotification = intent.getBooleanExtra ( EXTRA_STARTED_FROM_NOTIFICATION,
                false );
        if (startedFromNotification) {
            removeLocationUpdates ();
            stopSelf ();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged ( newConfig );
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i ( TAG, "in onBind()" );
        stopForeground ( true );
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i ( TAG, "in onRebind()" );
        stopForeground ( true );
        mChangingConfiguration = false;
        super.onRebind ( intent );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i ( TAG, "Last client unbound from service" );
        if (!mChangingConfiguration && Utils.requestingLocationUpdates ( this )) {
            Log.i ( TAG, "Starting foreground service" );
            startForeground ( NOTIFICATION_ID, getNotification () );
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages ( null );
    }
    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void requestLocationUpdates() {
        Log.i ( TAG, "Requesting location updates" );
        Utils.setRequestingLocationUpdates ( this, true );
        startService ( new Intent ( getApplicationContext (), MyService.class ) );
        try {
            mFusedLocationClient.removeLocationUpdates ( mLocationCallback );
            mFusedLocationClient.requestLocationUpdates ( mLocationRequest,
                    mLocationCallback, Looper.myLooper () );
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates ( this, false );
            Log.e ( TAG, "Lost location permission. Could not request updates. " + unlikely );
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i ( TAG, "Removing location updates" );
        try {
            mFusedLocationClient.removeLocationUpdates ( mLocationCallback );
            Utils.setRequestingLocationUpdates ( this, false );
            stopSelf ();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates ( this, true );
            Log.e ( TAG, "Lost location permission. Could not remove updates. " + unlikely );
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent ( this, MyService.class );
        CharSequence text = Utils.getLocationText ( mLocation, distancia );
        intent.putExtra ( EXTRA_STARTED_FROM_NOTIFICATION, true );
        PendingIntent servicePendingIntent = PendingIntent.getService ( this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        PendingIntent activityPendingIntent = PendingIntent.getActivity ( this, 0,
                new Intent ( this, MapsActivity.class ), 0 );
        NotificationCompat.Builder builder = new NotificationCompat.Builder ( this )
                .setContentText ( text )
                .setOngoing ( true )
                .setContentTitle ( "Saludable" )
                .setPriority ( Notification.PRIORITY_HIGH )
                .setSmallIcon ( R.mipmap.ic_launcher )
                .setWhen ( System.currentTimeMillis () );
        builder.setSound ( null );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId ( CHANNEL_ID ); // Channel ID
        }
        return builder.build ();
    }

    public void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation ()
                    .addOnCompleteListener ( new OnCompleteListener<Location> () {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful () && task.getResult () != null) {
                                mLocation = task.getResult ();
                                Intent intent = new Intent ( ACTION_BROADCAST );
                                intent.putExtra ( EXTRA_LOCATION, mLocation );
                                LocalBroadcastManager.getInstance ( getApplicationContext () ).sendBroadcast ( intent );
                            } else {
                                Log.w ( TAG, "Failed to get location." );
                            }
                        }
                    } );
        } catch (SecurityException unlikely) {
            Log.e ( TAG, "Lost location permission." + unlikely );
        }
    }

    public void getLocation() {
        try {
            mFusedLocationClient.requestLocationUpdates ( mLocationRequest,
                    mLocationCallback, Looper.myLooper () );
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates ( this, false );
            Log.e ( TAG, "Lost location permission. Could not request updates. " + unlikely );
        }
    }

    public void onNewLocation(Location location) {
        Log.i ( TAG, "New location: " + location );

        mLocation = location;
        if (mLocationant == null) {
            mLocationant = mLocation;
        }
        Intent intent = new Intent ( ACTION_BROADCAST );
        intent.putExtra ( EXTRA_LOCATION, location );
        LocalBroadcastManager.getInstance ( getApplicationContext () ).sendBroadcast ( intent );
        distancia = distancia + mLocationant.distanceTo ( mLocation );
        mLocationant = mLocation;
        if (serviceIsRunningInForeground ( this )) {
            mNotificationManager.notify ( NOTIFICATION_ID, getNotification () );
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest ();
        mLocationRequest.setInterval ( UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setFastestInterval ( FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setPriority ( LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService (
                Context.ACTIVITY_SERVICE );
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices (
                Integer.MAX_VALUE )) {
            if (getClass ().getName ().equals ( service.service.getClassName () )) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

}
