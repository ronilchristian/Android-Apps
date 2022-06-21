package com.example.civiladvocacyapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView namepart;

    OfficialViewHolder(View view) {
        super(view);
        office = view.findViewById(R.id.designation);
        namepart = view.findViewById(R.id.nameparty);
    }

}