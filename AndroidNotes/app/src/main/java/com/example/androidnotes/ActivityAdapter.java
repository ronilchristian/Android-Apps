package com.example.androidnotes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    List<Notes> notesList;
    MainActivity mainActivity;

    public ActivityAdapter(List<Notes> notesList ,MainActivity mainActivity) {
        this.notesList = notesList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view, parent, false);
        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleNote.setText(notesList.get(position).getTitle());
        String detail = notesList.get(position).getDetail();
        detail = detail.replaceAll("\n"," ");
        if(detail.length() > 80) {
            holder.detailNotes.setText(detail.substring(0,80) + "...");
        } else {
            holder.detailNotes.setText(detail);
        }
        holder.noteDate.setText(notesList.get(position).getMillis());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleNote;
        TextView detailNotes;
        TextView noteDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleNote=itemView.findViewById(R.id.note_title);
            detailNotes=itemView.findViewById(R.id.notes_detail);
            noteDate = itemView.findViewById(R.id.note_date);
        }
    }
}
