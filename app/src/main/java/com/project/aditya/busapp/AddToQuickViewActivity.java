package com.project.aditya.busapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddToQuickViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_quick_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String stop_number = intent.getStringExtra("number");
        String stop_name = intent.getStringExtra("name");
        ArrayList<String> serviceList = intent.getStringArrayListExtra("serviceList");

        TextView stopDeatails = (TextView)findViewById(R.id.textView_ATQuickView_stop);
        stopDeatails.setText(stop_number + " - " + stop_name);

        ListView serviceListView = (ListView)findViewById(R.id.listView_ATQuickView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.quickview_list_item, R.id.textView_ATQuickView_service, serviceList);
        assert serviceListView != null;
        serviceListView.setAdapter(arrayAdapter);
    }

}
