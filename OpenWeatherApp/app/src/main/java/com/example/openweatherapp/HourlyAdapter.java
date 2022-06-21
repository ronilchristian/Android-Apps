package com.example.openweatherapp;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openweatherapp.bean.Hourly;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    List<Hourly> hlist;
    MainActivity mainActivity;
    DateTimeFormatter sun = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault());
    DateTimeFormatter def = DateTimeFormatter.ofPattern("EEE", Locale.getDefault());

    String formatDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"));

    public HourlyAdapter(List<Hourly> hlist, MainActivity mainActivity) {
        this.hlist = hlist;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly, parent, false);
        view.setOnClickListener(mainActivity);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String icon ="_"+ hlist.get(position).getWeather().getIcon();
        int iconResId = mainActivity.getResources().getIdentifier(icon, "drawable", mainActivity.getPackageName());
        String df = hlist.get(position).getDt();
        String timzoneOffset = hlist.get(position).getTimezone_offset();

        if(mainActivity.dateTime(df, timzoneOffset, DateTimeFormatter.ofPattern("dd", Locale.getDefault())).equals(formatDateTime)) {
            holder.day.setText("Today");
        } else {
            holder.day.setText(mainActivity.dateTime(df, timzoneOffset, def));
        }
        holder.time.setText(mainActivity.dateTime(df, timzoneOffset, sun));
        holder.desc.setText(hlist.get(position).getWeather().getDescription());
        holder.imageView.setImageResource(iconResId);
        holder.temp.setText(hlist.get(position).getTemp());
    }

    @Override
    public int getItemCount() {
        return hlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, time, temp, desc;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            time = itemView.findViewById(R.id.time);
            temp = itemView.findViewById(R.id.hourly_temp);
            desc = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

