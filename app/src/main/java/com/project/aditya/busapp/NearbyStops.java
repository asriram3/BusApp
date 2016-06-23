package com.project.aditya.busapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.io.BufferedInputStream;
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


    public NearbyStops(Context ctxt){
        context = ctxt;

        lat = 0;
        lng = 0;

        try {
            loadLists();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public NearbyStops(Context ctxt, double lt, double lg){
        context = ctxt;
        lat = lt;
        lng = lg;
//        lat = 1.29694570097768;
//        lng = 103.76710295331488;

        try {
            loadLists();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadLists()throws IOException, ClassNotFoundException{
        latList = getListFromFile("latList.ser");
        longList = getListFromFile("longList.ser");
    }

    public ArrayList<BusStop> getNearbyStops(Location location)throws IOException, ClassNotFoundException{
        lat = location.getLatitude();
        lng = location.getLongitude();
        return getNearbyStops();
    }

    public ArrayList<BusStop> getNearbyStops()throws IOException, ClassNotFoundException{
//        long init = System.currentTimeMillis();

        if(latList == null || longList == null){
            loadLists();
        }

        if(lat==0&&lng==0){
            return new ArrayList<>();
        }


//        System.out.println("Time to get file: " + (System.currentTimeMillis()-init));
//        init = System.currentTimeMillis();

//        for(int i = 0; i<25; i++){
//            System.out.println(longList.get(i).num + "  long: " + longList.get(i).lng);
//        }



        int latmin = getIndex((lat-0.009), 0);
        int latmax = getIndex((lat+0.009), 0);
        int longmin = getIndex((lng-0.009), 1);
        int longmax = getIndex((lng+0.009), 1);

//        System.out.println("Time to get border locations: " + (System.currentTimeMillis()-init));
//        init = System.currentTimeMillis();

        ArrayList<BusStop> final_list = getListFromIndices(latmin, latmax, longmin, longmax);

//        System.out.println("Time to get final list: " + (System.currentTimeMillis()-init));
//        init = System.currentTimeMillis();

        Collections.sort(final_list, BusStop.distComparator);

//        System.out.println("Time to sort final list: " + (System.currentTimeMillis()-init));

//        for(int i = 0; i< final_list.size(); i++){
//            Log.d("BusApp Nearby", "No: "+final_list.get(i).num+" Name: "+final_list.get(i).name+" Dist: "+final_list.get(i).distance);
//        }

        return final_list;
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
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        ArrayList<BusStop> myList = (ArrayList<BusStop>)objectInputStream.readObject();
        return myList;
    }
}
