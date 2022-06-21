package com.example.civiladvocacyapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OfficialLoaderVolley {

    private static final String TAG = "OfficialLoaderRunnable";

    private static MainActivity mainActivity;
    private static RequestQueue queue;
//    private static Official officialObj;

    private static String URL = "";

    private static final String yourAPIKey = "AIzaSyDd8OI-4cu_aqmfwDwFY2DArUi-cYCLZYo";

    public static void downloadOfficial(MainActivity mainActivityIn, String address) {
        mainActivity = mainActivityIn;

        queue = Volley.newRequestQueue(mainActivity);

        URL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+yourAPIKey+"&address="+address;
        Uri.Builder buildURL = Uri.parse(URL).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error =
                error1 -> mainActivity.updateData(null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void parseJSON(String s) {

        try {
            JSONObject officialsObj = new JSONObject(s);
            JSONObject normalizedInput = officialsObj.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            Official newOfficial;

            JSONArray offices = officialsObj.getJSONArray("offices");
            JSONArray officials =  officialsObj.getJSONArray("officials");

            for(int i = 0; i < offices.length(); i++) {

                JSONObject office = (JSONObject) offices.get(i);
                final String officeName = office.getString("name");
                JSONArray officialIndices =  office.getJSONArray("officialIndices");

                for(int j = 0; j < officialIndices.length(); j++) {
                    int index = Integer.parseInt(officialIndices.get(j).toString());

                    JSONObject officialDetails = (JSONObject) officials.get(index);
                    final String officialName = officialDetails.getString("name");
                    newOfficial = new Official(officeName, officialName);

                    if(officialDetails.has("address")) {
                        JSONArray address = officialDetails.getJSONArray("address");
                        JSONObject startAddress = (JSONObject) address.get(0);
                        String line1 = "";
                        String line2 = "";
                        String cityLine = "";
                        String stateLine = "";
                        String zipLine = "";
                        if(startAddress.has("line1")){
                            line1 = startAddress.getString("line1");
                        }
                        if(startAddress.has("line2")) {
                            line2 = startAddress.getString("line2");
                        }
                        if(startAddress.has("city")) {
                            cityLine = startAddress.getString("city");
                        }
                        if(startAddress.has("state")) {
                            stateLine = startAddress.getString("state");
                        }
                        if(startAddress.has("zip")) {
                            zipLine = startAddress.getString("zip");
                        }

                        String line1F = line1 + line2;
                        String line2F = cityLine + ", " + stateLine + " " + zipLine;
                        String completeAddress = line1F + ", " + line2F;
                        newOfficial.setAddress(completeAddress);
                    }

                    if(officialDetails.has("party")) {
                        String party = officialDetails.getString("party");
                        newOfficial.setParty(party);
                    }

                    if(officialDetails.has("phones")) {
                        JSONArray phoneArr = officialDetails.getJSONArray("phones");
                        String phoneNum = (String) phoneArr.get(0);
                        newOfficial.setPhoneNum(phoneNum);
                    }

                    if(officialDetails.has("urls")){
                        JSONArray urlArr = officialDetails.getJSONArray("urls");
                        String url = (String) urlArr.get(0);
                        newOfficial.setWebURL(url);
                    }


                    if(officialDetails.has("emails")){
                        JSONArray emailArr = officialDetails.getJSONArray("emails");
                        String email = (String) emailArr.get(0);
                        newOfficial.setEmailID(email);
                    }

                    if(officialDetails.has("photoUrl")){
                        String photoUrl = officialDetails.getString("photoUrl");
                        newOfficial.setPhotoURL(photoUrl);
                    }

                    if(officialDetails.has("channels")){
                        JSONArray channelArr = officialDetails.getJSONArray("channels");
                        for(int z = 0; z < channelArr.length(); z++){
                            JSONObject channel = (JSONObject) channelArr.get(z);
                            String channelType = channel.getString("type");
                            String channelId = channel.getString("id");
                            switch (channelType){
                                case "Facebook":
                                    newOfficial.setFacebookID(channelId);
                                    break;
                                case "Twitter":
                                    newOfficial.setTwitterID(channelId);
                                    break;
                                case "YouTube":
                                    newOfficial.setYoutubeID(channelId);
                                    break;
                                default:
                                    Log.d(TAG, "Channel Type Not Appropriate");
                            }
                        }
                    }

                    mainActivity.addOfficialData(newOfficial);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
