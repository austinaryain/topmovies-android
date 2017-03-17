package com.example.aryai.topmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {


    TextView titleTV;
    TextView overviewTV;
    TextView ratingTV;
    TextView releaseTV;
    TextView languageTV;
    ImageView thumbnailIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTV = (TextView) findViewById(R.id.titleTV);
        overviewTV = (TextView) findViewById(R.id.overviewTV);
        ratingTV = (TextView) findViewById(R.id.ratingTV);
        releaseTV = (TextView) findViewById(R.id.releaseTV);
        languageTV = (TextView) findViewById(R.id.languageTV);
        thumbnailIV = (ImageView)findViewById(R.id.thumbIV);

        Intent i = getIntent();
        titleTV.setText(i.getStringExtra("TITLE"));
        overviewTV.setText(i.getStringExtra("OVERVIEW"));
        ratingTV.setText("Rating: " + Double.parseDouble(i.getStringExtra("RATING")) + " / 10");
        releaseTV.setText("Release Date: " + i.getStringExtra("RELEASEDATE"));
        languageTV.setText("Language: " + i.getStringExtra("LANGUAGE"));

        new ImageLoadTask(i.getStringExtra("THUMBNAIL"), thumbnailIV).execute();

    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
