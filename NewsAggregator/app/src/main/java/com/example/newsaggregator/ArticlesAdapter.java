package com.example.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArticlesAdapter extends
        RecyclerView.Adapter<NewsViewHolder> {

    private final MainActivity mainActivity;
    private final ArrayList<Articles> articlesList;

    public ArticlesAdapter(MainActivity mainActivity, ArrayList<Articles> articlesList) {
        this.mainActivity = mainActivity;
        this.articlesList = articlesList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Articles n = articlesList.get(position);

//        try{
//            Date d = (new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss")).parse(n.getDate());
//            String str = (new SimpleDateFormat("MMM dd,yyyy hh:mm")).format(d);
//            System.out.println("str"+str);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }

        if (!n.getImage().equals("null")) {
            new ImageLoader(holder.newsEntryImage, mainActivity).execute(n.getImage());
        }

        if (n.getAuthor().equals("null")) {
            n.setAuthor("");
        }

        if (n.getDescription().equals("null")) {
            n.setDescription("");
        }

        Log.d("NewsAdapter","Adapter"+n.getDescription());
        holder.headline.setText(n.getHeadline());
        holder.date.setText(n.getDate());
        holder.author.setText(n.getAuthor());
        holder.description.setText(n.getDescription());
        holder.count.setText((position + 1) + " of " + articlesList.size());
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    private void clickFlag(String name) {
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(name));
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        mainActivity.startActivity(intent);
    }
}
