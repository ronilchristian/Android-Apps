package com.example.newsaggregator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.newsaggregator.MainActivity;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    ImageView newsEntryImage;
    MainActivity mainActivity;

    public ImageLoader(ImageView newsEntryImage, MainActivity mainActivity) {
        this.newsEntryImage = newsEntryImage;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap icon = null;
        try {
            icon = BitmapFactory.decodeStream(new java.net.URL(strings[0]).openStream());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return icon;
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            newsEntryImage.setImageResource(mainActivity.getResources().getIdentifier("brokenimage", "drawable", mainActivity.getPackageName()));
        } else {
            newsEntryImage.setImageBitmap(result);
        }
    }
}
