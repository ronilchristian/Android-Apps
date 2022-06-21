package com.example.openweatherapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import com.example.openweatherapp.bean.Daily;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DailyForecast extends AppCompatActivity {
    RecyclerView recyclerView;
    DailyAdapter dailyAdapter;
    List<Daily> dlist;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        recyclerView = findViewById(R.id.recycler);

        Bundle bundle = getIntent().getBundleExtra("List");
        dlist = (ArrayList<Daily>)bundle.getSerializable("List");
        setTitle(getLocation(dlist.get(0).getTimezone(), dlist.get(0).getLon()));

        dailyAdapter = new DailyAdapter(dlist,this);
        recyclerView.setAdapter(dailyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public String getLocation(double loc, double lon) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocation(loc, lon, 1);
            if (address == null || address.isEmpty()) {
                return null;
            }
            String country = address.get(0).getCountryCode();
            String p1 = "";
            String p2 = "";
            if (country.equals("US")) {
                p1 = address.get(0).getLocality();
                p2 = address.get(0).getAdminArea();
            } else {
                p1 = address.get(0).getLocality();
                if (p1 == null)
                    p1 = address.get(0).getSubAdminArea();
                p2 = address.get(0).getCountryName();
            }

            return p1 + ", " + p2;
        } catch (IOException e) {
            return null;
        }
    }
}