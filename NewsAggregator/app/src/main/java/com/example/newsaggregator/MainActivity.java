package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final HashMap<String, HashSet<String>> categoryToSources = new HashMap<>();
    private final HashMap<String, String> sourcesToId = new HashMap<>();
    private final ArrayList<Articles> currentArticlesList = new ArrayList<>();

    private final ArrayList<String> sourcesDisplayed = new ArrayList<>();
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private ArticlesAdapter articlesAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ViewPager2 viewPager;

    private String selectedSource;
    private Map<String, Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.mainDrawerLayout);
        mDrawerList = findViewById(R.id.mainDrawerList);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        articlesAdapter = new ArticlesAdapter(this, currentArticlesList);
        viewPager = findViewById(R.id.mainViewPager);
        viewPager.setAdapter(articlesAdapter);

        // Load the data
        SourcesLoaderVolley.getSourcesData(this);
        setColor(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // You need this to set up the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }

    // You need the below to open the drawer when the toggle is clicked
    // Same method is called when an options menu item is selected.

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        setTitle(item.getTitle());

        sourcesDisplayed.clear();
        HashSet<String> lst = categoryToSources.get(item.getTitle().toString());
        if (lst != null) {
            sourcesDisplayed.addAll(lst);
            Collections.sort(sourcesDisplayed);
        }

        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectItem(int position) {

        viewPager.setBackground(null);

        selectedSource = sourcesDisplayed.get(position);
        String selectedId = sourcesToId.get(selectedSource);
        currentArticlesList.clear();

        ArticlesLoaderVolley.getArticlesData(this, selectedId);
    }

    public void updateSourcesData(ArrayList<Sources> sList) {
        categoryToSources.put("all", new HashSet<>());
        for (Sources sources : sList) {
            String name = sources.getName();

            Objects.requireNonNull(categoryToSources.get("all")).add(name);
        }

        for (Sources sources : sList) {
            String category = sources.getCategory();
            String name = sources.getName();
            String id = sources.getId();

            if (!categoryToSources.containsKey(category))
                categoryToSources.put(category, new HashSet<>());
            Objects.requireNonNull(categoryToSources.get(category)).add(name);

            if (!sourcesToId.containsKey(name))
                sourcesToId.put(name, id);
        }

        ArrayList<String> menuItemList = new ArrayList<>(categoryToSources.keySet());

        Collections.sort(menuItemList);
        for (String s : menuItemList)
            opt_menu.add(s);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourcesDisplayed);
//        {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                ((TextView)view.findViewById(android.R.id.text1)).setTextColor(getColor(sList.get(position).getCategory()));
//                return view;
//            }
//        };
        mDrawerList.setAdapter(arrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
    }

    public void updateArticlesData(ArrayList<Articles> aList) {
        ArrayList<Articles> articles = aList;

        if (articles == null) {
            Toast.makeText(this,
                    "No articles found!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        currentArticlesList.addAll(articles);
        articlesAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);

        mDrawerLayout.closeDrawer(mDrawerList);

        setTitle(selectedSource + " (" + currentArticlesList.size() + ")");
    }

    public void doOnClick(View v) {
        int pos = viewPager.getCurrentItem();
        Articles a = currentArticlesList.get(pos);
        String urlToUse = a.getUrl();
        if(!urlToUse.trim().equals("NONE"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlToUse));
            startActivity(intent);
        }
    }

    public void setColor(MainActivity mainActivity) {
        colors = new HashMap<>();
        Resources r = mainActivity.getResources();

        colors.put("business", r.getColor(R.color.business));
        colors.put("entertainment", r.getColor(R.color.entertainment));
        colors.put("general", r.getColor(R.color.general));
        colors.put("health", r.getColor(R.color.health));
        colors.put("science", r.getColor(R.color.science));
        colors.put("sports", r.getColor(R.color.sports));
        colors.put("technology", r.getColor(R.color.technology));
    }

    public int getColor(String type) {
        return colors.get(type);
    }
}