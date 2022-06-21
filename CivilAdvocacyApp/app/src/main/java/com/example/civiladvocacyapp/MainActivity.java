package com.example.civiladvocacyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

//    APIKey = AIzaSyDd8OI-4cu_aqmfwDwFY2DArUi-cYCLZYo

    private static final String TAG = "MainActivity";
    private final List<Official> officialList = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView; // Layout's recyclerview
    private FusedLocationProviderClient mFusedLocationClient;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private static final int LOCATION_REQUEST = 111;
    private static int OPEN_OFFICIAL_REQUEST = 0;
    private static String locationString = "Unspecified Location";
    private OfficialsAdapter mAdapter;
    private String officialLocation;
    public String addr;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.mainRecycler);
        // Data to recyclerview adapter
        mAdapter = new OfficialsAdapter(officialList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);

        //Make some data - not always needed - just used to fill list
//        for (int i = 0; i < 15; i++) {
//            officialList.add(new Official());
//        }
    }

    // From OnClickListener
    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Official m = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OPEN_OFFICIAL", m);
        intent.putExtra(Intent.EXTRA_TEXT, officialLocation);
        intent.putExtra(Intent.EXTRA_TEXT, officialLocation);
        activityResultLauncher.launch(intent);
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        Official m = officialList.get(pos);
        Toast.makeText(v.getContext(), "LONG " + m.toString(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "The back button was pressed - Bye!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAbout) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menuLocation) {
            if(!doNetworkCheck()) {
                ((TextView) findViewById(R.id.location)).setText("No Data For Location");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Data cannot be accessed/loaded without a network connection");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            // Create an edittext and set it to be the builder's view
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);

            new AlertDialog.Builder(this)
                    .setView(et)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setLocation(et.getText().toString());
                        }
                    })
                    .setNegativeButton("CANCEL", null)
                    .setTitle("Enter Address")
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                    return;
                }
            }
        }
        ((TextView) findViewById(R.id.location)).setText("No Data For Location");
    }

    public void updateData(Official official) {
        if (official == null) {
            officialList.clear();
//            mAdapter.notifyItemRangeChanged(0, officialList.size());
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    // Performing Network Check
    private boolean doNetworkCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Toast.makeText(this, "Cannot access Connectivity Manager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork == null) ? false : activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void addOfficialData(Official newOfficial) {
        if(newOfficial == null)
        {
            Log.d(TAG, "addOffice: New Official Data is NULL");
        }
        officialList.add(newOfficial);
        mAdapter.notifyDataSetChanged();
    }

    private void handleResult(ActivityResult result) {
        //Toast.makeText(this, "Coming from OfficialActivity Class", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void determineLocation() {
        if (checkAppPermissions()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            officialLocation = locationString;
                            ((TextView) findViewById(R.id.location)).setText(locationString);
                            OfficialLoaderVolley.downloadOfficial(this, addr);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    public String getPlace(Location loc) {
        Log.d(TAG, "getPlace: ");
        StringBuilder sb = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            Log.d(TAG, "getPlace: TRY");
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String postalCode = addresses.get(0).getPostalCode();
            this.addr = city;
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s, %s",
                    city, state, postalCode));
        } catch (IOException e) {
            sb.append("No address found");
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void setLocation(String input) {
        if (geocoder == null)
            geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            if(input.trim().isEmpty()) {
                Toast.makeText(this, "Please enter City, State or Zipcode to proceed!", Toast.LENGTH_LONG).show();
                return;
            }
            addresses = geocoder.getFromLocationName(input, 5);
            displayAddress(addresses);
        }
        catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        officialList.clear();
        OfficialLoaderVolley.downloadOfficial(this, addr);
    }

    private void displayAddress(List<Address> addresses) {
        if(addresses.size() == 0){
            ((TextView) findViewById(R.id.location)).setText("No Data For Location");
            return;
        }
        Address one = addresses.get(0);
        // check if it's null or not
        String city = one.getLocality() == null ? "" : one.getLocality();
        String state = one.getAdminArea() == null ? "" : one.getAdminArea();
        String postalCode = one.getPostalCode() == null ? "" : one.getPostalCode();
        officialLocation = city + ", " + state + " " + postalCode;
        addr = city;
        ((TextView) findViewById(R.id.location)).setText(officialLocation);
    }
}