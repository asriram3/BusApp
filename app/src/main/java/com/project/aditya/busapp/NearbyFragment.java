package com.project.aditya.busapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private double mParam1;
    private double mParam2;

    private ListView nearbyListView;
    private Location location;
    private ArrayAdapter<String> myAdapter;

    private OnFragmentInteractionListener mListener;

    public NearbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearbyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance(double param1, double param2) {

        NearbyFragment fragment = new NearbyFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, param1);
        args.putDouble(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getDouble(ARG_PARAM1);
            mParam2 = getArguments().getDouble(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction2(uri);
        }
    }

    public void setLocation(Location loc){
        location = loc;
        mParam1 = location.getLatitude();
        mParam2 = location.getLongitude();
        setLocation();
    }

    public void setLocation(){
        TextView tv = (TextView)getActivity().findViewById(R.id.textView_nearby);
        if(mParam1==-1.0 && mParam2==-1.0){
            tv.setText("Press the floating action button!");
            tv.setPadding(15,15,15,15);
        }
        else{
//            tv.setText("Lat: "+mParam1+"; Long: "+mParam2);
            tv.setVisibility(View.INVISIBLE);
            tv.setPadding(0,0,0,0);
            FetchNearbyLocations fetchNearbyLocations = new FetchNearbyLocations();
            fetchNearbyLocations.execute(location);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        myAdapter = new ArrayAdapter<>(getContext(), R.layout.nearby_list_item, R.id.textView_nearby_item);
        nearbyListView = (ListView)getActivity().findViewById(R.id.listView_nearby);
        nearbyListView.setAdapter(myAdapter);
        nearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stop = myAdapter.getItem(position).substring(0, 5);
                Intent intent = new Intent(getContext(), BusStopActivity.class);
                intent.putExtra("number", stop);
                startActivity(intent);
            }
        });
        setLocation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    public class FetchNearbyLocations extends AsyncTask<Location, Void, ArrayList<BusStop>> {

        @Override
        protected ArrayList<BusStop> doInBackground(Location... params) {
            NearbyStops nearby = new NearbyStops(getContext(), params[0].getLatitude(), params[0].getLongitude());
            ArrayList<BusStop> res = null;
            try {
                long init = System.currentTimeMillis();
                res =  nearby.getNearbyStops();
                long fin = System.currentTimeMillis();
                Log.d("BusApp Nearby", "Calculating nearby stops took " + (fin - init) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<BusStop> busStops) {
            if(busStops!=null)
                setNearbyLocations(busStops);
        }
        public void setNearbyLocations(ArrayList<BusStop> busStops) {

            System.out.println("setNearbyLocations called");

            for(int i = 0; i<busStops.size(); i++){
                myAdapter.add(busStops.get(i).num + " - " + busStops.get(i).name);
            }
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction2(Uri uri);
    }
}
