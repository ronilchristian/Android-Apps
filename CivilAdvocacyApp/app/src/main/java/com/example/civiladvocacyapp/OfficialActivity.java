package com.example.civiladvocacyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Official tempOfficialObj;
    private Picasso picasso;
    private ConstraintLayout constraintLayout;
    private ImageView official_photo;
    private ImageView partyIcon;
    private TextView official_location;
    private TextView official_office;
    private TextView official_name;
    private TextView official_party;
    private TextView address_line;
    private TextView phone_line;
    private TextView website_line;
    private TextView email_line;
    private String imageURL = "";
    private String fbID;
    private String tID;
    private String yID;
    private static int IMAGE_OPEN_REQUEST = 1;
    private static final String DEM_URL = "https://democrats.org";
    private static final String REP_URL = "https://www.gop.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        official_location = findViewById(R.id.officialLocation);
        official_photo = findViewById(R.id.officialPhotoView);
        partyIcon = findViewById(R.id.partyIcon);
        official_office = findViewById(R.id.officialOffice);
        official_name = findViewById(R.id.officialName);
        official_party = findViewById(R.id.officialParty);
        address_line = findViewById(R.id.addressLine);
        phone_line = findViewById(R.id.phoneLine);
        website_line = findViewById(R.id.websiteLine);
//        email_line = findViewById(R.id.emailLine);
        constraintLayout = findViewById(R.id.officialConstraintLayout);
        picasso = Picasso.get();
        
        picasso.setLoggingEnabled(true);
        Intent intent = getIntent();

        if(intent.hasExtra("OPEN_OFFICIAL")) {
            tempOfficialObj = (Official) intent.getSerializableExtra("OPEN_OFFICIAL");
            if(tempOfficialObj != null) {
                if(tempOfficialObj.getPhotoURL() != null){
                    imageURL = tempOfficialObj.getPhotoURL();
                }
//                // Check this once:
                else{
                    imageURL = "https://images-assets.nasa.gov/image/6900952/does_not_exist.jpg";
                }
            }
        }

        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            String location = intent.getStringExtra(Intent.EXTRA_TEXT);
            official_location.setText(location);
        }

        loadOfficialData();
        loadRemoteImage();

        // Usage of Linkify to link all the text views
        Linkify.addLinks(address_line, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(website_line, Linkify.ALL);
        Linkify.addLinks(phone_line, Linkify.ALL);
//        Linkify.addLinks(email_line, Linkify.ALL);

        activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleResult);
    }

    private void handleResult(ActivityResult result) {
        // do nothing
    }

    private void loadOfficialData(){
        official_office.setText(tempOfficialObj.getOffice());
        official_name.setText(tempOfficialObj.getName());

        String strParty = tempOfficialObj.getParty();
        if(strParty != null){
            official_party.setText("(" + strParty + ")");

            if(strParty.contains("Democratic")) {
                constraintLayout.setBackgroundColor(Color.BLUE);
                partyIcon.setImageResource(R.drawable.dem_logo);
            }
            else if (strParty.contains("Republican")) {
                constraintLayout.setBackgroundColor(Color.RED);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }
            else {
                constraintLayout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(ImageView.GONE);
            }
        }

        if(tempOfficialObj.getAddress() != null) {
            address_line.setText(tempOfficialObj.getAddress());
        }
        else {
            TextView addressTitle = findViewById(R.id.addressTitle);
            addressTitle.setVisibility(TextView.GONE);
            address_line.setVisibility(TextView.GONE);
        }

        if(tempOfficialObj.getPhoneNum() != null) {
            phone_line.setText(tempOfficialObj.getPhoneNum());
        }
        else {
            TextView phoneTitle = findViewById(R.id.phoneTitle);
            phoneTitle.setVisibility(TextView.GONE);
            phone_line.setVisibility(TextView.GONE);
        }

        if(tempOfficialObj.getWebURL() != null) {
            website_line.setText(tempOfficialObj.getWebURL());
        }
        else
        {
            TextView webTitle = findViewById(R.id.websiteTitle);
            webTitle.setVisibility(TextView.GONE);
            website_line.setVisibility(TextView.GONE);
        }

//        String emailStr = tempOfficialObj.getEmailID();
//        if(tempOfficialObj.getEmailID() != null) {
//            email_line.setText(tempOfficialObj.getEmailID());
//        }
//        else {
//            TextView emailTitle = findViewById(R.id.emailTitle);
//            emailTitle.setVisibility(TextView.GONE);
//            email_line.setVisibility(TextView.GONE);
//        }

        if(tempOfficialObj.getFacebookID() != null) {
            fbID = tempOfficialObj.getFacebookID();
        }
        else {
            ImageView fbIcon = findViewById(R.id.facebook);
            fbIcon.setVisibility(ImageView.GONE);
        }

        if(tempOfficialObj.getTwitterID() != null){
            tID = tempOfficialObj.getTwitterID();
        }
        else {
            ImageView tIcon = findViewById(R.id.twitter);
            tIcon.setVisibility(ImageView.GONE);
        }

        if(tempOfficialObj.getYoutubeID() != null){
            yID = tempOfficialObj.getYoutubeID();
        }
        else {
            ImageView yIcon = findViewById(R.id.youtube);
            yIcon.setVisibility(ImageView.GONE);
        }
    }

    private void loadRemoteImage() {
        if (!doNetworkCheck()) {
            official_photo.setImageResource(R.drawable.brokenimage);
            return;
        }
        picasso.load(imageURL).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(official_photo, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) official_photo.getDrawable()).getBitmap().getByteCount());
                    }
                    @Override
                    public void onError(Exception exception) {
                        Log.d(TAG, "onError: Inside loadImages() function:" + exception.getMessage());
                    }
                });
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

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + fbID;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + fbID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + tID));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + tID));
        }
        startActivity(intent);
    }


    public void youTubeClicked(View v) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + yID));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + yID)));
        }
    }

    public void officialPhotoClicked(View view) {
        if(tempOfficialObj.getPhotoURL() != null) {
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            String location = official_location.getText().toString();
            String party = official_party.getText().toString();
            intent.putExtra("OFFICIAL", tempOfficialObj);
            intent.putExtra("LOCATION", location);
            activityResultLauncher.launch(intent);
        }
    }

    public void partyIconClicked(View view) {
        Intent intent = null;
        if(tempOfficialObj.getParty().contains("Democratic")){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DEM_URL));
        }
        else if(tempOfficialObj.getParty().contains("Republican")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REP_URL));
        }
        else{
            Log.d(TAG, "clickPartyIcon: ERROR!");
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this, "The Back button was pressed - Going to Main Activity!", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}