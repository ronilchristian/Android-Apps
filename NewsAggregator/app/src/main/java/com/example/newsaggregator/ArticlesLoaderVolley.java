package com.example.newsaggregator;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.Articles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ArticlesLoaderVolley {

    private static final String TAG = "ArticlesLoaderVolley";

    public static void getArticlesData(MainActivity mainActivity, String source) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse("https://newsapi.org/v2/top-headlines?sources="+source+"&apiKey=929aff506ba44e939989bcde945477a3").buildUpon();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void handleResults(MainActivity mainActivity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.downloadFailed();
            return;
        }

        final ArrayList<Articles> articles = parseJSON(s);
        if (articles != null)
            Toast.makeText(mainActivity, "Loaded " + articles.size() + " articles.", Toast.LENGTH_LONG).show();
        mainActivity.updateArticlesData(articles);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<Articles> parseJSON(String s) {
        ArrayList<Articles> articlesList = new ArrayList<>();
        try {
            JSONObject jMain = new JSONObject(s);
            JSONArray jArticle = jMain.getJSONArray("articles");
            int articlesLength = jArticle.length();

            for (int i = 0; i < articlesLength; i++) {
                JSONObject article = (JSONObject) jArticle.get(i);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm", Locale.ENGLISH);
                articlesList.add(new Articles(article.getString("title"),
                        article.getString("author"),
                        article.getString("description"),
                        ZonedDateTime.parse(article.getString("publishedAt")).format(dateTimeFormatter),
                        article.getString("urlToImage"),
                        article.getString("url")
                ));
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return articlesList;
    }

}
