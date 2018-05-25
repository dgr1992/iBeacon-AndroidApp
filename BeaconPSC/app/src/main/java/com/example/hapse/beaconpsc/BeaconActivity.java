package com.example.hapse.beaconpsc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer{

    //search for this TAG in the logcat
    public static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    private TextView _myTextView;

    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        checkPermissions();

        //Get reference to text view
        _myTextView = (TextView) findViewById(R.id.myTextView);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        //We need to tell the library how the layout of the different beacon types look like
        //IBEACON
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //EDDYSTONE  URL
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        //ALTBEACON
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

    }

    private void checkPermissions(){
        // Check for bluetooth
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        // Check for location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        //Set up a notifier that notifies when there is beacon
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon beacon;
                if (beacons.size() > 0) {
                    beacon = beacons.iterator().next();
                    Log.i(TAG, "The first beacon I see is about " + beacon.getDistance() + " meters away.");
                    Log.i(TAG, "Reading…" + "\n" + "proximityUuid:" + " " + beacon.getId1() + "\n" +
                            "major:" + " " + beacon.getId2() + "\n" +
                            "minor:" + " " + beacon.getId3());

                    //Format the distance
                    String distance = "";
                    if(beacon.getDistance() < 1){
                        double tmpDistance = beacon.getDistance() * 10;
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        distance = formatter.format(tmpDistance) + " cm";
                    } else {
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        distance = formatter.format(beacon.getDistance()) + " m";
                    }

                    _myTextView.setText("UUID: " + beacon.getId1() + "\n" +
                            "Distance: " + distance + "\n" +
                            "Major: " + beacon.getId2() + "\n" +
                            "Minor: " + beacon.getId3());
                }
            }
        });


        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                try {
                    //Add the region so, after this the beacon will show up in the range notifier
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }
}
