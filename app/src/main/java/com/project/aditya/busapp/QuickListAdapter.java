package com.project.aditya.busapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Aditya on 23/6/2016.
 */
public class QuickListAdapter extends ArrayAdapter<String>{

    List<String> myList;
    JSONObject stopInfo;
    boolean[] shown;
    public static ArrayList<BusTimes>[] quickTimesList;
    Context context;

    public QuickListAdapter(Context context, int resource, List<String> items){
        super(context, resource, items);
        this.context = context;
        myList = items;
        shown = new boolean[items.size()];
        quickTimesList = new ArrayList[items.size()];
        shown[0] = true;
        shown[1] = true;
        for(int i = 2; i<items.size(); i++){
            shown[i] = false;
        }
        try{
            stopInfo = new JSONObject(loadJSONFromAsset("stopInfo.json"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class BusTimesUpdater implements GetBusTimes.onReceivedBusTimes{

        private int myPos;
        ArrayList<String> myServices;
        LinearLayout myLayout;
        LayoutInflater li;
        public BusTimesUpdater(int p, ArrayList<String> services, LinearLayout myLayout, LayoutInflater layoutInflater){
            myPos = p;
            myServices = services;
            this.myLayout = myLayout;
            li = layoutInflater;
        }

        @Override
        public void onReceived(ArrayList<BusTimes> busTimes) {
            ArrayList<BusTimes> toRemove = new ArrayList<>();
            for(int i = 0; i<busTimes.size(); i++){
                if(!myServices.contains(busTimes.get(i).getNum())){
                    toRemove.add(busTimes.get(i));
                }
            }
            for(BusTimes busTimes1 : toRemove){
                busTimes.remove(busTimes1);
            }

            quickTimesList[myPos] = busTimes;

            for(BusTimes x : busTimes){
                addServiceChildToLayout(x, myLayout, li);
            }
        }
    }

    public void addServiceChildToLayout(BusTimes x, LinearLayout myLayout, LayoutInflater li){
        View child = li.inflate(R.layout.service_time_layout_list_item, null);

        TextView tv1 = (TextView) child.findViewById(R.id.service_no);
        TextView tv2 = (TextView) child.findViewById(R.id.time_one);
        TextView tv3 = (TextView) child.findViewById(R.id.time_two);

        tv1.setText(x.getNum());

        if(x.getT1()>0)
            tv2.setText(""+x.getT1());
        else if(x.getT1()<=0 && x.getT1()>-3){
            tv2.setText("Arr");
        }
        else if(x.getT1()<-2){
            tv2.setText("No ETA");
        }

        if(x.getT2()>0){
            tv3.setText(""+x.getT2());
        }
        else{
            tv3.setText("No ETA");
        }

        if(x.getT1()<0 && x.getT2()<0){
            tv2.setText("No");
            tv3.setText("ETA");
        }

        myLayout.addView(child);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View v = convertView;

        final LayoutInflater li = LayoutInflater.from(getContext());
        if(v == null){
            v = li.inflate(R.layout.quickview_stop_item, null);
        }

        final String stop_num = myList.get(position);
        if(stop_num==null){return v;}

        String stop_name = "";
        try{
            stop_name = stopInfo.getJSONObject(stop_num).getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }

        String stop = stop_num + " - " + stop_name;
        final TextView stopText = (TextView)v.findViewById(R.id.textView_quickStop);
        stopText.setText(stop);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Set<String> servicesSet = sharedPreferences.getStringSet(stop_num, null);
        final ArrayList<String> services = new ArrayList<>();
        services.addAll(servicesSet);

        final LinearLayout serviceLayout = (LinearLayout)v.findViewById(R.id.LLquickServices);
        serviceLayout.removeAllViews();

        if(shown[position]){
            if(quickTimesList[position]==null){
                GetBusTimes getBusTimes = new GetBusTimes(new BusTimesUpdater(position, services, serviceLayout, li));
                getBusTimes.execute(stop_num);
            }
//            for(String str : services){
//                View child = li.inflate(R.layout.simple_list_item, null);
//
//                TextView tv = (TextView) child.findViewById(R.id.textView_simple);
//                tv.setText(str);
//
//                serviceLayout.addView(child);
//            }
        }


        stopText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shown[position]){
                    serviceLayout.removeAllViews();
                    shown[position] = false;
                }
                else{
//                    for(String str : services){
//                        View child = li.inflate(R.layout.simple_list_item, null);
//
//                        TextView tv = (TextView) child.findViewById(R.id.textView_simple);
//                        tv.setText(str);
//                        serviceLayout.addView(child);
//                    }
                    if(quickTimesList[position]==null){
                        GetBusTimes getBusTimes = new GetBusTimes(new BusTimesUpdater(position, services, serviceLayout, li));
                        getBusTimes.execute(stop_num);
                    }
                    else{
                        ArrayList<BusTimes> busTimes = quickTimesList[position];
                        for(BusTimes busTimes1 : busTimes){
                            addServiceChildToLayout(busTimes1, serviceLayout, li);
                        }
                    }
                    shown[position] = true;
                }
            }
        });

        return v;
    }


    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open(filename);
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
