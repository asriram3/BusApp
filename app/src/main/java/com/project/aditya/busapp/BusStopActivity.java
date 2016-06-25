package com.project.aditya.busapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class BusStopActivity extends AppCompatActivity implements GetBusTimes.onReceivedBusTimes {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        final Intent intent = getIntent();
        final String num = intent.getStringExtra("number");
        final GetBusTimes.onReceivedBusTimes thisContext = this;

        if(num==null){
            Toast.makeText(BusStopActivity.this, "No such stop!", Toast.LENGTH_SHORT).show();
            kill_activity();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView stop_name = (TextView)findViewById(R.id.textView_stop_name);
        TextView stop_no = (TextView)findViewById(R.id.textView_stop_no);



        stop_no.setText(num);
        String name = null;

        try{
            JSONObject jsonStopInfo = new JSONObject(loadJSONFromAsset("stopInfo.json"));
            name = jsonStopInfo.getJSONObject(num).getString("name");
            stop_name.setText(name);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(BusStopActivity.this, "No such stop!", Toast.LENGTH_SHORT).show();
            kill_activity();
        }

        //so that it can be used in the onClick
        final String nam = name;
        Button quickViewButton = (Button)findViewById(R.id.button_add_to_quickview);
        assert quickViewButton != null;

        final GetBusTimes getBusTimes = new GetBusTimes(this);
        getBusTimes.execute(num);

        quickViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getBaseContext(), AddToQuickViewActivity.class);
                intent1.putExtra("number", num);
                intent1.putExtra("name", nam);
                intent1.putStringArrayListExtra("serviceList", getBusTimes.serviceList);
                startActivity(intent1);
            }
        });
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                GetBusTimes getBusTimes1 = new GetBusTimes(thisContext);
                getBusTimes1.execute(num);
            }
        });

    }

    @Override
    public void onReceived(ArrayList<BusTimes> busTimes) {
        ListView lv = (ListView) findViewById(R.id.services_at_stop);
        ListAdapter mAdapter = new ListAdapter(getBaseContext(), R.layout.service_time_layout_list_item, busTimes);
        assert lv != null;
        lv.setAdapter(mAdapter);
    }


    public void kill_activity(){
        finish();
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
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
