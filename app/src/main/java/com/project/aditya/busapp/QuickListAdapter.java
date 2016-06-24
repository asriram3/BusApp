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
public class QuickListAdapter extends ArrayAdapter<String> {

    List<String> myList;
    JSONObject stopInfo;
    boolean[] shown;

    float low = 10;
    float high = 20;

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

        final LayoutInflater li = LayoutInflater.from(getContext());
        if(v == null){
            v = li.inflate(R.layout.quickview_stop_item, null);
        }

        String stop_num = myList.get(position);
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
        if(Build.VERSION.SDK_INT>=21){
//            stopText.setElevation(low);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Set<String> servicesSet = sharedPreferences.getStringSet(stop_num, null);
        final ArrayList<String> services = new ArrayList<>();
        services.addAll(servicesSet);

        final LinearLayout serviceLayout = (LinearLayout)v.findViewById(R.id.LLquickServices);
        serviceLayout.removeAllViews();

        if(shown[position] && Build.VERSION.SDK_INT>=21){
            System.out.println("View at position " + position + " was set to high");
//            stopText.setElevation(0);
        }

        if(shown[position]){
            for(String str : services){
                View child = li.inflate(R.layout.simple_list_item, null);

                TextView tv = (TextView) child.findViewById(R.id.textView_simple);
                tv.setText(str);

                if(Build.VERSION.SDK_INT>=21)
                    tv.setElevation(low);

                serviceLayout.addView(child);
            }
        }


        stopText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shown[position]){
                    serviceLayout.removeAllViews();

                    if(Build.VERSION.SDK_INT>=21)
                        stopText.setElevation(low);

                    shown[position] = false;
                }
                else{

                    if(Build.VERSION.SDK_INT>=21)
                        stopText.setElevation(high);

                    for(String str : services){
                        View child = li.inflate(R.layout.simple_list_item, null);

                        TextView tv = (TextView) child.findViewById(R.id.textView_simple);
                        tv.setText(str);

                        if(Build.VERSION.SDK_INT>=21)
                            tv.setElevation(low);

                        serviceLayout.addView(child);
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
