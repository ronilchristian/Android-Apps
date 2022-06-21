package com.example.civiladvocacyapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfficialsAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "OfficialsAdapter";
    private final List<Official> officialList;
    private final MainActivity mainAct;

    OfficialsAdapter(List<Official> offList, MainActivity ma) {
        this.officialList = offList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW OfficialViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER " + position);

        Official official = officialList.get(position);

        holder.office.setText(official.getOffice());
        holder.namepart.setText(official.getName() + " (" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }

}