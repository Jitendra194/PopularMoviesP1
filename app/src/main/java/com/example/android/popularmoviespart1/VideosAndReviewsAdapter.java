package com.example.android.popularmoviespart1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviespart1.MoviesData.MovieResponse;
import com.example.android.popularmoviespart1.utilities.NetworkUtils;

import java.util.ArrayList;


class VideosAndReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIDEOS = 1;
    public static final int REVIEWS = 2;

    @SuppressWarnings("CanBeFinal")
    private int mViewType;

    @SuppressWarnings("CanBeFinal")
    private ArrayList<MovieResponse.Results> mMovieData;

    public VideosAndReviewsAdapter(ArrayList<MovieResponse.Results> data, int viewType) {
        mMovieData = data;
        mViewType = viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == VIDEOS) {
            view = inflater.inflate(R.layout.trailer_card, parent, false);
            return new VideoViewHolder(view);
        } else if (viewType == REVIEWS) {
            view = inflater.inflate(R.layout.review_card, parent, false);
            return new ReviewViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIDEOS) {
            VideoViewHolder viewHolder = (VideoViewHolder) holder;
            viewHolder.bind(position);
        } else if (holder.getItemViewType() == REVIEWS) {
            ReviewViewHolder viewHolder = (ReviewViewHolder) holder;
            viewHolder.bind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieData.size() != 0) {
            return mMovieData.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressWarnings("CanBeFinal")
        private TextView mVideoName;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoName = itemView.findViewById(R.id.video_title);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            mVideoName.setText(mMovieData.get(position).name);
        }

        @Override
        public void onClick(View view) {
            Uri youtubeUri = Uri.parse(NetworkUtils.YOUTUBE_VIDEO_LINK +
                    mMovieData.get(getAdapterPosition()).key);
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressWarnings("CanBeFinal")
        private TextView mAuthorReview;
        @SuppressWarnings("CanBeFinal")
        private TextView mReviewAuthor;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorReview = itemView.findViewById(R.id.author_review);
            mReviewAuthor = itemView.findViewById(R.id.review_author);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse(mMovieData.get(getAdapterPosition()).url);
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }

        public void bind(int position) {
            mAuthorReview.setText(mMovieData.get(position).content);
            mReviewAuthor.setText(mMovieData.get(position).author);
        }
    }
}
