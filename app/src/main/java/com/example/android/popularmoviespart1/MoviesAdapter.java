package com.example.android.popularmoviespart1;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmoviespart1.MoviesData.Movie;
import com.example.android.popularmoviespart1.data.MoviesContract;
import com.example.android.popularmoviespart1.utilities.ExtractImageColor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ListItemClickListener clickListener;

    private static final int ITEM = 1;

    private ArrayList<Movie> mMovies;
    private Cursor mCursor;

    private static final String TAG = MoviesAdapter.class.getSimpleName();


    public MoviesAdapter(ListItemClickListener listener) {
        clickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int movieId);

        void onListItemLongClick(int movieId);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == ITEM) {
            int itemLayoutResource = R.layout.movie_list_item;
            View view = inflater.inflate(itemLayoutResource, parent, false);
            return new MoviesViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;
        Movie movie = null;
        if (mCursor == null) {
            movie = mMovies.get(position);
        } else {
            mCursor.moveToPosition(position);
        }
        moviesViewHolder.bind(movie, mCursor);
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return mMovies == null ? 0 : mMovies.size();
        } else {
            return mCursor.getCount();
        }
    }

    public void setMovies(ArrayList<Movie> movies, Cursor cursor) {
        mMovies = movies;
        mCursor = cursor;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final Context context;
        public final ImageView mMovieImage;
        public final LinearLayout linearLayout;
        public final TextView mMovieTitle;
        public final ImageView mStarView;
        public final TextView mMovieRating;
        private ExtractImageColor extractImageColor;

        private int MOVIE_ID;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mMovieImage = itemView.findViewById(R.id.movie_image);
            linearLayout = itemView.findViewById(R.id.movie_color_view);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mStarView = itemView.findViewById(R.id.star_color);
            mMovieRating = itemView.findViewById(R.id.movie_rating);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Movie movie, Cursor mCursor) {
            if (mCursor == null) {
                mMovieTitle.setText(movie.getTitle());
                mMovieRating.setText(String.valueOf(movie.getMovieRating()));
                Picasso.with(context).load(movie.getPosterUrl()).into(getTarget(mMovieImage));
            } else {
                String name = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE));
                String id = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
                double rating = mCursor.getDouble(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING));
                String poster = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH));

                MOVIE_ID = Integer.parseInt(id);

                File file = new File(poster + "/" + id + "_1.jpg");
                Picasso.with(context).load(file).into(getTarget(mMovieImage));
                mMovieRating.setText(String.valueOf(rating));
                mMovieTitle.setText(name);
            }
        }

        @Override
        public void onClick(View view) {
            if (mCursor == null) {
                clickListener.onListItemClick(mMovies.get(getAdapterPosition()).getId());
            } else {
                clickListener.onListItemClick(MOVIE_ID);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mCursor != null) {
                clickListener.onListItemLongClick(MOVIE_ID);
            }
            return true;
        }

        public ExtractImageColor getTarget(ImageView imageView) {
            extractImageColor = new ExtractImageColor(imageView) {
                @Override
                public void setSwatchToViews(Palette.Swatch swatch) {
                    linearLayout.setBackgroundColor(swatch.getRgb());
                    mMovieTitle.setTextColor(swatch.getTitleTextColor());
                    mMovieRating.setTextColor(swatch.getTitleTextColor());
                    mStarView.setColorFilter(swatch.getBodyTextColor());
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Log.e(TAG, context.getString(R.string.color_error_in_adapter));
                }
            };
            return extractImageColor;
        }
    }

}
