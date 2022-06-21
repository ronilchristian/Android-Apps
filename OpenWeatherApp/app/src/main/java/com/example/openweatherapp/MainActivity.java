package com.example.openweatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.openweatherapp.Services.Services;
import com.example.openweatherapp.bean.Daily;
import com.example.openweatherapp.bean.Hourly;
import com.example.openweatherapp.bean.WeatherData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final ArrayList<UserSettiings> plist = new ArrayList<>();

    TextView location, datetime, temp, feels_like, weather_desc, wind_desc;
    TextView humidity, uvi, visibility;
    TextView morning_temp, daytime_temp, evening_temp, night_temp;
    TextView sunrise, sunset;
    ImageView imageView;
    SwipeRefreshLayout swipeRefresh;
    HourlyAdapter hourlyAdapter;
    RecyclerView recyclerView;
    List<Hourly> hlist = new ArrayList<>();
    List<Daily> dlist = new ArrayList<>();
    Services services = new Services();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
    DateTimeFormatter sun = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
    double[] LatLon={0,0};
    String unit = "imperial";
    double lat = 41.8675766;
    double lon = -87.616232;
    int flag = 0;
    Bundle savedInstance;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        savedInstance = savedInstanceState;
        super.onCreate(savedInstanceState);

        plist.clear();
        plist.addAll(loadFile());
        UserSettiings us = plist.get(0);
        unit = us.getUnit();
        lat = us.getLat();
        lon = us.getLon();

        setContentView(R.layout.activity_main);
        /*swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                weatherService();
                swipeRefresh.setRefreshing(false);
            }
        });*/

        services.setMainActivity(this);

        if (hasNetworkConnection() == false) {
            setContentView(R.layout.no_network);
            flag = 1;
            Toast();
        } else {
            setText();
            weatherService();
            swipeRefresh.setOnRefreshListener(() -> {
                if(hasNetworkConnection() == false) {
                    setContentView(R.layout.no_network);
                    flag = 1;
                    Toast();
                } else {
                    flag = 0;
                    setContentView(R.layout.activity_main);
                    setText();
                    weatherService();
                }
                swipeRefresh.setRefreshing(false);
            });
        }
    }

    public void weatherService() {
        WeatherServicesRunnable weatherServicesRunnable = new WeatherServicesRunnable(this, LatLon, unit);
        new Thread(weatherServicesRunnable).start();
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    /*public void refresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            if(hasNetworkConnection() == false) {
                setContentView(R.layout.no_network);
                flag = 1;
                Toast();
            } else {
                flag = 0;
                setContentView(R.layout.activity_main);
                setText();
                weatherService();
            }
            swipeRefresh.setRefreshing(false);
        });
    }*/

    public void setText(){
        location = findViewById(R.id.location);
        datetime = findViewById(R.id.datetime);
        temp = findViewById(R.id.temperature);
        feels_like = findViewById(R.id.feelslike);
        weather_desc = findViewById(R.id.weatherdesc);
        wind_desc = findViewById(R.id.winddesc);
        humidity = findViewById(R.id.humidity);
        uvi = findViewById(R.id.uv_index);
        visibility = findViewById(R.id.visibility);
        morning_temp = findViewById(R.id.morning_temp);
        daytime_temp = findViewById(R.id.daytime_temp);
        evening_temp = findViewById(R.id.evening_temp);
        night_temp = findViewById(R.id.night_temp);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        recyclerView = findViewById(R.id.recyle);
        imageView = findViewById(R.id.imageView2);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    public void Toast() {
        Toast.makeText(this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String dateTime(String dt, String offset, DateTimeFormatter dtf ){
        return LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(offset), 0, ZoneOffset.UTC).format(dtf);
    }

    public int getIcon(String icon){
        return this.getResources().getIdentifier("_"+icon, "drawable", this.getPackageName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calender:
                if (hasNetworkConnection() == false) {
                    setContentView(R.layout.no_network);
                    flag = 1;
                    Toast();
                    return true;
                }
                else if(flag == 1) {
                    flag = 0;
                    setContentView(R.layout.activity_main);
                    setText();
                }
                Intent intent = new Intent(this, DailyForecast.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("List", (Serializable) dlist);
                intent.putExtra("List",bundle);
                startActivity(intent);
                return true;

            case R.id.location:
                if (hasNetworkConnection() == false) {
                    setContentView(R.layout.no_network);
                    flag = 1;
                    Toast();
                    return true;
                }
                else if(flag == 1) {
                    setContentView(R.layout.activity_main);
                    setText();
                    flag = 0;
                }
                final EditText txtUrl = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Enter a Location")
                        .setMessage("For US locations, enter as 'City',or 'City,State'.\n"+"For international locations enter as 'City,Country'\n")
                        .setView(txtUrl)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                location(txtUrl.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                return true;


            case R.id.unit:
                if (hasNetworkConnection() == false) {
                    setContentView(R.layout.no_network);
                    flag = 1;
                    Toast();
                    return true;
                }
                else if(flag == 1) {
                    flag = 0;
                    setContentView(R.layout.activity_main);
                    setText();
                }

                if(unit =="imperial") {
                    unit = "metric";
                    item.setIcon(R.drawable.units_c);
                } else {
                    unit ="imperial";
                    item.setIcon(R.drawable.units_f);
                }
                UserSettiings us = new UserSettiings(unit, lat, lon);
                plist.clear();
                plist.addAll(Collections.singleton(us));
                saveData();
                WeatherServicesRunnable weatherServicesRunnable = new WeatherServicesRunnable(this,LatLon, unit);
                new Thread(weatherServicesRunnable).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String convert(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void getWeather(WeatherData weather) {
        location.setText(getLocation(weather.getLat(), weather.getLon()));
        datetime.setText(dateTime(weather.getCurrent().getDt(), weather.getTimezone_offset(), dtf));
        temp.setText(weather.getCurrent().getTemp());
        feels_like.setText("Feels Like "+weather.getCurrent().getFeels_like());
        weather_desc.setText(convert(weather.getCurrent().getWeather().getDescription()+" ( "+weather.getCurrent().getClouds()+"% "+weather.getCurrent().getWeather().getMain()+" )"));
        wind_desc.setText("Winds: "+services.getWindDirection(Double.parseDouble(weather.getCurrent().getWind_deg()))+" at "+weather.getCurrent().getWind_speed());
        humidity.setText("Humidity: "+weather.getCurrent().getHumidity()+"%");
        uvi.setText("UV Index: "+services.convertCeil(weather.getCurrent().getUvi()));
        visibility.setText(weather.getCurrent().getVisibility());
        morning_temp.setText(weather.getDaily().get(0).getTemperature().getMorn());
        daytime_temp.setText(weather.getDaily().get(0).getTemperature().getDay());
        evening_temp.setText(weather.getDaily().get(0).getTemperature().getEve());
        night_temp.setText(weather.getDaily().get(0).getTemperature().getNight());
        sunrise.setText("Sunrise: "+dateTime(weather.getCurrent().getSunrise(),weather.getTimezone_offset(),sun));
        sunset.setText("Sunset: s"+dateTime(weather.getCurrent().getSunset(),weather.getTimezone_offset(),sun));
        imageView.setImageResource(getIcon(weather.getCurrent().getWeather().getIcon()));

        hlist = weather.getHourly();
        dlist = weather.getDaily();
        hourlyAdapter = new HourlyAdapter(hlist,this);

        recyclerView.setAdapter(hourlyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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

    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                return null;
            }
            return new double[] {address.get(0).getLatitude(), address.get(0).getLongitude()};
        } catch (IOException e) {
            return null;
        }
    }

    public void location(String url) {
        LatLon = getLatLon(url);
        if(LatLon == null) {
            Toast.makeText(this,"Enter valid location",Toast.LENGTH_SHORT).show();
        } else {
            UserSettiings us = new UserSettiings(unit, lat, lon);
            plist.clear();
            plist.addAll(Collections.singleton(us));
            saveData();
            WeatherServicesRunnable weatherServicesRunnable = new WeatherServicesRunnable(this, LatLon, unit);
            new Thread(weatherServicesRunnable).start();
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity"));
        startActivity(i);
    }

    private ArrayList<UserSettiings> loadFile() {
        ArrayList<UserSettiings> prodList = new ArrayList<>();
        try {
            InputStream is = getApplicationContext().openFileInput("settings.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String unit = jsonObject.getString("unit");
                Double lat = jsonObject.getDouble("lat");
                Double lon = jsonObject.getDouble("lon");
                UserSettiings product = new UserSettiings(unit, lat, lon);
                prodList.add(product);
            }
        } catch (FileNotFoundException e) {
            UserSettiings us = new UserSettiings(unit, lat, lon);
            plist.clear();
            plist.addAll(Collections.singleton(us));
            saveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prodList;
    }

    private void saveData() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("settings.json", Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(plist);
            printWriter.close();
            fos.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
