package com.example.aryai.topmovies;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by aryai on 3/16/2017.
 * RecyclerView Adapter for Movie List on MainActivity
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;
    private static ClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, rating;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View itemView) {
            super(itemView);
            title  = (TextView)itemView.findViewById(R.id.title);
            rating  = (TextView)itemView.findViewById(R.id.rating);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            overflow = (ImageView)itemView.findViewById(R.id.overflow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        MoviesAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public MoviesAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Log.i("MOVIE!", movie.getTitle());
        holder.title.setText(movie.getTitle());
        holder.rating.setText("Rating: " + movie.getRating());

        Glide.with(mContext).load(movie.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showMovieDetails(holder.overflow);
            }
        });
    }

    private void showMovieDetails(View v){

    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favorite", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

}
