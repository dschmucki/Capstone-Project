package com.example.catcha.locations.dataadapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.catcha.sync.model.Location;
import com.example.catcha.sync.model.Stations;
import com.example.catcha.sync.rest.TransportService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StationAutoCompleteAdapter extends ArrayAdapter<Location> implements Filterable {

    private TransportService transportService;
    private List<Location> stations;

    public StationAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        initTransportService();
        stations = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    stations = fetchStations(constraint.toString());
                }
                filterResults.values = stations;
                filterResults.count = stations.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Location getItem(int position) {
        return stations.get(position);
    }

    private void initTransportService() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://transport.opendata.ch/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))

                .build();

        transportService = retrofit.create(TransportService.class);
    }

    private List<Location> fetchStations(final String query) {
        Call<Stations> connections = transportService.findLocations(query);

        try {
            Response<Stations> response = connections.execute();

            if (response.isSuccessful()) {
                Stations result = response.body();
                return result.getStations();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
