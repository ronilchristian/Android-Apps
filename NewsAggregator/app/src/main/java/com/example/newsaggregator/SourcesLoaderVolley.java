package com.example.newsaggregator;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.Sources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SourcesLoaderVolley {

    private static final String TAG = "SourcesLoaderVolley";
    private static final String DATA_URL = "https://newsapi.org/v2/sources?apiKey=929aff506ba44e939989bcde945477a3";

    public static void getSourcesData(MainActivity mainActivity) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResults(mainActivity, response.toString());

        Response.ErrorListener error = error1 ->
        {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResults(mainActivity, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }

                };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.downloadFailed();
            return;
        }

        final ArrayList<Sources> sources = parseJSON(s);
        if (sources != null)
            Toast.makeText(mainActivity, "Loaded " + sources.size() + " sources.", Toast.LENGTH_LONG).show();
        mainActivity.updateSourcesData(sources);
    }

    private static ArrayList<Sources> parseJSON(String s) {
        ArrayList<Sources> sourcesList = new ArrayList<>();
        try {
            JSONObject jMain = new JSONObject(s);
            JSONArray jSource = jMain.getJSONArray("sources");
            int sourcesLength = jSource.length();

            for (int i = 0; i < sourcesLength; i++) {
                JSONObject source = (JSONObject) jSource.get(i);
                sourcesList.add(new Sources(source.getString("id"),
                        source.getString("name"),
                        source.getString("category"))
                );
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return sourcesList;
    }

}
