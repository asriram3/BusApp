package com.project.aditya.busapp;

import android.content.Context;
import android.content.SharedPreferences;
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

    public QuickListAdapter(Context context, int resource, List<String> items){
        super(context, resource, items);
        myList = items;
        try{
            stopInfo = new JSONObject(loadJSONFromAsset("stopInfo.json"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        LayoutInflater li = LayoutInflater.from(getContext());
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
        TextView stopText = (TextView)v.findViewById(R.id.textView_quickStop);
        stopText.setText(stop);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Set<String> servicesSet = sharedPreferences.getStringSet(stop_num, null);
        ArrayList<String> services = new ArrayList<>();
        services.addAll(servicesSet);

        LinearLayout serviceLayout = (LinearLayout)v.findViewById(R.id.LLquickServices);
        serviceLayout.removeAllViews();

        for(String str : services){
            View child = li.inflate(R.layout.simple_list_item, null);

            TextView tv = (TextView) child.findViewById(R.id.textView_simple);
            tv.setText(str);

            serviceLayout.addView(child);
        }

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
