package com.example.androidnotes;

import android.util.JsonWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class Notes implements Serializable {
    private String title;
    private String detail;
    private String millis;

    public Notes(String title, String detail, String millis) {
        this.title = title;
        this.detail = detail;
        this.millis = millis;
    }

    public String getTitle() {
        return title;
    }
    public String getDetail() { return detail; }
    public String getMillis() { return millis; }

    @Override
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriter(sw);
            jw.setIndent("  ");
            jw.beginObject();
            jw.name("title").value(getTitle());
            jw.name("detail").value(getDetail());
            jw.name("date").value(getMillis());
            jw.endObject();
            jw.close();
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
