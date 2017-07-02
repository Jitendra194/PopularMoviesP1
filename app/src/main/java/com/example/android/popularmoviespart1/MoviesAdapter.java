package com.example.android.popularmoviespart1;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmoviespart1.utilities.ExtractImageColor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;

    private final ListItemClickListener clickListener;

    private static final int LOADING = 0;
    private static final int ITEM = 1;

    private ArrayList<Movie> mMovies;

    private static final String TAG = MoviesAdapter.class.getSimpleName();


    public MoviesAdapter(ListItemClickListener listener) {
        clickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(Movie movie);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == LOADING) {
            int itemLayoutResource = R.layout.progress_bar;
            View view = inflater.inflate(itemLayoutResource, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == ITEM) {
            int itemLayoutResource = R.layout.movie_list_item;
            View view = inflater.inflate(itemLayoutResource, parent, false);
            return new MoviesViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        if (holder.getItemViewType() == ITEM) {
            MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;
            moviesViewHolder.bind(movie);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (isLoadingAdded) ? LOADING : ITEM;
    }

    public void setMovies(ArrayList<Movie> movies) {
        mMovies = movies;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        private final ImageView mMovieImage;
        private final LinearLayout linearLayout;
        private final TextView mMovieTitle;
        private final ImageView mStarView;
        private final TextView mMovieRating;
        private ExtractImageColor extractImageColor;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mMovieImage = itemView.findViewById(R.id.movie_image);
            linearLayout = itemView.findViewById(R.id.movie_color_view);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mStarView = itemView.findViewById(R.id.star_color);
            mMovieRating = itemView.findViewById(R.id.movie_rating);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            mMovieTitle.setText(movie.getTitle());
            mMovieRating.setText(String.valueOf(movie.getMovieRating()));
            Picasso.with(context).load(movie.getPosterUrl()).into(getTarget());
        }

        @Override
        public void onClick(View view) {
            clickListener.onListItemClick(mMovies.get(getAdapterPosition()));
        }

        private ExtractImageColor getTarget() {
            extractImageColor = new ExtractImageColor(mMovieImage) {
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

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }
}
