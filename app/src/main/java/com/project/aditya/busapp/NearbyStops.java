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

    private ArrayList<BusStop> StopList;

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


    public void loadLists()throws IOException, ClassNotFoundException{
        StopList = getListFromFile("latList.ser");
    }

    public ArrayList<BusStop> getNearbyStops(Location location)throws IOException, ClassNotFoundException{
        lat = location.getLatitude();
        lng = location.getLongitude();
        return getNearbyStops();
    }

    public ArrayList<BusStop> getNearbyStops()throws IOException, ClassNotFoundException{
//        long init = System.currentTimeMillis();

        if(StopList == null){
            loadLists();
        }

        if(lat==0&&lng==0){
            return new ArrayList<>();
        }

        for( BusStop busStop : StopList){
            busStop.setDistance(lat, lng);
        }
        Collections.sort(StopList, BusStop.distComparator);

        return getFinalList();
    }

    public ArrayList<BusStop> getFinalList(){
        ArrayList<BusStop> res = new ArrayList<>();
        for(int i = 0; i< 50; i++){
            res.add(StopList.get(i));
        }
        return res;
    }


    private ArrayList<BusStop> getListFromFile(String filename)throws IOException, ClassNotFoundException{
        FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+"/" + filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        ArrayList<BusStop> myList = (ArrayList<BusStop>)objectInputStream.readObject();
        return myList;
    }
}
