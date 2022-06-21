package com.example.openweatherapp;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringWriter;

public class UserSettiings {
    private String unit;
    private Double lat;
    private Double lon;

    public UserSettiings(String unit, Double lat, Double lon) {
        this.unit = unit;
        this.lat = lat;
        this.lon = lon;
    }

    public String getUnit() {
        return unit;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    @NonNull
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("unit").value(getUnit());
            jsonWriter.name("lat").value(getLat());
            jsonWriter.name("lon").value(getLon());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
