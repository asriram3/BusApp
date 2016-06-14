package com.project.aditya.busapp;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Aditya on 14/6/2016.
 */
public class NearbyStops {
    private Context context;
    private double lat;
    private double lng;

    private ArrayList<BusStop> latList;
    private ArrayList<BusStop> longList;

    public NearbyStops(Context ctxt, double lt, double lg){
        context = ctxt;
        lat = lt;
        lng = lg;
        lat = 1.29694570097768;
        lng = 103.76710295331488;
    }

    public ArrayList<BusStop> getNearbyStops()throws IOException, ClassNotFoundException{
        latList = getListFromFile("latList.ser");
        longList = getListFromFile("longList.ser");

//        for(int i = 0; i<25; i++){
//            System.out.println(longList.get(i).num + "  long: " + longList.get(i).lng);
//        }

        int latmin = getIndex((lat-0.009), 0);
        int latmax = getIndex((lat+0.009), 0);
        int longmin = getIndex((lng-0.009), 1);
        int longmax = getIndex((lng+0.009), 1);

        ArrayList<BusStop> final_list = getListFromIndices(latmin, latmax, longmin, longmax);

        Collections.sort(final_list, BusStop.distComparator);

        for(int i = 0; i< final_list.size(); i++){
            Log.d("BusApp Nearby", "No: "+final_list.get(i).num+" Name: "+final_list.get(i).name+" Dist: "+final_list.get(i).distance);
        }

        return new ArrayList<BusStop>();
    }

    public int getIndex(double val, int latOrLong){
        //latOrLong:
        //0 -> latitude
        //1 -> longitude

        int max = latList.size()-1;
        return binarySearch(0, max, val, latOrLong);

    }

    private ArrayList<BusStop> getListFromIndices(int latmin, int latmax, int longmin, int longmax){
        HashMap<String, Integer> list1 = new HashMap<>();
        for(int i = latmin; i<latmax; i++){
            list1.put(latList.get(i).name, 1);
        }
        ArrayList<BusStop> result = new ArrayList<>();
        for(int i = longmin; i<longmax; i++){
            if(list1.containsKey(longList.get(i).name)){
                BusStop stop = longList.get(i);
                stop.setDistance(lat, lng);
                result.add(stop);
            }
        }
        return result;
    }

    public int binarySearch(int min, int max, double val, int latOrLong){
        while(true){

            if(min>=max-1){return max;}
            int mid = (int)(min + max)/2;

            double check;
            if(latOrLong==0)
                check = latList.get(mid).lat;
            else
                check = longList.get(mid).lng;

            if(val>check){
                min = mid;
            }else{
                max = mid;
            }

        }

    }


    private ArrayList<BusStop> getListFromFile(String filename)throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+"/" + filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ArrayList<BusStop> myList = (ArrayList<BusStop>)objectInputStream.readObject();
        return myList;
    }
}
