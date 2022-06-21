package com.example.newsaggregator;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    TextView headline;
    TextView date;
    TextView author;
    ImageView newsEntryImage;
    TextView description;
    TextView count;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);
        headline = itemView.findViewById(R.id.headline);
        date = itemView.findViewById(R.id.date);
        author = itemView.findViewById(R.id.author);
        newsEntryImage = itemView.findViewById(R.id.newsEntryImage);
        description = itemView.findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());
        count = itemView.findViewById(R.id.count);
    }
}
