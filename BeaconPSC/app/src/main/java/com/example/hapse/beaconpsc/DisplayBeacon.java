package com.example.hapse.beaconpsc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by daniel on 5/26/18.
 */

public class DisplayBeacon {
    private String _uuid;
    private String _major;
    private String _minor;
    private double _distance;

    public DisplayBeacon(String uuid,String major,String minor, double distance){
        _uuid = uuid;
        _major = major;
        _minor = minor;
        _distance = distance;
    }

    public double getDistance(){
        return _distance;
    }

    public void setDistance(double distance){
        _distance = distance;
    }

    @Override
    public String toString() {
        String distance = "";
        if(_distance< 1){
            double tmpDistance = _distance * 10;
            NumberFormat formatter = new DecimalFormat("#0.00");
            distance = formatter.format(tmpDistance) + " cm";
        } else {
            NumberFormat formatter = new DecimalFormat("#0.00");
            distance = formatter.format(_distance) + " m";
        }

        return "UUID: " + _uuid + "\n" + "Distance: " + distance + "\n" + "Major: " + _major + "\n" + "Minor: " + _minor;
    }

    @Override
    public boolean equals(Object o){
        boolean equals = true;

        equals &= _uuid.compareTo(((DisplayBeacon)o)._uuid) == 0;
        equals &= _major.compareTo(((DisplayBeacon)o)._major) == 0;
        equals &= _minor.compareTo(((DisplayBeacon)o)._minor) == 0;

        return equals;
    }
}
