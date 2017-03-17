package com.example.aryai.topmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    }
}
