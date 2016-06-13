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

    public BusStop(String no, String nam, double lt, double lg){
        num = no;
        name = nam;
        lat = lt;
        lng = lg;
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
}
