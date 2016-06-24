package com.project.aditya.busapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuickViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuickViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuickViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private JSONObject jsonObject;

    public QuickViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuickViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuickViewFragment newInstance(String param1, String param2) {
        QuickViewFragment fragment = new QuickViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try{
            jsonObject = new JSONObject(loadJSONFromAsset("stopInfo.json"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quick_view, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction3(uri);
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refresh();
    }

    private ArrayList<String> stopList;
    private QuickListAdapter quickListAdapter;

    public void refresh(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Set<String> stops = sharedPreferences.getStringSet("stops", new HashSet<String>());

        if(stops.isEmpty()){
            Toast.makeText(getContext(), "No stops in QuickView", Toast.LENGTH_SHORT).show();
            return;
        }

        stopList = new ArrayList<>();
        stopList.addAll(stops);

        ListView quickStopList = (ListView) getActivity().findViewById(R.id.listView_quickView);
        quickListAdapter = new QuickListAdapter(getContext(), R.layout.quickview_stop_item, stopList);
        quickStopList.setAdapter(quickListAdapter);
    }

    public void setLocation(Location location)throws JSONException{
        ArrayList<BusStop> busStops = new ArrayList<>();
        for(String str : stopList){
            JSONObject stopInfo = jsonObject.getJSONObject(str);
            String name = stopInfo.getString("name");
            String num = stopInfo.getString("no");
            double lat = stopInfo.getDouble("lat");
            double lng = stopInfo.getDouble("lng");
            BusStop busStop = new BusStop(num, name, lat, lng);
            busStop.setDistance(location.getLatitude(), location.getLongitude());
            busStops.add(busStop);
        }
        Collections.sort(busStops, BusStop.distComparator);

        quickListAdapter.clear();
        for(BusStop busStop: busStops){
            quickListAdapter.add(busStop.num);
        }
        quickListAdapter.notifyDataSetChanged();
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
        void onFragmentInteraction3(Uri uri);
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(filename);
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
