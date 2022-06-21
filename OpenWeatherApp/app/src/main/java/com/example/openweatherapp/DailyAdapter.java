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

import com.example.openweatherapp.Services.Services;
import com.example.openweatherapp.bean.Daily;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    List<Daily> dlist;
    DailyForecast dailyForecast;
    public final static String f = "°F";

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE,MM/dd ", Locale.getDefault());

    public DailyAdapter(List<Daily> dlist, DailyForecast dailyForecast) {
        this.dlist = dlist;
        this.dailyForecast = dailyForecast;
    }

    @NonNull
    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyAdapter.ViewHolder holder, int position) {
        Services services = new Services();
        holder.daydate.setText(dateTime(dlist.get(position).getDt(), dlist.get(position).getTimezone_offset(), dtf));
        holder.tempminmax.setText(dlist.get(position).getTemperature().getMax()+
                "/"+ dlist.get(position).getTemperature().getMin());
        holder.desc.setText(dlist.get(position).getWeather().getDescription());
        holder.pop.setText("("+ dlist.get(position).getPop()+"% precip.)");
        holder.uvi.setText("UV Index: "+services.convertCeil(dlist.get(position).getUvi()));
        holder.morn.setText(dlist.get(position).getTemperature().getMorn());
        holder.dayt.setText(dlist.get(position).getTemperature().getDay());
        holder.even.setText(dlist.get(position).getTemperature().getEve());
        holder.night.setText(dlist.get(position).getTemperature().getNight());

        String icon ="_"+ dlist.get(position).getWeather().getIcon();
        int iconResId = dailyForecast.getResources().getIdentifier(icon, "drawable", dailyForecast.getPackageName());
        holder.imageView.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return dlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView daydate, tempminmax, desc, pop, uvi, morn, dayt, even, night;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            daydate = itemView.findViewById(R.id.daydate);
            tempminmax = itemView.findViewById(R.id.tempminmax);
            desc = itemView.findViewById(R.id.daily_desc);
            pop = itemView.findViewById(R.id.pop);
            uvi = itemView.findViewById(R.id.uvi);
            morn = itemView.findViewById(R.id.dailymorn);
            dayt = itemView.findViewById(R.id.dailydaytime);
            even = itemView.findViewById(R.id.dailyeven);
            night = itemView.findViewById(R.id.dailynight);
            imageView = itemView.findViewById(R.id.imageView3);
        }
    }

    public String dateTime(String dt, String offset, DateTimeFormatter dtf ){
        return LocalDateTime.ofEpochSecond(Long.parseLong(dt) + Long.parseLong(offset), 0, ZoneOffset.UTC).format(dtf);
    }
}

