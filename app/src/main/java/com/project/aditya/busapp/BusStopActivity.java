package com.project.aditya.busapp;

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

public class BusStopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        final Intent intent = getIntent();
        final String num = intent.getStringExtra("number");


        Button quickViewButton = (Button)findViewById(R.id.button_add_to_quickview);
        assert quickViewButton != null;
        quickViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getBaseContext(), AddToQuickViewActivity.class);
                intent1.putExtra("number", num);
                startActivity(intent1);
            }
        });

        if(num==null){
            Toast.makeText(BusStopActivity.this, "No such stop!", Toast.LENGTH_SHORT).show();
            kill_activity();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                GetBusTimes getBusTimes = new GetBusTimes();
                getBusTimes.execute(num);
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView stop_name = (TextView)findViewById(R.id.textView_stop_name);
        TextView stop_no = (TextView)findViewById(R.id.textView_stop_no);



        stop_no.setText(num);

        try{
            JSONObject jsonStopInfo = new JSONObject(loadJSONFromAsset("stopInfo.json"));
            stop_name.setText(jsonStopInfo.getJSONObject(num).getString("name"));
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(BusStopActivity.this, "No such stop!", Toast.LENGTH_SHORT).show();
            kill_activity();
        }

        GetBusTimes getBusTimes = new GetBusTimes();
        getBusTimes.execute(num);

    }


    public class GetBusTimes extends AsyncTask<String, Void, ArrayList<BusTimes>>{

        @Override
        protected ArrayList doInBackground(String... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String BusTimesJsonStr = null;
            ArrayList<BusTimes> busTimes = null;

            try {
                // Construct the URL
                URL url = new URL("http://datamall2.mytransport.sg/ltaodataservice/BusArrival?BusStopID="+params[0]+"&SST=True");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("AccountKey", "QOuhOOUltXtFKtlHrRpD8A==");
                urlConnection.setRequestProperty("UniqueUserID", "5cfedab3-e6f2-4ea7-b016-2e658ae60ca8");
                urlConnection.setRequestProperty("accept", "application/json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    BusTimesJsonStr = null;
                    return  busTimes;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    BusTimesJsonStr = null;
                }
                BusTimesJsonStr = buffer.toString();
//                System.out.println(BusTimesJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                BusTimesJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            ArrayList<BusTimes> bTimes = new ArrayList<>();
            try{
                JSONObject stopJSON = new JSONObject(BusTimesJsonStr);
                int num_stops = getNumberOfServices(stopJSON);

                for(int i = 0;i<num_stops; i++){
                    //bTimes[i] = getBusTimes(stopJSON, i);
                    bTimes.add(getBusTimes(stopJSON, i));
                    //System.out.println("Bus no: "+bTimes.get(i).no+" Time1: "+bTimes.get(i).t1+" Time2: "+bTimes.get(i).t2);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return bTimes;
        }

        @Override
        protected void onPostExecute(ArrayList<BusTimes> busTimes) {
            super.onPostExecute(busTimes);
            if(busTimes!=null){
                for(int i = 0; i<busTimes.size(); i++){
                    System.out.println("Bus no: "+busTimes.get(i).getNum()
                            +" Time1: "+busTimes.get(i).getT1()+" Time2: "+busTimes.get(i).getT2());
                }

                ListView lv = (ListView) findViewById(R.id.services_at_stop);
                ListAdapter mAdapter = new ListAdapter(getBaseContext(), R.layout.service_time_layout_list_item, busTimes);
                lv.setAdapter(mAdapter);
            }
        }

        public int getNumberOfServices(JSONObject myJSON){
            int res = 0;
            try{
                res = myJSON.getJSONArray("Services").length();
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }

        public BusTimes getBusTimes(JSONObject myJSON, int pos){
            BusTimes bt;
            String no = "";
            int t1=-1,t2=-1;
            try{
                JSONObject bus = myJSON.getJSONArray("Services").getJSONObject(pos);
                no = bus.getString("ServiceNo");
                String time1 = bus.getJSONObject("NextBus").getString("EstimatedArrival");
                if(time1.equals("")){
                    bt = new BusTimes(no, -1, -1);
                    return  bt;
                }
                t1 = getTimeFromString(time1);
                String time2 = bus.getJSONObject("SubsequentBus").getString("EstimatedArrival");
                if(time2.equals("")){
                    bt = new BusTimes(no, t1, -1);
                    return bt;
                }
                t2 = getTimeFromString(time2);

            }catch (Exception e){
                e.printStackTrace();
            }
            return new BusTimes(no, t1, t2);
        }

        public int getTimeFromString(String time){
            Calendar cal = Calendar.getInstance();
            int hour = Integer.parseInt(time.substring(11,13))-cal.get(Calendar.HOUR_OF_DAY);
            int offset = hour>0?hour*60:0;
            int minute = Integer.parseInt(time.substring(14,16))-cal.get(Calendar.MINUTE)+offset;
            int second = Integer.parseInt(time.substring(17,19))-cal.get(Calendar.SECOND);
            if(second<0){minute--;}
            return  minute;
        }
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
