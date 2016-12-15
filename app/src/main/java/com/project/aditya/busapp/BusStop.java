package com.project.aditya.busapp;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Aditya on 14/6/2016.
 */
public class BusStop implements Serializable {

    public String num;
    public String name;
    public double lat;
    public double lng;
    public double distance;
    private double R;
    private static final long serialVersionUID = -4523420843013848562L;

    public BusStop(String no, String nam, double lt, double lg){
        num = no;
        name = nam;
        lat = lt;
        lng = lg;
        R = 6378.137;
    }

    public void setDistance(double lat2, double lon2){
        /*
        var R = 6378.137; // Radius of earth in KM
        var dLat = (lat2 - lat1) * Math.PI / 180;
        var dLon = (lon2 - lon1) * Math.PI / 180;
        var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon/2) * Math.sin(dLon/2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        var d = R * c;
        return d * 1000; // meters
         */
        double lat1 = lat;
        double lon1 = lng;

        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        distance =  d * 1000;
    }

    public static Comparator<BusStop> latComparator = new Comparator<BusStop>(){

        @Override
        public int compare(BusStop o1, BusStop o2) {
            double l1 = o1.lat;
            double l2 = o2.lat;
            if(l1>l2){return 1;}
            else if(l1==l2){return 0;}
            else
                return -1;
        }
    };

    public static Comparator<BusStop> longComparator = new Comparator<BusStop>(){

        @Override
        public int compare(BusStop o1, BusStop o2) {
            double l1 = o1.lng;
            double l2 = o2.lng;
            if(l1>l2){return 1;}
            else if(l1==l2){return 0;}
            else
                return -1;
        }

    };

    public static Comparator<BusStop> distComparator = new Comparator<BusStop>() {
        @Override
        public int compare(BusStop lhs, BusStop rhs) {
            double d1 = lhs.distance;
            double d2 = rhs.distance;
            if(d1>d2){return 1;}
            else if(d1==d2){return 0;}
            else
                return -1;
        }
    };
}
