package com.project.aditya.busapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Aditya on 14/6/2016.
 */
public class ListSetup {
    Context context;

    public ListSetup(Context ctxt){
        context = ctxt;
    }

    public void setupLists()throws IOException {
        InputStream is = context.getAssets().open("bus-stops.json");
        String str = loadJSONFromAsset();
        int num_stops = 0;

        ArrayList<BusStop> mainList = new ArrayList<BusStop>();

        try{
            JSONObject obj = new JSONObject(str);
            num_stops = obj.getJSONArray("data").length();
            JSONArray arr = obj.getJSONArray("data");
            for(int i = 0; i<num_stops; i++){
                JSONObject stop = arr.getJSONObject(i);
                mainList.add(new BusStop(stop.getString("no"),stop.getString("name"),
                        Double.parseDouble(stop.getString("lat")),
                        Double.parseDouble(stop.getString("lng"))));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<BusStop> latList = new ArrayList<>(mainList);
        ArrayList<BusStop> longList = new ArrayList<>(mainList);

        Collections.sort(latList, BusStop.latComparator);
        Collections.sort(longList, BusStop.longComparator);

        for(int i = 0; i<25; i++){
            System.out.println(latList.get(i).num + "  " + latList.get(i).lat);
        }

        try
        {
            FileOutputStream fos = context.openFileOutput("latList.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(latList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try
        {
            FileOutputStream fos = context.openFileOutput("longList.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(longList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("bus-stops.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
