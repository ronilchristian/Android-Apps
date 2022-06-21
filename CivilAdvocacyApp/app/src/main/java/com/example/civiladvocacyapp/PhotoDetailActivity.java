package com.example.civiladvocacyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";
    private ConstraintLayout constraintLayout;
    private TextView photo_location;
    private TextView photo_office;
    private TextView photo_name;
    private ImageView photo_official;
    private ImageView partyIcon;
    private Official tempOfficialObj;
    private String party;
    private String imageURL;
    private static final String DEM_URL = "https://democrats.org";
    private static final String REP_URL = "https://www.gop.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        constraintLayout = findViewById(R.id.officialConstraintLayout);

        photo_location = findViewById(R.id.photoLayoutLocation);
        photo_office = findViewById(R.id.officePhoto);
        photo_name = findViewById(R.id.namePhoto);

        photo_official = findViewById(R.id.photoImageView);
        partyIcon = findViewById(R.id.partyPhoto);

        Intent intent = getIntent();

        if(intent.hasExtra("OFFICIAL")) {
            tempOfficialObj = (Official) intent.getSerializableExtra("OFFICIAL");
        }
        party = tempOfficialObj.getParty();
        imageURL = tempOfficialObj.getPhotoURL();

        photo_office.setText(tempOfficialObj.getOffice());
        photo_name.setText(tempOfficialObj.getName());

        if(intent.hasExtra("LOCATION")) {
            String location = intent.getStringExtra("LOCATION");
            photo_location.setText(location);
        }

        setPartyIcon();
        loadImage();
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

    private void setPartyIcon() {
        if (party.contains("Democratic")) {
            partyIcon.setImageResource(R.drawable.dem_logo);
            constraintLayout.setBackgroundColor(Color.BLUE);
        }
        else if(party.contains("Republican")){
            partyIcon.setImageResource(R.drawable.rep_logo);
            constraintLayout.setBackgroundColor(Color.RED);
        }
        else{
            partyIcon.setVisibility(ImageView.GONE);
            constraintLayout.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadImage(){
        if (!doNetworkCheck()) {
            photo_official.setImageResource(R.drawable.brokenimage);
            return;
        }

        Picasso.get().load(imageURL).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(photo_official,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) photo_official.getDrawable()).getBitmap().getByteCount());
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public void partyIconClicked(View view){
        Intent intent = null;
        if(tempOfficialObj.getParty().contains("Democratic")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DEM_URL));
        }
        else if(tempOfficialObj.getParty().contains("Republican")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REP_URL));
        }
        else{
            Log.d(TAG, "clickPartyIcon: Error!");
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}