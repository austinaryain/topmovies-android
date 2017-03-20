package com.example.aryai.topmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import net.redwarp.library.database.DatabaseHelper;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    private final Long ONE_DAY_MS = 86400000L;
    private ImageView cover;
    DatabaseHelper helper;
    Context mContext;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cover = (ImageView)findViewById(R.id.backdrop);

        initCollapsingToolbar();

        mContext = this;

        preferences = mContext.getSharedPreferences("com.example.aryai.topmovies", Context.MODE_PRIVATE);

        //Init DatabaseHelper
        helper = new DatabaseHelper(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();

        if(preferences.getLong("Updated_At", 0) == 0){
            setDatePreference();
        }

        //Check to see if the database contains the movies AND it has been more than 1 DAY
        if(helper.getCount(Movie.class) > 0 || System.currentTimeMillis() > (preferences.getLong("Updated_At", 0) + ONE_DAY_MS)){
            movieList = new ArrayList<>(helper.getAll(Movie.class));
        } else { //If not, Get them from the server.
            setDatePreference();
            //Delete All items in the Database
            List<Movie> databaseDelete = new ArrayList<>(helper.getAll(Movie.class));
            helper.beginTransaction();
            for (Movie mov : databaseDelete) {
                helper.delete(mov);
            }
            helper.setTransactionSuccessful();
            helper.endTransaction();
            //Execute the Asynchronous Movie Fetcher
            new MovieDataFetcher().execute();
       }

        adapter = new MoviesAdapter(this, movieList);
        adapter.setOnItemClickListener(new MoviesAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(MainActivity.this, DetailActivity.class);
                Movie m = movieList.get(position);
                i.putExtra("TITLE", m.getTitle());
                i.putExtra("OVERVIEW", m.getOverview());
                i.putExtra("THUMBNAIL", m.getThumbnail());
                i.putExtra("RELEASEDATE", m.getReleaseDate());
                i.putExtra("LANGUAGE", m.getLanguage());
                i.putExtra("RATING", String.valueOf(m.getRating()));
                startActivity(i);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_columns));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(getResources().getInteger(R.integer.grid_columns), dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        try {
            Picasso.with(this).load(R.drawable.cover).noFade().into(cover);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDatePreference() {
        Date date = new Date(System.currentTimeMillis());
        long millis = date.getTime();
        preferences.edit().putLong("Updated_At", millis).apply();
    }

    private void initCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class MovieDataFetcher extends AsyncTask<Void, Void, String> {

        private final String POSTER_PATH = "http://image.tmdb.org/t/p/w500";

        @Override
        protected String doInBackground(Void... params) {
            try {
                java.net.URL url = new URL("https://api.themoviedb.org/3/movie/top_rated?api_key=232f72a9ddf4da165eb64d7ac7168102&language=en-US&page=1");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            //Handle no response
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            try {
                //Get the Response as a JSON Object
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                //Grab the results array
                JSONArray movies = object.getJSONArray("results");
                //Setup a new Movie object
                Movie m;
                JSONObject currentMovie;

                //Iterate through the results array 10 times
                for(int i = 0; i < 10; i++){
                    currentMovie = movies.getJSONObject(i);

                    String title = currentMovie.getString("original_title");
                    float rating = (float)currentMovie.getDouble("vote_average");
                    String thumbnail = currentMovie.getString("poster_path");
                    String language = currentMovie.getString("original_language");
                    String overview = currentMovie.getString("overview");
                    String releaseDate = currentMovie.getString("release_date");

                    m = new Movie(title, rating, POSTER_PATH + thumbnail, language, overview, releaseDate);

                    movieList.add(m);
                }

                //Add each movie from the server to the SQLite database
                helper.beginTransaction();  //Open transaction to database
                for (Movie movie: movieList) {
                    helper.save(movie); //SAVE the movies
                }
                helper.setTransactionSuccessful();
                helper.endTransaction(); // Close the transaction
                adapter.notifyDataSetChanged(); //Update the MoviesAdapter

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
