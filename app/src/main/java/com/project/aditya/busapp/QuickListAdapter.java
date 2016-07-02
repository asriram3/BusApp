package com.project.aditya.busapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
public class QuickListAdapter extends ArrayAdapter<String> implements GetBusTimes.onReceivedBusTimes{

    List<String> myList;
    JSONObject stopInfo;
    boolean[] shown;
    List<ArrayList<BusTimes>> quickTimesList;
    private int currPos;

    public QuickListAdapter(Context context, int resource, List<String> items){
        super(context, resource, items);
        myList = items;
        shown = new boolean[items.size()];
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View v = convertView;
        currPos = position;

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
            for(String str : services){
                View child = li.inflate(R.layout.simple_list_item, null);

                TextView tv = (TextView) child.findViewById(R.id.textView_simple);
                tv.setText(str);

                serviceLayout.addView(child);
            }
        }

        stopText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BusStopActivity.class);
                intent.putExtra("number", stop_num);
                getContext().startActivity(intent);
            }
        });


        final ImageButton stopButton = (ImageButton)v.findViewById(R.id.button_quickStop);
        if(shown[position]){
            stopButton.setImageResource(android.R.drawable.arrow_up_float);
        }
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shown[position]){
                    serviceLayout.removeAllViews();
                    stopButton.setImageResource(android.R.drawable.arrow_down_float);
                    shown[position] = false;
                }
                else{
                    for(String str : services){
                        View child = li.inflate(R.layout.simple_list_item, null);

                        TextView tv = (TextView) child.findViewById(R.id.textView_simple);
                        tv.setText(str);
                        serviceLayout.addView(child);
                    }
                    stopButton.setImageResource(android.R.drawable.arrow_up_float);
                    shown[position] = true;
                }
            }
        });

        return v;
    }

    @Override
    public void onReceived(ArrayList<BusTimes> busTimes) {

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
