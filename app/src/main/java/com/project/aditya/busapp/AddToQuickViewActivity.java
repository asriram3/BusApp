package com.project.aditya.busapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddToQuickViewActivity extends AppCompatActivity {

    private String stop_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_quick_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String stop_number = intent.getStringExtra("number");
        stop_num = stop_number;
        String stop_name = intent.getStringExtra("name");
        ArrayList<String> serviceList = intent.getStringArrayListExtra("serviceList");

        TextView stopDeatails = (TextView)findViewById(R.id.textView_ATQuickView_stop);
        stopDeatails.setText(stop_number + " - " + stop_name);

        ListView serviceListView = (ListView)findViewById(R.id.listView_ATQuickView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.quickview_list_item, R.id.textView_ATQuickView_service, serviceList);
//        serviceListView.setAdapter(arrayAdapter);

        assert serviceListView != null;
        final MyCustomAdapter myCustomAdapter = new MyCustomAdapter(this, R.layout.quickview_list_item, R.id.textView_ATQuickView_service, serviceList);
        serviceListView.setAdapter(myCustomAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ArrayList<Service> arrayList = myCustomAdapter.serList;
                StringBuffer resposeText = new StringBuffer();
                resposeText.append("The following services were selected: \n");
                for(Service ser : arrayList){
                    if(ser.getSelected()){
                        resposeText.append( "\n" + ser.getNumber());
                    }
                }

                //Toast.makeText(AddToQuickViewActivity.this, resposeText, Toast.LENGTH_SHORT).show();

                addQuickView(stop_number, arrayList);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addQuickView(String num, ArrayList<Service> services){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> mySet = new HashSet<>();
        for(Service serv : services){
            if(serv.getSelected()){
                mySet.add(serv.getNumber());
            }
        }
        editor.putStringSet(num, mySet);

        Set<String> stops = sharedPreferences.getStringSet("stops", null);
        if(stops==null){
            stops = new HashSet<>();
        }
        if(!stops.contains(num)){
            stops.add(num);
            Toast.makeText(AddToQuickViewActivity.this, "Stop "+num+" was added to shared Preferences", Toast.LENGTH_SHORT).show();
        }
        editor.putStringSet("stops", stops);

        editor.commit();
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {
        //http://www.mysamplecode.com/2012/07/android-listview-checkbox-example.html

        public ArrayList<Service> serList;

        public MyCustomAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
            this.serList = new ArrayList<>();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> mySet = sharedPreferences.getStringSet(stop_num, null);

            for(String str : objects){
                boolean checked = false;
                if(mySet!= null && mySet.contains(str)){
                    checked = true;
                }
                serList.add(new Service(str, checked));
            }
        }

        private class ViewHolder {
            TextView num;
            CheckBox cbox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.quickview_list_item, null);

                holder = new ViewHolder();
                holder.num = (TextView) convertView.findViewById(R.id.textView_ATQuickView_service);
                holder.cbox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.cbox.setOnClickListener(onClickListener);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Service service = serList.get(position);
            holder.num.setText(service.getNumber());
            holder.cbox.setText(service.getNumber());
            holder.cbox.setChecked(service.getSelected());
            holder.cbox.setTag(service);

            return convertView;

        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v ;
                Service service = (Service) cb.getTag();
//                Toast.makeText(getApplicationContext(),
//                        "Clicked on Checkbox: " + cb.getText() +
//                                " is " + cb.isChecked(),
//                        Toast.LENGTH_SHORT).show();
                service.setSelected(cb.isChecked());
            }
        };

    }

    private class Service{
        String number = null;
        boolean selected = false;

        public Service(String num, boolean sel){
            number = num;
            selected = sel;
        }

        public String getNumber(){
            return number;
        }
        public void setNumber(String num){
            number = num;
        }
        public boolean getSelected(){
            return selected;
        }
        public void setSelected(boolean sel){
            selected = sel;
        }
    }


}
